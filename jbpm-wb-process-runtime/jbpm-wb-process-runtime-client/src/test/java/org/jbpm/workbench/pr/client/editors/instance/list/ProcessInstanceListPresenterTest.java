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
package org.jbpm.workbench.pr.client.editors.instance.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.basic.BasicFilterAddEvent;
import org.jbpm.workbench.common.client.filters.basic.BasicFilterRemoveEvent;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsJSONMarshaller;
import org.jbpm.workbench.df.client.filter.FilterSettingsManager;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.pr.client.editors.instance.signal.ProcessInstanceSignalPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.service.ProcessService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
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
import org.uberfire.workbench.model.ActivityResourceType;

import static java.util.Arrays.asList;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.likeTo;
import static org.jbpm.workbench.common.client.PerspectiveIds.PROCESS_INSTANCE_DETAILS_SCREEN;
import static org.jbpm.workbench.common.client.PerspectiveIds.SEARCH_PARAMETER_PROCESS_DEFINITION_ID;
import static org.jbpm.workbench.common.client.PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;
import static org.junit.Assert.*;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceListPresenterTest {

    private static final String PERSPECTIVE_ID = PerspectiveIds.PROCESS_INSTANCES;

    private org.jbpm.workbench.common.client.resources.i18n.Constants commonConstants;

    @Mock
    protected PlaceManager placeManager;

    private CallerMock<ProcessService> remoteProcessServiceCaller;

    @Mock
    private ProcessService processService;

    @Mock
    private ProcessInstanceListViewImpl viewMock;

    @Mock
    private DataSetQueryHelper dataSetQueryHelper;

    @Mock
    private DataSet dataSet;

    @Mock
    private DataSet dataSetProcessVar;

    @Mock
    private DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    @Mock
    private ListTable extendedPagedTable;

    @Spy
    private FilterSettings filterSettings;

    @Mock
    private FilterSettingsJSONMarshaller filterSettingsJSONMarshaller;

    @Spy
    private DataSetLookup dataSetLookup;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User identity;

    @Mock
    ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    @Mock
    UberfireBreadcrumbs breadcrumbs;

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    private PerspectiveActivity perspectiveActivity;

    private ArrayList<ProcessInstanceSummary> processInstanceSummaries;

    @Mock
    private FilterSettingsManager filterSettingsManager;

    @Mock
    ContainerSpec containerSpecMock;

    @Mock
    EventSourceMock<ProcessInstanceSelectionEvent> processInstanceSelectionEvent = new EventSourceMock<>();

    @Mock
    SpecManagementService specManagementService;

    Caller<SpecManagementService> specManagementServiceCaller;

    @InjectMocks
    private ProcessInstanceListPresenter presenter;

    public static ProcessInstanceSummary createProcessInstanceSummary(Long key) {
        return createProcessInstanceSummary(key,
                                            ProcessInstance.STATE_ACTIVE);
    }

    public static ProcessInstanceSummary createProcessInstanceSummary(Long key,
                                                                      Integer status) {
        return new ProcessInstanceSummary(key,
                                          "procTest",
                                          "test.0.1",
                                          "Test Proc",
                                          "1.0",
                                          status,
                                          new Date(),
                                          new Date(),
                                          "intiatior",
                                          "procTestInstanceDesc",
                                          "cKey",
                                          Long.valueOf(0),
                                          new Date(),
                                          0,
                                          null,
                                          0);
    }

    protected static void testProcessInstanceStatusCondition(Predicate<ProcessInstanceSummary> predicate,
                                                             Integer... validStatutes) {
        List<Integer> allStatus = Lists.newArrayList(ProcessInstance.STATE_ABORTED,
                                                     ProcessInstance.STATE_ACTIVE,
                                                     ProcessInstance.STATE_COMPLETED,
                                                     ProcessInstance.STATE_PENDING,
                                                     ProcessInstance.STATE_SUSPENDED);
        final List<Integer> validStatuses = asList(validStatutes);
        allStatus.removeAll(validStatuses);

        allStatus.forEach(s -> assertFalse(predicate.test(createProcessInstanceSummary(1l,
                                                                                       s))));
        validStatuses.forEach(s -> assertTrue(predicate.test(createProcessInstanceSummary(1l,
                                                                                          s))));
    }

    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        remoteProcessServiceCaller = new CallerMock<ProcessService>(processService);

        processInstanceSummaries = createProcessInstanceSummaryList(5);

        filterSettings.setKey("key");
        filterSettings.setDataSetLookup(dataSetLookup);

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(filterSettingsJSONMarshaller.fromJsonString(anyString())).thenReturn(filterSettings);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(filterSettingsManager.getVariablesFilterSettings(any())).thenReturn(filterSettings);
        when(serverTemplateSelectorMenuBuilder.getView()).thenReturn(mock(ServerTemplateSelectorMenuBuilder.ServerTemplateSelectorElementView.class));
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);
        when(perspectiveActivity.getIdentifier()).thenReturn(PERSPECTIVE_ID);

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
                return null;
            }
        }).when(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                  any(DataSetReadyCallback.class));

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSetProcessVar);
                return null;
            }
        }).when(dataSetQueryHelperDomainSpecific).lookupDataSet(anyInt(),
                                                                any(DataSetReadyCallback.class));
        commonConstants = org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE;

        presenter.setProcessService(remoteProcessServiceCaller);
        specManagementServiceCaller = new CallerMock<SpecManagementService>(specManagementService);

        ServerTemplate serverTemplateMock = mock(ServerTemplate.class);

        when(specManagementService.getServerTemplate(anyString())).thenReturn(serverTemplateMock);
        when(serverTemplateMock.getContainerSpec(anyString())).thenReturn(containerSpecMock);
    }

    @Test
    public void getDataTest() {
        presenter.getData(new Range(0,
                                    5));

        verify(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                 any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific,
               never()).lookupDataSet(anyInt(),
                                      any(DataSetReadyCallback.class));
        verify(viewMock,
               times(2)).hideBusyIndicator();
    }

    @Test
    public void isFilteredByProcessIdTest() {
        final String processId = "testProc";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_PROCESS_ID,
                                        processId));

        final String filterProcessId = presenter.isFilteredByProcessId(Collections.<DataSetOp>singletonList(filter));
        assertEquals(processId,
                     filterProcessId);
    }

    @Test
    public void isFilteredByProcessIdInvalidTest() {
        final String processId = "testProc";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(likeTo(COLUMN_PROCESS_ID,
                                      processId));

        final String filterProcessId = presenter.isFilteredByProcessId(Collections.<DataSetOp>singletonList(filter));
        assertNull(filterProcessId);
    }

    @Test
    public void abortProcessInstanceTest() {
        final Long processInstanceId = new Random().nextLong();
        final String containerId = "container";

        presenter.abortProcessInstance(containerId,
                                       processInstanceId);

        verify(processService).abortProcessInstance(anyString(),
                                                    eq(containerId),
                                                    eq(processInstanceId));
        verify(viewMock).displayNotification(Constants.INSTANCE.Aborting_Process_Instance(processInstanceId));
    }

    @Test
    public void abortProcessInstancesTest() {
        final Random random = new Random();
        final List<String> containers = new ArrayList<String>();
        containers.add("container");

        final List<Long> pIds = new ArrayList<Long>();
        pIds.add(random.nextLong());
        pIds.add(random.nextLong());
        pIds.add(random.nextLong());

        presenter.abortProcessInstances(containers,
                                        pIds);

        verify(processService).abortProcessInstances(anyString(),
                                                     eq(containers),
                                                     eq(pIds));
    }

    @Test
    public void bulkAbortProcessInstancesTest() {
        final List<Long> pIds = new ArrayList<Long>();
        final List<String> containers = new ArrayList<String>();
        for (ProcessInstanceSummary summary : processInstanceSummaries) {
            pIds.add(summary.getProcessInstanceId());
            containers.add(summary.getDeploymentId());
        }

        presenter.bulkAbort(processInstanceSummaries);

        verify(processService).abortProcessInstances(anyString(),
                                                     eq(containers),
                                                     eq(pIds));
    }

    @Test
    public void bulkAbortProcessInstancesStateTest() {
        processInstanceSummaries.add(createProcessInstanceSummary(new Random().nextLong(),
                                                                  ProcessInstance.STATE_ABORTED));
        final List<Long> pIds = new ArrayList<Long>();
        final List<String> containers = new ArrayList<String>();
        for (ProcessInstanceSummary summary : processInstanceSummaries) {
            if (summary.getState() == ProcessInstance.STATE_ACTIVE) {
                pIds.add(summary.getProcessInstanceId());
                containers.add(summary.getDeploymentId());
            }
        }

        presenter.bulkAbort(processInstanceSummaries);

        verify(processService).abortProcessInstances(anyString(),
                                                     eq(containers),
                                                     eq(pIds));
    }

    @Test
    public void bulkSignalProcessInstanceSingleAbortedTest() {
        ArrayList<ProcessInstanceSummary> processInstanceSummaries = new ArrayList<ProcessInstanceSummary>();
        processInstanceSummaries.add(createProcessInstanceSummary(new Random().nextLong(),
                                                                  ProcessInstance.STATE_ABORTED));

        presenter.bulkSignal(processInstanceSummaries);

        verify(placeManager,
               never()).goTo(any(PlaceRequest.class));
    }

    @Test
    public void bulkSignalProcessInstancesStateTest() {
        processInstanceSummaries.add(createProcessInstanceSummary(new Random().nextLong(),
                                                                  ProcessInstance.STATE_ABORTED));
        final List<Long> pIds = new ArrayList<Long>();
        for (ProcessInstanceSummary summary : processInstanceSummaries) {
            if (summary.getState() == ProcessInstance.STATE_ACTIVE) {
                pIds.add(summary.getProcessInstanceId());
            }
        }

        presenter.bulkSignal(processInstanceSummaries);

        final ArgumentCaptor<PlaceRequest> placeRequest = ArgumentCaptor.forClass(PlaceRequest.class);
        verify(placeManager).goTo(placeRequest.capture());

        assertEquals(ProcessInstanceSignalPresenter.SIGNAL_PROCESS_POPUP,
                     placeRequest.getValue().getIdentifier());
        assertEquals(StringUtils.join(pIds,
                                      ","),
                     placeRequest.getValue().getParameter("processInstanceId",
                                                          null));
    }

    @Test
    public void getDomainSpecifDataForProcessInstancesTest() {
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_PROCESS_ID,
                                        "testProc"));
        filterSettings.getDataSetLookup().addOperation(filter);

        when(dataSet.getRowCount()).thenReturn(1);//1 process instance
        when(dataSet.getValueAt(0,
                                COLUMN_PROCESS_INSTANCE_ID)).thenReturn(Long.valueOf(1));

        when(dataSetProcessVar.getRowCount()).thenReturn(2); //two domain variables associated
        when(dataSetProcessVar.getValueAt(0,
                                          PROCESS_INSTANCE_ID)).thenReturn(Long.valueOf(1));
        String processVariable1 = "var1";
        when(dataSetProcessVar.getValueAt(0,
                                          VARIABLE_NAME)).thenReturn(processVariable1);
        when(dataSetProcessVar.getValueAt(0,
                                          VARIABLE_VALUE)).thenReturn("value1");

        when(dataSetProcessVar.getValueAt(1,
                                          PROCESS_INSTANCE_ID)).thenReturn(Long.valueOf(1));
        String processVariable2 = "var2";
        when(dataSetProcessVar.getValueAt(1,
                                          VARIABLE_NAME)).thenReturn(processVariable2);
        when(dataSetProcessVar.getValueAt(1,
                                          VARIABLE_VALUE)).thenReturn("value2");

        Set<String> expectedColumns = new HashSet<String>();
        expectedColumns.add(processVariable1);
        expectedColumns.add(processVariable2);

        presenter.getData(new Range(0,
                                    5));

        ArgumentCaptor<Set> argument = ArgumentCaptor.forClass(Set.class);
        verify(viewMock).addDomainSpecifColumns(any(ExtendedPagedTable.class),
                                                argument.capture());

        assertEquals(expectedColumns,
                     argument.getValue());

        verify(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                 any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific).lookupDataSet(anyInt(),
                                                               any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific).setLastOrderedColumn(PROCESS_INSTANCE_ID);
        verify(dataSetQueryHelperDomainSpecific).setLastSortOrder(SortOrder.ASCENDING);

        when(dataSetProcessVar.getRowCount()).thenReturn(1); //one domain variables associated
        when(dataSetProcessVar.getValueAt(0,
                                          PROCESS_INSTANCE_ID)).thenReturn(Long.valueOf(1));
        processVariable1 = "varTest1";
        when(dataSetProcessVar.getValueAt(0,
                                          VARIABLE_NAME)).thenReturn(processVariable1);
        when(dataSetProcessVar.getValueAt(0,
                                          VARIABLE_VALUE)).thenReturn("value1");

        expectedColumns = Collections.singleton(processVariable1);

        presenter.getData(new Range(0,
                                    5));

        argument = ArgumentCaptor.forClass(Set.class);
        verify(viewMock,
               times(2)).addDomainSpecifColumns(any(ExtendedPagedTable.class),
                                                argument.capture());

        assertEquals(expectedColumns,
                     argument.getValue());
        verify(dataSetQueryHelper,
               times(2)).lookupDataSet(anyInt(),
                                       any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific,
               times(2)).lookupDataSet(anyInt(),
                                       any(DataSetReadyCallback.class));
    }

    public ArrayList<ProcessInstanceSummary> createProcessInstanceSummaryList(int listSize) {
        ArrayList<ProcessInstanceSummary> pIList = new ArrayList<ProcessInstanceSummary>();
        for (long i = 1; i <= listSize; i++) {
            pIList.add(createProcessInstanceSummary(i));
        }
        return pIList;
    }

    @Test
    public void testDataSetQueryHelperColumnMapping() {
        final Long TEST_PROC_INST_ID = Long.valueOf(55);
        final String TEST_PROC_ID = "TEST_PROC_ID";
        final String TEST_EXT_ID = "TEST_EXT_ID";
        final String TEST_PROC_NAME = "TEST_PROC_NAME";
        final String TEST_PROC_VER = "TEST_PROC_VER";
        final Integer TEST_STATE = 7;
        final Date TEST_START_DATE = new Date(new Date().getTime() - (2 * 60 * 60 * 1000));
        final Date TEST_END_DATE = new Date(new Date().getTime() + (2 * 60 * 60 * 1000));
        final String TEST_IDENTITY = "TEST_IDENTITY";
        final String TEST_INST_DESC = "TEST_INST_DESC";
        final String TEST_CORREL_KEY = "TEST_CORREL_KEY";
        final Long TEST_PARENT_PROC_INST_ID = Long.valueOf(66);
        final Date TEST_LAST_MODIF_DATE = new Date();
        final Integer TEST_ERROR_COUNT = 66;

        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_PROCESS_INSTANCE_ID)).thenReturn(TEST_PROC_INST_ID);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_PROCESS_ID)).thenReturn(TEST_PROC_ID);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_EXTERNAL_ID)).thenReturn(TEST_EXT_ID);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_PROCESS_NAME)).thenReturn(TEST_PROC_NAME);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_PROCESS_VERSION)).thenReturn(TEST_PROC_VER);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_STATUS)).thenReturn(TEST_STATE);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_START)).thenReturn(TEST_START_DATE);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_END)).thenReturn(TEST_END_DATE);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_IDENTITY)).thenReturn(TEST_IDENTITY);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_PROCESS_INSTANCE_DESCRIPTION)).thenReturn(TEST_INST_DESC);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_CORRELATION_KEY)).thenReturn(TEST_CORREL_KEY);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_PARENT_PROCESS_INSTANCE_ID)).thenReturn(TEST_PARENT_PROC_INST_ID);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_LAST_MODIFICATION_DATE)).thenReturn(TEST_LAST_MODIF_DATE);
        when(dataSetProcessVar.getValueAt(0,
                                          COLUMN_ERROR_COUNT)).thenReturn(TEST_ERROR_COUNT);

        ProcessInstanceSummary pis = presenter.createProcessInstanceSummaryFromDataSet(dataSetProcessVar,
                                                                                       0);

        assertEquals(TEST_PROC_INST_ID,
                     pis.getProcessInstanceId());
        assertEquals(TEST_PROC_ID,
                     pis.getProcessId());
        assertEquals(TEST_EXT_ID,
                     pis.getDeploymentId());
        assertEquals(TEST_PROC_NAME,
                     pis.getProcessName());
        assertEquals(TEST_PROC_VER,
                     pis.getProcessVersion());
        assertEquals(TEST_STATE,
                     pis.getState());
        assertEquals(TEST_START_DATE,
                     pis.getStartTime());
        assertEquals(TEST_END_DATE,
                     pis.getEndTime());
        assertEquals(TEST_IDENTITY,
                     pis.getInitiator());
        assertEquals(TEST_INST_DESC,
                     pis.getProcessInstanceDescription());
        assertEquals(TEST_CORREL_KEY,
                     pis.getCorrelationKey());
        assertEquals(TEST_PARENT_PROC_INST_ID,
                     pis.getParentId());
        assertEquals(TEST_LAST_MODIF_DATE,
                     pis.getLastModificationDate());
        assertEquals(TEST_ERROR_COUNT,
                     pis.getErrorCount());
    }

    @Test
    public void testDefaultActiveSearchFilters() {
        presenter.setupDefaultActiveSearchFilters();

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(viewMock).addActiveFilter(captor.capture());

        assertEquals(1,
                     captor.getAllValues().size());
        assertEquals(Constants.INSTANCE.State(),
                     captor.getValue().getKey());
        assertEquals(Constants.INSTANCE.State() + ": " + Constants.INSTANCE.Active(),
                     captor.getValue().getLabelValue());
        assertEquals(ProcessInstance.STATE_ACTIVE,
                     (captor.getValue().getValue()));
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

        assertEquals(1,
                     captor.getAllValues().size());
        assertEquals(Constants.INSTANCE.State(),
                     captor.getValue().getKey());
        assertEquals(Constants.INSTANCE.State() + ": " + Constants.INSTANCE.Active(),
                     captor.getValue().getLabelValue());
        assertEquals(ProcessInstance.STATE_ACTIVE,
                     (captor.getValue().getValue()));
    }

    @Test
    public void testActiveSearchFiltersProcessDefinitionId() {
        final PlaceRequest place = mock(PlaceRequest.class);
        final String processDefinitionId = "defId";
        when(place.getParameter(SEARCH_PARAMETER_PROCESS_DEFINITION_ID,
                                null)).thenReturn(processDefinitionId);
        presenter.onStartup(place);

        presenter.setupActiveSearchFilters();

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(viewMock).addActiveFilter(captor.capture());

        assertEquals(1,
                     captor.getAllValues().size());
        assertEquals(Constants.INSTANCE.Process_Definition_Id(),
                     captor.getValue().getKey());
        assertEquals(Constants.INSTANCE.Process_Definition_Id() + ": " + processDefinitionId,
                     captor.getValue().getLabelValue());
        assertEquals(processDefinitionId,
                     (captor.getValue().getValue()));
    }

    @Test
    public void testActiveSearchFiltersProcessInstanceId() {
        final PlaceRequest place = mock(PlaceRequest.class);
        final String processInstanceId = "1";
        when(place.getParameter(SEARCH_PARAMETER_PROCESS_DEFINITION_ID,
                                null)).thenReturn(null);
        when(place.getParameter(SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                                null)).thenReturn(processInstanceId);
        presenter.onStartup(place);

        presenter.setupActiveSearchFilters();

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(viewMock).addActiveFilter(captor.capture());

        assertEquals(1,
                     captor.getAllValues().size());
        assertEquals(Constants.INSTANCE.Id(),
                     captor.getValue().getKey());
        assertEquals(Constants.INSTANCE.Id() + ": " + processInstanceId,
                     captor.getValue().getLabelValue());
        assertEquals(1,
                     (captor.getValue().getValue()));
    }

    @Test
    public void testIsAuthorizedForTaskAdminView() {
        testIsAuthorizedForView(TASKS_ADMIN);
    }

    @Test
    public void testIsAuthorizedForTaskView() {
        testIsAuthorizedForView(TASKS);
    }

    private void testIsAuthorizedForView(final String perspectiveId) {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(true,
                                                                      false);

        assertTrue(presenter.isUserAuthorizedForPerspective(perspectiveId));

        final ArgumentCaptor<ResourceRef> captor = ArgumentCaptor.forClass(ResourceRef.class);
        verify(authorizationManager).authorize(captor.capture(),
                                               eq(identity));
        assertEquals(perspectiveId,
                     captor.getValue().getIdentifier());
        assertEquals(ActivityResourceType.PERSPECTIVE,
                     captor.getValue().getResourceType());

        assertFalse(presenter.isUserAuthorizedForPerspective(perspectiveId));
    }

    @Test
    public void testViewTasksActionCondition() {
        doAnswer(new PerspectiveAnswer(TASKS_ADMIN)).when(authorizationManager).authorize(any(ResourceRef.class),
                                                                                          eq(identity));

        assertTrue(presenter.getViewTasksActionCondition().test(new ProcessInstanceSummary()));

        doAnswer(new PerspectiveAnswer(TASKS)).when(authorizationManager).authorize(any(ResourceRef.class),
                                                                                    eq(identity));

        assertTrue(presenter.getViewTasksActionCondition().test(new ProcessInstanceSummary()));

        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(false);

        assertFalse(presenter.getViewJobsActionCondition().test(new ProcessInstanceSummary()));
    }

    @Test
    public void testViewJobsActionCondition() {
        doAnswer(new PerspectiveAnswer(JOBS)).when(authorizationManager).authorize(any(ResourceRef.class),
                                                                                   eq(identity));

        assertTrue(presenter.getViewJobsActionCondition().test(new ProcessInstanceSummary()));

        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(false);

        assertFalse(presenter.getViewJobsActionCondition().test(new ProcessInstanceSummary()));
    }

    @Test
    public void testViewErrorsActionCondition() {
        doAnswer(new PerspectiveAnswer(EXECUTION_ERRORS)).when(authorizationManager).authorize(any(ResourceRef.class),
                                                                                               eq(identity));

        ProcessInstanceSummary okProcInst = new ProcessInstanceSummary();
        ProcessInstanceSummary errProcInst = new ProcessInstanceSummary();
        errProcInst.setErrorCount(1);
        Predicate<ProcessInstanceSummary> viewErrCondition = presenter.getViewErrorsActionCondition();

        assertFalse(viewErrCondition.test(okProcInst));
        assertTrue(viewErrCondition.test(errProcInst));

        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(false);

        assertFalse(viewErrCondition.test(okProcInst));
        assertFalse(viewErrCondition.test(errProcInst));
    }

    @Test
    public void testAbortActionCondition() {
        testProcessInstanceStatusCondition(presenter.getAbortActionCondition(),
                                           ProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testSignalActionCondition() {
        testProcessInstanceStatusCondition(presenter.getSignalActionCondition(),
                                           ProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testOpenTaskView() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(true,
                                                                      false);

        presenter.openTaskView("");
        presenter.openTaskView("");

        final ArgumentCaptor<PlaceRequest> captor = ArgumentCaptor.forClass(PlaceRequest.class);
        verify(placeManager,
               times(2)).goTo(captor.capture());
        assertEquals(2,
                     captor.getAllValues().size());
        assertEquals(TASKS_ADMIN,
                     captor.getAllValues().get(0).getIdentifier());
        assertEquals(TASKS,
                     captor.getAllValues().get(1).getIdentifier());
    }

    @Test
    public void testCreateDataSetProcessInstanceCallbackOnError() {
        final ProcessInstanceListPresenter spy = spy(presenter);
        final ClientRuntimeError error = new ClientRuntimeError("");
        final FilterSettings filterSettings = mock(FilterSettings.class);
        final DataSetReadyCallback callback = spy.getDataSetReadyCallback(0,
                                                                          filterSettings);
        doNothing().when(spy).showErrorPopup(any());
        assertFalse(callback.onError(error));
        verify(viewMock).hideBusyIndicator();
        verify(spy).showErrorPopup(Constants.INSTANCE.ResourceCouldNotBeLoaded(commonConstants.Process_Instances()));
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
                                          eq(commonConstants.Manage_Process_Instances()),
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
                                        commonConstants.Manage_Process_Instances(),
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
                                          eq(commonConstants.Manage_Process_Instances()),
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

    @Test
    public void testSelectProcessInstanceWhenContainerStarted() {

        when(containerSpecMock.getStatus()).thenReturn(KieContainerStatus.STARTED);

        presenter.setSpecManagementService(specManagementServiceCaller);
        presenter.setProcessInstanceSelectedEvent(processInstanceSelectionEvent);

        ProcessInstanceSummary okProcInst = new ProcessInstanceSummary();
        presenter.selectProcessInstance(okProcInst);

        verify(processInstanceSelectionEvent).fire(any(ProcessInstanceSelectionEvent.class));
        verify(placeManager).goTo(PROCESS_INSTANCE_DETAILS_SCREEN);
        verify(viewMock,
               never()).displayNotification(anyString());
    }

    @Test
    public void testSelectProcessInstanceWhenContainerStopped() {

        when(containerSpecMock.getStatus()).thenReturn(KieContainerStatus.STOPPED);

        presenter.setSpecManagementService(specManagementServiceCaller);
        presenter.setProcessInstanceSelectedEvent(processInstanceSelectionEvent);

        ProcessInstanceSummary okProcInst = new ProcessInstanceSummary();
        presenter.selectProcessInstance(okProcInst);

        verify(processInstanceSelectionEvent,
               never()).fire(any(ProcessInstanceSelectionEvent.class));
        verify(placeManager,
               never()).goTo(PROCESS_INSTANCE_DETAILS_SCREEN);
        verify(viewMock).displayNotification(anyString());
    }

    @Test
    public void testFilterInitiator(){
        String variableNameWithInitiator = "initiator";
        String variableValueWithInitiator = "initiator people";

        String variableNameWithoutInitiator = "noinitiator";
        String variableValueWithoutInitiator = "other people";

        assertEquals(true, presenter.filterInitiator(variableNameWithInitiator, variableValueWithInitiator, variableValueWithInitiator));
        assertEquals(false, presenter.filterInitiator(variableNameWithoutInitiator, variableValueWithoutInitiator, variableValueWithoutInitiator));
        assertEquals(false, presenter.filterInitiator(variableNameWithInitiator, variableValueWithInitiator, variableValueWithoutInitiator));
        assertEquals(false, presenter.filterInitiator(variableNameWithoutInitiator, variableValueWithoutInitiator, variableValueWithInitiator));
    }
}
