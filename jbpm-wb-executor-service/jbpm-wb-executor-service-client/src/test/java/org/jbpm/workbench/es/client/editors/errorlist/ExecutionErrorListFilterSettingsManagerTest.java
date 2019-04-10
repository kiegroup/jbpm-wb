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

package org.jbpm.workbench.es.client.editors.errorlist;

import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsJSONMarshaller;
import org.jbpm.workbench.df.client.filter.SavedFilter;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.mocks.CallerMock;

import static org.jbpm.workbench.df.client.filter.FilterSettingsManagerImpl.DEFAULT_FILTER_SETTINGS_KEY;
import static org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants.COLUMN_ERROR_ACK;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ExecutionErrorListFilterSettingsManagerTest {

    @Mock
    UserPreferencesService userPreferencesService;

    @Mock
    FilterSettingsJSONMarshaller marshaller;

    Caller<UserPreferencesService> preferencesService;

    @InjectMocks
    ExecutionErrorListFilterSettingsManager manager;

    @Before
    public void init() {
        preferencesService = new CallerMock<>(userPreferencesService);
        manager.setPreferencesService(preferencesService);
    }

    @Test
    public void testDefaultFilters() {
        Consumer<List<SavedFilter>> callback = filters -> {
            assertEquals(2,
                         filters.size());
            assertEquals(Constants.INSTANCE.New(), filters.get(0).getName());
            assertEquals(DEFAULT_FILTER_SETTINGS_KEY, filters.get(0).getKey());
            assertEquals(Constants.INSTANCE.Acknowledged(), filters.get(1).getName());
        };

        final MultiGridPreferencesStore store = new MultiGridPreferencesStore();
        manager.loadSavedFiltersFromPreferences(store, callback);

        verify(userPreferencesService).saveUserPreferences(store);
    }

    @Test
    public void testInitDefaultFilters() {
        final List<FilterSettings> settings = manager.initDefaultFilters();
        assertNotNull(settings);
        assertEquals(2,
                     settings.size());
        final ColumnFilter columnFilter = settings.get(0).getDataSetLookup().getFirstFilterOp().getColumnFilterList().get(0);
        assertEquals(COLUMN_ERROR_ACK,
                     columnFilter.getColumnId());
        assertTrue(columnFilter instanceof CoreFunctionFilter);
        assertTrue(((CoreFunctionFilter) columnFilter).getParameters().get(0) instanceof Integer);
        assertEquals(DEFAULT_FILTER_SETTINGS_KEY, settings.get(0).getKey());
    }
}
