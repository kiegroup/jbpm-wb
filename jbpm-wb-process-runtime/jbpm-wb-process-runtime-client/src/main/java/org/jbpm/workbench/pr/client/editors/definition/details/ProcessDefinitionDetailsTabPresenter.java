/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.definition.details;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.model.TaskDefSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;

@Dependent
public class ProcessDefinitionDetailsTabPresenter {

    private Constants constants = Constants.INSTANCE;

    private String currentProcessDefId = "";

    private String currentDeploymentId = "";

    private String currentServerTemplateId = "";

    @Inject
    private AdvancedProcessDefDetailsView view;

    @Inject
    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    public void onProcessDefSelectionEvent(@Observes final ProcessDefSelectionEvent event) {
        this.currentProcessDefId = event.getProcessId();
        this.currentDeploymentId = event.getDeploymentId();
        this.currentServerTemplateId = event.getServerTemplateId();
        refreshView(currentProcessDefId,
                    currentDeploymentId);
        refreshProcessDef(currentServerTemplateId,
                          currentDeploymentId,
                          currentProcessDefId);
    }

    public IsWidget getWidget() {
        return view;
    }

    protected void refreshView(String processId,
                               String deploymentId) {
        view.setProcessIdText(processId);
        view.setDeploymentIdText(deploymentId);
    }

    private void refreshServiceTasks(Map<String, String> services) {

        view.setProcessServicesListBox("");
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        if (services.keySet().isEmpty()) {
            safeHtmlBuilder.appendEscaped(constants.NoServicesRequiredForThisProcess());
            view.setProcessServicesListBox(safeHtmlBuilder.toSafeHtml().asString());
        } else {
            for (String key : services.keySet()) {
                safeHtmlBuilder.appendEscapedLines(key + " - "
                                                           + services.get(key) + "\n");
            }
            view.setProcessServicesListBox(safeHtmlBuilder.toSafeHtml().asString());
        }
    }

    private void refreshProcessItems(ProcessSummary process) {
        if (process != null) {
            view.setProcessNameText(process.getName());
        } else {
            // set to empty to ensure it's clear state
            view.setProcessNameText("");
        }
    }

    private void refreshReusableSubProcesses(Collection<String> subprocesses) {
        view.setSubProcessListBox("");
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        if (subprocesses.isEmpty()) {
            safeHtmlBuilder.appendEscapedLines(constants.NoSubprocessesRequiredByThisProcess());
            view.setSubProcessListBox(safeHtmlBuilder.toSafeHtml().asString());
        } else {
            for (String key : subprocesses) {
                safeHtmlBuilder.appendEscapedLines(key + "\n");
            }
            view.setSubProcessListBox(safeHtmlBuilder.toSafeHtml().asString());
        }
    }

    private void refreshRequiredInputData(Map<String, String> inputs) {
        view.setProcessDataListBox("");
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        if (inputs.keySet().isEmpty()) {
            safeHtmlBuilder.appendEscapedLines(constants.NoProcessVariablesDefinedForThisProcess());
            view.setProcessDataListBox(safeHtmlBuilder.toSafeHtml().asString());
        } else {
            for (String key : inputs.keySet()) {
                safeHtmlBuilder.appendEscapedLines(key + " - "
                                                           + inputs.get(key) + "\n");
            }
            view.setProcessDataListBox(safeHtmlBuilder.toSafeHtml().asString());
        }
    }

    private void refreshAssociatedEntities(Map<String, String[]> entities) {
        view.setUsersGroupsListBox("");
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        if (entities.keySet().isEmpty()) {
            safeHtmlBuilder
                    .appendEscapedLines(constants.NoUserOrGroupUsedInThisProcess());
            view.setUsersGroupsListBox(safeHtmlBuilder.toSafeHtml().asString());
        } else {
            for (String key : entities.keySet()) {
                StringBuffer names = new StringBuffer();
                String[] entityNames = entities.get(key);
                if (entityNames != null) {
                    for (String entity : entityNames) {
                        names.append("'" + entity + "' ");
                    }
                }
                safeHtmlBuilder.appendEscapedLines(names
                                                           + " - " + key + "\n");
            }
            view.setUsersGroupsListBox(safeHtmlBuilder.toSafeHtml().asString());
        }
    }

    private void refreshTaskDef(final String serverTemplateId,
                                final String deploymentId,
                                final String processId) {
        view.setNumberOfHumanTasksText("");
        view.setHumanTasksListBox("");

        processRuntimeDataService.call(new RemoteCallback<List<TaskDefSummary>>() {

            @Override
            public void callback(final List<TaskDefSummary> userTaskSummaries) {
                view.setNumberOfHumanTasksText(String.valueOf(userTaskSummaries.size()));

                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                if (userTaskSummaries.isEmpty()) {
                    safeHtmlBuilder.appendEscapedLines(constants.NoUserTasksDefinedInThisProcess());
                    view.setHumanTasksListBox(safeHtmlBuilder.toSafeHtml().asString());
                } else {
                    for (TaskDefSummary t : userTaskSummaries) {
                        safeHtmlBuilder.appendEscapedLines(t.getName() + "\n");
                    }
                    view.setHumanTasksListBox(safeHtmlBuilder.toSafeHtml().asString());
                }
            }
        }).getProcessUserTasks(serverTemplateId,
                               deploymentId,
                               processId);
    }

    protected void refreshProcessDef(final String serverTemplateId,
                                     final String deploymentId,
                                     final String processId) {

        processRuntimeDataService.call(new RemoteCallback<ProcessSummary>() {

            @Override
            public void callback(ProcessSummary process) {
                if (process != null) {

                    refreshTaskDef(serverTemplateId,
                                   deploymentId,
                                   processId);

                    refreshAssociatedEntities(process.getAssociatedEntities());

                    refreshRequiredInputData(process.getProcessVariables());

                    refreshReusableSubProcesses(process.getReusableSubProcesses());

                    refreshProcessItems(process);

                    refreshServiceTasks(process.getServiceTasks());
                }
            }
        }).getProcess(new ProcessDefinitionKey(serverTemplateId,
                                               deploymentId,
                                               processId));
    }

    public interface AdvancedProcessDefDetailsView extends IsWidget {

        void setNumberOfHumanTasksText(String text);

        void setHumanTasksListBox(String text);

        void setUsersGroupsListBox(String text);

        void setProcessDataListBox(String text);

        void setProcessServicesListBox(String text);

        void setSubProcessListBox(String text);

        void displayNotification(String text);

        void setProcessNameText(String text);

        void setProcessIdText(String text);

        void setDeploymentIdText(String text);
    }
}
