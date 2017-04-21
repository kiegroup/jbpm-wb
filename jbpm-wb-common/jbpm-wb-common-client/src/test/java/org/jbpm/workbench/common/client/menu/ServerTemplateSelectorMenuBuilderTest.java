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

package org.jbpm.workbench.common.client.menu;

import java.util.Arrays;
import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ServerTemplateSelectorMenuBuilderTest {

    @InjectMocks
    ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    @Mock
    ServerTemplateSelectorMenuBuilder.ServerTemplateSelectorView view;

    private CallerMock<SpecManagementService> specManagementServiceCaller;

    @Mock
    private SpecManagementService specManagementService;

    @Before
    public void setup() {
        specManagementServiceCaller = new CallerMock<SpecManagementService>(specManagementService);
        serverTemplateSelectorMenuBuilder.setSpecManagementService(specManagementServiceCaller);
    }

    @Test
    public void testAddServerTemplates() {
        final String serverTemplateId = "id1";
        final ServerTemplate st1 = new ServerTemplate(serverTemplateId, "kie-server-template1");
        st1.addServerInstance(new ServerInstanceKey());

        final ServerTemplate st2 = new ServerTemplate("id2", "kie-server-template2");
        st2.addServerInstance(new ServerInstanceKey());

        when(specManagementService.listServerTemplates()).thenReturn(Arrays.asList(st1, st2));
        when(view.getSelectedServerTemplate()).thenReturn(serverTemplateId);

        serverTemplateSelectorMenuBuilder.init();

        verify(specManagementService).listServerTemplates();
        verify(view).setServerTemplateChangeHandler(any(ParameterizedCommand.class));
        verify(view).removeAllServerTemplates();
        verify(view).addServerTemplate(serverTemplateId);
        verify(view).addServerTemplate("id2");
        verify(view).getSelectedServerTemplate();
        verify(view).selectServerTemplate(serverTemplateId);
        verify(view).setVisible(true);

        verifyNoMoreInteractions(view);
    }

    @Test
    public void testAddServerTemplatesSelectedRemoved() {
        final ServerTemplate st1 = new ServerTemplate("id1", "kie-server-template1");
        st1.addServerInstance(new ServerInstanceKey());

        final ServerTemplate st2 = new ServerTemplate("id2", "kie-server-template2");
        st2.addServerInstance(new ServerInstanceKey());

        when(specManagementService.listServerTemplates()).thenReturn(Arrays.asList(st1, st2));
        when(view.getSelectedServerTemplate()).thenReturn("id3");

        serverTemplateSelectorMenuBuilder.init();

        verify(specManagementService).listServerTemplates();
        verify(view).setServerTemplateChangeHandler(any(ParameterizedCommand.class));
        verify(view).removeAllServerTemplates();
        verify(view).addServerTemplate("id1");
        verify(view).addServerTemplate("id2");
        verify(view).getSelectedServerTemplate();
        verify(view).clearSelectedServerTemplate();
        verify(view).setVisible(true);

        verifyNoMoreInteractions(view);
    }

    @Test
    public void testOneServerTemplate() {
        final String serverTemplateId = "id1";
        final ServerTemplate st1 = new ServerTemplate(serverTemplateId, "kie-server-template1");
        st1.addServerInstance(new ServerInstanceKey());

        when(specManagementService.listServerTemplates()).thenReturn(Collections.singletonList(st1));

        serverTemplateSelectorMenuBuilder.init();

        verify(specManagementService).listServerTemplates();
        verify(view).setServerTemplateChangeHandler(any(ParameterizedCommand.class));
        verify(view).removeAllServerTemplates();
        verify(view).addServerTemplate(serverTemplateId);
        verify(view).selectServerTemplate(serverTemplateId);
        verify(view).setVisible(false);

        verifyNoMoreInteractions(view);
    }

    @Test
    public void testServerTemplateSelected() {
        final String serverTemplateId = "id1";
        final ServerTemplate st1 = new ServerTemplate(serverTemplateId, "kie-server-template1");
        st1.addServerInstance(new ServerInstanceKey());

        when(specManagementService.listServerTemplates()).thenReturn(Collections.singletonList(st1));
        when(view.getSelectedServerTemplate()).thenReturn(serverTemplateId);

        serverTemplateSelectorMenuBuilder.init();

        verify(specManagementService).listServerTemplates();
        verify(view).setServerTemplateChangeHandler(any(ParameterizedCommand.class));
        verify(view).removeAllServerTemplates();
        verify(view).addServerTemplate(serverTemplateId);
        verify(view).selectServerTemplate(serverTemplateId);
        verify(view).setVisible(false);

        verifyNoMoreInteractions(view);
    }

    @Test
    public void testServerTemplateSelectedRemoved() {
        final String serverTemplateId = "id1";
        final ServerTemplate st1 = new ServerTemplate(serverTemplateId, "kie-server-template1");
        st1.addServerInstance(new ServerInstanceKey());

        when(specManagementService.listServerTemplates()).thenReturn(Collections.singletonList(st1));
        when(view.getSelectedServerTemplate()).thenReturn("id2");

        serverTemplateSelectorMenuBuilder.init();

        verify(specManagementService).listServerTemplates();
        verify(view).setServerTemplateChangeHandler(any(ParameterizedCommand.class));
        verify(view).removeAllServerTemplates();
        verify(view).addServerTemplate(serverTemplateId);
        verify(view).selectServerTemplate(serverTemplateId);
        verify(view).setVisible(false);

        verifyNoMoreInteractions(view);
    }

    @Test
    public void testServerTemplateUpdatedWithoutServerInstance() {
        final String serverTemplateId = "id1";
        final ServerTemplate st1 = new ServerTemplate(serverTemplateId, "kie-server-template1");
        when(specManagementService.listServerTemplates()).thenReturn(Collections.singletonList(st1));

        serverTemplateSelectorMenuBuilder.onServerTemplateUpdated(new ServerTemplateUpdated(st1));

        verify(specManagementService).listServerTemplates();
        verify(view).removeAllServerTemplates();
        verify(view,never()).addServerTemplate(serverTemplateId);
        verify(view).setVisible(false);
        verify(view).getSelectedServerTemplate();

        verifyNoMoreInteractions(view);
    }

    @Test
    public void testServerTemplateUpdatedWithServerInstance() {
        final String serverTemplateId = "id1";
        final ServerTemplate st1 = new ServerTemplate(serverTemplateId, "kie-server-template1");
        st1.addServerInstance(new ServerInstanceKey());
        when(specManagementService.listServerTemplates()).thenReturn(Collections.singletonList(st1));

        serverTemplateSelectorMenuBuilder.onServerTemplateUpdated(new ServerTemplateUpdated(st1));

        verifyNoMoreInteractions(view);
    }

}