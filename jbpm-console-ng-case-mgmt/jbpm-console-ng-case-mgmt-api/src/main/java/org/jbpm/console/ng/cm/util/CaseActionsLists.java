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

package org.jbpm.console.ng.cm.util;

import java.util.List;

import org.jbpm.console.ng.cm.model.CaseActionSummary;

public class CaseActionsLists {

    List<CaseActionSummary> availableActionList;
    List<CaseActionSummary> inprogressActionList;
    List<CaseActionSummary> completeActionList;

    public List<CaseActionSummary> getAvailableActionList() {
        return availableActionList;
    }

    public void setAvailableActionList(List<CaseActionSummary> availableActionList) {
        this.availableActionList = availableActionList;
    }

    public List<CaseActionSummary> getInprogressActionList() {
        return inprogressActionList;
    }

    public void setInprogressActionList(List<CaseActionSummary> inprogressActionList) {
        this.inprogressActionList = inprogressActionList;
    }

    public List<CaseActionSummary> getCompleteActionList() {
        return completeActionList;
    }

    public void setCompleteActionList(List<CaseActionSummary> completeActionList) {
        this.completeActionList = completeActionList;
    }
}