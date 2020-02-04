/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.instance.diagram;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.TakesValue;
import org.gwtbootstrap3.client.ui.Anchor;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;

@Dependent
@Templated(stylesheet = "ProcessInstanceDiagram.css")
public class SubProcessInstanceItemView implements TakesValue<ProcessInstanceSummary>, IsElement {

    @Inject
    @DataField("list-group-item")
    private ListItem listItem;

    @Inject
    @DataField("sub-process-name-text")
    public Anchor subProcessNameAnchor;

    @Inject
    @AutoBound
    private DataBinder<ProcessInstanceSummary> processInstanceSummary;

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceEvent;

    private Constants constants = Constants.INSTANCE;

    private ProcessInstanceKey processInstanceKey;

    @Override
    public void setValue(ProcessInstanceSummary value) {
        subProcessNameAnchor.setText(constants.ProcessInstanceIdAndName(value.getProcessInstanceId().toString(), value.getProcessName()));
        processInstanceKey = new ProcessInstanceKey(value.getServerTemplateId(), value.getDeploymentId(), value.getProcessInstanceId());
    }

    @Override
    public ProcessInstanceSummary getValue() {
        return processInstanceSummary.getModel();
    }

    @Override
    public HTMLElement getElement() {
        return listItem;
    }

    @EventHandler("sub-process-name-text")
    protected void onClickSubProcessInstanceId(final ClickEvent event) {
        processInstanceEvent.fire(new ProcessInstanceSelectionEvent(processInstanceKey, false, true));
    }
}
