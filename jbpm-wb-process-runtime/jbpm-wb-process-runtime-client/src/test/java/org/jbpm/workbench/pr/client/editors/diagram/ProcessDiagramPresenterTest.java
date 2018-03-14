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

package org.jbpm.workbench.pr.client.editors.diagram;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.service.ProcessImageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessDiagramPresenterTest {

    @Mock
    ProcessImageService imageService;

    @Mock
    ProcessDiagramWidgetView view;

    @InjectMocks
    ProcessDiagramPresenter presenter;

    @Before
    public void setup() {
        presenter.setProcessImageService(new CallerMock<>(imageService));
    }

    @Test
    public void testEmptyProcessDiagram() {
        when(imageService.getProcessDiagram(any(),
                                            any(),
                                            any())).thenReturn("",
                                                               null);

        presenter.onProcessSelectionEvent(new ProcessDefSelectionEvent());
        presenter.onProcessSelectionEvent(new ProcessDefSelectionEvent());

        verify(view,
               times(2)).displayMessage(Constants.INSTANCE.Process_Diagram_Not_Found());
    }

    @Test
    public void testProcessDiagram() {
        final String svgContent = "<svg></svg>";
        when(imageService.getProcessDiagram(any(),
                                            any(),
                                            any())).thenReturn(svgContent);

        presenter.onProcessSelectionEvent(new ProcessDefSelectionEvent());

        verify(view,
               never()).displayMessage(Constants.INSTANCE.Process_Diagram_Not_Found());
        verify(view).displayImage(svgContent);
    }

    @Test
    public void testEmptyProcessInstanceDiagram() {
        when(imageService.getProcessInstanceDiagram(any(),
                                                    any(),
                                                    any())).thenReturn("",
                                                                       null);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(null,
                                                                                    1l,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    null));
        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(null,
                                                                                    1l,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    null));

        verify(view,
               times(2)).displayMessage(Constants.INSTANCE.Process_Diagram_Not_Found());
    }

    @Test
    public void testProcessInstanceDiagram() {
        final String svgContent = "<svg></svg>";
        when(imageService.getProcessInstanceDiagram(any(),
                                                    any(),
                                                    any())).thenReturn(svgContent,
                                                                       null);

        presenter.onProcessInstanceSelectionEvent(new ProcessInstanceSelectionEvent(null,
                                                                                    1l,
                                                                                    null,
                                                                                    null,
                                                                                    null,
                                                                                    null));

        verify(view,
               never()).displayMessage(Constants.INSTANCE.Process_Diagram_Not_Found());
        verify(view).displayImage(svgContent);
    }
}
