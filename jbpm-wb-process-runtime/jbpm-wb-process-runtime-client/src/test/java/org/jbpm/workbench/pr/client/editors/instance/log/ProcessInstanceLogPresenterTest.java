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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.LogUtils;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_DATE;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_ID;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_NODE_NAME;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_NODE_TYPE;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceLogPresenterTest {

    @Mock
    ProcessInstanceLogPresenter.ProcessInstanceLogView view;

    @Mock
    protected ErrorPopupPresenter errorPopup;

    @Mock
    protected DataSetQueryHelper dataSetQueryHelper;

    @Mock
    protected ProcessInstanceLogFilterSettingsManager filterSettingsManager;

    @Mock
    FilterSettings currentFilterSettings;

    @Mock
    private DataSet dataSet;

    @InjectMocks
    ProcessInstanceLogPresenter presenter;

    private String processName = "processName";
    private String testTask = "testTask";
    private String datasetUID = "jbpmProcessInstanceLogs";

    private Date logDate = new Date();
    private String prettyTime = "";
    private String techTime;

    @Before
    public void setup() {

        techTime = DateUtils.getDateTimeStr(logDate);
        defineDatasetAnswer(0,
                            1L,
                            logDate,
                            "",
                            ProcessInstanceLogPresenter.NODE_START,
                            false);

        defineDatasetAnswer(1,
                            1L + 1,
                            logDate,
                            testTask,
                            ProcessInstanceLogPresenter.NODE_HUMAN_TASK,
                            false);
        defineDatasetAnswer(2,
                            1L + 2,
                            logDate,
                            "",
                            ProcessInstanceLogPresenter.NODE_END,
                            true);
        defineDatasetAnswer(3,
                            1L + 3,
                            logDate,
                            "",
                            "Split",
                            false);

        when(dataSet.getRowCount()).thenReturn(4);
        when(dataSet.getUUID()).thenReturn(datasetUID);

        presenter.setDataSetQueryHelper(dataSetQueryHelper);
        presenter.setFilterSettingsManager(filterSettingsManager);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
                return null;
            }
        }).when(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                  any(DataSetReadyCallback.class));

        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(currentFilterSettings);
        when(filterSettingsManager.createDefaultFilterSettingsPrototype(anyLong())).thenReturn(currentFilterSettings);
        when(currentFilterSettings.getKey()).thenReturn("key");
        when(currentFilterSettings.getDataSet()).thenReturn(dataSet);
        presenter.setProcessName(processName);
    }

    @Test
    public void addTechLogLineTest() {

        Optional<String> logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                                        logDate,
                                                                                        "",
                                                                                        ProcessInstanceLogPresenter.NODE_START,
                                                                                        false),
                                                        LogUtils.LogType.TECHNICAL);
        assertEquals(getTechLogCall(techTime,
                                    "StartNode",
                                    "",
                                    false,
                                    true),
                     logLine.get());

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "",
                                                                       ProcessInstanceLogPresenter.NODE_START,
                                                                       true),
                                       LogUtils.LogType.TECHNICAL);
        assertEquals(getTechLogCall(techTime,
                                    "StartNode",
                                    "",
                                    true,
                                    false),
                     logLine.get());

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "testTask",
                                                                       ProcessInstanceLogPresenter.NODE_HUMAN_TASK,
                                                                       false),
                                       LogUtils.LogType.TECHNICAL);
        assertEquals(getTechLogCall(techTime,
                                    "HumanTaskNode",
                                    testTask,
                                    false,
                                    false),
                     logLine.get());

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "testTask",
                                                                       ProcessInstanceLogPresenter.NODE_HUMAN_TASK,
                                                                       true),
                                       LogUtils.LogType.TECHNICAL);
        assertEquals(getTechLogCall(techTime,
                                    "HumanTaskNode",
                                    testTask,
                                    true,
                                    true),
                     logLine.get());

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "",
                                                                       ProcessInstanceLogPresenter.NODE_END,
                                                                       false),
                                       LogUtils.LogType.TECHNICAL);

        assertEquals(getTechLogCall(techTime,
                                    "EndNode",
                                    "",
                                    false,
                                    false),
                     logLine.get());

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "",
                                                                       ProcessInstanceLogPresenter.NODE_END,
                                                                       true),
                                       LogUtils.LogType.TECHNICAL);

        assertEquals(getTechLogCall(techTime,
                                    "EndNode",
                                    "",
                                    true,
                                    false),
                     logLine.get());

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
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

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
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
        Optional<String> logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                                        logDate,
                                                                                        "",
                                                                                        ProcessInstanceLogPresenter.NODE_START,
                                                                                        false),
                                                        LogUtils.LogType.BUSINESS);
        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Process(),
                                        processName,
                                        Constants.INSTANCE.WasStarted()),
                     logLine.get());

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "",
                                                                       ProcessInstanceLogPresenter.NODE_START,
                                                                       true),
                                       LogUtils.LogType.BUSINESS);
        assertEquals(Optional.empty(),
                     logLine);

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "testTask",
                                                                       ProcessInstanceLogPresenter.NODE_HUMAN_TASK,
                                                                       false),
                                       LogUtils.LogType.BUSINESS);
        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Task(),
                                        testTask,
                                        Constants.INSTANCE.WasStarted()),
                     logLine.get());
        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "testTask",
                                                                       ProcessInstanceLogPresenter.NODE_HUMAN_TASK,
                                                                       true),
                                       LogUtils.LogType.BUSINESS);

        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Task(),
                                        testTask,
                                        Constants.INSTANCE.WasCompleted()),
                     logLine.get());

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "",
                                                                       ProcessInstanceLogPresenter.NODE_END,
                                                                       false),
                                       LogUtils.LogType.BUSINESS);
        assertEquals(Optional.empty(),
                     logLine);
        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "",
                                                                       ProcessInstanceLogPresenter.NODE_END,
                                                                       true),
                                       LogUtils.LogType.BUSINESS);
        assertEquals(getBusinessLogCall(prettyTime,
                                        Constants.INSTANCE.Process(),
                                        processName,
                                        Constants.INSTANCE.WasCompleted()),
                     logLine.get());

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "",
                                                                       "Split",
                                                                       false),
                                       LogUtils.LogType.BUSINESS);

        assertEquals(Optional.empty(),
                     logLine);

        logLine = presenter.getLogLine(createProcessInstanceLogSummary(1L,
                                                                       logDate,
                                                                       "",
                                                                       "Split",
                                                                       true),
                                       LogUtils.LogType.BUSINESS);

        assertEquals(Optional.empty(),
                     logLine);
    }

    @Test
    public void refreshProcessInstanceDataAscTechTest() {
        presenter.refreshProcessInstanceData(LogUtils.LogOrder.ASC,
                                             LogUtils.LogType.TECHNICAL);

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
        verify(dataSetQueryHelper).setLastOrderedColumn(COLUMN_LOG_DATE);
        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogs(argumentDESC.capture());

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
    public void refreshProcessInstanceDataDescTechTest() {
        presenter.refreshProcessInstanceData(LogUtils.LogOrder.DESC,
                                             LogUtils.LogType.TECHNICAL);
        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.DESCENDING);
        verify(dataSetQueryHelper).setLastOrderedColumn(COLUMN_LOG_DATE);
        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogs(argumentDESC.capture());
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
    public void refreshProcessInstanceDataAscBusinessTest() {
        presenter.refreshProcessInstanceData(LogUtils.LogOrder.ASC,
                                             LogUtils.LogType.BUSINESS);

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
        verify(dataSetQueryHelper).setLastOrderedColumn(COLUMN_LOG_DATE);
        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogs(argumentDESC.capture());

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
    public void refreshProcessInstanceDataDescBusinessTest() {
        presenter.refreshProcessInstanceData(LogUtils.LogOrder.DESC,
                                             LogUtils.LogType.BUSINESS);

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.DESCENDING);
        verify(dataSetQueryHelper).setLastOrderedColumn(COLUMN_LOG_DATE);
        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogs(argumentDESC.capture());
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

    private String getBusinessLogCall(String time,
                                      String logType,
                                      String logName,
                                      String completed) {
        return ProcessInstanceLogPresenter.LOG_TEMPLATES.getBusinessLog(time,
                                                                        logType,
                                                                        SafeHtmlUtils.fromString(logName),
                                                                        completed).asString();
    }

    private String getTechLogCall(String time,
                                  String logType,
                                  String logName,
                                  boolean completed,
                                  boolean human) {
        return ProcessInstanceLogPresenter.LOG_TEMPLATES.getTechLog(time,
                                                                    logType,
                                                                    SafeHtmlUtils.fromString(logName),
                                                                    (completed ? " " + Constants.INSTANCE.Completed() : ""),
                                                                    (human ? Constants.INSTANCE.Human() : Constants.INSTANCE.System())).asString();
    }

    public void defineDatasetAnswer(int position,
                                    Long id,
                                    Date date,
                                    String nodeName,
                                    String nodeType,
                                    boolean completed) {
        when(dataSet.getValueAt(position,
                                COLUMN_LOG_ID)).thenReturn(id);
        when(dataSet.getValueAt(position,
                                COLUMN_LOG_DATE)).thenReturn(date);
        when(dataSet.getValueAt(position,
                                COLUMN_LOG_NODE_NAME)).thenReturn(nodeName);
        when(dataSet.getValueAt(position,
                                COLUMN_LOG_NODE_TYPE)).thenReturn(nodeType);
        when(dataSet.getValueAt(position,
                                COLUMN_LOG_TYPE)).thenReturn((completed ? 1 : 0));
    }

    private ProcessInstanceLogSummary createProcessInstanceLogSummary(Long id,
                                                                      Date date,
                                                                      String nodeName,
                                                                      String nodeType,
                                                                      boolean completed) {
        return ProcessInstanceLogSummary.builder()
                .id(id)
                .date(date)
                .name(nodeName)
                .nodeType(nodeType)
                .completed(completed).build();
    }

    @Test
    public void datasetLookupNotFoundTest() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).notFound();
                ;
                return null;
            }
        }).when(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                  any(DataSetReadyCallback.class));
        presenter.refreshProcessInstanceData(LogUtils.LogOrder.ASC,
                                             LogUtils.LogType.TECHNICAL);
        verify(errorPopup).showMessage(eq(org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.DataSetNotFound(datasetUID)));
    }

    @Test
    public void datasetLookupErrorTest() {
        String errorMessage = "error message";
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).onError(new ClientRuntimeError(errorMessage));
                return null;
            }
        }).when(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                  any(DataSetReadyCallback.class));
        presenter.refreshProcessInstanceData(LogUtils.LogOrder.ASC,
                                             LogUtils.LogType.TECHNICAL);
        verify(errorPopup).showMessage(eq(org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.DataSetError(datasetUID,
                                                                                                                          errorMessage)));
    }

    @Test
    public void onProcessInstaceSelectionTest() {

        Long processInstanceId = 1L;
        String processDefId = "processDefId";
        String deploymentId = "deploymentId";
        Integer processInstanceStatus = 0;
        String serverTemplateId = "serverTemplateId";

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(deploymentId,
                                                                                    processInstanceId,
                                                                                    processDefId,
                                                                                    processName,
                                                                                    processInstanceStatus,
                                                                                    serverTemplateId));

        verify(view).setActiveLogOrderButton(LogUtils.LogOrder.ASC);
        verify(view).setActiveLogTypeButton(LogUtils.LogType.BUSINESS);
        verify(filterSettingsManager).createDefaultFilterSettingsPrototype(eq(processInstanceId));
        verify(dataSetQueryHelper,
               times(2)).setCurrentTableSettings(filterSettingsManager.createDefaultFilterSettingsPrototype(processInstanceId));

        dataSetQueryHelper.setCurrentTableSettings(filterSettingsManager.createDefaultFilterSettingsPrototype(processInstanceId));

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
        verify(dataSetQueryHelper).setLastOrderedColumn(COLUMN_LOG_DATE);
        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogs(argumentDESC.capture());
        assertEquals(3,
                     argumentDESC.getValue().size());
    }
}
