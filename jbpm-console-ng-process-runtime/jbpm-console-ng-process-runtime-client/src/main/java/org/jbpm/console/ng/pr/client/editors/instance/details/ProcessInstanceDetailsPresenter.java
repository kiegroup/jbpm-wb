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

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

import com.google.gwt.view.client.ProvidesKey;
import java.util.ArrayList;
import java.util.Date;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.DummyProcessPath;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceStyleEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.DefaultPosition;

import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.split.WorkbenchSplitLayoutPanel;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Process Instance Details")
public class ProcessInstanceDetailsPresenter {

    private Constants constants = GWT.create(Constants.class);

    private PlaceRequest place;

    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;

    public interface ProcessInstanceDetailsView extends UberView<ProcessInstanceDetailsPresenter> {

        void displayNotification(String text);

        HTML getCurrentActivitiesListBox();

        HTML getLogTextArea();

        HTML getProcessInstanceIdText();

        HTML getProcessDefinitionIdText();

        HTML getProcessNameText();

        HTML getStateText();

        void setProcessInstance(ProcessInstanceSummary processInstance);

        HTML getProcessDeploymentText();

        HTML getProcessVersionText();

        void setProcessAssetPath(Path processAssetPath);

        void setCurrentActiveNodes(List<NodeInstanceSummary> activeNodes);

        void setCurrentCompletedNodes(List<NodeInstanceSummary> completedNodes);

        void setEncodedProcessSource(String encodedProcessSource);

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
    private Event<ProcessInstanceStyleEvent> processInstanceStyleEvent;
    
    @Inject
    private Event<ProcessInstancesUpdateEvent> processInstancesUpdatedEvent;

    @Inject
    private Caller<VFSService> fileServices;

    private String processInstanceId = "";

    private String processDefId = "";

    private ProcessInstanceSummary processSelected = null;

    public ProcessInstanceDetailsPresenter() {
        makeMenuBar();
    }

    public static final ProvidesKey<VariableSummary> KEY_PROVIDER = new ProvidesKey<VariableSummary>() {
        @Override
        public Object getKey(VariableSummary item) {
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

    public void refreshProcessInstanceData(final String deploymentId, final String processId,
            final String processDefId) {
        processSelected = null;

        dataServices.call(new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback(List<NodeInstanceSummary> details) {
                view.getLogTextArea().setText("");
                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                for (NodeInstanceSummary nis : details) {
                    if (!nis.getNodeName().equals("")) {
                        safeHtmlBuilder.appendEscapedLines(nis.getTimestamp() + ": " + nis.getId() + " - " + nis.getNodeName() + " (" + nis.getType()
                                + ") \n");

                    } else {
                        safeHtmlBuilder.appendEscapedLines(nis.getTimestamp() + ": " + nis.getId() + " - " + nis.getType() + "\n");
                    }
                }
                view.getLogTextArea().setHTML(safeHtmlBuilder.toSafeHtml());
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getProcessInstanceHistory(Long.parseLong(processId));

        view.getProcessDefinitionIdText().setText(processId);
        dataServices.call(new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback(List<NodeInstanceSummary> details) {
                view.setCurrentActiveNodes(details);
                view.getCurrentActivitiesListBox().setText("");
                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                for (NodeInstanceSummary nis : details) {
                    safeHtmlBuilder.appendEscapedLines(nis.getTimestamp() + ": "
                            + nis.getId() + " - " + nis.getNodeName() + " (" + nis.getType() + ") \n");
                }
                view.getCurrentActivitiesListBox().setHTML(safeHtmlBuilder.toSafeHtml());
            }
        }, new ErrorCallback<Message>() {
          @Override
          public boolean error( Message message, Throwable throwable ) {
              ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
              return true;
          }
      }).getProcessInstanceActiveNodes(Long.parseLong(processId));

        dataServices.call(new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback(ProcessSummary process) {
                view.getProcessDefinitionIdText().setText(process.getProcessDefId());
                view.getProcessNameText().setText(process.getName());
                view.getProcessVersionText().setText(process.getVersion());
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getProcessDesc(processDefId);

        dataServices.call(new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback(ProcessInstanceSummary process) {
                view.getProcessDeploymentText().setText(process.getDeploymentId());
                view.setProcessInstance(process);

                String statusStr = "Unknown";
                switch (process.getState()) {
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

                view.getStateText().setText(statusStr);
                processSelected = process;
                changeStyleRow(Long.parseLong(processId), processSelected.getProcessName(), processSelected.getProcessVersion(),
                        processSelected.getStartTime());

            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getProcessInstanceById(Long.parseLong(processId));

        dataServices.call(new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback(List<NodeInstanceSummary> details) {
                view.setCurrentCompletedNodes(details);
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getProcessInstanceCompletedNodes(Long.parseLong(processId));

        dataServices.call(new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback(final ProcessSummary process) {
                if (process != null) {
                    view.setEncodedProcessSource(process.getEncodedProcessSource());
                    if (process.getOriginalPath() != null) {
                        fileServices.call(new RemoteCallback<Path>() {
                            @Override
                            public void callback(Path processPath) {
                                view.setProcessAssetPath(processPath);
                                if (processSelected != null) {
                                    changeStyleRow(processSelected.getProcessInstanceId(), processSelected.getProcessName(), processSelected.getProcessVersion(),
                                            processSelected.getStartTime());
                                }
                            }
                        }).get(process.getOriginalPath());
                    } else {
                        view.setProcessAssetPath(new DummyProcessPath(process.getProcessDefId()));
                    }
                    if (processSelected != null) {
                        changeStyleRow(processSelected.getProcessInstanceId(), processSelected.getProcessName(), processSelected.getProcessVersion(),
                                processSelected.getStartTime());
                    }
                } else {
                    // set to null to ensure it's clear state
                    view.setEncodedProcessSource(null);
                    view.setProcessAssetPath(null);
                }
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getProcessById(deploymentId, processDefId);
    }

    @DefaultPosition
    public Position getPosition() {
        return Position.EAST;
    }

    private void changeStyleRow(long processInstanceId, String processDefName, String processDefVersion, Date startTime) {
        processInstanceStyleEvent.fire(new ProcessInstanceStyleEvent(processInstanceId, processDefName, processDefVersion, startTime));

    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
        WorkbenchSplitLayoutPanel splitPanel = (WorkbenchSplitLayoutPanel) view.asWidget().getParent().getParent().getParent().getParent()
                .getParent().getParent().getParent().getParent().getParent().getParent().getParent();
        splitPanel.setWidgetMinSize(splitPanel.getWidget(0), 500);
    }

    public void onProcessInstanceSelectionEvent(@Observes ProcessInstanceSelectionEvent event) {
        view.getProcessInstanceIdText().setText(String.valueOf(event.getProcessInstanceId()));

        view.getProcessNameText().setText(event.getProcessDefId());

        refreshProcessInstanceData(event.getDeploymentId(), String.valueOf(event.getProcessInstanceId()), event.getProcessDefId());

    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu(constants.Actions())
                .withItems(getActions())
                .endMenu()
                .newTopLevelMenu(constants.Views())
                .withItems(getViews())
                .endMenu()
                .newTopLevelMenu(constants.Refresh())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        refreshProcessInstanceData(view.getProcessDeploymentText().getText(),
                                view.getProcessInstanceIdText().getText(),
                                view.getProcessDefinitionIdText().getText());
                        view.displayNotification(constants.Process_Instances_Details_Refreshed());
                    }
                })
                .endMenu()
                .build();

    }

    private List<? extends MenuItem> getActions() {
        List<MenuItem> actions = new ArrayList<MenuItem>(2);
        actions.add(MenuFactory.newSimpleItem(constants.Signal()).respondsWith(new Command() {
            @Override
            public void execute() {

                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Signal Process Popup");
                placeRequestImpl.addParameter("processInstanceId", view.getProcessInstanceIdText().getText());
                placeManager.goTo(placeRequestImpl);
            }
        }).endMenu().build().getItems().get(0));

        actions.add(MenuFactory.newSimpleItem(constants.Abort()).respondsWith(new Command() {
            @Override
            public void execute() {
                if (Window.confirm("Are you sure that you want to abort the process instance?")) {
                    final long processInstanceId = Long.parseLong(view.getProcessInstanceIdText().getText());
                    kieSessionServices.call(new RemoteCallback<Void>() {
                        @Override
                        public void callback(Void v) {
                            refreshProcessInstanceData(view.getProcessDeploymentText().getText(),
                                    view.getProcessInstanceIdText().getText(),
                                    view.getProcessDefinitionIdText().getText());
                            processInstancesUpdatedEvent.fire(new ProcessInstancesUpdateEvent(processInstanceId));
                            view.displayNotification(constants.Aborting_Process_Instance() + "(id=" + processInstanceId + ")");

                        }
                    }, new ErrorCallback<Message>() {
                        @Override
                        public boolean error( Message message, Throwable throwable ) {
                            ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                            return true;
                        }
                    }).abortProcessInstance(processInstanceId);
                }
            }
        }).endMenu().build().getItems().get(0));

        return actions;
    }

    private List<? extends MenuItem> getViews() {
        List<MenuItem> views = new ArrayList<MenuItem>(2);
        views.add(MenuFactory.newSimpleItem(constants.Process_Model())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        if (view.getProcessAssetPath() == null) {
                            view.displayNotification("Process definition not found, most likely it's not deployed");
                            return;
                        }
                        StringBuffer nodeParam = new StringBuffer();
                        for (NodeInstanceSummary activeNode : view.getActiveNodes()) {
                            nodeParam.append(activeNode.getNodeUniqueName() + ",");
                        }
                        if (nodeParam.length() > 0) {
                            nodeParam.deleteCharAt(nodeParam.length() - 1);
                        }

                        StringBuffer completedNodeParam = new StringBuffer();
                        for (NodeInstanceSummary completedNode : view.getCompletedNodes()) {
                            if (completedNode.isCompleted()) {
                                // insert outgoing sequence flow and node as this is for on entry event
                                completedNodeParam.append(completedNode.getNodeUniqueName() + ",");
                                completedNodeParam.append(completedNode.getConnection() + ",");
                            } else if (completedNode.getConnection() != null) {
                                // insert only incoming sequence flow as node id was already inserted
                                completedNodeParam.append(completedNode.getConnection() + ",");
                            }

                        }
                        completedNodeParam.deleteCharAt(completedNodeParam.length() - 1);

                        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Designer");
                        placeRequestImpl.addParameter("activeNodes", nodeParam.toString());
                        placeRequestImpl.addParameter("completedNodes", completedNodeParam.toString());
                        placeRequestImpl.addParameter("readOnly", "true");
                        placeRequestImpl.addParameter("processId", view.getProcessDefinitionIdText().getText());
                        placeRequestImpl.addParameter("deploymentId", view.getProcessDeploymentText().getText());
                        placeManager.goTo(view.getProcessAssetPath(), placeRequestImpl);
                    }
                }).endMenu().build().getItems().get(0));

        views.add(MenuFactory.newSimpleItem(constants.Process_Variables()).respondsWith(new Command() {
            @Override
            public void execute() {

                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Process Variables List");
                placeRequestImpl.addParameter("processInstanceId", view.getProcessInstanceIdText().getText());
                placeRequestImpl.addParameter("processDefId", view.getProcessDefinitionIdText().getText());
                placeManager.goTo(placeRequestImpl);
            }
        }).endMenu().build().getItems().get(0));

        return views;
    }

}
