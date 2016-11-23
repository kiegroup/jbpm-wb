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

public enum CaseStageStatus {

    AVAILABLE("Available"),

    COMPLETED("Completed"),

    CANCELED("Canceled");

    private String status;

    CaseStageStatus(final String status) {
        this.status = status;
    }

    public static CaseStageStatus fromStatus(final String status) {
        return Arrays.stream(CaseStageStatus.values()).filter(e -> e.getStatus().equals(status)).findFirst().get();
    }

    public String getStatus() {
        return status;
    }

}