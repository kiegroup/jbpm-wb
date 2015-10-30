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
package org.jbpm.console.ng.df.client.filter.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.sort.ColumnSort;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetLookupJSONMarshallerTest {

    public static final String COLUMN_ACTIVATIONTIME = "activationTime";
    public static final String COLUMN_ACTUALOWNER = "actualOwner";
    public static final String COLUMN_CREATEDBY = "createdBy";
    public static final String COLUMN_CREATEDON = "createdOn";
    public static final String COLUMN_DEPLOYMENTID = "deploymentId";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DUEDATE = "dueDate";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PARENTID = "parentId";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_PROCESSID = "processId";
    public static final String COLUMN_PROCESSINSTANCEID = "processInstanceId";
    public static final String COLUMN_PROCESSSESSIONID = "processSessionId";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TASKID = "taskId";
    public static final String COLUMN_WORKITEMID = "workItemId";
    public static final String COLUMN_ORGANIZATIONAL_ENTITY = "oeid";

    @Mock
    protected DataSetLookup dataSetLookup;

    @GwtMock
    protected JSONObject jsonObject;

    @GwtMock
    protected JSONArray jsonArray;

    protected DataSetSort datasetSort;
    protected DataSetFilter datasetFilter;
    protected DataSetGroup datasetGroup;

    DataSetLookupJSONMarshaller dataSetLookupJSONMarshaller;

    @Before
    public void setUp() throws Exception {
        dataSetLookupJSONMarshaller = new DataSetLookupJSONMarshaller();

        datasetFilter = new DataSetFilter();
        List<ColumnFilter> condList = new  ArrayList<ColumnFilter>();
        condList.add( FilterFactory.equalsTo(COLUMN_ORGANIZATIONAL_ENTITY, "val1"));
        condList.add(FilterFactory.equalsTo(COLUMN_ACTUALOWNER, "val2"));
        condList.add(FilterFactory.AND(COLUMN_NAME, FilterFactory.equalsTo("1", "2")));
        datasetFilter.addFilterColumn(FilterFactory.AND(condList));

        datasetGroup = new DataSetGroup();
        datasetGroup.setColumnGroup(new ColumnGroup("id", "newId"));
        datasetGroup.addGroupFunction(new GroupFunction("id", "columnId", AggregateFunctionType.MAX));

        ArrayList<Interval> intervals=new ArrayList<Interval>();
        Interval interval= new Interval("interval");
        interval.setType("1");
        interval.setMaxValue("10");
        interval.setMinValue("0");
        intervals.add(interval);
        datasetGroup.setSelectedIntervalList(intervals);

        datasetSort = new DataSetSort();
        datasetSort.addSortColumn(new ColumnSort(COLUMN_NAME, ASCENDING));

        ArrayList<DataSetFilter> arrayDatasetFilters= new ArrayList<DataSetFilter>();
        arrayDatasetFilters.add(datasetFilter);

        ArrayList<DataSetGroup> arrayDatasetGroup= new ArrayList<DataSetGroup>();
        arrayDatasetGroup.add(datasetGroup);

        ArrayList<DataSetSort> arrayDatasetSort= new ArrayList<DataSetSort>();
        arrayDatasetSort.add(datasetSort);

        when(dataSetLookup.getDataSetUUID()).thenReturn("dataset");
        when(dataSetLookup.getNumberOfRows()).thenReturn(3);
        when(dataSetLookup.getRowOffset()).thenReturn(0);
        when(dataSetLookup.getOperationList(DataSetFilter.class)).thenReturn(arrayDatasetFilters);
        when(dataSetLookup.getOperationList(DataSetGroup.class)).thenReturn(arrayDatasetGroup);
        when(dataSetLookup.getOperationList(DataSetSort.class)).thenReturn(arrayDatasetSort);

    }

    @Test
    public void testToJSON() throws Exception {

        dataSetLookupJSONMarshaller.toJson(dataSetLookup);

        verify(dataSetLookup,times(1)).getDataSetUUID();
        verify(dataSetLookup,times(1)).getNumberOfRows();
        verify(dataSetLookup,times(1)).getRowOffset();
        verify(dataSetLookup,times(1)).getOperationList(DataSetFilter.class);
        verify(dataSetLookup,times(1)).getOperationList(DataSetGroup.class);
        verify(dataSetLookup,times(1)).getOperationList(DataSetSort.class);

    }

    @Test
    public void testFromJSON() throws Exception {
        when(jsonObject.get(eq("rowCount"))).thenReturn(new JSONString("5"));
        when(jsonObject.get(eq("rowOffset"))).thenReturn(new JSONString("6"));
        when(jsonObject.get(eq("filterOps"))).thenReturn(jsonArray);

        DataSetLookup dSetLookup = dataSetLookupJSONMarshaller.fromJson(jsonObject);
        assertTrue("The number of rows has to be 5", dSetLookup.getNumberOfRows()==5);
        assertTrue("the offset has to be 6", dSetLookup.getRowOffset()==6);

    }


}
