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
package org.jbpm.workbench.ht.client.editors.taskslist;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.jbpm.workbench.common.client.list.AdvancedSearchTable;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(AdvancedSearchTable.class)
public class TaskAdminListViewImplTest extends AbstractTaskListViewTest {
    
    @InjectMocks
    private TaskAdminListViewImpl view;

    @Mock
    private TaskAdminListPresenter presenter;

    @Override
    public AbstractTaskListView getView(){
        return view;
    }
    
    @Override
    public AbstractTaskListPresenter getPresenter(){
        return presenter;
    }

    @Override
    public int getExpectedDefaultTabFilterCount(){
        return 2;
    }

    @Before
    public void setup() {
        when(presenter.createAdminTabSettings()).thenReturn(filterSettings);
    }

}
