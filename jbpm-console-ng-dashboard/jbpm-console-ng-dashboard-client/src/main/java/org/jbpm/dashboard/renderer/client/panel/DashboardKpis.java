/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import org.jbpm.console.ng.gc.client.util.DateUtils;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardI18n;

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
        public static int metricJsCounter = 1;

        public static final String METRIC_HTML = "<div id=\"${this}\" class=\"card-pf card-pf-accented card-pf-aggregate-status\"" +
                " style=\"background-color:${bgColor}; width:${width}px; height:${height}px;" +
                " margin-top:${marginTop}px; margin-right:${marginRight}px; margin-bottom:${marginBottom}px; margin-left:${marginLeft}px;\">\n" +
                "  <h2 id=\"${thisValue}\">${value}</h2>\n" +
                "  <p id=\"${thisTitle}\" style=\"font-weight:400\">${title}</p>\n" +
                "</div>";

        public static final String metricJs() {
                String filterOn = "filterOn" + metricJsCounter++;
                return "var " + filterOn + "= ${isFilterOn};\n" +
                        "${this}.style.cursor=\"pointer\";\n" +
                        "${this}.style.backgroundColor = " + filterOn + " ? \"#2491C8\" : \"${bgColor}\";\n" +
                        "${thisValue}.style.color = " + filterOn + " ? \"white\" : \"black\";\n" +
                        "${thisTitle}.style.color = " + filterOn + "  ? \"white\" : \"black\";\n" +
                        "\n" +
                        "${this}.onclick = function() {\n" +
                        "  " + filterOn + " = !" + filterOn + ";\n" +
                        "  ${this}.style.backgroundColor = " + filterOn + "  ? \"#2491C8\" : \"${bgColor}\";\n" +
                        "  ${thisValue}.style.color = " + filterOn + "  ? \"white\" : \"black\";\n" +
                        "  ${thisTitle}.style.color = " + filterOn + "  ? \"white\" : \"black\";\n" +
                        "  ${doFilter};\n" +
                        "};";
        }
        
        public static DisplayerSettings processesTable(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newTableSettings()
                        .uuid(i18n.processInstances())
                        .title(i18n.processInstances())
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .column(COLUMN_PROCESS_INSTANCE_ID).format(i18n.processTableInstanceId(), NO_DECIMALS)
                        .column(COLUMN_PROCESS_EXTERNAL_ID).format(i18n.processTableDeploymentId())
                        .column(COLUMN_PROCESS_ID).format(i18n.processTableProcessId())
                        .column(COLUMN_PROCESS_NAME).format(i18n.processTableName())
                        .column(COLUMN_PROCESS_USER_ID).format(i18n.processTableInitiator())
                        .column(COLUMN_PROCESS_STATUS).format(i18n.processTableStatus()).expression(processStatusExpression(i18n))
                        .column(COLUMN_PROCESS_VERSION).format(i18n.processTableVersion())
                        .column(COLUMN_PROCESS_START_DATE).format(i18n.processTableStartDate(), DateUtils.getDateTimeFormatMask())
                        .column(COLUMN_PROCESS_END_DATE).format(i18n.processTableEndDate(), DateUtils.getDateTimeFormatMask())
                        .column(COLUMN_PROCESS_DURATION).format(i18n.processTableDuration())
                        .tablePageSize(10)
                        .tableOrderEnabled(true)
                        .tableOrderDefault(COLUMN_PROCESS_START_DATE, DESCENDING)
                        .tableWidth(1400)
                        .renderer(DefaultRenderer.UUID)
                        .filterOn(true, false, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static String processStatusExpression(DashboardI18n i18n) {
                return "['" + i18n.processStatusPending() + "'," +
                        "'" + i18n.processStatusActive() + "'," +
                        "'" + i18n.processStatusCompleted() + "'," +
                        "'" + i18n.processStatusAborted() + "'," +
                        "'" + i18n.processStatusSuspended() + "'][value]";
        }

        public static DisplayerSettings processTotal(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.totalProcesses())
                        .titleVisible(true)
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .filter(notNull(COLUMN_PROCESS_ID))
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.processes(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesActive(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.activeProcesses())
                        .titleVisible(true)
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .filter(COLUMN_PROCESS_STATUS, equalsTo(1))
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.activeProcesses(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesPending(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.pendingProcesses())
                        .titleVisible(true)
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .filter(COLUMN_PROCESS_STATUS, equalsTo(0))
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.pendingProcesses(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesSuspended(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.suspendedProcesses())
                        .titleVisible(true)
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .filter(COLUMN_PROCESS_STATUS, equalsTo(4))
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.suspendedProcesses(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesAborted(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.abortedProcesses())
                        .titleVisible(true)
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .filter(COLUMN_PROCESS_STATUS, equalsTo(3))
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.abortedProcesses(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesCompleted(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.completedProcesses())
                        .titleVisible(true)
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .filter(COLUMN_PROCESS_STATUS, equalsTo(2))
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.completedProcesses(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesByType(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newPieChartSettings()
                        .title(i18n.processesByType())
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .group(COLUMN_PROCESS_NAME)
                        .column(COLUMN_PROCESS_NAME).format(i18n.process())
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.processes(), NO_DECIMALS)
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOn("right")
                        .margins(10, 10, 10, 10)
                        .backgroundColor(BG_COLOR)
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesByVersion(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newPieChartSettings()
                        .title(i18n.processesByVersion())
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .group(COLUMN_PROCESS_VERSION)
                        .column(COLUMN_PROCESS_VERSION).format(i18n.processVersion()).expression("'Version ' + value")
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.processes(), NO_DECIMALS)
                        .subType_Donut()
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOn("right")
                        .margins(10, 10, 10, 10)
                        .backgroundColor(BG_COLOR)
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesByRunningTime(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newBubbleChartSettings()
                        .uuid(i18n.processesByRunningTime())
                        .title(i18n.processesByRunningTime())
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .filter(notNull(COLUMN_PROCESS_DURATION))
                        .group(COLUMN_PROCESS_NAME)
                        .column(COLUMN_PROCESS_NAME).format(i18n.process())
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.processes(), NO_DECIMALS)
                        .column(COLUMN_PROCESS_DURATION, AVERAGE, COLUMN_PROCESS_DURATION)
                        .format(i18n.processAverageDuration(), "#,##0 min").expression("value/60000")
                        .column(COLUMN_PROCESS_NAME).format(i18n.process())
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.processCount(), NO_DECIMALS)
                        .xAxisTitle(i18n.processCount())
                        .yAxisTitle(i18n.processAverageDuration())
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .margins(10, 30, 60, 0)
                        .backgroundColor(BG_COLOR)
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesByUser(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newBarChartSettings()
                        .title(i18n.processesStartedByUser())
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .filter(notNull(COLUMN_PROCESS_USER_ID))
                        .group(COLUMN_PROCESS_USER_ID)
                        .column(COLUMN_PROCESS_USER_ID).format(i18n.processUser())
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.processes(), NO_DECIMALS)
                        .subType_Bar()
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOn("right")
                        .margins(10, 20, 120, 10)
                        .backgroundColor(BG_COLOR)
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesByStartDate(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newAreaChartSettings()
                        .title(i18n.processesByStartDate())
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .filter(notNull(COLUMN_PROCESS_START_DATE))
                        .group(COLUMN_PROCESS_START_DATE).dynamic(30, DateIntervalType.DAY, true)
                        .column(COLUMN_PROCESS_START_DATE).format(i18n.processStartDate())
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.processes(), NO_DECIMALS)
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOff()
                        .margins(50, 5, 50, 20)
                        .backgroundColor(BG_COLOR)
                        .filterOn(true, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings processesByEndDate(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newAreaChartSettings()
                        .title(i18n.processesByEndDate())
                        .dataset(DATASET_PROCESS_INSTANCES)
                        .filter(notNull(COLUMN_PROCESS_END_DATE))
                        .group(COLUMN_PROCESS_END_DATE).dynamic(30, DateIntervalType.DAY, true)
                        .column(COLUMN_PROCESS_END_DATE).format(i18n.processEndDate())
                        .column(COLUMN_PROCESS_INSTANCE_ID, COUNT, "Processes")
                        .format(COLUMN_PROCESS_INSTANCE_ID, i18n.processes(), NO_DECIMALS)
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOff()
                        .margins(50, 5, 50, 20)
                        .backgroundColor(BG_COLOR)
                        .filterOn(true, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        // Task displayers
        public static DisplayerSettings tasksTable(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newTableSettings()
                        .uuid(i18n.taskInstances())
                        .title(i18n.taskInstances())
                        .dataset(DATASET_HUMAN_TASKS)
                        .column(COLUMN_TASK_ID).format(i18n.taskTableId(), NO_DECIMALS)
                        .column(COLUMN_PROCESS_NAME).format(i18n.taskTableProcess())
                        .column(COLUMN_PROCESS_INSTANCE_ID).format(i18n.taskTableProcessInstanceId(), NO_DECIMALS)
                        .column(COLUMN_TASK_NAME).format(i18n.taskTableName())
                        .column(COLUMN_TASK_OWNER_ID).format(i18n.taskTableOwner())
                        .column(COLUMN_TASK_STATUS).format(i18n.taskTableStatus())
                        .column(COLUMN_TASK_CREATED_DATE).format(i18n.taskTableStartDate(), DateUtils.getDateTimeFormatMask())
                        .column(COLUMN_TASK_END_DATE).format(i18n.taskTableEndDate(), DateUtils.getDateTimeFormatMask())
                        .column(COLUMN_TASK_DURATION).format(i18n.taskTableDuration())
                        .tablePageSize(10)
                        .tableOrderEnabled(true)
                        .tableOrderDefault(COLUMN_TASK_CREATED_DATE, DESCENDING)
                        .tableWidth(1400)
                        .renderer(DefaultRenderer.UUID)
                        .filterOn(true, false, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksTotal(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.totalTasks())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(notNull(COLUMN_TASK_ID))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasks(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksCreated(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.tasksCreated())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_CREATED))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasksCreated(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksReady(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.tasksReady())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_READY))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasksReady(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksReserved(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.tasksReserved())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_RESERVED))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasksReserved(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksInProgress(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.tasksInProgress())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_IN_PROGRESS))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasksInProgress(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksSuspended(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.tasksSuspended())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_SUSPENDED))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasksSuspended(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksCompleted(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.tasksCompleted())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_COMPLETED))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasksCompleted(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksFailed(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.tasksFailed())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_FAILED))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasksFailed(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksError(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.tasksError())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_ERROR))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasksError(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksExited(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.tasksExited())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_EXITED))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasksExited(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksObsolete(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newMetricSettings()
                        .title(i18n.tasksObsolete())
                        .titleVisible(true)
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(COLUMN_TASK_STATUS, equalsTo(TASK_STATUS_OBSOLETE))
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasksObsolete(), NO_DECIMALS)
                        .width(METRIC_WIDTH).height(METRIC_HEIGHT)
                        .margins(0, 0, 0, 0)
                        .backgroundColor(BG_COLOR)
                        .htmlTemplate(METRIC_HTML)
                        .jsTemplate(metricJs())
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksByProcess(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newPieChartSettings()
                        .title(i18n.tasksByProcess())
                        .dataset(DATASET_HUMAN_TASKS)
                        .group(COLUMN_PROCESS_NAME)
                        .column(COLUMN_PROCESS_NAME).format(i18n.process())
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasks(), NO_DECIMALS)
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOn("right")
                        .margins(10, 10, 10, 10)
                        .backgroundColor(BG_COLOR)
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksByRunningTime(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newBubbleChartSettings()
                        .uuid(i18n.tasksByRunningTime())
                        .title(i18n.tasksByRunningTime())
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(notNull(COLUMN_TASK_DURATION))
                        .group(COLUMN_PROCESS_NAME)
                        .column(COLUMN_PROCESS_NAME).format(i18n.process())
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasks(), NO_DECIMALS)
                        .column(COLUMN_TASK_DURATION, AVERAGE).format(i18n.taskAverageDuration(), "#,##0 min").expression("value/60000")
                        .column(COLUMN_PROCESS_NAME).format(i18n.process())
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.taskCount(), NO_DECIMALS)
                        .xAxisTitle(i18n.taskCount())
                        .yAxisTitle(i18n.taskAverageDuration())
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .margins(10, 30, 60, 0)
                        .backgroundColor(BG_COLOR)
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksByOwner(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newBarChartSettings()
                        .title(i18n.tasksByOwner())
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(notNull(COLUMN_TASK_OWNER_ID))
                        .group(COLUMN_TASK_OWNER_ID)
                        .column(COLUMN_TASK_OWNER_ID).format(i18n.taskOwner())
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasks(), NO_DECIMALS)
                        .subType_Bar()
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOn("right")
                        .margins(10, 20, 120, 10)
                        .backgroundColor(BG_COLOR)
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksByStatus(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newPieChartSettings()
                        .title(i18n.tasksByStatus())
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(notNull(COLUMN_TASK_STATUS))
                        .group(COLUMN_TASK_STATUS)
                        .column(COLUMN_TASK_STATUS).format(i18n.taskStatus())
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasks(), NO_DECIMALS)
                        .subType_Donut()
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOn("right")
                        .margins(10, 20, 120, 10)
                        .backgroundColor(BG_COLOR)
                        .filterOn(false, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksByCreationDate(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newAreaChartSettings()
                        .title(i18n.tasksByCreationDate())
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(notNull(COLUMN_TASK_CREATED_DATE))
                        .group(COLUMN_TASK_CREATED_DATE).dynamic(30, DateIntervalType.DAY, true)
                        .column(COLUMN_TASK_CREATED_DATE).format(i18n.taskCreationDate())
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasks(), NO_DECIMALS)
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOff()
                        .margins(50, 5, 50, 20)
                        .backgroundColor(BG_COLOR)
                        .filterOn(true, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksByStartDate(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newAreaChartSettings()
                        .title(i18n.tasksByStartDate())
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(notNull(COLUMN_TASK_START_DATE))
                        .group(COLUMN_TASK_START_DATE).dynamic(30, DateIntervalType.DAY, true)
                        .column(COLUMN_TASK_START_DATE).format(i18n.taskStartDate())
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasks(), NO_DECIMALS)
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOff()
                        .margins(50, 5, 50, 20)
                        .backgroundColor(BG_COLOR)
                        .filterOn(true, true, true)
                        .refreshOn()
                        .buildSettings();
        }

        public static DisplayerSettings tasksByEndDate(DashboardI18n i18n) {
                return DisplayerSettingsFactory.newAreaChartSettings()
                        .title(i18n.tasksByEndDate())
                        .dataset(DATASET_HUMAN_TASKS)
                        .filter(notNull(COLUMN_TASK_END_DATE))
                        .group(COLUMN_TASK_END_DATE).dynamic(30, DateIntervalType.DAY, true)
                        .column(COLUMN_TASK_END_DATE).format(i18n.taskEndDate())
                        .column(COLUMN_TASK_ID, COUNT, "Tasks")
                        .format(COLUMN_TASK_ID, i18n.tasks(), NO_DECIMALS)
                        .width(CHART_WIDTH).height(CHART_HEIGHT)
                        .legendOff()
                        .margins(50, 5, 50, 20)
                        .backgroundColor(BG_COLOR)
                        .filterOn(true, true, true)
                        .refreshOn()
                        .buildSettings();
        }
}