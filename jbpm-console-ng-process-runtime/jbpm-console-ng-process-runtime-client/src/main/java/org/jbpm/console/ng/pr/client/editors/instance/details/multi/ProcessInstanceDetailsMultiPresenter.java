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
package org.jbpm.console.ng.pr.client.editors.instance.details.multi;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.client.editors.diagram.ProcessDiagramUtil;
import org.jbpm.console.ng.pr.client.editors.documents.list.ProcessDocumentListPresenter;
import org.jbpm.console.ng.pr.client.editors.instance.details.ProcessInstanceDetailsPresenter;
import org.jbpm.console.ng.pr.client.editors.instance.log.RuntimeLogPresenter;
import org.jbpm.console.ng.pr.client.editors.variables.list.ProcessVariableListPresenter;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
@WorkbenchScreen(identifier = "Process Instance Details Multi", preferredWidth = 500)
public class ProcessInstanceDetailsMultiPresenter {

    public interface ProcessInstanceDetailsMultiView
            extends UberView<ProcessInstanceDetailsMultiPresenter> {

        IsWidget getOptionsButton();

        IsWidget getRefreshButton();

        IsWidget getCloseButton();

        void selectInstanceDetailsTab();
    }

    @Inject
    public ProcessInstanceDetailsMultiView view;

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceSelected;
    
    @Inject
    private Event<ProcessInstancesUpdateEvent> processInstancesUpdatedEvent;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Inject
    private ProcessInstanceDetailsPresenter detailsPresenter;

    @Inject
    private ProcessVariableListPresenter variableListPresenter;

    @Inject
    private ProcessDocumentListPresenter documentListPresenter;

    @Inject
    private RuntimeLogPresenter runtimeLogPresenter;

    private String selectedDeploymentId = "";

    private int selectedProcessInstanceStatus = 0;

    private String selectedProcessDefName = "";

    private PlaceRequest place;

    private String deploymentId = "";

    private String processId = "";

    @WorkbenchPartView
    public UberView<ProcessInstanceDetailsMultiPresenter> getView() {
        return view;
    }

    @DefaultPosition
    public Position getPosition() {
        return CompassPosition.EAST;
    }



    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Details();
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    public void onProcessSelectionEvent( @Observes ProcessInstanceSelectionEvent event ) {
        deploymentId = String.valueOf( event.getProcessInstanceId() );
        processId = event.getProcessDefId();
        selectedDeploymentId = event.getDeploymentId();
        selectedProcessInstanceStatus = event.getProcessInstanceStatus();
        selectedProcessDefName = event.getProcessDefName();

        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( this.place, String.valueOf(deploymentId) + " - " + selectedProcessDefName ) );

        view.selectInstanceDetailsTab();
    }

    public void refresh() {
        processInstanceSelected.fire( new ProcessInstanceSelectionEvent( selectedDeploymentId, Long.valueOf( deploymentId ), processId, selectedProcessDefName, selectedProcessInstanceStatus ) );
    }

    public void signalProcessInstance() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Signal Process Popup" );
        placeRequestImpl.addParameter( "processInstanceId", deploymentId );
        placeManager.goTo( placeRequestImpl );

    }

    public void abortProcessInstance() {
        dataServices.call( new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback( ProcessInstanceSummary processInstance ) {
                if ( processInstance.getState() == ProcessInstance.STATE_ACTIVE ||
                        processInstance.getState() == ProcessInstance.STATE_PENDING ) {
                    if ( Window.confirm( "Are you sure that you want to abort the process instance?" ) ) {
                        final long processInstanceId = Long.parseLong(deploymentId);
                        kieSessionServices.call( new RemoteCallback<Void>() {
                            @Override
                            public void callback( Void v ) {
                                processInstancesUpdatedEvent.fire(new ProcessInstancesUpdateEvent(0L));
                            }
                        }, new ErrorCallback<Message>() {
                            @Override
                            public boolean error( Message message,
                                                  Throwable throwable ) {
                                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                                return true;
                            }
                        } ).abortProcessInstance( processInstanceId );
                    }
                } else {
                    Window.alert( "Process instance needs to be active in order to be aborted" );
                }
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getProcessInstanceById( Long.parseLong( deploymentId ) );
    }

    public void goToProcessInstanceModelPopup() {
        if ( place != null && !deploymentId.equals( "" ) ) {
            dataServices.call( new RemoteCallback<List<NodeInstanceSummary>>() {
                @Override
                public void callback( List<NodeInstanceSummary> activeNodes ) {
                    final StringBuffer nodeParam = new StringBuffer();
                    for ( NodeInstanceSummary activeNode : activeNodes ) {
                        nodeParam.append( activeNode.getNodeUniqueName() + "," );
                    }
                    if ( nodeParam.length() > 0 ) {
                        nodeParam.deleteCharAt( nodeParam.length() - 1 );
                    }

                    dataServices.call( new RemoteCallback<List<NodeInstanceSummary>>() {
                        @Override
                        public void callback( List<NodeInstanceSummary> completedNodes ) {
                            StringBuffer completedNodeParam = new StringBuffer();
                            for ( NodeInstanceSummary completedNode : completedNodes ) {
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

                            placeManager.goTo( ProcessDiagramUtil.buildPlaceRequest( new DefaultPlaceRequest( "" )
                                                                                             .addParameter( "activeNodes", nodeParam.toString() )
                                                                                             .addParameter( "completedNodes", completedNodeParam.toString() )
                                                                                             .addParameter( "readOnly", "true" )
                                                                                             .addParameter( "processId", processId)
                                                                                             .addParameter( "deploymentId", selectedDeploymentId ) ) );

                        }
                    }, new ErrorCallback<Message>() {
                        @Override
                        public boolean error( Message message,
                                              Throwable throwable ) {
                            ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                            return true;
                        }
                    } ).getProcessInstanceCompletedNodes( Long.parseLong(deploymentId) );

                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error( Message message,
                                      Throwable throwable ) {
                    ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                    return true;
                }
            } ).getProcessInstanceActiveNodes( Long.parseLong(deploymentId) );

        }
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return view.getOptionsButton();
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
                                return view.getRefreshButton();
                            }
                        };
                    }
                } ).endMenu()

                .build();
    }

    public void closeDetails() {
        placeManager.closePlace( place );
    }

    public void variableListRefreshGrid() {
        variableListPresenter.refreshGrid();
    }

    public void documentListRefreshGrid() {
        documentListPresenter.refreshGrid();
    }

    public IsWidget getProcessIntanceView() {
        return detailsPresenter.getWidget();
    }

    public IsWidget getProcessVariablesView() {
        return variableListPresenter.getWidget();
    }

    public IsWidget getDocumentView() {
        return documentListPresenter.getWidget();
    }

    public IsWidget getLogsView() {
        return runtimeLogPresenter.getWidget();
    }

}
