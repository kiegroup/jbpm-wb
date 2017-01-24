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
package org.jbpm.workbench.df.client.filter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.json.DataSetJSONMarshaller;
import org.dashbuilder.dataset.json.DataSetLookupJSONMarshaller;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FilterSettingsJSONMarshallerTest {

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


    protected DataSetSort datasetSort;
    protected DataSetFilter datasetFilter;
    protected DataSetGroup datasetGroup;

    FilterSettingsJSONMarshaller filterSettingsJSONMarshaller;

    @Before
    public void setUp() throws Exception {
        DataSetJSONMarshaller datasetJsonMarshaller = new DataSetJSONMarshaller();
        DataSetLookupJSONMarshaller lookupJsonMarshaller = new DataSetLookupJSONMarshaller();
        DisplayerSettingsJSONMarshaller displayerJsonMarshaller = new DisplayerSettingsJSONMarshaller(datasetJsonMarshaller, lookupJsonMarshaller);
        filterSettingsJSONMarshaller = new FilterSettingsJSONMarshaller(displayerJsonMarshaller);
    }

    @Test
    public void testJSONConversion() throws Exception {
        FilterSettings filterSettings = createTableSettings();
        String json = filterSettingsJSONMarshaller.toJsonString(filterSettings);
        FilterSettings fs = filterSettingsJSONMarshaller.fromJsonString(json);
        assertTrue("Check the number of operations",
                fs.getDataSetLookup().getOperationList().size() == filterSettings.getDataSetLookup().getOperationList().size());
        assertTrue("Check the DataSetUUID",
                filterSettings.getDataSetLookup().getDataSetUUID().equals(fs.getDataSetLookup().getDataSetUUID()) );
        assertTrue("Check ColumSettingList size",
                filterSettings.getColumnSettingsList().size() ==fs.getColumnSettingsList().size() );
        assertTrue("Check the ColumSettingList(0)",
                filterSettings.getColumnSettingsList().get(0).getColumnId().equals(fs.getColumnSettingsList().get(0).getColumnId()));
        assertTrue("Check the Settings selfApply",
                filterSettings.getSettingsFlatMap().get("type").equals(fs.getSettingsFlatMap().get("type")));
        assertTrue("Check the Settings table.sort.enabled",
                filterSettings.getSettingsFlatMap().get("table.sort.enabled").equals(fs.getSettingsFlatMap().get("table.sort.enabled")));
        assertTrue("Check the Settings table.sort.columnId",
                filterSettings.getSettingsFlatMap().get("table.sort.columnId").equals(fs.getSettingsFlatMap().get("table.sort.columnId")));
    }

    private FilterSettings createTableSettings(){
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset("jbpmHumanTasksWithUser");
        List<Comparable> names = new ArrayList<Comparable>();
        List<String> states= ImmutableList.of("Ready","Reserved","InProgress");
        for(String s : states){
            names.add(s);
        }
        builder.filter(COLUMN_STATUS, equalsTo(COLUMN_STATUS, names));

        List<ColumnFilter> condList = new  ArrayList<ColumnFilter>();
        condList.add(equalsTo(COLUMN_ORGANIZATIONAL_ENTITY, "user"));

        ColumnFilter myGroupFilter = AND( OR( condList ), equalsTo( COLUMN_ACTUALOWNER, "" ));

        builder.filter( OR( myGroupFilter, equalsTo(COLUMN_ACTUALOWNER, "user" )) );
//        builder.group(COLUMN_TASKID);

        builder.setColumn(COLUMN_ACTIVATIONTIME, "Activation Time", "MMM dd E, yyyy");
        builder.setColumn( COLUMN_ACTUALOWNER, "Actual_Owner");
        builder.setColumn( COLUMN_CREATEDBY,"CreatedBy" );
        builder.setColumn( COLUMN_CREATEDON , "Created on", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_DEPLOYMENTID, "DeploymentId" );
        builder.setColumn( COLUMN_DESCRIPTION, "Description" );
        builder.setColumn( COLUMN_DUEDATE, "Due Date", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_NAME, "Task" );
        builder.setColumn( COLUMN_PARENTID,  "ParentId");
        builder.setColumn( COLUMN_PRIORITY, "Priority" );
        builder.setColumn( COLUMN_PROCESSID, "ProcessId" );
        builder.setColumn( COLUMN_PROCESSINSTANCEID, "ProcessInstanceId" );
        builder.setColumn( COLUMN_PROCESSSESSIONID, "ProcessSesionId" );
        builder.setColumn( COLUMN_STATUS, "Status" );
        builder.setColumn(COLUMN_TASKID, "Id");
        builder.setColumn(COLUMN_WORKITEMID, "WorkItemId");

        builder.filterOn(true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(COLUMN_CREATEDON, DESCENDING);

        return builder.buildSettings();

    }



}
