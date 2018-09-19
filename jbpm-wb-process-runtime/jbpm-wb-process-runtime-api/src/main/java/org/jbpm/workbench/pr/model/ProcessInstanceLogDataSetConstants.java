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

package org.jbpm.workbench.pr.model;

public final class ProcessInstanceLogDataSetConstants {

    public static final String PROCESS_INSTANCE_LOGS_DATASET = "jbpmProcessInstanceLogs";

    public static final String COLUMN_LOG_ID = "nodeInstanceId";
    public static final String COLUMN_LOG_NODE_ID = "nodeId";
    public static final String COLUMN_LOG_NODE_NAME = "nodeName";
    public static final String COLUMN_LOG_NODE_TYPE = "nodeType";
    public static final String COLUMN_LOG_DEPLOYMENT_ID = "externalId";
    public static final String COLUMN_LOG_PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String COLUMN_LOG_DATE = "log_date";
    public static final String COLUMN_LOG_CONNECTION = "connection";
    public static final String COLUMN_LOG_TYPE = "type";
    public static final String COLUMN_LOG_WORK_ITEM_ID = "workItemId";
    public static final String COLUMN_LOG_REFERENCE_ID = "referenceId";
    public static final String COLUMN_LOG_NODE_CONTAINER_ID = "nodeContainerId";
    public static final String COLUMN_LOG_SLA_DUE_DATE = "sla_due_date";
    public static final String COLUMN_LOG_SLA_COMPLIANCE = "slaCompliance";

    private ProcessInstanceLogDataSetConstants() {
    }
}
