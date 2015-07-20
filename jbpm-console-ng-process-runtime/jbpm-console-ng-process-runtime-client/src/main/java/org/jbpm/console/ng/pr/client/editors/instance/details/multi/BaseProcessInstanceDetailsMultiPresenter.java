package org.jbpm.console.ng.pr.client.editors.instance.details.multi;

import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsPresenter;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView.TabbedDetailsView;
import org.jbpm.console.ng.pr.client.editors.diagram.ProcessDiagramUtil;
import org.jbpm.console.ng.pr.client.editors.instance.list.BaseProcessInstanceListPresenter;
import org.jbpm.console.ng.pr.client.editors.instance.list.BaseProcessInstanceListPresenter.BaseProcessInstanceListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

public abstract class BaseProcessInstanceDetailsMultiPresenter extends AbstractTabbedDetailsPresenter {

    public interface BaseProcessInstanceDetailsMultiView
            extends TabbedDetailsView<BaseProcessInstanceDetailsMultiPresenter> {

        IsWidget getOptionsButton();

        IsWidget getRefreshButton();

        IsWidget getCloseButton();
    }

    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;

    @Inject
    protected Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceSelected;

    @Inject
    private Event<ProcessInstancesUpdateEvent> processInstancesUpdatedEvent;

    @Inject
    protected Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    protected String selectedDeploymentId = "";

    protected int selectedProcessInstanceStatus = 0;

    protected String selectedProcessDefName = "";

    protected Constants constants = GWT.create( Constants.class );

    protected Position getPosition() {
        return CompassPosition.EAST;
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        super.onStartup( place );
    }

    public void onProcessSelectionEvent( @Observes ProcessInstanceSelectionEvent event ) {
        deploymentId = String.valueOf( event.getProcessInstanceId() );
        processId = event.getProcessDefId();
        selectedDeploymentId = event.getDeploymentId();
        selectedProcessInstanceStatus = event.getProcessInstanceStatus();
        selectedProcessDefName = event.getProcessDefName();

        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( this.place, String.valueOf( deploymentId ) + " - " + selectedProcessDefName ) );

        getSpecificView().getTabPanel().selectTab( 0 );
    }

    public void refresh() {
        processInstanceSelected.fire( new ProcessInstanceSelectionEvent( selectedDeploymentId, Long.valueOf( deploymentId ), processId, selectedProcessDefName, selectedProcessInstanceStatus ) );
    }

    public void abortProcessInstance() {
        dataServices.call( new RemoteCallback<ProcessInstanceSummary>() {

            @Override
            public void callback( ProcessInstanceSummary processInstance ) {
                if ( processInstance.getState() == ProcessInstance.STATE_ACTIVE ||
                        processInstance.getState() == ProcessInstance.STATE_PENDING ) {
                    if ( Window.confirm( "Are you sure that you want to abort the process instance?" ) ) {
                        final long processInstanceId = Long.parseLong( deploymentId );
                        kieSessionServices.call( new RemoteCallback<Void>() {

                            @Override
                            public void callback( Void v ) {
                                processInstancesUpdatedEvent.fire( new ProcessInstancesUpdateEvent( 0L ) );
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
                                    .addParameter( "processId", processId )
                                    .addParameter( "deploymentId", selectedDeploymentId ) ) );

                        }
                    }, new ErrorCallback<Message>() {

                        @Override
                        public boolean error( Message message,
                                Throwable throwable ) {
                            ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                            return true;
                        }
                    } ).getProcessInstanceCompletedNodes( Long.parseLong( deploymentId ) );

                }
            }, new ErrorCallback<Message>() {

                @Override
                public boolean error( Message message,
                        Throwable throwable ) {
                    ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                    return true;
                }
            } ).getProcessInstanceActiveNodes( Long.parseLong( deploymentId ) );

        }
    }

    @OnClose
    public void onClose() {
        super.onClose();
    }

    protected Menus buildMenu() {
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
                                return getSpecificView().getOptionsButton();
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
                                return getSpecificView().getRefreshButton();
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
                                return getSpecificView().getCloseButton();
                            }
                        };
                    }
                } ).endMenu().build();
    }

    public abstract Menus buildSpecificMenu();

    protected abstract BaseProcessInstanceDetailsMultiView getSpecificView();

    public abstract String getTitle();

    public abstract UberView<BaseProcessInstanceDetailsMultiPresenter> getView();
}
