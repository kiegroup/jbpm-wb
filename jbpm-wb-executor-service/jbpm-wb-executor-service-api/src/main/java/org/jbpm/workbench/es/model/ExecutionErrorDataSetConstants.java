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
package org.jbpm.workbench.es.model;

public interface ExecutionErrorDataSetConstants {

    String EXECUTION_ERROR_LIST_DATASET = "jbpmExecutionErrorList";

    String COLUMN_ERROR_ACK = "ERROR_ACK";
    String COLUMN_ERROR_ACK_AT = "ERROR_ACK_AT";
    String COLUMN_ERROR_ACK_BY = "ERROR_ACK_BY";
    String COLUMN_ACTIVITY_ID = "ACTIVITY_ID";
    String COLUMN_ACTIVITY_NAME = "ACTIVITY_NAME";
    String COLUMN_DEPLOYMENT_ID = "DEPLOYMENT_ID";
    String COLUMN_ERROR_DATE = "ERROR_DATE";
    String COLUMN_ERROR_ID = "ERROR_ID";
    String COLUMN_ERROR_MSG = "ERROR_MSG";
    String COLUMN_JOB_ID = "JOB_ID";
    String COLUMN_PROCESS_ID = "PROCESS_ID";
    String COLUMN_PROCESS_INST_ID = "PROCESS_INST_ID";
    String COLUMN_ERROR_TYPE = "ERROR_TYPE";

    String COL_ID_ACTIONS = "Actions";
    String EXECUTION_ERROR_LIST_PREFIX = "DS_ExecutionErrorListGrid";
}
