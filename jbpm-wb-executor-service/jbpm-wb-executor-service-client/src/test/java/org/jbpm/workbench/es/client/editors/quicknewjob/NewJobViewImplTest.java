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
package org.jbpm.workbench.es.client.editors.quicknewjob;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jboss.errai.common.client.dom.DOMTokenList;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.NumberInput;
import org.jboss.errai.common.client.dom.RadioInput;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jbpm.workbench.es.model.RequestParameterSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.FormGroup;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.client.views.pfly.widgets.SanitizedNumberInput;
import org.uberfire.ext.widgets.table.client.DataGrid;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class NewJobViewImplTest {

    @Mock
    private DataGrid<RequestParameterSummary> datagrid;

    @Mock
    private ListItem basicTab;

    @Mock
    private Div basicPane;

    @Mock
    private ListItem advancedTab;

    @Mock
    private SanitizedNumberInput jobRetriesInput;

    @Mock
    private Div advancedPane;

    @Mock
    private DOMTokenList basicTabClassList;

    @Mock
    private DOMTokenList basicPaneClassList;

    @Mock
    private DOMTokenList advancedTabClassList;

    @Mock
    private DOMTokenList advancedPaneClassList;

    @Mock
    private TextInput jobNameInput;

    @Mock
    private FormGroup jobNameGroup;

    @Mock
    private Span jobNameHelp;

    @Mock
    Div dateFiltersInput;

    @Mock
    private RadioInput jobRunNowRadio;

    @Mock
    private TextInput jobTypeInput;

    @Mock
    private FormGroup jobTypeGroup;

    @Mock
    private Span jobTypeHelp;

    @Mock
    private FormGroup jobRetriesGroup;

    @Mock
    private Span jobRetriesHelp;

    @Mock
    private Modal modal;

    @Mock
    private InlineNotification inlineNotification;

    @Mock
    private HTMLElement inlineNotificationElement;

    @Mock
    private DOMTokenList inlineNotificationClassList;

    @Mock
    private NumberInput numberInput;

    @InjectMocks
    private NewJobViewImpl view;

    @Before
    public void setupMocks() {
        when(basicTab.getClassList()).thenReturn(basicTabClassList);
        when(basicPane.getClassList()).thenReturn(basicPaneClassList);
        when(advancedTab.getClassList()).thenReturn(advancedTabClassList);
        when(advancedPane.getClassList()).thenReturn(advancedPaneClassList);
        when(inlineNotification.getElement()).thenReturn(inlineNotificationElement);
        when(inlineNotificationElement.getClassList()).thenReturn(inlineNotificationClassList);
        when(jobRetriesInput.getElement()).thenReturn(numberInput);
    }

    @Test
    public void testRedraw() {
        view.show();
        view.onAdvancedTabMouseUp(null);
        view.onAdvancedTabMouseUp(null);
        verify(datagrid,times(1)).redraw();
    }
}