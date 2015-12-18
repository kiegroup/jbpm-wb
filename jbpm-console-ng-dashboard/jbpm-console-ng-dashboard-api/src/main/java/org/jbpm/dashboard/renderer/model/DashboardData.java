/**
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.

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
package org.jbpm.dashboard.renderer.model;

public class DashboardData {

    public static final String DATASET_HUMAN_TASKS = "tasksMonitoring";
    public static final String DATASET_PROCESS_INSTANCES = "processesMonitoring";

    public static final String COLUMN_PROCESS_ID = "PROCESSID";
    public static final String COLUMN_PROCESS_INSTANCE_ID = "PROCESSINSTANCEID";
    public static final String COLUMN_PROCESS_EXTERNAL_ID = "EXTERNALID";
    public static final String COLUMN_PROCESS_NAME = "PROCESSNAME";
    public static final String COLUMN_PROCESS_USER_ID = "USER_IDENTITY";
    public static final String COLUMN_PROCESS_START_DATE = "START_DATE";
    public static final String COLUMN_PROCESS_END_DATE = "END_DATE";
    public static final String COLUMN_PROCESS_STATUS = "STATUS";
    public static final String COLUMN_PROCESS_VERSION = "PROCESSVERSION";
    public static final String COLUMN_PROCESS_DURATION = "DURATION";

    public static final String COLUMN_TASK_ID = "TASKID";
    public static final String COLUMN_TASK_NAME = "TASKNAME";
    public static final String COLUMN_TASK_CREATOR_ID = "USERID";
    public static final String COLUMN_TASK_OWNER_ID = "USERID";
    public static final String COLUMN_TASK_CREATED_DATE = "CREATEDDATE";
    public static final String COLUMN_TASK_START_DATE = "STARTDATE";
    public static final String COLUMN_TASK_END_DATE = "ENDDATE";
    public static final String COLUMN_TASK_STATUS = "STATUS";
    public static final String COLUMN_TASK_DURATION = "DURATION";

    public static final String TASK_STATUS_CREATED = "Created";
    public static final String TASK_STATUS_READY = "Ready";
    public static final String TASK_STATUS_RESERVED = "Reserved";
    public static final String TASK_STATUS_IN_PROGRESS = "InProgress";
    public static final String TASK_STATUS_SUSPENDED = "Suspended";
    public static final String TASK_STATUS_COMPLETED = "Completed";
    public static final String TASK_STATUS_FAILED = "Failed";
    public static final String TASK_STATUS_ERROR = "Error";
    public static final String TASK_STATUS_EXITED = "Exited";
    public static final String TASK_STATUS_OBSOLETE = "Obsolete";

}

