/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.bd.integration;

import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.impl.DataSetImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.definition.QueryParam;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
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
    DataSetDef dataSetDef;

    @Before
    public void setUp() {
        when(kieServerIntegration.getServerClient("servereTemplateId")).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(QueryServicesClient.class)).thenReturn(queryServicesClient);
    }

    @Test
    public void appendEqualToIntervalSelectionTest() {
        String filterValue = "testValue";

        DataSetGroup dataSetGroup = new DataSetGroup();
        dataSetGroup.setColumnGroup(new ColumnGroup(COLUMN_TEST, COLUMN_TEST, GroupStrategy.DYNAMIC));
        List<Interval> intervalList = new ArrayList<Interval>();
        Interval interval = new Interval(filterValue);
        intervalList.add(interval);
        dataSetGroup.setSelectedIntervalList(intervalList);

        List<QueryParam> filterParams = new ArrayList<>();
        kieServerDataSetProvider.appendIntervalSelection(dataSetGroup, filterParams);

        assertEquals(1, filterParams.size());
        assertEquals(COLUMN_TEST, filterParams.get(0).getColumn());
        assertEquals("EQUALS_TO",filterParams.get(0).getOperator());
        assertEquals(filterValue,filterParams.get(0).getValue().get(0));
    }

    @Test
    public void appendBetweenIntervalSelectionTest() {
        String filterValue = "testValue";
        Long minValue = Long.valueOf(0);
        Long maxValue = Long.valueOf(2);

        DataSetGroup dataSetGroup = new DataSetGroup();
        dataSetGroup.setColumnGroup(new ColumnGroup(COLUMN_TEST, COLUMN_TEST, GroupStrategy.DYNAMIC));
        List<Interval> intervalList = new ArrayList<Interval>();
        Interval interval = new Interval(filterValue);
        interval.setMinValue(minValue);
        interval.setMaxValue(maxValue);
        intervalList.add(interval);
        dataSetGroup.setSelectedIntervalList(intervalList);
        List<QueryParam> filterParams = new ArrayList<>();

        kieServerDataSetProvider.appendIntervalSelection(dataSetGroup, filterParams);

        assertEquals(1, filterParams.size());
        assertEquals(COLUMN_TEST, filterParams.get(0).getColumn());
        assertEquals("BETWEEN",filterParams.get(0).getOperator());
        assertEquals(Double.valueOf(minValue),filterParams.get(0).getValue().get(0));
        assertEquals(Double.valueOf(maxValue),filterParams.get(0).getValue().get(1));
    }

    @Test
    public void lookupDataSetLogicalExprTest() throws Exception {
        DataSetLookup lookup = new DataSetLookup();
        lookup.setDataSetUUID("");
        when(dataSetDef.getUUID()).thenReturn("");

        final ColumnFilter testFilter = OR( likeTo("column1","%value%"),likeTo("column2","%value%"));

        DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(testFilter);
        lookup.addOperation(filter);

        kieServerDataSetProvider.lookupDataSet(dataSetDef ,ConsoleDataSetLookup.fromInstance(lookup,"servereTemplateId"));

        final ArgumentCaptor<QueryFilterSpec> captorEdit = ArgumentCaptor.forClass(QueryFilterSpec.class);
        verify(queryServicesClient).query( anyString(),anyString(), captorEdit.capture(), anyInt(),anyInt(), any());

        assertNotNull(captorEdit.getValue());
        QueryParam[] parameters =captorEdit.getValue().getParameters();
        assertEquals(1,parameters.length);

        List<CoreFunctionFilter> expr = (List<CoreFunctionFilter>)parameters[0].getValue();
        assertEquals("OR",parameters[0].getOperator());

        assertEquals("column1 like %value%, true",expr.get(0).toString());
        assertEquals("column2 like %value%, true",expr.get(1).toString());


    }

}
