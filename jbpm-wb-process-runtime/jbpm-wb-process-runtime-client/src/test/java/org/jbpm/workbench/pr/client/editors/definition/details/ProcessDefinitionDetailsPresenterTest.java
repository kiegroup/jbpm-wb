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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.menu.PrimaryActionMenuBuilder;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

import static org.jbpm.workbench.common.client.PerspectiveIds.SEARCH_PARAMETER_PROCESS_DEFINITION_ID;
import static org.junit.Assert.*;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_INSTANCES;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessDefinitionDetailsPresenterTest {

    @InjectMocks
    ProcessDefinitionDetailsPresenter presenter;

    @Mock
    EventSourceMock<ProcessDefSelectionEvent> processDefSelectionEvent = new EventSourceMock<ProcessDefSelectionEvent>();

    @Mock
    PlaceManager placeManager;

    @Mock
    PrimaryActionMenuBuilder primaryActionMenuBuilder;

    @Mock
    ProcessDefinitionDetailsPresenter.ProcessDefinitionDetailsView view;

    @Test
    public void testViewInstance() {
        //Select process
        final String process = "evaluation";
        presenter.onProcessSelectionEvent(new ProcessDefSelectionEvent(process));

        //Navigate to process
        presenter.viewProcessInstances();

        final ArgumentCaptor<PlaceRequest> captor = ArgumentCaptor.forClass(PlaceRequest.class);
        verify(placeManager).goTo(captor.capture());
        final PlaceRequest request = captor.getValue();
        assertEquals(PROCESS_INSTANCES,
                     request.getIdentifier());
        assertEquals(process,
                     request.getParameter(SEARCH_PARAMETER_PROCESS_DEFINITION_ID,
                                          null));
    }
}