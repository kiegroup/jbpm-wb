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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.df.client.filter.SavedFilter;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.COLUMN_ERROR_COUNT;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.HUMAN_TASKS_WITH_ADMIN_DATASET;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskAdminListFilterSettingsManagerTest extends AbstractTaskListFilterSettingsManagerTest {

    @InjectMocks
    TaskAdminListFilterSettingsManager manager;

    @Override
    public AbstractTaskListFilterSettingsManager getFilterSettingsManager() {
        return manager;
    }

    @Override
    public String getDataSetId() {
        return HUMAN_TASKS_WITH_ADMIN_DATASET;
    }

    @Override
    protected List<String> getDataSetExpectedColumns() {
        final List<String> dataSetExpectedColumns = new ArrayList<>(super.getDataSetExpectedColumns());
        dataSetExpectedColumns.add(COLUMN_ERROR_COUNT);
        return dataSetExpectedColumns;
    }

    @Test
    public void testDefaultFilters() {
        Consumer<List<SavedFilter>> callback = filters -> {
            assertEquals(2,
                         filters.size());
            assertEquals(Constants.INSTANCE.Active(),
                         filters.get(0).getName());
            assertEquals(Constants.INSTANCE.Task_Admin(),
                         filters.get(1).getName());
        };

        final MultiGridPreferencesStore store = new MultiGridPreferencesStore();
        manager.loadSavedFiltersFromPreferences(store,
                                                callback);

        verify(preferencesService).saveUserPreferences(store);
    }
}
