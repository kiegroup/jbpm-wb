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
package org.jbpm.console.ng.pr.client.editors.instance.details;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.ga.model.process.DummyProcessPath;
import org.jbpm.console.ng.gc.client.list.base.AbstractListPresenter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.NodeInstanceLogSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceKey;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceStyleEvent;
import org.jbpm.console.ng.pr.service.ProcessInstanceNodeHistoryService;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.paging.PageResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

@Dependent
public class ProcessInstanceDetailsPresenter extends AbstractListPresenter<NodeInstanceLogSummary> {

    private String currentDeploymentId;
    private String currentProcessInstanceId;
    private String currentProcessDefId;

    public interface ProcessInstanceNodeHistoryView extends AbstractListView.ListView<NodeInstanceLogSummary, ProcessInstanceDetailsPresenter> {

        void displayNotification( String text );

        void initGrid( ProcessInstanceDetailsPresenter presenter );
    }

    public interface ProcessInstanceDetailsView extends IsWidget {

        void displayNotification( String text );

        FormControlStatic getProcessDefinitionIdText();

        FormControlStatic getStateText();

        FormControlStatic getProcessDeploymentText();

        FormControlStatic getProcessVersionText();

        FormControlStatic getCorrelationKeyText();

        FormControlStatic getParentProcessInstanceIdText();

        void setProcessAssetPath( Path processAssetPath );

        void setEncodedProcessSource( String encodedProcessSource );

        Path getProcessAssetPath();

        String getEncodedProcessSource();

        void initLables();
    }

    public interface ProcessInstanceDetailsWholeView extends IsWidget  {

        ProcessInstanceNodeHistoryView getNodeHistoryView();

        ProcessInstanceDetailsView getProcessInstanceDetailsView();

        void setUiBinder();
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ProcessInstanceDetailsWholeView wholeView;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Caller<ProcessInstanceService> processInstanceService;

    @Inject
    private Event<ProcessInstanceStyleEvent> processInstanceStyleEvent;

    @Inject
    private Caller<ProcessInstanceNodeHistoryService> processInstanceFullHistoryService;

    @Inject
    private Caller<VFSService> fileServices;

    private Constants constants = GWT.create( Constants.class );

    private ProcessInstanceSummary processSelected = null;

    private int processInstanceStatus = 0;

    public IsWidget getWidget() {
        return wholeView;
    }

    @PostConstruct
    public void init() {
        wholeView.getNodeHistoryView().initGrid( this );
        wholeView.getProcessInstanceDetailsView().initLables();
        wholeView.setUiBinder();
    }

    public void refreshProcessInstanceData( final String deploymentId, final String processId, final String processDefId ) {
        processSelected = null;
        wholeView.getProcessInstanceDetailsView().getProcessDefinitionIdText().setText( processId );

        dataServices.call( new RemoteCallback<ProcessSummary>() {

            @Override
            public void callback( ProcessSummary process ) {
                wholeView.getProcessInstanceDetailsView().getProcessDefinitionIdText().setText( process.getProcessDefId() );
                wholeView.getProcessInstanceDetailsView().getProcessVersionText().setText( process.getVersion() );
            }
        }, new ErrorCallback<Message>() {

            @Override
            public boolean error( Message message,
                    Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getProcessDesc( deploymentId, processDefId );

        processInstanceService.call( new RemoteCallback<ProcessInstanceSummary>() {

            @Override
            public void callback( ProcessInstanceSummary process ) {
                wholeView.getProcessInstanceDetailsView().getProcessDeploymentText().setText( process.getDeploymentId() );
                wholeView.getProcessInstanceDetailsView().getCorrelationKeyText().setText( process.getCorrelationKey() );
                if ( process.getParentId() > 0 ) {
                    wholeView.getProcessInstanceDetailsView().getParentProcessInstanceIdText().setText( process.getParentId().toString() );
                } else {
                    wholeView.getProcessInstanceDetailsView().getParentProcessInstanceIdText().setText( constants.No_Parent_Process_Instance() );
                }
                String statusStr = "Unknown";
                switch ( process.getState() ) {
                    case ProcessInstance.STATE_ACTIVE:
                        statusStr = "Active";
                        break;
                    case ProcessInstance.STATE_ABORTED:
                        statusStr = "Aborted";
                        break;
                    case ProcessInstance.STATE_COMPLETED:
                        statusStr = "Completed";
                        break;
                    case ProcessInstance.STATE_PENDING:
                        statusStr = "Pending";
                        break;
                    case ProcessInstance.STATE_SUSPENDED:
                        statusStr = "Suspended";
                        break;
                    default:
                        break;
                }
                wholeView.getProcessInstanceDetailsView().getStateText().setText( statusStr );
                processSelected = process;
                changeStyleRow( Long.parseLong( processId ), processSelected.getProcessName(), processSelected.getProcessVersion(),
                        processSelected.getStartTime() );

            }
        }, new ErrorCallback<Message>() {

            @Override
            public boolean error( Message message,
                    Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getItem( new ProcessInstanceKey( Long.parseLong( processId ) ) );

        dataServices.call( new RemoteCallback<ProcessSummary>() {

            @Override
            public void callback( final ProcessSummary process ) {
                if ( process != null ) {
                    wholeView.getProcessInstanceDetailsView().setEncodedProcessSource( process.getEncodedProcessSource() );
                    if ( process.getOriginalPath() != null ) {
                        fileServices.call( new RemoteCallback<Path>() {

                            @Override
                            public void callback( Path processPath ) {
                                wholeView.getProcessInstanceDetailsView().setProcessAssetPath( processPath );
                                if ( processSelected != null ) {
                                    changeStyleRow( processSelected.getProcessInstanceId(), processSelected.getProcessName(), processSelected.getProcessVersion(),
                                            processSelected.getStartTime() );
                                }
                            }
                        } ).get( process.getOriginalPath() );
                    } else {
                        wholeView.getProcessInstanceDetailsView().setProcessAssetPath( new DummyProcessPath( process.getProcessDefId() ) );
                    }
                    if ( processSelected != null ) {
                        changeStyleRow( processSelected.getProcessInstanceId(), processSelected.getProcessName(), processSelected.getProcessVersion(),
                                processSelected.getStartTime() );
                    }
                } else {
                    // set to null to ensure it's clear state
                    wholeView.getProcessInstanceDetailsView().setEncodedProcessSource( null );
                    wholeView.getProcessInstanceDetailsView().setProcessAssetPath( null );
                }
            }
        }, new ErrorCallback<Message>() {

            @Override
            public boolean error( Message message,
                    Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getProcessById( deploymentId, processDefId );

    }

    private void changeStyleRow( long processInstanceId, String processDefName, String processDefVersion, Date startTime ) {
        processInstanceStyleEvent.fire( new ProcessInstanceStyleEvent( processInstanceId, processDefName, processDefVersion, startTime ) );

    }

    public void onProcessInstanceSelectionEvent( @Observes ProcessInstanceSelectionEvent event ) {
        this.currentDeploymentId = event.getDeploymentId();
        this.currentProcessInstanceId = String.valueOf( event.getProcessInstanceId() );
        this.currentProcessDefId = event.getProcessDefId();
        this.processInstanceStatus = event.getProcessInstanceStatus();
        super.refreshGrid();
        refreshProcessInstanceData( currentDeploymentId, currentProcessInstanceId, currentProcessDefId );
    }

    public int getProcessInstanceStatus() {
        return this.processInstanceStatus;
    }

    @Override
    protected ListView getListView() {
        return  wholeView.getNodeHistoryView();
    }

    @Override
    public void getData( Range visibleRange ) {
        wholeView.getNodeHistoryView().showBusyIndicator( constants.Loading() );
        if ( currentProcessInstanceId != null ) {
            final Range visibleRangeView = getListView().getListGrid().getVisibleRange();
            ColumnSortList columnSortList = wholeView.getNodeHistoryView().getListGrid().getColumnSortList();
            if ( currentFilter == null ) {
                currentFilter = new PortableQueryFilter( visibleRangeView.getStart(),
                        visibleRangeView.getLength(),
                        false, "",
                        (columnSortList.size() > 0) ? columnSortList.get( 0 )
                                .getColumn().getDataStoreName() : "",
                        (columnSortList.size() > 0) ? columnSortList.get( 0 )
                                .isAscending() : true );
            }
            if ( currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
                    || currentFilter.getParams().get( "textSearch" ) == null || currentFilter.getParams().get( "textSearch" ).equals( "" ) ) {
                currentFilter.setOffset( visibleRangeView.getStart() );
                currentFilter.setCount( visibleRangeView.getLength() );
            } else {
                currentFilter.setOffset( 0 );
                currentFilter.setCount( wholeView.getNodeHistoryView().getListGrid().getPageSize() );
            }
            if ( currentFilter.getParams() == null ) {
                currentFilter.setParams( new HashMap<String, Object>() );
            }
            currentFilter.getParams().put( "processInstanceId", currentProcessInstanceId );

            currentFilter.setOrderBy( (columnSortList.size() > 0) ? columnSortList.get( 0 )
                    .getColumn().getDataStoreName() : "" );
            currentFilter.setIsAscending( (columnSortList.size() > 0) ? columnSortList.get( 0 )
                    .isAscending() : true );

            processInstanceFullHistoryService.call( new RemoteCallback<PageResponse<NodeInstanceLogSummary>>() {

                @Override
                public void callback( PageResponse<NodeInstanceLogSummary> response ) {
                    wholeView.getNodeHistoryView().hideBusyIndicator();
                    dataProvider.updateRowCount( response.getTotalRowSize(), response.isTotalRowSizeExact() );
                    dataProvider.updateRowData( response.getStartRowIndex(), response.getPageRowList() );
                }
            }, new ErrorCallback<Message>() {

                @Override
                public boolean error( Message message,
                        Throwable throwable ) {
                    wholeView.getNodeHistoryView().hideBusyIndicator();
                    wholeView.getNodeHistoryView().displayNotification( "Error: Getting history of Process instance: " + message );
                    GWT.log( throwable.toString() );
                    return true;
                }
            } ).getData( currentFilter );
        }

    }
}
