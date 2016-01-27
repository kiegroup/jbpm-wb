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
package org.jbpm.console.ng.pr.model;

public interface ProcessInstanceDataSetConstants {

    String PROCESS_INSTANCE_DATASET = "jbpmProcessInstances";
    String PROCESS_INSTANCE_WITH_VARIABLES_DATASET = "jbpmProcessInstancesWithVariables";

    String COLUMN_PROCESSINSTANCEID = "processInstanceId";
    String COLUMN_PROCESSID = "processId";
    String COLUMN_START = "start_date";
    String COLUMN_END = "end_date";
    String COLUMN_STATUS = "status";
    String COLUMN_PARENTPROCESSINSTANCEID = "parentProcessInstanceId";
    String COLUMN_OUTCOME = "outcome";
    String COLUMN_DURATION = "duration";
    String COLUMN_IDENTITY = "user_identity";
    String COLUMN_PROCESSVERSION = "processVersion";
    String COLUMN_PROCESSNAME = "processName";
    String COLUMN_CORRELATIONKEY = "correlationKey";
    String COLUMN_EXTERNALID = "externalId";
    String COLUMN_PROCESSINSTANCEDESCRIPTION = "processInstanceDescription";
}
