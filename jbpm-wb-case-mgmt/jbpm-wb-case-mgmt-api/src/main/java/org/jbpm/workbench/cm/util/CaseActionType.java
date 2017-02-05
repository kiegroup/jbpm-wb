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

package org.jbpm.workbench.cm.util;

public enum CaseActionType {

    AD_HOC("AD_HOC"),

    ADD_DYNAMIC_USER_TASK("add_dynamic_user_task"),

    ADD_DYNAMIC_TASK("add_dynamic_task"),

    ADD_DYNAMIC_SUBPROCESS("add_dynamic_subprocess"),

    INPROGRESS("InProgress"),

    COMPLETED("Completed");

    private String status;


    CaseActionType(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

}