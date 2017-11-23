/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.client.editors.instance.details.multi;

import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.pr.client.editors.instance.details.ProcessInstanceDetailsPresenter;
import org.jbpm.workbench.pr.client.editors.instance.details.ProcessInstanceDetailsViewImpl;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.UserTaskSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.CallerMock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static java.util.Collections.singletonList;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceDetailsPresenterTest {

    private static final int ACTIVE_STATE = 1;
    private static final String PROCESS_VERSION = "1.0";
    private static final String PROCESS_ID = "evaluation";
    private static final String PROCESS_INSTANCE_ID = "3";
    private static final String SERVER_TEMPLATE_ID = "testTemplate";
    private static final String DEPLOYMENT_ID = "evaluation_1.0.0-SNAPSHOT";

    private HTML htmlMock;
    private UserTaskSummary userTaskSummary;
    private NodeInstanceSummary nodeInstanceSummary;
    private ProcessInstanceSummary processInstanceSummary;
    private ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView viewMock;

    @Mock
    private ProcessRuntimeDataService processRuntimeDataServiceMock;
    @InjectMocks
    private ProcessInstanceDetailsPresenter presenter;


    @Before
    public void setUp() {
        htmlMock = mock(HTML.class);
        viewMock = mock(ProcessInstanceDetailsViewImpl.class,
                        (Answer) invocationOnMock -> htmlMock);
        presenter.setView(viewMock);
        presenter.setProcessRuntimeDataService(new CallerMock<>(processRuntimeDataServiceMock));
        nodeInstanceSummary = getNodeInstanceSummary();
        when(processRuntimeDataServiceMock.getProcessInstanceActiveNodes(SERVER_TEMPLATE_ID,
                                                                         DEPLOYMENT_ID,
                                                                         Long.parseLong(PROCESS_INSTANCE_ID))).thenReturn(singletonList(nodeInstanceSummary));
        processInstanceSummary = getProcessInstanceSummary();
        when(processRuntimeDataServiceMock.getProcessInstance(eq(SERVER_TEMPLATE_ID),
                                                              any(ProcessInstanceKey.class))).thenReturn(processInstanceSummary);
    }

    @Test
    public void setProcessInstanceDetailsTest() {
        presenter.refreshProcessInstanceDataRemote(DEPLOYMENT_ID,
                                                   PROCESS_INSTANCE_ID,
                                                   SERVER_TEMPLATE_ID);

        verify(htmlMock).setText(processInstanceSummary.getProcessId());
        verify(htmlMock).setText(Constants.INSTANCE.Active());
        verify(htmlMock).setText(processInstanceSummary.getDeploymentId());
        verify(htmlMock).setText(processInstanceSummary.getProcessVersion());
        verify(htmlMock).setText(processInstanceSummary.getCorrelationKey());
        verify(htmlMock).setText(Constants.INSTANCE.No_Parent_Process_Instance());

        ArgumentCaptor<SafeHtml> argumentCaptor = ArgumentCaptor.forClass(SafeHtml.class);
        verify(htmlMock,
               times(2)).setHTML(argumentCaptor.capture());
        List<SafeHtml> safeHtmlList = argumentCaptor.getAllValues();
        assertThat(safeHtmlList).as("Active user tasks & Current Activities are set").hasSize(2);
        assertThat(safeHtmlList.get(0).asString())
                  .as("Active user tasks")
                  .contains(userTaskSummary.getName(),
                            userTaskSummary.getStatus(),
                            userTaskSummary.getOwner());
        assertThat(safeHtmlList.get(1).asString())
                  .as("Current Activities")
                  .contains(nodeInstanceSummary.getTimestamp(),
                            String.valueOf(nodeInstanceSummary.getId()),
                            nodeInstanceSummary.getNodeName(),
                            nodeInstanceSummary.getType());
    }

    private NodeInstanceSummary getNodeInstanceSummary() {
        NodeInstanceSummary nodeInstanceSummary = new NodeInstanceSummary();
        nodeInstanceSummary.setTimestamp("Fri Oct 27 17:47:07 CEST 2017");
        nodeInstanceSummary.setId(1L);
        nodeInstanceSummary.setNodeName("Self Evaluation");
        nodeInstanceSummary.setType("HumanTaskNode");
        return nodeInstanceSummary;
    }

    private ProcessInstanceSummary getProcessInstanceSummary() {
        ProcessInstanceSummary processInstanceSummary = new ProcessInstanceSummary();
        processInstanceSummary.setProcessId(PROCESS_ID);
        processInstanceSummary.setState(ACTIVE_STATE);
        processInstanceSummary.setDeploymentId(DEPLOYMENT_ID);
        processInstanceSummary.setProcessVersion(PROCESS_VERSION);
        processInstanceSummary.setCorrelationKey(PROCESS_INSTANCE_ID);
        processInstanceSummary.setParentId(0L);
        processInstanceSummary.setActiveTasks(singletonList(getUserTaskSummary()));
        return processInstanceSummary;
    }

    private UserTaskSummary getUserTaskSummary() {
        userTaskSummary = new UserTaskSummary(1L,
                                              "Self Evaluation",
                                              "testuser",
                                              "Reserved");
        return userTaskSummary;
    }
}
