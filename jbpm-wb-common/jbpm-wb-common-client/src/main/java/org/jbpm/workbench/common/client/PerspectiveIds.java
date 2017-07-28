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

package org.jbpm.workbench.common.client;

public interface PerspectiveIds extends org.kie.workbench.common.workbench.client.PerspectiveIds {

    String SEARCH_PARAMETER_PROCESS_INSTANCE_ID = "processInstanceId";
    String SEARCH_PARAMETER_PROCESS_DEFINITION_ID = "processDefinitionId";
    String SEARCH_PARAMETER_TASK_ID = "taskId";
    String SEARCH_PARAMETER_JOB_ID = "jobId";
    String SEARCH_PARAMETER_IS_ERROR_ACK = "isErrorAck";
}
