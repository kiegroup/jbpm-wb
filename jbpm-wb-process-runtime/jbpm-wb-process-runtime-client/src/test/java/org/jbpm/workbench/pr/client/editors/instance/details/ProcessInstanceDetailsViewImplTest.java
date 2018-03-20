/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.client.editors.instance.details;

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
public class ProcessInstanceDetailsViewImplTest {

    @Mock
    TabContent tabContent;

    @Mock
    private NavTabs navTabs;

    @Mock(name = "instanceDetailsTab")
    private TabListItem instanceDetailsTab;

    @Mock(name = "instanceDetailsPane")
    private TabPane instanceDetailsPane;

    @Mock(name = "documentTab")
    @SuppressWarnings("unused")
    private TabListItem documentTab;

    @Mock(name = "documentPane")
    @SuppressWarnings("unused")
    private TabPane documentPane;

    @Mock(name = "logsTab")
    private TabListItem logsTab;

    @Mock(name = "logsPane")
    private TabPane logsPane;

    @Mock(name = "diagramTab")
    private TabListItem diagramTab;

    @Mock(name = "diagramPane")
    private TabPane diagramPane;

    @Mock
    private ProcessInstanceDetailsPresenter presenter;

    @InjectMocks
    private ProcessInstanceDetailsViewImpl processInstanceDetailsMultiView;

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
        processInstanceDetailsMultiView.displayOnlyLogTab();

        verify(instanceDetailsPane).setVisible(true);
        verify(instanceDetailsTab).setVisible(true);
        verify(logsPane).setVisible(true);
        verify(logsTab).setVisible(true);
        verify(diagramPane).setVisible(true);
        verify(diagramTab).setVisible(true);
        verify(instanceDetailsTab).showTab();
    }

    @Test
    public void initTabsTest() {
        processInstanceDetailsMultiView.initTabs();

        verify(presenter).getProcessInstanceView();
        verify(presenter).getProcessVariablesView();
        verify(presenter).getDocumentView();
        verify(presenter).getLogsView();
        verify(presenter).getProcessDiagramView();

        verify(navTabs,
               times(5)).add(any(TabListItem.class));
        verify(tabContent,
               times(5)).add(any(TabPane.class));
    }
}
