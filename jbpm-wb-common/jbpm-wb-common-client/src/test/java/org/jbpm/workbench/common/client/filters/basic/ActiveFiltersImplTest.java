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

import javax.enterprise.event.Event;

import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.active.ActiveFiltersImpl;
import org.jbpm.workbench.common.client.filters.active.ActiveFiltersView;
import org.jbpm.workbench.common.client.filters.active.ClearAllActiveFiltersEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActiveFiltersImplTest {

    @Mock
    ActiveFiltersView view;

    @Spy
    Event<ClearAllActiveFiltersEvent> clearAllActiveFiltersEvent = new EventSourceMock<>();

    @InjectMocks
    ActiveFiltersImpl activeFilters;

    @Before
    public void setup() {
        doNothing().when(clearAllActiveFiltersEvent).fire(any());
    }

    @Test
    public void testRemoveAllActiveFilters() {
        activeFilters.removeAllActiveFilters();

        verify(view).removeAllActiveFilters(false);
        verify(clearAllActiveFiltersEvent).fire(any());
    }

    @Test
    public void testRemoveAllActiveFiltersCallback() {
        doAnswer(invocation -> {
            final Command callback = (Command) invocation.getArguments()[0];
            callback.execute();
            return null;
        }).when(view).setRemoveAllFilterCallback(any());

        activeFilters.init();

        verify(view).removeAllActiveFilters(true);
        verify(clearAllActiveFiltersEvent).fire(any());
    }

    @Test
    public void testAddActiveFilter() {
        final ActiveFilterItem<Object> filter = new ActiveFilterItem<>();
        activeFilters.addActiveFilter(filter);

        verify(view).addActiveFilter(filter);
    }

    @Test
    public void testSaveFilterCallbackNull() {
        doAnswer(invocation -> {
            ParameterizedCommand<String> callback = (ParameterizedCommand<String>) invocation.getArguments()[0];
            callback.execute(null);
            return null;
        }).when(view).setSaveFilterCallback(any());

        activeFilters.setSaveFilterCallback((name, callback) -> callback.accept(null));
        activeFilters.init();

        verify(view).setSaveFilterCallback(any());
        verify(view).closeSaveFilter();
        verify(view,
               never()).setSaveFilterErrorMessage(any());
    }

    @Test
    public void testSaveFilterCallbackError() {
        final String message = "error";
        doAnswer(invocation -> {
            ParameterizedCommand<String> callback = (ParameterizedCommand<String>) invocation.getArguments()[0];
            callback.execute(null);
            return null;
        }).when(view).setSaveFilterCallback(any());

        activeFilters.setSaveFilterCallback((name, callback) -> callback.accept(message));
        activeFilters.init();

        verify(view).setSaveFilterCallback(any());
        verify(view,
               never()).closeSaveFilter();
        verify(view).setSaveFilterErrorMessage(message);
    }
}
