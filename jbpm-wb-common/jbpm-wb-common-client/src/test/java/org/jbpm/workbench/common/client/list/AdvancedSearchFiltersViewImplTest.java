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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.common.client.util.DateRange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.views.pfly.widgets.Moment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AdvancedSearchFiltersViewImplTest {

    @Mock
    DataBinder<List<ActiveFilterItem>> dataBinder;

    @Spy
    List<ActiveFilterItem> modelList = new ArrayList<>();

    @Mock
    TranslationService translationService;

    @InjectMocks
    AdvancedSearchFiltersViewImpl view;

    @Before
    public void setup() {
        when(dataBinder.getModel()).thenReturn(modelList);
    }

    @Test
    public void testDateRangeChange() {
        final Consumer addCallback = mock(Consumer.class);
        final String label = "label";
        final String selectedLabel = "selectedLabel";
        final Moment startMoment = mock(Moment.class);
        final Date startDate = new Date();
        when(startMoment.asDate()).thenReturn(startDate);
        final Moment endMoment = mock(Moment.class);
        final Date endDate = new Date();
        when(endMoment.asDate()).thenReturn(endDate);

        view.onDateRangeValueChange(label,
                                    selectedLabel,
                                    startMoment,
                                    endMoment,
                                    addCallback,
                                    null);

        ArgumentCaptor<ActiveFilterItem> captor = ArgumentCaptor.forClass(ActiveFilterItem.class);
        verify(modelList).add(captor.capture());
        assertEquals(1, captor.getAllValues().size());
        assertEquals(label, captor.getValue().getLabelKey());
        assertEquals(selectedLabel, captor.getValue().getLabelValue());
        assertEquals(startDate, ((DateRange)captor.getValue().getValue()).getStartDate());
        assertEquals(endDate, ((DateRange)captor.getValue().getValue()).getEndDate());
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

        view.addActiveFilter("key1",
                             "someValue",
                             "someValue",
                             null);

        assertEquals(1,
                     modelList.size());

        view.addActiveFilter("key1",
                             "anotherValue",
                             "anotherValue",
                             null);

        assertEquals(1,
                     modelList.size());
    }
}
