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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.common.client.dataset.ErrorHandlerBuilder;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.active.ClearAllActiveFiltersEvent;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.LogUtils;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.WorkItemParameterSummary;
import org.jbpm.workbench.pr.model.WorkItemSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.uberfire.mocks.CallerMock;

import static java.util.Collections.emptyList;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceLogPresenterTest {

    @Mock
    ProcessInstanceLogPresenter.ProcessInstanceLogView view;

    @Mock
    DataSetQueryHelper logsDataSetQueryHelper;

    @Mock
    ProcessInstanceLogFilterSettingsManager filterSettingsManager;

    @Mock
    ProcessInstanceLogBasicFiltersPresenter processInstanceLogBasicFiltersPresenter;

    @Mock
    FilterSettings currentFilterSettings;

    @Mock
    DataSet dataSet;

    @Mock
    TaskService taskServiceMock;

    Caller<TaskService> taskService;

    @Mock
    ManagedInstance<ErrorHandlerBuilder> errorHandlerBuilder;

    @Mock
    DefaultWorkbenchErrorCallback errorCallback;

    @Spy
    ErrorHandlerBuilder errorHandler;

    @Mock
    ProcessRuntimeDataService processRuntimeDataServiceMock;

    Caller<ProcessRuntimeDataService> processRuntimeDataService;

    @InjectMocks
    ProcessInstanceLogPresenter presenter;

    String processName = "processName";
    String testTask = "testTask";
    String datasetUID = "jbpmProcessInstanceLogs";

    Date logDate = new Date();

    Long[] pilIds = new Long[4];
    String[] pilNodeType = new String[4];
    String[] pilNodeNames = new String[4];
    Boolean[] pilCompleted = new Boolean[4];

    @Before
    public void setup() {
        pilIds[0] = 1L;
        pilIds[1] = 1L + 1;
        pilIds[2] = 1L + 2;
        pilIds[3] = 1l + 3;

        pilNodeNames[0] = "";
        pilNodeNames[1] = testTask;
        pilNodeNames[2] = "";
        pilNodeNames[3] = "";

        pilNodeType[0] = LogUtils.NODE_TYPE_START;
        pilNodeType[1] = LogUtils.NODE_TYPE_HUMAN_TASK;
        pilNodeType[2] = LogUtils.NODE_TYPE_END;
        pilNodeType[3] = "Split";

        pilCompleted[0] = false;
        pilCompleted[1] = false;
        pilCompleted[2] = true;
        pilCompleted[3] = false;

        for (int i = 0; i < pilIds.length; i++) {
            defineDatasetAnswer(i,
                                pilIds[i],
                                logDate,
                                pilNodeNames[i],
                                pilNodeType[i],
                                pilCompleted[i]);
        }

        when(dataSet.getRowCount()).thenReturn(4);
        when(dataSet.getUUID()).thenReturn(datasetUID);

        presenter.setDataSetQueryHelper(logsDataSetQueryHelper);
        presenter.setFilterSettingsManager(filterSettingsManager);
        presenter.setProcessInstanceLogBasicFiltersPresenter(processInstanceLogBasicFiltersPresenter);
        doAnswer((InvocationOnMock invocation) -> {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
                return null;
        }).when(logsDataSetQueryHelper).lookupDataSet(anyInt(),
                                                      any(DataSetReadyCallback.class));

        when(logsDataSetQueryHelper.getCurrentTableSettings()).thenReturn(currentFilterSettings);
        when(filterSettingsManager.createDefaultFilterSettingsPrototype(anyLong())).thenReturn(currentFilterSettings);
        when(currentFilterSettings.getKey()).thenReturn("key");
        when(currentFilterSettings.getDataSet()).thenReturn(dataSet);
        when(currentFilterSettings.getDataSetLookup()).thenReturn(mock(DataSetLookup.class));

        taskService = new CallerMock<>(taskServiceMock);
        presenter.setTaskService(taskService);
        when(errorHandlerBuilder.get()).thenReturn(errorHandler);
        doNothing().when(errorHandler).showErrorMessage(any());
        errorHandler.setErrorCallback(errorCallback);
        processRuntimeDataService = new CallerMock<>(processRuntimeDataServiceMock);
        presenter.setProcessRuntimeDataService(processRuntimeDataService);
    }

    private void assertProcessInstanceLogContent(Long id,
                                                 Date date,
                                                 String nodeName,
                                                 String nodeType,
                                                 boolean completed,
                                                 ProcessInstanceLogSummary processInstanceLogSummaryDest) {
        assertEquals(id,
                     processInstanceLogSummaryDest.getId());
        assertEquals(date,
                     processInstanceLogSummaryDest.getDate());
        assertEquals(nodeName,
                     processInstanceLogSummaryDest.getName());
        assertEquals(nodeType,
                     processInstanceLogSummaryDest.getNodeType());
        assertEquals(completed,
                     processInstanceLogSummaryDest.isCompleted());
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

    @Test
    public void dataSetLookupNotFoundTest() {
        doAnswer((InvocationOnMock invocation) -> {
                ((DataSetReadyCallback) invocation.getArguments()[1]).notFound();
                return null;
        }).when(logsDataSetQueryHelper).lookupDataSet(anyInt(),
                                                      any(DataSetReadyCallback.class));

        presenter.setProcessInstance(new ProcessInstanceSummary());

        verify(errorHandler).notFound();
    }

    @Test
    public void dataSetLookupErrorTest() {
        final ClientRuntimeError error = new ClientRuntimeError("error message");
        doAnswer((InvocationOnMock invocation) -> {
            ((DataSetReadyCallback) invocation.getArguments()[1]).onError(error);
                return null;
        }).when(logsDataSetQueryHelper).lookupDataSet(anyInt(),
                                                      any(DataSetReadyCallback.class));

        presenter.setProcessInstance(new ProcessInstanceSummary());

        verify(errorHandler).onError(error);
        verify(errorCallback).error(error.getThrowable());
    }

    @Test
    public void testLoadMoreProcessInstanceLogs() {
        presenter.setCurrentPage(0);
        presenter.setProcessInstance(new ProcessInstanceSummary());
        assertEquals(0,
                     presenter.getCurrentPage());
        verify(logsDataSetQueryHelper).lookupDataSet(eq(presenter.getPageSize() * presenter.getCurrentPage()),
                                                     any());
        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogsList(argumentDESC.capture());
        assertEquals(4,
                     argumentDESC.getValue().size());
        verify(view).hideLoadButton(true);

        presenter.loadMoreProcessInstanceLogs();

        assertEquals(1,
                     presenter.getCurrentPage());
        verify(logsDataSetQueryHelper).lookupDataSet(eq(presenter.getPageSize() * presenter.getCurrentPage()),
                                                     any());
        verify(view,
               times(2)).setLogsList(argumentDESC.capture());
        assertEquals(8,
                     argumentDESC.getValue().size());

        verify(view,
               times(2)).setLogsList(anyList());
    }

    @Test
    public void testLoadTaskDetails() {
        Long workItemId = 1L;
        String containerId = "deploymentId";
        String serverTemplateId = "server-template-id";

        TaskSummary task = TaskSummary.builder().id(workItemId).actualOwner("owner").createdOn(new Date()).description("description").build();
        ProcessInstanceLogItemDetailsView humanTaskView = mock(ProcessInstanceLogItemDetailsView.class);
        when(taskServiceMock.getTaskByWorkItemId(serverTemplateId,
                                                 containerId,
                                                 workItemId)).thenReturn(task);

        presenter.setProcessInstance(ProcessInstanceSummary.builder().withServerTemplateId(serverTemplateId).withDeploymentId(containerId).withProcessInstanceId(1l).build());
        presenter.loadTaskDetails(workItemId,
                                  logDate,
                                  humanTaskView);

        verify(taskServiceMock).getTaskByWorkItemId(serverTemplateId,
                                                    containerId,
                                                    workItemId);
        verify(humanTaskView).setTaskDetailsData(task,
                                                 logDate);
    }

    @Test
    public void testLoadWorkItemDetails() {
        Long workItemId = 1L;
        Long processInstanceId = 2L;
        String containerId = "deploymentId";
        String serverTemplateId = "server-template-id";
        WorkItemParameterSummary param1 = new WorkItemParameterSummary("param1",
                                                                       "value1");
        WorkItemParameterSummary param2 = new WorkItemParameterSummary("param2",
                                                                       "value2");

        WorkItemSummary workItemSummary =
                WorkItemSummary.builder()
                        .id(workItemId)
                        .name("Dynamic Task")
                        .parameters(Arrays.asList(param1,
                                                  param2)).build();

        ProcessInstanceLogItemDetailsView workItemView = mock(ProcessInstanceLogItemDetailsView.class);
        when(processRuntimeDataServiceMock.getWorkItemByProcessInstanceId(serverTemplateId,
                                                                          containerId,
                                                                          processInstanceId,
                                                                          workItemId)).thenReturn(workItemSummary);

        presenter.setProcessInstance(ProcessInstanceSummary.builder().withServerTemplateId(serverTemplateId).withDeploymentId(containerId).withProcessInstanceId(processInstanceId).build());
        presenter.loadWorkItemDetails(workItemId,
                                      workItemView);

        verify(processRuntimeDataServiceMock).getWorkItemByProcessInstanceId(serverTemplateId,
                                                                             containerId,
                                                                             processInstanceId,
                                                                             workItemId);
        verify(workItemView).setDetailsData(workItemSummary);
    }

    @Test
    public void onProcessInstanceSelectionTest() {
        Long processInstanceId = 1L;
        String processDefId = "processDefId";
        String deploymentId = "deploymentId";
        Integer processInstanceStatus = 0;
        String serverTemplateId = "serverTemplateId";
        when(logsDataSetQueryHelper.getCurrentTableSettings()).thenReturn(currentFilterSettings);

        presenter.setProcessInstance(ProcessInstanceSummary
                                             .builder()
                                             .withServerTemplateId(serverTemplateId)
                                             .withDeploymentId(deploymentId)
                                             .withProcessInstanceId(processInstanceId)
                                             .withProcessId(processDefId)
                                             .withProcessName(processName)
                                             .withState(processInstanceStatus)
                                             .build());

        verify(view,
               never()).addActiveFilter(any(ActiveFilterItem.class));
        verify(currentFilterSettings).setServerTemplateId(serverTemplateId);
        verify(currentFilterSettings).setTablePageSize(presenter.getPageSize());
        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(view).setLogsList(argumentDESC.capture());
        assertEquals(4,
                     argumentDESC.getValue().size());

        for (int i = 0; i < argumentDESC.getValue().size(); i++) {
            assertProcessInstanceLogContent(pilIds[i],
                                            logDate,
                                            pilNodeNames[i],
                                            pilNodeType[i],
                                            pilCompleted[i],
                                            (ProcessInstanceLogSummary) argumentDESC.getValue().get(i));
        }
    }

    @Test
    public void processInstanceSelectionDefaultFiltersAdditionTest() {
        String serverTemplateId = "serverTemplateId";
        DataSetQueryHelper dataSetQueryHelperSpy = spy(DataSetQueryHelper.class);
        doAnswer((InvocationOnMock invocation) -> {
            ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
            return null;
        }).when(dataSetQueryHelperSpy).lookupDataSet(anyInt(),
                                                     any(DataSetReadyCallback.class));
        presenter.setDataSetQueryHelper(dataSetQueryHelperSpy);
        presenter.setProcessInstance(ProcessInstanceSummary.builder().withServerTemplateId(serverTemplateId).withDeploymentId("deploymentId").withProcessInstanceId(1L).build());
        verify(processInstanceLogBasicFiltersPresenter).onClearAllActiveFiltersEvent(any(ClearAllActiveFiltersEvent.class));
        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(view,
               times(2)).addActiveFilter(captor.capture());

        final ActiveFilterItem nodeTypeFilterItem = captor.getAllValues().get(0);
        assertNotNull(nodeTypeFilterItem);
        assertEquals(Constants.INSTANCE.EventNodeType(),
                     nodeTypeFilterItem.getKey());
        List values = (List) nodeTypeFilterItem.getValue();
        assertEquals(8,
                     values.size());
        assertEquals(LogUtils.NODE_TYPE_START,
                     values.get(0));
        assertEquals(LogUtils.NODE_TYPE_END,
                     values.get(1));
        assertEquals(LogUtils.NODE_TYPE_HUMAN_TASK,
                     values.get(2));
        assertEquals(LogUtils.NODE_TYPE_ACTION,
                     values.get(3));
        assertEquals(LogUtils.NODE_TYPE_MILESTONE,
                     values.get(4));
        assertEquals(LogUtils.NODE_TYPE_SUBPROCESS,
                     values.get(5));
        assertEquals(LogUtils.NODE_TYPE_RULE_SET,
                     values.get(6));
        assertEquals(LogUtils.NODE_TYPE_WORK_ITEM,
                     values.get(7));
        final ActiveFilterItem typeFilterItem = captor.getAllValues().get(1);
        assertNotNull(typeFilterItem);
        assertEquals(Constants.INSTANCE.EventType(),
                     typeFilterItem.getKey());
        assertEquals(emptyList(),
                     typeFilterItem.getValue());

        verify(currentFilterSettings,
               times(2)).setServerTemplateId(serverTemplateId);
        verify(currentFilterSettings,
               times(2)).setTablePageSize(presenter.getPageSize());
    }

    @Test
    public void testDefaultActiveSearchFilters() {
        presenter.setupActiveSearchFilters();

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(view,
               times(2)).addActiveFilter(captor.capture());

        final ActiveFilterItem nodeTypefilterItem = captor.getAllValues().get(0);
        assertNotNull(nodeTypefilterItem);
        assertEquals(Constants.INSTANCE.EventNodeType(),
                     nodeTypefilterItem.getKey());
        List values = (List) nodeTypefilterItem.getValue();
        assertEquals(8,
                     values.size());
        assertEquals(LogUtils.NODE_TYPE_START,
                     values.get(0));
        assertEquals(LogUtils.NODE_TYPE_END,
                     values.get(1));
        assertEquals(LogUtils.NODE_TYPE_HUMAN_TASK,
                     values.get(2));
        assertEquals(LogUtils.NODE_TYPE_ACTION,
                     values.get(3));
        assertEquals(LogUtils.NODE_TYPE_MILESTONE,
                     values.get(4));
        assertEquals(LogUtils.NODE_TYPE_SUBPROCESS,
                     values.get(5));
        assertEquals(LogUtils.NODE_TYPE_RULE_SET,
                     values.get(6));
        assertEquals(LogUtils.NODE_TYPE_WORK_ITEM,
                     values.get(7));

        final ActiveFilterItem typeFilterItem = captor.getAllValues().get(1);
        assertNotNull(typeFilterItem);
        assertEquals(Constants.INSTANCE.EventType(),
                     typeFilterItem.getKey());
        assertEquals(emptyList(),
                     typeFilterItem.getValue());
    }
}
