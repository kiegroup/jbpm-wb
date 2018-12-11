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
package org.jbpm.workbench.ht.client.editors.tasklogs;

import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.AbstractLogItemView;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskEventSummary;
import org.jbpm.workbench.ht.util.TaskEventType;

@Dependent
@Templated
public class TaskLogItemView extends AbstractLogItemView<TaskLogsPresenter> implements TakesValue<TaskEventSummary>,
                                                                                       IsElement {

    private Constants constants = Constants.INSTANCE;

    @Inject
    private TranslationService translationService;

    @Inject
    @AutoBound
    private DataBinder<TaskEventSummary> logSummary;

    @Override
    public TaskEventSummary getValue() {
        return logSummary.getModel();
    }

    @Override
    public void setValue(final TaskEventSummary model) {
        logSummary.setModel(model);

        final TaskEventType type = TaskEventType.valueOf(model.getType());
        final String logString = constants.Task() + " " + translationService.format(type.getTypeTranslationId()).toLowerCase();
        final Date modelLogTime = model.getLogTime();

        tooltip(logTime);
        logTime.setAttribute("data-original-title", DateUtils.getDateTimeStr(modelLogTime));
        logTime.setTextContent(DateUtils.getPrettyTime(modelLogTime));

        tooltip(logIcon);
        logIcon.setAttribute("data-original-title", logString);
        logIcon.setClassName(getIconClass(type));

        logInfo.setTextContent(getLogInfoContent(type, model));
        logTypeDesc.setTextContent(logString);
    }

    private String getIconClass(final TaskEventType type) {
        String iconClass = "list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm";
        switch (type) {
            case STOPPED:
            case EXITED:
            case FAILED:
            case SKIPPED:
            case SUSPENDED: {
                iconClass += " fa fa-cogs";
                iconClass += " kie-timeline-icon--completed";
                break;
            }
            case UPDATED:
            case CLAIMED:
            case STARTED: {
                iconClass += " fa fa-user";
                break;
            }
            case COMPLETED:
            case RELEASED: {
                iconClass += " fa fa-user";
                iconClass += " kie-timeline-icon--completed";
                break;
            }
            default: {
                iconClass += " fa fa-cogs";
            }
        }
        return iconClass;
    }

    private String getLogInfoContent(final TaskEventType type, final TaskEventSummary model) {
        String logInfoContent = constants.ByUser() + " " + model.getUserId() + " ";
        switch (type) {
            case ADDED: {
                if (model.getUserId() != null && !model.getUserId().isEmpty()) {
                    logInfoContent = constants.ByProcess() + " '" + model.getUserId() + "' ";
                } else {
                    logInfoContent = "";
                }
                break;
            }
            case UPDATED: {
                logInfoContent += " (" + model.getMessage() + " ) ";
                break;
            }
        }
        return logInfoContent;
    }
}
