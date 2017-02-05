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

package org.jbpm.workbench.cm.util;

import java.util.List;

import org.jbpm.workbench.cm.model.CaseActionSummary;


public class Actions {

    private List<CaseActionSummary> availableActions;
    private List<CaseActionSummary> inProgressAction;
    private List<CaseActionSummary> completeActions;

    public List<CaseActionSummary> getAvailableActions() {
        return availableActions;
    }

    public void setAvailableActions(List<CaseActionSummary> availableActions) {
        this.availableActions = availableActions;
    }

    public List<CaseActionSummary> getInProgressAction() {
        return inProgressAction;
    }

    public void setInProgressAction(List<CaseActionSummary> inProgressAction) {
        this.inProgressAction = inProgressAction;
    }

    public List<CaseActionSummary> getCompleteActions() {
        return completeActions;
    }

    public void setCompleteActions(List<CaseActionSummary> completeActions) {
        this.completeActions = completeActions;
    }

}