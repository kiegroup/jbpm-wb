/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.pr.client.editors.definition.list;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.jboss.errai.common.client.api.Caller;

import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;

import org.jbpm.workbench.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.workbench.forms.client.display.views.PopupFormDisplayerView;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.console.ng.pr.events.ProcessDefSelectionEvent;
import org.jbpm.console.ng.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessDefinitionListPresenterTest {

    @Mock
    ProcessDefinitionListPresenter.ProcessDefinitionListView view;

    @Mock
    ExtendedPagedTable extendedPagedTable;

    @Mock
    protected PlaceManager placeManager;

    @Mock
    protected EventSourceMock<ProcessDefSelectionEvent> processDefSelectionEvent;

    Caller<ProcessRuntimeDataService> processRuntimeDataServiceCaller;

    @Mock
    ProcessRuntimeDataService processRuntimeDataService;

    @Mock
    StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    @Mock
    PopupFormDisplayerView formDisplayPopUp;

    @Mock
    HasData next;

    @InjectMocks
    ProcessDefinitionListPresenter presenter;

    @Before
    public void setup() {
        processRuntimeDataServiceCaller = new CallerMock<ProcessRuntimeDataService>(processRuntimeDataService);
        presenter.setProcessRuntimeDataService(processRuntimeDataServiceCaller);
        when(view.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getColumnSortList()).thenReturn(new ColumnSortList());

        when(next.getVisibleRange()).thenReturn(new Range(1, 1));
        presenter.getDataProvider().addDataDisplay(next);
    }

    @Test
    public void testSearchString() {
        final String textSearchStr = "textSearch";
        final SearchEvent searchEvent = new SearchEvent(textSearchStr);

        presenter.onSearchEvent(searchEvent);

        assertEquals(searchEvent.getFilter(), presenter.getTextSearchStr());
        verify(processRuntimeDataService).getProcessesByFilter(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyBoolean());
    }

    @Test
    public void testProcessDefNameDefinitionPropagation(){
        String processDefName = "testProcessDefName";

        ProcessSummary processSummary = new ProcessSummary();
        processSummary.setProcessDefId("testProcessDefId");
        processSummary.setDeploymentId("testDeploymentId");
        processSummary.setProcessDefName(processDefName);

        when(placeManager.getStatus(any(PlaceRequest.class))).thenReturn(PlaceStatus.CLOSE);
        presenter.selectProcessDefinition(processSummary, true);

        verify(processDefSelectionEvent).fire(any(ProcessDefSelectionEvent.class));
        ArgumentCaptor<ProcessDefSelectionEvent> argument = ArgumentCaptor.forClass( ProcessDefSelectionEvent.class );
        verify( processDefSelectionEvent).fire(argument.capture());
        assertEquals( processDefName, argument.getValue().getProcessDefName() );
    }

    @Test
    public void testProcessDefNameDefinitionOpenGenericForm(){
        String processDefName = "testProcessDefName";

        presenter.openGenericForm("processDefId","deploymentId", processDefName);

        ArgumentCaptor< ProcessDisplayerConfig> argument = ArgumentCaptor.forClass(  ProcessDisplayerConfig.class );
        verify( startProcessDisplayProvider).setup(argument.capture(),any());
        assertEquals(processDefName,argument.getValue().getProcessName());
    }

    @Test
    public void testGetData() {
        when(processRuntimeDataService.getProcessesByFilter(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyBoolean()))
                .thenReturn(getMockList(10))
                .thenReturn(getMockList(1));

        Range range = new Range(0, 10);
        final ProcessDefinitionListPresenter presenter = spy(this.presenter);
        presenter.getData(range);

        verify(presenter).updateDataOnCallback(anyList(), eq(0), eq(10), eq(false));

        range = new Range(10, 10);
        presenter.getData(range);

        verify(presenter).updateDataOnCallback(anyList(), eq(10), eq(11), eq(true));
    }

    private static List<ProcessSummary> getMockList(int instances) {
        final List<ProcessSummary> summaries = new ArrayList<>();
        for (int i = 0; i < instances; i++) {
            summaries.add(new ProcessSummary());
        }
        return summaries;
    }

}