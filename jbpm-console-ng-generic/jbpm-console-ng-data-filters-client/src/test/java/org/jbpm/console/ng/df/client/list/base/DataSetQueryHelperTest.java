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
package org.jbpm.console.ng.df.client.list.base;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.displayer.client.DataSetHandler;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.FilterSettingsBuilderHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DataSetQueryHelperTest {

    public static final String COLUMN_1 = "columnOne";
    public static final String COLUMN_2 = "actualTwo";

    @Mock
    protected DataSetLookup dataSetLookup;

    protected FilterSettings currentTableSetting;

    @Mock
    protected DataSetHandler dataSetHandlerMock;

    @Mock
    protected DataSetClientServices dataSetClientServicesMock;

    private DataSetQueryHelper dataSetQueryHelper;

    @Before
    public void setUp() throws Exception {
        currentTableSetting = createTableSettings();
        dataSetQueryHelper = new DataSetQueryHelper(dataSetClientServicesMock);
        dataSetQueryHelper.setCurrentTableSettings(currentTableSetting);
        dataSetQueryHelper.setDataSetHandler(dataSetHandlerMock);

    }

    @Test
    public void lookupDataSetTest() throws Exception {
        currentTableSetting.setTablePageSize(5);
        dataSetQueryHelper.lookupDataSet(0, new DataSetReadyCallback() {
            @Override public void callback(DataSet dataSet) {

            }

            @Override public void notFound() {

            }

            @Override public boolean onError(ClientRuntimeError error) {
                return false;
            }
        });
        verify(dataSetHandlerMock).limitDataSetRows(0, 5);
        verify(dataSetHandlerMock).sort(COLUMN_1,DESCENDING);
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
        builder.filter(COLUMN_1, equalsTo(COLUMN_1, names));


        builder.setColumn(COLUMN_1, "Column1", "MMM dd E, yyyy");
        builder.setColumn(COLUMN_2, "Column2");

        builder.filterOn(true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(COLUMN_1, DESCENDING);
        return builder.buildSettings();

    }



}
