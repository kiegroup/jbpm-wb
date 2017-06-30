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

package org.jbpm.workbench.forms.client.display.views;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.forms.client.display.GenericFormDisplayer;
import org.jbpm.workbench.forms.client.resources.AppResources;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class EmbeddedFormDisplayViewTest {

    @InjectMocks
    protected EmbeddedFormDisplayView view;
    @Mock
    private GenericFormDisplayer displayerMock;
    @Mock(name = "formContainer")
    private FlowPanel formContainer;
    @Mock(name = "formPanel")
    private FlowPanel formPanel;

    @Test
    public void displayPanelCreationTest() {
        view.display(displayerMock);

        verify(formContainer).clear();
        verify(formContainer,
               times(2)).add(any(FlowPanel.class));

        verify(formPanel).addStyleName(AppResources.INSTANCE.style().taskFormPanel());

        verify(displayerMock).getContainer();
        verify(displayerMock).getFooter();
    }
}
