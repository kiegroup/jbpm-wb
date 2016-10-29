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

package org.jbpm.console.ng.cm.client.util;

import java.util.Arrays;

public enum CaseStatusEnum {

    PENDING("Pending", 0),

    ACTIVE("Active", 1),

    COMPLETED("Completed", 2),

    ABORTED("Aborted", 3),

    SUSPENDED("Suspended", 4);

    private String label;

    private Integer status;

    CaseStatusEnum(final String label, final Integer status) {
        this.label = label;
        this.status = status;
    }

    public static CaseStatusEnum fromStatus(final Integer status) {
        return Arrays.stream(CaseStatusEnum.values()).filter(e -> e.getStatus() == status).findFirst().get();
    }

    public Integer getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }

}