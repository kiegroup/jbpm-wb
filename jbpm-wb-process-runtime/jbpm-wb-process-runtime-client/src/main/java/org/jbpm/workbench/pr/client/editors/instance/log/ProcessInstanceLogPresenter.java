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
package org.jbpm.workbench.pr.client.editors.instance.log;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;

import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;
import org.jbpm.workbench.pr.client.util.LogUtils.LogOrder;
import org.jbpm.workbench.pr.client.util.LogUtils.LogType;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

import static java.util.stream.Collectors.toList;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.*;

@Dependent
public class ProcessInstanceLogPresenter {

    public static final LogTemplates LOG_TEMPLATES = GWT.create(LogTemplates.class);
    public static String NODE_HUMAN_TASK = "HumanTaskNode";
    public static String NODE_START = "StartNode";
    public static String NODE_END = "EndNode";

    private Constants constants = Constants.INSTANCE;
    private String processName;
    private String serverTemplateId;

    @Inject
    private ProcessInstanceLogView view;

    int pageSize = Integer.MAX_VALUE;
    int startRange = 0;

    protected DataSetQueryHelper dataSetQueryHelper;

    protected ProcessInstanceLogFilterSettingsManager filterSettingsManager;

    @Inject
    protected ErrorPopupPresenter errorPopup;

    @Inject
    public void setDataSetQueryHelper(final DataSetQueryHelper dataSetQueryHelper) {
        this.dataSetQueryHelper = dataSetQueryHelper;
    }

    @Inject
    public void setFilterSettingsManager(final ProcessInstanceLogFilterSettingsManager filterSettingsManager) {
        this.filterSettingsManager = filterSettingsManager;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public IsWidget getWidget() {
        return view;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    public void refreshProcessInstanceData(final LogOrder logOrder,
                                           final LogType logType) {
        try {
            final FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
            currentTableSettings.setServerTemplateId(this.serverTemplateId);
            currentTableSettings.setTablePageSize(pageSize);
            dataSetQueryHelper.setLastOrderedColumn(COLUMN_LOG_DATE);
            dataSetQueryHelper.setLastSortOrder(logOrder.equals(LogOrder.ASC) ? SortOrder.ASCENDING : SortOrder.DESCENDING);

            dataSetQueryHelper.setCurrentTableSettings(currentTableSettings);
            dataSetQueryHelper.setDataSetHandler(currentTableSettings);
            dataSetQueryHelper.lookupDataSet(
                    startRange,
                    new DataSetReadyCallback() {
                        @Override
                        public void callback(DataSet dataSet) {
                            if (dataSet != null && dataSetQueryHelper.getCurrentTableSettings().getKey().equals(currentTableSettings.getKey())) {
                                List<ProcessInstanceLogSummary> logs = new ArrayList<ProcessInstanceLogSummary>();
                                for (int i = 0; i < dataSet.getRowCount(); i++) {
                                    logs.add(new ProcessInstanceLogSummaryDataSetMapper().apply(dataSet,
                                                                                                i));
                                }
                                List<String> logsLines = logs.stream()
                                        .map(rls -> getLogLine(rls,
                                                               logType))
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .collect(toList());

                                view.setLogs(logsLines);
                            }
                        }

                        @Override
                        public void notFound() {
                            errorPopup.showMessage(org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.DataSetNotFound(currentTableSettings.getDataSet().getUUID()));
                        }

                        @Override
                        public boolean onError(ClientRuntimeError error) {
                            errorPopup.showMessage(org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.DataSetError(currentTableSettings.getDataSet().getUUID(),
                                                                                                                                   error.getMessage()));
                            return false;
                        }
                    });
        } catch (Exception e) {
            errorPopup.showMessage(org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.UnexpectedError(e.getMessage()));
        }
    }

    protected Optional<String> getLogLine(ProcessInstanceLogSummary logSummary,
                                          LogType logType) {
        if (LogType.TECHNICAL.equals(logType)) {
            String agent = constants.System();
            if ((NODE_HUMAN_TASK.equals(logSummary.getNodeType()) && logSummary.isCompleted()) ||
                    (NODE_START.equals(logSummary.getNodeType()) && !logSummary.isCompleted())) {
                agent = constants.Human();
            }

            return Optional.of(LOG_TEMPLATES.getTechLog(DateUtils.getDateTimeStr(logSummary.getDate()),
                                                        logSummary.getNodeType(),
                                                        SafeHtmlUtils.fromString(logSummary.getName()),
                                                        (logSummary.isCompleted() ? " " + constants.Completed() : ""),
                                                        agent).asString());
        } else {
            String prettyTime = DateUtils.getPrettyTime(logSummary.getDate());

            if (NODE_HUMAN_TASK.equals(logSummary.getNodeType())) {
                return Optional.of(LOG_TEMPLATES.getBusinessLog(prettyTime,
                                                                constants.Task(),
                                                                SafeHtmlUtils.fromString(logSummary.getName()),
                                                                (logSummary.isCompleted() ? constants.WasCompleted() : constants.WasStarted())).asString());
            } else if (NODE_START.equals(logSummary.getNodeType()) && !logSummary.isCompleted()) {
                return Optional.of(LOG_TEMPLATES.getBusinessLog(
                        prettyTime,
                        constants.Process(),
                        SafeHtmlUtils.fromString(getProcessName()),
                        constants.WasStarted()).asString());
            } else if (NODE_END.equals(logSummary.getNodeType()) && logSummary.isCompleted()) {
                return Optional.of(LOG_TEMPLATES.getBusinessLog(
                        prettyTime,
                        constants.Process(),
                        SafeHtmlUtils.fromString(getProcessName()),
                        constants.WasCompleted()).asString());
            }
        }
        return Optional.empty();
    }

    public void onProcessInstanceSelectionEvent(@Observes final ProcessInstanceSelectionEvent event) {
        setProcessName(event.getProcessDefName());
        setServerTemplateId(event.getServerTemplateId());

        view.setActiveLogOrderButton(LogOrder.ASC);
        view.setActiveLogTypeButton(LogType.BUSINESS);

        dataSetQueryHelper.setCurrentTableSettings(filterSettingsManager.createDefaultFilterSettingsPrototype(event.getProcessInstanceId()));
        refreshProcessInstanceData(LogOrder.ASC,
                                   LogType.BUSINESS);
    }

    public interface ProcessInstanceLogView extends IsWidget {

        void init(final ProcessInstanceLogPresenter presenter);

        void setActiveLogTypeButton(LogType logType);

        void setActiveLogOrderButton(LogOrder logOrder);

        void setLogs(List<String> logs);
    }

    public interface LogTemplates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("{0}: {1} '{2}' {3}")
        SafeHtml getBusinessLog(String time,
                                String logType,
                                SafeHtml logName,
                                String completed);

        @SafeHtmlTemplates.Template("{0}: {1} ({2}){3} - {4}")
        SafeHtml getTechLog(String time,
                            String logType,
                            SafeHtml logName,
                            String completed,
                            String agent);
    }
}