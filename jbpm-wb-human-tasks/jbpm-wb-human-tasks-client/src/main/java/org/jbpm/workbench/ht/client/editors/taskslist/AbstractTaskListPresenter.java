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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.dataset.AbstractDataSetReadyCallback;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.list.MultiGridView;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.TaskCompletedEvent;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.common.client.util.DataSetUtils.*;
import static org.jbpm.workbench.common.client.util.TaskUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

public abstract class AbstractTaskListPresenter<V extends AbstractTaskListPresenter.TaskListView> extends AbstractMultiGridPresenter<TaskSummary, V> {

    protected Constants constants = Constants.INSTANCE;

    private Caller<TaskService> taskService;

    private DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    @Inject
    private ErrorPopupPresenter errorPopup;

    @Inject
    private Event<TaskSelectionEvent> taskSelected;

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
        return new AbstractDataSetReadyCallback(errorPopup,
                                                view,
                                                tableSettings.getUUID()) {

            @Override
            public void callback(DataSet dataSet) {
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
                                                    filterValue,
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
            }

            @Override
            public boolean onError(final ClientRuntimeError error) {
                view.hideBusyIndicator();

                showErrorPopup(Constants.INSTANCE.TaskListCouldNotBeLoaded());

                return false;
            }
        };
    }

    void showErrorPopup(final String message) {
        ErrorPopup.showMessage(message);
    }

    protected String isFilteredByTaskName(List<DataSetOp> ops) {
        for (DataSetOp dataSetOp : ops) {
            if (dataSetOp.getType().equals(DataSetOpType.FILTER)) {
                List<ColumnFilter> filters = ((DataSetFilter) dataSetOp).getColumnFilterList();

                for (ColumnFilter filter : filters) {

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
                }
            }
        }

        return null;
    }

    public void getDomainSpecifDataForTasks(final Integer startRange,
                                            final String filterValue,
                                            final List<TaskSummary> myTasksFromDataSet,
                                            final Boolean lastPageExactCount) {

        FilterSettings variablesTableSettings = filterSettingsManager.getVariablesFilterSettings(filterValue);
        variablesTableSettings.setTablePageSize(-1);
        variablesTableSettings.setServerTemplateId(getSelectedServerTemplate());

        dataSetQueryHelperDomainSpecific.setDataSetHandler(variablesTableSettings);
        dataSetQueryHelperDomainSpecific.setCurrentTableSettings(variablesTableSettings);
        dataSetQueryHelperDomainSpecific.setLastOrderedColumn(COLUMN_TASK_ID);
        dataSetQueryHelperDomainSpecific.setLastSortOrder(SortOrder.ASCENDING);

        List<Comparable> tasksIds = new ArrayList<Comparable>();
        for (TaskSummary task : myTasksFromDataSet) {
            tasksIds.add(task.getId());
        }
        DataSetFilter filter = new DataSetFilter();
        ColumnFilter filter1 = equalsTo(COLUMN_TASK_VARIABLE_TASK_ID,
                                        tasksIds);
        filter.addFilterColumn(filter1);
        variablesTableSettings.getDataSetLookup().addOperation(filter);

        dataSetQueryHelperDomainSpecific.lookupDataSet(0,
                                                       createDataSetDomainSpecificCallback(startRange,
                                                                                           myTasksFromDataSet,
                                                                                           variablesTableSettings,
                                                                                           lastPageExactCount));
    }

    protected DataSetReadyCallback createDataSetDomainSpecificCallback(final int startRange,
                                                                       final List<TaskSummary> instances,
                                                                       final FilterSettings tableSettings,
                                                                       boolean lastPageExactCount) {
        return new AbstractDataSetReadyCallback(errorPopup,
                                                view,
                                                tableSettings.getUUID()) {
            @Override
            public void callback(DataSet dataSet) {
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
                    view.addDomainSpecifColumns((ListTable) view.getListGrid(),
                                                columns);
                }
                updateDataOnCallback(instances,
                                     startRange,
                                     startRange + instances.size(),
                                     lastPageExactCount);
            }
        };
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

    public void selectTask(final TaskSummary summary) {
        boolean logOnly = false;
        if (TASK_STATUS_COMPLETED.equals(summary.getStatus()) ||
                TASK_STATUS_EXITED.equals(summary.getStatus())) {
            logOnly = true;
        }
        setupDetailBreadcrumb(constants.TaskBreadcrumb(summary.getId()));
        placeManager.goTo(PerspectiveIds.TASK_DETAILS_SCREEN);
        fireTaskSelectionEvent(summary,
                               logOnly);
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

    public void onTaskCompletedEvent(@Observes TaskCompletedEvent event) {
        refreshGrid();
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
                             processInstId),
                    constants.Process_Instance_Id(),
                    processInstId,
                    processInstId,
                    v -> removeActiveFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                                     v))
            );
        } else {
            final Optional<String> taskIdSearch = getSearchParameter(PerspectiveIds.SEARCH_PARAMETER_TASK_ID);
            if (taskIdSearch.isPresent()) {
                final String taskId = taskIdSearch.get();
                addActiveFilter(
                        equalsTo(COLUMN_TASK_ID,
                                 taskId),
                        constants.Task(),
                        taskId,
                        taskId,
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
        addActiveFilter(equalsTo(COLUMN_STATUS,
                                 TASK_STATUS_READY),
                        constants.Status(),
                        TASK_STATUS_READY,
                        TASK_STATUS_READY,
                        v -> removeActiveFilter(equalsTo(COLUMN_STATUS,
                                                         v))
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
        return task -> task.getActualOwner() != null && task.getStatus().equals(TASK_STATUS_IN_PROGRESS);
    }

    protected Predicate<TaskSummary> getClaimActionCondition() {
        return task -> task.getStatus().equals(TASK_STATUS_READY);
    }

    protected Predicate<TaskSummary> getReleaseActionCondition() {
        return task -> task.getStatus().equals(TASK_STATUS_RESERVED) || task.getStatus().equals(TASK_STATUS_IN_PROGRESS);
    }

    protected Predicate<TaskSummary> getProcessInstanceCondition() {
        return task -> task.getProcessInstanceId() != null;
    }

    public interface TaskListView<T extends AbstractTaskListPresenter> extends MultiGridView<TaskSummary, T> {

        void addDomainSpecifColumns(ListTable<TaskSummary> extendedPagedTable,
                                    Set<String> columns);

    }
}
