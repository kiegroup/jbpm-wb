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

package org.jbpm.workbench.es.backend.server;

import java.util.function.Function;

import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.jbpm.workbench.es.util.ExecutionErrorType;
import org.kie.server.api.model.admin.ExecutionErrorInstance;

public class ExecutionErrorSummaryMapper implements Function<ExecutionErrorInstance, ExecutionErrorSummary> {

    @Override
    public ExecutionErrorSummary apply(final ExecutionErrorInstance executionErrorInstance) {
        if (executionErrorInstance == null) {
            return null;
        }
        return ExecutionErrorSummary.builder()
                .errorId(executionErrorInstance.getErrorId())
                .error(executionErrorInstance.getError())
                .acknowledged(executionErrorInstance.isAcknowledged())
                .acknowledgedAt(executionErrorInstance.getAcknowledgedAt())
                .acknowledgedBy(executionErrorInstance.getAcknowledgedBy())
                .activityId(executionErrorInstance.getActivityId())
                .activityName(executionErrorInstance.getActivityName())
                .errorDate(executionErrorInstance.getErrorDate())
                .type(ExecutionErrorType.fromType(executionErrorInstance.getType()))
                .deploymentId(executionErrorInstance.getContainerId())
                .processInstanceId(executionErrorInstance.getProcessInstanceId())
                .processId(executionErrorInstance.getProcessId())
                .jobId(executionErrorInstance.getJobId())
                .message(executionErrorInstance.getErrorMessage())
                .build();
    }
}