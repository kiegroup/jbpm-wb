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
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceDiagramPresenterTest {

    @Mock
    ProcessRuntimeDataService processService;

    @Mock
    EventSourceMock<NotificationEvent> notificationEvent;

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
        when(processService.getProcessInstanceDiagramSummary(any(),
                                                             any(),
                                                             any())).thenReturn(ProcessInstanceDiagramSummary.builder().withProcessNodes(emptyList()).withNodeInstances(emptyList()).build(),
                                                                                ProcessInstanceDiagramSummary.builder().withProcessNodes(emptyList()).withNodeInstances(emptyList()).withSvgContent("").build());

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
        when(processService.getProcessInstanceDiagramSummary(any(),
                                                             any(),
                                                             any())).thenReturn(ProcessInstanceDiagramSummary.builder().withProcessNodes(emptyList()).withNodeInstances(emptyList()).withSvgContent(svgContent).build(),
                                                                                ProcessInstanceDiagramSummary.builder().withProcessNodes(emptyList()).withNodeInstances(emptyList()).build());
        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(null,
                                                                                    1l,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    null));

        verify(view,
               never()).displayMessage(Constants.INSTANCE.Process_Diagram_Not_FoundContainerShouldBeAvailable(anyString()));
        verify(view).displayImage(svgContent);
    }

    @Test
    public void testOnProcessInstanceSelectionEvent() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long processInstanceId = 1L;
        String svgContent = "<svg></svg>";

        List<ProcessNodeSummary> nodes = Arrays.asList(new ProcessNodeSummary(0l,
                                                                              " ",
                                                                              "Start"),
                                                       new ProcessNodeSummary(1l,
                                                                              "name-1",
                                                                              "HumanTask"),
                                                       new ProcessNodeSummary(2l,
                                                                              " ",
                                                                              "Split"));

        List<NodeInstanceSummary> nodeInstances = Arrays.asList(
                NodeInstanceSummary.builder().withId(1l).withName("name-1").withType("HumanTask").build(),
                NodeInstanceSummary.builder().withId(2l).withName(" ").withType("Split").build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(4l).withName(" ").withType("End").withCompleted(true).build()
        );

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setSvgContent(svgContent);
        summary.setProcessNodes(nodes);
        summary.setNodeInstances(nodeInstances);

        when(processService.getProcessInstanceDiagramSummary(serverTemplateId,
                                                             containerId,
                                                             processInstanceId)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(containerId,
                                                                                    processInstanceId,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    serverTemplateId));

        verify(view).showBusyIndicator(any());

        ArgumentCaptor<List> nodesCaptor = ArgumentCaptor.forClass(List.class);
        verify(view,
               times(2)).setProcessNodes(nodesCaptor.capture());

        assertThat(nodesCaptor.getAllValues().get(0)).isEmpty();
        assertThat(nodesCaptor.getAllValues().get(1)).hasSameSizeAs(nodes).containsExactly(new ProcessNodeSummary(1l,
                                                                                                                  "name-1",
                                                                                                                  "HumanTask"),
                                                                                           new ProcessNodeSummary(2l,
                                                                                                                  " ",
                                                                                                                  "Split"),
                                                                                           new ProcessNodeSummary(0l,
                                                                                                                  " ",
                                                                                                                  "Start"));

        ArgumentCaptor<List> nodeInstancesCaptor = ArgumentCaptor.forClass(List.class);
        verify(view,
               times(2)).setNodeInstances(nodeInstancesCaptor.capture());

        assertThat(nodeInstancesCaptor.getAllValues().get(0)).isEmpty();
        assertThat(nodeInstancesCaptor.getAllValues().get(1)).hasSameSizeAs(nodeInstances).containsExactly(
                NodeInstanceSummary.builder().withId(4l).withName(" ").withType("End").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(1l).withName("name-1").withType("HumanTask").build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(2l).withName(" ").withType("Split").build()
        );
    }

    @Test
    public void testOnProcessNodeNullSelected() {
        presenter.onProcessNodeSelected(null);

        verify(view).setValue(new ProcessNodeSummary());
    }

    @Test
    public void testOnProcessNodeSelected() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long processInstanceId = 1L;

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
        summary.setProcessNodes(nodes);
        summary.setNodeInstances(emptyList());

        when(processService.getProcessInstanceDiagramSummary(serverTemplateId,
                                                             containerId,
                                                             processInstanceId)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(containerId,
                                                                                    processInstanceId,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    serverTemplateId));

        presenter.onProcessNodeSelected(1l);

        verify(view).setValue(humanTask);
    }

    @Test
    public void testOnNodeTriggered() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long processInstanceId = 1L;

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
        summary.setProcessNodes(nodes);
        summary.setNodeInstances(emptyList());

        when(processService.getProcessInstanceDiagramSummary(serverTemplateId,
                                                             containerId,
                                                             processInstanceId)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(containerId,
                                                                                    processInstanceId,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    serverTemplateId));

        presenter.onProcessNodeTrigger(1l);

        verify(processService).triggerProcessInstanceNode(serverTemplateId,
                                                          containerId,
                                                          processInstanceId,
                                                          humanTask.getId());
        verify(notificationEvent).fire(any());
        verify(view).setValue(new ProcessNodeSummary());
    }

    @Test
    public void testOnNodeInstanceCancelled() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long processInstanceId = 1L;

        NodeInstanceSummary humanTask = NodeInstanceSummary.builder().withId(1l).withName("name-1").withType("HumanTask").build();

        List<NodeInstanceSummary> nodeInstances = Arrays.asList(
                humanTask,
                NodeInstanceSummary.builder().withId(2l).withName(" ").withType("Split").build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(4l).withName(" ").withType("End").withCompleted(true).build()
        );

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessNodes(emptyList());
        summary.setNodeInstances(nodeInstances);

        when(processService.getProcessInstanceDiagramSummary(serverTemplateId,
                                                             containerId,
                                                             processInstanceId)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(containerId,
                                                                                    processInstanceId,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    serverTemplateId));

        presenter.onNodeInstanceCancel(1l);

        verify(processService).cancelProcessInstanceNode(serverTemplateId,
                                                         containerId,
                                                         processInstanceId,
                                                         humanTask.getId());
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testOnNodeInstanceReTriggered() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long processInstanceId = 1L;

        NodeInstanceSummary humanTask = NodeInstanceSummary.builder().withId(1l).withName("name-1").withType("HumanTask").build();

        List<NodeInstanceSummary> nodeInstances = Arrays.asList(
                humanTask,
                NodeInstanceSummary.builder().withId(2l).withName(" ").withType("Split").build(),
                NodeInstanceSummary.builder().withId(3l).withName("name-3").withType("HumanTask").withCompleted(true).build(),
                NodeInstanceSummary.builder().withId(4l).withName(" ").withType("End").withCompleted(true).build()
        );

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setProcessNodes(emptyList());
        summary.setNodeInstances(nodeInstances);

        when(processService.getProcessInstanceDiagramSummary(serverTemplateId,
                                                             containerId,
                                                             processInstanceId)).thenReturn(summary);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(containerId,
                                                                                    processInstanceId,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    serverTemplateId));

        presenter.onNodeInstanceReTrigger(1l);

        verify(processService).reTriggerProcessInstanceNode(serverTemplateId,
                                                            containerId,
                                                            processInstanceId,
                                                            humanTask.getId());
        verify(notificationEvent).fire(any());
    }
}
