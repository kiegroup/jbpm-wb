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

package org.jbpm.workbench.forms.client.display.views.display;

import org.jbpm.workbench.forms.client.display.GenericFormDisplayer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmbeddedFormDisplayerTest {

    public static final String ERROR_HEADER = "Error header.";
    public static final String ERROR_MESSAGE = "Cannot render form.";

    @Mock
    private EmbeddedFormDisplayerView view;

    @Mock
    private Command onCloseCommand;

    @Mock
    private GenericFormDisplayer formDisplayer;

    private EmbeddedFormDisplayer displayer;

    @Before
    public void init() {
        displayer = new EmbeddedFormDisplayer(view);

        displayer.setOnCloseCommand(onCloseCommand);
    }

    @Test
    public void testDisplayForm() {
        displayer.display(formDisplayer);
        verify(view).display(formDisplayer);

        assertEquals(formDisplayer, displayer.getCurrentDisplayer());

        assertEquals(onCloseCommand, displayer.getOnCloseCommand());

        displayer.asWidget();
        verify(view).asWidget();
    }

    @Test
    public void testShowErrorMessage() {
        displayer.displayErrorMessage(ERROR_HEADER, ERROR_MESSAGE);
        verify(view).showErrorMessage(ERROR_HEADER, ERROR_MESSAGE);
    }
}
