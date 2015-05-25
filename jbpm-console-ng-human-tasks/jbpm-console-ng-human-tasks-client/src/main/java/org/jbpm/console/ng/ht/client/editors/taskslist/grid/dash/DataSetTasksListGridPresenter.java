/*
 * Copyright 2012 JBoss Inc
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

import com.google.gwt.core.client.GWT;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.dashbuilder.dataset.DataSet;

import org.dashbuilder.dataset.client.DataSetClientServiceError;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.displayer.client.DataSetHandlerImpl;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

import org.jbpm.console.ng.gc.client.displayer.TableSettings;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;

import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.jbpm.console.ng.ht.service.TaskQueryService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.paging.PageResponse;


import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Dependent
@WorkbenchScreen(identifier = "DataSet Tasks List")
public class DataSetTasksListGridPresenter extends AbstractScreenListPresenter<TaskSummary> {

    public interface DataSetTaskListView extends ListView<TaskSummary, DataSetTasksListGridPresenter> {

    }

    @Inject
    private DataSetTaskListView view;

    private Constants constants = GWT.create(Constants.class);

    @Inject
    private Caller<TaskQueryService> taskQueryService;

    @Inject
    private Caller<TaskLifeCycleService> taskOperationsService;


    @Inject
    private ErrorPopupPresenter errorPopup;


    public DataSetTasksListGridPresenter() {
        dataProvider = new AsyncDataProvider<TaskSummary>() {

            @Override
            protected void onRangeChanged(HasData<TaskSummary> display) {
                view.showBusyIndicator(constants.Loading());
                final Range visibleRange = view.getListGrid().getVisibleRange();
                getData(visibleRange);

            }
        };
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData(final Range visibleRange) {
        try {
            if(currentTableSetting!=null) {
                currentTableSetting.setTablePageSize( view.getListGrid().getPageSize() );
                dataSetHandler = new DataSetHandlerImpl( currentTableSetting.getDataSetLookup() );
                super.lookupDataSet( visibleRange.getStart(), new DataSetReadyCallback() {
                    @Override
                    public void callback( DataSet dataSet ) {
                        if ( dataSet != null && dataSet.getRowCount() > 0 ) {
                            List<TaskSummary> myTasksFromDataSet = new ArrayList<TaskSummary>();

                            for ( int i = 0; i < dataSet.getRowCount(); i++ ) {
                                myTasksFromDataSet.add( new TaskSummary(
                                                getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_TASKID, i ),
                                                getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_NAME, i ),
                                                getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION, i ),
                                                getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_STATUS, i ),
                                                getColumnIntValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PRIORITY, i ),
                                                getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER, i ),
                                                getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_CREATEDBY, i ),
                                                getColumnDateValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_CREATEDON, i ),
                                                getColumnDateValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME, i ),
                                                getColumnDateValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_DUEDATE, i ),
                                                getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PROCESSID, i ),
                                                getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID, i ),
                                                getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID, i ),
                                                getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID, i ),
                                                getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PARENTID, i ) )
                                );

                            }
                            PageResponse<TaskSummary> taskSummaryPageResponse = new PageResponse<TaskSummary>();
                            taskSummaryPageResponse.setPageRowList( myTasksFromDataSet );
                            taskSummaryPageResponse.setStartRowIndex( visibleRange.getStart() );
                            taskSummaryPageResponse.setTotalRowSize( dataSet.getRowCountNonTrimmed() );

                            DataSetTasksListGridPresenter.this.updateDataOnCallback( taskSummaryPageResponse );
                        }
                        view.hideBusyIndicator();
                    }

                    @Override
                    public void notFound() {
                        view.hideBusyIndicator();
                        errorPopup.showMessage( "Not found DataSet with UUID [  jbpmHumanTasks ] " );
                        GWT.log( "DataSet with UUID [  jbpmHumanTasks ] not found." );
                    }

                    @Override
                    public boolean onError( DataSetClientServiceError error ) {
                        view.hideBusyIndicator();
                        errorPopup.showMessage( "DataSet with UUID [  jbpmHumanTasks ] error: " + error.getThrowable() );
                        GWT.log( "DataSet with UUID [  jbpmHumanTasks ] error: ", error.getThrowable() );
                        return false;
                    }
                } );
            }else {
                view.hideBusyIndicator();
            }
        } catch (Exception e) {
            GWT.log("Error looking up dataset with UUID [ jbpmHumanTasks ]");
        }

    }

    public void filterGrid(TableSettings tableSettings) {
        this.currentTableSetting = tableSettings;
        refreshGrid();
    }


    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List() + "DB";
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

    public Long getColumnLongValue (DataSet currentDataSet, String columnId, int index){
        try{
            return (Long)currentDataSet.getColumnById( columnId ).getValues().get( index );
        } catch ( Exception e ){

        }
        return null;
    }

    public String getColumnStringValue(DataSet currentDataSet, String columnId, int index){
        try{
            return (String)currentDataSet.getColumnById( columnId ).getValues().get( index );
        } catch ( Exception e ){

        }
        return null;
    }

    public Date getColumnDateValue(DataSet currentDataSet,String columnId, int index){
        try{
            return (Date) currentDataSet.getColumnById( columnId ).getValues().get( index );
        } catch ( Exception e ){

        }
        return null;
    }
    public int getColumnIntValue(DataSet currentDataSet,String columnId, int index){
        try{
            return Integer.parseInt((String)currentDataSet.getColumnById( columnId ).getValues().get( index) );
        } catch ( Exception e ){

        }
        return -1;
    }



}