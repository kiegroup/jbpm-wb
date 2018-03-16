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

package org.jbpm.workbench.common.client.filters.active;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import elemental2.dom.HTMLInputElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SaveFilterPopoverViewTest {

    @Mock
    HTMLInputElement filterName;

    @InjectMocks
    SaveFilterPopoverView popoverView;

    @Test
    public void testOnSaveCallback() {
        final String value = "value";
        filterName.value = value;

        final ParameterizedCommand<String> callback = mock(ParameterizedCommand.class);

        popoverView.setSaveCallback(callback);

        popoverView.onSave(null);

        verify(callback).execute(value);
    }

    @Test
    public void testOnKeyPressEvent() {
        final String value = "value";
        filterName.value = value;

        final ParameterizedCommand<String> callback = mock(ParameterizedCommand.class);

        popoverView.setSaveCallback(callback);

        final KeyDownEvent event = mock(KeyDownEvent.class);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ENTER,
                                                  KeyCodes.KEY_A);
        popoverView.onKeyPressEvent(event);
        popoverView.onKeyPressEvent(event);

        verify(callback).execute(value);
    }

    @Test
    public void testCancelCallback() {
        final Command callback = mock(Command.class);

        popoverView.setCancelCallback(callback);

        popoverView.onCancel(null);

        verify(callback).execute();
    }
}
