/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.client.editors.instance.details;

import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.UserTaskSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceDetailsTabPresenterTest {

    private static final int ACTIVE_STATE = 1;
    private static final int SLA_MET = 2;
    private static final String PROCESS_VERSION = "1.0";
    private static final String PROCESS_ID = "evaluation";
    private static final Long PROCESS_INSTANCE_ID = 3l;
    private static final String SERVER_TEMPLATE_ID = "testTemplate";
    private static final String DEPLOYMENT_ID = "evaluation_1.0.0-SNAPSHOT";

    private UserTaskSummary userTaskSummary;
    private NodeInstanceSummary nodeInstanceSummary;
    private ProcessInstanceSummary processInstanceSummary;

    @Mock
    private ProcessInstanceDetailsTabPresenter.ProcessInstanceDetailsTabView view;

    @Mock
    private ProcessRuntimeDataService processRuntimeDataServiceMock;

    @InjectMocks
    private ProcessInstanceDetailsTabPresenter presenter;

    @Before
    public void setUp() {
        presenter.setProcessRuntimeDataService(new CallerMock<>(processRuntimeDataServiceMock));
        nodeInstanceSummary = getNodeInstanceSummary();
        processInstanceSummary = getProcessInstanceSummary();
        when(processRuntimeDataServiceMock.getProcessInstanceActiveNodes(processInstanceSummary.getProcessInstanceKey())).thenReturn(singletonList(nodeInstanceSummary));
    }

    @Test
    public void setProcessInstanceDetailsTest() {
        presenter.setProcessInstance(processInstanceSummary);

        verify(view).setProcessDefinitionIdText(processInstanceSummary.getProcessId());
        verify(view).setStateText(Constants.INSTANCE.Active());
        verify(view).setProcessDeploymentText(processInstanceSummary.getDeploymentId());
        verify(view).setProcessVersionText(processInstanceSummary.getProcessVersion());
        verify(view).setCorrelationKeyText(processInstanceSummary.getCorrelationKey());
        verify(view).setParentProcessInstanceIdText(Constants.INSTANCE.No_Parent_Process_Instance());
        verify(view).setSlaComplianceText(org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.SlaMet());

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(view, times(2)).setActiveTasksListBox(argumentCaptor.capture());

        assertThat(argumentCaptor.getAllValues()).as("Active user tasks are set").hasSize(2);
        assertEquals("", argumentCaptor.getAllValues().get(0));
        assertThat(argumentCaptor.getAllValues().get(1))
                .as("Active user tasks")
                .contains(userTaskSummary.getName(),
                          userTaskSummary.getStatus(),
                          userTaskSummary.getOwner());

        argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(view, times(2)).setCurrentActivitiesListBox(argumentCaptor.capture());
        assertThat(argumentCaptor.getAllValues()).as("Current Activities are set").hasSize(2);
        assertEquals("", argumentCaptor.getAllValues().get(0));
        assertThat(argumentCaptor.getAllValues().get(1))
                .as("Current Activities")
                .contains(nodeInstanceSummary.getTimestamp().toString(),
                          String.valueOf(nodeInstanceSummary.getId()),
                          nodeInstanceSummary.getName(),
                          nodeInstanceSummary.getType());
    }

    private NodeInstanceSummary getNodeInstanceSummary() {
        NodeInstanceSummary nodeInstanceSummary = new NodeInstanceSummary();
        nodeInstanceSummary.setTimestamp(new Date());
        nodeInstanceSummary.setId(1L);
        nodeInstanceSummary.setName("Self Evaluation");
        nodeInstanceSummary.setType("HumanTaskNode");
        return nodeInstanceSummary;
    }

    private ProcessInstanceSummary getProcessInstanceSummary() {
        ProcessInstanceSummary processInstanceSummary = new ProcessInstanceSummary();
        processInstanceSummary.setServerTemplateId(SERVER_TEMPLATE_ID);
        processInstanceSummary.setProcessId(PROCESS_ID);
        processInstanceSummary.setState(ACTIVE_STATE);
        processInstanceSummary.setDeploymentId(DEPLOYMENT_ID);
        processInstanceSummary.setProcessVersion(PROCESS_VERSION);
        processInstanceSummary.setCorrelationKey(PROCESS_INSTANCE_ID.toString());
        processInstanceSummary.setParentId(0L);
        processInstanceSummary.setActiveTasks(singletonList(getUserTaskSummary()));
        processInstanceSummary.setSlaCompliance(SLA_MET);
        return processInstanceSummary;
    }

    private UserTaskSummary getUserTaskSummary() {
        userTaskSummary = new UserTaskSummary(1L, "Self Evaluation", "testuser", "Reserved");
        return userTaskSummary;
    }
}
