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

package org.jbpm.workbench.common.client.util;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TaskUtils {

    public static String TASK_STATUS_CREATED = "Created";
    public static String TASK_STATUS_READY = "Ready";
    public static String TASK_STATUS_RESERVED = "Reserved";
    public static String TASK_STATUS_IN_PROGRESS = "InProgress";
    public static String TASK_STATUS_SUSPENDED = "Suspended";
    public static String TASK_STATUS_FAILED = "Failed";
    public static String TASK_STATUS_ERROR = "Error";
    public static String TASK_STATUS_EXITED = "Exited";
    public static String TASK_STATUS_OBSOLETE = "Obsolete";
    public static String TASK_STATUS_COMPLETED = "Completed";

    public enum TaskType {
        PERSONAL, ACTIVE, GROUP, ALL, ADMIN
    }

    public static List<String> getStatusByType(TaskType type) {
        ImmutableList<String> status = null;
        switch (type) {
            case ACTIVE:
                status = ImmutableList.of(TASK_STATUS_READY, TASK_STATUS_RESERVED,
                                          TASK_STATUS_IN_PROGRESS);
                break;
            case GROUP:
                status = ImmutableList.of(TASK_STATUS_READY);
                break;
            case ALL:
                status = ImmutableList.of(TASK_STATUS_CREATED, TASK_STATUS_READY, TASK_STATUS_RESERVED,
                                          TASK_STATUS_IN_PROGRESS, TASK_STATUS_SUSPENDED, TASK_STATUS_FAILED, TASK_STATUS_ERROR,
                                          TASK_STATUS_EXITED, TASK_STATUS_OBSOLETE, TASK_STATUS_COMPLETED);
                break;
            case PERSONAL:
                status = ImmutableList.of(TASK_STATUS_IN_PROGRESS, TASK_STATUS_CREATED, TASK_STATUS_RESERVED);
                break;
            case ADMIN:
                status = ImmutableList.of(TASK_STATUS_READY,
                                          TASK_STATUS_IN_PROGRESS, TASK_STATUS_CREATED,
                                          TASK_STATUS_RESERVED, TASK_STATUS_SUSPENDED);
                break;
            default:
                throw new IllegalStateException("Unrecognized view type '" + type + "'!");

        }
        return Lists.newArrayList(status);
    }
}
