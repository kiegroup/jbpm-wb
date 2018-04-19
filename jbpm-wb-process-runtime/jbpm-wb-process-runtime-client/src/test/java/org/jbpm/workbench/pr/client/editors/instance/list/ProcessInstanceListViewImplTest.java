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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.AbstractMultiGridViewTest;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.util.GenericErrorSummaryCountCell;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.pr.client.editors.instance.list.ProcessInstanceListViewImpl.COL_ID_ACTIONS;
import static org.jbpm.workbench.pr.client.editors.instance.list.ProcessInstanceListViewImpl.COL_ID_SELECT;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceListViewImplTest extends AbstractMultiGridViewTest<ProcessInstanceSummary> {

    @Mock
    private ProcessInstanceListPresenter presenter;

    @Spy
    private GenericErrorSummaryCountCell cellMock;

    @Mock
    private ManagedInstance<GenericErrorSummaryCountCell> popoverCellInstance;

    @Mock
    private ConfirmPopup confirmPopup;

    @GwtMock
    private AnchorListItem anchorListItem;

    @InjectMocks
    @Spy
    private ProcessInstanceListViewImpl view;

    @Override
    protected AbstractMultiGridView getView() {
        return view;
    }

    @Override
    protected AbstractMultiGridPresenter getPresenter() {
        return presenter;
    }

    @Override
    public List<String> getExpectedInitialColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_PROCESS_INSTANCE_ID,
                             COLUMN_PROCESS_NAME,
                             COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                             COLUMN_PROCESS_VERSION,
                             COLUMN_LAST_MODIFICATION_DATE,
                             COLUMN_ERROR_COUNT,
                             COL_ID_ACTIONS);
    }

    @Override
    public List<String> getExpectedBannedColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_PROCESS_INSTANCE_ID,
                             COLUMN_PROCESS_NAME,
                             COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                             COL_ID_ACTIONS);
    }

    @Override
    public Integer getExpectedNumberOfColumns() {
        return 14;
    }

    @Before
    @Override
    public void setupMocks() {
        super.setupMocks();
        when(popoverCellInstance.get()).thenReturn(cellMock);
    }

    @Test
    public void testSignalCommand() {
        final ExtendedPagedTable table = mock(ExtendedPagedTable.class);

        view.getSignalCommand(table).execute();

        verify(presenter).bulkSignal(any());
        verify(table).deselectAllItems();
    }

    @Test
    public void testBulkSignal() {
        doAnswer(invocation -> {
            ClickHandler handler = (ClickHandler) invocation.getArguments()[0];
            handler.onClick(mock(ClickEvent.class));
            return null;
        }).when(anchorListItem).addClickHandler(any());
        final ExtendedPagedTable table = mock(ExtendedPagedTable.class);

        view.getBulkSignal(table);

        verify(presenter).bulkSignal(any());
        verify(table).deselectAllItems();
    }

    @Test
    public void testBulkAbort() {
        doAnswer(invocation -> {
            ClickHandler handler = (ClickHandler) invocation.getArguments()[0];
            handler.onClick(mock(ClickEvent.class));
            return null;
        }).when(anchorListItem).addClickHandler(any());
        final ExtendedPagedTable table = mock(ExtendedPagedTable.class);

        view.getBulkAbort(table);

        ArgumentCaptor<Command> captor = ArgumentCaptor.forClass(Command.class);
        verify(confirmPopup).show(any(),
                                  any(),
                                  any(),
                                  captor.capture());

        captor.getValue().execute();

        verify(presenter).bulkAbort(any());
        verify(table).deselectAllItems();
    }

    @Test
    public void testAbortCommand() {
        final ExtendedPagedTable table = mock(ExtendedPagedTable.class);

        view.getAbortCommand(table).execute();

        verify(presenter).bulkAbort(any());
        verify(table).deselectAllItems();
    }
}
