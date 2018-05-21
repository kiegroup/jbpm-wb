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
package org.jbpm.workbench.ht.client.editors.taskslist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.cellview.client.RowStyles;
import org.jbpm.workbench.common.client.list.AbstractMultiGridViewTest;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.ht.client.resources.HumanTaskResources;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.jbpm.workbench.common.client.list.AbstractMultiGridView.COL_ID_ACTIONS;
import static org.jbpm.workbench.ht.util.TaskStatus.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.COLUMN_NAME;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractTaskListViewTest extends AbstractMultiGridViewTest<TaskSummary> {

    public abstract AbstractTaskListView getView();

    public abstract AbstractTaskListPresenter getPresenter();

    @Override
    public List<String> getExpectedBannedColumns() {
        return Arrays.asList(COLUMN_NAME,
                             COL_ID_ACTIONS);
    }

    @Override
    public Integer getExpectedNumberOfColumns() {
        return 15;
    }

    @Test
    public void addDomainSpecifColumnsTest() {
        final ListTable<TaskSummary> currentListGrid = spy(new ListTable<>(new GridGlobalPreferences()));
        final Set<String> domainColumns = new HashSet<String>();
        domainColumns.add("var1");
        domainColumns.add("var2");
        domainColumns.add("var3");
        getView().addDomainSpecifColumns(currentListGrid,
                                         domainColumns);

        final ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(currentListGrid).addColumns(argument.capture());

        final List<ColumnMeta> columns = argument.getValue();
        assertTrue(columns.size() == 3);
        int i = 0;
        for (String domainColumn : domainColumns) {
            assertEquals(columns.get(i).getCaption(),
                         domainColumn);
            i++;
        }
    }

    @Test
    public void testStylesNotAppliedDependingOnPriority() {
        final ListTable<TaskSummary> currentListGrid = spy(new ListTable<>(new GridGlobalPreferences()));
        getView().initSelectionModel(currentListGrid);

        final ArgumentCaptor<RowStyles> rowStylesApplied = ArgumentCaptor.forClass(RowStyles.class);

        verify(currentListGrid).setRowStyles(rowStylesApplied.capture());

        assertNull(rowStylesApplied.getValue().getStyleNames(TaskSummary.builder()
                                                                     .status(TASK_STATUS_READY.getIdentifier())
                                                                     .priority(1)
                                                                     .build(),
                                                             1));
        assertNull(rowStylesApplied.getValue().getStyleNames(TaskSummary.builder()
                                                                     .status(TASK_STATUS_READY.getIdentifier())
                                                                     .priority(3)
                                                                     .build(),
                                                             1));
        assertNull(rowStylesApplied.getValue().getStyleNames(TaskSummary.builder()
                                                                     .status(TASK_STATUS_READY.getIdentifier())
                                                                     .priority(10)
                                                                     .build(),
                                                             1));
        assertEquals(HumanTaskResources.INSTANCE.css().taskCompleted(),
                     rowStylesApplied.getValue().getStyleNames(TaskSummary.builder()
                                                                       .status(TASK_STATUS_COMPLETED.getIdentifier())
                                                                       .priority(10)
                                                                       .build(),
                                                               1));
    }

    public abstract List<String> getExpectedInitialColumns();
}