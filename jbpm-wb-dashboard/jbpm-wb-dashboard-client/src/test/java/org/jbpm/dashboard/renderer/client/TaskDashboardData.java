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
package org.jbpm.dashboard.renderer.client;

import java.util.Date;

import org.dashbuilder.dataset.RawDataSet;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;

public class TaskDashboardData extends RawDataSet {

    public static final TaskDashboardData INSTANCE = new TaskDashboardData(
            new String[]{
                    // Ensure the tests also works with columns in a different case,
                    // which is actually the case when working with some DBs
                    COLUMN_PROCESS_NAME.toUpperCase(),
                    COLUMN_PROCESS_INSTANCE_ID.toUpperCase(),
                    COLUMN_TASK_ID.toUpperCase(),
                    COLUMN_TASK_NAME.toUpperCase(),
                    COLUMN_TASK_OWNER_ID.toUpperCase(),
                    COLUMN_TASK_CREATED_DATE.toUpperCase(),
                    COLUMN_TASK_START_DATE.toUpperCase(),
                    COLUMN_TASK_END_DATE.toUpperCase(),
                    COLUMN_TASK_STATUS.toUpperCase(),
                    COLUMN_TASK_DURATION.toUpperCase(),
                    COLUMN_PROCESS_EXTERNAL_ID.toUpperCase()},
            new Class[]{
                    String.class,
                    Integer.class,
                    Integer.class,
                    String.class,
                    String.class,
                    Date.class,
                    Date.class,
                    Date.class,
                    String.class,
                    Integer.class,
                    String.class},
            new String[][]{
                    {"Process A", "1", "1", "Task 1", "user1", "01/01/19 10:00", "01/01/19 12:00", null, TASK_STATUS_IN_PROGRESS, null, "deploymentId"},
                    {"Process A", "1", "2", "Task 2", "user1", "01/01/19 09:00", "11/01/19 12:00", "01/01/19 13:00", TASK_STATUS_COMPLETED, "9000", "deploymentId"},
                    {"Process A", "1", "3", "Task 3", "user2", "01/01/19 08:00", "10/01/19 12:00", null, TASK_STATUS_SUSPENDED, null, "deploymentId"},
                    {"Process A", "1", "4", "Task 4", "user2", "01/01/19 10:00", "09/01/19 12:00", null, TASK_STATUS_IN_PROGRESS, null, "deploymentId"},
                    {"Process B", "2", "5", "Task 2", "user1", "01/01/19 06:00", "08/01/19 12:00", null, TASK_STATUS_IN_PROGRESS, null, "deploymentId"},
                    {"Process B", "2", "6", "Task 2", "user3", "01/01/19 07:00", "07/01/19 12:00", null, TASK_STATUS_ERROR, null, "deploymentId"},
                    {"Process B", "2", "7", "Task 3", "user4", "01/01/19 08:00", "05/01/19 12:00", null, TASK_STATUS_RESERVED, null, "deploymentId"},
                    {"Process B", "2", "8", "Task 4", "user4", "01/01/19 10:00", "05/11/19 12:00", "12/02/19 16:00", TASK_STATUS_COMPLETED, "10000", "deploymentId"},
                    {"Process B", "2", "9", "Task 4", "user4", "01/01/19 10:00", "05/11/19 12:00", null, TASK_STATUS_EXITED, null, "deploymentId"}
            });

    public TaskDashboardData(String[] columnIds,
                             Class[] types,
                             String[][] data) {
        super(columnIds,
              types,
              data);
    }
}
