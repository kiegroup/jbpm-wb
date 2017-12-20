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

    String PROCESS_INSTANCE_DETAILS_SCREEN = "Process Instance Details Multi";
    String PROCESS_INSTANCE_LIST_SCREEN = "DataSet Process Instance List With Variables";
    String PROCESS_DEFINITION_DETAILS_SCREEN = "Advanced Process Details Multi";
    String PROCESS_DEFINITION_LIST_SCREEN = "Process Definition List";
    String DASHBOARD_SCREEN = "DashboardScreen";
    String EXECUTION_ERROR_DETAILS_SCREEN = "Execution Error Details";
    String EXECUTION_ERROR_LIST_SCREEN = "Execution Error List";
    String JOB_LIST_SCREEN = "Job List";
    String JOB_DETAILS_SCREEN = "Job Details";
    String TASK_ADMIN_LIST_SCREEN = "Task Admin List";
    String TASK_LIST_SCREEN = "Task List";
    String TASK_DETAILS_SCREEN = "Task Details Multi";

}