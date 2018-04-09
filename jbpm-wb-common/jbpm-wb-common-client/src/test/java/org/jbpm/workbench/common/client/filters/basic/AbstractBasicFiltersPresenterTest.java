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

import java.util.function.Consumer;
import javax.enterprise.event.Event;

import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.saved.SavedFilterSelectedEvent;
import org.jbpm.workbench.df.client.filter.FilterEditorPopup;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public abstract class AbstractBasicFiltersPresenterTest {

    @Mock
    BasicFiltersView view;

    @Mock
    FilterEditorPopup filterEditorPopup;

    @Mock
    FilterSettingsManager filterSettingsManager;

    @Spy
    Event<BasicFilterAddEvent> activeFilters = new EventSourceMock<>();

    @Spy
    Event<BasicFilterRemoveEvent> basicFilterRemoveEvent = new EventSourceMock<>();

    @Spy
    Event<SavedFilterSelectedEvent> savedFilterSelectedEvent = new EventSourceMock<>();

    public abstract BasicFiltersPresenter getPresenter();

    public BasicFiltersView getView() {
        return view;
    }

    @Before
    public void init() {
        doNothing().when(savedFilterSelectedEvent).fire(any());
        doNothing().when(activeFilters).fire(any());
        doNothing().when(basicFilterRemoveEvent).fire(any());
    }

    @Test
    public void testSaveAdvancedFiltersCallback() {
        doAnswer(invocation -> {
            Command callback = (Command) invocation.getArguments()[0];
            callback.execute();
            return null;
        }).when(view).setAdvancedFiltersCallback(any());

        getPresenter().init();

        verify(filterEditorPopup).setTitle(any());
        ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(filterEditorPopup).show(any(),
                                       captor.capture());
        final FilterSettings filterSettings = new FilterSettings();
        captor.getValue().accept(filterSettings);

        verify(filterSettingsManager).saveFilterIntoPreferences(eq(filterSettings),
                                                                captor.capture());
        captor.getValue().accept(true);

        verify(filterEditorPopup).hide();
        verify(savedFilterSelectedEvent).fire(any());
    }

    @Test
    public void testSaveInvalidAdvancedFiltersCallback() {
        doAnswer(invocation -> {
            Command callback = (Command) invocation.getArguments()[0];
            callback.execute();
            return null;
        }).when(view).setAdvancedFiltersCallback(any());

        getPresenter().init();

        verify(filterEditorPopup).setTitle(any());
        ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(filterEditorPopup).show(any(),
                                       captor.capture());
        final FilterSettings filterSettings = new FilterSettings();
        captor.getValue().accept(filterSettings);

        verify(filterSettingsManager).saveFilterIntoPreferences(eq(filterSettings),
                                                                captor.capture());
        captor.getValue().accept(false);

        verify(filterEditorPopup).setTableNameError(any());
    }

    @Test
    public void testSearchFilterListEmpty() {
        getPresenter().addSearchFilterList("columnId",
                                           new ActiveFilterItem<>(null,
                                                                  null,
                                                                  null,
                                                                  emptyList(),
                                                                  null));

        verify(basicFilterRemoveEvent).fire(any());
    }

    @Test
    public void testSearchFilterList() {
        getPresenter().addSearchFilterList("columnId",
                                           new ActiveFilterItem<>(null,
                                                                  null,
                                                                  null,
                                                                  singletonList("value"),
                                                                  null));

        verify(activeFilters).fire(any());
    }

    public abstract void testLoadFilters();
}
