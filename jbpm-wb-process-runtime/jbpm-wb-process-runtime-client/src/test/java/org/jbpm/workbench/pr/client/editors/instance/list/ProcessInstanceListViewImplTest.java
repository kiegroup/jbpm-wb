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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.AbstractMultiGridViewTest;
import org.jbpm.workbench.common.client.util.GenericErrorSummaryCountCell;
import org.jbpm.workbench.common.client.util.SLAComplianceCell;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.jbpm.workbench.pr.client.editors.instance.list.ProcessInstanceListViewImpl.*;
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
    public List<String> getExpectedTabs() {
        return Arrays.asList(TAB_ACTIVE,
                             TAB_SEARCH,
                             TAB_ABORTED,
                             TAB_COMPLETED);
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
        when(presenter.createActiveTabSettings()).thenReturn(filterSettings);
        when(presenter.createCompletedTabSettings()).thenReturn(filterSettings);
        when(presenter.createAbortedTabSettings()).thenReturn(filterSettings);
        when(popoverCellInstance.get()).thenReturn(cellMock);
    }
}
