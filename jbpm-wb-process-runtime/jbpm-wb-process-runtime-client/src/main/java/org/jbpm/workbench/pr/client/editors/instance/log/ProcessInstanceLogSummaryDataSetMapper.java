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

import java.util.function.BiFunction;

import org.dashbuilder.dataset.DataSet;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;

import static org.jbpm.workbench.common.client.util.DataSetUtils.*;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.*;

public class ProcessInstanceLogSummaryDataSetMapper implements BiFunction<DataSet, Integer, ProcessInstanceLogSummary> {

    @Override
    public ProcessInstanceLogSummary apply(final DataSet dataSet,
                                           final Integer row) {
        return ProcessInstanceLogSummary.builder()
                .id(getColumnLongValue(dataSet,
                                       COLUMN_LOG_ID,
                                       row))
                .nodeId(getColumnStringValue(dataSet,
                                             COLUMN_LOG_NODE_ID,
                                             row))
                .name(getColumnStringValue(dataSet,
                                           COLUMN_LOG_NODE_NAME,
                                           row))
                .nodeType(getColumnStringValue(dataSet,
                                               COLUMN_LOG_NODE_TYPE,
                                               row))
                .logDeploymentId(getColumnStringValue(dataSet,
                                                      COLUMN_LOG_DEPLOYMENT_ID,
                                                      row))
                .date(getColumnDateValue(dataSet,
                                         COLUMN_LOG_DATE,
                                         row))
                .completed(getColumnIntValue(dataSet,
                                             COLUMN_LOG_TYPE,
                                             row) == 1)
                .workItemId(getColumnLongValue(dataSet,
                                               COLUMN_LOG_WORK_ITEM_ID,
                                               row))
                .referenceId(getColumnLongValue(dataSet,
                                                COLUMN_LOG_REFERENCE_ID,
                                                row))
                .nodeContainerId(getColumnStringValue(dataSet,
                                                      COLUMN_LOG_NODE_CONTAINER_ID,
                                                      row))
                .build();
    }
}
