/**
 * Copyright (C) 2012 JBoss Inc
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
package org.jbpm.dashboard.renderer.client.panel;

import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.renderer.client.DefaultRenderer;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardConstants;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;

public class DashboardKpis {

    public static final int METRIC_WIDTH = 200;
    public static final int METRIC_HEIGHT = 90;
    public static final int CHART_WIDTH = 400;
    public static final int CHART_HEIGHT = 200;
    public static final String NO_DECIMALS = "#,##0";
    public static final String BG_COLOR = "FFFFFF";

    public static final DisplayerSettings PROCESSES_TABLE = DisplayerSettingsFactory
            .newTableSettings()
            .uuid(DashboardConstants.INSTANCE.processInstances())
            .title(DashboardConstants.INSTANCE.processInstances())
            .dataset(DATASET_PROCESS_INSTANCES)
            .column(COLUMN_PROCESS_INSTANCE_ID).format(DashboardConstants.INSTANCE.processTableInstanceId(), NO_DECIMALS)
            .column(COLUMN_PROCESS_EXTERNAL_ID).format(DashboardConstants.INSTANCE.processTableDeploymentId())
            .column(COLUMN_PROCESS_ID).format(DashboardConstants.INSTANCE.processTableProcessId())
            .column(COLUMN_PROCESS_NAME).format(DashboardConstants.INSTANCE.processTableName())
            .column(COLUMN_PROCESS_USER_ID).format(DashboardConstants.INSTANCE.processTableInitiator())
            .column(COLUMN_PROCESS_STATUS).format(DashboardConstants.INSTANCE.processTableStatus()).expression(processStatusExpression())
            .column(COLUMN_PROCESS_VERSION).format(DashboardConstants.INSTANCE.processTableVersion())
            .column(COLUMN_PROCESS_START_DATE).format(DashboardConstants.INSTANCE.processTableStartDate(), "MMM dd, yyyy")
            .column(COLUMN_PROCESS_END_DATE).format(DashboardConstants.INSTANCE.processTableEndDate(), "MMM dd, yyyy")
            .column(COLUMN_PROCESS_DURATION).format(DashboardConstants.INSTANCE.processTableDuration())
            .tablePageSize(10)
            .tableOrderEnabled(true)
            .tableOrderDefault(COLUMN_PROCESS_START_DATE, DESCENDING)
            .tableWidth(1400)
            .renderer(DefaultRenderer.UUID)
            .filterOn(true, false, true)
            .refreshOn()
            .buildSettings();

    public static String processStatusExpression() {
        return "['" + DashboardConstants.INSTANCE.processStatusPending() + "'," +
                "'" + DashboardConstants.INSTANCE.processStatusActive() + "'," +
                "'" + DashboardConstants.INSTANCE.processStatusCompleted() + "'," +
                "'" + DashboardConstants.INSTANCE.processStatusAborted() + "'," +
                "'" + DashboardConstants.INSTANCE.processStatusSuspended() + "'][value]";
    }

    public static final DisplayerSettings PROCESSES_TOTAL = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.totalProcesses())
            .titleVisible(true)
            .dataset(DATASET_PROCESS_INSTANCES)
            .column(COUNT, "Processes")
            .format(DashboardConstants.INSTANCE.processes(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_ACTIVE = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.activeProcesses())
            .titleVisible(true)
            .dataset(DATASET_PROCESS_INSTANCES)
            .filter(COLUMN_PROCESS_STATUS, equalsTo(1))
            .column(COUNT, "Processes")
            .format(DashboardConstants.INSTANCE.activeProcesses(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_PENDING = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.pendingProcesses())
            .titleVisible(true)
            .dataset(DATASET_PROCESS_INSTANCES)
            .filter(COLUMN_PROCESS_STATUS, equalsTo(0))
            .column(COUNT, "Processes")
            .format(DashboardConstants.INSTANCE.pendingProcesses(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_SUSPENDED = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.suspendedProcesses())
            .titleVisible(true)
            .dataset(DATASET_PROCESS_INSTANCES)
            .filter(COLUMN_PROCESS_STATUS, equalsTo(4))
            .column(COUNT, "Processes")
            .format(DashboardConstants.INSTANCE.suspendedProcesses(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_ABORTED = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.abortedProcesses())
            .titleVisible(true)
            .dataset(DATASET_PROCESS_INSTANCES)
            .filter(COLUMN_PROCESS_STATUS, equalsTo(3))
            .column(COUNT, "Processes")
            .format(DashboardConstants.INSTANCE.abortedProcesses(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_COMPLETED = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.completedProcesses())
            .titleVisible(true)
            .dataset(DATASET_PROCESS_INSTANCES)
            .filter(COLUMN_PROCESS_STATUS, equalsTo(2))
            .column(COUNT, "Processes")
            .format(DashboardConstants.INSTANCE.completedProcesses(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_BY_TYPE = DisplayerSettingsFactory
            .newPieChartSettings()
            .title(DashboardConstants.INSTANCE.processesByType())
            .dataset(DATASET_PROCESS_INSTANCES)
            .group(COLUMN_PROCESS_NAME)
            .column(COLUMN_PROCESS_NAME).format(DashboardConstants.INSTANCE.process())
            .column(COUNT, "Processes").format(DashboardConstants.INSTANCE.processes(), NO_DECIMALS)
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOn("right")
            .margins(10, 10, 10, 10)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_BY_VERSION = DisplayerSettingsFactory
            .newPieChartSettings()
            .title(DashboardConstants.INSTANCE.processesByVersion())
            .dataset(DATASET_PROCESS_INSTANCES)
            .group(COLUMN_PROCESS_VERSION)
            .column(COLUMN_PROCESS_VERSION).format(DashboardConstants.INSTANCE.processVersion()).expression("'Version ' + value")
            .column(COUNT, "Processes").format(DashboardConstants.INSTANCE.processes(), NO_DECIMALS)
            .subType_Donut()
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOn("right")
            .margins(10, 10, 10, 10)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_BY_RUNNING_TIME = DisplayerSettingsFactory
            .newBubbleChartSettings()
            .uuid(DashboardConstants.INSTANCE.processesByRunningTime())
            .title(DashboardConstants.INSTANCE.processesByRunningTime())
            .dataset(DATASET_PROCESS_INSTANCES)
            .filter(notNull(COLUMN_PROCESS_DURATION))
            .group(COLUMN_PROCESS_NAME)
            .column(COLUMN_PROCESS_NAME).format(DashboardConstants.INSTANCE.process())
            .column(COUNT, "Processes").format(DashboardConstants.INSTANCE.processes(), NO_DECIMALS)
            .column(COLUMN_PROCESS_DURATION, AVERAGE, COLUMN_PROCESS_DURATION)
            .format(DashboardConstants.INSTANCE.processAverageDuration(), "#,##0 min").expression("value/60000")
            .column(COLUMN_PROCESS_NAME).format(DashboardConstants.INSTANCE.process())
            .column(COUNT, "Processes").format(DashboardConstants.INSTANCE.processCount(), NO_DECIMALS)
            .xAxisTitle(DashboardConstants.INSTANCE.processCount())
            .yAxisTitle(DashboardConstants.INSTANCE.processAverageDuration())
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .margins(10, 30, 60, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_BY_USER = DisplayerSettingsFactory
            .newBarChartSettings()
            .title(DashboardConstants.INSTANCE.processesStartedByUser())
            .dataset(DATASET_PROCESS_INSTANCES)
            .filter(notNull(COLUMN_PROCESS_USER_ID))
            .group(COLUMN_PROCESS_USER_ID)
            .column(COLUMN_PROCESS_USER_ID).format(DashboardConstants.INSTANCE.processUser())
            .column(COUNT, "Processes").format(DashboardConstants.INSTANCE.processes(), NO_DECIMALS)
            .subType_Bar()
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOn("right")
            .margins(10, 20, 120, 10)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_BY_START_DATE = DisplayerSettingsFactory
            .newAreaChartSettings()
            .title(DashboardConstants.INSTANCE.processesByStartDate())
            .dataset(DATASET_PROCESS_INSTANCES)
            .filter(notNull(COLUMN_PROCESS_START_DATE))
            .group(COLUMN_PROCESS_START_DATE).dynamic(30, DateIntervalType.DAY, true)
            .column(COLUMN_PROCESS_START_DATE).format(DashboardConstants.INSTANCE.processStartDate())
            .column(COUNT, "Processes").format(DashboardConstants.INSTANCE.processes(), NO_DECIMALS)
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOff()
            .margins(50, 5, 50, 20)
            .backgroundColor(BG_COLOR)
            .filterOn(true, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings PROCESSES_BY_END_DATE = DisplayerSettingsFactory
            .newAreaChartSettings()
            .title(DashboardConstants.INSTANCE.processesByEndDate())
            .dataset(DATASET_PROCESS_INSTANCES)
            .filter(notNull(COLUMN_PROCESS_END_DATE))
            .group(COLUMN_PROCESS_END_DATE).dynamic(30, DateIntervalType.DAY, true)
            .column(COLUMN_PROCESS_END_DATE).format(DashboardConstants.INSTANCE.processEndDate())
            .column(COUNT, "Processes").format(DashboardConstants.INSTANCE.processes(), NO_DECIMALS)
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOff()
            .margins(50, 5, 50, 20)
            .backgroundColor(BG_COLOR)
            .filterOn(true, true, true)
            .refreshOn()
            .buildSettings();


    // Task displayers


    public static final DisplayerSettings TASKS_TABLE = DisplayerSettingsFactory
            .newTableSettings()
            .uuid(DashboardConstants.INSTANCE.taskInstances())
            .title(DashboardConstants.INSTANCE.taskInstances())
            .dataset(DATASET_HUMAN_TASKS)
            .column(COLUMN_TASK_ID).format(DashboardConstants.INSTANCE.taskTableId(), NO_DECIMALS)
            .column(COLUMN_PROCESS_NAME).format(DashboardConstants.INSTANCE.taskTableProcess())
            .column(COLUMN_TASK_NAME).format(DashboardConstants.INSTANCE.taskTableName())
            .column(COLUMN_TASK_OWNER_ID).format(DashboardConstants.INSTANCE.taskTableOwner())
            .column(COLUMN_TASK_STATUS).format(DashboardConstants.INSTANCE.taskTableStatus())
            .column(COLUMN_TASK_CREATED_DATE).format(DashboardConstants.INSTANCE.taskTableStartDate(), "MMM dd, yyyy")
            .column(COLUMN_TASK_END_DATE).format(DashboardConstants.INSTANCE.taskTableEndDate(), "MMM dd, yyyy")
            .column(COLUMN_TASK_DURATION).format(DashboardConstants.INSTANCE.taskTableDuration())
            .tablePageSize(10)
            .tableOrderEnabled(true)
            .tableOrderDefault(COLUMN_TASK_CREATED_DATE, DESCENDING)
            .tableWidth(1400)
            .renderer(DefaultRenderer.UUID)
            .filterOn(true, false, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_TOTAL = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.totalTasks())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasks(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_CREATED = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.tasksCreated())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_CREATED))
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasksCreated(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_READY = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.tasksReady())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_READY))
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasksReady(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_RESERVED = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.tasksReserved())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_RESERVED))
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasksReserved(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_IN_PROGRESS = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.tasksInProgress())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_IN_PROGRESS))
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasksInProgress(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_SUSPENDED = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.tasksSuspended())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_SUSPENDED))
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasksSuspended(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_COMPLETED = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.tasksCompleted())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_COMPLETED))
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasksCompleted(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_FAILED = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.tasksFailed())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_FAILED))
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasksFailed(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_ERROR = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.tasksError())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_ERROR))
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasksError(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_EXITED = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.tasksExited())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_EXITED))
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasksExited(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_OBSOLETE = DisplayerSettingsFactory
            .newMetricSettings()
            .title(DashboardConstants.INSTANCE.tasksObsolete())
            .titleVisible(true)
            .dataset(DATASET_HUMAN_TASKS)
            .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_OBSOLETE))
            .column(COUNT, "Tasks")
            .format(DashboardConstants.INSTANCE.tasksObsolete(), NO_DECIMALS)
            .width(METRIC_WIDTH).height(METRIC_HEIGHT)
            .margins(0, 0, 0, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_BY_PROCESS = DisplayerSettingsFactory
            .newPieChartSettings()
            .title(DashboardConstants.INSTANCE.tasksByProcess())
            .dataset(DATASET_HUMAN_TASKS)
            .group(COLUMN_PROCESS_NAME)
            .column(COLUMN_PROCESS_NAME).format(DashboardConstants.INSTANCE.process())
            .column(COLUMN_TASK_ID, COUNT, "Tasks").format(DashboardConstants.INSTANCE.tasks(), NO_DECIMALS)
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOn("right")
            .margins(10, 10, 10, 10)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_BY_RUNNING_TIME = DisplayerSettingsFactory
            .newBubbleChartSettings()
            .uuid(DashboardConstants.INSTANCE.tasksByRunningTime())
            .title(DashboardConstants.INSTANCE.tasksByRunningTime())
            .dataset(DATASET_HUMAN_TASKS)
            .filter(notNull(COLUMN_TASK_DURATION))
            .group(COLUMN_PROCESS_NAME)
            .column(COLUMN_PROCESS_NAME).format(DashboardConstants.INSTANCE.process())
            .column(COLUMN_TASK_ID, COUNT, "Tasks").format(DashboardConstants.INSTANCE.tasks(), NO_DECIMALS)
            .column(COLUMN_TASK_DURATION, AVERAGE).format(DashboardConstants.INSTANCE.taskAverageDuration(), "#,##0 min").expression("value/60000")
            .column(COLUMN_PROCESS_NAME).format(DashboardConstants.INSTANCE.process())
            .column(COLUMN_TASK_ID, COUNT, "Tasks").format(DashboardConstants.INSTANCE.taskCount(), NO_DECIMALS)
            .xAxisTitle(DashboardConstants.INSTANCE.taskCount())
            .yAxisTitle(DashboardConstants.INSTANCE.taskAverageDuration())
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .margins(10, 30, 60, 0)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_BY_OWNER = DisplayerSettingsFactory
            .newBarChartSettings()
            .title(DashboardConstants.INSTANCE.tasksByOwner())
            .dataset(DATASET_HUMAN_TASKS)
            .filter(notNull(COLUMN_TASK_OWNER_ID))
            .group(COLUMN_TASK_OWNER_ID)
            .column(COLUMN_TASK_OWNER_ID).format(DashboardConstants.INSTANCE.taskOwner())
            .column(COUNT, "Tasks").format(DashboardConstants.INSTANCE.tasks(), NO_DECIMALS)
            .subType_Bar()
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOn("right")
            .margins(10, 20, 120, 10)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_BY_STATUS = DisplayerSettingsFactory
            .newPieChartSettings()
            .title(DashboardConstants.INSTANCE.tasksByStatus())
            .dataset(DATASET_HUMAN_TASKS)
            .filter(notNull(COLUMN_TASK_STATUS))
            .group(COLUMN_TASK_STATUS)
            .column(COLUMN_TASK_STATUS).format(DashboardConstants.INSTANCE.taskStatus())
            .column(COUNT, "Tasks").format(DashboardConstants.INSTANCE.tasks(), NO_DECIMALS)
            .subType_Donut()
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOn("right")
            .margins(10, 20, 120, 10)
            .backgroundColor(BG_COLOR)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_BY_CREATION_DATE = DisplayerSettingsFactory
            .newAreaChartSettings()
            .title(DashboardConstants.INSTANCE.tasksByCreationDate())
            .dataset(DATASET_HUMAN_TASKS)
            .filter(notNull(COLUMN_TASK_CREATED_DATE))
            .group(COLUMN_TASK_CREATED_DATE).dynamic(30, DateIntervalType.DAY, true)
            .column(COLUMN_TASK_CREATED_DATE).format(DashboardConstants.INSTANCE.taskCreationDate())
            .column(COUNT, "Tasks").format(DashboardConstants.INSTANCE.tasks(), NO_DECIMALS)
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOff()
            .margins(50, 5, 50, 20)
            .backgroundColor(BG_COLOR)
            .filterOn(true, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_BY_START_DATE = DisplayerSettingsFactory
            .newAreaChartSettings()
            .title(DashboardConstants.INSTANCE.tasksByStartDate())
            .dataset(DATASET_HUMAN_TASKS)
            .filter(notNull(COLUMN_TASK_START_DATE))
            .group(COLUMN_TASK_START_DATE).dynamic(30, DateIntervalType.DAY, true)
            .column(COLUMN_TASK_START_DATE).format(DashboardConstants.INSTANCE.taskStartDate())
            .column(COUNT, "Tasks").format(DashboardConstants.INSTANCE.tasks(), NO_DECIMALS)
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOff()
            .margins(50, 5, 50, 20)
            .backgroundColor(BG_COLOR)
            .filterOn(true, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings TASKS_BY_END_DATE = DisplayerSettingsFactory
            .newAreaChartSettings()
            .title(DashboardConstants.INSTANCE.tasksByEndDate())
            .dataset(DATASET_HUMAN_TASKS)
            .filter(notNull(COLUMN_TASK_END_DATE))
            .group(COLUMN_TASK_END_DATE).dynamic(30, DateIntervalType.DAY, true)
            .column(COLUMN_TASK_END_DATE).format(DashboardConstants.INSTANCE.taskEndDate())
            .column(COUNT, "Tasks").format(DashboardConstants.INSTANCE.tasks(), NO_DECIMALS)
            .width(CHART_WIDTH).height(CHART_HEIGHT)
            .legendOff()
            .margins(50, 5, 50, 20)
            .backgroundColor(BG_COLOR)
            .filterOn(true, true, true)
            .refreshOn()
            .buildSettings();
}