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
package org.jbpm.workbench.cm.backend.server;


import java.util.function.Function;

import org.jbpm.workbench.cm.model.CaseActionSummary;
import org.jbpm.workbench.cm.util.CaseActionStatus;
import org.kie.server.api.model.instance.NodeInstance;

public class CaseActionNodeInstanceMapper implements Function<NodeInstance, CaseActionSummary> {

    private String actualOwner;
    private CaseActionStatus actionStatus;

    public CaseActionNodeInstanceMapper(String actualOwner, CaseActionStatus actionStatus) {
        this.actualOwner = actualOwner;
        this.actionStatus = actionStatus;
    }

    @Override
    public CaseActionSummary apply(NodeInstance nodeInstance) {
        if (nodeInstance == null) {
            return null;
        }
        return CaseActionSummary.builder()
                .id(nodeInstance.getId())
                .name(nodeInstance.getName())
                .type(nodeInstance.getNodeType())
                .createdOn(nodeInstance.getDate())
                .actualOwner(actualOwner)
                .actionStatus(actionStatus)
                .build();
    }

}
