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
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.basic.BasicFilterAddEvent;
import org.jbpm.workbench.common.client.filters.basic.BasicFilterRemoveEvent;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.es.client.editors.errordetails.ExecutionErrorDetailsPresenter;
import org.jbpm.workbench.es.client.editors.events.ExecutionErrorSelectedEvent;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.jbpm.workbench.es.service.ExecutorService;
import org.jbpm.workbench.es.util.ExecutionErrorType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.jbpm.workbench.common.client.PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID;
import static org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants.*;
import static org.junit.Assert.*;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ExecutionErrorListPresenterTest {

    private static final String PERSPECTIVE_ID = PerspectiveIds.EXECUTION_ERRORS;
    private org.jbpm.workbench.common.client.resources.i18n.Constants commonConstants;

    private CallerMock<ExecutorService> callerMockExecutorService;

    @Mock
    private ExecutorService executorServiceMock;

    @Mock
    private ExecutionErrorListViewImpl viewMock;

    @Mock
    private DataSetQueryHelper dataSetQueryHelper;

    @Mock
    private ListTable<ExecutionErrorSummary> extendedPagedTable;

    @Mock
    private EventSourceMock<ExecutionErrorSelectedEvent> executionErrorSelectedEventMock;

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
    private AuthorizationManager authorizationManager;

    @Mock
    private User identity;

    @Spy
    private FilterSettings filterSettings;

    @Spy
    private DataSetLookup dataSetLookup;

    @Mock
    private DataSet dataSet;

    @InjectMocks
    private ExecutionErrorListPresenter presenter;

    @Before
    public void setupMocks() {
        callerMockExecutorService = new CallerMock<>(executorServiceMock);

        filterSettings.setDataSetLookup(dataSetLookup);
        filterSettings.setKey("key");

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(extendedPagedTable.getColumnSortList()).thenReturn(null);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(serverTemplateSelectorMenuBuilder.getView()).thenReturn(mock(ServerTemplateSelectorMenuBuilder.ServerTemplateSelectorElementView.class));
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);
        when(perspectiveActivity.getIdentifier()).thenReturn(PERSPECTIVE_ID);

        doAnswer((InvocationOnMock invocation) -> {
            ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
            return null;
        }).when(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                  any(DataSetReadyCallback.class));
        commonConstants = org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE;
        presenter.setExecutorService(callerMockExecutorService);
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
        final Short ack = 0;
        final String ackBy = "ackBy";
        final Date ackAt = new Date();
        final Date errorDate = new Date();

        final DataSet dataSet = mock(DataSet.class);

        when(dataSet.getValueAt(0,
                                COLUMN_ERROR_ID)).thenReturn(errorId);
        when(dataSet.getValueAt(0,
                                COLUMN_ERROR_TYPE)).thenReturn(errorType);
        when(dataSet.getValueAt(0,
                                COLUMN_DEPLOYMENT_ID)).thenReturn(deploymentId);
        when(dataSet.getValueAt(0,
                                COLUMN_PROCESS_INST_ID)).thenReturn(processInsId);
        when(dataSet.getValueAt(0,
                                COLUMN_PROCESS_ID)).thenReturn(processId);
        when(dataSet.getValueAt(0,
                                COLUMN_ACTIVITY_ID)).thenReturn(activityId);
        when(dataSet.getValueAt(0,
                                COLUMN_ACTIVITY_NAME)).thenReturn(activityName);
        when(dataSet.getValueAt(0,
                                COLUMN_JOB_ID)).thenReturn(jobId);
        when(dataSet.getValueAt(0,
                                COLUMN_ERROR_MSG)).thenReturn(errorMessage);
        when(dataSet.getValueAt(0,
                                COLUMN_ERROR_ACK)).thenReturn(ack);
        when(dataSet.getValueAt(0,
                                COLUMN_ERROR_ACK_BY)).thenReturn(ackBy);
        when(dataSet.getValueAt(0,
                                COLUMN_ERROR_ACK_AT)).thenReturn(ackAt);
        when(dataSet.getValueAt(0,
                                COLUMN_ERROR_DATE)).thenReturn(errorDate);

        final ExecutionErrorSummary es = presenter.createExecutionErrorSummaryFromDataSet(dataSet,
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
        assertEquals(Boolean.valueOf(ack != 0),
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

    @Test
    public void testDefaultActiveSearchFilters() {
        presenter.setupDefaultActiveSearchFilters();

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(viewMock).addActiveFilter(captor.capture());

        final ActiveFilterItem filterItem = captor.getValue();
        assertNotNull(filterItem);
        assertEquals(Constants.INSTANCE.Acknowledged(),
                     filterItem.getKey());
        assertEquals("0",
                     filterItem.getValue());
        assertEquals(Constants.INSTANCE.Acknowledged() + ": " + commonConstants.No(),
                     filterItem.getLabelValue());
    }

    @Test
    public void testActiveSearchFilters() {
        final PlaceRequest place = mock(PlaceRequest.class);
        when(place.getParameter(anyString(),
                                anyString())).thenReturn(null);
        presenter.onStartup(place);

        presenter.setupActiveSearchFilters();

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(viewMock).addActiveFilter(captor.capture());

        final ActiveFilterItem filterItem = captor.getValue();
        assertNotNull(filterItem);
        assertEquals(Constants.INSTANCE.Acknowledged(),
                     filterItem.getKey());
        assertEquals("0",
                     filterItem.getValue());
        assertEquals(Constants.INSTANCE.Acknowledged() + ": " + commonConstants.No(),
                     filterItem.getLabelValue());
    }

    @Test
    public void testActiveSearchFiltersProcessInstanceId() {
        final PlaceRequest place = mock(PlaceRequest.class);
        final String processInstanceId = "1";
        when(place.getParameter(SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                                null)).thenReturn(processInstanceId);
        presenter.onStartup(place);

        presenter.setupActiveSearchFilters();

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(viewMock).addActiveFilter(captor.capture());

        final ActiveFilterItem filterItem = captor.getValue();
        assertNotNull(filterItem);
        assertEquals(Constants.INSTANCE.Process_Instance_Id(),
                     filterItem.getKey());
        assertEquals(processInstanceId,
                     filterItem.getValue());
        assertEquals(Constants.INSTANCE.Process_Instance_Id() + ": " + processInstanceId,
                     filterItem.getLabelValue());
    }

    @Test
    public void testGoToJobErrorSummary() {
        String jobId = "1";

        presenter.goToJob(ExecutionErrorSummary.builder().jobId(Long.valueOf(jobId)).build());

        final ArgumentCaptor<PlaceRequest> captor = ArgumentCaptor.forClass(PlaceRequest.class);
        verify(placeManager).goTo(captor.capture());
        final PlaceRequest request = captor.getValue();
        assertEquals(JOBS,
                     request.getIdentifier());
        assertEquals(jobId,
                     request.getParameter(PerspectiveIds.SEARCH_PARAMETER_JOB_ID,
                                          null));
    }

    @Test
    public void testGoProcessInstanceErrorSummary() {

        ExecutionErrorSummary errorSummary =
                ExecutionErrorSummary.builder()
                        .deploymentId("test_depId")
                        .processInstanceId(Long.valueOf("1"))
                        .processId("test_processId")
                        .build();

        presenter.goToProcessInstance(errorSummary);
        final ArgumentCaptor<PlaceRequest> captor = ArgumentCaptor.forClass(PlaceRequest.class);
        verify(placeManager).goTo(captor.capture());
        final PlaceRequest request = captor.getValue();
        assertEquals(PROCESS_INSTANCES,
                     request.getIdentifier());
        assertEquals(errorSummary.getProcessInstanceId().toString(),
                     request.getParameter(PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                                          null));
    }

    @Test
    public void testViewJobsActionCondition() {
        doAnswer(new PerspectiveAnswer(JOBS)).when(authorizationManager).authorize(any(ResourceRef.class),
                                                                                   eq(identity));

        assertTrue(presenter.getViewJobActionCondition().test(ExecutionErrorSummary.builder().jobId(Long.valueOf(1)).build()));

        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(false);

        assertFalse(presenter.getViewJobActionCondition().test(new ExecutionErrorSummary()));
    }

    @Test
    public void testViewProcessInstanceActionCondition() {
        doAnswer(new PerspectiveAnswer(PROCESS_INSTANCES)).when(authorizationManager).authorize(any(ResourceRef.class),
                                                                                                eq(identity));

        assertTrue(presenter.getViewProcessInstanceActionCondition().test(ExecutionErrorSummary.builder().processInstanceId(Long.valueOf(1)).build()));

        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(false);

        assertFalse(presenter.getViewProcessInstanceActionCondition().test(new ExecutionErrorSummary()));
    }

    @Test
    public void testGoTaskErrorSummary() {
        doAnswer(new PerspectiveAnswer(TASKS_ADMIN)).when(authorizationManager).authorize(any(ResourceRef.class),
                                                                                          eq(identity));

        ExecutionErrorSummary errorSummary =
                ExecutionErrorSummary.builder()
                        .activityId(Long.valueOf("1"))
                        .build();

        presenter.goToTask(errorSummary);

        final ArgumentCaptor<PlaceRequest> captor = ArgumentCaptor.forClass(PlaceRequest.class);
        verify(placeManager).goTo(captor.capture());
        final PlaceRequest request = captor.getValue();
        assertEquals(TASKS_ADMIN,
                     request.getIdentifier());
        assertEquals(errorSummary.getActivityId().toString(),
                     request.getParameter(PerspectiveIds.SEARCH_PARAMETER_TASK_ID,
                                          null));
    }

    @Test
    public void testViewTaskActionCondition() {
        doAnswer(new PerspectiveAnswer(TASKS_ADMIN)).when(authorizationManager).authorize(any(ResourceRef.class),
                                                                                          eq(identity));

        assertTrue(presenter.getViewTaskActionCondition().test(ExecutionErrorSummary.builder()
                                                                       .type(ExecutionErrorType.TASK)
                                                                       .activityId(Long.valueOf(1)).build()));

        assertFalse(presenter.getViewProcessInstanceActionCondition().test(new ExecutionErrorSummary()));
        assertFalse(presenter.getViewProcessInstanceActionCondition().test(ExecutionErrorSummary.builder()
                                                                                   .type(ExecutionErrorType.TASK).build()));
        assertFalse(presenter.getViewTaskActionCondition().test(ExecutionErrorSummary.builder()
                                                                        .type(ExecutionErrorType.PROCESS)
                                                                        .activityId(Long.valueOf(1)).build()));
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(false);

        assertFalse(presenter.getViewTaskActionCondition().test(ExecutionErrorSummary.builder()
                                                                        .type(ExecutionErrorType.TASK)
                                                                        .activityId(Long.valueOf(1)).build()));
    }

    @Test
    public void testOnSelectionEvent() {
        String deploymentId = "evaluation.1.0.1";
        String errorId = "testErrorId";
        String processId = "Evaluation";
        Long processInstanceId = Long.valueOf(1);
        ExecutionErrorSummary errorSummary =
                ExecutionErrorSummary.builder()
                        .errorId(errorId)
                        .deploymentId(deploymentId)
                        .processId(processId)
                        .processInstanceId(processInstanceId)
                        .build();
        presenter.setExecutionErrorSelectedEvent(executionErrorSelectedEventMock);
        presenter.selectExecutionError(errorSummary);
        final ArgumentCaptor<ExecutionErrorSelectedEvent> captor = ArgumentCaptor.forClass(ExecutionErrorSelectedEvent.class);
        verify(executionErrorSelectedEventMock).fire(captor.capture());
        assertEquals(deploymentId,
                     captor.getValue().getDeploymentId());
        assertEquals(errorId,
                     captor.getValue().getErrorId());

        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(Constants.INSTANCE.ExecutionErrorBreadcrumb(ExecutionErrorDetailsPresenter.getErrorDetailTitle(errorSummary))),
                                          eq(Commands.DO_NOTHING));
        assertEquals("Evaluation - 1 (evaluation.1.0.1)",
                     ExecutionErrorDetailsPresenter.getErrorDetailTitle(errorSummary));
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
                                          eq(commonConstants.Manage_ExecutionErrors()),
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
                                        commonConstants.Manage_ExecutionErrors(),
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
                                          eq(commonConstants.Manage_ExecutionErrors()),
                                          captureCommand.capture());

        captureCommand.getValue().execute();
        verify(placeManagerMock).closePlace(detailScreenId);

        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(detailLabel),
                                          eq(Commands.DO_NOTHING));
    }

    protected class PerspectiveAnswer implements Answer<Boolean> {

        private String perspectiveId;

        public PerspectiveAnswer(String perspectiveId) {
            this.perspectiveId = perspectiveId;
        }

        @Override
        public Boolean answer(InvocationOnMock invocation) throws Throwable {
            return perspectiveId.equals(((ResourceRef) invocation.getArguments()[0]).getIdentifier());
        }
    }

    @Test
    public void testOnBasicFilterAddEvent() {
        final ActiveFilterItem<Object> filter = new ActiveFilterItem<>("key1",
                                                                       null,
                                                                       null,
                                                                       null,
                                                                       null);
        final ColumnFilter columnFilter = mock(ColumnFilter.class);
        presenter.onBasicFilterAddEvent(new BasicFilterAddEvent(filter,
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
        presenter.onBasicFilterRemoveEvent(new BasicFilterRemoveEvent(filter,
                                                                      columnFilter));

        verify(viewMock).removeActiveFilter(filter);
        verify(filterSettings).removeColumnFilter(columnFilter);
    }
}