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

public final class ProcessInstanceDataSetConstants {

    private ProcessInstanceDataSetConstants() {
    }

    public static final String PROCESS_INSTANCE_DATASET = "jbpmProcessInstances";
    public static final String PROCESS_INSTANCE_WITH_VARIABLES_DATASET = "jbpmProcessInstancesWithVariables";

    public static final String COLUMN_PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String COLUMN_PROCESS_ID = "processId";
    public static final String COLUMN_START = "start_date";
    public static final String COLUMN_END = "end_date";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_PARENT_PROCESS_INSTANCE_ID = "parentProcessInstanceId";
    public static final String COLUMN_OUTCOME = "outcome";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_IDENTITY = "user_identity";
    public static final String COLUMN_PROCESS_VERSION = "processVersion";
    public static final String COLUMN_PROCESS_NAME = "processName";
    public static final String COLUMN_CORRELATION_KEY = "correlationKey";
    public static final String COLUMN_EXTERNAL_ID = "externalId";
    public static final String COLUMN_PROCESS_INSTANCE_DESCRIPTION = "processInstanceDescription";

    public static final String PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX = "DS_ProcessInstancesWithVariablesIncludedGrid";

    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String PROCESS_NAME = "processId";
    public static final String VARIABLE_NAME = "variableId";
    public static final String VARIABLE_VALUE = "value";
    public static final String VARIABLE_ID = "id";

    public static final String COL_ID_SELECT = "Select";
    public static final String COL_ID_ACTIONS = "Actions";
}
