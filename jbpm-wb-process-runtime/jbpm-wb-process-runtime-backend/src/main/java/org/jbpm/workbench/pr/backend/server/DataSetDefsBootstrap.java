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
package org.jbpm.workbench.pr.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jbpm.workbench.ks.integration.AbstractDataSetDefsBootstrap;
import org.jbpm.workbench.ks.integration.event.QueryDefinitionLoaded;

import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;

@ApplicationScoped
public class DataSetDefsBootstrap extends AbstractDataSetDefsBootstrap {

    public void registerDataSetDefinitions(@Observes QueryDefinitionLoaded event) {
        if (event.getDefinition().getName().equals(PROCESS_INSTANCE_DATASET)) {
            registerDataSetDefinition(event.getDefinition(),
                                      builder ->
                                              builder.number(COLUMN_PROCESS_INSTANCE_ID)
                                                      .label(COLUMN_PROCESS_ID)
                                                      .date(COLUMN_START)
                                                      .date(COLUMN_END)
                                                      .number(COLUMN_STATUS)
                                                      .number(COLUMN_PARENT_PROCESS_INSTANCE_ID)
                                                      .label(COLUMN_OUTCOME)
                                                      .number(COLUMN_DURATION)
                                                      .label(COLUMN_IDENTITY)
                                                      .label(COLUMN_PROCESS_VERSION)
                                                      .label(COLUMN_PROCESS_NAME)
                                                      .label(COLUMN_CORRELATION_KEY)
                                                      .label(COLUMN_EXTERNAL_ID)
                                                      .label(COLUMN_PROCESS_INSTANCE_DESCRIPTION)
                                                      .date(COLUMN_SLA_DUE_DATE)
                                                      .number(COLUMN_SLA_COMPLIANCE)
                                                      .date(COLUMN_LAST_MODIFICATION_DATE)
                                                      .number(COLUMN_ERROR_COUNT)
            );
        } else if (event.getDefinition().getName().equals(PROCESS_INSTANCE_WITH_VARIABLES_DATASET)) {
            registerDataSetDefinition(event.getDefinition(),
                                      builder ->
                                              builder.number(PROCESS_INSTANCE_ID)
                                                      .label(PROCESS_NAME)
                                                      .number(VARIABLE_ID)
                                                      .label(VARIABLE_NAME)
                                                      .label(VARIABLE_VALUE)
            );
        }
    }
}
