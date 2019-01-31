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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceDiagramSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.model.TimerInstanceSummary;
import org.jbpm.workbench.pr.model.TimerSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.process.ProcessInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static com.google.common.collect.Maps.immutableEntry;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceDiagramPresenterTest {

    @Mock
    ProcessRuntimeDataService processService;

    @Mock
    EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    EventSourceMock<ProcessInstanceSelectionEvent> processInstanceEvent;

    @Mock
    ProcessInstanceDiagramView view;

    @InjectMocks
    ProcessInstanceDiagramPresenter presenter;

    @Before
    public void setup() {
        presenter.setProcessService(new CallerMock<>(processService));
    }

    @Test
    public void testEmptyProcessInstanceDiagram() {
        when(processService.getProcessInstanceDiagramSummary(any())).thenReturn(
                ProcessInstanceDiagramSummary.builder().withProcessDefinition(ProcessSummary.builder().nodes(emptyList()).timers(emptyList()).build()).withNodeInstances(emptyList()).withTimerInstances(emptyList()).build(),
                ProcessInstanceDiagramSummary.builder().withProcessDefinition(ProcessSummary.builder().nodes(emptyList()).timers(emptyList()).build()).withNodeInstances(emptyList()).withTimerInstances(emptyList()).withSvgContent("").build()
        );

        presenter.setProcessInstance(ProcessInstanceSummary.builder().withProcessInstanceId(1l).withState(ProcessInstance.STATE_ACTIVE).build());
        presenter.setProcessInstance(ProcessInstanceSummary.builder().withProcessInstanceId(1l).withState(ProcessInstance.STATE_ACTIVE).build());

        verify(view, times(2)).displayMessage(Constants.INSTANCE.Process_Diagram_Not_FoundContainerShouldBeAvailable(anyString()));
    }

    @Test
    public void testProcessInstanceDiagram() {
        final String svgContent = "<svg></svg>";
        when(processService.getProcessInstanceDiagramSummary(any())).thenReturn(
                ProcessInstanceDiagramSummary.builder().withProcessDefinition(ProcessSummary.builder().nodes(emptyList()).timers(emptyList()).build()).withNodeInstances(emptyList()).withTimerInstances(emptyList()).withSvgContent(svgContent).build(),
                ProcessInstanceDiagramSummary.builder().withProcessDefinition(ProcessSummary.builder().nodes(emptyList()).timers(emptyList()).build()).withNodeInstances(emptyList()).withTimerInstances(emptyList()).build()
        );

        presenter.setProcessInstance(ProcessInstanceSummary.builder().withProcessInstanceId(1l).withState(ProcessInstance.STATE_ACTIVE).build());

        verify(view, never()).displayMessage(Constants.INSTANCE.Process_Diagram_Not_FoundContainerShouldBeAvailable(anyString()));
        verify(view).displayImage(svgContent);
    }

    @Test
    public void testOnProcessInstanceSelectionEvent() {
        ProcessInstanceSummary processInstance = ProcessInstanceSummary.builder().withServerTemplateId("serverTemplateId").withDeploymentId("containerId").withProcessInstanceId(1l).withState(ProcessInstance.STATE_ACTIVE).build();

        String svgContent = "<svg></svg>";

        List<ProcessNodeSummary> nodes = Arrays.asList(new ProcessNodeSummary(1l,
                                                                              " ",
                                                                              "StartNode",
                                                                              "_1"),
                                                       new ProcessNodeSummary(2l,
                                                                              "task-name",
                                                                              "HumanTask",
                                                                              "_2"),
                                                       new ProcessNodeSummary(3l,
                                                                              " ",
                                                                              "Split",
                                                                              "_3"));

        List<NodeInstanceSummary> nodeInstances = Arrays.asList(
                NodeInstanceSummary.builder().withId(1l).withName("name-1").withNodeUniqueName("_1").withType("HumanTask").withCompleted(false).build(),
                NodeInstanceSummary.builder().withId(2l).withName(" ").withNodeUniqueName("_2").withType("Split").withCompleted(false).build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withNodeUniqueName("_3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(4l).withName(" ").withNodeUniqueName("_4").withType("End").withCompleted(true).build()
        );

        List<TimerInstanceSummary> timerInstances = Arrays.asList(
                TimerInstanceSummary.builder().withId(2l).withName("t2").build(),
                TimerInstanceSummary.builder().withId(1l).withName("t1").build());

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setSvgContent(svgContent);
        summary.setProcessDefinition(ProcessSummary.builder().nodes(nodes).timers(emptyList()).build());
        summary.setNodeInstances(nodeInstances);
        summary.setTimerInstances(timerInstances);

        when(processService.getProcessInstanceDiagramSummary(processInstance.getProcessInstanceKey())).thenReturn(summary);

        presenter.setProcessInstance(processInstance);

        verify(view).showBusyIndicator(any());

        ArgumentCaptor<List> nodesCaptor = ArgumentCaptor.forClass(List.class);
        verify(view, times(2)).setProcessNodes(nodesCaptor.capture());

        assertThat(nodesCaptor.getAllValues().get(0)).isEmpty();
        final List<ProcessNodeSummary> viewNodes = nodesCaptor.getAllValues().get(1);
        assertThat(viewNodes).hasSameSizeAs(nodes);

        assertThat(viewNodes.get(0).getLabel()).isEqualTo("Split-3");
        assertThat(viewNodes.get(0).getName()).isEqualTo("Split");
        assertThat(viewNodes.get(0).getType()).isEqualTo("Split");
        assertThat(viewNodes.get(0).getUniqueId()).isEqualTo("_3");
        assertThat(viewNodes.get(0).getCallbacks()).hasSize(1);

        assertThat(viewNodes.get(1).getLabel()).isEqualTo("StartNode-1");
        assertThat(viewNodes.get(1).getName()).isEqualTo("StartNode");
        assertThat(viewNodes.get(1).getType()).isEqualTo("StartNode");
        assertThat(viewNodes.get(1).getUniqueId()).isEqualTo("_1");
        assertThat(viewNodes.get(1).getCallbacks()).isNullOrEmpty();

        assertThat(viewNodes.get(2).getLabel()).isEqualTo("task-name-2");
        assertThat(viewNodes.get(2).getName()).isEqualTo("task-name");
        assertThat(viewNodes.get(2).getType()).isEqualTo("HumanTask");
        assertThat(viewNodes.get(2).getUniqueId()).isEqualTo("_2");
        assertThat(viewNodes.get(2).getCallbacks()).hasSize(1);

        ArgumentCaptor<List> nodeInstancesCaptor = ArgumentCaptor.forClass(List.class);
        verify(view, times(2)).setNodeInstances(nodeInstancesCaptor.capture());

        assertThat(nodeInstancesCaptor.getAllValues().get(0)).isEmpty();
        final List<NodeInstanceSummary> viewNodeInstances = nodeInstancesCaptor.getAllValues().get(1);
        assertThat(viewNodeInstances).hasSameSizeAs(nodeInstances);

        assertThat(viewNodeInstances.get(0).getLabel()).isEqualTo("End-4");
        assertThat(viewNodeInstances.get(0).getName()).isEqualTo("End");
        assertThat(viewNodeInstances.get(0).getType()).isEqualTo("End");
        assertThat(viewNodeInstances.get(0).getDescription()).startsWith("Completed");
        assertThat(viewNodeInstances.get(0).getCallbacks()).isNullOrEmpty();

        assertThat(viewNodeInstances.get(1).getLabel()).isEqualTo("name-1-1");
        assertThat(viewNodeInstances.get(1).getName()).isEqualTo("name-1");
        assertThat(viewNodeInstances.get(1).getType()).isEqualTo("HumanTask");
        assertThat(viewNodeInstances.get(1).getDescription()).startsWith("Started");
        assertThat(viewNodeInstances.get(1).getCallbacks()).hasSize(2);

        assertThat(viewNodeInstances.get(2).getLabel()).isEqualTo("name-3-3");
        assertThat(viewNodeInstances.get(2).getName()).isEqualTo("name-3");
        assertThat(viewNodeInstances.get(2).getType()).isEqualTo("HumanTask");
        assertThat(viewNodeInstances.get(2).getDescription()).startsWith("Completed");
        assertThat(viewNodeInstances.get(2).getCallbacks()).isNullOrEmpty();

        assertThat(viewNodeInstances.get(3).getLabel()).isEqualTo("Split-2");
        assertThat(viewNodeInstances.get(3).getName()).isEqualTo("Split");
        assertThat(viewNodeInstances.get(3).getType()).isEqualTo("Split");
        assertThat(viewNodeInstances.get(3).getDescription()).startsWith("Started");
        assertThat(viewNodeInstances.get(3).getCallbacks()).hasSize(2);

        ArgumentCaptor<List> timerInstancesCaptor = ArgumentCaptor.forClass(List.class);
        verify(view, times(2)).setTimerInstances(timerInstancesCaptor.capture());

        assertThat(timerInstancesCaptor.getAllValues().get(0)).isEmpty();
        final List<TimerInstanceSummary> viewTimerInstances = timerInstancesCaptor.getAllValues().get(1);
        assertThat(viewTimerInstances).hasSameSizeAs(timerInstances);

        assertThat(viewTimerInstances.get(0).getLabel()).isEqualTo("t1-1");
        assertThat(viewTimerInstances.get(0).getName()).isEqualTo("t1");
        assertThat(viewTimerInstances.get(0).getDescription()).startsWith("NextExecution");
        assertThat(viewTimerInstances.get(0).getCallbacks()).hasSize(1);

        assertThat(viewTimerInstances.get(1).getLabel()).isEqualTo("t2-2");
        assertThat(viewTimerInstances.get(1).getName()).isEqualTo("t2");
        assertThat(viewTimerInstances.get(1).getDescription()).startsWith("NextExecution");
        assertThat(viewTimerInstances.get(1).getCallbacks()).hasSize(1);

        ArgumentCaptor<Map> badgesInstancesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(view).setNodeBadges(badgesInstancesCaptor.capture());
        final Map<String, Long> badges = badgesInstancesCaptor.getValue();
        assertThat(badges).containsExactly(immutableEntry("_1", 1l), immutableEntry("_2", 1l), immutableEntry("_3", 1l), immutableEntry("_4", 1l));
    }

    @Test
    public void testOnDiagramNodeNullSelected() {
        presenter.onDiagramNodeSelected(null);

        verify(view).setValue(new ProcessNodeSummary());
        verify(view).setNodeInstances(null);
        verify(view).setTimerInstances(null);
    }

    @Test
    public void testOnDiagramNodeSelected() {
        ProcessInstanceSummary processInstance = ProcessInstanceSummary.builder().withServerTemplateId("serverTemplateId").withDeploymentId("containerId").withProcessInstanceId(1l).withState(ProcessInstance.STATE_ACTIVE).build();

        ProcessNodeSummary timerNode = new ProcessNodeSummary(1l, "name-1", "Timer", "_1");
        List<ProcessNodeSummary> nodes = Arrays.asList(new ProcessNodeSummary(0l,
                                                                              " ",
                                                                              "Start",
                                                                              "_0"),
                                                       timerNode,
                                                       new ProcessNodeSummary(2l,
                                                                              " ",
                                                                              "Split",
                                                                              "_2"));

        NodeInstanceSummary timerNodeInstance = NodeInstanceSummary.builder().withId(1l).withName("name-1").withNodeUniqueName("_1").withType("Timer").withCompleted(false).build();

        List<NodeInstanceSummary> nodeInstances = Arrays.asList(
                timerNodeInstance,
                NodeInstanceSummary.builder().withId(2l).withName(" ").withNodeUniqueName("_2").withType("Split").withCompleted(false).build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withNodeUniqueName("_3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(4l).withName(" ").withNodeUniqueName("_4").withType("End").withCompleted(true).build()
        );

        TimerInstanceSummary timerInstance = TimerInstanceSummary.builder().withId(1l).withTimerId(0l).withName("t1").build();

        List<TimerInstanceSummary> timerInstances = Arrays.asList(
                timerInstance,
                TimerInstanceSummary.builder().withId(2l).withTimerId(1l).withName("t2").build()
        );

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessDefinition(ProcessSummary.builder().nodes(nodes).timers(singletonList(TimerSummary.builder().id(0l).uniqueId("_1").build())).build());
        summary.setNodeInstances(nodeInstances);
        summary.setTimerInstances(timerInstances);

        when(processService.getProcessInstanceDiagramSummary(processInstance.getProcessInstanceKey())).thenReturn(summary);

        presenter.setProcessInstance(processInstance);

        presenter.onDiagramNodeSelected("_1");

        verify(view).setValue(timerNode);
        verify(view).setNodeInstances(singletonList(timerNodeInstance));
        verify(view).setTimerInstances(singletonList(timerInstance));
        verify(view).setNodeBadges(any());
        verify(view).onShow();
    }

    @Test
    public void testOnNodeTriggered() {
        ProcessInstanceSummary processInstance = ProcessInstanceSummary.builder().withServerTemplateId("serverTemplateId").withDeploymentId("containerId").withProcessInstanceId(1l).withState(ProcessInstance.STATE_ACTIVE).build();

        final ProcessNodeSummary humanTask = new ProcessNodeSummary(1l,
                                                                    "name-1",
                                                                    "HumanTask",
                                                                    "_1");
        final List<ProcessNodeSummary> nodes = Arrays.asList(new ProcessNodeSummary(0l,
                                                                                    " ",
                                                                                    "Start",
                                                                                    "_0"),
                                                             humanTask,
                                                             new ProcessNodeSummary(2l,
                                                                                    " ",
                                                                                    "Split",
                                                                                    "_2"));

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessDefinition(ProcessSummary.builder().nodes(nodes).timers(emptyList()).build());
        summary.setNodeInstances(emptyList());
        summary.setTimerInstances(emptyList());

        when(processService.getProcessInstanceDiagramSummary(processInstance.getProcessInstanceKey())).thenReturn(summary);

        presenter.setProcessInstance(processInstance);

        presenter.onProcessNodeTrigger(humanTask);

        verify(processService).triggerProcessInstanceNode(processInstance.getProcessInstanceKey(),
                                                          humanTask.getId());
        verify(notificationEvent).fire(any());
        verify(view, times(2)).setValue(new ProcessNodeSummary());
    }

    @Test
    public void testOnNodeInstanceCancelled() {
        ProcessInstanceSummary processInstance = ProcessInstanceSummary.builder().withServerTemplateId("serverTemplateId").withDeploymentId("containerId").withProcessInstanceId(1l).withState(ProcessInstance.STATE_ACTIVE).build();

        NodeInstanceSummary humanTask = NodeInstanceSummary.builder().withId(1l).withName("name-1").withNodeUniqueName("_1").withType("HumanTask").withCompleted(false).build();

        List<NodeInstanceSummary> nodeInstances = Arrays.asList(
                humanTask,
                NodeInstanceSummary.builder().withId(2l).withName(" ").withNodeUniqueName("_2").withType("Split").withCompleted(false).build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withNodeUniqueName("_3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(4l).withName(" ").withNodeUniqueName("_4").withType("End").withCompleted(true).build()
        );

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessDefinition(ProcessSummary.builder().nodes(emptyList()).timers(emptyList()).build());
        summary.setNodeInstances(nodeInstances);
        summary.setTimerInstances(emptyList());

        when(processService.getProcessInstanceDiagramSummary(processInstance.getProcessInstanceKey())).thenReturn(summary);

        presenter.setProcessInstance(processInstance);

        presenter.onNodeInstanceCancel(humanTask);

        verify(processService).cancelProcessInstanceNode(processInstance.getProcessInstanceKey(),
                                                         humanTask.getId());
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testOnNodeInstanceReTriggered() {
        ProcessInstanceSummary processInstance = ProcessInstanceSummary.builder().withServerTemplateId("serverTemplateId").withDeploymentId("containerId").withProcessInstanceId(1l).withState(ProcessInstance.STATE_ACTIVE).build();

        NodeInstanceSummary humanTask = NodeInstanceSummary.builder().withId(1l).withName("name-1").withNodeUniqueName("_1").withType("HumanTask").withCompleted(false).build();

        List<NodeInstanceSummary> nodeInstances = Arrays.asList(
                humanTask,
                NodeInstanceSummary.builder().withId(2l).withName(" ").withNodeUniqueName("_2").withType("Split").withCompleted(false).build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withNodeUniqueName("_3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(4l).withName(" ").withNodeUniqueName("_4").withType("End").withCompleted(true).build()
        );

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessDefinition(ProcessSummary.builder().nodes(emptyList()).timers(emptyList()).build());
        summary.setNodeInstances(nodeInstances);
        summary.setTimerInstances(emptyList());

        when(processService.getProcessInstanceDiagramSummary(processInstance.getProcessInstanceKey())).thenReturn(summary);

        presenter.setProcessInstance(processInstance);

        presenter.onNodeInstanceReTrigger(humanTask);

        verify(processService).reTriggerProcessInstanceNode(processInstance.getProcessInstanceKey(),
                                                            humanTask.getId());
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testOnTimerInstanceReschedule() {
        ProcessInstanceSummary processInstance = ProcessInstanceSummary.builder().withServerTemplateId("serverTemplateId").withDeploymentId("containerId").withProcessInstanceId(1l).withState(ProcessInstance.STATE_ACTIVE).build();

        final TimerInstanceSummary timer = TimerInstanceSummary.builder().withId(1l).withName("t1").build();

        List<TimerInstanceSummary> timerInstance = Arrays.asList(
                timer,
                TimerInstanceSummary.builder().withId(2l).withName("t2").build());

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessDefinition(ProcessSummary.builder().nodes(emptyList()).timers(emptyList()).build());
        summary.setNodeInstances(emptyList());
        summary.setTimerInstances(timerInstance);

        when(processService.getProcessInstanceDiagramSummary(processInstance.getProcessInstanceKey())).thenReturn(summary);

        presenter.setProcessInstance(processInstance);

        presenter.onTimerInstanceReschedule(timer);

        verify(processService).rescheduleTimerInstance(processInstance.getProcessInstanceKey(),
                                                       timer);
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testTimerInstanceForNode() {
        ProcessInstanceSummary processInstance = ProcessInstanceSummary.builder().withServerTemplateId("serverTemplateId").withDeploymentId("containerId").withProcessInstanceId(1l).withState(ProcessInstance.STATE_ACTIVE).build();

        List<TimerSummary> timers = Arrays.asList(
                TimerSummary.builder().id(0l).nodeId(1l).uniqueId("_1").build(),
                TimerSummary.builder().id(1l).nodeId(2l).uniqueId("_2").build()
        );

        TimerInstanceSummary t1 = TimerInstanceSummary.builder().withId(1l).withTimerId(0l).withName("t1").build();
        TimerInstanceSummary t2 = TimerInstanceSummary.builder().withId(2l).withTimerId(1l).withName("t2").build();

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessDefinition(ProcessSummary.builder().nodes(emptyList()).timers(timers).build());
        summary.setNodeInstances(emptyList());
        summary.setTimerInstances(Arrays.asList(t1, t2));

        when(processService.getProcessInstanceDiagramSummary(processInstance.getProcessInstanceKey())).thenReturn(summary);

        presenter.setProcessInstance(processInstance);

        assertThat(presenter.getTimerInstanceForNode("_1")).containsExactly(t1);
        assertThat(presenter.getTimerInstanceForNode("_2")).containsExactly(t2);
        assertThat(presenter.getTimerInstanceForNode("_3")).isEmpty();
        assertThat(presenter.getTimerInstanceForNode(null)).isEmpty();
    }

    @Test
    public void testIsProcessNodeTypeTriggerAllowed() {
        assertFalse(presenter.isProcessNodeTypeTriggerAllowed(null));
        assertFalse(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary()));
        assertFalse(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary(1l,
                                                                                     "",
                                                                                     "StartNode",
                                                                                     "_1")));
        assertFalse(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary(1l,
                                                                                     "",
                                                                                     "Join",
                                                                                     "_1")));
        assertTrue(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary(1l,
                                                                                    "",
                                                                                    "Split",
                                                                                    "_1")));
        assertTrue(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary(1l,
                                                                                    "",
                                                                                    "HumanTaskNode",
                                                                                    "_1")));
        assertTrue(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary(1l,
                                                                                    "",
                                                                                    "TimerNode",
                                                                                    "_1")));
    }
}
