/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.client.screens;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.common.collect.Maps;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.api.ProcessAdminService;
import org.jbpm.workbench.client.i18n.ProcessAdminConstants;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

import static java.util.Collections.emptyList;

@Dependent
@WorkbenchScreen(identifier = ProcessAdminSettingsPresenter.SCREEN_ID)
public class ProcessAdminSettingsPresenter {

    public static final String SCREEN_ID = "Process Admin Settings";

    private final Map<String, ProcessSummary> processeSummaryMap = Maps.newHashMap();

    private ProcessAdminConstants constants = ProcessAdminConstants.INSTANCE;

    @Inject
    private ProcessAdminSettingsView view;

    @Inject
    private Caller<ProcessAdminService> instancesAdminServices;

    @Inject
    private Caller<SpecManagementService> specManagementService;

    @Inject
    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    @PostConstruct
    public void init() {
        specManagementService.call(
                (ServerTemplateList st) -> {
                    if (st.getServerTemplates() == null) {
                        return;
                    }
                    final Set<String> stId = Arrays.stream(st.getServerTemplates()).filter(e -> e.getServerInstanceKeys() != null && !e.getServerInstanceKeys().isEmpty())
                            .map(s -> s.getId())
                            .collect(Collectors.toSet());
                    view.addServerTemplates(stId);
                }).listServerTemplates();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Instances_Admin();
    }

    @WorkbenchPartView
    public UberView<ProcessAdminSettingsPresenter> getView() {
        return view;
    }

    public void generateMockInstances(final String serverTemplateId,
                                      final String processId,
                                      final Integer amountOfTasks,
                                      final String correlationKey,
                                      final List<ProcessVariableSummary> variables) {
        final ProcessSummary summary = processeSummaryMap.get(processId);

        if (summary == null) {
            return;
        }

        final Map<String, Object> params = variables.stream().collect(Collectors.toMap(ProcessVariableSummary::getVariableName,
                                                                                       ProcessVariableSummary::getVariableValue));

        instancesAdminServices.call(
                p -> view.displayNotification(constants.ProcessInstancesSuccessfullyCreated()))
                .generateMockInstances(serverTemplateId,
                                       summary.getDeploymentId(),
                                       summary.getProcessDefId(),
                                       correlationKey,
                                       params,
                                       amountOfTasks);
    }

    public void onServerTemplateSelected(final String serverTemplateId) {
        view.clearProcessList();
        processRuntimeDataService.call(
                (List<ProcessSummary> ps) -> {
                    processeSummaryMap.clear();
                    final Set<String> pid = ps.stream().map(p -> {
                        processeSummaryMap.put(p.getProcessDefId(),
                                               p);
                        return p.getProcessDefId();
                    }).collect(Collectors.toSet());
                    view.addProcessList(pid);
                })
                .getProcesses(serverTemplateId,
                              0,
                              Integer.MAX_VALUE,
                              "",
                              true);
    }

    public void onProcessSelected(final String serverTemplateId,
                                  final String processId) {
        final ProcessSummary process = processeSummaryMap.get(processId);

        processRuntimeDataService.call((ProcessSummary summary) -> {
            if (summary == null || summary.getProcessVariables() == null) {
                view.addProcessVariables(emptyList());
            } else {
                final List<ProcessVariableSummary> variables = summary.getProcessVariables().keySet().stream().map(k -> new ProcessVariableSummary(k,
                                                                                                                                                   null)).collect(Collectors.toList());
                view.addProcessVariables(variables);
            }
        }).getProcess(serverTemplateId,
                      new ProcessDefinitionKey(serverTemplateId,
                                               process.getDeploymentId(),
                                               process.getProcessDefId()));
    }

    public interface ProcessAdminSettingsView extends UberView<ProcessAdminSettingsPresenter> {

        void displayNotification(String text);

        void addServerTemplates(Set<String> serverTemplateId);

        void addProcessVariables(List<ProcessVariableSummary> variables);

        void clearProcessList();

        void addProcessList(Set<String> processId);
    }
}