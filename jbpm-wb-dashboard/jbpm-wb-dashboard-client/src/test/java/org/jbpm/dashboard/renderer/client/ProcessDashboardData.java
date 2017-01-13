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

public class ProcessDashboardData extends RawDataSet {

    /**
     * Status:
     * 0: pending
     * 1: active
     * 2: completed
     * 3: aborted
     * 4: suspended
     */
    public static final ProcessDashboardData INSTANCE = new ProcessDashboardData(
            new String[] {
                    // Ensure the tests also works with columns in a different case,
                    // which is actually the case when working with some DBs
                    COLUMN_PROCESS_ID.toUpperCase(),
                    COLUMN_PROCESS_INSTANCE_ID.toUpperCase(),
                    COLUMN_PROCESS_EXTERNAL_ID.toUpperCase(),
                    COLUMN_PROCESS_NAME.toUpperCase(),
                    COLUMN_PROCESS_USER_ID.toUpperCase(),
                    COLUMN_PROCESS_START_DATE.toUpperCase(),
                    COLUMN_PROCESS_END_DATE.toUpperCase(),
                    COLUMN_PROCESS_STATUS.toUpperCase(),
                    COLUMN_PROCESS_VERSION.toUpperCase(),
                    COLUMN_PROCESS_DURATION.toUpperCase()},
            new Class[] {
                    String.class,
                    Integer.class,
                    String.class,
                    String.class,
                    String.class,
                    Date.class,
                    Date.class,
                    Integer.class,
                    String.class,
                    Integer.class},
            new String[][] {
                    {"1", "1", "org.jbpm.test", "Process A", "user1", "01/01/19 12:00", null, "1", "1", null},
                    {"1", "2", "org.jbpm.test", "Process A", "user2", "01/01/19 12:00", null, "1", "1", null},
                    {"1", "3", "org.jbpm.test", "Process B", "user1", "01/01/19 12:00", null, "1", "1", null},
                    {"1", "4", "org.jbpm.test", "Process B", "user2", "01/01/19 12:00", "01/02/19 10:00", "2", "1", "100000"}
            });

    public ProcessDashboardData(String[] columnIds, Class[] types, String[][] data) {
        super(columnIds, types, data);
    }

}
