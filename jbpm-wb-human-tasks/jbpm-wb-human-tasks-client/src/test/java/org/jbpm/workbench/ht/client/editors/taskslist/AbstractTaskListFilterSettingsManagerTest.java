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

import java.util.Arrays;
import java.util.List;
import javax.enterprise.event.Event;

import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.df.client.events.SavedFilterAddedEvent;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.df.client.filter.FilterSettingsJSONMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractTaskListFilterSettingsManagerTest {

    @Mock
    TranslationService translationServiceMock;

    @Mock
    protected User identity;

    @Mock
    protected FilterSettingsJSONMarshaller marshaller;

    @Spy
    protected Event<SavedFilterAddedEvent> filterSavedEvent = new EventSourceMock<>();

    @Mock
    protected UserPreferencesService preferencesService;

    protected CallerMock<UserPreferencesService> userPreferencesServiceCallerMock;

    public abstract AbstractTaskListFilterSettingsManager getFilterSettingsManager();

    public abstract String getDataSetId();

    @Before
    public void setupMocks() {
        userPreferencesServiceCallerMock = new CallerMock<>(preferencesService);
        getFilterSettingsManager().setPreferencesService(userPreferencesServiceCallerMock);
        doNothing().when(filterSavedEvent).fire(any(SavedFilterAddedEvent.class));
    }

    @Test
    public void testIsNullTableSettingsPrototype() {
        when(identity.getIdentifier()).thenReturn("user");
        FilterSettings filterSettings = getFilterSettingsManager().createFilterSettingsPrototype();
        List<DataSetOp> ops = filterSettings.getDataSetLookup().getOperationList();
        for (DataSetOp op : ops) {
            if (op.getType().equals(DataSetOpType.FILTER)) {
                List<ColumnFilter> columnFilters = ((DataSetFilter) op).getColumnFilterList();
                for (ColumnFilter columnFilter : columnFilters) {
                    assertTrue((columnFilter).toString().contains(COLUMN_ACTUAL_OWNER + " is_null"));
                }
            }
        }
    }

    @Test
    public void getVariablesTableSettingsTest() {
        FilterSettings filterSettings = getFilterSettingsManager().getVariablesFilterSettings("Test");
        List<DataSetOp> ops = filterSettings.getDataSetLookup().getOperationList();
        for (DataSetOp op : ops) {
            if (op.getType().equals(DataSetOpType.FILTER)) {
                List<ColumnFilter> columnFilters = ((DataSetFilter) op).getColumnFilterList();
                for (ColumnFilter columnFilter : columnFilters) {
                    assertTrue((columnFilter).toString().contains(COLUMN_TASK_VARIABLE_TASK_NAME + " = Test"));
                }
            }
        }
    }

    @Test
    public void testDataSetName() {
        assertEquals(getDataSetId(),
                     getFilterSettingsManager().createFilterSettingsPrototype().getDataSetLookup().getDataSetUUID());
    }

    @Test
    public void testStatusSettingsColumns() {
        final FilterSettingsBuilderHelper helper = FilterSettingsBuilderHelper.init();
        getFilterSettingsManager().commonColumnSettings().accept(helper);
        final FilterSettings filterSettings = helper.buildSettings();
        assertEquals(getDataSetExpectedColumns().size(), filterSettings.getColumnSettingsList().size());
        getDataSetExpectedColumns().forEach(c -> assertNotNull(filterSettings.getColumnSettings(c)));
    }

    protected List<String> getDataSetExpectedColumns() {
        return Arrays.asList(
                COLUMN_ACTIVATION_TIME,
                COLUMN_ACTUAL_OWNER,
                COLUMN_CREATED_BY,
                COLUMN_CREATED_ON,
                COLUMN_DEPLOYMENT_ID,
                COLUMN_DESCRIPTION,
                COLUMN_DUE_DATE,
                COLUMN_NAME,
                COLUMN_PARENT_ID,
                COLUMN_PRIORITY,
                COLUMN_PROCESS_ID,
                COLUMN_PROCESS_INSTANCE_ID,
                COLUMN_PROCESS_SESSION_ID,
                COLUMN_STATUS,
                COLUMN_TASK_ID,
                COLUMN_WORK_ITEM_ID,
                COLUMN_LAST_MODIFICATION_DATE,
                COLUMN_PROCESS_INSTANCE_CORRELATION_KEY,
                COLUMN_PROCESS_INSTANCE_DESCRIPTION
        );
    }
}
