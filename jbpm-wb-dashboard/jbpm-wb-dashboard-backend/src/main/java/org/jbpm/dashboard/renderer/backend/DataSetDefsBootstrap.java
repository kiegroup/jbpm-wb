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
package org.jbpm.dashboard.renderer.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jbpm.workbench.ks.integration.AbstractDataSetDefsBootstrap;
import org.jbpm.workbench.ks.integration.event.QueryDefinitionLoaded;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;

@ApplicationScoped
public class DataSetDefsBootstrap extends AbstractDataSetDefsBootstrap {

    public void registerDataSetDefinitions(@Observes QueryDefinitionLoaded event) {
        if (event.getDefinition().getName().equals(DATASET_PROCESS_INSTANCES)) {
            registerDataSetDefinition(event.getDefinition(),
                                      builder ->
                                              builder.number(COLUMN_PROCESS_INSTANCE_ID)
                                                      .label(COLUMN_PROCESS_ID)
                                                      .date(COLUMN_PROCESS_START_DATE)
                                                      .date(COLUMN_PROCESS_END_DATE)
                                                      .number(COLUMN_PROCESS_STATUS)
                                                      .number(COLUMN_PROCESS_DURATION)
                                                      .label(COLUMN_PROCESS_USER_ID)
                                                      .label(COLUMN_PROCESS_VERSION)
                                                      .label(COLUMN_PROCESS_NAME)
                                                      .label(COLUMN_PROCESS_EXTERNAL_ID)
            );
        } else if (event.getDefinition().getName().equals(DATASET_HUMAN_TASKS)) {
            registerDataSetDefinition(event.getDefinition(),
                                      builder ->
                                              builder.label(COLUMN_PROCESS_NAME)
                                                      .label(COLUMN_PROCESS_EXTERNAL_ID)
                                                      .label(COLUMN_TASK_ID)
                                                      .label(COLUMN_TASK_NAME)
                                                      .label(COLUMN_TASK_STATUS)
                                                      .date(COLUMN_TASK_CREATED_DATE)
                                                      .date(COLUMN_TASK_START_DATE)
                                                      .date(COLUMN_TASK_END_DATE)
                                                      .number(COLUMN_PROCESS_INSTANCE_ID)
                                                      .label(COLUMN_TASK_OWNER_ID)
                                                      .number(COLUMN_TASK_DURATION)
            );
        }
    }
}