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
package org.jbpm.workbench.pr.client.editors.variables.list;

import java.util.List;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.widgets.common.client.tables.PopoverTextCell;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ProcessVariableListViewTest {

    @SuppressWarnings("rawtypes")
    @Mock
    protected ExtendedPagedTable currentListGrid;

    @InjectMocks
    private ProcessVariableListViewImpl view;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testDataStoreNameIsSet() {
        doAnswer( new Answer() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[ 0 ];
                for ( ColumnMeta columnMeta : columns ) {
                    assertNotNull( columnMeta.getColumn().getDataStoreName() );
                }
                return null;
            }
        } ).when( currentListGrid ).addColumns( anyList() );

        view.initColumns( currentListGrid );

        verify( currentListGrid ).addColumns( anyList() );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testDisplayButtons() {
        doAnswer( new Answer() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[ 0 ];
                for ( ColumnMeta columnMeta : columns ) {
                    if(ProcessVariableListViewImpl.COL_ID_ACTIONS.equals(columnMeta.getColumn().getDataStoreName())){
                        assertTrue( columnMeta.getColumn().getCell() instanceof CompositeCell);
                    }
                    if(ProcessVariableListViewImpl.COL_ID_VARVALUE.equals(columnMeta.getColumn().getDataStoreName())){
                        assertTrue(columnMeta.getColumn().getCell() instanceof PopoverTextCell );
                    }
                }
                return null;
            }
        } ).when( currentListGrid ).addColumns(anyList());

        view.initColumns(currentListGrid);
        verify( currentListGrid ).addColumns(anyList());

    }

}
