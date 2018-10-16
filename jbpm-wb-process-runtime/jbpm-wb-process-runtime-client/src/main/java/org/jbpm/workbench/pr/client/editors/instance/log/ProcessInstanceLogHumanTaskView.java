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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.TaskStatusConverter;

@Dependent
@Templated
public class ProcessInstanceLogHumanTaskView implements IsElement {

    private Constants constants = Constants.INSTANCE;

    @Inject
    @DataField("humanTaskContainer")
    Div humanTaskContainer;

    @Inject
    @DataField("createdOn")
    Span createdOn;

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

    @Override
    public HTMLElement getElement() {
        return humanTaskContainer;
    }

    public void setDetailsData(final TaskSummary model,
                               Date logDate) {
        taskId.setTextContent(model.getId().toString());
        createdOn.setTextContent(DateUtils.getDateTimeStr(model.getCreatedOn()));
        createdBy.setTextContent(model.getCreatedBy());
        updatedOn.setTextContent(DateUtils.getDateTimeStr(logDate));
        taskId.setTextContent(model.getId().toString());
        taskStatus.setTextContent(new TaskStatusConverter().toWidgetValue(model.getTaskStatus()));
        if (model.getActualOwner().isEmpty()) {
            actualOwner.setTextContent(constants.NotClaimed());
        } else {
            actualOwner.setTextContent(model.getActualOwner());
        }
        description.setTextContent(model.getDescription());
    }
}
