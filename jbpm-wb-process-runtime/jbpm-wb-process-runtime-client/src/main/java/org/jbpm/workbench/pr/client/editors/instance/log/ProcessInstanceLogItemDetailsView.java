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

import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.TaskStatusConverter;
import org.jbpm.workbench.pr.model.WorkItemParameterSummary;
import org.jbpm.workbench.pr.model.WorkItemSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Dependent
@Templated
public class ProcessInstanceLogItemDetailsView implements IsElement {

    private Constants constants = Constants.INSTANCE;

    @Inject
    @DataField("nodeDetailsContainer")
    Div nodeDetailsContainer;

    @Inject
    @DataField("humanTaskContainer")
    Div humanTaskContainer;

    @Inject
    @DataField("workItemDetailsContainer")
    Div workItemDetailsContainer;

    @Inject
    @DataField("createdOn")
    Span createdOn;

    @Inject
    @DataField("createdByLabel")
    Span createdByLabel;

    @Inject
    @DataField("createdBy")
    Span createdBy;

    @Inject
    @DataField("updatedOn")
    Span updatedOn;

    @Inject
    @DataField("taskId")
    Span taskId;

    @Inject
    @DataField("taskStatus")
    Span taskStatus;

    @Inject
    @DataField("actualOwner")
    Span actualOwner;

    @Inject
    @DataField("description")
    Span description;

    @Inject
    @DataField("workItemDetails")
    Div workItemDetails;

    @Inject
    @DataField("empty-list-item")
    private Div emptyContainer;

    @Inject
    @Bound
    @ListContainer("dl")
    @DataField("params")
    @SuppressWarnings("unused")
    private ListComponent<WorkItemParameterSummary, WorkItemParameterListViewImpl> workItemParameters;

    @Inject
    @AutoBound
    private DataBinder<List<WorkItemParameterSummary>> workItemParameterList;

    @Override
    public HTMLElement getElement() {
        return nodeDetailsContainer;
    }

    public void setTaskDetailsData(final TaskSummary model,
                                   Date logDate) {
        removeCSSClass(humanTaskContainer,
                       "hidden");
        taskId.setTextContent(model.getId().toString());
        createdOn.setTextContent(DateUtils.getDateTimeStr(model.getCreatedOn()));
        if (model.getCreatedBy() != null && !model.getCreatedBy().isEmpty()) {
            createdBy.setTextContent(model.getCreatedBy());
        } else {
            createdByLabel.setHidden(true);
            createdBy.setHidden(true);
        }
        updatedOn.setTextContent(DateUtils.getDateTimeStr(logDate));
        taskStatus.setTextContent(new TaskStatusConverter().toWidgetValue(model.getTaskStatus()));
        if (model.getActualOwner() != null && !model.getActualOwner().isEmpty()) {
            actualOwner.setTextContent(model.getActualOwner());
        } else {
            actualOwner.setTextContent(constants.NotClaimed());
        }
        description.setTextContent(model.getDescription());
    }

    public void setDetailsData(final WorkItemSummary model) {
        if (model != null &&
                model.getParameters() != null &&
                !model.getParameters().isEmpty()) {
            workItemParameterList.setModel(model.getParameters());
            addCSSClass(emptyContainer,
                        "hidden");
            removeCSSClass(workItemDetails,
                           "hidden");
        } else {
            removeCSSClass(emptyContainer,
                           "hidden");
            addCSSClass(workItemDetails,
                        "hidden");
        }
    }
}
