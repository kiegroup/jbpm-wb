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

package org.jbpm.workbench.pr.client.editors.instance.list;

import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerType;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsJSONMarshaller;
import org.jbpm.workbench.df.client.filter.SavedFilter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.mocks.CallerMock;

import static java.util.Collections.singletonList;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceListFilterSettingsManagerTest {

    @Mock
    UserPreferencesService userPreferencesService;

    Caller<UserPreferencesService> preferencesService;

    @Mock
    FilterSettingsJSONMarshaller marshaller;

    @InjectMocks
    ProcessInstanceListFilterSettingsManager manager;

    @Before
    public void init() {
        preferencesService = new CallerMock<>(userPreferencesService);
        manager.setPreferencesService(preferencesService);
    }

    @Test
    public void testDefaultFilters() {
        Consumer<List<SavedFilter>> callback = filters -> {
            assertEquals(3,
                         filters.size());
            assertEquals(Constants.INSTANCE.Active(),
                         filters.get(0).getName());
            assertEquals(Constants.INSTANCE.Completed(),
                         filters.get(1).getName());
            assertEquals(Constants.INSTANCE.Aborted(),
                         filters.get(2).getName());
        };

        final MultiGridPreferencesStore store = new MultiGridPreferencesStore();
        manager.loadSavedFiltersFromPreferences(store,
                                                callback);

        verify(userPreferencesService).saveUserPreferences(store);
    }

    /**
     * Test Filter Settings migration from String based to builder class
     */
    @Test
    public void testVariablesFilterSettings() {
        final List<Long> processIds = singletonList(1l);
        final FilterSettings settings = manager.getVariablesFilterSettings(processIds);

        assertNotNull(settings.getDataSetLookup());
        final DataSetLookup dataSetLookup = settings.getDataSetLookup();
        assertEquals("jbpmProcessInstancesWithVariables",
                     dataSetLookup.getDataSetUUID());
        assertEquals(PROCESS_INSTANCE_ID,
                     settings.getTableDefaultSortColumnId());
        assertEquals(SortOrder.ASCENDING,
                     settings.getTableDefaultSortOrder());
        assertEquals(DisplayerType.TABLE,
                     settings.getType());
        assertEquals(true,
                     settings.isFilterEnabled());
        assertEquals(true,
                     settings.isFilterSelfApplyEnabled());
        assertEquals(true,
                     settings.isFilterNotificationEnabled());
        assertEquals(-1,
                     dataSetLookup.getNumberOfRows());
        assertEquals(0,
                     dataSetLookup.getRowOffset());
        assertNotNull(dataSetLookup.getFirstFilterOp());
        final DataSetFilter firstFilterOp = dataSetLookup.getFirstFilterOp();
        assertEquals(DataSetOpType.FILTER,
                     firstFilterOp.getType());
        assertEquals(1,
                     firstFilterOp.getColumnFilterList().size());
        final ColumnFilter columnFilter = firstFilterOp.getColumnFilterList().get(0);
        assertEquals(PROCESS_INSTANCE_ID,
                     columnFilter.getColumnId());
        assertTrue(columnFilter instanceof CoreFunctionFilter);
        assertEquals(CoreFunctionType.IN,
                     ((CoreFunctionFilter) columnFilter).getType());
        assertEquals(1,
                     ((CoreFunctionFilter) columnFilter).getParameters().size());
        assertEquals(processIds.get(0),
                     ((CoreFunctionFilter) columnFilter).getParameters().get(0));

        final DataSetGroup groupOp = dataSetLookup.getLastGroupOp();
        assertEquals(DataSetOpType.GROUP,
                     groupOp.getType());
        assertEquals(false,
                     groupOp.isJoin());
        assertEquals(5,
                     groupOp.getGroupFunctions().size());

        assertEquals(PROCESS_INSTANCE_ID,
                     groupOp.getGroupFunctions().get(0).getColumnId());
        assertEquals(PROCESS_INSTANCE_ID,
                     groupOp.getGroupFunctions().get(0).getSourceId());

        assertEquals(PROCESS_NAME,
                     groupOp.getGroupFunctions().get(1).getColumnId());
        assertEquals(PROCESS_NAME,
                     groupOp.getGroupFunctions().get(1).getSourceId());

        assertEquals(VARIABLE_ID,
                     groupOp.getGroupFunctions().get(2).getColumnId());
        assertEquals(VARIABLE_ID,
                     groupOp.getGroupFunctions().get(2).getSourceId());

        assertEquals(VARIABLE_NAME,
                     groupOp.getGroupFunctions().get(3).getColumnId());
        assertEquals(VARIABLE_NAME,
                     groupOp.getGroupFunctions().get(3).getSourceId());

        assertEquals(VARIABLE_VALUE,
                     groupOp.getGroupFunctions().get(4).getColumnId());
        assertEquals(VARIABLE_VALUE,
                     groupOp.getGroupFunctions().get(4).getSourceId());

        assertEquals(5,
                     settings.getColumnSettingsList().size());
        assertEquals(PROCESS_INSTANCE_ID,
                     settings.getColumnSettingsList().get(0).getColumnId());
        assertEquals("processInstanceId",
                     settings.getColumnSettingsList().get(0).getColumnName());

        assertEquals(PROCESS_NAME,
                     settings.getColumnSettingsList().get(1).getColumnId());
        assertEquals("processName",
                     settings.getColumnSettingsList().get(1).getColumnName());

        assertEquals(VARIABLE_ID,
                     settings.getColumnSettingsList().get(2).getColumnId());
        assertEquals("variableID",
                     settings.getColumnSettingsList().get(2).getColumnName());

        assertEquals(VARIABLE_NAME,
                     settings.getColumnSettingsList().get(3).getColumnId());
        assertEquals("variableName",
                     settings.getColumnSettingsList().get(3).getColumnName());

        assertEquals(VARIABLE_VALUE,
                     settings.getColumnSettingsList().get(4).getColumnId());
        assertEquals("variableValue",
                     settings.getColumnSettingsList().get(4).getColumnName());

        assertEquals("Filtered",
                     settings.getTableName());
        assertEquals("Filtered Desc",
                     settings.getTableDescription());
        assertEquals(false,
                     settings.isEditable());

        verifyNoMoreInteractions(marshaller);
    }
}
