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
package org.jbpm.workbench.es.client.editors.errorlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.DataSet;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.base.DataSetQueryHelper;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.jbpm.workbench.es.model.events.ExecErrorChangedEvent;
import org.jbpm.workbench.es.service.ExecutorService;
import org.jbpm.workbench.es.util.ExecutionErrorType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.dashbuilder.dataset.sort.SortOrder.ASCENDING;
import static org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ExecutionErrorListPresenterTest {

    private CallerMock<ExecutorService> callerMockExecutorService;

    @Mock
    private ExecutorService executorServiceMock;

    @Mock
    private ExecutionErrorListViewImpl viewMock;

    @Mock
    private DataSetQueryHelper dataSetQueryHelper;

    @Mock
    private ExtendedPagedTable<ExecutionErrorSummary> extendedPagedTable;

    @Mock
    private EventSourceMock<ExecErrorChangedEvent> execErrorChangedEvent;

    @Spy
    private FilterSettings filterSettings;

    @InjectMocks
    private ExecutionErrorListPresenter presenter;

    private static ExecutionErrorSummary createTestError(int id) {
        return ExecutionErrorSummary.builder()
                .errorId(id + "")
                .error(id + "_stackTrace")
                .acknowledged(false)
                .acknowledgedAt(new Date())
                .acknowledgedBy("testUser")
                .activityId(Long.valueOf(id + 20))
                .activityName(id + "_Act_name")
                .errorDate(new Date())
                .type(ExecutionErrorType.TASK)
                .deploymentId(id + "_deployment")
                .processInstanceId(Long.valueOf(id))
                .processId(id + "_processId")
                .jobId(Long.valueOf(id))
                .message(id + "_message").build();
    }

    @Before
    public void setupMocks() {
        callerMockExecutorService = new CallerMock<>(executorServiceMock);

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(extendedPagedTable.getColumnSortList()).thenReturn(null);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);

        presenter.setExecutorServices(callerMockExecutorService);
    }

    @Test
    public void getDataTest() {
        presenter.setAddingDefaultFilters(false);
        presenter.getData(new Range(0,
                                    5));

        verify(dataSetQueryHelper).setLastSortOrder(ASCENDING);
        verify(viewMock).hideBusyIndicator();
    }

    @Test
    public void acknowledgeErrorTest() {
        final String errorId = "errorId";
        final String deploymentId = "deploymentId";

        presenter.acknowledgeExecutionError(errorId,
                                            deploymentId);

        verify(executorServiceMock).acknowledgeError(anyString(),
                                                     eq(deploymentId),
                                                     eq(errorId));
    }

    @Test
    public void bulkAcknowledgeTest() {
        String error1_ID = "error1";
        String error2_ID = "error2";
        String error3_ID = "error3";
        String error1_deploymentId = "deploymentId_1";
        String error2_deploymentId = "deploymentId_2";
        String error3_deploymentId = "deploymentId_3";
        final ArrayList<ExecutionErrorSummary> testErrors =
                new ArrayList<>(
                        Arrays.asList(
                                ExecutionErrorSummary.builder().errorId(error1_ID).deploymentId(error1_deploymentId).acknowledged(false).build(),
                                ExecutionErrorSummary.builder().errorId(error2_ID).deploymentId(error2_deploymentId).acknowledged(false).build(),
                                ExecutionErrorSummary.builder().errorId(error3_ID).deploymentId(error3_deploymentId).acknowledged(true).build()
                        ));

        presenter.bulkAcknowledge(testErrors);

        verify(executorServiceMock).acknowledgeError(anyString(),
                                                     eq(error1_deploymentId),
                                                     eq(error1_ID));
        verify(executorServiceMock).acknowledgeError(anyString(),
                                                     eq(error2_deploymentId),
                                                     eq(error2_ID));
        verifyNoMoreInteractions(executorServiceMock);
    }

    @Test
    public void testGetExecutionErrorSummary() {
        final String errorId = "errorId";
        final String errorType = "Process";
        final String deploymentId = "deploymentId";
        final Long processInsId = 1L;
        final String processId = "processId";
        final Long activityId = 1L;
        final String activityName = "activityName";
        final Long jobId = 1L;
        final String errorMessage = "errorMessage";
        final boolean ack = false;
        final String ackBy = "ackBy";
        final Date ackAt = new Date();
        final Date errorDate = new Date();

        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class),
                                                     eq(COLUMN_ERROR_ID),
                                                     eq(0))).thenReturn(errorId);
        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class),
                                                     eq(COLUMN_ERROR_TYPE),
                                                     eq(0))).thenReturn(errorType);
        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class),
                                                     eq(COLUMN_DEPLOYMENT_ID),
                                                     eq(0))).thenReturn(deploymentId);

        when(dataSetQueryHelper.getColumnLongValue(any(DataSet.class),
                                                   eq(COLUMN_PROCESS_INST_ID),
                                                   eq(0))).thenReturn(processInsId);
        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class),
                                                     eq(COLUMN_PROCESS_ID),
                                                     eq(0))).thenReturn(processId);
        when(dataSetQueryHelper.getColumnLongValue(any(DataSet.class),
                                                   eq(COLUMN_ACTIVITY_ID),
                                                   eq(0))).thenReturn(activityId);
        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class),
                                                     eq(COLUMN_ACTIVITY_NAME),
                                                     eq(0))).thenReturn(activityName);
        when(dataSetQueryHelper.getColumnLongValue(any(DataSet.class),
                                                   eq(COLUMN_JOB_ID),
                                                   eq(0))).thenReturn(jobId);
        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class),
                                                     eq(COLUMN_ERROR_MSG),
                                                     eq(0))).thenReturn(errorMessage);
        when(dataSetQueryHelper.getColumnBooleanValue(any(DataSet.class),
                                                      eq(COLUMN_ERROR_ACK),
                                                      eq(0))).thenReturn(ack);
        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class),
                                                     eq(COLUMN_ERROR_ACK_BY),
                                                     eq(0))).thenReturn(ackBy);
        when(dataSetQueryHelper.getColumnDateValue(any(DataSet.class),
                                                   eq(COLUMN_ERROR_ACK_AT),
                                                   eq(0))).thenReturn(ackAt);
        when(dataSetQueryHelper.getColumnDateValue(any(DataSet.class),
                                                   eq(COLUMN_ERROR_DATE),
                                                   eq(0))).thenReturn(errorDate);

        final ExecutionErrorSummary es = presenter.createExecutionErrorSummaryFromDataSet(mock(DataSet.class),
                                                                                          0);

        assertEquals(errorMessage,
                     es.getErrorMessage());
        assertEquals(errorType,
                     es.getType().getType());
        assertEquals(errorMessage,
                     es.getErrorMessage());
        assertEquals(ackAt,
                     es.getAcknowledgedAt());
        assertEquals(ackBy,
                     es.getAcknowledgedBy());
        assertEquals(ack,
                     es.isAcknowledged());
        assertEquals(activityId,
                     es.getActivityId());
        assertEquals(activityName,
                     es.getActivityName());
        assertEquals(deploymentId,
                     es.getDeploymentId());
        assertEquals(errorId,
                     es.getErrorId());
        assertEquals(processId,
                     es.getProcessId());
        assertEquals(processInsId,
                     es.getProcessInstanceId());
        assertEquals(jobId,
                     es.getJobId());
    }

}