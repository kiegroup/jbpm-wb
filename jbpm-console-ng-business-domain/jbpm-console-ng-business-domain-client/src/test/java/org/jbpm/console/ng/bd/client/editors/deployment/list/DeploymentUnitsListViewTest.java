/*
 * Copyright 2015 JBoss by Red Hat.
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
package org.jbpm.console.ng.bd.client.editors.deployment.list;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DeploymentUnitsListViewTest {

    @Mock
    protected ExtendedPagedTable<KModuleDeploymentUnitSummary> currentListGrid;

    @InjectMocks
    private DeploymentUnitsListViewImpl view;



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

        view.initColumns(currentListGrid);

        verify( currentListGrid ).addColumns(anyList());
    }


    @Test
    public void testKBaseColumnValueSetToDefault() {

        final KModuleDeploymentUnitSummary unitWithNull = new KModuleDeploymentUnitSummary("a:b:c", "a", "b", "c", null, null, "Singleton", "MERGE_COLLECTIONS");
        final KModuleDeploymentUnitSummary unitEmptyString = new KModuleDeploymentUnitSummary("a:b:c", "a", "b", "c", "", "", "Singleton", "MERGE_COLLECTIONS");
        final KModuleDeploymentUnitSummary unitWhiteSpace = new KModuleDeploymentUnitSummary("a:b:c", "a", "b", "c", "    ", "   ", "Singleton", "MERGE_COLLECTIONS");

        doAnswer( new Answer() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[ 0 ];
                for ( ColumnMeta columnMeta : columns ) {
                    if (columnMeta.getCaption().equals("KieBaseName")) {
                        Column column = columnMeta.getColumn();

                        Object value = column.getValue(unitWithNull);
                        assertNotNull(value);
                        assertEquals("DEFAULT", value);

                        value = column.getValue(unitEmptyString);
                        assertNotNull(value);
                        assertEquals("DEFAULT", value);

                        value = column.getValue(unitWhiteSpace);
                        assertNotNull(value);
                        assertEquals("DEFAULT", value);
                    }
                }
                return null;
            }
        } ).when( currentListGrid ).addColumns(anyList());

        view.initColumns(currentListGrid);

        verify( currentListGrid ).addColumns(anyList());

    }

    @Test
    public void testKSessionColumnValueSetToDefault() {

        final KModuleDeploymentUnitSummary unitWithNull = new KModuleDeploymentUnitSummary("a:b:c", "a", "b", "c", null, null, "Singleton", "MERGE_COLLECTIONS");
        final KModuleDeploymentUnitSummary unitEmptyString = new KModuleDeploymentUnitSummary("a:b:c", "a", "b", "c", "", "", "Singleton", "MERGE_COLLECTIONS");
        final KModuleDeploymentUnitSummary unitWhiteSpace = new KModuleDeploymentUnitSummary("a:b:c", "a", "b", "c", "    ", "   ", "Singleton", "MERGE_COLLECTIONS");

        doAnswer( new Answer() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[ 0 ];
                for ( ColumnMeta columnMeta : columns ) {
                    if (columnMeta.getCaption().equals("KieSessionName")) {
                        Column column = columnMeta.getColumn();

                        Object value = column.getValue(unitWithNull);
                        assertNotNull(value);
                        assertEquals("DEFAULT", value);

                        value = column.getValue(unitEmptyString);
                        assertNotNull(value);
                        assertEquals("DEFAULT", value);

                        value = column.getValue(unitWhiteSpace);
                        assertNotNull(value);
                        assertEquals("DEFAULT", value);
                    }
                }
                return null;
            }
        } ).when( currentListGrid ).addColumns(anyList());

        view.initColumns(currentListGrid);

        verify( currentListGrid ).addColumns(anyList());

    }

}

