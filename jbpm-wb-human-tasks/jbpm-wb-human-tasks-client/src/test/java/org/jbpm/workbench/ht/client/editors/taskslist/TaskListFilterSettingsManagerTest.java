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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;

import static org.jbpm.workbench.ht.client.editors.taskslist.TaskListFilterSettingsManager.TAB_ADMIN;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.HUMAN_TASKS_WITH_USER_DATASET;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

    @Test
    public void testLoadPreferencesRemovingAdminTab() {
        final MultiGridPreferencesStore pref = new MultiGridPreferencesStore();
        pref.getGridsId().add(TAB_ADMIN);

        manager.loadSavedFiltersFromPreferences(pref,
                                                null);

        ArgumentCaptor<MultiGridPreferencesStore> captor = ArgumentCaptor.forClass(MultiGridPreferencesStore.class);
        verify(preferencesService, times(2)).saveUserPreferences(captor.capture());

        assertFalse(captor.getAllValues().get(0).getGridsId().contains(TAB_ADMIN));
    }
}
