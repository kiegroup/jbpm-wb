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

package org.jbpm.workbench.common.client.filters.basic;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.util.DateRange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Moment;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class BasicFiltersViewImplTest {

    @Mock
    TranslationService translationService;

    @InjectMocks
    BasicFiltersViewImpl view;

    @Test
    public void testDateRangeChange() {
        final Consumer<ActiveFilterItem<DateRange>> callback = mock(Consumer.class);
        final String label = "label";
        final String selectedLabel = "selectedLabel";
        final Moment startMoment = mock(Moment.class);
        when(startMoment.milliseconds(anyInt())).thenReturn(startMoment);
        final Date startDate = new Date();
        when(startMoment.asDate()).thenReturn(startDate);
        final Moment endMoment = mock(Moment.class);
        when(endMoment.milliseconds(anyInt())).thenReturn(endMoment);
        final Date endDate = new Date();
        when(endMoment.asDate()).thenReturn(endDate);

        view.onDateRangeValueChange(label,
                                    selectedLabel,
                                    startMoment,
                                    endMoment,
                                    callback);

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(callback).accept(captor.capture());

        assertEquals(1,
                     captor.getAllValues().size());
        assertEquals(label,
                     captor.getValue().getKey());
        assertEquals(label + ": " + selectedLabel,
                     captor.getValue().getLabelValue());
        assertEquals(startDate,
                     ((DateRange) captor.getValue().getValue()).getStartDate());
        assertEquals(endDate,
                     ((DateRange) captor.getValue().getValue()).getEndDate());
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
        testKeyCode(keyCode,
                    1);
    }

    protected void testKeyCode(int keyCode,
                               int wantedNumberOfInvocations) {
        final KeyboardEvent event = mock(KeyboardEvent.class);
        when(event.getKeyCode()).thenReturn(keyCode);
        view.getNumericInputListener().call(event);
        verify(event,
               times(wantedNumberOfInvocations)).preventDefault();
    }

    protected void testValidKeyCode(int keyCode) {
        testKeyCode(keyCode,
                    0);
    }

    @Test
    public void testAddActiveFilter() {
        final Consumer<ActiveFilterItem<Integer>> callback = mock(Consumer.class);
        final String labelKey = "key1";
        final String labelValue = "someValue";
        final String hint = "hint";
        final Integer value = 1;

        view.addActiveFilter(labelKey,
                             labelValue,
                             hint,
                             value,
                             callback);

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(callback).accept(captor.capture());

        assertEquals(1,
                     captor.getAllValues().size());
        final ActiveFilterItem filterItem = captor.getValue();
        assertEquals(labelKey,
                     filterItem.getKey());
        assertEquals(labelKey + ": " + labelValue,
                     filterItem.getLabelValue());
        assertEquals(hint,
                     filterItem.getHint());
        assertEquals(value,
                     filterItem.getValue());
    }

    @Test
    public void testClearAllSelectFilter(){
        final Input input = mock(Input.class);
        when(input.getChecked()).thenReturn(true, false);
        view.getSelectInputs().put("label",
                                   Arrays.asList(
                                           input,
                                           input
                                   ));

        view.clearAllSelectFilter();

        verify(input).setChecked(false);
        verify(input, times(2)).getChecked();
        verifyNoMoreInteractions(input);
    }

    @Test
    public void testClearSelectFilter(){
        final Input input = mock(Input.class);
        when(input.getChecked()).thenReturn(true, false);
        view.getSelectInputs().put("label1",
                                   Arrays.asList(
                                           input,
                                           input
                                   ));
        view.getSelectInputs().put("label2",
                                   Arrays.asList(
                                           input,
                                           input
                                   ));

        view.clearSelectFilter("label1");
        view.clearSelectFilter("label3");

        verify(input).setChecked(false);
        verify(input, times(2)).getChecked();
        verifyNoMoreInteractions(input);
    }

    @Test
    public void testCheckSelectFilter(){
        final Input input1 = mock(Input.class);
        when(input1.getChecked()).thenReturn(true);
        when(input1.getValue()).thenReturn("1");
        final Input input2 = mock(Input.class);
        when(input2.getChecked()).thenReturn(false);
        when(input2.getValue()).thenReturn("2");
        view.getSelectInputs().put("label1",
                                   Arrays.asList(
                                           input1,
                                           input2
                                   ));

        view.checkSelectFilter("label1", "1");
        view.checkSelectFilter("label1", "2");

        verify(input1, never()).setChecked(true);
        verify(input2).setChecked(true);
    }
}
