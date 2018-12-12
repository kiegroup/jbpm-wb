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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.AbstractLogItemView;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.LogUtils;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;

@Dependent
@Templated
public class ProcessInstanceLogItemView extends AbstractLogItemView<ProcessInstanceLogPresenter>
        implements TakesValue<ProcessInstanceLogSummary>,
                   IsElement {

    private Constants constants = Constants.INSTANCE;

    @Inject
    @DataField("detailsPanelDiv")
    private Div detailsPanelDiv;

    @Inject
    @DataField("detailsLink")
    private Anchor detailsLink;

    @Inject
    @DataField("detailsInfoDiv")
    private Div detailsInfoDiv;

    @Inject
    @AutoBound
    private DataBinder<ProcessInstanceLogSummary> logSummary;

    @Inject
    private ProcessInstanceLogItemDetailsView workItemView;

    @Override
    public ProcessInstanceLogSummary getValue() {
        return logSummary.getModel();
    }

    @Override
    public void setValue(final ProcessInstanceLogSummary model) {
        logSummary.setModel(model);

        setLogTime(model.getDate());
        setLogIcon(model);
        setLogInfo(model);
        setLogType(model);
        setDetails(model);
    }

    private void setLogIcon(ProcessInstanceLogSummary model) {
        tooltip(logIcon);
        String iconClass = "list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm";
        if (isHumanTask(model) || (isStartNode(model) && !model.isCompleted())) {
            iconClass += " fa fa-user";
            logIcon.setAttribute("data-original-title", constants.Human_Task());
        } else {
            iconClass += " fa fa-cogs";
            logIcon.setAttribute("data-original-title", constants.System_Task());
        }
        if (model.isCompleted()) {
            iconClass += " kie-timeline-icon--completed";
        }
        logIcon.setClassName(iconClass);
    }

    private void setLogInfo(ProcessInstanceLogSummary model) {
        String agent = getAgent(model);
        if (model.isCompleted()) {
            logInfo.setTextContent(constants.NodeWasLeft(agent));
        } else {
            logInfo.setTextContent(constants.NodeWasEntered(agent));
        }
    }

    private void setLogType(ProcessInstanceLogSummary model) {
        String logTitle = model.getNodeType();
        String name = model.getName();
        if (isHumanTask(model)) {
            logTitle = constants.Task_(model.getName());
        } else if (name != null && name.trim().length() > 0) {
            logTitle = model.getNodeType() + " '" + name + "' ";
        }
        logTypeDesc.setTextContent(logTitle);
    }

    private void setDetails(ProcessInstanceLogSummary model) {
        if (model.getWorkItemId() == null) {
            detailsPanelDiv.setHidden(true);
        } else {
            String panelId = Document.get().createUniqueId();
            detailsLink.setAttribute("href",
                                     "#" + panelId);
            detailsInfoDiv.setId(panelId);
            detailsPanelDiv.setHidden(false);
        }
    }

    private String getAgent(final ProcessInstanceLogSummary model) {
        if (isHumanTask(model) || (isStartNode(model) && !model.isCompleted())) {
            return constants.Human();
        } else {
            return constants.System();
        }
    }

    @EventHandler("detailsLink")
    public void loadProcessInstanceLogsDetails(final @ForEvent("click") MouseEvent event) {
        if (!detailsInfoDiv.hasChildNodes() && (logSummary.getModel().getWorkItemId() != null)) {
            if (isHumanTask(logSummary.getModel())) {
                presenter.loadTaskDetails(logSummary.getModel().getWorkItemId(),
                                          logSummary.getModel().getDate(),
                                          workItemView);
            } else {
                presenter.loadWorkItemDetails(logSummary.getModel().getWorkItemId(),
                                              workItemView);
            }
            detailsInfoDiv.appendChild(workItemView.getElement());
        }
    }

    private boolean isHumanTask(ProcessInstanceLogSummary model) {
        return LogUtils.NODE_TYPE_HUMAN_TASK.equals(model.getNodeType());
    }

    private boolean isStartNode(ProcessInstanceLogSummary model) {
        return LogUtils.NODE_TYPE_START.equals(model.getNodeType());
    }
}
