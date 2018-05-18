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

package org.jbpm.workbench.ht.util;

import java.util.Arrays;

public enum TaskStatus {

    TASK_STATUS_CREATED("Created"),
    TASK_STATUS_READY("Ready"),
    TASK_STATUS_RESERVED("Reserved"),
    TASK_STATUS_IN_PROGRESS("InProgress"),
    TASK_STATUS_SUSPENDED("Suspended"),
    TASK_STATUS_FAILED("Failed"),
    TASK_STATUS_ERROR("Error"),
    TASK_STATUS_EXITED("Exited"),
    TASK_STATUS_OBSOLETE("Obsolete"),
    TASK_STATUS_COMPLETED("Completed");

    private String identifier;

    TaskStatus(final String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static TaskStatus fromStatus(final String status) {
        return Arrays.stream(TaskStatus.values()).filter(e -> e.getIdentifier().equals(status)).findFirst().get();
    }

    public boolean equals(String otherName) {
        return identifier.equals(otherName);
    }

}