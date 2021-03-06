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

package org.jbpm.workbench.ht.client.editors.taskslist;

import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.df.client.filter.SavedFilter;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;

import static org.jbpm.workbench.df.client.filter.FilterSettingsManagerImpl.DEFAULT_FILTER_SETTINGS_KEY;
import static org.jbpm.workbench.ht.client.editors.taskslist.TaskListFilterSettingsManager.DATA_SET_TASK_LIST_PREFIX;
import static org.jbpm.workbench.ht.client.editors.taskslist.TaskListFilterSettingsManager.TAB_ADMIN;
import static org.jbpm.workbench.ht.client.editors.taskslist.TaskListFilterSettingsManager.TAB_ALL;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.HUMAN_TASKS_WITH_USER_DATASET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class TaskListFilterSettingsManagerTest extends AbstractTaskListFilterSettingsManagerTest {

    @InjectMocks
    TaskListFilterSettingsManager manager;

    @Override
    public AbstractTaskListFilterSettingsManager getFilterSettingsManager() {
        return manager;
    }

    @Override
    public String getDataSetId() {
        return HUMAN_TASKS_WITH_USER_DATASET;
    }

    @Before
    public void setup() {
        super.setupMocks();
    }

    @Test
    public void testLoadPreferencesRemovingFilterAdmin() {
        testRemoveFilter(TAB_ADMIN);
    }

    @Test
    public void testLoadPreferencesRemovingFilterAll() {
        testRemoveFilter(TAB_ALL);
    }

    private void testRemoveFilter(final String name){
        final MultiGridPreferencesStore pref = new MultiGridPreferencesStore();
        pref.getGridsId().add(name);

        manager.loadSavedFiltersFromPreferences(pref,
                                                null);

        ArgumentCaptor<MultiGridPreferencesStore> captor = ArgumentCaptor.forClass(MultiGridPreferencesStore.class);
        verify(preferencesService,
               times(2)).saveUserPreferences(captor.capture());

        assertFalse(captor.getAllValues().get(0).getGridsId().contains(name));
    }

    @Test
    public void testDefaultFilters() {
        Consumer<List<SavedFilter>> callback = filters -> {
            assertEquals(3, filters.size());
            assertEquals(Constants.INSTANCE.Active(), filters.get(0).getName());
            assertEquals(DATA_SET_TASK_LIST_PREFIX + "_" + DEFAULT_FILTER_SETTINGS_KEY, filters.get(0).getKey());
            assertEquals(Constants.INSTANCE.Personal(), filters.get(1).getName());
            assertEquals(Constants.INSTANCE.Group(), filters.get(2).getName());
        };

        final MultiGridPreferencesStore store = new MultiGridPreferencesStore();
        manager.loadSavedFiltersFromPreferences(store, callback);

        verify(preferencesService).saveUserPreferences(store);
    }
}
