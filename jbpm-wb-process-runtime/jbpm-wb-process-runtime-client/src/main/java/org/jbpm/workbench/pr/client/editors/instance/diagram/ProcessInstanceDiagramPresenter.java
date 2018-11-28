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

package org.jbpm.workbench.pr.client.editors.instance.diagram;

import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.jbpm.workbench.pr.service.ProcessImageService;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Dependent
public class ProcessInstanceDiagramPresenter {

    private Event<NotificationEvent> notification;
    private Caller<ProcessImageService> processImageService;
    private Caller<ProcessRuntimeDataService> processService;
    private Constants constants = Constants.INSTANCE;
    private List<ProcessNodeSummary> processNodes;
    private String containerId;
    private Long processInstanceId;
    private String serverTemplateId;

    @Inject
    private ProcessInstanceDiagramView view;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void onProcessInstanceSelectionEvent(@Observes final ProcessInstanceSelectionEvent event) {
        view.showBusyIndicator(constants.Loading());
        processNodes = emptyList();
        view.setProcessNodes(processNodes);
        containerId = event.getDeploymentId();
        processInstanceId = event.getProcessInstanceId();
        serverTemplateId = event.getServerTemplateId();

        loadProcessInstanceDetails();
    }

    protected void loadProcessInstanceDetails() {
        processImageService.call((String svgContent) -> displayImage(svgContent,
                                                                     containerId)).getProcessInstanceDiagram(serverTemplateId,
                                                                                                             containerId,
                                                                                                             processInstanceId);
        processService.call((List<ProcessNodeSummary> nodes) -> {
            processNodes = nodes.stream().sorted(Comparator.comparing(ProcessNodeSummary::getLabel,
                                                                      String.CASE_INSENSITIVE_ORDER)).collect(toList());
            view.setProcessNodes(processNodes);
        }).getProcessInstanceNodes(serverTemplateId,
                                   containerId,
                                   processInstanceId);
    }

    public void displayImage(final String svgContent,
                             final String containerId) {
        if (svgContent == null || svgContent.isEmpty()) {
            view.displayMessage(constants.Process_Diagram_Not_FoundContainerShouldBeAvailable(containerId));
        } else {
            view.displayImage(svgContent);
        }
        view.hideBusyIndicator();
    }

    public void onProcessNodeSelected(final String nodeId) {
        view.setValue(nodeId == null || nodeId.trim().isEmpty() ? new ProcessNodeSummary() : getProcessNodeSummary(nodeId));
    }

    protected ProcessNodeSummary getProcessNodeSummary(final String nodeId) {
        return processNodes.stream().filter(node -> node.getId().toString().equals(nodeId)).findFirst().get();
    }

    public void onProcessNodeTrigger(final String nodeId) {
        final ProcessNodeSummary node = getProcessNodeSummary(nodeId);
        processService.call((Void) -> {
            notification.fire(new NotificationEvent(constants.NodeTriggered(node.getLabel()),
                                                    NotificationEvent.NotificationType.SUCCESS));
            view.setValue(new ProcessNodeSummary());
            loadProcessInstanceDetails();
        }).triggerProcessInstanceNode(serverTemplateId,
                                      containerId,
                                      processInstanceId,
                                      node.getId());
    }

    @WorkbenchPartTitle
    public String getName() {
        return constants.Diagram();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    @Inject
    public void setProcessImageService(final Caller<ProcessImageService> processImageService) {
        this.processImageService = processImageService;
    }

    @Inject
    public void setProcessService(final Caller<ProcessRuntimeDataService> processService) {
        this.processService = processService;
    }

    @Inject
    public void setNotification(final Event<NotificationEvent> notification) {
        this.notification = notification;
    }
}
