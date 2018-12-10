/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.backend.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.UserTaskSummary;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskSummary;

public class ProcessInstanceSummaryMapper implements Function<ProcessInstance, ProcessInstanceSummary> {

    @Override
    public ProcessInstanceSummary apply(ProcessInstance processInstance) {
        if (processInstance == null) {
            return null;
        }

        ProcessInstanceSummary summary = new ProcessInstanceSummary(
                processInstance.getId(),
                processInstance.getProcessId(),
                processInstance.getContainerId(),
                processInstance.getProcessName(),
                processInstance.getProcessVersion(),
                processInstance.getState(),
                processInstance.getDate(),
                null,
                processInstance.getInitiator(),
                processInstance.getProcessInstanceDescription(),
                processInstance.getCorrelationKey(),
                processInstance.getParentId(),
                null,
                processInstance.getSlaCompliance(),
                processInstance.getSlaDueDate(),
                0
        );

        if (processInstance.getActiveUserTasks() != null && processInstance.getActiveUserTasks().getTasks() != null) {
            List<TaskSummary> tasks = processInstance.getActiveUserTasks().getItems();

            List<UserTaskSummary> userTaskSummaries = new ArrayList<UserTaskSummary>();
            for (TaskSummary taskSummary : tasks) {
                UserTaskSummary userTaskSummary = new UserTaskSummary(taskSummary.getId(),
                                                                      taskSummary.getName(),
                                                                      taskSummary.getActualOwner(),
                                                                      taskSummary.getStatus());

                userTaskSummaries.add(userTaskSummary);
            }
            summary.setActiveTasks(userTaskSummaries);
        }

        return summary;
    }
}
