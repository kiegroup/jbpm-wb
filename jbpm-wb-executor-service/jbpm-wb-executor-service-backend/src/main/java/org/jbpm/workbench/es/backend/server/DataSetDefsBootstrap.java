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
package org.jbpm.workbench.es.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jbpm.workbench.ks.integration.AbstractDataSetDefsBootstrap;
import org.jbpm.workbench.ks.integration.event.QueryDefinitionLoaded;

import static org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants.*;
import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;

@ApplicationScoped
public class DataSetDefsBootstrap extends AbstractDataSetDefsBootstrap {

    public void registerDataSetDefinitions(@Observes QueryDefinitionLoaded event) {
        if (event.getDefinition().getName().equals(REQUEST_LIST_DATASET)) {
            registerDataSetDefinition(event.getDefinition(),
                                      builder ->
                                              builder.number(COLUMN_ID)
                                                      .date(COLUMN_TIMESTAMP)
                                                      .label(COLUMN_STATUS)
                                                      .label(COLUMN_COMMANDNAME)
                                                      .label(COLUMN_MESSAGE)
                                                      .label(COLUMN_BUSINESSKEY)
                                                      .number(COLUMN_RETRIES)
                                                      .number(COLUMN_EXECUTIONS)
                                                      .label(COLUMN_PROCESS_NAME)
                                                      .number(COLUMN_PROCESS_INSTANCE_ID)
                                                      .label(COLUMN_PROCESS_INSTANCE_DESCRIPTION)
                                                      .label(COLUMN_JOB_DEPLOYMENT_ID)
            );
        } else if (event.getDefinition().getName().equals(EXECUTION_ERROR_LIST_DATASET)) {
            registerDataSetDefinition(event.getDefinition(),
                                      builder ->
                                              builder.number(COLUMN_ERROR_ACK)
                                                      .text(COLUMN_ERROR_ACK_BY)
                                                      .date(COLUMN_ERROR_ACK_AT)
                                                      .number(COLUMN_ACTIVITY_ID)
                                                      .label(COLUMN_ACTIVITY_NAME)
                                                      .label(COLUMN_DEPLOYMENT_ID)
                                                      .date(COLUMN_ERROR_DATE)
                                                      .label(COLUMN_ERROR_ID)
                                                      .label(COLUMN_ERROR_MSG)
                                                      .number(COLUMN_JOB_ID)
                                                      .label(COLUMN_PROCESS_ID)
                                                      .number(COLUMN_PROCESS_INST_ID)
                                                      .label(COLUMN_ERROR_TYPE));
        }
    }
}