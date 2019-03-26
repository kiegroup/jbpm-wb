/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.ht.client.editors.taskslist;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.*;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.MultiGridView;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.*;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.ht.util.TaskStatus;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.common.client.util.DataSetUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

public abstract class AbstractTaskListPresenter<V extends AbstractTaskListPresenter.TaskListView> extends AbstractMultiGridPresenter<TaskSummary, V> {

    protected Constants constants = Constants.INSTANCE;

    private Caller<TaskService> taskService;

    private DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    protected TranslationService translationService;

    @Inject
    private Event<TaskSelectionEvent> taskSelected;

    private TaskSummary selectedTask = null;

    public abstract void setupDetailBreadcrumb(String detailLabel);

    @Override
    public void openErrorView(final String tId) {
        final PlaceRequest request = new DefaultPlaceRequest(PerspectiveIds.EXECUTION_ERRORS);
        request.addParameter(PerspectiveIds.SEARCH_PARAMETER_IS_ERROR_ACK,
                             Boolean.toString(false));
        request.addParameter(PerspectiveIds.SEARCH_PARAMETER_TASK_ID,
                             tId);
        request.addParameter(PerspectiveIds.SEARCH_PARAMETER_ERROR_TYPE,
                             constants.Task());
        placeManager.goTo(request);
    }

    @Override
    public Predicate<TaskSummary> getViewErrorsActionCondition() {
        return tId -> isUserAuthorizedForPerspective(PerspectiveIds.EXECUTION_ERRORS) && tId.getErrorCount() != null && tId.getErrorCount() > 0;
    }

    @Override
    protected DataSetReadyCallback getDataSetReadyCallback(final Integer startRange,
                                                           final FilterSettings tableSettings) {
        return errorHandlerBuilder.get().withUUID(tableSettings.getUUID()).withDataSetCallback(
                dataSet -> {
                    if (dataSet != null && dataSetQueryHelper.getCurrentTableSettings().getKey().equals(tableSettings.getKey())) {
                        final List<TaskSummary> myTasksFromDataSet = new ArrayList<TaskSummary>();

                        for (int i = 0; i < dataSet.getRowCount(); i++) {
                            myTasksFromDataSet.add(new TaskSummaryDataSetMapper().apply(dataSet,
                                                                                        i));
                        }

                        boolean lastPageExactCount = false;
                        if (dataSet.getRowCount() < view.getListGrid().getPageSize()) {
                            lastPageExactCount = true;
                        }

                        List<DataSetOp> ops = tableSettings.getDataSetLookup().getOperationList();
                        String filterValue = isFilteredByTaskName(ops); //Add here the check to add the domain data columns taskName?
                        if (filterValue != null) {
                            getDomainSpecifDataForTasks(startRange,
                                                        myTasksFromDataSet,
                                                        lastPageExactCount);
                        } else {
                            updateDataOnCallback(myTasksFromDataSet,
                                                 startRange,
                                                 startRange + myTasksFromDataSet.size(),
                                                 lastPageExactCount);
                        }
                    }
                    view.hideBusyIndicator();
                })
                .withEmptyResultsCallback(() -> setEmptyResults());
    }

    @Override
    protected void removeActiveFilter(final ColumnFilter columnFilter) {
        super.removeActiveFilter(columnFilter);
        if (isFilteredByTaskName(columnFilter) != null) {
            view.removeDomainSpecifColumns();
        }
    }

    protected String isFilteredByTaskName(List<DataSetOp> ops) {
        for (DataSetOp dataSetOp : ops) {
            if (dataSetOp.getType().equals(DataSetOpType.FILTER)) {
                List<ColumnFilter> filters = ((DataSetFilter) dataSetOp).getColumnFilterList();
                for (ColumnFilter filter : filters) {
                    final String taskName = isFilteredByTaskName(filter);
                    if (taskName != null) {
                        return taskName;
                    }
                }
            }
        }
        return null;
    }

    protected String isFilteredByTaskName(ColumnFilter filter) {
        if (filter instanceof CoreFunctionFilter) {
            CoreFunctionFilter coreFilter = ((CoreFunctionFilter) filter);
            if (filter.getColumnId().toUpperCase().equals(COLUMN_NAME.toUpperCase()) &&
                    ((CoreFunctionFilter) filter).getType() == CoreFunctionType.EQUALS_TO) {

                List parameters = coreFilter.getParameters();
                if (parameters.size() > 0) {
                    return parameters.get(0).toString();
                }
            }
        }
        return null;
    }

    public void getDomainSpecifDataForTasks(final Integer startRange,
                                            final List<TaskSummary> tasksFromDataSet,
                                            final Boolean lastPageExactCount) {

        List<Long> taskIds = tasksFromDataSet.stream().map(t -> t.getId()).collect(Collectors.toList());
        FilterSettings variablesTableSettings = filterSettingsManager.getVariablesFilterSettings(taskIds);
        variablesTableSettings.setTablePageSize(-1);
        variablesTableSettings.setServerTemplateId(getSelectedServerTemplate());

        dataSetQueryHelperDomainSpecific.setDataSetHandler(variablesTableSettings);
        dataSetQueryHelperDomainSpecific.setCurrentTableSettings(variablesTableSettings);
        dataSetQueryHelperDomainSpecific.setLastOrderedColumn(COLUMN_TASK_ID);
        dataSetQueryHelperDomainSpecific.setLastSortOrder(SortOrder.ASCENDING);
        dataSetQueryHelperDomainSpecific.lookupDataSet(0,
                                                       createDataSetDomainSpecificCallback(startRange,
                                                                                           tasksFromDataSet,
                                                                                           variablesTableSettings,
                                                                                           lastPageExactCount));
    }

    protected DataSetReadyCallback createDataSetDomainSpecificCallback(final int startRange,
                                                                       final List<TaskSummary> instances,
                                                                       final FilterSettings tableSettings,
                                                                       boolean lastPageExactCount) {
        return errorHandlerBuilder.get().withUUID(tableSettings.getUUID()).withDataSetCallback(
                dataSet -> {
                    if (dataSet.getRowCount() > 0) {
                        Set<String> columns = new HashSet<String>();
                        for (int i = 0; i < dataSet.getRowCount(); i++) {
                            Long taskId = getColumnLongValue(dataSet,
                                                             COLUMN_TASK_ID,
                                                             i);
                            String variableName = getColumnStringValue(dataSet,
                                                                       COLUMN_TASK_VARIABLE_NAME,
                                                                       i);
                            String variableValue = getColumnStringValue(dataSet,
                                                                        COLUMN_TASK_VARIABLE_VALUE,
                                                                        i);
                            for (TaskSummary task : instances) {
                                if (task.getId().equals(taskId)) {
                                    task.addDomainData(variableName,
                                                       variableValue);
                                    columns.add(variableName);
                                }
                            }
                        }
                        view.addDomainSpecifColumns(columns);
                    }
                    updateDataOnCallback(instances,
                                         startRange,
                                         startRange + instances.size(),
                                         lastPageExactCount);
                })
                .withEmptyResultsCallback(() -> setEmptyResults());
    }

    public void releaseTask(final TaskSummary task) {
        taskService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void nothing) {
                        view.displayNotification(constants.TaskReleased(String.valueOf(task.getId())));
                        refreshGrid();
                    }
                }).releaseTask(getSelectedServerTemplate(),
                               task.getDeploymentId(),
                               task.getId());
    }

    public void claimTask(final TaskSummary task) {
        taskService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void nothing) {
                        view.displayNotification(constants.TaskClaimed(String.valueOf(task.getId())));
                        refreshGrid();
                    }
                }
        ).claimTask(getSelectedServerTemplate(),
                    task.getDeploymentId(),
                    task.getId());
    }

    public void claimAndWorkTask(final TaskSummary task) {
        taskService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void nothing) {
                        view.displayNotification(constants.TaskClaimed(String.valueOf(task.getId())));
                        selectSummaryItem(task);
                        refreshGrid();
                    }
                }
        ).claimTask(getSelectedServerTemplate(),
                    task.getDeploymentId(),
                    task.getId());
    }

    public void resumeTask(final TaskSummary task) {
        taskService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void nothing) {
                        view.displayNotification(constants.TaskResumed(String.valueOf(task.getId())));
                        refreshGrid();
                    }
                }
        ).resumeTask(getSelectedServerTemplate(),
                     task.getDeploymentId(),
                     task.getId());
    }

    public void suspendTask(final TaskSummary task) {
        taskService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void nothing) {
                        view.displayNotification(constants.TaskSuspended(String.valueOf(task.getId())));
                        refreshGrid();
                    }
                }
        ).suspendTask(getSelectedServerTemplate(),
                      task.getDeploymentId(),
                      task.getId());
    }

    public Menus getMenus() { //To be used by subclass methods annotated with @WorkbenchMenu
        return MenuFactory
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .build();
    }

    @Override
    public void selectSummaryItem(final TaskSummary summary) {
        boolean logOnly = false;
        if (TaskStatus.TASK_STATUS_COMPLETED.equals(summary.getTaskStatus()) ||
                TaskStatus.TASK_STATUS_EXITED.equals(summary.getTaskStatus())) {
            logOnly = true;
        }
        setupDetailBreadcrumb(constants.TaskBreadcrumb(summary.getId()));
        placeManager.goTo(PerspectiveIds.TASK_DETAILS_SCREEN);
        fireTaskSelectionEvent(summary,
                               logOnly);
        selectedTask = summary;
    }

    private void fireTaskSelectionEvent(TaskSummary summary,
                                        boolean logOnly) {
        taskSelected.fire(new TaskSelectionEvent(getSelectedServerTemplate(),
                                                 summary.getDeploymentId(),
                                                 summary.getId(),
                                                 summary.getName(),
                                                 summary.isForAdmin(),
                                                 logOnly,
                                                 summary.getDescription(),
                                                 summary.getExpirationTime(),
                                                 summary.getStatus(),
                                                 summary.getActualOwner(),
                                                 summary.getPriority(),
                                                 summary.getProcessInstanceId(),
                                                 summary.getProcessId()));
    }

    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event) {
        refreshGrid();
    }

    public void onTaskDetailsClosed(@Observes BeforeClosePlaceEvent closed) {
        if (PerspectiveIds.TASK_DETAILS_SCREEN.equals(closed.getPlace().getIdentifier())) {
            selectedTask = null;
        }
    }

    public void onTaskCompletedEvent(@Observes TaskCompletedEvent event) {
        //Need to filter events only for a related task that was selected.
        if(isSameTaskFromEvent().test(event)){
            refreshGrid();
        }
    }

    protected Predicate<AbstractTaskEvent> isSameTaskFromEvent() {
        return e -> selectedTask != null && e.getServerTemplateId().equals(getSelectedServerTemplate()) && e.getContainerId().equals(selectedTask.getDeploymentId()) && e.getTaskId().equals(selectedTask.getId());
    }

    @Inject
    public void setDataSetQueryHelperDomainSpecific(final DataSetQueryHelper dataSetQueryHelperDomainSpecific) {
        this.dataSetQueryHelperDomainSpecific = dataSetQueryHelperDomainSpecific;
    }

    @Inject
    public void setTaskService(final Caller<TaskService> taskService) {
        this.taskService = taskService;
    }

    @Override
    public void setupActiveSearchFilters() {
        final Optional<String> processInstIdSearch = getSearchParameter(PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID);
        if (processInstIdSearch.isPresent()) {
            final String processInstId = processInstIdSearch.get();
            addActiveFilter(
                    equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                             Integer.valueOf(processInstId)),
                    constants.Process_Instance_Id(),
                    processInstId,
                    Integer.valueOf(processInstId),
                    v -> removeActiveFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                                     v))
            );
        } else {
            final Optional<String> taskIdSearch = getSearchParameter(PerspectiveIds.SEARCH_PARAMETER_TASK_ID);
            if (taskIdSearch.isPresent()) {
                final String taskId = taskIdSearch.get();
                addActiveFilter(
                        equalsTo(COLUMN_TASK_ID,
                                 Integer.valueOf(taskId)),
                        constants.Task(),
                        taskId,
                        Integer.valueOf(taskId),
                        v -> removeActiveFilter(equalsTo(COLUMN_TASK_ID,
                                                         v))
                );
            } else {
                super.setupActiveSearchFilters();
            }
        }
    }

    @Override
    public void setupDefaultActiveSearchFilters() {
        final List<String> status = Arrays.asList(TaskStatus.TASK_STATUS_READY.getIdentifier(),
                                                  TaskStatus.TASK_STATUS_IN_PROGRESS.getIdentifier(),
                                                  TaskStatus.TASK_STATUS_RESERVED.getIdentifier());
        final List<String> statusLabels = Arrays.asList(translationService.format(TaskStatus.TASK_STATUS_READY.getIdentifier()),
                                                        translationService.format(TaskStatus.TASK_STATUS_IN_PROGRESS.getIdentifier()),
                                                        translationService.format(TaskStatus.TASK_STATUS_RESERVED.getIdentifier()));
        addActiveFilter(in(COLUMN_STATUS,
                           status),
                        constants.Status(),
                        String.join(", ",
                                    statusLabels),
                        status,
                        v -> removeActiveFilter(in(COLUMN_STATUS,
                                                   status))
        );
    }

    public void openProcessInstanceView(final String processInstanceId) {
        navigateToPerspective(PerspectiveIds.PROCESS_INSTANCES,
                              PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                              processInstanceId);
    }

    protected abstract Predicate<TaskSummary> getSuspendActionCondition();

    protected abstract Predicate<TaskSummary> getResumeActionCondition();

    protected Predicate<TaskSummary> getCompleteActionCondition() {
        return task -> task.getActualOwner() != null && TaskStatus.TASK_STATUS_IN_PROGRESS.equals(task.getTaskStatus());
    }

    protected Predicate<TaskSummary> getClaimActionCondition() {
        return task -> TaskStatus.TASK_STATUS_READY.equals(task.getTaskStatus());
    }

    protected Predicate<TaskSummary> getReleaseActionCondition() {
        return task -> TaskStatus.TASK_STATUS_RESERVED.equals(task.getTaskStatus()) || TaskStatus.TASK_STATUS_IN_PROGRESS.equals(task.getTaskStatus());
    }

    protected Predicate<TaskSummary> getProcessInstanceCondition() {
        return task -> task.getProcessInstanceId() != null;
    }

    public interface TaskListView<T extends AbstractTaskListPresenter> extends MultiGridView<TaskSummary, T> {

        void addDomainSpecifColumns(Set<String> columns);

        void removeDomainSpecifColumns();
    }

    @Inject
    public void setTranslationService(TranslationService translationService) {
        this.translationService = translationService;
    }

    protected TaskSummary getSelectedTask() {
        return selectedTask;
    }
}
