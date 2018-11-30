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
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceDiagramSummary;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Dependent
public class ProcessInstanceDiagramPresenter {

    private Event<NotificationEvent> notification;
    private Caller<ProcessRuntimeDataService> processService;
    private Constants constants = Constants.INSTANCE;
    private List<ProcessNodeSummary> processNodes;
    private List<NodeInstanceSummary> nodeInstances;
    private String containerId;
    private Long processInstanceId;
    private String serverTemplateId;

    @Inject
    private ProcessInstanceDiagramView view;

    @PostConstruct
    public void init() {
        view.setOnProcessNodeSelectedCallback(id -> onProcessNodeSelected(id));
        view.setOnProcessNodeTriggeredCallback(id -> onProcessNodeTrigger(id));
        view.setOnNodeInstanceCancelCallback(id -> onNodeInstanceCancel(id));
        view.setOnNodeInstanceReTriggerCallback(id -> onNodeInstanceReTrigger(id));
    }

    public void onProcessInstanceSelectionEvent(@Observes final ProcessInstanceSelectionEvent event) {
        view.showBusyIndicator(constants.Loading());
        processNodes = emptyList();
        view.setProcessNodes(processNodes);
        nodeInstances = emptyList();
        view.setNodeInstances(nodeInstances);
        containerId = event.getDeploymentId();
        processInstanceId = event.getProcessInstanceId();
        serverTemplateId = event.getServerTemplateId();

        loadProcessInstanceDetails();
    }

    protected void loadProcessInstanceDetails() {
        processService.call((ProcessInstanceDiagramSummary summary) -> {
            displayImage(summary.getSvgContent(),
                         containerId);
            processNodes = summary.getProcessNodes().stream().sorted(Comparator.comparing(ProcessNodeSummary::getLabel,
                                                                                          String.CASE_INSENSITIVE_ORDER)).collect(toList());
            view.setProcessNodes(processNodes);

            nodeInstances = summary.getNodeInstances().stream().sorted(Comparator.comparing(NodeInstanceSummary::getLabel,
                                                                                            String.CASE_INSENSITIVE_ORDER)).collect(toList());
            view.setNodeInstances(nodeInstances);
        }).getProcessInstanceDiagramSummary(serverTemplateId,
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

    public void onProcessNodeSelected(final Long nodeId) {
        view.setValue(nodeId == null ? new ProcessNodeSummary() : getProcessNodeSummary(nodeId));
    }

    protected ProcessNodeSummary getProcessNodeSummary(final Long nodeId) {
        return processNodes.stream().filter(node -> node.getId().equals(nodeId)).findFirst().get();
    }

    protected NodeInstanceSummary getNodeInstanceSummary(final Long nodeId) {
        return nodeInstances.stream().filter(node -> node.getId().equals(nodeId)).findFirst().get();
    }

    public void onProcessNodeTrigger(final Long nodeId) {
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

    public void onNodeInstanceReTrigger(final Long nodeId) {
        final NodeInstanceSummary node = getNodeInstanceSummary(nodeId);
        processService.call((Void) -> {
            notification.fire(new NotificationEvent(constants.NodeInstanceReTriggered(node.getLabel()),
                                                    NotificationEvent.NotificationType.SUCCESS));
            loadProcessInstanceDetails();
        }).reTriggerProcessInstanceNode(serverTemplateId,
                                        containerId,
                                        processInstanceId,
                                        node.getId());
    }

    public void onNodeInstanceCancel(final Long nodeId) {
        final NodeInstanceSummary node = getNodeInstanceSummary(nodeId);
        processService.call((Void) -> {
            notification.fire(new NotificationEvent(constants.NodeInstanceCancelled(node.getLabel()),
                                                    NotificationEvent.NotificationType.SUCCESS));
            loadProcessInstanceDetails();
        }).cancelProcessInstanceNode(serverTemplateId,
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
    public void setProcessService(final Caller<ProcessRuntimeDataService> processService) {
        this.processService = processService;
    }

    @Inject
    public void setNotification(final Event<NotificationEvent> notification) {
        this.notification = notification;
    }
}
