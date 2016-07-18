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
package org.jbpm.console.ng.pr.admin.client.editors.settings;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.model.ProcessSummary;
import org.jbpm.console.ng.pr.admin.client.i18n.ProcessAdminConstants;
import org.jbpm.console.ng.pr.admin.service.ProcessAdminService;
import org.jbpm.console.ng.pr.service.ProcessRuntimeDataService;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

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
                (Collection<ServerTemplate> st) -> {
                    final Set<String> stId = FluentIterable.from(st)
                            .filter(e -> e.getServerInstanceKeys() != null && !e.getServerInstanceKeys().isEmpty())
                            .transform(s -> s.getId())
                            .toSet();
                    view.addServerTemplates(stId);
                }
                , new DefaultErrorCallback())
                .listServerTemplates();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Instances_Admin();
    }

    @WorkbenchPartView
    public UberView<ProcessAdminSettingsPresenter> getView() {
        return view;
    }

    public void generateMockInstances(final String serverTemplateId, final String processId, int amountOfTasks) {
        final ProcessSummary summary = processeSummaryMap.get(processId);

        if (summary == null) return;
        instancesAdminServices.call(
                p -> view.displayNotification(constants.ProcessInstancesSuccessfullyCreated()),
                new DefaultErrorCallback())
                .generateMockInstances(serverTemplateId, summary.getDeploymentId(), summary.getProcessDefId(), null, null, amountOfTasks);
    }

    public void onServerTemplateSelected(final String serverTemplateId) {
        view.clearProcessList();
        processRuntimeDataService.call(
                (List<ProcessSummary> ps) -> {
                    processeSummaryMap.clear();
                    final Set<String> pid = FluentIterable.from(ps).transform(p -> {
                        processeSummaryMap.put(p.getProcessDefId(), p);
                        return p.getProcessDefId();
                    }).toSet();
                    view.addProcessList(pid);
                },
                new DefaultErrorCallback())
                .getProcesses(serverTemplateId, 0, Integer.MAX_VALUE, "", true);
    }

    public interface ProcessAdminSettingsView extends UberView<ProcessAdminSettingsPresenter> {

        void displayNotification(String text);

        void addServerTemplates(Set<String> serverTemplateId);

        void clearProcessList();

        void addProcessList(Set<String> processId);

    }

}