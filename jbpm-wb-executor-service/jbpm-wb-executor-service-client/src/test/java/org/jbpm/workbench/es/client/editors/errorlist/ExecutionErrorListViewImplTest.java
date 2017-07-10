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

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.list.AbstractMultiGridViewTest;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.jbpm.workbench.es.client.editors.errorlist.ExecutionErrorListViewImpl.*;
import static org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ExecutionErrorListViewImplTest extends AbstractMultiGridViewTest<ExecutionErrorSummary> {

    @Mock
    protected ExecutionErrorListPresenter presenter;

    @InjectMocks
    @Spy
    private ExecutionErrorListViewImpl view;

    @Override
    public ExecutionErrorListViewImpl getView() {
        return view;
    }

    @Override
    public ExecutionErrorListPresenter getPresenter() {
        return presenter;
    }

    @Override
    public List<String> getExpectedTabs() {
        return Arrays.asList(TAB_SEARCH,
                             TAB_ACK,
                             TAB_ALL,
                             TAB_NEW);
    }

    @Override
    public List<String> getExpectedInitialColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_ERROR_TYPE,
                             COLUMN_PROCESS_INST_ID,
                             COLUMN_ERROR_DATE,
                             COLUMN_DEPLOYMENT_ID,
                             COL_ID_ACTIONS);
    }

    @Override
    public List<String> getExpectedBannedColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_ERROR_TYPE,
                             COLUMN_PROCESS_INST_ID,
                             COLUMN_ERROR_DATE,
                             COL_ID_ACTIONS);
    }

    @Override
    public Integer getExpectedNumberOfColumns() {
        return 15;
    }

    @Before
    @Override
    public void setupMocks() {
        super.setupMocks();
        when(presenter.createAllTabSettings()).thenReturn(filterSettings);
        when(presenter.createNewTabSettings()).thenReturn(filterSettings);
        when(presenter.createAcknowledgedTabSettings()).thenReturn(filterSettings);
    }
}
