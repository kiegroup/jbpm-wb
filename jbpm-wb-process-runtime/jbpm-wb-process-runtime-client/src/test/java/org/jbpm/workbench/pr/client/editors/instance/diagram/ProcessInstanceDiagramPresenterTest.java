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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceDiagramSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.jbpm.workbench.pr.model.TimerInstanceSummary;
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

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
        when(processService.getProcessInstanceDiagramSummary(any())).thenReturn(ProcessInstanceDiagramSummary.builder().withProcessNodes(emptyList()).withNodeInstances(emptyList()).withTimerInstances(emptyList()).withProcessInstanceState(1).build(),
                                                                                ProcessInstanceDiagramSummary.builder().withProcessNodes(emptyList()).withNodeInstances(emptyList()).withTimerInstances(emptyList()).withProcessInstanceState(1).withSvgContent("").build());

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(null,
                                                                                    1l,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    null));
        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(null,
                                                                                    1l,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    null));

        verify(view,
               times(2)).displayMessage(Constants.INSTANCE.Process_Diagram_Not_FoundContainerShouldBeAvailable(anyString()));
    }

    @Test
    public void testProcessInstanceDiagram() {
        final String svgContent = "<svg></svg>";
        when(processService.getProcessInstanceDiagramSummary(any())).thenReturn(ProcessInstanceDiagramSummary.builder().withProcessNodes(emptyList()).withNodeInstances(emptyList()).withTimerInstances(emptyList()).withProcessInstanceState(1).withSvgContent(svgContent).build(),
                                                                                ProcessInstanceDiagramSummary.builder().withProcessNodes(emptyList()).withNodeInstances(emptyList()).withTimerInstances(emptyList()).withProcessInstanceState(1).build());
        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(new ProcessInstanceKey(null,
                                                                                                           null,
                                                                                                           1l),
                                                                                    null,
                                                                                    null,
                                                                                    false));

        verify(view,
               never()).displayMessage(Constants.INSTANCE.Process_Diagram_Not_FoundContainerShouldBeAvailable(anyString()));
        verify(view).displayImage(svgContent);
    }

    @Test
    public void testOnProcessInstanceSelectionEvent() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey("serverTemplateId",
                                                                "containerId",
                                                                1L);
        String svgContent = "<svg></svg>";

        List<ProcessNodeSummary> nodes = Arrays.asList(new ProcessNodeSummary(1l,
                                                                              " ",
                                                                              "StartNode"),
                                                       new ProcessNodeSummary(2l,
                                                                              "task-name",
                                                                              "HumanTask"),
                                                       new ProcessNodeSummary(3l,
                                                                              " ",
                                                                              "Split"));

        List<NodeInstanceSummary> nodeInstances = Arrays.asList(
                NodeInstanceSummary.builder().withId(1l).withName("name-1").withType("HumanTask").withCompleted(false).build(),
                NodeInstanceSummary.builder().withId(2l).withName(" ").withType("Split").withCompleted(false).build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(4l).withName(" ").withType("End").withCompleted(true).build()
        );

        List<TimerInstanceSummary> timerInstances = Arrays.asList(
                TimerInstanceSummary.builder().withId(2l).withName("t2").build(),
                TimerInstanceSummary.builder().withId(1l).withName("t1").build());

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessInstanceState(ProcessInstance.STATE_ACTIVE);
        summary.setSvgContent(svgContent);
        summary.setProcessNodes(nodes);
        summary.setNodeInstances(nodeInstances);
        summary.setTimerInstances(timerInstances);

        when(processService.getProcessInstanceDiagramSummary(instanceKey)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(instanceKey,
                                                                                    null,
                                                                                    null,
                                                                                    false));

        verify(view).showBusyIndicator(any());

        ArgumentCaptor<List> nodesCaptor = ArgumentCaptor.forClass(List.class);
        verify(view,
               times(2)).setProcessNodes(nodesCaptor.capture());

        assertThat(nodesCaptor.getAllValues().get(0)).isEmpty();
        final List<ProcessNodeSummary> viewNodes = nodesCaptor.getAllValues().get(1);
        assertThat(viewNodes).hasSameSizeAs(nodes);
        assertThat(viewNodes.get(0).getLabel()).isEqualTo("1-StartNode");
        assertThat(viewNodes.get(0).getName()).isEqualTo(" ");
        assertThat(viewNodes.get(0).getType()).isEqualTo("StartNode");
        assertThat(viewNodes.get(0).getCallbacks()).isNullOrEmpty();

        assertThat(viewNodes.get(1).getLabel()).isEqualTo("2-task-name");
        assertThat(viewNodes.get(1).getName()).isEqualTo("task-name");
        assertThat(viewNodes.get(1).getType()).isEqualTo("HumanTask");
        assertThat(viewNodes.get(1).getCallbacks()).hasSize(1);

        assertThat(viewNodes.get(2).getLabel()).isEqualTo("3-Split");
        assertThat(viewNodes.get(2).getName()).isEqualTo(" ");
        assertThat(viewNodes.get(2).getType()).isEqualTo("Split");
        assertThat(viewNodes.get(2).getCallbacks()).hasSize(1);

        ArgumentCaptor<List> nodeInstancesCaptor = ArgumentCaptor.forClass(List.class);
        verify(view,
               times(2)).setNodeInstances(nodeInstancesCaptor.capture());

        assertThat(nodeInstancesCaptor.getAllValues().get(0)).isEmpty();
        final List<NodeInstanceSummary> viewNodeInstances = nodeInstancesCaptor.getAllValues().get(1);
        assertThat(viewNodeInstances).hasSameSizeAs(nodeInstances);

        assertThat(viewNodeInstances.get(0).getLabel()).isEqualTo("1-name-1");
        assertThat(viewNodeInstances.get(0).getName()).isEqualTo("name-1");
        assertThat(viewNodeInstances.get(0).getType()).isEqualTo("HumanTask");
        assertThat(viewNodeInstances.get(0).getDescription()).startsWith("Started");
        assertThat(viewNodeInstances.get(0).getCallbacks()).hasSize(2);

        assertThat(viewNodeInstances.get(1).getLabel()).isEqualTo("2-Split");
        assertThat(viewNodeInstances.get(1).getName()).isEqualTo(" ");
        assertThat(viewNodeInstances.get(1).getType()).isEqualTo("Split");
        assertThat(viewNodeInstances.get(1).getDescription()).startsWith("Started");
        assertThat(viewNodeInstances.get(1).getCallbacks()).hasSize(2);

        assertThat(viewNodeInstances.get(2).getLabel()).isEqualTo("3-name-3");
        assertThat(viewNodeInstances.get(2).getName()).isEqualTo("name-3");
        assertThat(viewNodeInstances.get(2).getType()).isEqualTo("HumanTask");
        assertThat(viewNodeInstances.get(2).getDescription()).startsWith("Completed");
        assertThat(viewNodeInstances.get(2).getCallbacks()).isNullOrEmpty();

        assertThat(viewNodeInstances.get(3).getLabel()).isEqualTo("4-End");
        assertThat(viewNodeInstances.get(3).getName()).isEqualTo(" ");
        assertThat(viewNodeInstances.get(3).getType()).isEqualTo("End");
        assertThat(viewNodeInstances.get(3).getDescription()).startsWith("Completed");
        assertThat(viewNodeInstances.get(3).getCallbacks()).isNullOrEmpty();

        ArgumentCaptor<List> timerInstancesCaptor = ArgumentCaptor.forClass(List.class);
        verify(view,
               times(2)).setTimerInstances(timerInstancesCaptor.capture());

        assertThat(timerInstancesCaptor.getAllValues().get(0)).isEmpty();
        final List<TimerInstanceSummary> viewTimerInstances = timerInstancesCaptor.getAllValues().get(1);
        assertThat(viewTimerInstances).hasSameSizeAs(timerInstances);

        assertThat(viewTimerInstances.get(0).getLabel()).isEqualTo("1-t1");
        assertThat(viewTimerInstances.get(0).getName()).isEqualTo("t1");
        assertThat(viewTimerInstances.get(0).getDescription()).startsWith("NextExecution");
        assertThat(viewTimerInstances.get(0).getCallbacks()).hasSize(1);

        assertThat(viewTimerInstances.get(1).getLabel()).isEqualTo("2-t2");
        assertThat(viewTimerInstances.get(1).getName()).isEqualTo("t2");
        assertThat(viewTimerInstances.get(1).getDescription()).startsWith("NextExecution");
        assertThat(viewTimerInstances.get(1).getCallbacks()).hasSize(1);
    }

    @Test
    public void testOnProcessNodeNullSelected() {
        presenter.onProcessNodeSelected(null);

        verify(view).setValue(new ProcessNodeSummary());
    }

    @Test
    public void testOnProcessNodeSelected() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey("serverTemplateId",
                                                                "containerId",
                                                                1L);

        final ProcessNodeSummary humanTask = new ProcessNodeSummary(1l,
                                                                    "name-1",
                                                                    "HumanTask");
        final List<ProcessNodeSummary> nodes = Arrays.asList(new ProcessNodeSummary(0l,
                                                                                    " ",
                                                                                    "Start"),
                                                             humanTask,
                                                             new ProcessNodeSummary(2l,
                                                                                    " ",
                                                                                    "Split"));

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessInstanceState(ProcessInstance.STATE_ACTIVE);
        summary.setProcessNodes(nodes);
        summary.setNodeInstances(emptyList());
        summary.setTimerInstances(emptyList());

        when(processService.getProcessInstanceDiagramSummary(instanceKey)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(instanceKey,
                                                                                    null,
                                                                                    null,
                                                                                    false));

        presenter.onProcessNodeSelected(1l);

        verify(view).setValue(humanTask);
    }

    @Test
    public void testOnNodeTriggered() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey("serverTemplateId",
                                                                "containerId",
                                                                1L);

        final ProcessNodeSummary humanTask = new ProcessNodeSummary(1l,
                                                                    "name-1",
                                                                    "HumanTask");
        final List<ProcessNodeSummary> nodes = Arrays.asList(new ProcessNodeSummary(0l,
                                                                                    " ",
                                                                                    "Start"),
                                                             humanTask,
                                                             new ProcessNodeSummary(2l,
                                                                                    " ",
                                                                                    "Split"));

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessInstanceState(ProcessInstance.STATE_ACTIVE);
        summary.setProcessNodes(nodes);
        summary.setNodeInstances(emptyList());
        summary.setTimerInstances(emptyList());

        when(processService.getProcessInstanceDiagramSummary(instanceKey)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(instanceKey,
                                                                                    null,
                                                                                    null,
                                                                                    false));

        presenter.onProcessNodeTrigger(humanTask);

        verify(processService).triggerProcessInstanceNode(instanceKey,
                                                          humanTask.getId());
        verify(notificationEvent).fire(any());
        verify(view).setValue(new ProcessNodeSummary());
    }

    @Test
    public void testOnNodeInstanceCancelled() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey("serverTemplateId",
                                                                "containerId",
                                                                1L);

        NodeInstanceSummary humanTask = NodeInstanceSummary.builder().withId(1l).withName("name-1").withType("HumanTask").withCompleted(false).build();

        List<NodeInstanceSummary> nodeInstances = Arrays.asList(
                humanTask,
                NodeInstanceSummary.builder().withId(2l).withName(" ").withType("Split").withCompleted(false).build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(4l).withName(" ").withType("End").withCompleted(true).build()
        );

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessInstanceState(ProcessInstance.STATE_ACTIVE);
        summary.setProcessNodes(emptyList());
        summary.setNodeInstances(nodeInstances);
        summary.setTimerInstances(emptyList());

        when(processService.getProcessInstanceDiagramSummary(instanceKey)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(instanceKey,
                                                                                    null,
                                                                                    null,
                                                                                    false));

        presenter.onNodeInstanceCancel(humanTask);

        verify(processService).cancelProcessInstanceNode(instanceKey,
                                                         humanTask.getId());
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testOnNodeInstanceReTriggered() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey("serverTemplateId",
                                                                "containerId",
                                                                1L);

        NodeInstanceSummary humanTask = NodeInstanceSummary.builder().withId(1l).withName("name-1").withType("HumanTask").withCompleted(false).build();

        List<NodeInstanceSummary> nodeInstances = Arrays.asList(
                humanTask,
                NodeInstanceSummary.builder().withId(2l).withName(" ").withType("Split").withCompleted(false).build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(4l).withName(" ").withType("End").withCompleted(true).build()
        );

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessInstanceState(ProcessInstance.STATE_ACTIVE);
        summary.setProcessNodes(emptyList());
        summary.setNodeInstances(nodeInstances);
        summary.setTimerInstances(emptyList());

        when(processService.getProcessInstanceDiagramSummary(instanceKey)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(instanceKey,
                                                                                    null,
                                                                                    null,
                                                                                    false));

        presenter.onNodeInstanceReTrigger(humanTask);

        verify(processService).reTriggerProcessInstanceNode(instanceKey,
                                                            humanTask.getId());
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testOnTimerInstanceReschedule() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey("serverTemplateId",
                                                                "containerId",
                                                                1L);

        final TimerInstanceSummary timer = TimerInstanceSummary.builder().withId(1l).withName("t1").build();

        List<TimerInstanceSummary> timerInstance = Arrays.asList(
                timer,
                TimerInstanceSummary.builder().withId(2l).withName("t2").build());

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessInstanceState(ProcessInstance.STATE_ACTIVE);
        summary.setProcessNodes(emptyList());
        summary.setNodeInstances(emptyList());
        summary.setTimerInstances(timerInstance);

        when(processService.getProcessInstanceDiagramSummary(instanceKey)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(instanceKey,
                                                                                    null,
                                                                                    null,
                                                                                    false));

        presenter.onTimerInstanceReschedule(timer);

        verify(processService).rescheduleTimerInstance(instanceKey,
                                                       timer);
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testIsProcessNodeTypeTriggerAllowed() {
        assertFalse(presenter.isProcessNodeTypeTriggerAllowed(null));
        assertFalse(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary()));
        assertFalse(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary(1l,
                                                                                     "",
                                                                                     "StartNode")));
        assertFalse(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary(1l,
                                                                                     "",
                                                                                     "Join")));
        assertTrue(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary(1l,
                                                                                    "",
                                                                                    "Split")));
        assertTrue(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary(1l,
                                                                                    "",
                                                                                    "HumanTaskNode")));
        assertTrue(presenter.isProcessNodeTypeTriggerAllowed(new ProcessNodeSummary(1l,
                                                                                    "",
                                                                                    "TimerNode")));
    }
}
