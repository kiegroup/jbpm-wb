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

package org.jbpm.workbench.ht.backend.server;

import java.util.function.Function;

import org.jbpm.workbench.ht.model.TaskSummary;
import org.kie.server.api.model.instance.TaskInstance;

public class TaskSummaryMapper implements Function<TaskInstance, TaskSummary> {

    @Override
    public TaskSummary apply(final TaskInstance taskInstance) {
        if (taskInstance == null) {
            return null;
        } else {
            return TaskSummary.builder()
                              .id(taskInstance.getId())
                              .name(taskInstance.getName())
                              .description(taskInstance.getDescription())
                              .status(taskInstance.getStatus())
                              .priority(taskInstance.getPriority())
                              .actualOwner(taskInstance.getActualOwner())
                              .createdBy(taskInstance.getCreatedBy())
                              .createdOn(taskInstance.getCreatedOn())
                              .activationTime(taskInstance.getActivationTime())
                              .expirationTime(taskInstance.getExpirationDate())
                              .processId(taskInstance.getProcessId())
                              .processInstanceId(taskInstance.getProcessInstanceId())
                              .deploymentId(taskInstance.getContainerId())
                              .parentId(taskInstance.getParentId())
                              .errorCount(0)
                              .build();
        }
    }
}
