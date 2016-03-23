/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.dataset.AbstractDataSetReadyCallback;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.jbpm.console.ng.gc.client.menu.RestoreDefaultFiltersMenuBuilder;
import org.jbpm.console.ng.ht.client.editors.quicknewtask.QuickNewTaskPopup;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.ext.widgets.common.client.menu.RefreshSelectorMenuBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.console.ng.ht.model.TaskDataSetConstants.*;

@Dependent
@WorkbenchScreen( identifier = "DataSet Tasks List" )
public class DataSetTasksListGridPresenter extends AbstractScreenListPresenter<TaskSummary> {

    public interface DataSetTaskListView extends ListView<TaskSummary, DataSetTasksListGridPresenter> {

        int getRefreshValue();

        void saveRefreshValue( int newValue );

        void applyFilterOnPresenter( String key );

        void addDomainSpecifColumns( ExtendedPagedTable<TaskSummary> extendedPagedTable, Set<String> columns );

        FilterSettings getVariablesTableSettings( String processName );
    }

    @Inject
    private DataSetTaskListView view;

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private Caller<TaskLifeCycleService> taskOperationsService;

    @Inject
    DataSetQueryHelper dataSetQueryHelper;

    @Inject
    private DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    @Inject
    private QuickNewTaskPopup quickNewTaskPopup;

    @Inject
    private ErrorPopupPresenter errorPopup;

    private RefreshSelectorMenuBuilder refreshSelectorMenuBuilder = new RefreshSelectorMenuBuilder(this);

    public DataSetTasksListGridPresenter() {

        dataProvider = new AsyncDataProvider<TaskSummary>() {

            @Override
            protected void onRangeChanged( HasData<TaskSummary> display ) {
                view.showBusyIndicator( constants.Loading() );
                final Range visibleRange = view.getListGrid().getVisibleRange();
                getData( visibleRange );

            }
        };
    }

    public DataSetTasksListGridPresenter(DataSetTaskListView view,
            Caller<TaskLifeCycleService> taskOperationsService,
            DataSetQueryHelper dataSetQueryHelper,
            DataSetQueryHelper dataSetQueryHelperDomainSpecific,
                                         User identity
    ) {
        this.view = view;
        this.taskOperationsService = taskOperationsService;
        this.dataSetQueryHelper = dataSetQueryHelper;
        this.dataSetQueryHelperDomainSpecific = dataSetQueryHelperDomainSpecific;
        this.identity=identity;
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData( final Range visibleRange ) {
        try {
            if ( !isAddingDefaultFilters() ) {
                FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
                if ( currentTableSettings != null ) {
                    currentTableSettings.setTablePageSize( view.getListGrid().getPageSize() );
                    ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                    if ( columnSortList != null && columnSortList.size() > 0 ) {
                        dataSetQueryHelper.setLastOrderedColumn( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 ).getColumn().getDataStoreName() : "" );
                        dataSetQueryHelper.setLastSortOrder( ( columnSortList.size() > 0 ) && columnSortList.get( 0 ).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING );
                    } else {
                        dataSetQueryHelper.setLastOrderedColumn(COLUMN_CREATED_ON);
                        dataSetQueryHelper.setLastSortOrder( SortOrder.ASCENDING );
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
                    if( currentTableSettings.getDataSetLookup().getDataSetUUID().equals(HUMAN_TASKS_WITH_ADMIN_DATASET) ||
                        currentTableSettings.getDataSetLookup().getDataSetUUID().equals(HUMAN_TASKS_WITH_USER_DATASET)) {

                        if (currentTableSettings.getDataSetLookup().getFirstFilterOp() != null) {
                            currentTableSettings.getDataSetLookup().getFirstFilterOp().addFilterColumn(getUserGroupFilters()); // add actualowner = idenfier or in my groups
                        } else {
                            final DataSetFilter filter = new DataSetFilter();
                            filter.addFilterColumn(getUserGroupFilters());
                            currentTableSettings.getDataSetLookup().addOperation(filter);
                        }

                    }
                    dataSetQueryHelper.setDataSetHandler(currentTableSettings);
                    dataSetQueryHelper.lookupDataSet(visibleRange.getStart(), createDataSetTaskCallback(visibleRange.getStart(), currentTableSettings));
                }
            }
        } catch ( Exception e ) {
            errorPopup.showMessage(Constants.INSTANCE.UnexpectedError(e.getMessage()) );
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

    protected ColumnFilter getUserGroupFilters( ) {
        Set<Group> groups = identity.getGroups();
        List<ColumnFilter> condList = new ArrayList<ColumnFilter>();
        for ( Group g : groups ) {
            condList.add( FilterFactory.equalsTo( COLUMN_ORGANIZATIONAL_ENTITY, g.getName() ) );

        }
        //Adding own identity to check against potential owners
        condList.add( FilterFactory.equalsTo( COLUMN_ORGANIZATIONAL_ENTITY, identity.getIdentifier() ) );

        ColumnFilter myGroupFilter = FilterFactory.AND(FilterFactory.OR(condList),
                FilterFactory.OR(FilterFactory.equalsTo(COLUMN_ACTUAL_OWNER, ""), FilterFactory.isNull(COLUMN_ACTUAL_OWNER)));

        return FilterFactory.OR(myGroupFilter, FilterFactory.equalsTo(COLUMN_ACTUAL_OWNER, identity.getIdentifier()));
    }

    protected DataSetReadyCallback createDataSetTaskCallback(final int startRange, final FilterSettings tableSettings){
        return new AbstractDataSetReadyCallback( errorPopup, view, tableSettings.getDataSet() )  {


            @Override
            public void callback(DataSet dataSet) {

                if (dataSet != null) {
                    final List<TaskSummary> myTasksFromDataSet = new ArrayList<TaskSummary>();

                    for (int i = 0; i < dataSet.getRowCount(); i++) {
                        myTasksFromDataSet.add(createTaskSummaryFromDataSet(dataSet, i));

                    }
                    List<DataSetOp> ops = tableSettings.getDataSetLookup().getOperationList();
                    String filterValue = isFilteredByTaskName(ops); //Add here the check to add the domain data columns taskName?


                    if (filterValue != null) {
                        getDomainSpecifDataForTasks(startRange, dataSet.getRowCountNonTrimmed(),filterValue, myTasksFromDataSet);
                    } else {
                        PageResponse<TaskSummary> taskSummaryPageResponse = new PageResponse<TaskSummary>();
                        taskSummaryPageResponse.setPageRowList(myTasksFromDataSet);
                        taskSummaryPageResponse.setStartRowIndex(startRange);
                        taskSummaryPageResponse.setTotalRowSize(dataSet.getRowCountNonTrimmed());
                        taskSummaryPageResponse.setTotalRowSizeExact(true);
                        if (startRange + dataSet.getRowCount() == dataSet.getRowCountNonTrimmed()) {
                            taskSummaryPageResponse.setLastPage(true);
                        } else {
                            taskSummaryPageResponse.setLastPage(false);
                        }

                        updateDataOnCallback(taskSummaryPageResponse);
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
                                ((CoreFunctionFilter) filter).getType()== CoreFunctionType.EQUALS_TO) {

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
    public void getDomainSpecifDataForTasks(final int startRange, final int rowCountNotTrimmed, String filterValue, final List<TaskSummary> myTasksFromDataSet) {

        FilterSettings variablesTableSettings = view.getVariablesTableSettings(filterValue);
        variablesTableSettings.setTablePageSize(-1);

        dataSetQueryHelperDomainSpecific.setDataSetHandler(variablesTableSettings);
        dataSetQueryHelperDomainSpecific.setCurrentTableSettings(variablesTableSettings);
        dataSetQueryHelperDomainSpecific.setLastOrderedColumn(COLUMN_TASK_ID);
        dataSetQueryHelperDomainSpecific.setLastSortOrder(SortOrder.ASCENDING);

        List<Comparable> tasksIds = new ArrayList<Comparable>();
        for (TaskSummary task : myTasksFromDataSet) {
            tasksIds.add(task.getTaskId());
        }
        DataSetFilter filter = new DataSetFilter();
        ColumnFilter filter1 = FilterFactory.equalsTo(COLUMN_TASK_VARIABLE_TASK_ID, tasksIds);
        filter.addFilterColumn(filter1);
        variablesTableSettings.getDataSetLookup().addOperation(filter);

        dataSetQueryHelperDomainSpecific.lookupDataSet(0, createDataSetDomainSpecificCallback(startRange, rowCountNotTrimmed, myTasksFromDataSet,variablesTableSettings.getDataSet()));

    }

    protected DataSetReadyCallback createDataSetDomainSpecificCallback(final int startRange, final int totalRowSize, final List<TaskSummary> instances, final DataSet dataset){
        return new AbstractDataSetReadyCallback( errorPopup, view, dataset ) {
            @Override
            public void callback(DataSet dataSet) {
                if(dataSet.getRowCount()>0) {
                    Set<String> columns = new HashSet<String>();
                    for (int i = 0; i < dataSet.getRowCount(); i++) {
                        Long taskId = dataSetQueryHelperDomainSpecific.getColumnLongValue(dataSet, COLUMN_TASK_ID, i);
                        String variableName = dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSet,COLUMN_TASK_VARIABLE_NAME, i);
                        String variableValue = dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSet,COLUMN_TASK_VARIABLE_VALUE, i);

                        for (TaskSummary task : instances) {
                            if (task.getTaskId().equals(taskId)) {
                                task.addDomainData(variableName, variableValue);
                                columns.add(variableName);
                            }
                        }
                    }
                    view.addDomainSpecifColumns(view.getListGrid(), columns);
                }
                PageResponse<TaskSummary> taskSummaryPageResponse = new PageResponse<TaskSummary>();
                taskSummaryPageResponse.setPageRowList(instances);
                taskSummaryPageResponse.setStartRowIndex(startRange);
                taskSummaryPageResponse.setTotalRowSize(totalRowSize);
                taskSummaryPageResponse.setTotalRowSizeExact(true);
                if (startRange + instances.size() == totalRowSize) {
                    taskSummaryPageResponse.setLastPage(true);
                } else {
                    taskSummaryPageResponse.setLastPage(false);
                }

                updateDataOnCallback(taskSummaryPageResponse);
            }

        };
    }

    protected TaskSummary createTaskSummaryFromDataSet(final DataSet dataSet, int i) {
        return new TaskSummary(
                dataSetQueryHelper.getColumnLongValue( dataSet, COLUMN_TASK_ID, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_NAME, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_DESCRIPTION, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_STATUS, i ),
                dataSetQueryHelper.getColumnIntValue(dataSet, COLUMN_PRIORITY, i),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_ACTUAL_OWNER, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_CREATED_BY, i ),
                dataSetQueryHelper.getColumnDateValue(dataSet, COLUMN_CREATED_ON, i),
                dataSetQueryHelper.getColumnDateValue(dataSet, COLUMN_ACTIVATION_TIME, i),
                dataSetQueryHelper.getColumnDateValue(dataSet, COLUMN_DUE_DATE, i),
                dataSetQueryHelper.getColumnStringValue(dataSet, COLUMN_PROCESS_ID, i),
                dataSetQueryHelper.getColumnLongValue(dataSet, COLUMN_PROCESS_SESSION_ID, i),
                dataSetQueryHelper.getColumnLongValue(dataSet, COLUMN_PROCESS_INSTANCE_ID, i),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_DEPLOYMENT_ID, i ),
                dataSetQueryHelper.getColumnLongValue( dataSet, COLUMN_PARENT_ID, i ),
                HUMAN_TASKS_WITH_ADMIN_DATASET.equals(dataSet.getUUID()));
    }

    public void filterGrid( FilterSettings tableSettings ) {
        dataSetQueryHelper.setCurrentTableSettings( tableSettings );
        refreshGrid();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List();
    }

    @WorkbenchPartView
    public UberView<DataSetTasksListGridPresenter> getView() {
        return view;
    }

    public void releaseTask( final Long taskId,
                             final String userId ) {
        taskOperationsService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( Constants.INSTANCE.TaskReleased(String.valueOf(taskId)) );
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                errorPopup.showMessage( Constants.INSTANCE.UnexpectedError(throwable.getMessage()) );
                return true;
            }
        } ).release( taskId, userId );
    }

    public void claimTask( final Long taskId,
                           final String userId,
                           final String deploymentId ) {
        taskOperationsService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( Constants.INSTANCE.TaskClaimed(String.valueOf(taskId)));
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                errorPopup.showMessage( Constants.INSTANCE.UnexpectedError(throwable.getMessage())  );
                return true;
            }
        } ).claim(taskId, userId, deploymentId );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu(Constants.INSTANCE.New_Task())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        quickNewTaskPopup.show();
                    }
                })
                .endMenu()
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .newTopLevelCustomMenu(refreshSelectorMenuBuilder).endMenu()
                .newTopLevelCustomMenu(new RestoreDefaultFiltersMenuBuilder(this)).endMenu()
                .build();
    }


    @Override
    public void onGridPreferencesStoreLoaded() {
        refreshSelectorMenuBuilder.loadOptions(view.getRefreshValue());
    }


    @Override
    public void onUpdateRefreshInterval(boolean enableAutoRefresh, int newInterval) {
        super.onUpdateRefreshInterval(enableAutoRefresh, newInterval);
        view.saveRefreshValue(newInterval);
    }

    @Override
    protected void onSearchEvent( @Observes SearchEvent searchEvent ) {
        textSearchStr = searchEvent.getFilter();
        view.applyFilterOnPresenter( dataSetQueryHelper.getCurrentTableSettings().getKey() );
    }
}