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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.DummyProcessPath;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Process Instance Details")
public class ProcessInstanceDetailsPresenter {

    private Constants constants = GWT.create( Constants.class );

    private PlaceRequest place;

    public interface ProcessInstanceDetailsView extends UberView<ProcessInstanceDetailsPresenter> {

        void displayNotification( String text );

        ListBox getCurrentActivitiesListBox();

        TextArea getLogTextArea();

        TextBox getProcessIdText();

        TextBox getProcessNameText();

        TextBox getStateText();

        void setProcessInstance( ProcessInstanceSummary processInstance );

        TextBox getProcessDeploymentText();

        TextBox getProcessVersionText();

        void setProcessAssetPath( Path processAssetPath );

        void setCurrentActiveNodes( List<NodeInstanceSummary> activeNodes );

        void setCurrentCompletedNodes( List<NodeInstanceSummary> completedNodes );

        void setEncodedProcessSource( String encodedProcessSource );
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ProcessInstanceDetailsView view;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Caller<VFSService> fileServices;

    private ListDataProvider<VariableSummary> dataProvider = new ListDataProvider<VariableSummary>();

    public static final ProvidesKey<VariableSummary> KEY_PROVIDER = new ProvidesKey<VariableSummary>() {
        @Override
        public Object getKey( VariableSummary item ) {
            return item == null ? null : item.getVariableId();
        }
    };

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Instance_Details();
    }

    @WorkbenchPartView
    public UberView<ProcessInstanceDetailsPresenter> getView() {
        return view;
    }

    public void refreshProcessInstanceData( final String processId,
                                            final String processDefId ) {
        dataServices.call( new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback( List<NodeInstanceSummary> details ) {
                view.getLogTextArea().setText( "" );
                String fullLog = "";
                for ( NodeInstanceSummary nis : details ) {
                    if(!nis.getNodeName().equals("")){
                        fullLog += nis.getTimestamp() + ": " + nis.getId() + " - " + nis.getNodeName() + " (" + nis.getType()
                                + ") \n";
                    }else{
                        fullLog += nis.getTimestamp() + ": " + nis.getId() + " - " + nis.getType() + "\n";
                    }
                }
                view.getLogTextArea().setText( fullLog );
            }
        } ).getProcessInstanceHistory( Long.parseLong( processId ) );
        dataServices.call( new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback( List<NodeInstanceSummary> details ) {
                view.setCurrentActiveNodes( details );
                view.getCurrentActivitiesListBox().clear();
                for ( NodeInstanceSummary nis : details ) {

                    view.getCurrentActivitiesListBox().addItem(
                            nis.getTimestamp() + ":" + nis.getId() + " - " + nis.getNodeName() + " (" + nis.getType() + ")",
                            String.valueOf( nis.getId() ) );
                }
            }
        } ).getProcessInstanceActiveNodes( Long.parseLong( processId ) );

        dataServices.call( new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback( ProcessSummary process ) {
                view.getProcessIdText().setText( process.getId());
                view.getProcessNameText().setText( process.getName() );
                view.getProcessDeploymentText().setText( process.getDeploymentId() );
                view.getProcessVersionText().setText( process.getVersion() );
            }
        } ).getProcessDesc( processDefId );

        dataServices.call( new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback( ProcessInstanceSummary process ) {
                view.setProcessInstance( process );

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

                view.getStateText().setText( statusStr );
            }
        } ).getProcessInstanceById( Long.parseLong( processId ) );

        dataServices.call( new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback( List<NodeInstanceSummary> details ) {
                view.setCurrentCompletedNodes( details );
            }
        } ).getProcessInstanceCompletedNodes( Long.parseLong( processId ) );

        loadVariables( processId, processDefId );

        dataServices.call( new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback( ProcessSummary process ) {
                view.setEncodedProcessSource( process.getEncodedProcessSource() );
                if ( process.getOriginalPath() != null ) {
                    fileServices.call( new RemoteCallback<Path>() {
                        @Override
                        public void callback( Path processPath ) {
                            view.setProcessAssetPath( processPath );
                        }
                    } ).get( process.getOriginalPath() );
                } else {
                    view.setProcessAssetPath( new DummyProcessPath( process.getId() ) );
                }
            }
        } ).getProcessById( processDefId );
    }

    public void addDataDisplay( HasData<VariableSummary> display ) {
        dataProvider.addDataDisplay( display );
    }

    public ListDataProvider<VariableSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

    @OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

    @OnReveal
    public void onReveal() {
        String processId = place.getParameter( "processInstanceId", "" );
        String processDefId = place.getParameter( "processDefId", "" );
        view.getProcessIdText().setText( processId );
        view.getProcessNameText().setText( processDefId );
        refreshProcessInstanceData( processId, processDefId );
    }

    public void loadVariables( final String processId,
                               final String processDefId ) {
        dataServices.call( new RemoteCallback<List<VariableSummary>>() {
            @Override
            public void callback( List<VariableSummary> variables ) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll( variables );
                dataProvider.refresh();
            }
        } ).getVariablesCurrentState( Long.parseLong( processId ), processDefId );
    }

}
