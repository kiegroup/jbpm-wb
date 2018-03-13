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

import java.util.ArrayList;
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

        when(dataGrid.getVisibleItems()).thenReturn(asList(gs,
                                                           gs));

        table.setItemSelection(gs,
                               true);

        assertTrue(table.hasSelectedItems());
    }

    @Test
    public void testDeselectAllItems() {
        GenericSummary gs = mock(GenericSummary.class);
        when(dataGrid.getVisibleItems()).thenReturn(asList(gs,
                                                           gs));

        table.deselectAllItems();

        assertTrue(table.getSelectedItems().isEmpty());
    }

    @Test
    public void setItemSelectedSelectionTest() {
        GenericSummary gs_selected = mock(GenericSummary.class);

        List<GenericSummary> selectedItems = new ArrayList<>();
        selectedItems.add(gs_selected);
        table.setSelectedItems(selectedItems);
        checkSelectedItemsContent(1,
                                  (GenericSummary) table.getSelectedItems().get(0));

        table.setItemSelection(gs_selected,
                               true);
        checkSelectedItemsContent(1,
                                  (GenericSummary) table.getSelectedItems().get(0));

        table.setItemSelection(gs_selected,
                               false);
        checkSelectedItemsContent(0);
    }

    @Test
    public void setItemNotSelectedSelectionTest() {
        GenericSummary gs_selected = mock(GenericSummary.class);
        GenericSummary gs_new_selection = mock(GenericSummary.class);
        List<GenericSummary> selectedItems = new ArrayList<>();
        selectedItems.add(gs_selected);
        table.setSelectedItems(selectedItems);
        checkSelectedItemsContent(1,
                                  (GenericSummary) table.getSelectedItems().get(0));
        table.setItemSelection(gs_new_selection,
                               true);
        checkSelectedItemsContent(2,
                                  (GenericSummary) table.getSelectedItems().get(0),
                                  (GenericSummary) table.getSelectedItems().get(1));
        table.setItemSelection(gs_new_selection,
                               false);
        checkSelectedItemsContent(1,
                                  (GenericSummary) table.getSelectedItems().get(0));
    }

    private void checkSelectedItemsContent(int size,
                                           GenericSummary... genericSummary) {
        assertEquals(size,
                     table.getSelectedItems().size());
        for (int i = 0; i < genericSummary.length; i++) {
            assertEquals(genericSummary[i],
                         table.getSelectedItems().get(i));
        }
    }

    @Test
    public void testIsAllItemsSelected() {
        final GenericSummary gs = mock(GenericSummary.class);

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

        table.setItemSelection(gs,
                               true);

        assertTrue(table.isAllItemsSelected());

        table.setItemSelection(gs,
                               false);

        assertFalse(table.isAllItemsSelected());
    }
}
