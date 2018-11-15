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

import com.google.gwt.user.client.TakesValue;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.AbstractView;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskEventSummary;
import org.jbpm.workbench.ht.util.TaskEventType;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class TaskLogItemView extends AbstractView<TaskLogsPresenter> implements TakesValue<TaskEventSummary>,
                                                                                IsElement {

    Constants constants = Constants.INSTANCE;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField("list-group-item")
    Div listGroupItem;

    @Inject
    @DataField("logIcon")
    Span logIcon;

    @Inject
    @DataField("logTime")
    Span logTime;

    @Inject
    @DataField("logTypeDesc")
    Span logTypeDesc;

    @Inject
    @DataField("logInfo")
    Span logInfo;

    @Inject
    @AutoBound
    private DataBinder<TaskEventSummary> logSummary;

    @PostConstruct
    public void init() {
        tooltip(logIcon);
    }

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    @Override
    public TaskEventSummary getValue() {
        return this.logSummary.getModel();
    }

    @Override
    public void setValue(final TaskEventSummary model) {
        String iconClass = "list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm";
        this.logSummary.setModel(model);
        tooltip(logTime);
        logTime.setTextContent(DateUtils.getPrettyTime(model.getLogTime()));
        logTime.setAttribute("data-original-title",
                             DateUtils.getDateTimeStr(model.getLogTime()));
        String logInfoContent = constants.ByUser() + " " + model.getUserId() + " ";
        TaskEventType type = TaskEventType.valueOf(model.getType());
        switch (type) {
            case ADDED: {
                if (model.getUserId() != null && !model.getUserId().isEmpty()) {
                    logInfoContent = constants.ByProcess() + " '" + model.getUserId() + "' ";
                } else {
                    logInfoContent = "";
                }
                iconClass += " fa fa-cogs";
                break;
            }
            case ACTIVATED:
            case CREATED:
            case FORWARDED:
            case RESUMED:
            case DELEGATED:
            case NOMINATED: {
                iconClass += " fa fa-cogs";
                break;
            }
            case STOPPED:
            case EXITED:
            case FAILED:
            case SKIPPED:
            case SUSPENDED: {
                iconClass += " fa fa-cogs";
                iconClass += " kie-timeline-icon--completed";
                break;
            }
            case UPDATED: {
                iconClass += " fa fa-user";
                logInfoContent += " (" + model.getMessage() + " ) ";
                break;
            }
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
        tooltip(logIcon);
        String logString = constants.Task() + " " + translationService.format(type.getTypeTranslationId()).toLowerCase();
        logIcon.setAttribute("data-original-title",
                             logString);
        logIcon.setClassName(iconClass);
        logTypeDesc.setTextContent(logString);

        logInfo.setTextContent(logInfoContent);
    }
}
