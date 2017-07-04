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

package org.jbpm.workbench.es.util;

import java.util.Arrays;

public enum ExecutionErrorType {

    PROCESS("Process"),

    TASK("Task"),

    DB("DB"),

    JOB("Job"),

    UNKNOWN("Unknown");

    private String type;

    ExecutionErrorType(final String type) {
        this.type = type;
    }

    public static ExecutionErrorType fromType(final String type) {
        return Arrays.stream(ExecutionErrorType.values()).filter(e -> e.getType().equals(type)).findFirst().get();
    }

    public String getType() {
        return type;
    }

}
