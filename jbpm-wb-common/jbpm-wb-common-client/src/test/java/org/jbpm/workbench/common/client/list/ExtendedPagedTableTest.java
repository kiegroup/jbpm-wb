/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.common.client.list;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.model.GenericSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.table.client.DataGrid;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ExtendedPagedTableTest {

    @Mock
    GridGlobalPreferences preferences;

    @Mock
    DataGrid dataGrid;

    ExtendedPagedTable table;

    @Before
    public void setup() {
        table = new ExtendedPagedTable(preferences);
        table.dataGrid = dataGrid;
    }

    @Test
    public void testHasSelectedItems() {
        when(dataGrid.getVisibleItems()).thenReturn(emptyList());

        assertFalse(table.hasSelectedItems());

        GenericSummary gs = mock(GenericSummary.class);
        when(gs.isSelected()).thenReturn(true,
                                         false);
        when(dataGrid.getVisibleItems()).thenReturn(asList(gs,
                                                           gs));

        assertTrue(table.hasSelectedItems());
    }

    @Test
    public void testDeselectAllItems() {
        GenericSummary gs = mock(GenericSummary.class);
        when(dataGrid.getVisibleItems()).thenReturn(asList(gs,
                                                           gs));

        table.deselectAllItems();

        verify(gs,
               times(2)).setSelected(false);
    }

    @Test
    public void testGetSelectedItems() {
        GenericSummary gs = mock(GenericSummary.class);
        when(gs.isSelected()).thenReturn(true,
                                         false);
        when(dataGrid.getVisibleItems()).thenReturn(asList(gs,
                                                           gs));

        List<GenericSummary> selected = table.getSelectedItems();

        assertNotNull(selected);
        assertEquals(1,
                     selected.size());
    }

    @Test
    public void testIsAllItemsSelected() {
        final GenericSummary gs = mock(GenericSummary.class);
        when(gs.isSelected()).thenReturn(true,
                                         true,
                                         false);

        final List<GenericSummary> list1 = emptyList();
        final List<GenericSummary> list2 = asList(gs,
                                                  gs);
        final List<GenericSummary> list3 = singletonList(gs);
        when(dataGrid.getVisibleItemCount()).thenReturn(list1.size(),
                                                        list2.size(),
                                                        list3.size());
        when(dataGrid.getVisibleItems()).thenReturn(list2,
                                                    list3);

        assertFalse(table.isAllItemsSelected());
        assertTrue(table.isAllItemsSelected());
        assertFalse(table.isAllItemsSelected());
    }
}
