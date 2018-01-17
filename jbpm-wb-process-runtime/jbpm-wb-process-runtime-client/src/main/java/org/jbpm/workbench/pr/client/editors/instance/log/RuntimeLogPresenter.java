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
package org.jbpm.workbench.pr.client.editors.instance.log;

import java.util.Collections;
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
import org.jboss.errai.common.client.api.Caller;

import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.RuntimeLogSummary;
import org.jbpm.workbench.pr.client.util.LogUtils.LogOrder;
import org.jbpm.workbench.pr.client.util.LogUtils.LogType;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;

import static java.util.stream.Collectors.toList;

@Dependent
public class RuntimeLogPresenter {

    public static final LogTemplates LOG_TEMPLATES = GWT.create(LogTemplates.class);
    public static String NODE_HUMAN_TASK = "HumanTaskNode";
    public static String NODE_START = "StartNode";
    public static String NODE_END = "EndNode";

    private Constants constants = Constants.INSTANCE;
    private Long processInstanceId;
    private String processName;
    private String serverTemplateId;
    private String deploymentId;
    @Inject
    private RuntimeLogView view;
    @Inject
    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public IsWidget getWidget() {
        return view;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
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

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public void refreshProcessInstanceData(final LogOrder logOrder,
                                           final LogType logType) {
        processRuntimeDataService.call((List<RuntimeLogSummary> logs) -> {
            if (logOrder == LogOrder.ASC) {
                Collections.reverse(logs);
            }

            List<String> logsLine = logs.stream()
                    .map(rls -> getLogLine(rls,
                                           logType))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toList());

            view.setLogs(logsLine);
        }).getProcessInstanceLogs(serverTemplateId,
                                  deploymentId,
                                  processInstanceId);
    }

    protected Optional<String> getLogLine(RuntimeLogSummary logSummary,
                                          LogType logType) {
        if (LogType.TECHNICAL.equals(logType)) {
            String agent = constants.System();
            if ((NODE_HUMAN_TASK.equals(logSummary.getNodeType()) && logSummary.isCompleted()) ||
                    (NODE_START.equals(logSummary.getNodeType()) && !logSummary.isCompleted())) {
                agent = constants.Human();
            }

            return Optional.of(LOG_TEMPLATES.getTechLog(DateUtils.getDateTimeStr(logSummary.getDate()),
                                                        logSummary.getNodeType(),
                                                        SafeHtmlUtils.fromString(logSummary.getNodeName()),
                                                        (logSummary.isCompleted() ? " " + constants.Completed() : ""),
                                                        agent).asString());
        } else {
            String prettyTime = DateUtils.getPrettyTime(logSummary.getDate());

            if (NODE_HUMAN_TASK.equals(logSummary.getNodeType())) {
                return Optional.of(LOG_TEMPLATES.getBusinessLog(prettyTime,
                                                                constants.Task(),
                                                                SafeHtmlUtils.fromString(logSummary.getNodeName()),
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
        setProcessInstanceId(event.getProcessInstanceId());
        setProcessName(event.getProcessDefName());
        setServerTemplateId(event.getServerTemplateId());
        setDeploymentId(event.getDeploymentId());

        view.setActiveLogOrderButton(LogOrder.ASC);
        view.setActiveLogTypeButton(LogType.BUSINESS);
        refreshProcessInstanceData(LogOrder.ASC,
                                   LogType.BUSINESS);
    }

    @Inject
    public void setProcessRuntimeDataService(final Caller<ProcessRuntimeDataService> processRuntimeDataService) {
        this.processRuntimeDataService = processRuntimeDataService;
    }

    public interface RuntimeLogView extends IsWidget {

        void init(final RuntimeLogPresenter presenter);

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