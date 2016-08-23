/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.bd.integration;

import java.util.ArrayList;
import java.util.List;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.group.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.definition.QueryParam;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class KieServerDataSetProviderTest {
    public static String COLUMN_TEST = "columTest";

    KieServerDataSetProvider kieServerDataSetProvider;

    @Before
    public void setUp() {
        kieServerDataSetProvider = new KieServerDataSetProvider();
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

}
