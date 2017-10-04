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
package org.jbpm.dashboard.renderer.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.dashboard.renderer.client.panel.DashboardScreen;
import org.jbpm.dashboard.renderer.client.panel.DashboardView;
import org.jbpm.dashboard.renderer.client.panel.ProcessDashboard;
import org.jbpm.dashboard.renderer.client.panel.TaskDashboard;
import org.jbpm.dashboard.renderer.client.panel.events.ProcessDashboardFocusEvent;
import org.jbpm.dashboard.renderer.client.panel.events.TaskDashboardFocusEvent;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DashboardScreenTest {

    @Mock
    DashboardView view;

    @Mock
    ProcessDashboard processDashboard;

    @Mock
    TaskDashboard taskDashboard;

    @Mock
    PlaceManager placeManager;

    @Mock
    ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    @Mock
    EventSourceMock<ProcessDashboardFocusEvent> processDashboardFocusEvent;

    @Mock
    EventSourceMock<TaskDashboardFocusEvent> taskDashboardFocusEvent;

    DashboardScreen presenter;


    @Before
    public void setUp() throws Exception {
        presenter = new DashboardScreen(view, processDashboard, taskDashboard, placeManager,
                serverTemplateSelectorMenuBuilder, processDashboardFocusEvent, taskDashboardFocusEvent);
    }

   @Test
    public void testShowProcesses() {
        presenter.showProcesses();
        verify(processDashboardFocusEvent).fire(any());
    }

   @Test
    public void testShowTasks() {
        presenter.showTasks();
        verify(taskDashboardFocusEvent).fire(any());
    }
}