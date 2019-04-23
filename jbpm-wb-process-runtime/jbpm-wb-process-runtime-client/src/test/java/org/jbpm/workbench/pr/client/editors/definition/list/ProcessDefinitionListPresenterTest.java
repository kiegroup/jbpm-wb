/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.definition.list;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.dataset.ErrorHandlerBuilder;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.workbench.forms.client.display.views.PopupFormDisplayerView;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessSummary;
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

import static java.util.Collections.emptyList;
import static org.jbpm.workbench.common.client.PerspectiveIds.SEARCH_PARAMETER_PROCESS_DEFINITION_ID;
import static org.junit.Assert.*;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_INSTANCES;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessDefinitionListPresenterTest {

    private static final String PERSPECTIVE_ID = PerspectiveIds.PROCESS_DEFINITIONS;

    private org.jbpm.workbench.common.client.resources.i18n.Constants commonConstants;

    @Mock
    protected PlaceManager placeManager;

    @Mock
    private UberfireBreadcrumbs breadcrumbs;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User identity;

    @Mock
    private DataSet dataSet;

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    private DataSetQueryHelper dataSetQueryHelper;

    @Mock
    private PerspectiveActivity perspectiveActivity;

    @Mock
    protected EventSourceMock<ProcessDefSelectionEvent> processDefSelectionEvent;

    @Mock
    ProcessDefinitionListPresenter.ProcessDefinitionListView view;

    @Mock
    private FilterSettings filterSettings;

    @Mock
    ExtendedPagedTable extendedPagedTable;

    Caller<ProcessRuntimeDataService> processRuntimeDataServiceCaller;

    @Mock
    ProcessRuntimeDataService processRuntimeDataService;

    @Mock
    StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    @Mock
    PopupFormDisplayerView formDisplayPopUp;

    @Mock
    DefaultWorkbenchErrorCallback errorCallback;

    @Mock
    private ManagedInstance<ErrorHandlerBuilder> errorHandlerBuilder;

    @Spy
    private ErrorHandlerBuilder errorHandler;

    @InjectMocks
    @Spy
    ProcessDefinitionListPresenter presenter;

    private static List<ProcessSummary> getMockList(int instances) {
        final List<ProcessSummary> summaries = new ArrayList<>();
        for (int i = 0; i < instances; i++) {
            summaries.add(new ProcessSummary());
        }
        return summaries;
    }

    @Before
    public void setup() {
        processRuntimeDataServiceCaller = new CallerMock<ProcessRuntimeDataService>(processRuntimeDataService);
        presenter.setProcessRuntimeDataService(processRuntimeDataServiceCaller);
        when(view.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getColumnSortList()).thenReturn(new ColumnSortList());
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);
        when(perspectiveActivity.getIdentifier()).thenReturn(PERSPECTIVE_ID);

        commonConstants = org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE;
    }

    @Test
    public void testProcessDefNameDefinitionPropagation() {
        final ProcessSummary processSummary = new ProcessSummary();
        processSummary.setProcessDefId("testProcessDefId");
        processSummary.setDeploymentId("testDeploymentId");
        processSummary.setProcessDefName("testProcessDefName");
        processSummary.setDynamic(false);

        presenter.selectSummaryItem(processSummary);

        verify(processDefSelectionEvent).fire(any(ProcessDefSelectionEvent.class));
        ArgumentCaptor<ProcessDefSelectionEvent> argument = ArgumentCaptor.forClass(ProcessDefSelectionEvent.class);
        verify(processDefSelectionEvent).fire(argument.capture());
        final ProcessDefSelectionEvent event = argument.getValue();
        assertEquals(processSummary.getProcessDefName(),
                     event.getProcessDefName());
        assertEquals(processSummary.getDeploymentId(),
                     event.getDeploymentId());
        assertEquals(processSummary.getProcessDefId(),
                     event.getProcessId());
        assertEquals(processSummary.isDynamic(),
                     event.isDynamic());

        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(Constants.INSTANCE.ProcessDefinitionBreadcrumb((processSummary.getName()))),
                                          eq(Commands.DO_NOTHING));

    }

    @Test
    public void testProcessDefNameDefinitionOpenGenericForm() {
        String processDefName = "testProcessDefName";

        presenter.openGenericForm("processDefId",
                                  "deploymentId",
                                  processDefName,
                                  false);

        ArgumentCaptor<ProcessDisplayerConfig> argument = ArgumentCaptor.forClass(ProcessDisplayerConfig.class);
        verify(startProcessDisplayProvider).setup(argument.capture(),
                                                  any());
        assertEquals(processDefName,
                     argument.getValue().getProcessName());
    }

    @Test
    public void testGetData() {
        doAnswer(invocation -> {
            ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
            return null;
        }).when(dataSetQueryHelper).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));


        dataSetQueryHelper.setCurrentTableSettings(filterSettings);
        when(errorHandlerBuilder.get()).thenReturn(errorHandler);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(presenter.getDataSetQueryHelper()).thenReturn(dataSetQueryHelper);
        presenter.getData(new Range(0, 5));

        verify(dataSetQueryHelper).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
    }

    @Test
    public void testOnRuntimeDataServiceError() {
        final Throwable throwable = mock(Throwable.class);
        assertFalse(presenter.onRuntimeDataServiceError(throwable));

        verify(presenter).updateDataOnCallback(emptyList(), 0, 0, true);
        verify(errorCallback).error(throwable);
        verify(view).hideBusyIndicator();
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
                                          eq(commonConstants.Manage_Process_Definitions()),
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
                                        commonConstants.Manage_Process_Definitions(),
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
                                          eq(commonConstants.Manage_Process_Definitions()),
                                          captureCommand.capture());

        captureCommand.getValue().execute();
        verify(placeManagerMock).closePlace(detailScreenId);

        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(detailLabel),
                                          eq(Commands.DO_NOTHING));
    }

    @Test
    public void testIsAuthorizedForView() {
        String perspectiveId = PROCESS_INSTANCES;
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
    public void testViewProcessInstanceActionCondition() {

        doAnswer(new PerspectiveAnswer(PROCESS_INSTANCES)).when(authorizationManager).authorize(any(ResourceRef.class),
                                                                                                eq(identity));
        assertTrue(presenter.getViewProcessInstanceActionCondition().test(new ProcessSummary()));

        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(false);

        assertFalse(presenter.getViewProcessInstanceActionCondition().test(new ProcessSummary()));
    }

    @Test
    public void testViewProcessInstances() {
        String processDefinition = "procDef";
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(true);

        presenter.viewProcessInstances(processDefinition);

        final ArgumentCaptor<PlaceRequest> captor = ArgumentCaptor.forClass(PlaceRequest.class);
        verify(placeManager).goTo(captor.capture());
        assertEquals(1,
                     captor.getAllValues().size());
        assertEquals(PROCESS_INSTANCES,
                     captor.getValue().getIdentifier());
        assertEquals(1,
                     captor.getValue().getParameters().size());
        assertEquals(processDefinition,
                     captor.getValue().getParameters().get(SEARCH_PARAMETER_PROCESS_DEFINITION_ID));
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
}
