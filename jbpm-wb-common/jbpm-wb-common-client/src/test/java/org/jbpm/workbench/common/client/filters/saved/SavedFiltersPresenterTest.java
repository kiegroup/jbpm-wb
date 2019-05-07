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

package org.jbpm.workbench.common.client.filters.saved;

import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.df.client.events.SavedFilterAddedEvent;
import org.jbpm.workbench.df.client.filter.FilterSettingsManager;
import org.jbpm.workbench.df.client.filter.SavedFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mvp.Command;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SavedFiltersPresenterTest {

    @Mock
    FilterSettingsManager filterSettingsManager;

    @Mock
    SavedFiltersViewImpl view;

    @Spy
    @InjectMocks
    SavedFiltersPresenter presenter;

    @Test
    public void testLoadSavedFilters() {
        final SavedFilter filter = new SavedFilter("key", "name");
        doAnswer(invocation -> {
            Consumer<List<SavedFilter>> filters = (Consumer<List<SavedFilter>>) invocation.getArguments()[0];
            filters.accept(singletonList(filter));
            return null;
        }).when(filterSettingsManager).loadSavedFilters(any());

        presenter.loadSavedFilters();

        verify(view).addSavedFilter(filter);
    }

    @Test
    public void testRemoveSavedFilters() {
        final SavedFilter filter = new SavedFilter("key", "name");
        doAnswer(invocation -> {
            Command callback = (Command) invocation.getArguments()[1];
            callback.execute();
            return null;
        }).when(filterSettingsManager).removeSavedFilterFromPreferences(anyString(), any());

        presenter.removeSavedFilter(filter);

        verify(view).removeSavedFilter(filter);
        verify(filterSettingsManager).removeSavedFilterFromPreferences(eq(filter.getKey()), any());
        verify(view, never()).setFirstFilterAsDefault();
    }

    @Test
    public void testRemoveDefaultSavedFilters() {
        final SavedFilter filter = new SavedFilter("key", "name");
        filter.setDefaultFilter(true);
        doAnswer(invocation -> {
            Command callback = (Command) invocation.getArguments()[1];
            callback.execute();
            return null;
        }).when(filterSettingsManager).removeSavedFilterFromPreferences(anyString(), any());

        presenter.removeSavedFilter(filter);

        verify(view).removeSavedFilter(filter);
        verify(filterSettingsManager).removeSavedFilterFromPreferences(eq(filter.getKey()), any());
        verify(view).setFirstFilterAsDefault();
    }

    @Test
    public void testOnRestoreFilters() {
        presenter.onRestoreFilters();

        verify(view).removeAllSavedFilters();
        verify(filterSettingsManager).resetDefaultSavedFilters(any());
    }

    @Test
    public void testOnSaveFilter() {
        final SavedFilter filter = new SavedFilter("key", "name");
        presenter.onSaveFilter(new SavedFilterAddedEvent(filter));

        verify(view).addSavedFilter(filter);
    }

    @Test
    public void testMenus() {
        presenter.getMenus(menus -> assertEquals(1,
                                                 menus.getItems().size()));
    }

    @Test
    public void testOnSaveDefaultActiveFilter() {
        String defaultFilterKey = "filterKey";
        final SavedFilter filter = new SavedFilter(defaultFilterKey, "name");
        doAnswer(invocation -> {
            Command callback = (Command) invocation.getArguments()[1];
            callback.execute();
            return null;
        }).when(filterSettingsManager).saveDefaultActiveFilter(anyString(), any());

        presenter.onSaveDefaultActiveFilter(new SavedFilterAsDefaultActiveEvent(filter));

        verify(filterSettingsManager).saveDefaultActiveFilter(eq(filter.getKey()), any());
        verify(view).updateSavedFiltersDefault(defaultFilterKey);
    }
}
