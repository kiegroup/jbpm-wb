/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.df.client.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.df.client.filter.FilterSettingsManagerImpl.DEFAULT_FILTER_SETTINGS_KEY;
import static org.jbpm.workbench.df.client.filter.FilterSettingsManagerImpl.FILTER_TABLE_SETTINGS;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FilterSettingsManagerImplTest {

    private static String gridGlobalPreferenceKey = "DS_test";

    protected CallerMock<UserPreferencesService> userPreferencesService;

    @Mock
    protected UserPreferencesService userPreferencesServiceMock;

    @Mock
    MultiGridPreferencesStore multiGridPreferencesStore;

    @Mock
    FilterSettings filterSettingsMock;

    @Mock
    protected FilterSettingsJSONMarshaller marshaller;

    @Spy
    FilterSettingsManagerImpl filterSettingsManagerImpl;

    @Before
    public void setupMocks() {
        userPreferencesService = new CallerMock<>(userPreferencesServiceMock);
        filterSettingsManagerImpl.setPreferencesService(userPreferencesService);
        filterSettingsManagerImpl.setMarshaller(marshaller);

        when(userPreferencesServiceMock.loadUserPreferences(gridGlobalPreferenceKey,
                                                            UserPreferencesType.MULTIGRIDPREFERENCES)).thenReturn(multiGridPreferencesStore);
        Mockito.doReturn(gridGlobalPreferenceKey).when(filterSettingsManagerImpl).getGridGlobalPreferencesKey();
    }

    @Test
    public void testGetFilterSettingFirstTime() {
        Consumer<FilterSettings> filterSettingsConsumer = mock(Consumer.class);
        when(multiGridPreferencesStore.getGridSettings("key")).thenReturn(null);

        filterSettingsManagerImpl.getFilterSettings("key", filterSettingsConsumer);

        verify(userPreferencesServiceMock).loadUserPreferences(filterSettingsManagerImpl.getGridGlobalPreferencesKey(),
                                                               UserPreferencesType.MULTIGRIDPREFERENCES);
        verify(filterSettingsManagerImpl).createFilterSettingsPrototype();
    }

    @Test
    public void testGetFilterSetting() {
        String filterSettingJsonContent = "jsonContent";
        Consumer<FilterSettings> filterSettingsConsumer = mock(Consumer.class);
        HashMap<String, Object> gridsSettingsMock = mock(HashMap.class);
        when(multiGridPreferencesStore.getGridSettings("key")).thenReturn(gridsSettingsMock);
        when(gridsSettingsMock.get(FILTER_TABLE_SETTINGS)).thenReturn(filterSettingJsonContent);
        when(marshaller.fromJsonString(anyString())).thenReturn(filterSettingsMock);

        filterSettingsManagerImpl.getFilterSettings("key", filterSettingsConsumer);

        verify(userPreferencesServiceMock).loadUserPreferences(filterSettingsManagerImpl.getGridGlobalPreferencesKey(),
                                                               UserPreferencesType.MULTIGRIDPREFERENCES);
        verify(gridsSettingsMock).get(FILTER_TABLE_SETTINGS);
        verify(marshaller).fromJsonString(filterSettingJsonContent);
        verify(filterSettingsManagerImpl, never()).createFilterSettingsPrototype();
    }

    @Test
    public void testDefaultActiveFilterInitialization() {
        Consumer<FilterSettings> filterSettingsConsumer = mock(Consumer.class);
        FilterSettings filterSettingsMock1 = mock(FilterSettings.class);
        FilterSettings filterSettingsMock2 = mock(FilterSettings.class);
        when(filterSettingsManagerImpl.initDefaultFilters()).thenReturn(Arrays.asList(filterSettingsMock1, filterSettingsMock2));
        when(multiGridPreferencesStore.getDefaultGridId()).thenReturn(null);

        filterSettingsManagerImpl.defaultActiveFilterInit(filterSettingsConsumer);
        verify(filterSettingsManagerImpl).initDefaultFilters();
        verify(multiGridPreferencesStore).setDefaultGridId(DEFAULT_FILTER_SETTINGS_KEY);
        verify(filterSettingsManagerImpl).addFilterToPreferencesStore(filterSettingsMock1, multiGridPreferencesStore);
        verify(filterSettingsManagerImpl).addFilterToPreferencesStore(filterSettingsMock2, multiGridPreferencesStore);

        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        ArgumentCaptor<Consumer> consumerArgumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(filterSettingsManagerImpl).saveMultiGridPreferencesStore(eq(multiGridPreferencesStore), commandArgumentCaptor.capture());

        when(multiGridPreferencesStore.getDefaultGridId()).thenReturn(DEFAULT_FILTER_SETTINGS_KEY);
        commandArgumentCaptor.getValue().execute();
        verify(filterSettingsManagerImpl).getFilterSettings(eq(DEFAULT_FILTER_SETTINGS_KEY), consumerArgumentCaptor.capture());
        consumerArgumentCaptor.getValue().accept(filterSettingsMock1);
        verify(filterSettingsConsumer).accept(filterSettingsMock1);
    }

    @Test
    public void testDefaultActiveFilterLoading() {
        String defaultKey = "defaultFilter";
        Consumer<FilterSettings> filterSettingsConsumer = mock(Consumer.class);
        when(multiGridPreferencesStore.getDefaultGridId()).thenReturn(defaultKey);

        filterSettingsManagerImpl.defaultActiveFilterInit(filterSettingsConsumer);
        verify(filterSettingsManagerImpl, never()).initDefaultFilters();

        ArgumentCaptor<Consumer> consumerArgumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(filterSettingsManagerImpl).getFilterSettings(eq(defaultKey), consumerArgumentCaptor.capture());

        consumerArgumentCaptor.getValue().accept(filterSettingsMock);
        verify(filterSettingsConsumer).accept(filterSettingsMock);
    }

    @Test
    public void testSaveDefaultActiveFilter() {
        String newDefaultFilter = "newDefaultActiveFilter";
        Command callbackMock = mock(Command.class);

        filterSettingsManagerImpl.saveDefaultActiveFilter(newDefaultFilter, callbackMock);

        verify(userPreferencesServiceMock).loadUserPreferences(gridGlobalPreferenceKey, UserPreferencesType.MULTIGRIDPREFERENCES);
        verify(userPreferencesServiceMock).saveUserPreferences(multiGridPreferencesStore);
        verify(multiGridPreferencesStore).setDefaultGridId(newDefaultFilter);
        verify(callbackMock).execute();
    }

    @Test
    public void testRemoveDefaultSavedFilter() {
        String defaultFilterKey = "key";
        String nextSavedFilterKey = "next";
        ArrayList<String> savedFiltersIds = new ArrayList<>();
        savedFiltersIds.addAll(Arrays.asList(nextSavedFilterKey, "filter2", "filter3"));

        when(multiGridPreferencesStore.getDefaultGridId()).thenReturn(defaultFilterKey);
        when(multiGridPreferencesStore.getGridsId()).thenReturn(savedFiltersIds);

        filterSettingsManagerImpl.removeSavedFilter(multiGridPreferencesStore, defaultFilterKey);

        verify(multiGridPreferencesStore).removeTab(defaultFilterKey);
        verify(multiGridPreferencesStore).setDefaultGridId(nextSavedFilterKey);
    }

    @Test
    public void testRemoveLastSavedFilter() {
        String defaultFilterKey = "key";
        ArrayList<String> savedFiltersIds = new ArrayList<>();

        when(multiGridPreferencesStore.getDefaultGridId()).thenReturn(defaultFilterKey);
        when(multiGridPreferencesStore.getGridsId()).thenReturn(savedFiltersIds);

        filterSettingsManagerImpl.removeSavedFilter(multiGridPreferencesStore, defaultFilterKey);

        verify(multiGridPreferencesStore).removeTab(defaultFilterKey);
        verify(multiGridPreferencesStore).setDefaultGridId(null);
    }

    @Test
    public void testRemoveNotDefaultSavedFilter() {
        String defaultFilterKey = "defaultKey";
        String key = "key";
        ArrayList<String> savedFiltersIds = new ArrayList<>();
        savedFiltersIds.addAll(Arrays.asList("filter1", "filter2", defaultFilterKey));

        when(multiGridPreferencesStore.getDefaultGridId()).thenReturn(defaultFilterKey);
        when(multiGridPreferencesStore.getGridsId()).thenReturn(savedFiltersIds);

        filterSettingsManagerImpl.removeSavedFilter(multiGridPreferencesStore, key);

        verify(multiGridPreferencesStore).removeTab(key);
        verify(multiGridPreferencesStore, never()).setDefaultGridId(anyString());
    }
}
