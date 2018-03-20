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

package org.jbpm.workbench.pr.client.editors.definition.details;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated
public class ProcessDefinitionDetailsTabViewImpl extends Composite implements
                                                                   ProcessDefinitionDetailsTabPresenter.AdvancedProcessDefDetailsView {

    @Inject
    @DataField
    public HTMLParagraphElement numberOfHumanTasksText;

    @Inject
    @DataField
    public HTMLParagraphElement humanTasksListBox;

    @Inject
    @DataField
    public HTMLParagraphElement usersGroupsListBox;

    @Inject
    @DataField
    public HTMLParagraphElement processDataListBox;

    @Inject
    @DataField
    public HTMLParagraphElement processServicesListBox;

    @Inject
    @DataField
    public HTMLParagraphElement subprocessListBox;

    @Inject
    @DataField
    public HTMLLabelElement nroOfHumanTasksLabel;

    @Inject
    @DataField
    public HTMLLabelElement humanTasksListLabel;

    @Inject
    @DataField
    public HTMLLabelElement usersGroupsListLabel;

    @Inject
    @DataField
    public HTMLLabelElement subprocessListLabel;

    @Inject
    @DataField
    public HTMLLabelElement processDataListLabel;

    @Inject
    @DataField
    public HTMLLabelElement processServicesListLabel;

    @Inject
    @DataField
    protected HTMLParagraphElement processIdText;

    @Inject
    @DataField
    protected HTMLParagraphElement processNameText;

    @Inject
    @DataField
    protected HTMLParagraphElement deploymentIdText;

    @Inject
    @DataField
    protected HTMLLabelElement processNameLabel;

    @Inject
    @DataField
    protected HTMLLabelElement processIdLabel;

    @Inject
    @DataField
    protected HTMLLabelElement deploymentIdLabel;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = Constants.INSTANCE;

    @PostConstruct
    public void init() {
        processIdLabel.textContent = constants.Process_Definition_Id();
        processNameLabel.textContent = constants.Process_Definition_Name();
        nroOfHumanTasksLabel.textContent = constants.Human_Tasks_Count();
        deploymentIdLabel.textContent = constants.Deployment_Name();
        humanTasksListLabel.textContent = constants.Human_Tasks();
        usersGroupsListLabel.textContent = constants.User_And_Groups();
        subprocessListLabel.textContent = constants.SubProcesses();
        processDataListLabel.textContent = constants.Process_Variables();
        processServicesListLabel.textContent = constants.Services();
    }

    @Override
    public void setNumberOfHumanTasksText(final String text) {
        numberOfHumanTasksText.textContent = text;
    }

    @Override
    public void setHumanTasksListBox(final String text) {
        humanTasksListBox.innerHTML = text;
    }

    @Override
    public void setUsersGroupsListBox(final String text) {
        usersGroupsListBox.innerHTML = text;
    }

    @Override
    public void setProcessDataListBox(final String text) {
        processDataListBox.innerHTML = text;
    }

    @Override
    public void setProcessServicesListBox(final String text) {
        processServicesListBox.innerHTML = text;
    }

    @Override
    public void setSubProcessListBox(final String text) {
        subprocessListBox.innerHTML = text;
    }

    @Override
    public void displayNotification(final String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public void setProcessNameText(final String text) {
        this.processNameText.textContent = text;
    }

    @Override
    public void setDeploymentIdText(final String text) {
        this.deploymentIdText.textContent = text;
    }

    @Override
    public void setProcessIdText(final String text) {
        this.processIdText.textContent = text;
    }
}
