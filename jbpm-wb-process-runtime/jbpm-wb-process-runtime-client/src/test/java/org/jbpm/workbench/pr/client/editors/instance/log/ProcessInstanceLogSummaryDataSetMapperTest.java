/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.instance.log;

import java.util.Date;

import org.dashbuilder.dataset.DataSet;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;
import org.junit.Test;

import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_DATE;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_ID;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_NODE_NAME;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_NODE_TYPE;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_TYPE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProcessInstanceLogSummaryDataSetMapperTest {

    @Test
    public void testDataSetQueryHelperColumnMapping() {
        final Long logId = Long.valueOf(55);
        final Date logDate = new Date();
        final String nodeName = "nodeName";
        final String nodeType = "nodeType";
        final int type = 1;
        DataSet dataSet = mock(DataSet.class);

        defineDatasetAnswer(dataSet,
                            0,
                            logId,
                            logDate,
                            nodeName,
                            nodeType,
                            type);

        ProcessInstanceLogSummary rls = new ProcessInstanceLogSummaryDataSetMapper().apply(dataSet,
                                                                                           0);

        assertEquals(logId,
                     rls.getId());
        assertEquals(logDate,
                     rls.getDate());
        assertEquals(nodeName,
                     rls.getName());
        assertEquals(nodeType,
                     rls.getNodeType());
        assertTrue(rls.isCompleted());
    }

    public void defineDatasetAnswer(DataSet dataSet,
                                    int position,
                                    Long id,
                                    Date date,
                                    String nodeName,
                                    String nodeType,
                                    int type) {
        when(dataSet.getValueAt(position,
                                COLUMN_LOG_ID)).thenReturn(id);
        when(dataSet.getValueAt(position,
                                COLUMN_LOG_DATE)).thenReturn(date);
        when(dataSet.getValueAt(position,
                                COLUMN_LOG_NODE_NAME)).thenReturn(nodeName);
        when(dataSet.getValueAt(position,
                                COLUMN_LOG_NODE_TYPE)).thenReturn(nodeType);
        when(dataSet.getValueAt(position,
                                COLUMN_LOG_TYPE)).thenReturn(type);
    }
}
