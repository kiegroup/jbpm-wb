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

package org.jbpm.workbench.pr.client.editors.instance.details;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.client.i18n.Constants;

@Dependent
@Templated(value = "ProcessInstanceDetailsViewImpl.html")
public class ProcessInstanceDetailsViewImpl extends Composite implements
                                                              ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView {

    @Inject
    @DataField
    public HTML processDefinitionIdText;

    @Inject
    @DataField
    public HTML processDeploymentText;

    @Inject
    @DataField
    public HTML processVersionText;

    @Inject
    @DataField
    public HTML correlationKeyText;

    @Inject
    @DataField
    public FormLabel parentProcessInstanceIdLabel;

    @Inject
    @DataField
    public HTML parentProcessInstanceIdText;

    @Inject
    @DataField
    public HTML stateText;

    @Inject
    @DataField
    public HTML currentActivitiesListBox;

    @Inject
    @DataField
    public FormLabel processDefinitionIdLabel;

    @Inject
    @DataField
    public FormLabel processDeploymentLabel;

    @Inject
    @DataField
    public FormLabel processVersionLabel;

    @Inject
    @DataField
    public FormLabel correlationKeyLabel;

    @Inject
    @DataField
    public FormLabel stateLabel;

    @Inject
    @DataField
    public FormLabel currentActivitiesListLabel;

    @Inject
    @DataField
    public HTML activeTasksListBox;

    @Inject
    @DataField
    public FormLabel activeTasksListLabel;

    private Constants constants = Constants.INSTANCE;

    @PostConstruct
    public void init(  ) {
        processDefinitionIdLabel.setText( constants.Process_Definition_Id() );
        processDeploymentLabel.setText( constants.Deployment_Name() );
        processVersionLabel.setText( constants.Process_Definition_Version() );
        correlationKeyLabel.setText( constants.Correlation_Key() );
        stateLabel.setText( constants.Process_Instance_State() );
        activeTasksListLabel.setText( constants.Active_Tasks() );
        currentActivitiesListLabel.setText( constants.Current_Activities() );
        parentProcessInstanceIdLabel.setText(constants.Parent_Process_Instance());
    }


    @Override
    public HTML getProcessDefinitionIdText() {
        return processDefinitionIdText;
    }

    @Override
    public HTML getCurrentActivitiesListBox() {
        return currentActivitiesListBox;
    }

    @Override
    public HTML getActiveTasksListBox() {
        return activeTasksListBox;
    }

    @Override
    public HTML getStateText() {
        return this.stateText;
    }

    @Override
    public HTML getProcessDeploymentText() {
        return processDeploymentText;
    }

    @Override
    public HTML getCorrelationKeyText() {
        return correlationKeyText;
    }

    @Override
    public HTML getParentProcessInstanceIdText() {
        return parentProcessInstanceIdText;
    }

    @Override
    public HTML getProcessVersionText() {
        return processVersionText;
    }

}