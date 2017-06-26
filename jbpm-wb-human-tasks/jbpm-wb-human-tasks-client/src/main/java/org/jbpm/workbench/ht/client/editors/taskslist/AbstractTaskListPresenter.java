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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.Range;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
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
import org.jboss.errai.security.shared.api.Group;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.dataset.AbstractDataSetReadyCallback;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.MultiGridView;
import org.jbpm.workbench.common.client.menu.RestoreDefaultFiltersMenuBuilder;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.df.client.list.base.DataSetQueryHelper;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.NewTaskEvent;
import org.jbpm.workbench.ht.model.events.TaskCompletedEvent;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.common.client.util.DataSetUtils.*;
import static org.jbpm.workbench.common.client.util.TaskUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

public abstract class AbstractTaskListPresenter<V extends AbstractTaskListPresenter.TaskListView> extends AbstractMultiGridPresenter<TaskSummary, V> {

    public interface TaskListView<T extends AbstractTaskListPresenter> extends MultiGridView<TaskSummary, T> {

        void addDomainSpecifColumns(ExtendedPagedTable<TaskSummary> extendedPagedTable, Set<String> columns);

        void setSelectedTask(TaskSummary selectedTask);

    }

    protected Constants constants = Constants.INSTANCE;

    private Caller<TaskService> taskService;

    private DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    @Inject
    private ErrorPopupPresenter errorPopup;

    @Inject
    private Event<TaskSelectionEvent> taskSelected;

    @Override
    public void getData(final Range visibleRange) {
        try {
            if (!isAddingDefaultFilters()) {
                FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
                currentTableSettings.setServerTemplateId(getSelectedServerTemplate());
                currentTableSettings.setTablePageSize(view.getListGrid().getPageSize());
                ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                if (columnSortList != null && columnSortList.size() > 0) {
                    dataSetQueryHelper.setLastOrderedColumn(columnSortList.size() > 0 ? columnSortList.get(0).getColumn().getDataStoreName() : "");
                    dataSetQueryHelper.setLastSortOrder((columnSortList.size() > 0) && columnSortList.get(0).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING);
                } else {
                    dataSetQueryHelper.setLastOrderedColumn(COLUMN_CREATED_ON);
                    dataSetQueryHelper.setLastSortOrder(SortOrder.ASCENDING);
                }

                final List<ColumnFilter> filters = getColumnFilters(textSearchStr);
                if (filters.isEmpty() == false) {
                    if (currentTableSettings.getDataSetLookup().getFirstFilterOp() != null) {
                        currentTableSettings.getDataSetLookup().getFirstFilterOp().addFilterColumn(OR(filters));
                    } else {
                        final DataSetFilter filter = new DataSetFilter();
                        filter.addFilterColumn(OR(filters));
                        currentTableSettings.getDataSetLookup().addOperation(filter);
                    }
                }
                dataSetQueryHelper.setDataSetHandler(currentTableSettings);
                dataSetQueryHelper.lookupDataSet(visibleRange.getStart(), createDataSetTaskCallback(visibleRange.getStart(), currentTableSettings));
            }
        } catch (Exception e) {
            errorPopup.showMessage(constants.UnexpectedError(e.getMessage()));
        }

    }

    protected List<ColumnFilter> getColumnFilters(final String searchString) {
        final List<ColumnFilter> filters = new ArrayList<ColumnFilter>();
        if (searchString != null && searchString.trim().length() > 0) {
            try {
                final Long taskId = Long.valueOf(searchString.trim());
                filters.add(equalsTo(COLUMN_TASK_ID, taskId));
            } catch (NumberFormatException ex) {
                filters.add(likeTo(COLUMN_NAME, "%" + searchString.toLowerCase() + "%", false));
                filters.add(likeTo(COLUMN_DESCRIPTION, "%" + searchString.toLowerCase() + "%", false));
                filters.add(likeTo(COLUMN_PROCESS_ID, "%" + searchString.toLowerCase() + "%", false));
            }
        }
        return filters;
    }

    /**
     * Generates a dataset filter depending of the user roles and the kind of dataset.
     * <br>In case of the adminDataset (isAdminDateset=true), retrieve the tasks that are accessible for the user logged
     * roles, without restriction over the task owner.
     * <br>In other cases, retrieve the tasks available for the user logged roles AND without owner(claimed by the groups
     * members) OR the user logged owned tasks
     * @param isAdminDataset true if the filter to create is an adminDataSet
     * @return the dynamic filter to add, depeding on the user logged roles and the kind of dataset
     */
    protected ColumnFilter getUserGroupFilters(boolean isAdminDataset) {
        Set<Group> groups = identity.getGroups();
        List<ColumnFilter> condList = new ArrayList<ColumnFilter>();
        for (Group g : groups) {
            condList.add(equalsTo(COLUMN_ORGANIZATIONAL_ENTITY, g.getName()));

        }
        condList.add(equalsTo(COLUMN_ORGANIZATIONAL_ENTITY, identity.getIdentifier()));
        ColumnFilter myGroupFilter;
        if (isAdminDataset) {
            return OR(COLUMN_ORGANIZATIONAL_ENTITY, condList);
        } else {
            myGroupFilter = AND(OR(condList),
                    OR(equalsTo(COLUMN_ACTUAL_OWNER, ""), isNull(COLUMN_ACTUAL_OWNER)));
            return OR(myGroupFilter, equalsTo(COLUMN_ACTUAL_OWNER, identity.getIdentifier()));
        }
    }

    protected DataSetReadyCallback createDataSetTaskCallback(final int startRange, final FilterSettings tableSettings) {
        return new AbstractDataSetReadyCallback(errorPopup, view, tableSettings.getUUID()) {

            @Override
            public void callback(DataSet dataSet) {
                if (dataSet != null && dataSetQueryHelper.getCurrentTableSettings().getKey().equals(tableSettings.getKey())) {
                    final List<TaskSummary> myTasksFromDataSet = new ArrayList<TaskSummary>();

                    for (int i = 0; i < dataSet.getRowCount(); i++) {
                        myTasksFromDataSet.add(new TaskSummaryDataSetMapper().apply(dataSet,
                                                                                    i));
                    }

                    boolean lastPageExactCount = false;
                    if( dataSet.getRowCount() < view.getListGrid().getPageSize()) {
                        lastPageExactCount = true;
                    }

                    List<DataSetOp> ops = tableSettings.getDataSetLookup().getOperationList();
                    String filterValue = isFilteredByTaskName(ops); //Add here the check to add the domain data columns taskName?
                    if (AbstractMultiGridView.TAB_SEARCH.equals(tableSettings.getKey()) == false && filterValue != null) {
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

        };
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

    public void getDomainSpecifDataForTasks(final int startRange, String filterValue, final List<TaskSummary> myTasksFromDataSet, boolean lastPageExactCount) {

        FilterSettings variablesTableSettings = getVariablesTableSettings(filterValue);
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
        ColumnFilter filter1 = equalsTo(COLUMN_TASK_VARIABLE_TASK_ID, tasksIds);
        filter.addFilterColumn(filter1);
        variablesTableSettings.getDataSetLookup().addOperation(filter);

        dataSetQueryHelperDomainSpecific.lookupDataSet(0, createDataSetDomainSpecificCallback(startRange, myTasksFromDataSet, variablesTableSettings, lastPageExactCount));

    }

    protected DataSetReadyCallback createDataSetDomainSpecificCallback(final int startRange, final List<TaskSummary> instances, final FilterSettings tableSettings, boolean lastPageExactCount) {
        return new AbstractDataSetReadyCallback(errorPopup, view, tableSettings.getUUID()) {
            @Override
            public void callback(DataSet dataSet) {
                if (dataSet.getRowCount() > 0) {
                    Set<String> columns = new HashSet<String>();
                    for (int i = 0; i < dataSet.getRowCount(); i++) {
                        Long taskId = getColumnLongValue(dataSet, COLUMN_TASK_ID, i);
                        String variableName = getColumnStringValue(dataSet, COLUMN_TASK_VARIABLE_NAME, i);
                        String variableValue = getColumnStringValue(dataSet, COLUMN_TASK_VARIABLE_VALUE, i);

                        for (TaskSummary task : instances) {
                            if (task.getId().equals(taskId)) {
                                task.addDomainData(variableName, variableValue);
                                columns.add(variableName);
                            }
                        }
                    }
                    view.addDomainSpecifColumns(view.getListGrid(), columns);
                }
                updateDataOnCallback(instances, startRange, startRange + instances.size(), lastPageExactCount);
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
                }).releaseTask(getSelectedServerTemplate(), task.getDeploymentId(), task.getId());
        taskSelected.fire( new TaskSelectionEvent( getSelectedServerTemplate(), task.getDeploymentId(), task.getId(), task.getName() ) );
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
        ).claimTask(getSelectedServerTemplate(), task.getDeploymentId(), task.getId());
        taskSelected.fire( new TaskSelectionEvent( getSelectedServerTemplate(), task.getDeploymentId(), task.getId(), task.getName() ) );
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
        ).resumeTask(getSelectedServerTemplate(), task.getDeploymentId(), task.getId());
        taskSelected.fire( new TaskSelectionEvent( getSelectedServerTemplate(), task.getDeploymentId(), task.getId(), task.getName() ) );
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
        ).suspendTask(getSelectedServerTemplate(), task.getDeploymentId(), task.getId());
        taskSelected.fire( new TaskSelectionEvent( getSelectedServerTemplate(), task.getDeploymentId(), task.getId(), task.getName() ) );
    }
    
    public Menus getMenus(){ //To be used by subclass methods annotated with @WorkbenchMenu
        return MenuFactory
            .newTopLevelCustomMenu(serverTemplateSelectorMenuBuilder).endMenu()
            .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
            .newTopLevelCustomMenu(refreshSelectorMenuBuilder).endMenu()
            .newTopLevelCustomMenu(new RestoreDefaultFiltersMenuBuilder(this)).endMenu()
            .build();
    }

    public void selectTask(final TaskSummary summary, final Boolean close) {
        final DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest( "Task Details Multi" );
        final PlaceStatus status = placeManager.getStatus( defaultPlaceRequest );
        boolean logOnly = false;
        if ( summary.getStatus().equals( TASK_STATUS_COMPLETED ) && summary.isLogOnly() ) {
            logOnly = true;
        }
        if ( status == PlaceStatus.CLOSE ) {
            placeManager.goTo( defaultPlaceRequest );
            taskSelected.fire( new TaskSelectionEvent( getSelectedServerTemplate(), summary.getDeploymentId(), summary.getId(), summary.getName(), summary.isForAdmin(), logOnly ) );
        } else if ( status == PlaceStatus.OPEN && !close ) {
            taskSelected.fire( new TaskSelectionEvent( getSelectedServerTemplate(), summary.getDeploymentId(),summary.getId(), summary.getName(), summary.isForAdmin(), logOnly ) );
        } else if ( status == PlaceStatus.OPEN && close ) {
            placeManager.closePlace( "Task Details Multi" );
        }
    }

    public void refreshNewTask( @Observes NewTaskEvent newTask ) {
        refreshGrid();
        PlaceStatus status = placeManager.getStatus( new DefaultPlaceRequest( "Task Details Multi" ) );
        if ( status == PlaceStatus.OPEN ) {
            taskSelected.fire( new TaskSelectionEvent( getSelectedServerTemplate(), null, newTask.getNewTaskId(), newTask.getNewTaskName() ) );
        } else {
            placeManager.goTo( "Task Details Multi" );
            taskSelected.fire( new TaskSelectionEvent( getSelectedServerTemplate(), null, newTask.getNewTaskId(), newTask.getNewTaskName() ) );
        }

        view.setSelectedTask(TaskSummary.builder().id(newTask.getNewTaskId()).name(newTask.getNewTaskName()).build());
    }

    public void onTaskRefreshedEvent( @Observes TaskRefreshedEvent event ) {
        refreshGrid();
    }

    public void onTaskCompletedEvent( @Observes TaskCompletedEvent event ) {
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
    public void setupAdvancedSearchView() {
        view.addNumericFilter(constants.Id(),
                              constants.FilterByTaskId(),
                              v -> addAdvancedSearchFilter(equalsTo(COLUMN_TASK_ID,
                                                                    v)),
                              v -> removeAdvancedSearchFilter(equalsTo(COLUMN_TASK_ID,
                                                                       v))
        );

        view.addTextFilter(constants.Task(),
                           constants.FilterByTaskName(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_NAME,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_NAME,
                                                                  v,
                                                                  false))
        );

        final Map<String, String> status = getStatusByType(TaskType.ALL).stream().sorted().collect(Collectors.toMap(Function.identity(),
                                                                                                                    Function.identity()));
        view.addSelectFilter(constants.Status(),
                             status,
                             false,
                             v -> addAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                                                   v)),
                             v -> removeAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                                                      v))
        );

        view.addTextFilter(constants.Process_Instance_Correlation_Key(),
                           constants.FilterByCorrelationKey(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_PROCESS_INSTANCE_CORRELATION_KEY,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_PROCESS_INSTANCE_CORRELATION_KEY,
                                                                  v,
                                                                  false))
        );

        view.addTextFilter(constants.Actual_Owner(),
                           constants.FilterByActualOwner(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_ACTUAL_OWNER,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_ACTUAL_OWNER,
                                                                  v,
                                                                  false))
        );

        view.addTextFilter(constants.Process_Instance_Description(),
                           constants.FilterByProcessInstanceDescription(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                                  v,
                                                                  false))
        );

        view.addDateRangeFilter(constants.Created_On(),
                                v -> addAdvancedSearchFilter(between(COLUMN_CREATED_ON,
                                                                     v.getStartDate(),
                                                                     v.getEndDate())),
                                v -> removeAdvancedSearchFilter(between(COLUMN_CREATED_ON,
                                                                        v.getStartDate(),
                                                                        v.getEndDate()))
        );

    }
    
    @Override
    public void setupActiveSearchFilters() {
        final Optional<String> processInstIdSearch = getSearchParameter(PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID);
        if (processInstIdSearch.isPresent()) {
            final String processInstId = processInstIdSearch.get();
            view.addActiveFilter(
                constants.Process_Instance_Id(),
                processInstId,
                processInstId,
                v -> removeAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID, v))
            );
            addAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID, processInstId));
        } else {
            super.setupActiveSearchFilters();
        }
    }

    @Override
    public void setupDefaultActiveSearchFilters() {
        view.addActiveFilter(constants.Status(),
                             TASK_STATUS_READY,
                             TASK_STATUS_READY,
                             v -> removeAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                                                      v))
        );
        addAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                         TASK_STATUS_READY));
    }
    
    public void openProcessInstanceView(final String processInstanceId) {
        navigateToPerspective(PerspectiveIds.PROCESS_INSTANCES,
                              PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                              processInstanceId);
    }

    protected void addProcessNameFilter(final String dataSetId){
        final DataSetLookup dataSetLookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(dataSetId)
                .group(COLUMN_PROCESS_ID)
                .column(COLUMN_PROCESS_ID)
                .sort(COLUMN_PROCESS_ID,
                      SortOrder.ASCENDING)
                .buildLookup();
        view.addDataSetSelectFilter(constants.Process_Name(),
                                    AbstractMultiGridView.TAB_SEARCH,
                                    dataSetLookup,
                                    COLUMN_PROCESS_ID,
                                    COLUMN_PROCESS_ID,
                                    v -> addAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_ID,
                                                                          v)),
                                    v -> removeAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_ID,
                                                                             v)));
    }

    public FilterSettings createStatusSettings(final String dataSetId, final List<Comparable> status){
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(dataSetId);

        if (status != null){
            builder.filter(COLUMN_STATUS,
                           equalsTo(COLUMN_STATUS,
                                    status));
        }
        builder.group(COLUMN_TASK_ID);

        addCommonColumnSettings(builder);
        return builder.buildSettings();
    }

    protected void addCommonColumnSettings(FilterSettingsBuilderHelper builder) {
        builder.setColumn(COLUMN_ACTIVATION_TIME, constants.ActivationTime(), DateUtils.getDateTimeFormatMask());
        builder.setColumn(COLUMN_ACTUAL_OWNER, constants.Actual_Owner());
        builder.setColumn(COLUMN_CREATED_BY, constants.CreatedBy());
        builder.setColumn(COLUMN_CREATED_ON, constants.Created_On(), DateUtils.getDateTimeFormatMask());
        builder.setColumn(COLUMN_DEPLOYMENT_ID, constants.DeploymentId());
        builder.setColumn(COLUMN_DESCRIPTION, constants.Description());
        builder.setColumn(COLUMN_DUE_DATE, constants.DueDate(), DateUtils.getDateTimeFormatMask());
        builder.setColumn(COLUMN_NAME, constants.Task());
        builder.setColumn(COLUMN_PARENT_ID, constants.ParentId());
        builder.setColumn(COLUMN_PRIORITY, constants.Priority());
        builder.setColumn(COLUMN_PROCESS_ID, constants.Process_Id());
        builder.setColumn(COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_Id());
        builder.setColumn(COLUMN_STATUS, constants.Status());
        builder.setColumn(COLUMN_TASK_ID, constants.Id());
        builder.setColumn(COLUMN_WORK_ITEM_ID, constants.WorkItemId());
        builder.setColumn(COLUMN_LAST_MODIFICATION_DATE, constants.Last_Modification_Date());
        builder.setColumn(COLUMN_PROCESS_INSTANCE_CORRELATION_KEY, constants.Process_Instance_Correlation_Key());
        builder.setColumn(COLUMN_PROCESS_INSTANCE_DESCRIPTION, constants.Process_Instance_Description());

        builder.filterOn(true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(COLUMN_CREATED_ON, SortOrder.DESCENDING);
    }

    public FilterSettings getVariablesTableSettings( String taskName ) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_VARIABLES_DATASET);
        builder.filter(equalsTo(COLUMN_TASK_VARIABLE_TASK_NAME, taskName));

        builder.filterOn(true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(COLUMN_TASK_ID, SortOrder.ASCENDING);

        FilterSettings varTableSettings =builder.buildSettings();
        varTableSettings.setTablePageSize(-1);

        return varTableSettings;
    }

    @Override
    public FilterSettings createSearchTabSettings() {
        return createTableSettingsPrototype();
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
}
