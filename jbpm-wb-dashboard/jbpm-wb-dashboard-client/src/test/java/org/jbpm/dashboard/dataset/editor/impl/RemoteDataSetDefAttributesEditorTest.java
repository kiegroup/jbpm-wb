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

package org.jbpm.dashboard.dataset.editor.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.list.DropDownEditor;
import org.dashbuilder.common.client.editor.list.DropDownEditor.Entry;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mocks.CallerMock;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class RemoteDataSetDefAttributesEditorTest {

    @Mock DropDownEditor queryTarget;
    @Mock DropDownEditor serverTemplates;
    @Mock ValueBoxEditor<String> dataSource;
    @Mock ValueBoxEditor<String> dbSQL;
    @Mock SpecManagementService specManagementService;
    @Mock RemoteDataSetDefAttributesEditor.View view;

    Caller<SpecManagementService> specManagementServiceCaller;
    RemoteDataSetDefAttributesEditor presenter;
    

    @Before
    public void setup() {
        when(specManagementService.listServerTemplates()).thenReturn(new ServerTemplateList(Arrays.asList(new ServerTemplate("", ""))));
        specManagementServiceCaller = new CallerMock<>(specManagementService);
        presenter = new RemoteDataSetDefAttributesEditor(queryTarget, serverTemplates, dataSource, dbSQL, view, specManagementServiceCaller);
        
        when(serverTemplates.newEntry(anyString(), anyString())).thenReturn(Mockito.mock(Entry.class));
        when(queryTarget.newEntry(anyString(), anyString())).thenReturn(Mockito.mock(Entry.class));
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).initWidgets(any(DropDownEditor.View.class), any(DropDownEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class));
        verify(dataSource, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(queryTarget, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(serverTemplates, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(dbSQL, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        
        verify(specManagementService).listServerTemplates();
        verify(queryTarget).setEntries(any());
        verify(serverTemplates).setSelectHint(anyString());
    }
    
    @Test
    public void testDataSource() {
        assertEquals(dataSource, presenter.dataSource());
    }

    @Test
    public void testQueryTarget() {
        assertEquals(queryTarget, presenter.queryTarget());
    }

    @Test
    public void testServerTemplate() {
        assertEquals(serverTemplates, presenter.serverTemplateId());
    }

    @Test
    public void testDbSQL() {
        assertEquals(dbSQL, presenter.dbSQL());
    }
}
