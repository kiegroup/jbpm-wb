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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.COLUMN_ERROR_COUNT;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.HUMAN_TASKS_WITH_ADMIN_DATASET;

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
}
