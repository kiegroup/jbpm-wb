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

package org.jbpm.workbench.pr.backend.server;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessInstanceDiagramSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.model.TimerInstanceSummary;
import org.jbpm.workbench.pr.model.TimerSummary;
import org.jbpm.workbench.pr.model.WorkItemSummary;
import org.jbpm.workbench.pr.service.ProcessImageService;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.admin.TimerInstance;
import org.kie.server.api.model.definition.NodeDefinition;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.TimerDefinition;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.api.model.instance.TaskSummaryList;
import org.kie.server.api.model.instance.WorkItemInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.admin.ProcessAdminServicesClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jbpm.workbench.pr.backend.server.ProcessSummaryMapperTest.assertProcessSummary;
import static org.jbpm.workbench.pr.backend.server.WorkItemSummaryMapperTest.assertWorkItemSummary;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoteProcessRuntimeDataServiceImplTest {

    private final Long processInstanceId = 1l;
    private final String processId = "processId";
    private final String containerId = "containerId";
    private final String serverTemplateId = "serverTemplateId";

    @Mock
    private KieServerIntegration kieServerIntegration;

    @Mock
    private QueryServicesClient queryServicesClient;

    @Mock
    private ProcessAdminServicesClient processAdminServicesClient;

    @Mock
    private ProcessServicesClient processServicesClient;

    @Mock
    private ProcessImageService processImageService;

    @InjectMocks
    private RemoteProcessRuntimeDataServiceImpl service;

    @Before
    public void setup() {
        final KieServicesClient kieServicesClient = mock(KieServicesClient.class);
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(QueryServicesClient.class)).thenReturn(queryServicesClient);
        when(kieServicesClient.getServicesClient(ProcessServicesClient.class)).thenReturn(processServicesClient);
        when(kieServicesClient.getServicesClient(ProcessAdminServicesClient.class)).thenReturn(processAdminServicesClient);
    }

    @Test
    public void getProcessInstanceDetailsTest() {
        final Long processInstanceId = 1L;
        final TaskSummary taskSummaryMock = mock(TaskSummary.class);
        final TaskSummaryList taskSummaryListSpy = spy(new TaskSummaryList(singletonList(taskSummaryMock)));
        final ProcessInstance processInstanceSpy = spy(ProcessInstance.builder()
                                                               .activeUserTasks(taskSummaryListSpy)
                                                               .build());
        when(queryServicesClient.findProcessInstanceById(processInstanceId)).thenReturn(processInstanceSpy);
        service.getProcessInstance(new ProcessInstanceKey(serverTemplateId,
                                                          containerId,
                                                          processInstanceId));
        verify(processInstanceSpy).getProcessId();
        verify(processInstanceSpy).getState();
        verify(processInstanceSpy).getContainerId();
        verify(processInstanceSpy).getProcessVersion();
        verify(processInstanceSpy).getCorrelationKey();
        verify(processInstanceSpy).getParentId();
        verify(processInstanceSpy).getSlaCompliance();
        verify(processInstanceSpy).getSlaDueDate();
        verifyActiveUserTasks(taskSummaryListSpy,
                              taskSummaryMock);
        verifyCurrentActivities(processInstanceId);
    }

    public void verifyActiveUserTasks(TaskSummaryList taskSummaryList,
                                      TaskSummary taskSummary) {
        verify(taskSummaryList).getItems();
        verify(taskSummary).getName();
        verify(taskSummary).getStatus();
        verify(taskSummary).getActualOwner();
    }

    private void verifyCurrentActivities(Long processInstanceId) {
        final NodeInstance nodeInstanceMock = mock(NodeInstance.class);
        final List<NodeInstance> nodeInstanceList = singletonList(nodeInstanceMock);
        when(queryServicesClient.findActiveNodeInstances(processInstanceId,
                                                         0,
                                                         Integer.MAX_VALUE)).thenReturn(nodeInstanceList);
        when(nodeInstanceMock.getDate()).thenReturn(new Date());
        ProcessInstanceKey instanceKey = new ProcessInstanceKey(serverTemplateId,
                                                                containerId,
                                                                processInstanceId);
        service.getProcessInstanceActiveNodes(instanceKey);
        verify(queryServicesClient).findActiveNodeInstances(processInstanceId,
                                                            0,
                                                            Integer.MAX_VALUE);
        verify(nodeInstanceMock).getDate();
        verify(nodeInstanceMock).getId();
        verify(nodeInstanceMock).getName();
        verify(nodeInstanceMock).getNodeType();
    }

    @Test
    public void testInvalidServerTemplate() throws Exception {
        final Method[] methods = ProcessRuntimeDataService.class.getMethods();
        for (Method method : methods) {
            final Class<?> returnType = method.getReturnType();
            final Class<?>[] parameterType = method.getParameterTypes();
            final Object[] args = new Object[method.getParameterCount()];
            Object result = method.invoke(service,
                                          args);

            assertMethodResult(method,
                               returnType,
                               result);

            if (parameterType[0].isAssignableFrom(String.class)) {
                args[0] = "";
            } else if (parameterType[0].isAssignableFrom(ProcessInstanceKey.class)) {
                args[0] = new ProcessInstanceKey("",
                                                 "",
                                                 null);
            }

            result = method.invoke(service,
                                   args);
            assertMethodResult(method,
                               returnType,
                               result);
        }
    }

    private void assertMethodResult(final Method method,
                                    final Class<?> returnType,
                                    final Object result) {
        if (Collection.class.isAssignableFrom(returnType)) {
            assertNotNull(format("Returned collection for method %s should not be null",
                                 method.getName()),
                          result);
            assertTrue(format("Returned collection for method %s should be empty",
                              method.getName()),
                       ((Collection) result).isEmpty());
        } else {
            assertNull(format("Returned object for method %s should be null",
                              method.getName()),
                       result);
        }
    }

    @Test
    public void testGetProcesses() {
        final ProcessDefinition def = ProcessDefinition.builder().id(processId).nodes(emptyList()).timers(emptyList()).build();

        when(queryServicesClient.findProcesses(0,
                                               10,
                                               "",
                                               true)).thenReturn(singletonList(def));

        final List<ProcessSummary> summaries = service.getProcesses(serverTemplateId,
                                                                    0,
                                                                    10,
                                                                    "",
                                                                    true);

        assertNotNull(summaries);
        assertEquals(1, summaries.size());
        assertProcessSummary(def, summaries.get(0));
    }

    @Test
    public void testGetProcessesByFilter() {
        final ProcessDefinition def = ProcessDefinition.builder().id(processId).timers(emptyList()).nodes(emptyList()).build();

        when(queryServicesClient.findProcesses("filter",
                                               0,
                                               10,
                                               "",
                                               true)).thenReturn(singletonList(def));

        final List<ProcessSummary> summaries = service.getProcessesByFilter(serverTemplateId,
                                                                            "filter",
                                                                            0,
                                                                            10,
                                                                            "",
                                                                            true);

        assertNotNull(summaries);
        assertEquals(1, summaries.size());
        assertProcessSummary(def, summaries.get(0));
    }

    @Test
    public void testGetProcess() {
        final ProcessDefinition def = ProcessDefinition.builder().id(processId).nodes(emptyList()).timers(emptyList()).build();

        when(processServicesClient.getProcessDefinition(containerId, processId)).thenReturn(def);

        final ProcessDefinitionKey pdk = new ProcessDefinitionKey(serverTemplateId,
                                                                  containerId,
                                                                  processId);

        final ProcessSummary summary = service.getProcess(pdk);

        assertNotNull(summary);
        assertProcessSummary(def, summary);
    }

    @Test
    public void testGetWorkItemByProcessInstanceId() {
        Long workItemId = 1L;
        Long processInstanceId = 2L;

        WorkItemInstance workItem = WorkItemInstance.builder()
                .id(workItemId)
                .parameters(singletonMap("key",
                                         "value"))
                .build();

        when(processServicesClient.getWorkItem(containerId,
                                               processInstanceId,
                                               workItemId)).thenReturn(workItem);

        final WorkItemSummary summary = service.getWorkItemByProcessInstanceId(serverTemplateId,
                                                                               containerId,
                                                                               processInstanceId,
                                                                               workItemId);

        assertNotNull(summary);
        assertWorkItemSummary(workItem,
                              summary);
    }

    @Test
    public void testGetProcessInstanceActiveNodes() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey(serverTemplateId,
                                                                containerId,
                                                                processInstanceId);
        List<NodeInstance> nodeInstances = Arrays.asList(NodeInstance.builder().id(1l).name("name-1").nodeType("HumanTask").build(),
                                                         NodeInstance.builder().id(2l).name(" ").nodeType("Split").build());

        when(queryServicesClient.findActiveNodeInstances(instanceKey.getProcessInstanceId(),
                                                         0,
                                                         Integer.MAX_VALUE)).thenReturn(nodeInstances);

        List<NodeInstanceSummary> nodes = service.getProcessInstanceActiveNodes(instanceKey);

        assertThat(nodes).hasSize(2).containsExactly(NodeInstanceSummary.builder().withId(1l).withName("name-1").withType("HumanTask").build(),
                                                     NodeInstanceSummary.builder().withId(2l).withName(" ").withType("Split").build());
    }

    @Test
    public void testGetProcessInstanceCompletedNodes() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey(serverTemplateId,
                                                                containerId,
                                                                processInstanceId);

        List<NodeInstance> nodeInstances = Arrays.asList(NodeInstance.builder().id(1l).name("name-1").nodeType("HumanTask").build(),
                                                         NodeInstance.builder().id(2l).name(" ").nodeType("Split").build());

        when(queryServicesClient.findCompletedNodeInstances(instanceKey.getProcessInstanceId(),
                                                            0,
                                                            Integer.MAX_VALUE)).thenReturn(nodeInstances);

        List<NodeInstanceSummary> nodes = service.getProcessInstanceCompletedNodes(instanceKey);

        assertThat(nodes).hasSize(2).containsExactly(NodeInstanceSummary.builder().withId(1l).withName("name-1").withType("HumanTask").build(),
                                                     NodeInstanceSummary.builder().withId(2l).withName(" ").withType("Split").build());
    }

    @Test
    public void testGetProcessInstanceTimerInstances() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey(serverTemplateId, containerId, processInstanceId);

        List<TimerInstance> timerInstances = Arrays.asList(TimerInstance.builder().id(1l).timerId(0l).timerName("timer1").period(2l).delay(1).build(),
                                                           TimerInstance.builder().id(2l).timerId(1l).timerName("time2").period(1l).delay(2).build());

        when(processAdminServicesClient.getTimerInstances(containerId, processInstanceId)).thenReturn(timerInstances);

        List<TimerInstanceSummary> timers = service.getProcessInstanceTimerInstances(instanceKey);

        assertThat(timers).hasSize(2).containsExactly(TimerInstanceSummary.builder().withId(1l).withTimerId(0l).withName("timer1").withPeriod(2l).withDelay(1l).build(),
                                                      TimerInstanceSummary.builder().withId(2l).withTimerId(1l).withName("time2").withPeriod(1l).withDelay(2l).build());
    }

    @Test
    public void testTriggerProcessInstanceNode() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey(serverTemplateId,
                                                                containerId,
                                                                processInstanceId);
        Long nodeId = 2L;

        service.triggerProcessInstanceNode(instanceKey,
                                           nodeId);

        verify(processAdminServicesClient).triggerNode(containerId,
                                                       processInstanceId,
                                                       nodeId);
    }

    @Test
    public void testReTriggerProcessInstanceNode() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey(serverTemplateId,
                                                                containerId,
                                                                processInstanceId);
        Long nodeId = 2L;

        service.reTriggerProcessInstanceNode(instanceKey,
                                             nodeId);

        verify(processAdminServicesClient).retriggerNodeInstance(containerId,
                                                                 processInstanceId,
                                                                 nodeId);
    }

    @Test
    public void testCancelProcessInstanceNode() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey(serverTemplateId,
                                                                containerId,
                                                                processInstanceId);
        Long nodeId = 2L;

        service.cancelProcessInstanceNode(instanceKey,
                                          nodeId);

        verify(processAdminServicesClient).cancelNodeInstance(containerId,
                                                              processInstanceId,
                                                              nodeId);
    }

    @Test
    public void testGetProcessInstanceDiagramSummary() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey(serverTemplateId, containerId, processInstanceId);
        String svgContent = "<svg></svg>";
        Integer state = org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
        String processName = "process";

        when(queryServicesClient.findProcessInstanceById(processInstanceId)).thenReturn(ProcessInstance.builder().id(processInstanceId).containerId(containerId).processId(processId).state(state).processName(processName).build());
        when(processImageService.getProcessInstanceDiagram(serverTemplateId, containerId, processInstanceId, "", "", "")).thenReturn(svgContent);

        List<NodeDefinition> processNodes = Arrays.asList(NodeDefinition.builder().id(1l).name("name-1").type("HumanTask").uniqueId("_1").build(),
                                                          NodeDefinition.builder().id(2l).name(" ").type("Split").uniqueId("_2").build());

        List<TimerDefinition> timers = Arrays.asList(TimerDefinition.builder().id(1l).nodeId(2l).nodeName("name-1").uniqueId("_1").build(),
                                                     TimerDefinition.builder().id(2l).nodeId(0l).nodeName(" ").uniqueId("_2").build());

        when(processServicesClient.getProcessDefinition(containerId, processId)).thenReturn(ProcessDefinition.builder().id(processId).nodes(processNodes).timers(timers).build());

        List<NodeInstance> activeNodeInstances = Arrays.asList(NodeInstance.builder().id(1l).name("name-1").nodeType("HumanTask").build(),
                                                               NodeInstance.builder().id(2l).name(" ").nodeType("Split").build());

        when(queryServicesClient.findActiveNodeInstances(processInstanceId, 0, Integer.MAX_VALUE)).thenReturn(activeNodeInstances);

        List<NodeInstance> completedNodeInstances = Arrays.asList(NodeInstance.builder().id(3l).name("name-3").nodeType("HumanTask").completed(true).build(),
                                                                  NodeInstance.builder().id(4l).name(" ").nodeType("End").completed(true).build());

        when(queryServicesClient.findCompletedNodeInstances(processInstanceId, 0, Integer.MAX_VALUE)).thenReturn(completedNodeInstances);

        List<TimerInstance> timerInstances = Arrays.asList(TimerInstance.builder().id(1l).timerId(1l).timerName("timer1").processInstanceId(processInstanceId).repeatLimit(1).period(2l).delay(1).build(),
                                                           TimerInstance.builder().id(2l).timerId(2l).timerName("time2").processInstanceId(processInstanceId).repeatLimit(1).period(1l).delay(2).build());

        when(processAdminServicesClient.getTimerInstances(containerId, processInstanceId)).thenReturn(timerInstances);

        ProcessInstanceDiagramSummary summary = service.getProcessInstanceDiagramSummary(instanceKey, "", "", "");

        assertEquals(processInstanceId, summary.getId());
        assertEquals(processName, summary.getName());
        assertEquals(svgContent, summary.getSvgContent());
        assertNotNull(summary.getProcessDefinition());

        assertThat(summary.getProcessDefinition().getNodes()).hasSize(2).containsExactly(new ProcessNodeSummary(1l,
                                                                                                                "name-1",
                                                                                                                "HumanTask",
                                                                                                                "_1"),
                                                                                         new ProcessNodeSummary(2l,
                                                                                                                " ",
                                                                                                                "Split",
                                                                                                                "_2"));

        assertThat(summary.getProcessDefinition().getTimers()).hasSize(2).containsExactly(new TimerSummary(1l,
                                                                                                           2l,
                                                                                                           "name-1",
                                                                                                           "_1"),
                                                                                          new TimerSummary(2l,
                                                                                                           0l,
                                                                                                           " ",
                                                                                                           "_2"));

        assertThat(summary.getNodeInstances()).hasSize(4).containsExactly(NodeInstanceSummary.builder().withId(1l).withName("name-1").withType("HumanTask").build(),
                                                                          NodeInstanceSummary.builder().withId(2l).withName(" ").withType("Split").build(),
                                                                          NodeInstanceSummary.builder().withId(3l).withName("name-3").withType("HumanTask").withCompleted(true).build(),
                                                                          NodeInstanceSummary.builder().withId(4l).withName(" ").withType("End").withCompleted(true).build());

        assertThat(summary.getTimerInstances()).hasSize(2).containsExactly(TimerInstanceSummary.builder().withId(1l).withTimerId(1l).withName("timer1").withProcessInstanceId(processInstanceId).withRepeatLimit(1).withPeriod(2l).withDelay(1l).build(),
                                                                           TimerInstanceSummary.builder().withId(2l).withTimerId(2l).withName("time2").withProcessInstanceId(processInstanceId).withRepeatLimit(1).withPeriod(1l).withDelay(2l).build());
    }

    @Test
    public void testGetProcessInstanceDiagramSummaryCompletedStatus() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey(serverTemplateId, containerId, processInstanceId);
        String svgContent = "<svg></svg>";
        Integer state = org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
        String processName = "process";

        when(queryServicesClient.findProcessInstanceById(processInstanceId)).thenReturn(ProcessInstance.builder().id(processInstanceId).processId(processId).containerId(containerId).state(state).processName(processName).build());

        when(processImageService.getProcessInstanceDiagram(serverTemplateId, containerId, processInstanceId, "", "", "")).thenReturn(svgContent);

        when(processServicesClient.getProcessDefinition(containerId, processId)).thenReturn(new ProcessDefinition());

        ProcessInstanceDiagramSummary summary = service.getProcessInstanceDiagramSummary(instanceKey, "", "", "");

        assertEquals(processInstanceId, summary.getId());
        assertEquals(processName, summary.getName());
        assertEquals(svgContent, summary.getSvgContent());
        assertNotNull(summary.getProcessDefinition());
        assertThat(summary.getProcessDefinition().getNodes()).isEmpty();
        assertThat(summary.getProcessDefinition().getTimers()).isEmpty();
        assertThat(summary.getNodeInstances()).isEmpty();
        assertThat(summary.getTimerInstances()).isEmpty();

        verify(queryServicesClient, never()).findActiveNodeInstances(any(), any(), any());

        verify(queryServicesClient, never()).findCompletedNodeInstances(any(), any(), any());

        verify(processAdminServicesClient, never()).getTimerInstances(any(), any());
    }

    @Test
    public void testRescheduleTimerInstance() {
        ProcessInstanceKey instanceKey = new ProcessInstanceKey(serverTemplateId,
                                                                containerId,
                                                                processInstanceId);

        TimerInstanceSummary summary = TimerInstanceSummary.builder().withId(2l).withRelative(false).withDelay(3l).withProcessInstanceId(1l).withPeriod(4l).withRepeatLimit(1).build();

        service.rescheduleTimerInstance(instanceKey,
                                        summary);

        verify(processAdminServicesClient).updateTimer(containerId,
                                                       processInstanceId,
                                                       summary.getId(),
                                                       summary.getDelay(),
                                                       summary.getPeriod(),
                                                       summary.getRepeatLimit());

        summary.setRelative(true);

        service.rescheduleTimerInstance(instanceKey,
                                        summary);

        verify(processAdminServicesClient).updateTimerRelative(containerId,
                                                               processInstanceId,
                                                               summary.getId(),
                                                               summary.getDelay(),
                                                               summary.getPeriod(),
                                                               summary.getRepeatLimit());

        verifyNoMoreInteractions(processAdminServicesClient);
    }
}