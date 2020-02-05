/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.instance.details;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLParagraphElement;
import org.gwtbootstrap3.client.ui.Anchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;

@Dependent
@Templated
public class ProcessInstanceDetailsTabViewImpl extends Composite implements ProcessInstanceDetailsTabPresenter.ProcessInstanceDetailsTabView {

    @Inject
    @DataField
    public HTMLParagraphElement processDefinitionIdText;

    @Inject
    @DataField
    public HTMLParagraphElement processDeploymentText;

    @Inject
    @DataField
    public HTMLParagraphElement processVersionText;

    @Inject
    @DataField
    public HTMLParagraphElement slaComplianceText;

    @Inject
    @DataField
    public HTMLParagraphElement correlationKeyText;

    @Inject
    @DataField
    public HTMLLabelElement parentProcessInstanceIdLabel;

    @Inject
    @DataField
    public HTMLParagraphElement stateText;

    @Inject
    @DataField
    public HTMLParagraphElement currentActivitiesListBox;

    @Inject
    @DataField
    public HTMLLabelElement processDefinitionIdLabel;

    @Inject
    @DataField
    public HTMLLabelElement processDeploymentLabel;

    @Inject
    @DataField
    public HTMLLabelElement processVersionLabel;

    @Inject
    @DataField
    public HTMLLabelElement slaComplianceLabel;

    @Inject
    @DataField
    public HTMLLabelElement correlationKeyLabel;

    @Inject
    @DataField
    public HTMLLabelElement stateLabel;

    @Inject
    @DataField
    public HTMLLabelElement currentActivitiesListLabel;

    @Inject
    @DataField
    public HTMLParagraphElement activeTasksListBox;

    @Inject
    @DataField
    public HTMLLabelElement activeTasksListLabel;

    @Inject
    @DataField("parent-process-instanceId")
    private Anchor parentAnchor;

    private Constants constants = Constants.INSTANCE;

    private Command callback;

    @PostConstruct
    public void init() {
        processDefinitionIdLabel.textContent = constants.Process_Definition_Id();
        processDeploymentLabel.textContent = constants.Deployment_Name();
        processVersionLabel.textContent = constants.Process_Definition_Version();
        slaComplianceLabel.textContent = constants.SlaCompliance();
        correlationKeyLabel.textContent = constants.Correlation_Key();
        stateLabel.textContent = constants.Process_Instance_State();
        activeTasksListLabel.textContent = constants.Active_Tasks();
        currentActivitiesListLabel.textContent = constants.Current_Activities();
        parentProcessInstanceIdLabel.textContent = constants.Parent_Process_Instance();
    }

    @Override
    public void setProcessInstanceDetailsCallback(Command callback) {
        this.callback = callback;
    }

    @Override
    public void setProcessDefinitionIdText(final String value) {
        processDefinitionIdText.textContent = value;
    }

    @Override
    public void setCurrentActivitiesListBox(final String value) {
        currentActivitiesListBox.innerHTML = value;
    }

    @Override
    public void setActiveTasksListBox(final String value) {
        activeTasksListBox.innerHTML = value;
    }

    @Override
    public void setStateText(final String value) {
        stateText.textContent = value;
    }

    @Override
    public void setProcessDeploymentText(final String value) {
        processDeploymentText.textContent = value;
    }

    @Override
    public void setCorrelationKeyText(final String value) {
        correlationKeyText.textContent = value;
    }

    @Override
    public void setParentProcessInstanceIdText(final String value, final boolean hasParentProcessInstanceId) {
        if (!hasParentProcessInstanceId) {
            parentAnchor.setEnabled(false);
        } else {
            parentAnchor.setEnabled(true);
        }
        parentAnchor.setText(value);
    }

    @Override
    public void setProcessVersionText(final String value) {
        processVersionText.textContent = value;
    }

    @Override
    public void setSlaComplianceText(final String value) {
        slaComplianceText.textContent = value;
    }

    @EventHandler("parent-process-instanceId")
    protected void onClickParentInstanceId(final ClickEvent event) {
        if (callback != null) {
            callback.execute();
        }
    }
}
