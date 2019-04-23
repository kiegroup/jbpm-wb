/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.backend.server;

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.jbpm.workbench.ks.integration.ConsoleDataSetLookup;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_DYNAMIC;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROCESSDEF;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROCESSNAME;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROCESSVERSION;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROJECT;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.PROCESS_DEFINITION_DATASET;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class ProcessDefinitionDataSetProviderTest {

    @Mock
    protected ProcessRuntimeDataService processRuntimeDataService;

    @InjectMocks
    ProcessDefinitionDataSetProvider processDefinitionDataSetProvider;

    private static final String SERVER_TEMPLATE_ID = "serverTemplateId";
    private static final String TEXT_SEARCH = "one";
    private static final String SORT_COLUMN_NAME = "ProcessName";

    private DataSetDef dataSetdef;

    @Before
    public void init() {
        ProcessSummary processSummaryOne = new ProcessSummary("test-def-id", "test-def-name-one", "testone", "1", false);
        ProcessSummary processSummaryTwo = new ProcessSummary("test-def-id", "test-def-name", "testtwo", "1", false);
        ProcessSummary processSummaryThree = new ProcessSummary("test-def-id", "test-def-name", "testtwo", "2", false);

        List<ProcessSummary> processSummaries = Arrays.asList(processSummaryOne, processSummaryTwo, processSummaryThree);
        doAnswer(answer -> Arrays.asList(processSummaryOne)).when(processRuntimeDataService).getProcessesByFilter(SERVER_TEMPLATE_ID,
                                                                                                                  TEXT_SEARCH,
                                                                                                                  0,
                                                                                                                  -1,
                                                                                                                  SORT_COLUMN_NAME,
                                                                                                                  false);

        doAnswer(answer -> processSummaries).when(processRuntimeDataService).getProcessesByFilter(SERVER_TEMPLATE_ID,
                                                                                                  "",
                                                                                                  0,
                                                                                                  -1,
                                                                                                  SORT_COLUMN_NAME,
                                                                                                  false);



        dataSetdef = new ProcessDefinitionDataSetGenerator().getDataSetDef();
    }

    @Test
    public void testGetDataSetMetadata() throws Exception {
        DataSetMetadata dataSetMetadata = processDefinitionDataSetProvider.getDataSetMetadata(dataSetdef);

        assertEquals(PROCESS_DEFINITION_DATASET, dataSetMetadata.getUUID());

        assertThat(dataSetMetadata.getColumnIds()).hasSize(5).containsOnlyOnce(COL_ID_PROCESSNAME, COL_ID_PROCESSVERSION, COL_ID_PROJECT, COL_ID_PROCESSDEF, COL_DYNAMIC);
        assertThat(dataSetMetadata.getColumnTypes()).hasSize(5).containsOnly(ColumnType.LABEL);
    }

    @Test
    public void testLookupDataSet() throws Exception {
        DataSetLookup lookup = new DataSetLookup();
        lookup.setDataSetUUID(PROCESS_DEFINITION_DATASET);
        DataSetLookup consoleDataSetLookup = ConsoleDataSetLookup.fromInstance(lookup, SERVER_TEMPLATE_ID);
        CoreFunctionFilter coreFunctionFilter = new CoreFunctionFilter(SORT_COLUMN_NAME, CoreFunctionType.LIKE_TO, TEXT_SEARCH);
        DataSetFilter dataSetFilter = new DataSetFilter();
        dataSetFilter.addFilterColumn(coreFunctionFilter);
        consoleDataSetLookup.addOperation(dataSetFilter);
        DataSet dataSet = processDefinitionDataSetProvider.lookupDataSet(dataSetdef, consoleDataSetLookup);

        assertEquals(PROCESS_DEFINITION_DATASET, dataSet.getUUID());
        assertEquals(1, dataSet.getRowCount());
        assertThat(dataSet.getColumns()).hasSize(5).extracting(dataColumn -> dataColumn.getId()).contains(COL_ID_PROCESSVERSION,
                                                                                                          COL_ID_PROCESSVERSION,
                                                                                                          COL_ID_PROJECT,
                                                                                                          COL_ID_PROCESSDEF,
                                                                                                          COL_DYNAMIC);
    }

    @Test
    public void testLookupDataSetWithoutFilterColumn() throws Exception {
        DataSetLookup lookup = new DataSetLookup();
        lookup.setDataSetUUID(PROCESS_DEFINITION_DATASET);
        DataSetLookup consoleDataSetLookup = ConsoleDataSetLookup.fromInstance(lookup, SERVER_TEMPLATE_ID);
        DataSetFilter dataSetFilter = new DataSetFilter();
        consoleDataSetLookup.addOperation(dataSetFilter);
        DataSet dataSet = processDefinitionDataSetProvider.lookupDataSet(dataSetdef, consoleDataSetLookup);

        assertEquals(PROCESS_DEFINITION_DATASET, dataSet.getUUID());
        assertEquals(3, dataSet.getRowCount());
        assertThat(dataSet.getColumns()).hasSize(5).extracting(dataColumn -> dataColumn.getId()).contains(COL_ID_PROCESSVERSION,
                                                                                                          COL_ID_PROCESSVERSION,
                                                                                                          COL_ID_PROJECT,
                                                                                                          COL_ID_PROCESSDEF,
                                                                                                          COL_DYNAMIC);
    }
}
