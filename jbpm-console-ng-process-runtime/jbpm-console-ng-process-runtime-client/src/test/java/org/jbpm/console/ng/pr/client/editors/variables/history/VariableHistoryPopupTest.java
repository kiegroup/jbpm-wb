/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.pr.client.editors.variables.history;


import java.util.Comparator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jbpm.console.ng.pr.model.ProcessVariableSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;

import static org.junit.Assert.*;


@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class VariableHistoryPopupTest {

    public static int COLUMN_NEW_VALUE_POSITION = 0;
    public static int COLUMN_OLD_VALUE_POSITION = 1;

    @Mock
    private Pagination paginationMock;

    @Mock
    private FormControlStatic variableNameTextBoxMock;

    private VariableHistoryPopup variableHistoryPopup;

    DataGrid<ProcessVariableSummary> testDataGrid;

    @Before
    public void setupMocks() {

        testDataGrid = new DataGrid<ProcessVariableSummary>();
        variableHistoryPopup = new VariableHistoryPopup(testDataGrid, paginationMock, variableNameTextBoxMock);

    }

    @Test
    public void variableNewValueComparatorTest() {

        ProcessVariableSummary processVariableSummary1 = new ProcessVariableSummary();
        ProcessVariableSummary processVariableSummary2 = new ProcessVariableSummary();
        ProcessVariableSummary processVariableSummary3 = new ProcessVariableSummary();

        processVariableSummary1.setNewValue("A");
        processVariableSummary2.setNewValue("B");
        processVariableSummary3.setNewValue("A");

        Comparator varNewValueComparator = variableHistoryPopup.getSortHandler().getComparator(testDataGrid.getColumn(COLUMN_NEW_VALUE_POSITION));

        assertEquals(-1, varNewValueComparator.compare(processVariableSummary1, processVariableSummary2));
        assertEquals(1, varNewValueComparator.compare(processVariableSummary2, processVariableSummary1));
        assertEquals(0, varNewValueComparator.compare(processVariableSummary1, processVariableSummary3));

        processVariableSummary1.setNewValue(null);
        processVariableSummary2.setNewValue("B");

        assertEquals(-1, varNewValueComparator.compare(processVariableSummary1, processVariableSummary2));
        assertEquals(1, varNewValueComparator.compare(processVariableSummary2, processVariableSummary1));

        processVariableSummary3.setNewValue(null);
        assertEquals(0, varNewValueComparator.compare(processVariableSummary1, processVariableSummary3));

        assertEquals("", testDataGrid.getColumn(COLUMN_NEW_VALUE_POSITION).getValue(processVariableSummary3));
        assertEquals("B", testDataGrid.getColumn(COLUMN_NEW_VALUE_POSITION).getValue(processVariableSummary2));

    }

    @Test
    public void variableOldValueComparatorTest() {

        ProcessVariableSummary processVariableSummary1 = new ProcessVariableSummary();
        ProcessVariableSummary processVariableSummary2 = new ProcessVariableSummary();
        ProcessVariableSummary processVariableSummary3 = new ProcessVariableSummary();

        processVariableSummary1.setOldValue("A");
        processVariableSummary2.setOldValue("B");
        processVariableSummary3.setOldValue("A");

        Comparator varOldValueComparator = variableHistoryPopup.getSortHandler().getComparator(testDataGrid.getColumn(COLUMN_OLD_VALUE_POSITION));

        assertEquals(-1, varOldValueComparator.compare(processVariableSummary1, processVariableSummary2));
        assertEquals(1, varOldValueComparator.compare(processVariableSummary2, processVariableSummary1));
        assertEquals(0, varOldValueComparator.compare(processVariableSummary1, processVariableSummary3));

        processVariableSummary1.setOldValue(null);
        processVariableSummary2.setOldValue("B");

        assertEquals(-1, varOldValueComparator.compare(processVariableSummary1, processVariableSummary2));
        assertEquals(1, varOldValueComparator.compare(processVariableSummary2, processVariableSummary1));

        processVariableSummary3.setOldValue(null);
        assertEquals(0, varOldValueComparator.compare(processVariableSummary1, processVariableSummary3));

        assertEquals("", testDataGrid.getColumn(COLUMN_OLD_VALUE_POSITION).getValue(processVariableSummary3));
        assertEquals("B", testDataGrid.getColumn(COLUMN_OLD_VALUE_POSITION).getValue(processVariableSummary2));
    }

}
