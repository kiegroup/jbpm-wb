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
package org.jbpm.workbench.ht.model;

public final class TaskDataSetConstants {

    public static final String HUMAN_TASKS_DATASET = "jbpmHumanTasks";
    public static final String HUMAN_TASKS_WITH_USER_DATASET = "jbpmHumanTasksWithUser";
    public static final String HUMAN_TASKS_WITH_ADMIN_DATASET = "jbpmHumanTasksWithAdmin";
    public static final String HUMAN_TASKS_WITH_VARIABLES_DATASET = "jbpmHumanTasksWithVariables";

    public static final String COLUMN_ACTIVATION_TIME = "activationTime";
    public static final String COLUMN_ACTUAL_OWNER = "actualOwner";
    public static final String COLUMN_CREATED_BY = "createdBy";
    public static final String COLUMN_CREATED_ON = "createdOn";
    public static final String COLUMN_DEPLOYMENT_ID = "deploymentId";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DUE_DATE = "dueDate";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PARENT_ID = "parentId";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_PROCESS_ID = "processId";
    public static final String COLUMN_PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String COLUMN_PROCESS_SESSION_ID = "processSessionId";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TASK_ID = "taskId";
    public static final String COLUMN_WORK_ITEM_ID = "workItemId";
    public static final String COLUMN_ORGANIZATIONAL_ENTITY = "id";
    public static final String COLUMN_LAST_MODIFICATION_DATE = "lastModificationDate";
    public static final String COLUMN_PROCESS_INSTANCE_CORRELATION_KEY = "correlationKey";
    public static final String COLUMN_PROCESS_INSTANCE_DESCRIPTION = "processInstanceDescription";
    public static final String COLUMN_EXCLUDED_OWNER = "entity_id";

    public static final String COLUMN_TASK_VARIABLE_TASK_ID = "taskId";
    public static final String COLUMN_TASK_VARIABLE_TASK_NAME = "TASKNAME";
    public static final String COLUMN_TASK_VARIABLE_NAME = "name";
    public static final String COLUMN_TASK_VARIABLE_VALUE = "value";

    private TaskDataSetConstants() {
    }
}
