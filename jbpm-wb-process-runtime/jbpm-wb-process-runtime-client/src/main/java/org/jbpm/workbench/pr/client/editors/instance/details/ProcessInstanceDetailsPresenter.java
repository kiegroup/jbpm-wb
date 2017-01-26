/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.client.editors.instance.details;

import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.UserTaskSummary;
import org.jbpm.workbench.pr.client.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.events.ProcessInstanceStyleEvent;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.kie.api.runtime.process.ProcessInstance;

@Dependent
public class ProcessInstanceDetailsPresenter {

    private String currentDeploymentId;
    private String currentProcessInstanceId;
    private String currentProcessDefId;
    private String currentServerTemplateId;

    public interface ProcessInstanceDetailsView extends IsWidget {

//      TODO Review interface to not expose GWT components
        HTML getCurrentActivitiesListBox();

        HTML getActiveTasksListBox();

        HTML getProcessDefinitionIdText();

        HTML getStateText();

        HTML getProcessDeploymentText();

        HTML getProcessVersionText();

        HTML getCorrelationKeyText();

        HTML getParentProcessInstanceIdText();

    }

    @Inject
    private ProcessInstanceDetailsView view;

    @Inject
    private Event<ProcessInstanceStyleEvent> processInstanceStyleEvent;

    @Inject
    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    private Constants constants = Constants.INSTANCE;

    private ProcessInstanceSummary processSelected = null;

    public IsWidget getWidget() {
        return view;
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
        this.currentServerTemplateId = event.getServerTemplateId();

        refreshProcessInstanceDataRemote(currentDeploymentId, currentProcessInstanceId, currentProcessDefId, currentServerTemplateId);
    }

    public void refreshProcessInstanceDataRemote(final String deploymentId,
                                                 final String processId,
                                                 final String processDefId,
                                                 final String serverTemplateId) {
        processSelected = null;

        view.getProcessDefinitionIdText().setText("");
        view.getProcessVersionText().setText("");
        view.getProcessDeploymentText().setText("");
        view.getCorrelationKeyText().setText("");
        view.getParentProcessInstanceIdText().setText("");
        view.getActiveTasksListBox().setText("");
        view.getStateText().setText("");
        view.getCurrentActivitiesListBox().setText( "" );

        processRuntimeDataService.call(new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback( final ProcessInstanceSummary process ) {
                view.getProcessDefinitionIdText().setText( process.getProcessId() );
                view.getProcessVersionText().setText( process.getProcessVersion() );
                view.getProcessDeploymentText().setText( process.getDeploymentId() );
                view.getCorrelationKeyText().setText(process.getCorrelationKey());
                if(process.getParentId() > 0){
                    view.getParentProcessInstanceIdText().setText(process.getParentId().toString());
                }else{
                    view.getParentProcessInstanceIdText().setText(constants.No_Parent_Process_Instance());
                }

                String statusStr = constants.Unknown();
                switch ( process.getState() ) {
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

                if (process.getActiveTasks() != null && !process.getActiveTasks().isEmpty()) {
                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();

                    for ( UserTaskSummary uts : process.getActiveTasks() ) {
                        safeHtmlBuilder.appendEscapedLines( uts.getName() + " (" + uts.getStatus() +")  "+constants.Owner() +": " + uts.getOwner() +" \n" );
                    }
                    view.getActiveTasksListBox().setHTML( safeHtmlBuilder.toSafeHtml() );
                }
                view.getStateText().setText( statusStr );
                processSelected = process;
                changeStyleRow( Long.parseLong( processId ), processSelected.getProcessName(), processSelected.getProcessVersion(),
                        processSelected.getStartTime() );

            }
        } ).getProcessInstance(serverTemplateId, new ProcessInstanceKey(serverTemplateId, deploymentId, Long.parseLong(processId)));


        processRuntimeDataService.call(new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback( final List<NodeInstanceSummary> details ) {
                final SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                for ( NodeInstanceSummary nis : details ) {
                    safeHtmlBuilder.appendEscapedLines( nis.getTimestamp() + ": "
                            + nis.getId() + " - " + nis.getNodeName() + " (" + nis.getType() + ") \n" );
                }
                view.getCurrentActivitiesListBox().setHTML( safeHtmlBuilder.toSafeHtml() );
            }
        } ).getProcessInstanceActiveNodes( serverTemplateId, deploymentId, Long.parseLong( processId ) );
    }

}