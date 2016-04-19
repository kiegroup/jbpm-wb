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
package org.jbpm.console.ng.pr.client.editors.instance.details;

import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ga.model.process.DummyProcessPath;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceKey;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.UserTaskSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceStyleEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

@Dependent
public class ProcessInstanceDetailsPresenter {

    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;
    private String currentDeploymentId;
    private String currentProcessInstanceId;
    private String currentProcessDefId;

    public interface ProcessInstanceDetailsView extends IsWidget {

        void displayNotification( String text );

        HTML getCurrentActivitiesListBox();

        HTML getActiveTasksListBox();

        HTML getProcessDefinitionIdText();

        HTML getStateText();

        void setProcessInstance( ProcessInstanceSummary processInstance );

        HTML getProcessDeploymentText();

        HTML getProcessVersionText();

        HTML getCorrelationKeyText();

        HTML getParentProcessInstanceIdText();

        void setProcessAssetPath( Path processAssetPath );

        void setCurrentActiveNodes( List<NodeInstanceSummary> activeNodes );

        void setCurrentCompletedNodes( List<NodeInstanceSummary> completedNodes );

        void setEncodedProcessSource( String encodedProcessSource );

        List<NodeInstanceSummary> getCompletedNodes();

        Path getProcessAssetPath();

        String getEncodedProcessSource();

        List<NodeInstanceSummary> getActiveNodes();
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ProcessInstanceDetailsView view;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Caller<ProcessInstanceService> processInstanceService;

    @Inject
    private Event<ProcessInstanceStyleEvent> processInstanceStyleEvent;

    @Inject
    private Event<ProcessInstancesUpdateEvent> processInstancesUpdatedEvent;

    @Inject
    private Caller<VFSService> fileServices;

    private Constants constants = GWT.create(Constants.class);

    private ProcessInstanceSummary processSelected = null;

    public IsWidget getWidget() {
        return view;
    }

    public void refreshProcessInstanceData( final String deploymentId,
                                            final String processId,
                                            final String processDefId ) {
        processSelected = null;

        view.getProcessDefinitionIdText().setText(processId);
        dataServices.call(
                new RemoteCallback<List<NodeInstanceSummary>>() {
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
                },
                new DefaultErrorCallback()
        ).getProcessInstanceActiveNodes(Long.parseLong(processId));

        dataServices.call(
                new RemoteCallback<ProcessSummary>() {
                    @Override
                    public void callback(ProcessSummary process) {
                        view.getProcessDefinitionIdText().setText(process.getProcessDefId());
                        view.getProcessVersionText().setText(process.getVersion());
                    }
                },
                new DefaultErrorCallback()
        ).getProcessDesc(deploymentId, processDefId);

        processInstanceService.call(
                new RemoteCallback<ProcessInstanceSummary>() {
                    @Override
                    public void callback(ProcessInstanceSummary process) {
                        view.getProcessDeploymentText().setText(process.getDeploymentId());
                        view.getCorrelationKeyText().setText(process.getCorrelationKey());
                        if (process.getParentId() > 0) {
                            view.getParentProcessInstanceIdText().setText(process.getParentId().toString());
                        } else {
                            view.getParentProcessInstanceIdText().setText(constants.No_Parent_Process_Instance());
                        }

                        view.setProcessInstance(process);

                        String statusStr = constants.Unknown();
                        switch (process.getState()) {
                            case ProcessInstance.STATE_ACTIVE:
                                statusStr = constants.Active();
                                break;
                            case ProcessInstance.STATE_ABORTED:
                                statusStr = constants.Aborted();
                                break;
                            case ProcessInstance.STATE_COMPLETED:
                                statusStr = constants.Completed();
                                break;
                            case ProcessInstance.STATE_PENDING:
                                statusStr = constants.Pending();
                                break;
                            case ProcessInstance.STATE_SUSPENDED:
                                statusStr = constants.Suspended();
                                break;
                            default:
                                break;
                        }
                        view.getActiveTasksListBox().setText("");
                        if (process.getActiveTasks() != null && !process.getActiveTasks().isEmpty()) {
                            SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();

                            for (UserTaskSummary uts : process.getActiveTasks()) {
                                safeHtmlBuilder.appendEscapedLines(uts.getName() + " (" + uts.getStatus() + ")  " + constants.Owner() + ": " + uts.getOwner() + " \n");
                            }
                            view.getActiveTasksListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                        }
                        view.getStateText().setText(statusStr);
                        processSelected = process;
                        changeStyleRow(Long.parseLong(processId), processSelected.getProcessName(), processSelected.getProcessVersion(),
                                processSelected.getStartTime());

                    }
                },
                new DefaultErrorCallback()
        ).getItem(new ProcessInstanceKey(Long.parseLong(processId)));

        dataServices.call(
                new RemoteCallback<List<NodeInstanceSummary>>() {
                    @Override
                    public void callback(List<NodeInstanceSummary> details) {
                        view.setCurrentCompletedNodes(details);
                    }
                },
                new DefaultErrorCallback()
        ).getProcessInstanceCompletedNodes(Long.parseLong(processId));

        dataServices.call(
                new RemoteCallback<ProcessSummary>() {
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
                },
                new DefaultErrorCallback()
        ).getProcessById(deploymentId, processDefId);
    }

    private void changeStyleRow( long processInstanceId,
                                 String processDefName,
                                 String processDefVersion,
                                 Date startTime ) {
        processInstanceStyleEvent.fire( new ProcessInstanceStyleEvent( processInstanceId, processDefName, processDefVersion, startTime ) );

    }

    public void onProcessInstanceSelectionEvent( @Observes ProcessInstanceSelectionEvent event ) {
        this.currentDeploymentId = event.getDeploymentId();
        this.currentProcessInstanceId = String.valueOf( event.getProcessInstanceId() );
        this.currentProcessDefId = event.getProcessDefId();

        refreshProcessInstanceData( currentDeploymentId, currentProcessInstanceId, currentProcessDefId );
    }

}
