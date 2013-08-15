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
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.DummyProcessPath;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Process Instance Details")
public class ProcessInstanceDetailsPresenter {

    private Constants constants = GWT.create( Constants.class );

    private PlaceRequest place;
    
    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;

    public interface ProcessInstanceDetailsView extends UberView<ProcessInstanceDetailsPresenter> {

        void displayNotification( String text );

        ListBox getCurrentActivitiesListBox();

        TextArea getLogTextArea();

        TextBox getProcessInstanceIdText();
        
        TextBox getProcessDefinitionIdText();

        TextBox getProcessNameText();

        TextBox getStateText();

        void setProcessInstance( ProcessInstanceSummary processInstance );

        TextBox getProcessDeploymentText();

        TextBox getProcessVersionText();

        void setProcessAssetPath( Path processAssetPath );

        void setCurrentActiveNodes( List<NodeInstanceSummary> activeNodes );

        void setCurrentCompletedNodes( List<NodeInstanceSummary> completedNodes );

        void setEncodedProcessSource( String encodedProcessSource );
        
        List<NodeInstanceSummary> getCompletedNodes();
        
        Path getProcessAssetPath();
        
        String getEncodedProcessSource();
        
        List<NodeInstanceSummary> getActiveNodes();
    }

    private Menus menus;
    
    @Inject
    private PlaceManager placeManager;

    @Inject
    private ProcessInstanceDetailsView view;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Caller<VFSService> fileServices;

    private ListDataProvider<VariableSummary> dataProvider = new ListDataProvider<VariableSummary>();
    
    private String processInstanceId = "";
    
    private String processDefId = "";

    public ProcessInstanceDetailsPresenter() {
        makeMenuBar();
    }


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
        
        view.getProcessDefinitionIdText().setText(processId);
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
                
                view.getProcessDefinitionIdText().setText( process.getId());
                view.getProcessNameText().setText( process.getName() );
                view.getProcessVersionText().setText( process.getVersion() );
            }
        } ).getProcessDesc( processDefId );

        dataServices.call( new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback( ProcessInstanceSummary process ) {
                view.getProcessDeploymentText().setText( process.getDeploymentId());
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

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
        this.processInstanceId = place.getParameter( "processInstanceId", "" );
        this.processDefId = place.getParameter( "processDefId", "" );
        view.getProcessInstanceIdText().setText( processInstanceId );
        view.getProcessNameText().setText( processDefId );
        refreshProcessInstanceData( processInstanceId, processDefId );
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
    
    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }
    
    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( constants.Signal())
                .respondsWith(new Command() {
                        @Override
                        public void execute() {

                            PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Signal Process Popup");
                            placeRequestImpl.addParameter("processInstanceId", view.getProcessInstanceIdText().getText());
                            placeManager.goTo(placeRequestImpl);
                        }
                })
                .endMenu()
                .newTopLevelMenu( constants.Abort())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        final long processInstanceId = Long.parseLong(view.getProcessInstanceIdText().getText());
                        kieSessionServices.call(new RemoteCallback<Void>() {
                                @Override
                                public void callback(Void v) {
                                    refreshProcessInstanceData( view.getProcessInstanceIdText().getText(), 
                                    view.getProcessDefinitionIdText().getText() );
                                    view.displayNotification(constants.Aborting_Process_Instance() + "(id=" + processInstanceId + ")");
                                    
                                }
                            }).abortProcessInstance(processInstanceId);
  
                    }
                })
                .endMenu()
                .newTopLevelMenu( constants.View_Process_Model())
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        StringBuffer nodeParam = new StringBuffer();
                        for ( NodeInstanceSummary activeNode : view.getActiveNodes() ) {
                            nodeParam.append( activeNode.getNodeUniqueName() + "," );
                        }
                        if ( nodeParam.length() > 0 ) {
                            nodeParam.deleteCharAt( nodeParam.length() - 1 );
                        }

                        StringBuffer completedNodeParam = new StringBuffer();
                        for ( NodeInstanceSummary completedNode : view.getCompletedNodes() ) {
                            if ( completedNode.isCompleted() ) {
                                // insert outgoing sequence flow and node as this is for on entry event
                                completedNodeParam.append( completedNode.getNodeUniqueName() + "," );
                                completedNodeParam.append( completedNode.getConnection() + "," );
                            } else if ( completedNode.getConnection() != null ) {
                                // insert only incoming sequence flow as node id was already inserted
                                completedNodeParam.append( completedNode.getConnection() + "," );
                            }

                        }
                        completedNodeParam.deleteCharAt( completedNodeParam.length() - 1 );

                        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Designer" );
                        placeRequestImpl.addParameter( "activeNodes", nodeParam.toString() );
                        placeRequestImpl.addParameter( "completedNodes", completedNodeParam.toString() );
                        placeRequestImpl.addParameter( "readOnly", "true" );
                        if ( view.getEncodedProcessSource() != null ) {
                            placeRequestImpl.addParameter( "encodedProcessSource", view.getEncodedProcessSource() );
                        }

                        placeManager.goTo( view.getProcessAssetPath(), placeRequestImpl );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( constants.Refresh() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        refreshProcessInstanceData( view.getProcessInstanceIdText().getText(), 
                                view.getProcessDefinitionIdText().getText() );
                        view.displayNotification( constants.Process_Instances_Details_Refreshed() );
                    }
                } )
                .endMenu().build();

    }

}
