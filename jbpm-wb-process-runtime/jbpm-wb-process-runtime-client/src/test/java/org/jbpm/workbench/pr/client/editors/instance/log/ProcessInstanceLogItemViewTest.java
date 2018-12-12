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

package org.jbpm.workbench.pr.client.editors.instance.log;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceLogItemViewTest {

    @Mock
    private MouseEvent mouseEvent;

    @Mock
    private ProcessInstanceLogPresenter presenter;

    @Mock
    private Constants constants;

    @Mock
    private Span logTime;

    @Mock
    protected Span logInfo;

    @Mock
    protected Span logIcon;

    @Mock
    protected Span logTypeDesc;

    @Mock
    private Div detailsPanelDiv;

    @Mock
    private Anchor detailsLink;

    @Mock
    private Div detailsInfoDiv;

    @Mock
    private TranslationService translationService;

    @Mock
    private DataBinder<ProcessInstanceLogSummary> logSummary;

    @Mock
    private ProcessInstanceLogItemDetailsView workItemView;

    @InjectMocks
    private ProcessInstanceLogItemView view;

    private static final Date date = Date.from(LocalDateTime.of(2018, 12, 12, 11, 55, 57).atZone(ZoneId.systemDefault()).toInstant());

    @Before
    public void setupMocks() {
        when(constants.Human_Task()).thenReturn("Human task");
        when(constants.System_Task()).thenReturn("System task");
        when(constants.NodeWasLeft(any())).then(i -> "(" + i.getArgumentAt(0, String.class) + ") node was COMPLETED");
        when(constants.NodeWasEntered(any())).then(i -> "(" + i.getArgumentAt(0, String.class) + ")" + " node was ENTERED");
        when(constants.Task_(any())).then(i -> "Task '" + i.getArgumentAt(0, String.class) + "'");
        when(constants.Human()).thenReturn("Human");
        when(constants.System()).thenReturn("System");

        when(translationService.format(any())).then(i -> i.getArgumentAt(0, String.class));
    }

    @Test
    public void testCompletedHumanTask() {
        ProcessInstanceLogSummary model = createModel("HumanTaskNode", "userTask", true, true);

        view.setValue(model);

        verify(logIcon).setAttribute("data-original-title", "Human task");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-user kie-timeline-icon--completed");
        verify(logInfo).setTextContent("(Human) node was COMPLETED");
        verify(logTypeDesc).setTextContent("Task 'userTask'");
        verify(detailsPanelDiv).setHidden(false);

        verifyHumanTaskDetails(model);
    }

    @Test
    public void testNotCompletedHumanTask() {
        ProcessInstanceLogSummary model = createModel("HumanTaskNode", "userTask", false, true);

        view.setValue(model);

        verify(logIcon).setAttribute("data-original-title", "Human task");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-user");
        verify(logInfo).setTextContent("(Human) node was ENTERED");
        verify(logTypeDesc).setTextContent("Task 'userTask'");
        verify(detailsPanelDiv).setHidden(false);

        verifyHumanTaskDetails(model);
    }

    @Test
    public void testCompletedWorkItemNode() {
        ProcessInstanceLogSummary model = createModel("WorkItemNode", "workItemTask", true, true);

        view.setValue(model);

        verify(logIcon).setAttribute("data-original-title", "System task");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-cogs kie-timeline-icon--completed");
        verify(logInfo).setTextContent("(System) node was COMPLETED");
        verify(logTypeDesc).setTextContent("WorkItemNode 'workItemTask' ");
        verify(detailsPanelDiv).setHidden(false);

        verifySystemTaskDetails(model);
    }

    @Test
    public void testNotCompletedWorkItemNode() {
        ProcessInstanceLogSummary model = createModel("WorkItemNode", "workItemTask", false, true);

        view.setValue(model);

        verify(logIcon).setAttribute("data-original-title", "System task");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-cogs");
        verify(logInfo).setTextContent("(System) node was ENTERED");
        verify(logTypeDesc).setTextContent("WorkItemNode 'workItemTask' ");
        verify(detailsPanelDiv).setHidden(false);

        verifySystemTaskDetails(model);
    }

    @Test
    public void testCompletedStartNode() {
        ProcessInstanceLogSummary model = createModel("StartNode", "startNode", true, false);

        view.setValue(model);

        verify(logIcon).setAttribute("data-original-title", "System task");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-cogs kie-timeline-icon--completed");
        verify(logInfo).setTextContent("(System) node was COMPLETED");
        verify(logTypeDesc).setTextContent("StartNode 'startNode' ");
        verify(detailsPanelDiv).setHidden(true);

        verifyNoDetails(model);
    }

    @Test
    public void testNotCompletedStartNode() {
        ProcessInstanceLogSummary model = createModel("StartNode", "startNode", false, false);

        view.setValue(model);

        verify(logIcon).setAttribute("data-original-title", "Human task");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-user");
        verify(logInfo).setTextContent("(Human) node was ENTERED");
        verify(logTypeDesc).setTextContent("StartNode 'startNode' ");
        verify(detailsPanelDiv).setHidden(true);

        verifyNoDetails(model);
    }

    private static Date createDate(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return Date.from(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second).atZone(ZoneId.systemDefault()).toInstant());
    }

    private ProcessInstanceLogSummary createModel(String nodeType, String name, boolean completed, boolean hasWorkItem) {
        Long workItemId = hasWorkItem ? 2L : null;

        return ProcessInstanceLogSummary.builder()
                .id(1L)
                .name(name)
                .date(date)
                .nodeId("2")
                .workItemId(workItemId)
                .nodeType(nodeType)
                .completed(completed)
                .nodeContainerId("userTask")
                .logDeploymentId("_5CEF0690-F864-4E0D-B03B-0E3288172D5D")
                .build();
    }

    private void verifyHumanTaskDetails(ProcessInstanceLogSummary model) {
        verify(detailsLink).setAttribute("href", "#11544612157000");
        verify(detailsInfoDiv).setId("11544612157000");

        when(logSummary.getModel()).thenReturn(model);
        when(detailsInfoDiv.hasChildNodes()).thenReturn(false);
        view.loadProcessInstanceLogsDetails(mouseEvent);
        verify(presenter).loadTaskDetails(2L, date, workItemView);
    }

    private void verifySystemTaskDetails(ProcessInstanceLogSummary model) {
        verify(detailsLink).setAttribute("href", "#11544612157000");
        verify(detailsInfoDiv).setId("11544612157000");

        when(logSummary.getModel()).thenReturn(model);
        when(detailsInfoDiv.hasChildNodes()).thenReturn(false);
        view.loadProcessInstanceLogsDetails(mouseEvent);
        verify(presenter).loadWorkItemDetails(model.getWorkItemId(), workItemView);
    }

    private void verifyNoDetails(ProcessInstanceLogSummary model) {
        verify(detailsLink, never()).setAttribute("href", "#11544612157000");
        verify(detailsInfoDiv, never()).setId("11544612157000");

        when(logSummary.getModel()).thenReturn(model);
        when(detailsInfoDiv.hasChildNodes()).thenReturn(false);
        view.loadProcessInstanceLogsDetails(mouseEvent);
        verify(presenter, never()).loadTaskDetails(2L, date, workItemView);
        verify(presenter, never()).loadWorkItemDetails(model.getWorkItemId(), workItemView);
    }
}