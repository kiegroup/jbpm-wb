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

package org.jbpm.workbench.common.client.list;

import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.common.client.util.DateRange;
import org.jbpm.workbench.common.client.util.UTCDateBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AdvancedSearchFiltersViewImplTest {

    @Mock
    DataBinder<List<ActiveFilterItem>> dataBinder;

    @Mock
    List<ActiveFilterItem> modelList;

    @Mock
    TranslationService translationService;

    @InjectMocks
    AdvancedSearchFiltersViewImpl view;

    @Before
    public void setup() {
        when(dataBinder.getModel()).thenReturn(modelList);
    }

    @Test
    public void testDateChangeNoValue() {
        UTCDateBox fromDate = mock(UTCDateBox.class);
        when(fromDate.getValue()).thenReturn(null,
                                             null,
                                             1l);
        UTCDateBox toDate = mock(UTCDateBox.class);
        when(toDate.getValue()).thenReturn(null,
                                           1l,
                                           null);

        view.onDateValueChange("",
                               fromDate,
                               toDate,
                               null,
                               null);
        view.onDateValueChange("",
                               fromDate,
                               toDate,
                               null,
                               null);
        view.onDateValueChange("",
                               fromDate,
                               toDate,
                               null,
                               null);

        verifyZeroInteractions(dataBinder);
        verifyZeroInteractions(modelList);
    }

    @Test
    public void testDateChange() {
        UTCDateBox fromDate = mock(UTCDateBox.class);
        when(fromDate.getValue()).thenReturn(System.currentTimeMillis());
        UTCDateBox toDate = mock(UTCDateBox.class);
        when(toDate.getValue()).thenReturn(System.currentTimeMillis());
        Consumer addCallback = mock(Consumer.class);

        view.onDateValueChange("",
                               fromDate,
                               toDate,
                               addCallback,
                               null);

        verify(modelList).add(any(ActiveFilterItem.class));
        verify(fromDate).setValue(null);
        verify(toDate).setValue(null);
        verify(addCallback).accept(any(DateRange.class));
    }

    @Test
    public void testNumericInput() {
        testValidKeyCode(KeyCodes.KEY_BACKSPACE);
        testValidKeyCode(KeyCodes.KEY_NINE);
        testValidKeyCode(KeyCodes.KEY_NUM_EIGHT);
        testValidKeyCode(KeyCodes.KEY_NUM_ZERO);

        testInvalidKeyCode(KeyCodes.KEY_NUM_MINUS);
        testInvalidKeyCode(KeyCodes.KEY_NUM_PLUS);
        testInvalidKeyCode(KeyCodes.KEY_SPACE);
        testInvalidKeyCode(KeyCodes.KEY_INSERT);
    }

    protected void testInvalidKeyCode(int keyCode) {
        testKeyCode(keyCode, 1);
    }

    protected void testKeyCode(int keyCode, int wantedNumberOfInvocations) {
        final KeyboardEvent event = mock(KeyboardEvent.class);
        when(event.getKeyCode()).thenReturn(keyCode);
        view.getNumericInputListener().call(event);
        verify(event,
               times(wantedNumberOfInvocations)).preventDefault();
    }

    protected void testValidKeyCode(int keyCode) {
        testKeyCode(keyCode, 0);
    }
}
