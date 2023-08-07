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
package org.jbpm.workbench.ks.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.impl.DataSetImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.definition.QueryParam;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.filter.FilterFactory.likeTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KieServerDataSetProviderTest {

    public static String COLUMN_TEST = "columTest";

    @InjectMocks
    KieServerDataSetProvider kieServerDataSetProvider;

    @Mock
    KieServerIntegration kieServerIntegration;

    @Mock
    KieServicesClient kieServicesClient;

    @Mock
    QueryServicesClient queryServicesClient;

    @Mock
    DataSetImpl dataSet;

    @Mock
    RemoteDataSetDef dataSetDef;

    @Mock
    QueryDefinition queryDef;

    @Before
    public void setUp() {
        when(kieServerIntegration.getServerClient("servereTemplateId")).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(QueryServicesClient.class)).thenReturn(queryServicesClient);
    }

    @Test
    public void appendEqualToIntervalSelectionTest() {
        String filterValue = "testValue";

        DataSetGroup dataSetGroup = new DataSetGroup();
        dataSetGroup.setColumnGroup(new ColumnGroup(COLUMN_TEST,
                                                    COLUMN_TEST,
                                                    GroupStrategy.DYNAMIC));
        List<Interval> intervalList = new ArrayList<Interval>();
        Interval interval = new Interval(filterValue);
        intervalList.add(interval);
        dataSetGroup.setSelectedIntervalList(intervalList);

        List<QueryParam> filterParams = new ArrayList<>();
        
        kieServerDataSetProvider.appendIntervalSelection(dataSetDef,
                                                         dataSetGroup,
                                                         filterParams);

        assertEquals(1,
                     filterParams.size());
        assertEquals(COLUMN_TEST,
                     filterParams.get(0).getColumn());
        assertEquals("EQUALS_TO",
                     filterParams.get(0).getOperator());
        assertEquals(filterValue,
                     filterParams.get(0).getValue().get(0));
    }

    @Test
    public void appendBetweenIntervalSelectionTest() {
        String filterValue = "testValue";
        Long minValue = Long.valueOf(0);
        Long maxValue = Long.valueOf(2);

        DataSetGroup dataSetGroup = new DataSetGroup();
        dataSetGroup.setColumnGroup(new ColumnGroup(COLUMN_TEST,
                                                    COLUMN_TEST,
                                                    GroupStrategy.DYNAMIC));
        List<Interval> intervalList = new ArrayList<Interval>();
        Interval interval = new Interval(filterValue);
        interval.setMinValue(minValue);
        interval.setMaxValue(maxValue);
        intervalList.add(interval);
        dataSetGroup.setSelectedIntervalList(intervalList);
        List<QueryParam> filterParams = new ArrayList<>();

        kieServerDataSetProvider.appendIntervalSelection(dataSetDef,
                dataSetGroup,
                                                         filterParams);

        assertEquals(1,
                     filterParams.size());
        assertEquals(COLUMN_TEST,
                     filterParams.get(0).getColumn());
        assertEquals("BETWEEN",
                     filterParams.get(0).getOperator());
        assertEquals(Double.valueOf(minValue),
                     filterParams.get(0).getValue().get(0));
        assertEquals(Double.valueOf(maxValue),
                     filterParams.get(0).getValue().get(1));
    }

    @Test
    public void lookupDataSetLogicalExprTest() throws Exception {
        DataSetLookup lookup = new DataSetLookup();
        lookup.setDataSetUUID("");
        when(dataSetDef.getUUID()).thenReturn("");

        final ColumnFilter testFilter = OR(likeTo("column1",
                                                  "%value%"),
                                           likeTo("column2",
                                                  "%value%"));

        DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(testFilter);
        lookup.addOperation(filter);

        kieServerDataSetProvider.lookupDataSet(dataSetDef,
                                               ConsoleDataSetLookup.fromInstance(lookup,
                                                                                 "servereTemplateId"));

        final ArgumentCaptor<QueryFilterSpec> captorEdit = ArgumentCaptor.forClass(QueryFilterSpec.class);
        verify(queryServicesClient).query(anyString(),
                                          anyString(),
                                          captorEdit.capture(),
                                          anyInt(),
                                          anyInt(),
                                          any());

        assertNotNull(captorEdit.getValue());
        QueryParam[] parameters = captorEdit.getValue().getParameters();
        assertEquals(1,
                     parameters.length);

        List<CoreFunctionFilter> expr = (List<CoreFunctionFilter>) parameters[0].getValue();
        assertEquals("OR",
                     parameters[0].getOperator());

        assertEquals("column1 like %value%, true",
                     expr.get(0).toString());
        assertEquals("column2 like %value%, true",
                     expr.get(1).toString());
    }
    
    @Test
    public void testGroupWithInterval() {
        
        DataSetGroup dataSetGroup = new DataSetGroup();
        dataSetGroup.setColumnGroup(new ColumnGroup(COLUMN_TEST,
                                                    COLUMN_TEST,
                                                    GroupStrategy.DYNAMIC,
                                                    30,
                                                    DateIntervalType.DAY.name()));
      

        List<QueryParam> filterParams = new ArrayList<>();
        List<DataColumn> extraColumns = new ArrayList<>();
        kieServerDataSetProvider.handleDataSetGroup(dataSetDef, 
                                                    dataSetGroup, 
                                                    filterParams, 
                                                    extraColumns);

        assertEquals(1,
                     filterParams.size());
        assertEquals(COLUMN_TEST,
                     filterParams.get(0).getColumn());
        assertEquals("group",
                     filterParams.get(0).getOperator());
        
        assertEquals(3,
                     filterParams.get(0).getValue().size());
        assertEquals(COLUMN_TEST,
                     filterParams.get(0).getValue().get(0));
        assertEquals(DateIntervalType.DAY.name(),
                     filterParams.get(0).getValue().get(1));
        assertEquals(30,
                     filterParams.get(0).getValue().get(2));
    }
    
    @Test
    public void testGroupWithNotSetInterval() {
        
        DataSetGroup dataSetGroup = new DataSetGroup();
        dataSetGroup.setColumnGroup(new ColumnGroup(COLUMN_TEST,
                                                    COLUMN_TEST,
                                                    GroupStrategy.DYNAMIC));
      

        List<QueryParam> filterParams = new ArrayList<>();
        List<DataColumn> extraColumns = new ArrayList<>();
        kieServerDataSetProvider.handleDataSetGroup(dataSetDef, 
                                                    dataSetGroup, 
                                                    filterParams, 
                                                    extraColumns);

        assertEquals(1,
                     filterParams.size());
        assertEquals(COLUMN_TEST,
                     filterParams.get(0).getColumn());
        assertEquals("group",
                     filterParams.get(0).getOperator());
        
        assertEquals(1,
                     filterParams.get(0).getValue().size());
        assertEquals(COLUMN_TEST,
                     filterParams.get(0).getValue().get(0));
    }
    
    @Test
    public void testPerformQueryTestMode() {
        QueryFilterSpec filterSpec = new QueryFilterSpec();
        
        ConsoleDataSetLookup dataSetLookup = Mockito.mock(ConsoleDataSetLookup.class);
        when(dataSetLookup.testMode()).thenReturn(true);
        when(dataSetLookup.getNumberOfRows()).thenReturn(10);
        when(dataSetLookup.getRowOffset()).thenReturn(1);
        when(dataSetLookup.getDataSetUUID()).thenReturn("");
        
        kieServerDataSetProvider.performQuery(dataSetDef, dataSetLookup, queryServicesClient, filterSpec);
        
        verify(kieServerIntegration).broadcastToKieServers(any(),
                                                           any());
        verify(dataSetLookup, times(1)).testMode();
        
        verify(queryServicesClient).query(anyString(),
                                          anyString(),
                                          any(QueryFilterSpec.class),
                                          anyInt(),
                                          anyInt(),
                                          any());
    }
    
    @Test
    public void testPerformQueryRegularMode() {
        String columnId = "test";
        String columnType = "LABEL";
        QueryFilterSpec filterSpec = new QueryFilterSpec();
        
        ConsoleDataSetLookup dataSetLookup = Mockito.mock(ConsoleDataSetLookup.class);
        when(dataSetLookup.testMode()).thenReturn(false);
        when(dataSetLookup.getNumberOfRows()).thenReturn(10);
        when(dataSetLookup.getRowOffset()).thenReturn(1);
        when(dataSetLookup.getDataSetUUID()).thenReturn("");
        
        when(queryDef.getColumns()).thenReturn(Collections.singletonMap(columnId, columnType));
        when(queryServicesClient.getQuery(any())).thenReturn(queryDef);
        
        kieServerDataSetProvider.performQuery(dataSetDef, dataSetLookup, queryServicesClient, filterSpec);
        
        verify(kieServerIntegration, times(0)).broadcastToKieServers(anyString(),
                                                           any());
        verify(dataSetLookup, times(1)).testMode();
        
        verify(queryServicesClient).query(anyString(),
                                          anyString(),
                                          any(QueryFilterSpec.class),
                                          anyInt(),
                                          anyInt(),
                                          any());
        
        verify(dataSetDef).addColumn(eq(columnId), eq(ColumnType.LABEL));
    }    
    
    @Test
    public void testDataSetMetaData() throws Exception {
        Map<String, String> columns = new HashMap<>();
        columns.put("test", "NUMBER");
        QueryDefinition definition = QueryDefinition.builder()
                .name("q1")
                .columns(columns)
                .build();
        
        when(dataSetDef.getColumns()).thenReturn(null, new ArrayList<>());
        when(dataSetDef.getServerTemplateId()).thenReturn("servereTemplateId");
        when(queryServicesClient.getQuery(any())).thenReturn(definition);
        
        kieServerDataSetProvider.getDataSetMetadata(dataSetDef);
        
        verify(dataSetDef, times(1)).addColumn(eq("test"), eq(ColumnType.NUMBER));
        
        verify(queryServicesClient).getQuery(any());
    }
    
    @Test
    public void testNoAdoptLookup() throws Exception {
        ConsoleDataSetLookup dataSetLookup = Mockito.mock(ConsoleDataSetLookup.class);      
        
        kieServerDataSetProvider.adoptLookup(dataSetDef, dataSetLookup);
        
        verify(dataSetDef, times(0)).getServerTemplateId();        
    }
    
    @Test
    public void testAdoptLookup() throws Exception {
        DataSetLookup dataSetLookup = Mockito.mock(DataSetLookup.class);
        when(dataSetDef.getDataSetFilter()).thenReturn(Mockito.mock(DataSetFilter.class));
        when(dataSetDef.getServerTemplateId()).thenReturn("servereTemplateId");

        ConsoleDataSetLookup adopted = kieServerDataSetProvider.adoptLookup(dataSetDef, dataSetLookup);
        
        verify(dataSetDef, times(1)).getServerTemplateId(); 
        assertNotNull(adopted.getOperationList());
        assertEquals(1, adopted.getOperationList().size());
    }

    @Test
    public void testGroupFunctionColumnType() {
        for (ColumnType type :
                ColumnType.values()) {
            assertGroupFuntionColumnType(type,
                                         type == ColumnType.DATE ? ColumnType.LABEL : type);
        }
    }

    protected void assertGroupFuntionColumnType(final ColumnType source,
                                                final ColumnType expected) {
        final DataSetDef def = new DataSetDef();
        def.addColumn("columnId",
                      source);
        final ColumnGroup columnGroup = new ColumnGroup("sourceId",
                                                        "columnId");
        final GroupFunction groupFunction = new GroupFunction("sourceId",
                                                              "columnId",
                                                              null);

        assertEquals(expected,
                     kieServerDataSetProvider.getGroupFunctionColumnType(def,
                                                                         columnGroup,
                                                                         groupFunction));
    }
}
