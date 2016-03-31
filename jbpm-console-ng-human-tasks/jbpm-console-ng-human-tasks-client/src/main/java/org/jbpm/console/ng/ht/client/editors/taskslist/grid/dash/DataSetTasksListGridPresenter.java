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



import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
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


import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.paging.PageResponse;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.console.ng.ht.client.editors.quicknewtask.QuickNewTaskPopup;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;
import org.uberfire.mvp.Command;

import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.filter.FilterFactory.likeTo;

@Dependent
@WorkbenchScreen(identifier = "DataSet Tasks List")
public class DataSetTasksListGridPresenter extends AbstractScreenListPresenter<TaskSummary> {

    public interface DataSetTaskListView extends ListView<TaskSummary, DataSetTasksListGridPresenter> {
        int getRefreshValue();
        void restoreTabs();
        void saveRefreshValue(int newValue);
        void applyFilterOnPresenter(String key);
    }

    @Inject
    private DataSetTaskListView view;

    private Constants constants = GWT.create(Constants.class);


    @Inject
    private Caller<TaskLifeCycleService> taskOperationsService;

    @Inject
    DataSetQueryHelper dataSetQueryHelper;

    @Inject
    private QuickNewTaskPopup quickNewTaskPopup;


    @Inject
    private ErrorPopupPresenter errorPopup;


    public DataSetTasksListGridPresenter() {


        dataProvider = new AsyncDataProvider<TaskSummary>() {

            @Override
            protected void onRangeChanged(HasData<TaskSummary> display) {
                view.showBusyIndicator(constants.Loading());
                final Range visibleRange = view.getListGrid().getVisibleRange();
                getData( visibleRange );

            }
        };
    }

    public DataSetTasksListGridPresenter(DataSetTaskListView view,
            Caller<TaskLifeCycleService> taskOperationsService,
            DataSetQueryHelper dataSetQueryHelper,
                                         User identity
            ) {
        this.view = view;
        this.taskOperationsService = taskOperationsService;
        this.dataSetQueryHelper = dataSetQueryHelper;
        this.identity = identity;
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData(final Range visibleRange) {
        try {

            if(!isAddingDefaultFilters()) {
                FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
                if ( currentTableSettings != null ) {
                    currentTableSettings.setTablePageSize( view.getListGrid().getPageSize() );
                    ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                    if ( columnSortList != null && columnSortList.size() > 0 ) {
                        dataSetQueryHelper.setLastOrderedColumn( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 ).getColumn().getDataStoreName() : "" );
                        dataSetQueryHelper.setLastSortOrder( ( columnSortList.size() > 0 ) && columnSortList.get( 0 ).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING );
                    } else {
                        dataSetQueryHelper.setLastOrderedColumn( DataSetTasksListGridViewImpl.COLUMN_CREATEDON );
                        dataSetQueryHelper.setLastSortOrder( SortOrder.ASCENDING );
                    }
                    if ( textSearchStr != null && textSearchStr.trim().length() > 0 ) {

                        DataSetFilter filter = new DataSetFilter();
                        List<ColumnFilter> filters = new ArrayList<ColumnFilter>();
                        filters.add( likeTo( DataSetTasksListGridViewImpl.COLUMN_NAME, "%" + textSearchStr.toLowerCase() + "%", false ) );
                        filters.add( likeTo( DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION, "%" + textSearchStr.toLowerCase() + "%", false ) );
                        filters.add( likeTo( DataSetTasksListGridViewImpl.COLUMN_PROCESSID, "%" + textSearchStr.toLowerCase() + "%", false ) );
                        filter.addFilterColumn( OR( filters ) );

                        if ( currentTableSettings.getDataSetLookup().getFirstFilterOp() != null ) {
                            currentTableSettings.getDataSetLookup().getFirstFilterOp().addFilterColumn( OR( filters ) );
                        } else {
                            currentTableSettings.getDataSetLookup().addOperation( filter );
                        }
                        textSearchStr = "";
                    }
                    boolean isAdminDataset = currentTableSettings.getDataSetLookup().getDataSetUUID().equals(DataSetTasksListGridViewImpl.HUMAN_TASKS_WITH_ADMINS_DATASET);
                    if( isAdminDataset ||
                            currentTableSettings.getDataSetLookup().getDataSetUUID().equals(DataSetTasksListGridViewImpl.HUMAN_TASKS_WITH_USERS_DATASET)) {

                        if (currentTableSettings.getDataSetLookup().getFirstFilterOp() != null) {
                            currentTableSettings.getDataSetLookup().getFirstFilterOp().addFilterColumn(getUserGroupFilters(isAdminDataset));
                        } else {
                            final DataSetFilter filter = new DataSetFilter();
                            filter.addFilterColumn(getUserGroupFilters(isAdminDataset));
                            currentTableSettings.getDataSetLookup().addOperation(filter);
                        }

                    }
                    dataSetQueryHelper.setDataSetHandler( currentTableSettings );
                    dataSetQueryHelper.lookupDataSet( visibleRange.getStart(), new DataSetReadyCallback() {
                        @Override
                        public void callback( DataSet dataSet ) {
                            if ( dataSet != null) {
                                List<TaskSummary> myTasksFromDataSet = new ArrayList<TaskSummary>();

                                for ( int i = 0; i < dataSet.getRowCount(); i++ ) {
                                    myTasksFromDataSet.add( createTaskSummaryFromDataSet(dataSet, i) );

                                }
                                PageResponse<TaskSummary> taskSummaryPageResponse = new PageResponse<TaskSummary>();
                                taskSummaryPageResponse.setPageRowList( myTasksFromDataSet );
                                taskSummaryPageResponse.setStartRowIndex( visibleRange.getStart() );
                                taskSummaryPageResponse.setTotalRowSize( dataSet.getRowCountNonTrimmed() );
                                taskSummaryPageResponse.setTotalRowSizeExact( true );
                                if ( visibleRange.getStart() + dataSet.getRowCount() == dataSet.getRowCountNonTrimmed() ) {
                                    taskSummaryPageResponse.setLastPage( true );
                                } else {
                                    taskSummaryPageResponse.setLastPage( false );
                                }
                                DataSetTasksListGridPresenter.this.updateDataOnCallback( taskSummaryPageResponse );
                            }

                        }

                        @Override
                        public void notFound() {
                            errorPopup.showMessage( "Not found DataSet with UUID [  jbpmHumanTasks ] " );
                            GWT.log( "DataSet with UUID [  jbpmHumanTasks ] not found." );
                        }

                        @Override
                        public boolean onError( final ClientRuntimeError error ) {
                            error.getThrowable().printStackTrace();
                            errorPopup.showMessage( "DataSet with UUID [  jbpmHumanTasks ] error: " + error.getThrowable() );
                            GWT.log( "DataSet with UUID [  jbpmHumanTasks ] error: ", error.getThrowable() );
                            return false;
                        }
                    } );

                }
                view.hideBusyIndicator();
            }
        } catch (Exception e) {
            GWT.log("Error looking up dataset with UUID [ jbpmHumanTasks ]");
        }

    }

    protected TaskSummary createTaskSummaryFromDataSet(final DataSet dataSet, int i) {
        return new TaskSummary(
                dataSetQueryHelper.getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_TASKID, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_NAME, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_STATUS, i ),
                dataSetQueryHelper.getColumnIntValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PRIORITY, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_CREATEDBY, i ),
                dataSetQueryHelper.getColumnDateValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_CREATEDON, i ),
                dataSetQueryHelper.getColumnDateValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME, i ),
                dataSetQueryHelper.getColumnDateValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_DUEDATE, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PROCESSID, i ),
                dataSetQueryHelper.getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID, i ),
                dataSetQueryHelper.getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID, i ),
                dataSetQueryHelper.getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PARENTID, i ),
                DataSetTasksListGridViewImpl.HUMAN_TASKS_WITH_ADMINS_DATASET.equals(dataSetQueryHelper.getCurrentTableSettings().getDataSetLookup().getDataSetUUID()));
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
    protected ColumnFilter getUserGroupFilters(boolean isAdminDataset ) {
        Set<Group> groups = identity.getGroups();
        List<ColumnFilter> condList = new ArrayList<ColumnFilter>();

        for ( Group g : groups ) {
            condList.add( FilterFactory.equalsTo(DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY, g.getName()) );
        }

        condList.add( FilterFactory.equalsTo( DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY, identity.getIdentifier() ) );

        ColumnFilter myGroupFilter;
        if(isAdminDataset){
                return  FilterFactory.OR( DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY,  condList  );
            } else {
                myGroupFilter =
                        FilterFactory.AND(FilterFactory.OR(condList),
                                          FilterFactory.OR(FilterFactory.equalsTo(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER, ""),
                                                           FilterFactory.isNull(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER)));
                return FilterFactory.OR(myGroupFilter, FilterFactory.equalsTo(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER, identity.getIdentifier()));
            }
    }


    public void filterGrid(FilterSettings tableSettings) {
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

    public void releaseTask(final Long taskId, final String userId) {
        taskOperationsService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Task Released");
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                errorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).release(taskId, userId);
    }

    public void claimTask(final Long taskId, final String userId, final String deploymentId) {
        taskOperationsService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Task Claimed");
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                errorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).claim(taskId, userId, deploymentId);
    }

    @WorkbenchMenu
    public Menus getMenus() {

        view.setupButtons();
        setupRefreshButton();

        return MenuFactory

                .newTopLevelMenu( Constants.INSTANCE.New_Task() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        quickNewTaskPopup.show();
                    }
                } )
                .endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                view.getMenuRefreshButton().addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent clickEvent ) {
                                        refreshGrid();
                                    }
                                } );
                                return view.getMenuRefreshButton();
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return "org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTaskListGridPresenter#menuRefreshButton";
                            }

                        };
                    }
                } ).endMenu()


                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return menuActionsButton;
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return "org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTaskListGridPresenter#menuActionsButton";
                            }

                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                view.getMenuResetTabsButton().addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent clickEvent ) {
                                        view.restoreTabs();
                                    }
                                } );
                                return view.getMenuResetTabsButton();
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return "org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTaskListGridPresenter#menuResetTabsButton";
                            }

                        };
                    }
                } ).endMenu()
                .build();


    }

    @Override
    protected void onSearchEvent( @Observes SearchEvent searchEvent ) {
        textSearchStr = searchEvent.getFilter();
        if(textSearchStr!=null && textSearchStr.trim().length()>0){
            Map<String, Object> params = new HashMap<String, Object>();
            params.put( "textSearch", textSearchStr );
            dataSetQueryHelper.getCurrentTableSettings().getKey();

            view.applyFilterOnPresenter( dataSetQueryHelper.getCurrentTableSettings().getKey() );
        }
    }

    protected void saveRefreshValue(int newValue){
        view.saveRefreshValue( newValue );
    }

    protected int getRefreshValue(){
        return view.getRefreshValue();
    }

    public void setupRefreshButton( ) {
        menuActionsButton = new Button();
        createRefreshToggleButton(menuActionsButton);
    }


}