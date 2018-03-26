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

package org.jbpm.workbench.pr.client.editors.definition.details;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.menu.PrimaryActionMenuBuilder;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessDefinitionDetailsPresenterTest {

    @Spy
    Event<ProcessDefSelectionEvent> processDefSelectionEvent = new EventSourceMock<>();

    @Spy
    Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent = new EventSourceMock<>();

    @Mock
    PrimaryActionMenuBuilder primaryActionMenuBuilder;

    @InjectMocks
    ProcessDefinitionDetailsPresenter presenter;

    @Before
    public void setup() {
        doNothing().when(changeTitleWidgetEvent).fire(any());
        doNothing().when(processDefSelectionEvent).fire(any());
    }

    @Test
    public void testOnProcessSelectionEvent() {
        presenter.onProcessSelectionEvent(new ProcessDefSelectionEvent("processId",
                                                                       "deploymentId",
                                                                       "serverTemplateId",
                                                                       "processDefName",
                                                                       false));

        verify(primaryActionMenuBuilder).setVisible(true);
        ArgumentCaptor<ChangeTitleWidgetEvent> captor = ArgumentCaptor.forClass(ChangeTitleWidgetEvent.class);
        verify(changeTitleWidgetEvent).fire(captor.capture());
        assertEquals("deploymentId - processDefName",
                     captor.getValue().getTitle());
    }
}
