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

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.AbstractMultiGridViewTest;
import org.jbpm.workbench.es.model.RequestSummary;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.jbpm.workbench.common.client.list.AbstractMultiGridView.COL_ID_SELECT;
import static org.jbpm.workbench.es.client.editors.requestlist.RequestListViewImpl.COL_ID_ACTIONS;
import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;

@RunWith(GwtMockitoTestRunner.class)
public class RequestListViewImplTest extends AbstractMultiGridViewTest<RequestSummary> {

    @Mock
    private RequestListPresenter presenter;

    @InjectMocks
    @Spy
    private RequestListViewImpl view;

    @Override
    protected AbstractMultiGridPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected AbstractMultiGridView getView() {
        return view;
    }

    @Override
    public List<String> getExpectedInitialColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_ID,
                             COLUMN_BUSINESSKEY,
                             COLUMN_COMMANDNAME,
                             COL_ID_ACTIONS);
    }

    @Override
    public List<String> getExpectedBannedColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_ID,
                             COLUMN_COMMANDNAME,
                             COL_ID_ACTIONS);
    }

    @Override
    public Integer getExpectedNumberOfColumns() {
        return 10;
    }
}
