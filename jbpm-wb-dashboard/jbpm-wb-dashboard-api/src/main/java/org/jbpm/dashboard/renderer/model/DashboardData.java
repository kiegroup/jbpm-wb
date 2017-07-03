/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

    public static final String COLUMN_PROCESS_ID = "processId";
    public static final String COLUMN_PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String COLUMN_PROCESS_EXTERNAL_ID = "externalId";
    public static final String COLUMN_PROCESS_NAME = "processName";
    public static final String COLUMN_PROCESS_USER_ID = "user_identity";
    public static final String COLUMN_PROCESS_START_DATE = "start_date";
    public static final String COLUMN_PROCESS_END_DATE = "end_date";
    public static final String COLUMN_PROCESS_STATUS = "status";
    public static final String COLUMN_PROCESS_VERSION = "processVersion";
    public static final String COLUMN_PROCESS_DURATION = "duration";

    public static final String COLUMN_TASK_ID = "taskId";
    public static final String COLUMN_TASK_NAME = "taskName";
    public static final String COLUMN_TASK_CREATOR_ID = "userId";
    public static final String COLUMN_TASK_OWNER_ID = "userId";
    public static final String COLUMN_TASK_CREATED_DATE = "createdDate";
    public static final String COLUMN_TASK_START_DATE = "startDate";
    public static final String COLUMN_TASK_END_DATE = "endDate";
    public static final String COLUMN_TASK_STATUS = "status";
    public static final String COLUMN_TASK_DURATION = "duration";

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

