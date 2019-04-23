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
package org.jbpm.workbench.pr.client.editors.definition.list;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.AbstractMultiGridViewTest;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.jbpm.workbench.pr.client.editors.variables.list.ProcessVariableListViewImpl.COL_ID_ACTIONS;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROCESSNAME;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROCESSVERSION;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROJECT;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessDefinitionListViewTest extends AbstractMultiGridViewTest {

    @Mock
    ProcessDefinitionListPresenter presenter;

    @InjectMocks
    @Spy
    ProcessDefinitionListViewImpl view;

    @Before
    public void setup() {
        setupMocks();
    }

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
        List<String> initColumns = new ArrayList<String>();
        initColumns.add(COL_ID_PROCESSNAME);
        initColumns.add(COL_ID_PROCESSVERSION);
        initColumns.add(COL_ID_PROJECT);
        initColumns.add(COL_ID_ACTIONS);
        return initColumns;
    }

    @Override
    public List<String> getExpectedBannedColumns() {
        List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(COL_ID_PROCESSNAME);
        bannedColumns.add(COL_ID_ACTIONS);
        return bannedColumns;
    }

    @Override
    public Integer getExpectedNumberOfColumns() {
        return 4;
    }

}
