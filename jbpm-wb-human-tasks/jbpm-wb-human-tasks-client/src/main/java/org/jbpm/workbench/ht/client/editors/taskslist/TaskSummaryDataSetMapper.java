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

package org.jbpm.workbench.ht.client.editors.taskslist;

import java.util.function.BiFunction;

import org.dashbuilder.dataset.DataSet;
import org.jbpm.workbench.ht.model.TaskSummary;

import static org.jbpm.workbench.common.client.util.DataSetUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

public class TaskSummaryDataSetMapper implements BiFunction<DataSet, Integer, TaskSummary> {

    @Override
    public TaskSummary apply(final DataSet dataSet,
                             final Integer row) {
        return TaskSummary.builder()
                .id(getColumnLongValue(dataSet,
                                       COLUMN_TASK_ID,
                                       row))
                .name(getColumnStringValue(dataSet,
                                           COLUMN_NAME,
                                           row))
                .description(getColumnStringValue(dataSet,
                                                  COLUMN_DESCRIPTION,
                                                  row))
                .status(getColumnStringValue(dataSet,
                                             COLUMN_STATUS,
                                             row))
                .priority(getColumnIntValue(dataSet,
                                            COLUMN_PRIORITY,
                                            row))
                .actualOwner(getColumnStringValue(dataSet,
                                                  COLUMN_ACTUAL_OWNER,
                                                  row))
                .createdBy(getColumnStringValue(dataSet,
                                                COLUMN_CREATED_BY,
                                                row))
                .createdOn(getColumnDateValue(dataSet,
                                              COLUMN_CREATED_ON,
                                              row))
                .activationTime(getColumnDateValue(dataSet,
                                                   COLUMN_ACTIVATION_TIME,
                                                   row))
                .expirationTime(getColumnDateValue(dataSet,
                                                   COLUMN_DUE_DATE,
                                                   row))
                .processId(getColumnStringValue(dataSet,
                                                COLUMN_PROCESS_ID,
                                                row))
                .processInstanceId(getColumnLongValue(dataSet,
                                                      COLUMN_PROCESS_INSTANCE_ID,
                                                      row))
                .deploymentId(getColumnStringValue(dataSet,
                                                   COLUMN_DEPLOYMENT_ID,
                                                   row))
                .parentId(getColumnLongValue(dataSet,
                                             COLUMN_PARENT_ID,
                                             row))
                .lastModificationDate(getColumnDateValue(dataSet,
                                                         COLUMN_LAST_MODIFICATION_DATE,
                                                         row))
                .processInstanceCorrelationKey(getColumnStringValue(dataSet,
                                                                    COLUMN_PROCESS_INSTANCE_CORRELATION_KEY,
                                                                    row))
                .processInstanceDescription(getColumnStringValue(dataSet,
                                                                 COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                                 row))
                .isForAdmin(HUMAN_TASKS_WITH_ADMIN_DATASET.equals(dataSet.getUUID()))
                .build();
    }
}
