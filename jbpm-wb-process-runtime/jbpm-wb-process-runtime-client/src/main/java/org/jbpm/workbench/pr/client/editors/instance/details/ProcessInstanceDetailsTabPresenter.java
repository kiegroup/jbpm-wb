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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.UserTaskSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.kie.api.runtime.process.ProcessInstance;

@Dependent
public class ProcessInstanceDetailsTabPresenter {

    private String currentDeploymentId;

    private String currentProcessInstanceId;

    private String currentServerTemplateId;

    private ProcessInstanceDetailsTabView view;

    private Constants constants = Constants.INSTANCE;

    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    @Inject
    public void setView(final ProcessInstanceDetailsTabView view) {
        this.view = view;
    }

    @Inject
    public void setProcessRuntimeDataService(final Caller<ProcessRuntimeDataService> processRuntimeDataService) {
        this.processRuntimeDataService = processRuntimeDataService;
    }

    public IsWidget getWidget() {
        return view;
    }

    public void onProcessInstanceSelectionEvent(@Observes ProcessInstanceSelectionEvent event) {
        this.currentDeploymentId = event.getDeploymentId();
        this.currentProcessInstanceId = String.valueOf(event.getProcessInstanceId());
        this.currentServerTemplateId = event.getServerTemplateId();

        refreshProcessInstanceDataRemote(currentDeploymentId,
                                         currentProcessInstanceId,
                                         currentServerTemplateId);
    }

    public void refreshProcessInstanceDataRemote(final String deploymentId,
                                                 final String processId,
                                                 final String serverTemplateId) {
        view.setProcessDefinitionIdText("");
        view.setProcessVersionText("");
        view.setProcessDeploymentText("");
        view.setCorrelationKeyText("");
        view.setParentProcessInstanceIdText("");
        view.setActiveTasksListBox("");
        view.setStateText("");
        view.setCurrentActivitiesListBox("");

        processRuntimeDataService.call(
                (final ProcessInstanceSummary process) -> {
                    view.setProcessDefinitionIdText(process.getProcessId());
                    view.setProcessVersionText(process.getProcessVersion());
                    view.setProcessDeploymentText(process.getDeploymentId());
                    view.setCorrelationKeyText(process.getCorrelationKey());
                    if (process.getParentId() > 0) {
                        view.setParentProcessInstanceIdText(process.getParentId().toString());
                    } else {
                        view.setParentProcessInstanceIdText(constants.No_Parent_Process_Instance());
                    }

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
                    view.setStateText(statusStr);

                    String slaComplianceStr = mapSlaCompliance(process);
                    view.setSlaComplianceText(slaComplianceStr);

                    if (process.getActiveTasks() != null && !process.getActiveTasks().isEmpty()) {
                        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();

                        for (UserTaskSummary uts : process.getActiveTasks()) {
                            safeHtmlBuilder.appendEscapedLines(uts.getName() + " (" + uts.getStatus() + ")  " + constants.Owner() + ": " + uts.getOwner() + " \n");
                        }
                        view.setActiveTasksListBox(safeHtmlBuilder.toSafeHtml().asString());
                    }
                }).getProcessInstance(new ProcessInstanceKey(serverTemplateId,
                                                             deploymentId,
                                                             Long.parseLong(processId)));

        processRuntimeDataService.call(
                (final List<NodeInstanceSummary> details) -> {
                    final SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                    for (NodeInstanceSummary nis : details) {
                        safeHtmlBuilder.appendEscapedLines(nis.getTimestamp() + ": "
                                                                   + nis.getId() + " - " + nis.getName() + " (" + nis.getType() + ") \n");
                    }
                    view.setCurrentActivitiesListBox(safeHtmlBuilder.toSafeHtml().asString());
                }
        ).getProcessInstanceActiveNodes(new ProcessInstanceKey(serverTemplateId,
                                                               deploymentId,
                                                               Long.parseLong(processId)));
    }

    protected String mapSlaCompliance(ProcessInstanceSummary process) {
        String slaComplianceStr = constants.Unknown();
        switch (process.getSlaCompliance()) {
            case ProcessInstance.SLA_NA:
                slaComplianceStr = constants.SlaNA();
                break;
            case ProcessInstance.SLA_PENDING:
                slaComplianceStr = constants.SlaPending();
                break;
            case ProcessInstance.SLA_MET:
                slaComplianceStr = constants.SlaMet();
                break;
            case ProcessInstance.SLA_ABORTED:
                slaComplianceStr = constants.SlaAborted();
                break;
            case ProcessInstance.SLA_VIOLATED:
                slaComplianceStr = constants.SlaViolated();
                break;
            default:
                break;
        }
        return slaComplianceStr;
    }

    public interface ProcessInstanceDetailsTabView extends IsWidget {

        void setCurrentActivitiesListBox(String value);

        void setActiveTasksListBox(String value);

        void setProcessDefinitionIdText(String value);

        void setStateText(String value);

        void setProcessDeploymentText(String value);

        void setProcessVersionText(String value);

        void setCorrelationKeyText(String value);

        void setParentProcessInstanceIdText(String value);

        void setSlaComplianceText(String value);
    }
}