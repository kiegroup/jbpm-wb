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
package org.jbpm.workbench.pr.client.editors.instance.list;

import java.util.*;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.dataset.ErrorHandlerBuilder;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.basic.BasicFilterAddEvent;
import org.jbpm.workbench.common.client.filters.basic.BasicFilterRemoveEvent;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsJSONMarshaller;
import org.jbpm.workbench.df.client.filter.FilterSettingsManager;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.pr.client.editors.instance.signal.ProcessInstanceSignalPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.service.ProcessService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.process.ProcessInstance;
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
import static java.util.Collections.singletonMap;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.in;
import static org.dashbuilder.dataset.filter.FilterFactory.likeTo;
import static org.jbpm.workbench.common.client.PerspectiveIds.*;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;
import static org.junit.Assert.*;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.EXECUTION_ERRORS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.JOBS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.TASKS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.TASKS_ADMIN;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceListPresenterTest {

    private static final String PERSPECTIVE_ID = PerspectiveIds.PROCESS_INSTANCES;
    private String datasetUId = PROCESS_INSTANCE_DATASET;

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
    private ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    @Mock
    private UberfireBreadcrumbs breadcrumbs;

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    private PerspectiveActivity perspectiveActivity;

    private ArrayList<ProcessInstanceSummary> processInstanceSummaries;

    @Mock
    private FilterSettingsManager filterSettingsManager;

    @Mock
    private EventSourceMock<ProcessInstanceSelectionEvent> processInstanceSelectionEvent = new EventSourceMock<>();

    @Mock
    private ManagedInstance<ErrorHandlerBuilder> errorHandlerBuilder;

    @Spy
    private ErrorHandlerBuilder errorHandler;

    @InjectMocks
    private ProcessInstanceListPresenter presenter;

    public static ProcessInstanceSummary createProcessInstanceSummary(Long key) {
        return createProcessInstanceSummary(key,
                                            ProcessInstance.STATE_ACTIVE);
    }

    public static ProcessInstanceSummary createProcessInstanceSummary(Long key,
                                                                      Integer status) {
        return new ProcessInstanceSummary("serverTemplateId",
                                          key,
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
        dataSetLookup.setDataSetUUID(datasetUId);

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(filterSettingsJSONMarshaller.fromJsonString(anyString())).thenReturn(filterSettings);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(filterSettings.getUUID()).thenReturn(datasetUId);
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

        when(errorHandlerBuilder.get()).thenReturn(errorHandler);
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
    public void testRemoveActiveFilter() {
        final String processId = "testProc";
        presenter.removeActiveFilter(equalsTo(COLUMN_PROCESS_ID, processId));

        verify(viewMock).removeDomainSpecifColumns();

        reset(viewMock);

        presenter.removeActiveFilter(in(COLUMN_PROCESS_ID, Arrays.asList(processId)));

        verify(viewMock, never()).removeDomainSpecifColumns();
    }

    @Test
    public void isFilteredByProcessIdTest() {
        final String processId = "testProc";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_PROCESS_ID, processId));

        final String filterProcessId = presenter.isFilteredByProcessId(Collections.<DataSetOp>singletonList(filter));
        assertEquals(processId, filterProcessId);
    }

    @Test
    public void isFilteredByProcessIdInvalidTest() {
        final String processId = "testProc";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(likeTo(COLUMN_PROCESS_ID, processId));

        final String filterProcessId = presenter.isFilteredByProcessId(Collections.<DataSetOp>singletonList(filter));
        assertNull(filterProcessId);
    }

    @Test
    public void abortProcessInstanceTest() {
        final Long processInstanceId = new Random().nextLong();
        final String containerId = "container";

        presenter.abortProcessInstance(containerId,
                                       processInstanceId);

        verify(processService).abortProcessInstance(new ProcessInstanceKey("",
                                                                           containerId,
                                                                           processInstanceId));
        verify(viewMock).displayNotification(Constants.INSTANCE.Aborting_Process_Instance(processInstanceId));
    }

    @Test
    public void abortProcessInstancesTest() {
        final Random random = new Random();

        final Map<String, List<Long>> containerInstance = singletonMap("container",
                                                                       Arrays.asList(random.nextLong(),
                                                                                     random.nextLong(),
                                                                                     random.nextLong()));

        presenter.abortProcessInstances(containerInstance);

        verify(processService).abortProcessInstances(anyString(),
                                                     eq(containerInstance));
    }

    @Test
    public void bulkAbortProcessInstancesTest() {
        final Map<String, List<Long>> containerInstance = new HashMap<>();
        for (ProcessInstanceSummary summary : processInstanceSummaries) {
            containerInstance.computeIfAbsent(summary.getDeploymentId(),
                                              key -> new ArrayList<>()).add(summary.getProcessInstanceId());
        }

        presenter.bulkAbort(processInstanceSummaries);

        verify(processService).abortProcessInstances(anyString(),
                                                     eq(containerInstance));
    }

    @Test
    public void bulkAbortProcessInstancesStateTest() {
        processInstanceSummaries.add(createProcessInstanceSummary(new Random().nextLong(),
                                                                  ProcessInstance.STATE_ABORTED));
        final Map<String, List<Long>> containerInstance = new HashMap<>();
        for (ProcessInstanceSummary summary : processInstanceSummaries) {
            if (summary.getState() == ProcessInstance.STATE_ACTIVE) {
                containerInstance.computeIfAbsent(summary.getDeploymentId(),
                                                  key -> new ArrayList<>()).add(summary.getProcessInstanceId());
            }
        }

        presenter.bulkAbort(processInstanceSummaries);

        verify(processService).abortProcessInstances(anyString(),
                                                     eq(containerInstance));
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
        verify(viewMock).addDomainSpecifColumns(argument.capture());

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
        verify(viewMock, times(2)).addDomainSpecifColumns(argument.capture());

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
    public void testExistActiveSearchFilters() {
        final PlaceRequest place = mock(PlaceRequest.class);
        presenter.onStartup(place);

        when(place.getParameter(SEARCH_PARAMETER_PROCESS_INSTANCE_ID, null)).thenReturn("1");
        assertTrue(presenter.existActiveSearchFilters());

        when(place.getParameter(SEARCH_PARAMETER_PROCESS_DEFINITION_ID, null)).thenReturn("1");
        assertTrue(presenter.existActiveSearchFilters());

        when(place.getParameter(SEARCH_PARAMETER_PROCESS_INSTANCE_ID, null)).thenReturn(null);
        when(place.getParameter(SEARCH_PARAMETER_PROCESS_DEFINITION_ID, null)).thenReturn(null);
        assertFalse(presenter.existActiveSearchFilters());
    }

    @Test
    public void testActiveFilterLabelStatus() {
        ColumnFilter testColumFilter = equalsTo(COLUMN_STATUS,
                                                Arrays.asList(String.valueOf(ProcessInstance.STATE_ACTIVE),
                                                              String.valueOf(ProcessInstance.STATE_COMPLETED)));

        ActiveFilterItem activeFilterItem = presenter.getActiveFilterFromColumnFilter(testColumFilter);

        assertEquals(Constants.INSTANCE.State(), activeFilterItem.getKey());
        assertEquals(Constants.INSTANCE.State() + ": " + Constants.INSTANCE.Active() + ", " + Constants.INSTANCE.Completed(),
                     activeFilterItem.getLabelValue());
        assertNotEquals(testColumFilter.toString(), activeFilterItem.getLabelValue());
    }

    @Test
    public void testActiveFilterLabelOther() {
        ColumnFilter testColumFilter = equalsTo(COLUMN_PROCESS_ID, "evaluation");
        ActiveFilterItem activeFilterItem = presenter.getActiveFilterFromColumnFilter(testColumFilter);
        assertEquals(COLUMN_PROCESS_ID, activeFilterItem.getKey());
        assertEquals(testColumFilter.toString(), activeFilterItem.getLabelValue());

        testColumFilter = equalsTo(COLUMN_PROCESS_INSTANCE_ID, 1);
        activeFilterItem = presenter.getActiveFilterFromColumnFilter(testColumFilter);
        assertEquals(COLUMN_PROCESS_INSTANCE_ID, activeFilterItem.getKey());
        assertEquals(testColumFilter.toString(), activeFilterItem.getLabelValue());
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
        presenter.onBasicFilterAddEvent(new BasicFilterAddEvent("ProcessInstanceLogDataset",
                                                                filter,
                                                                columnFilter));
        verify(viewMock,
               never()).addActiveFilter(filter);
        verify(filterSettings,
               never()).addColumnFilter(columnFilter);

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
        presenter.onBasicFilterRemoveEvent(new BasicFilterRemoveEvent("ProcessInstanceLogDataset",
                                                                      filter,
                                                                      columnFilter));
        verify(viewMock,
               never()).removeActiveFilter(filter);
        verify(filterSettings,
               never()).removeColumnFilter(columnFilter);

        presenter.onBasicFilterRemoveEvent(new BasicFilterRemoveEvent(datasetUId,
                                                                      filter,
                                                                      columnFilter));

        verify(viewMock).removeActiveFilter(filter);
        verify(filterSettings).removeColumnFilter(columnFilter);
    }

    @Test
    public void testSelectProcessInstance() {
        presenter.setProcessInstanceSelectedEvent(processInstanceSelectionEvent);

        ProcessInstanceSummary okProcInst = new ProcessInstanceSummary();
        presenter.selectSummaryItem(okProcInst);

        ArgumentCaptor<ProcessInstanceSelectionEvent> argument = ArgumentCaptor.forClass(ProcessInstanceSelectionEvent.class);
        verify(processInstanceSelectionEvent).fire(argument.capture());
        verify(placeManager).goTo(PROCESS_INSTANCE_DETAILS_SCREEN);
        verify(viewMock,
               never()).displayNotification(anyString());
    }

    @Test
    public void testFilterInitiator() {
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
