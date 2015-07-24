/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.dashboard.renderer.client.panel.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web- toolkit-doc-1-5&t=DevGuideInternationalization
 * (for more information).
 * <p/>
 * Each method name matches up with a key in ProcessDashboardConstants.properties (the properties file can still be used on the server). To use
 * this, use <code>GWT.create(DashboardConstants.class)</code>.
 */
public interface DashboardConstants extends Messages {

    DashboardConstants INSTANCE = GWT.create(DashboardConstants.class);

    String processDashboardName();
    String processScreenTitle();
    String taskScreenTitle();
    String allProcesses();
    String allTasks();
    String selectedProcessStatusHeader(String status, String process);
    String selectedTaskStatusHeader(String status, String process);

    String noData();
    String loading();
    String process();
    String processes();
    String processInstances();
    String processTableInstanceId();
    String processTableDeploymentId();
    String processTableProcessId();
    String processTableName();
    String processTableInitiator();
    String processTableStatus();
    String processTableVersion();
    String processTableStartDate();
    String processTableEndDate();
    String processTableDuration();
    String processStatusPending();
    String processStatusActive();
    String processStatusCompleted();
    String processStatusAborted();
    String processStatusSuspended();

    String totalProcesses();
    String pendingProcesses();
    String activeProcesses();
    String completedProcesses();
    String abortedProcesses();
    String suspendedProcesses();

    String processesByType();
    String processesByVersion();
    String processesByRunningTime();
    String processesStartedByUser();
    String processesByStartDate();
    String processesByEndDate();

    String processVersion();
    String processAverageDuration();
    String processCount();
    String processUser();
    String processStartDate();
    String processEndDate();

    String loadingDashboard();
    String showDashboard();
    String showInstances();
    String showTasks();

    String total();
    String byType();
    String byStartDate();
    String byUser();
    String byRunningTime();
    String byEndDate();
    String byVersion();
    String byProcess();
    String byCreationDate();
    String byStatus();

    String totalTasks();
    String tasksCreated();
    String tasksReady();
    String tasksReserved();
    String tasksInProgress();
    String tasksSuspended();
    String tasksCompleted();
    String tasksFailed();
    String tasksError();
    String tasksExited();
    String tasksObsolete();
    String tasksByProcess();
    String tasksByRunningTime();
    String tasksByOwner();
    String tasksByStatus();
    String tasksByCreationDate();
    String taskCreationDate();
    String tasksByStartDate();
    String tasksByEndDate();

    String taskAverageDuration();
    String taskCount();
    String taskOwner();
    String taskStatus();
    String taskStartDate();
    String taskEndDate();

    String taskStatusCreated();
    String taskStatusReady();
    String taskStatusReserved();
    String taskStatusInProgress();
    String taskStatusSuspended();
    String taskStatusCompleted();
    String taskStatusFailed();
    String taskStatusError();
    String taskStatusExited();
    String taskStatusObsolete();

    String tasks();
    String taskInstances();
    String taskTableId();
    String taskTableProcess();
    String taskTableName();
    String taskTableOwner();
    String taskTableStatus();
    String taskTableStartDate();
    String taskTableEndDate();
    String taskTableDuration();
}
