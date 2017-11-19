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
package org.jbpm.workbench.es.client.editors.requestlist;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.es.client.editors.events.JobSelectedEvent;
import org.jbpm.workbench.es.client.editors.jobdetails.JobDetailsPresenter;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.RequestSummary;
import org.jbpm.workbench.es.model.events.RequestChangedEvent;
import org.jbpm.workbench.es.service.ExecutorService;
import org.jbpm.workbench.es.util.RequestStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.dashbuilder.dataset.sort.SortOrder.ASCENDING;
import static org.jbpm.workbench.es.client.editors.util.JobUtils.createRequestSummary;
import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RequestListPresenterTest {

    private static final Long REQUEST_ID = 1L;

    private CallerMock<ExecutorService> callerMockExecutorService;

    @Mock
    private ExecutorService executorServiceMock;

    @Mock
    private RequestListViewImpl viewMock;

    @Mock
    private DataSetQueryHelper dataSetQueryHelper;

    @Spy
    private DataSetLookup dataSetLookup;

    @Mock
    private ExtendedPagedTable<RequestSummary> extendedPagedTable;

    @Mock
    private EventSourceMock<RequestChangedEvent> requestChangedEvent;

    @Mock
    private EventSourceMock<JobSelectedEvent> jobSelectedEventMock;

    @Mock
    private PlaceManager placeManager;

    @Spy
    private FilterSettings filterSettings;

    private RequestListPresenter presenter;

    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        callerMockExecutorService = new CallerMock<ExecutorService>(executorServiceMock);

        filterSettings.setDataSetLookup(dataSetLookup);

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(extendedPagedTable.getColumnSortList()).thenReturn(null);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(viewMock.getAdvancedSearchFilterSettings()).thenReturn(filterSettings);

        presenter = new RequestListPresenter(viewMock,
                                             callerMockExecutorService,
                                             dataSetQueryHelper,
                                             requestChangedEvent,
                                             jobSelectedEventMock,
                                             placeManager);
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
    public void cancelRequestTest() {
        presenter.cancelRequest(null,
                                REQUEST_ID);

        verify(requestChangedEvent,
               times(1)).fire(any(RequestChangedEvent.class));
        verify(executorServiceMock).cancelRequest(anyString(),
                                                  eq(null),
                                                  eq(REQUEST_ID));
    }

    @Test
    public void requeueRequestTest() {
        presenter.requeueRequest(null,
                                 REQUEST_ID);

        verify(requestChangedEvent,
               times(1)).fire(any(RequestChangedEvent.class));
        verify(executorServiceMock).requeueRequest(anyString(),
                                                   eq(null),
                                                   eq(REQUEST_ID));
    }

    @Test
    public void cancelRequestTestWithDeploymentId() {
        presenter.cancelRequest("test",
                                REQUEST_ID);

        verify(requestChangedEvent,
               times(1)).fire(any(RequestChangedEvent.class));
        verify(executorServiceMock).cancelRequest(anyString(),
                                                  eq("test"),
                                                  eq(REQUEST_ID));
    }

    @Test
    public void requeueRequestTestWithDeploymentId() {
        presenter.requeueRequest("test",
                                 REQUEST_ID);

        verify(requestChangedEvent,
               times(1)).fire(any(RequestChangedEvent.class));
        verify(executorServiceMock).requeueRequest(anyString(),
                                                   eq("test"),
                                                   eq(REQUEST_ID));
    }

    @Test
    public void testGetRequestSummary() {
        final Long id = 1l;
        final String message = "message";
        final String status = "DONE";
        final String commandName = "commandName";
        final String businessKey = "businessKey";
        final Integer retries = 2;
        final Integer executions = 1;
        final Date time = new Date();
        final String processName = "myProcessName";
        final Long processInstanceId = Long.valueOf(33);
        final String processInstanceDescription = "myProcessInstanceDescription";
        final String deploymentId = "test";

        final DataSet dataSet = mock(DataSet.class);

        when(dataSet.getValueAt(0,
                                COLUMN_ID)).thenReturn(id);
        when(dataSet.getValueAt(0,
                                COLUMN_TIMESTAMP)).thenReturn(time);
        when(dataSet.getValueAt(0,
                                COLUMN_STATUS)).thenReturn(status);
        when(dataSet.getValueAt(0,
                                COLUMN_COMMANDNAME)).thenReturn(commandName);
        when(dataSet.getValueAt(0,
                                COLUMN_MESSAGE)).thenReturn(message);
        when(dataSet.getValueAt(0,
                                COLUMN_BUSINESSKEY)).thenReturn(businessKey);
        when(dataSet.getValueAt(0,
                                COLUMN_RETRIES)).thenReturn(retries);
        when(dataSet.getValueAt(0,
                                COLUMN_EXECUTIONS)).thenReturn(executions);
        when(dataSet.getValueAt(0,
                                COLUMN_PROCESS_NAME)).thenReturn(processName);
        when(dataSet.getValueAt(0,
                                COLUMN_PROCESS_INSTANCE_ID)).thenReturn(processInstanceId);
        when(dataSet.getValueAt(0,
                                COLUMN_PROCESS_INSTANCE_DESCRIPTION)).thenReturn(processInstanceDescription);
        when(dataSet.getValueAt(0,
                                COLUMN_JOB_DEPLOYMENT_ID)).thenReturn(deploymentId);

        final RequestSummary rs = presenter.getRequestSummary(dataSet,
                                                              0);

        assertEquals(id,
                     rs.getId());
        assertEquals(time,
                     rs.getTime());
        assertEquals(RequestStatus.DONE,
                     rs.getStatus());
        assertEquals(commandName,
                     rs.getCommandName());
        assertEquals(message,
                     rs.getMessage());
        assertEquals(businessKey,
                     rs.getKey());
        assertEquals(retries,
                     rs.getRetries());
        assertEquals(executions,
                     rs.getExecutions());
        assertEquals(processName,
                     rs.getProcessName());
        assertEquals(processInstanceId,
                     rs.getProcessInstanceId());
        assertEquals(processInstanceDescription,
                     rs.getProcessInstanceDescription());
        assertEquals(deploymentId,
                     rs.getDeploymentId());
    }

    @Test
    public void testDefaultActiveSearchFilters() {
        presenter.setupDefaultActiveSearchFilters();

        verify(viewMock).addActiveFilter(eq(Constants.INSTANCE.Status()),
                                         eq(Constants.INSTANCE.Running()),
                                         eq(RequestStatus.RUNNING.name()),
                                         any(Consumer.class));
    }

    @Test
    public void testActiveSearchFilters() {
        final PlaceRequest place = mock(PlaceRequest.class);
        when(place.getParameter(anyString(),
                                anyString())).thenReturn(null);
        presenter.onStartup(place);

        presenter.setupActiveSearchFilters();

        verify(viewMock).addActiveFilter(eq(Constants.INSTANCE.Status()),
                                         eq(Constants.INSTANCE.Running()),
                                         eq(RequestStatus.RUNNING.name()),
                                         any(Consumer.class));
    }

    @Test
    public void testStatusActionConditionPredicates() {
        final RequestStatus[] CANCEL_ALLOW_STATUSES = new RequestStatus[]{
                RequestStatus.QUEUED,
                RequestStatus.RETRYING,
                RequestStatus.RUNNING
        };
        final RequestStatus[] REQUEUE_ALLOW_STATUSES = new RequestStatus[]{
                RequestStatus.ERROR,
                RequestStatus.RUNNING
        };
        RequestSummary testJob = new RequestSummary();
        for (RequestStatus status : RequestStatus.values()) {
            testJob.setStatus(status);
            assertEquals(Arrays.asList(CANCEL_ALLOW_STATUSES).contains(status),
                         presenter.getCancelActionCondition().test(testJob));
            assertEquals(Arrays.asList(REQUEUE_ALLOW_STATUSES).contains(status),
                         presenter.getRequeueActionCondition().test(testJob));
        }
    }

    @Test
    public void testViewProcessActionConditionPredicates() {
        RequestSummary testJob = new RequestSummary();
        testJob.setProcessInstanceId(33L);
        assertTrue(presenter.getViewProcessActionCondition().test(testJob));
        testJob.setProcessInstanceId(null);
        assertFalse(presenter.getViewProcessActionCondition().test(testJob));
        assertFalse(presenter.getViewProcessActionCondition().test(new RequestSummary()));
    }

    @Test
    public void testJobSelectionWithDetailsClosed() {
        RequestSummary job = createRequestSummary();
        boolean closed = true;
        when(placeManager.getStatus(any(DefaultPlaceRequest.class))).thenReturn(PlaceStatus.CLOSE);
        presenter.selectJob(job,
                            closed);

        verify(placeManager).goTo(JobDetailsPresenter.SCREEN_ID);
        final ArgumentCaptor<JobSelectedEvent> captor = ArgumentCaptor.forClass(JobSelectedEvent.class);
        verify(jobSelectedEventMock).fire(captor.capture());
        assertJobSelectedEventContent(captor.getValue(),
                                      job.getDeploymentId(),
                                      job.getId());
    }

    @Test
    public void testJobSelectionWithDetailsOpen() {
        RequestSummary job = createRequestSummary();
        boolean closed = false;
        when(placeManager.getStatus(any(DefaultPlaceRequest.class))).thenReturn(PlaceStatus.OPEN);
        presenter.selectJob(job,
                            closed);

        verify(placeManager,
               never()).goTo(any(PlaceRequest.class));

        final ArgumentCaptor<JobSelectedEvent> captor = ArgumentCaptor.forClass(JobSelectedEvent.class);
        verify(jobSelectedEventMock).fire(captor.capture());
        assertJobSelectedEventContent(captor.getValue(),
                                      job.getDeploymentId(),
                                      job.getId());
    }

    @Test
    public void testCloseDetails() {
        RequestSummary job = createRequestSummary();
        boolean closed = true;
        when(placeManager.getStatus(any(DefaultPlaceRequest.class))).thenReturn(PlaceStatus.OPEN);
        presenter.selectJob(job,
                            closed);

        verify(placeManager,
               never()).goTo(anyString());
        verify(jobSelectedEventMock,
               never()).fire(any());
        verify(placeManager).closePlace(JobDetailsPresenter.SCREEN_ID);
    }

    private void assertJobSelectedEventContent(JobSelectedEvent event,
                                               String deploymentId,
                                               Long jobId) {
        assertEquals(jobId,
                     event.getJobId());
        assertEquals(deploymentId,
                     event.getDeploymentId());
    }
}