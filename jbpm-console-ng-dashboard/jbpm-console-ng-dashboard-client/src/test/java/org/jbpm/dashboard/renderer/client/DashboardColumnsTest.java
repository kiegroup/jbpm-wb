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
package org.jbpm.dashboard.renderer.client;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl;
import org.junit.Before;
import org.junit.Test;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;
import static org.junit.Assert.*;

/**
 * JBPM-4902 related tests.
 * <p>Assure the DB columns used in the process and task dashboards match those defined in the ProcessInstanceLog
 * and BAMTaskSummaryImpl entities. That way the dashboard is protected against changes on such persistent entities.
 */
public class DashboardColumnsTest {

    Set<String> columnNamesProcessInstanceLog = new HashSet<String>();
    Set<String> columnNamesBAMTaskSummary = new HashSet<String>();

    @Before
    public void setUp() {
        Field[] fields = ProcessInstanceLog.class.getDeclaredFields();
        for (Field field : fields) {
            String columnName = field.getName();
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null && columnAnnotation.name() != null && columnAnnotation.name().length() > 0) {
                columnName = columnAnnotation.name();
            }
            columnNamesProcessInstanceLog.add(columnName);
        }
        fields = BAMTaskSummaryImpl.class.getDeclaredFields();
        for (Field field : fields) {
            String columnName = field.getName();
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null && columnAnnotation.name() != null && columnAnnotation.name().length() > 0) {
                columnName = columnAnnotation.name();
            }
            columnNamesBAMTaskSummary.add(columnName);
        }
    }

    @Test
    public void testProcessMonitoringColumns() {
        assertTrue(columnNamesProcessInstanceLog.contains(COLUMN_PROCESS_ID));
        assertTrue(columnNamesProcessInstanceLog.contains(COLUMN_PROCESS_DURATION));
        assertTrue(columnNamesProcessInstanceLog.contains(COLUMN_PROCESS_END_DATE));
        assertTrue(columnNamesProcessInstanceLog.contains(COLUMN_PROCESS_EXTERNAL_ID));
        assertTrue(columnNamesProcessInstanceLog.contains(COLUMN_PROCESS_INSTANCE_ID));
        assertTrue(columnNamesProcessInstanceLog.contains(COLUMN_PROCESS_NAME));
        assertTrue(columnNamesProcessInstanceLog.contains(COLUMN_PROCESS_START_DATE));
        assertTrue(columnNamesProcessInstanceLog.contains(COLUMN_PROCESS_STATUS));
        assertTrue(columnNamesProcessInstanceLog.contains(COLUMN_PROCESS_USER_ID));
        assertTrue(columnNamesProcessInstanceLog.contains(COLUMN_PROCESS_VERSION));
    }

    @Test
    public void testTaskMonitoringColumns() {
        assertTrue(columnNamesBAMTaskSummary.contains(COLUMN_TASK_CREATED_DATE));
        assertTrue(columnNamesBAMTaskSummary.contains(COLUMN_TASK_CREATOR_ID));
        assertTrue(columnNamesBAMTaskSummary.contains(COLUMN_TASK_DURATION));
        assertTrue(columnNamesBAMTaskSummary.contains(COLUMN_TASK_END_DATE));
        assertTrue(columnNamesBAMTaskSummary.contains(COLUMN_TASK_ID));
        assertTrue(columnNamesBAMTaskSummary.contains(COLUMN_TASK_NAME));
        assertTrue(columnNamesBAMTaskSummary.contains(COLUMN_TASK_OWNER_ID));
        assertTrue(columnNamesBAMTaskSummary.contains(COLUMN_TASK_START_DATE));
        assertTrue(columnNamesBAMTaskSummary.contains(COLUMN_TASK_STATUS));
    }
}