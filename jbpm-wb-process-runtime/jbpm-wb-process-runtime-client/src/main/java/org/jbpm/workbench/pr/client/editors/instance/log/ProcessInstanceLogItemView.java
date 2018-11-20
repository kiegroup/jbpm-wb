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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.workbench.common.client.util.AbstractView;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.LogUtils;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;

@Dependent
@Templated
public class ProcessInstanceLogItemView extends AbstractView<ProcessInstanceLogPresenter>
        implements TakesValue<ProcessInstanceLogSummary>,
                   IsElement {

    private Constants constants = Constants.INSTANCE;

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
    @DataField("nodeTypeDesc")
    Span nodeTypeDesc;

    @Inject
    @DataField("logCompleted")
    Span logCompleted;

    @Inject
    @DataField("detailsPanelDiv")
    Div detailsPanelDiv;

    @Inject
    @DataField("detailsLink")
    Anchor detailsLink;

    @Inject
    @DataField("detailsInfoDiv")
    Div detailsInfoDiv;

    @Inject
    @AutoBound
    private DataBinder<ProcessInstanceLogSummary> logSummary;

    @Inject
    ProcessInstanceLogItemDetailsView workItemView;

    @PostConstruct
    public void init() {
        tooltip(logIcon);
    }

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    @Override
    public ProcessInstanceLogSummary getValue() {
        return this.logSummary.getModel();
    }

    @Override
    public void setValue(final ProcessInstanceLogSummary model) {
        String iconClass = "list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm";
        this.logSummary.setModel(model);
        logTime.setTextContent(DateUtils.getPrettyTime(model.getDate()));

        String agent = constants.System();

        if (LogUtils.NODE_TYPE_HUMAN_TASK.equals(model.getNodeType()) ||
                (LogUtils.NODE_TYPE_START.equals(model.getNodeType()) && !model.isCompleted())) {
            iconClass += " fa fa-user";
            agent = constants.Human();
            tooltip(logIcon);
            logIcon.setAttribute("data-original-title",
                                 constants.Human_Task());
        } else {
            iconClass += " fa fa-cogs";
            logIcon.setAttribute("data-original-title",
                                 constants.System_Task());
        }
        if (model.isCompleted()) {
            iconClass += " kie-timeline-icon--completed";
            logCompleted.setTextContent(constants.NodeWasLeft(agent));
        } else {
            logCompleted.setTextContent(constants.NodeWasEntered(agent));
        }
        logIcon.setClassName(iconClass);
        nodeTypeDesc.setTextContent(getLogTitle(model));

        if (model.getWorkItemId() == null) {
            detailsPanelDiv.setHidden(true);
        } else {
            setDetailsPanelAttributes(model);
            detailsPanelDiv.setHidden(false);
        }
    }

    private String getLogTitle(ProcessInstanceLogSummary logsum) {
        if (LogUtils.NODE_TYPE_HUMAN_TASK.equals(logsum.getNodeType())) {
            return constants.Task_(logsum.getName());
        }
        String name = logsum.getName();
        if (name != null && name.trim().length() > 0) {
            return logsum.getNodeType() + " '" + name + "' ";
        }
        return logsum.getNodeType();
    }

    private void setDetailsPanelAttributes(ProcessInstanceLogSummary model) {
        String panelId = model.getId().toString() + model.getDate().getTime();
        detailsLink.setAttribute("href",
                                 "#" + panelId);
        detailsInfoDiv.setId(panelId);
    }

    @EventHandler("detailsLink")
    public void loadProcessInstanceLogsDetails(final @ForEvent("click") MouseEvent event) {
        if (!detailsInfoDiv.hasChildNodes() && (logSummary.getModel().getWorkItemId() != null)) {
            if (LogUtils.NODE_TYPE_HUMAN_TASK.equals(logSummary.getModel().getNodeType())) {
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
}
