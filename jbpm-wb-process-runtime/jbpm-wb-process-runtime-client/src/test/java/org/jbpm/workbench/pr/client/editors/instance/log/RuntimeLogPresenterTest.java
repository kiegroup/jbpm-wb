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

package org.jbpm.workbench.pr.client.editors.instance.log;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.LogUtils;
import org.jbpm.workbench.pr.model.RuntimeLogSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RuntimeLogPresenterTest {

    @Mock
    RuntimeLogPresenter.RuntimeLogView view;

    Caller<ProcessRuntimeDataService> processRuntimeDataServiceCaller;

    @Mock
    ProcessRuntimeDataService processRuntimeDataService;

    @InjectMocks
    RuntimeLogPresenter presenter;

    private String serverTemplateId = "serverTemplateId";
    private String deploymentId = "deploymentId";
    private String processName = "processName";
    private String testTask = "testTask";
    private Long processInstanceId = 1L;

    private Date logDate = new Date();
    private String prettyTime = "";
    private String techTime;

    @Before
    public void setup() {
        processRuntimeDataServiceCaller = new CallerMock<ProcessRuntimeDataService>(processRuntimeDataService);
        presenter.setProcessRuntimeDataService(processRuntimeDataServiceCaller);

        techTime = DateUtils.getDateTimeStr(logDate);
        RuntimeLogSummary logs0 = new RuntimeLogSummary(1L,
                                                        logDate,
                                                        "",
                                                        RuntimeLogPresenter.NODE_START,
                                                        false);

        RuntimeLogSummary logs1 = new RuntimeLogSummary(1L,
                                                        logDate,
                                                        testTask,
                                                        RuntimeLogPresenter.NODE_HUMAN_TASK,
                                                        false);
        RuntimeLogSummary logs2 = new RuntimeLogSummary(1L,
                                                        logDate,
                                                        "",
                                                        RuntimeLogPresenter.NODE_END,
                                                        true);
        RuntimeLogSummary logs3 = new RuntimeLogSummary(1L,
                                                        logDate,
                                                        "",
                                                        "Split",
                                                        false);

        when(processRuntimeDataService.getProcessInstanceLogs(serverTemplateId,
                                                              deploymentId,
                                                              processInstanceId)).thenReturn(Arrays.asList(logs0,
                                                                                                           logs1,
                                                                                                           logs2,
                                                                                                           logs3));

        presenter.setServerTemplateId(serverTemplateId);
        presenter.setProcessName(processName);
        presenter.setDeploymentId(deploymentId);
        presenter.setProcessInstanceId(processInstanceId);
    }

    @Test
    public void addTechLogLineTest() {

        Optional<String> logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                                              logDate,
                                                                              "",
                                                                              RuntimeLogPresenter.NODE_START,
                                                                              false),
                                                        LogUtils.LogType.TECHNICAL);
        assertEquals(getTechLogCall(techTime,
                                    "StartNode",
                                    "",
                                    false,
                                    true),
                     logLine.get());

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "",
                                                             RuntimeLogPresenter.NODE_START,
                                                             true),
                                       LogUtils.LogType.TECHNICAL);
        assertEquals(getTechLogCall(techTime,
                                    "StartNode",
                                    "",
                                    true,
                                    false),
                     logLine.get());

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "testTask",
                                                             RuntimeLogPresenter.NODE_HUMAN_TASK,
                                                             false),
                                       LogUtils.LogType.TECHNICAL);
        assertEquals(getTechLogCall(techTime,
                                    "HumanTaskNode",
                                    testTask,
                                    false,
                                    false),
                     logLine.get());

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "testTask",
                                                             RuntimeLogPresenter.NODE_HUMAN_TASK,
                                                             true),
                                       LogUtils.LogType.TECHNICAL);
        assertEquals(getTechLogCall(techTime,
                                    "HumanTaskNode",
                                    testTask,
                                    true,
                                    true),
                     logLine.get());

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "",
                                                             RuntimeLogPresenter.NODE_END,
                                                             false),
                                       LogUtils.LogType.TECHNICAL);

        assertEquals(getTechLogCall(techTime,
                                    "EndNode",
                                    "",
                                    false,
                                    false),
                     logLine.get());

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "",
                                                             RuntimeLogPresenter.NODE_END,
                                                             true),
                                       LogUtils.LogType.TECHNICAL);

        assertEquals(getTechLogCall(techTime,
                                    "EndNode",
                                    "",
                                    true,
                                    false),
                     logLine.get());

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "",
                                                             "Split",
                                                             false),
                                       LogUtils.LogType.TECHNICAL);

        assertEquals(getTechLogCall(techTime,
                                    "Split",
                                    "",
                                    false,
                                    false),
                     logLine.get());

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "",
                                                             "Split",
                                                             true),
                                       LogUtils.LogType.TECHNICAL);

        assertEquals(getTechLogCall(techTime,
                                    "Split",
                                    "",
                                    true,
                                    false),
                     logLine.get());
    }

    @Test
    public void addBusinessLoglineTest() {

        presenter.setProcessName(processName);
        Optional<String> logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                                              logDate,
                                                                              "",
                                                                              RuntimeLogPresenter.NODE_START,
                                                                              false),
                                                        LogUtils.LogType.BUSINESS);
        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Process(),
                                        processName,
                                        Constants.INSTANCE.WasStarted()),
                     logLine.get());

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "",
                                                             RuntimeLogPresenter.NODE_START,
                                                             true),
                                       LogUtils.LogType.BUSINESS);
        assertEquals(Optional.empty(),
                     logLine);

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "testTask",
                                                             RuntimeLogPresenter.NODE_HUMAN_TASK,
                                                             false),
                                       LogUtils.LogType.BUSINESS);
        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Task(),
                                        testTask,
                                        Constants.INSTANCE.WasStarted()),
                     logLine.get());
        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "testTask",
                                                             RuntimeLogPresenter.NODE_HUMAN_TASK,
                                                             true),
                                       LogUtils.LogType.BUSINESS);

        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Task(),
                                        testTask,
                                        Constants.INSTANCE.WasCompleted()),
                     logLine.get());

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "",
                                                             RuntimeLogPresenter.NODE_END,
                                                             false),
                                       LogUtils.LogType.BUSINESS);
        assertEquals(Optional.empty(),
                     logLine);
        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "",
                                                             RuntimeLogPresenter.NODE_END,
                                                             true),
                                       LogUtils.LogType.BUSINESS);
        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Process(),
                                        processName,
                                        Constants.INSTANCE.WasCompleted()),
                     logLine.get());

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "",
                                                             "Split",
                                                             false),
                                       LogUtils.LogType.BUSINESS);

        assertEquals(Optional.empty(),
                     logLine);

        logLine = presenter.getLogLine(new RuntimeLogSummary(1L,
                                                             logDate,
                                                             "",
                                                             "Split",
                                                             true),
                                       LogUtils.LogType.BUSINESS);

        assertEquals(Optional.empty(),
                     logLine);
    }

    @Test
    public void refreshProcessInstanceDataDescTechTest() {
        presenter.refreshProcessInstanceData(LogUtils.LogOrder.DESC,
                                             LogUtils.LogType.TECHNICAL);

        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogs(argumentDESC.capture());
        verify(processRuntimeDataService).getProcessInstanceLogs(serverTemplateId,
                                                                 deploymentId,
                                                                 processInstanceId);

        assertEquals(4,
                     argumentDESC.getValue().size());
        assertEquals(getTechLogCall(techTime,
                                    "StartNode",
                                    "",
                                    false,
                                    true),
                     argumentDESC.getValue().get(0));
        assertEquals(getTechLogCall(techTime,
                                    "HumanTaskNode",
                                    testTask,
                                    false,
                                    false),
                     argumentDESC.getValue().get(1));
        assertEquals(getTechLogCall(techTime,
                                    "EndNode",
                                    "",
                                    true,
                                    false),
                     argumentDESC.getValue().get(2));
        assertEquals(getTechLogCall(techTime,
                                    "Split",
                                    "",
                                    false,
                                    false),
                     argumentDESC.getValue().get(3));
    }

    @Test
    public void refreshProcessInstanceDataAscTechTest() {
        presenter.refreshProcessInstanceData(LogUtils.LogOrder.ASC,
                                             LogUtils.LogType.TECHNICAL);

        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogs(argumentDESC.capture());
        verify(processRuntimeDataService).getProcessInstanceLogs(serverTemplateId,
                                                                 deploymentId,
                                                                 processInstanceId);

        assertEquals(4,
                     argumentDESC.getValue().size());
        assertEquals(getTechLogCall(techTime,
                                    "Split",
                                    "",
                                    false,
                                    false),
                     argumentDESC.getValue().get(0));
        assertEquals(getTechLogCall(techTime,
                                    "EndNode",
                                    "",
                                    true,
                                    false),
                     argumentDESC.getValue().get(1));
        assertEquals(getTechLogCall(techTime,
                                    "HumanTaskNode",
                                    testTask,
                                    false,
                                    false),
                     argumentDESC.getValue().get(2));
        assertEquals(getTechLogCall(techTime,
                                    "StartNode",
                                    "",
                                    false,
                                    true),
                     argumentDESC.getValue().get(3));
    }

    @Test
    public void refreshProcessInstanceDataDescBusinessTest() {
        presenter.refreshProcessInstanceData(LogUtils.LogOrder.DESC,
                                             LogUtils.LogType.BUSINESS);

        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogs(argumentDESC.capture());
        verify(processRuntimeDataService).getProcessInstanceLogs(serverTemplateId,
                                                                 deploymentId,
                                                                 processInstanceId);

        assertEquals(3,
                     argumentDESC.getValue().size());

        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Process(),
                                        processName,
                                        Constants.INSTANCE.WasStarted()),
                     argumentDESC.getValue().get(0));

        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Task(),
                                        testTask,
                                        Constants.INSTANCE.WasStarted()),
                     argumentDESC.getValue().get(1));
        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Process(),
                                        processName,
                                        Constants.INSTANCE.WasCompleted()),
                     argumentDESC.getValue().get(2));
    }

    @Test
    public void refreshProcessInstanceDataAscBusinessTest() {
        presenter.refreshProcessInstanceData(LogUtils.LogOrder.ASC,
                                             LogUtils.LogType.BUSINESS);

        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogs(argumentDESC.capture());
        verify(processRuntimeDataService).getProcessInstanceLogs(serverTemplateId,
                                                                 deploymentId,
                                                                 processInstanceId);

        assertEquals(3,
                     argumentDESC.getValue().size());
        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Process(),
                                        processName,
                                        Constants.INSTANCE.WasCompleted()),
                     argumentDESC.getValue().get(0));
        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Task(),
                                        testTask,
                                        Constants.INSTANCE.WasStarted()),
                     argumentDESC.getValue().get(1));
        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Process(),
                                        processName,
                                        Constants.INSTANCE.WasStarted()),
                     argumentDESC.getValue().get(2));
    }

    private String getBusinessLogCall(String time,
                                      String logType,
                                      String logName,
                                      String completed) {
        return RuntimeLogPresenter.LOG_TEMPLATES.getBusinessLog(time,
                                                                logType,
                                                                SafeHtmlUtils.fromString(logName),
                                                                completed).asString();
    }

    private String getTechLogCall(String time,
                                  String logType,
                                  String logName,
                                  boolean completed,
                                  boolean human) {
        return RuntimeLogPresenter.LOG_TEMPLATES.getTechLog(time,
                                                            logType,
                                                            SafeHtmlUtils.fromString(logName),
                                                            (completed ? " " + Constants.INSTANCE.Completed() : ""),
                                                            (human ? Constants.INSTANCE.Human() : Constants.INSTANCE.System())).asString();
    }
}
