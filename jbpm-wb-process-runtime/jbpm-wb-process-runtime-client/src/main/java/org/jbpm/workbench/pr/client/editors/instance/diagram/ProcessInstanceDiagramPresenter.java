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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.pr.client.editors.instance.ProcessInstanceSummaryAware;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceDiagramSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.jbpm.workbench.pr.model.TimerInstanceSummary;
import org.jbpm.workbench.pr.model.TimerSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Dependent
public class ProcessInstanceDiagramPresenter implements ProcessInstanceSummaryAware {

    private Constants constants = Constants.INSTANCE;

    private Caller<ProcessRuntimeDataService> processService;
    private List<ProcessNodeSummary> processNodes;
    private List<NodeInstanceSummary> nodeInstances;
    private List<TimerInstanceSummary> timerInstances;
    private List<TimerSummary> timers;
    private ProcessInstanceSummary processInstance;
    private boolean forLog;

    @Inject
    private TimerInstanceRescheduleView rescheduleView;

    @Inject
    private ProcessInstanceDiagramView view;

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceEvent;

    @Inject
    private Event<NotificationEvent> notification;

    @PostConstruct
    public void init() {
        view.setOnProcessNodeSelectedCallback(id -> onDiagramNodeSelected(id));
        view.setOnDiagramNodeSelectionCallback(id -> onDiagramNodeSelected(id));
    }

    @Override
    public void setProcessInstance(ProcessInstanceSummary processInstance) {
        view.showBusyIndicator(constants.Loading());

        this.processInstance = processInstance;

        processNodes = emptyList();
        view.setProcessNodes(processNodes);
        nodeInstances = emptyList();
        view.setNodeInstances(nodeInstances);
        timerInstances = emptyList();
        view.setTimerInstances(timerInstances);
        timers = emptyList();
        view.setValue(new ProcessNodeSummary());

        loadProcessInstanceDetails();
    }

    public void onProcessInstanceSelectionEvent(@Observes final ProcessInstanceSelectionEvent event) {
        forLog = event.isForLog();
    }

    protected void loadProcessInstanceDetails() {
        processService.call((ProcessInstanceDiagramSummary summary) -> {
            displayImage(summary.getSvgContent(),
                         processInstance.getDeploymentId());

            processNodes = summary.getProcessDefinition().getNodes().stream().sorted(comparing(ProcessNodeSummary::getName, String.CASE_INSENSITIVE_ORDER).thenComparingLong(ProcessNodeSummary::getId)).collect(toList());

            processNodes.stream().filter(this::isProcessNodeTypeTriggerAllowed).forEach(pn -> pn.addCallback(constants.Trigger(),
                                                                                                             () -> onProcessNodeTrigger(pn)));

            view.setProcessNodes(processNodes);

            nodeInstances = summary.getNodeInstances().stream().sorted(comparing(NodeInstanceSummary::getName, String.CASE_INSENSITIVE_ORDER).thenComparingLong(NodeInstanceSummary::getId)).collect(toList());

            nodeInstances.forEach(ni -> {
                ni.setDescription((ni.isCompleted() ? constants.Completed() : constants.Started()) + " " + DateUtils.getPrettyTime(ni.getTimestamp()));
                if (ni.isCompleted() == false) {
                    ni.addCallback(constants.Cancel(),
                                   () -> onNodeInstanceCancel(ni));
                    ni.addCallback(constants.ReTrigger(),
                                   () -> onNodeInstanceReTrigger(ni));
                }
            });

            final Map<String, Long> badges = nodeInstances.stream().collect(Collectors.groupingBy(NodeInstanceSummary::getNodeUniqueName, Collectors.counting()));
            view.setNodeBadges(badges);

            view.setNodeInstances(nodeInstances);

            timerInstances = summary.getTimerInstances().stream().sorted(comparing(TimerInstanceSummary::getName, String.CASE_INSENSITIVE_ORDER).thenComparingLong(TimerInstanceSummary::getId)).collect(toList());

            timerInstances.forEach(ti -> {
                ti.setDescription(constants.NextExecution() + " " + DateUtils.getPrettyTime(ti.getNextFireTime()));
                ti.addCallback(constants.Reschedule(),
                               () -> {
                                   rescheduleView.setOnReschedule(timer -> onTimerInstanceReschedule(timer));
                                   rescheduleView.setValue(ti);
                                   rescheduleView.show();
                               });
            });

            view.setTimerInstances(timerInstances);

            timers = summary.getProcessDefinition().getTimers().stream().collect(toList());

            if (forLog || processInstance.getState() != ProcessInstance.STATE_ACTIVE) {
                view.hideNodeActions();
            }

            view.onShow();

        }).getProcessInstanceDiagramSummary(processInstance.getProcessInstanceKey());
    }

    public void displayImage(final String svgContent, final String containerId) {
        if (svgContent == null || svgContent.isEmpty()) {
            view.displayMessage(constants.Process_Diagram_Not_FoundContainerShouldBeAvailable(containerId));
        } else {
            view.displayImage(svgContent);
        }
        view.hideBusyIndicator();
    }

    public void onDiagramNodeSelected(final String nodeId) {
        if (nodeId == null) {
            view.setValue(new ProcessNodeSummary());
            view.setNodeInstances(nodeInstances);
            view.setTimerInstances(timerInstances);
        } else {
            view.setValue(processNodes.stream().filter(node -> node.getUniqueId().equals(nodeId)).findFirst().orElseGet(() -> new ProcessNodeSummary()));
            view.setNodeInstances(nodeInstances.stream().filter(node -> node.getNodeUniqueName().equals(nodeId)).collect(toList()));
            view.setTimerInstances(getTimerInstanceForNode(nodeId));
        }
    }

    protected List<TimerInstanceSummary> getTimerInstanceForNode(final String nodeId) {
        final Optional<TimerSummary> summary = timers.stream().filter(timer -> timer.getUniqueId().equals(nodeId)).findFirst();
        if (summary.isPresent()) {
            return timerInstances.stream().filter(instance -> instance.getTimerId().equals(summary.get().getId())).collect(toList());
        } else {
            return emptyList();
        }
    }

    protected Boolean isProcessNodeTypeTriggerAllowed(final ProcessNodeSummary nodeSummary) {
        if (nodeSummary == null || nodeSummary.getType() == null) {
            return false;
        }

        if ("StartNode".equals(nodeSummary.getType()) || "Join".equals(nodeSummary.getType())) {
            return false;
        }

        return true;
    }

    protected ProcessNodeSummary getProcessNodeSummary(final Long nodeId) {
        return processNodes.stream().filter(node -> node.getId().equals(nodeId)).findFirst().get();
    }

    public void onProcessNodeTrigger(final ProcessNodeSummary node) {
        processService.call((Void) -> {
            notification.fire(new NotificationEvent(constants.NodeTriggered(node.getLabel()),
                                                    NotificationEvent.NotificationType.SUCCESS));
            view.setValue(new ProcessNodeSummary());
            refreshDetails();
        }).triggerProcessInstanceNode(processInstance.getProcessInstanceKey(),
                                      node.getId());
    }

    protected void refreshDetails() {
        processInstanceEvent.fire(new ProcessInstanceSelectionEvent(processInstance.getProcessInstanceKey(),
                                                                    forLog));
    }

    public void onNodeInstanceReTrigger(final NodeInstanceSummary node) {
        processService.call((Void) -> {
            notification.fire(new NotificationEvent(constants.NodeInstanceReTriggered(node.getLabel()),
                                                    NotificationEvent.NotificationType.SUCCESS));
            refreshDetails();
        }).reTriggerProcessInstanceNode(processInstance.getProcessInstanceKey(),
                                        node.getId());
    }

    public void onNodeInstanceCancel(final NodeInstanceSummary node) {
        processService.call((Void) -> {
            notification.fire(new NotificationEvent(constants.NodeInstanceCancelled(node.getLabel()),
                                                    NotificationEvent.NotificationType.SUCCESS));
            refreshDetails();
        }).cancelProcessInstanceNode(processInstance.getProcessInstanceKey(),
                                     node.getId());
    }

    public void onTimerInstanceReschedule(final TimerInstanceSummary summary) {
        processService.call((Void) -> {
            notification.fire(new NotificationEvent(constants.TimerInstanceRescheduled(summary.getLabel()),
                                                    NotificationEvent.NotificationType.SUCCESS));
            refreshDetails();
        }).rescheduleTimerInstance(processInstance.getProcessInstanceKey(),
                                   summary);
    }

    public void onShow(){
        view.onShow();
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
}
