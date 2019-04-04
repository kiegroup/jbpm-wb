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
package org.jbpm.workbench.ht.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.dashbuilder.dataset.impl.SQLDataSetDefBuilderImpl;
import org.jbpm.workbench.ks.integration.AbstractDataSetDefsBootstrap;
import org.jbpm.workbench.ks.integration.event.QueryDefinitionLoaded;

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@ApplicationScoped
public class DataSetDefsBootstrap extends AbstractDataSetDefsBootstrap {

    public void registerDataSetDefinitions(@Observes QueryDefinitionLoaded event) {
        if (event.getDefinition().getName().equals(HUMAN_TASKS_DATASET)) {
            registerDataSetDefinition(event.getDefinition(),
                                      builder ->
                                              addBuilderCommonColumns(builder)
            );
        } else if (event.getDefinition().getName().equals(HUMAN_TASKS_WITH_USER_DATASET)) {
            registerDataSetDefinition(event.getDefinition(),
                                      builder ->
                                              addBuilderCommonColumns(builder)
                                                      .label(COLUMN_ORGANIZATIONAL_ENTITY)
                                                      .label(COLUMN_EXCLUDED_OWNER)
            );
        } else if (event.getDefinition().getName().equals(HUMAN_TASKS_WITH_ADMIN_DATASET)) {
            registerDataSetDefinition(event.getDefinition(),
                                      builder ->
                                              addBuilderCommonColumns(builder)
                                                      .label(COLUMN_ORGANIZATIONAL_ENTITY)
                                                      .number(COLUMN_ERROR_COUNT)
            );
        } else if (event.getDefinition().getName().equals(HUMAN_TASKS_WITH_VARIABLES_DATASET)) {
            registerDataSetDefinition(event.getDefinition(),
                                      builder ->
                                              builder.number(COLUMN_TASK_VARIABLE_TASK_ID)
                                                      .label(COLUMN_TASK_VARIABLE_NAME)
                                                      .label(COLUMN_TASK_VARIABLE_VALUE)
            );
        }
    }

    private SQLDataSetDefBuilderImpl addBuilderCommonColumns(SQLDataSetDefBuilderImpl builder) {
        return builder
                .date(COLUMN_ACTIVATION_TIME)
                .label(COLUMN_ACTUAL_OWNER)
                .label(COLUMN_CREATED_BY)
                .date(COLUMN_CREATED_ON)
                .label(COLUMN_DEPLOYMENT_ID)
                .text(COLUMN_DESCRIPTION)
                .date(COLUMN_DUE_DATE)
                .label(COLUMN_NAME)
                .number(COLUMN_PARENT_ID)
                .number(COLUMN_PRIORITY)
                .label(COLUMN_PROCESS_ID)
                .number(COLUMN_PROCESS_INSTANCE_ID)
                .number(COLUMN_PROCESS_SESSION_ID)
                .label(COLUMN_STATUS)
                .number(COLUMN_TASK_ID)
                .number(COLUMN_WORK_ITEM_ID)
                .date(COLUMN_LAST_MODIFICATION_DATE)
                .label(COLUMN_PROCESS_INSTANCE_CORRELATION_KEY)
                .text(COLUMN_PROCESS_INSTANCE_DESCRIPTION)
                .date(COLUMN_SLA_DUE_DATE)
                .number(COLUMN_SLA_COMPLIANCE);
    }
}
