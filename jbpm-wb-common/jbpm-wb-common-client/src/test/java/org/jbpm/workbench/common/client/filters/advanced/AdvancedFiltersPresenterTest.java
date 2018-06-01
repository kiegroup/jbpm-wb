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

package org.jbpm.workbench.common.client.filters.advanced;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.filters.saved.SavedFilterSelectedEvent;
import org.jbpm.workbench.df.client.filter.AdvancedFilterEditor;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AdvancedFiltersPresenterTest {

    @Mock
    AdvancedFilterEditor advancedFilterEditorView;

    @Mock
    FilterSettingsManager filterSettingsManager;

    @Mock
    FilterSettings filterSettingsMock;

    @Mock
    PlaceManager placeManagerMock;

    @Mock
    EventSourceMock<SavedFilterSelectedEvent> savedFilterEvent;

    @Mock
    EventSourceMock<NotificationEvent> notificationEvent;

    AdvancedFiltersPresenter presenter;

    @Before
    public void setup() {
        when(filterSettingsManager.createFilterSettingsPrototype()).thenReturn(filterSettingsMock);
        doNothing().when(savedFilterEvent).fire(any());
        doNothing().when(notificationEvent).fire(any());
        presenter = spy(AdvancedFiltersPresenter.class);
        presenter.setFilterSettingsManager(filterSettingsManager);
        presenter.setAdvancedFilterEditorView(advancedFilterEditorView);
        presenter.setSavedFilterSelectedEvent(savedFilterEvent);
        presenter.setNotificationEvent(notificationEvent);
        presenter.setPlaceManager(placeManagerMock);
    }

    @Test
    public void testSuccessfulSaveAdvancedFilters() {
        presenter.onOpen();

        ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(advancedFilterEditorView).init(any(),
                                              captor.capture());
        final FilterSettings filterSettings = new FilterSettings();
        captor.getValue().accept(filterSettings);

        ArgumentCaptor<Consumer> captor2 = ArgumentCaptor.forClass(Consumer.class);
        verify(filterSettingsManager).saveFilterIntoPreferences(eq(filterSettings),
                                                                captor2.capture());
        captor2.getValue().accept(true);

        verify(savedFilterEvent).fire(any());
    }

    @Test
    public void testUnsuccessfulSaveAdvancedFilters() {
        presenter.onOpen();

        ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(advancedFilterEditorView).init(any(),
                                              captor.capture());
        final FilterSettings filterSettings = new FilterSettings();
        captor.getValue().accept(filterSettings);

        ArgumentCaptor<Consumer> captor2 = ArgumentCaptor.forClass(Consumer.class);
        verify(filterSettingsManager).saveFilterIntoPreferences(eq(filterSettings),
                                                                captor2.capture());
        captor2.getValue().accept(false);

        verifyNoMoreInteractions(savedFilterEvent);
        verify(advancedFilterEditorView).setTableNameError(anyString());
    }
}
