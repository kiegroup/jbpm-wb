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
package org.jbpm.workbench.ht.client.editors.taskdetailsmulti;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskDetailsMultiViewImplTest {

    @Mock
    private NavTabs navTabs;

    @Mock
    TabContent tabContent;

    @Mock(name = "taskDetailsPane")
    private TabPane taskDetailsPane;

    @Mock(name = "taskDetailsTab")
    private TabListItem taskDetailsTab;

    @Mock(name = "processContextPane")
    @SuppressWarnings("unused")
    private TabPane processContextPane;

    @Mock(name = "processContextTab")
    @SuppressWarnings("unused")
    private TabListItem processContextTab;

    @Mock(name = "taskAssignmentsPane")
    @SuppressWarnings("unused")
    private TabPane taskAssignmentsPane;

    @Mock(name = "taskAssignmentsTab")
    @SuppressWarnings("unused")
    private TabListItem taskAssignmentsTab;

    @Mock(name = "taskCommentsPane")
    @SuppressWarnings("unused")
    private TabPane taskCommentsPane;

    @Mock(name = "taskCommentsTab")
    @SuppressWarnings("unused")
    private TabListItem taskCommentsTab;

    @Mock(name = "taskAdminPane")
    @SuppressWarnings("unused")
    private TabPane taskAdminPane;

    @Mock(name = "taskAdminTab")
    @SuppressWarnings("unused")
    private TabListItem taskAdminTab;

    @Mock(name = "taskLogsPane")
    private TabPane taskLogsPane;

    @Mock(name = "taskLogsTab")
    private TabListItem taskLogsTab;

    @Mock
    private TaskDetailsMultiPresenter presenter;

    @InjectMocks
    private TaskDetailsMultiViewImpl taskDetailsMultiView;

    @Before
    public void setupMocks() {
        when(navTabs.iterator()).thenReturn(new Iterator<Widget>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Widget next() {
                return null;
            }

            @Override
            public void remove() {

            }
        });
        when(tabContent.iterator()).thenReturn(new Iterator<Widget>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Widget next() {
                return null;
            }

            @Override
            public void remove() {

            }
        });
    }

    @Test
    public void displayOnlyLogTabTest() {
        taskDetailsMultiView.displayOnlyLogTab();

        verify(taskDetailsPane).setVisible(true);
        verify(taskDetailsTab).setVisible(true);
        verify(taskLogsPane).setVisible(true);
        verify(taskLogsTab).setVisible(true);
        verify(taskDetailsTab).showTab();
    }

    @Test
    public void initTabsTest() {
        taskDetailsMultiView.initTabs();

        verify(presenter).getGenericFormView();
        verify(presenter).getTaskDetailsView();
        verify(presenter).getProcessContextView();
        verify(presenter).getTaskAssignmentsView();
        verify(presenter).getTaskCommentsView();
        verify(presenter).getTaskAdminView();
        verify(presenter).getTaskLogsView();

        verify(navTabs, times(7)).add(any(TabListItem.class));
        verify(tabContent, times(7)).add(any(TabPane.class));
    }

}
