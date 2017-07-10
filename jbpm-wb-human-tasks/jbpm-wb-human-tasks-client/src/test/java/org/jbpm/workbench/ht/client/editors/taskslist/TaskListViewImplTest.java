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

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.ht.client.editors.taskslist.TaskListViewImpl.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskListViewImplTest extends AbstractTaskListViewTest {

    @InjectMocks
    @Spy
    private TaskListViewImpl view;

    @Mock
    private TaskListPresenter presenter;

    @Override
    public AbstractTaskListView getView() {
        return view;
    }

    @Override
    public AbstractTaskListPresenter getPresenter() {
        return presenter;
    }

    @Override
    public List<String> getExpectedTabs() {
        return Arrays.asList(TAB_SEARCH,
                             TAB_ALL,
                             TAB_GROUP,
                             TAB_PERSONAL,
                             TAB_ACTIVE);
    }

    @Before
    @Override
    public void setupMocks() {
        super.setupMocks();
        when(presenter.createActiveTabSettings()).thenReturn(filterSettings);
        when(presenter.createAllTabSettings()).thenReturn(filterSettings);
        when(presenter.createGroupTabSettings()).thenReturn(filterSettings);
        when(presenter.createPersonalTabSettings()).thenReturn(filterSettings);
    }

    @Test
    public void testLoadPreferencesRemovingAdminTab() {
        final MultiGridPreferencesStore pref = new MultiGridPreferencesStore();
        pref.getGridsId().add(TAB_ALL);
        pref.getGridsId().add(TAB_GROUP);
        pref.getGridsId().add(TAB_PERSONAL);
        pref.getGridsId().add(TAB_ACTIVE);
        pref.getGridsId().add(TAB_ADMIN);

        view.loadTabsFromPreferences(pref,
                                     presenter);

        assertFalse(pref.getGridsId().contains(TAB_ADMIN));

        assertTabAdded(TAB_SEARCH,
                       TAB_ALL,
                       TAB_GROUP,
                       TAB_PERSONAL,
                       TAB_ACTIVE);

        verify(filterPagedTable,
               never())
                .addTab(any(ExtendedPagedTable.class),
                        eq(TAB_ADMIN),
                        any(Command.class),
                        eq(false));
    }
}
