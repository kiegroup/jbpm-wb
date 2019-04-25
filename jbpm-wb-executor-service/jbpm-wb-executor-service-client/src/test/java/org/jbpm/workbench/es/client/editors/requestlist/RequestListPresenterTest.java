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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.dataset.ErrorHandlerBuilder;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.basic.BasicFilterAddEvent;
import org.jbpm.workbench.common.client.filters.basic.BasicFilterRemoveEvent;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.es.client.editors.events.JobSelectedEvent;
import org.jbpm.workbench.es.client.editors.quicknewjob.NewJobPresenter;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.RequestSummary;
import org.jbpm.workbench.es.model.events.RequestChangedEvent;
import org.jbpm.workbench.es.service.ExecutorService;
import org.jbpm.workbench.es.util.RequestStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;
import org.uberfire.mvp.PlaceRequest;

import static java.util.Collections.*;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.common.client.PerspectiveIds.SEARCH_PARAMETER_JOB_ID;
import static org.jbpm.workbench.common.client.PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID;
import static org.jbpm.workbench.es.client.editors.util.JobUtils.createRequestSummary;
import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RequestListPresenterTest {

    private static final Long REQUEST_ID = 1L;
    private static final String PERSPECTIVE_ID = PerspectiveIds.JOBS;
    private String datasetUId = REQUEST_LIST_DATASET;

    private org.jbpm.workbench.common.client.resources.i18n.Constants commonConstants;

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
    private ListTable<RequestSummary> extendedPagedTable;

    @Mock
    private EventSourceMock<RequestChangedEvent> requestChangedEvent;

    @Mock
    private EventSourceMock<JobSelectedEvent> jobSelectedEventMock;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private UberfireBreadcrumbs breadcrumbs;

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    private PerspectiveActivity perspectiveActivity;

    @Mock
    private ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    @Mock
    private NewJobPresenter newJobPresenterMock;

    @Mock
    private DataSet dataSet;

    @Spy
    private FilterSettings filterSettings;

    @Mock
    private ManagedInstance<ErrorHandlerBuilder> errorHandlerBuilder;

    private RequestListPresenter presenter;

    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        callerMockExecutorService = new CallerMock<ExecutorService>(executorServiceMock);

        filterSettings.setDataSetLookup(dataSetLookup);
        filterSettings.setKey("key");

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(extendedPagedTable.getColumnSortList()).thenReturn(null);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(filterSettings.getUUID()).thenReturn(datasetUId);

        when(serverTemplateSelectorMenuBuilder.getView()).thenReturn(mock(ServerTemplateSelectorMenuBuilder.ServerTemplateSelectorElementView.class));
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);
        when(perspectiveActivity.getIdentifier()).thenReturn(PERSPECTIVE_ID);

        doAnswer((InvocationOnMock invocation) -> {
            ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
            return null;
        }).when(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                  any(DataSetReadyCallback.class));
        commonConstants = org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE;

        when(errorHandlerBuilder.get()).thenReturn(new ErrorHandlerBuilder());

        presenter = new RequestListPresenter(viewMock,
                                             callerMockExecutorService,
                                             dataSetQueryHelper,
                                             requestChangedEvent,
                                             jobSelectedEventMock,
                                             placeManager);
        presenter.setUberfireBreadcrumbs(breadcrumbs);
        presenter.setPerspectiveManager(perspectiveManager);
        presenter.setServerTemplateSelectorMenuBuilder(serverTemplateSelectorMenuBuilder);
        presenter.setNewJobPresenter(newJobPresenterMock);
        presenter.setErrorHandlerBuilder(errorHandlerBuilder);
    }

    @Test
    public void getDataTest() {
        presenter.getData(new Range(0,
                                    5));

        verify(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                 any(DataSetReadyCallback.class));
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
    public void testExistActiveSearchFilters() {
        final PlaceRequest place = mock(PlaceRequest.class);
        presenter.onStartup(place);

        when(place.getParameter(SEARCH_PARAMETER_PROCESS_INSTANCE_ID, null)).thenReturn("1");
        assertTrue(presenter.existActiveSearchFilters());

        when(place.getParameter(SEARCH_PARAMETER_JOB_ID, null)).thenReturn("1");
        assertTrue(presenter.existActiveSearchFilters());

        when(place.getParameter(SEARCH_PARAMETER_PROCESS_INSTANCE_ID, null)).thenReturn(null);
        when(place.getParameter(SEARCH_PARAMETER_JOB_ID, null)).thenReturn(null);
        assertFalse(presenter.existActiveSearchFilters());
    }

    @Test
    public void testDefaultActiveSearchFiltersWithProcessId() {
        final PlaceRequest place = mock(PlaceRequest.class);
        final String processInstanceId = "1";
        when(place.getParameter(SEARCH_PARAMETER_PROCESS_INSTANCE_ID, null)).thenReturn(processInstanceId);
        presenter.onStartup(place);

        presenter.setupActiveSearchFilters();

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(viewMock).addActiveFilter(captor.capture());

        assertEquals(1, captor.getAllValues().size());
        assertEquals(Constants.INSTANCE.Process_Instance_Id(), captor.getValue().getKey());
        assertEquals(Constants.INSTANCE.Process_Instance_Id() + ": " + processInstanceId, captor.getValue().getLabelValue());
        assertEquals(Integer.valueOf(processInstanceId), (captor.getValue().getValue()));
    }

    @Test
    public void testActiveFilterLabelStatus() {
        ColumnFilter testColumFilter = equalsTo(COLUMN_STATUS,
                                                Arrays.asList(RequestStatus.CANCELLED.name(),
                                                              RequestStatus.RUNNING.name()));

        ActiveFilterItem activeFilterItem = presenter.getActiveFilterFromColumnFilter(testColumFilter);

        assertEquals(Constants.INSTANCE.Status(), activeFilterItem.getKey());
        assertEquals("Status: Canceled, Running", activeFilterItem.getLabelValue());
        assertNotEquals(testColumFilter.toString(), activeFilterItem.getLabelValue());
    }

    @Test
    public void testActiveFilterLabelOther() {
        ColumnFilter testColumFilter = equalsTo(COLUMN_PROCESS_INSTANCE_ID, 1);
        ActiveFilterItem activeFilterItem = presenter.getActiveFilterFromColumnFilter(testColumFilter);
        assertEquals(COLUMN_PROCESS_INSTANCE_ID, activeFilterItem.getKey());
        assertEquals(testColumFilter.toString(), activeFilterItem.getLabelValue());

        testColumFilter = equalsTo(COLUMN_BUSINESSKEY, "key");
        activeFilterItem = presenter.getActiveFilterFromColumnFilter(testColumFilter);
        assertEquals(COLUMN_BUSINESSKEY, activeFilterItem.getKey());
        assertEquals(testColumFilter.toString(), activeFilterItem.getLabelValue());
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
        presenter.selectSummaryItem(job);

        verify(placeManager).goTo(PerspectiveIds.JOB_DETAILS_SCREEN);
        final ArgumentCaptor<JobSelectedEvent> captor = ArgumentCaptor.forClass(JobSelectedEvent.class);
        verify(jobSelectedEventMock).fire(captor.capture());
        assertJobSelectedEventContent(captor.getValue(),
                                      job.getDeploymentId(),
                                      job.getId());
        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(Constants.INSTANCE.JobBreadcrumb(job.getId())),
                                          eq(Commands.DO_NOTHING));
    }

    @Test
    public void testJobSelectionWithDetailsOpen() {
        RequestSummary job = createRequestSummary();
        presenter.selectSummaryItem(job);

        verify(placeManager,
               never()).goTo(any(PlaceRequest.class));

        final ArgumentCaptor<JobSelectedEvent> captor = ArgumentCaptor.forClass(JobSelectedEvent.class);
        verify(jobSelectedEventMock).fire(captor.capture());
        assertJobSelectedEventContent(captor.getValue(),
                                      job.getDeploymentId(),
                                      job.getId());
    }

    @Test
    public void testOpenNewJobDialog_serverTemplateNull() {
        presenter.setSelectedServerTemplate(null);

        assertNotNull(presenter.getNewJobCommand());

        presenter.getNewJobCommand().execute();

        verify(viewMock).displayNotification("SelectServerTemplate");
        verify(newJobPresenterMock,
               never()).openNewJobDialog(anyString());
    }

    @Test
    public void testOpenNewJobDialog_serverTemplateEmpty() {
        assertTrue(presenter.getSelectedServerTemplate().isEmpty());

        assertNotNull(presenter.getNewJobCommand());

        presenter.getNewJobCommand().execute();

        verify(viewMock).displayNotification("SelectServerTemplate");
        verify(newJobPresenterMock,
               never()).openNewJobDialog(anyString());
    }

    @Test
    public void testOpenNewJobDialog_serverTemplateSet() {
        final String serverTemplateTest = "serverTemplateTest";
        presenter.setSelectedServerTemplate(new ServerTemplate(serverTemplateTest,
                                                               null,
                                                               singletonList(Capability.PROCESS.name()),
                                                               emptyMap(),
                                                               emptyList()));

        assertNotNull(presenter.getNewJobCommand());

        presenter.getNewJobCommand().execute();
        assertEquals(serverTemplateTest,
                     presenter.getSelectedServerTemplate());
        verify(newJobPresenterMock).openNewJobDialog(serverTemplateTest);
        verify(viewMock,
               times(3)).getListGrid();
        verify(viewMock).clearBlockingError();
        verifyNoMoreInteractions(viewMock);
    }

    @Test
    public void testListBreadcrumbCreation() {
        presenter.createListBreadcrumb();
        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);
        verify(breadcrumbs).clearBreadcrumbs(PERSPECTIVE_ID);
        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(commonConstants.Home()),
                                          captureCommand.capture());

        captureCommand.getValue().execute();
        verify(placeManager).goTo(PerspectiveIds.HOME);

        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(commonConstants.Manage_Jobs()),
                                          eq(Commands.DO_NOTHING));

        verifyNoMoreInteractions(breadcrumbs);
    }

    @Test
    public void testSetupDetailBreadcrumb() {
        String detailLabel = "detailLabel";
        String detailScreenId = "screenId";

        PlaceManager placeManagerMock = mock(PlaceManager.class);
        presenter.setPlaceManager(placeManagerMock);
        presenter.setupDetailBreadcrumb(placeManagerMock,
                                        commonConstants.Manage_Jobs(),
                                        detailLabel,
                                        detailScreenId);

        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);

        verify(breadcrumbs).clearBreadcrumbs(PERSPECTIVE_ID);
        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(commonConstants.Home()),
                                          captureCommand.capture());
        captureCommand.getValue().execute();
        verify(placeManagerMock).goTo(PerspectiveIds.HOME);

        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(commonConstants.Manage_Jobs()),
                                          captureCommand.capture());

        captureCommand.getValue().execute();
        verify(placeManagerMock).closePlace(detailScreenId);

        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(detailLabel),
                                          eq(Commands.DO_NOTHING));
    }

    private void assertJobSelectedEventContent(JobSelectedEvent event,
                                               String deploymentId,
                                               Long jobId) {
        assertEquals(jobId,
                     event.getJobId());
        assertEquals(deploymentId,
                     event.getDeploymentId());
    }

    @Test
    public void testOnBasicFilterAddEvent() {
        final ActiveFilterItem<Object> filter = new ActiveFilterItem<>("key1",
                                                                       null,
                                                                       null,
                                                                       null,
                                                                       null);
        final ColumnFilter columnFilter = mock(ColumnFilter.class);
        presenter.onBasicFilterAddEvent(new BasicFilterAddEvent(datasetUId,
                                                                filter,
                                                                columnFilter));

        verify(viewMock).addActiveFilter(filter);
        verify(filterSettings).addColumnFilter(columnFilter);
    }

    @Test
    public void testOnBasicFilterRemoveEvent() {
        final ActiveFilterItem<Object> filter = new ActiveFilterItem<>("key1",
                                                                       null,
                                                                       null,
                                                                       null,
                                                                       null);
        final ColumnFilter columnFilter = mock(ColumnFilter.class);
        presenter.onBasicFilterRemoveEvent(new BasicFilterRemoveEvent(datasetUId,
                                                                      filter,
                                                                      columnFilter));

        verify(viewMock).removeActiveFilter(filter);
        verify(filterSettings).removeColumnFilter(columnFilter);
    }

    @Test
    public void bulkCancelJobsDependingOnStatusTest() {
        final String serverTemplateTest = "serverTemplateTest";
        presenter.setSelectedServerTemplate(new ServerTemplate(serverTemplateTest, null));
        String deploymentId = "deploymentId";
        String key = "key";
        Long jobId_1 = new Random().nextLong();
        Long jobId_2 = jobId_1 + 1;
        Long jobId_3 = jobId_2 + 1;

        ArrayList<RequestSummary> requestSummaries = new ArrayList<>();
        requestSummaries.add(createRequestSummary(jobId_1,
                                                  key,
                                                  deploymentId,
                                                  RequestStatus.QUEUED));
        requestSummaries.add(createRequestSummary(jobId_2,
                                                  key,
                                                  deploymentId,
                                                  RequestStatus.RUNNING));
        requestSummaries.add(createRequestSummary(jobId_3,
                                                  key,
                                                  deploymentId,
                                                  RequestStatus.ERROR));

        presenter.bulkCancel(requestSummaries);

        verify(executorServiceMock).cancelRequest(anyString(),
                                                  anyString(),
                                                  eq(jobId_1));
        verify(executorServiceMock).cancelRequest(anyString(),
                                                  anyString(),
                                                  eq(jobId_2));
        verify(executorServiceMock,
               never()).cancelRequest(anyString(),
                                      anyString(),
                                      eq(jobId_3));
        verify(viewMock).displayNotification(Constants.INSTANCE.RequestCanceled(jobId_1));
        verify(viewMock).displayNotification(Constants.INSTANCE.RequestCanceled(jobId_2));
        verify(viewMock).displayNotification(Constants.INSTANCE.Job_Can_Not_Be_Cancelled(jobId_3));
    }

    @Test
    public void bulkRequeueJobsDependingOnStatusTest() {
        final String serverTemplateTest = "serverTemplateTest";
        presenter.setSelectedServerTemplate(new ServerTemplate(serverTemplateTest, null));
        String deploymentId = "deploymentId";
        String key = "key";
        Long jobId_1 = new Random().nextLong();
        Long jobId_2 = jobId_1 + 1;
        Long jobId_3 = jobId_2 + 1;

        ArrayList<RequestSummary> requestSummaries = new ArrayList<>();
        requestSummaries.add(createRequestSummary(jobId_1,
                                                  key,
                                                  deploymentId,
                                                  RequestStatus.ERROR));
        requestSummaries.add(createRequestSummary(jobId_2,
                                                  key,
                                                  deploymentId,
                                                  RequestStatus.RUNNING));
        requestSummaries.add(createRequestSummary(jobId_3,
                                                  key,
                                                  deploymentId,
                                                  RequestStatus.QUEUED));

        presenter.bulkRequeue(requestSummaries);

        verify(executorServiceMock).requeueRequest(anyString(),
                                                   anyString(),
                                                   eq(jobId_1));
        verify(executorServiceMock).requeueRequest(anyString(),
                                                   anyString(),
                                                   eq(jobId_2));
        verify(executorServiceMock,
               never()).requeueRequest(anyString(),
                                       anyString(),
                                       eq(jobId_3));
        verify(viewMock).displayNotification(Constants.INSTANCE.RequestRequeued(jobId_1));
        verify(viewMock).displayNotification(Constants.INSTANCE.RequestRequeued(jobId_2));
        verify(viewMock).displayNotification(Constants.INSTANCE.Job_Can_Not_Be_Requeued(jobId_3));
    }
}