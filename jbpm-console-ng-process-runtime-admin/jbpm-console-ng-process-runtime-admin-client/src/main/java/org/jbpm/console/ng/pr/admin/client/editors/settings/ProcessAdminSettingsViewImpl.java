/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.pr.admin.client.editors.settings;

import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.admin.client.i18n.ProcessAdminConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "ProcessAdminSettingsViewImpl.html")
public class ProcessAdminSettingsViewImpl extends Composite implements ProcessAdminSettingsPresenter.ProcessAdminSettingsView {

    @Inject
    @DataField
    public Button generateMockInstancesButton;

    @Inject
    @DataField
    public Button resetButton;

    @Inject
    @DataField
    public FormLabel processListLabel;

    @Inject
    @DataField
    public FormLabel amountOfTasksLabel;

    @Inject
    @DataField
    public TextBox amountOfTasksText;

    @Inject
    @DataField
    public Select serverTemplate;

    @Inject
    @DataField
    public FormLabel serverTemplateLabel;

    @Inject
    @DataField
    public Select processList;

    @Inject
    private PlaceManager placeManager;

    private ProcessAdminSettingsPresenter presenter;

    @Inject
    private Event<NotificationEvent> notification;

    private ProcessAdminConstants constants = ProcessAdminConstants.INSTANCE;

    @Override
    public void init(final ProcessAdminSettingsPresenter presenter) {
        this.presenter = presenter;

        serverTemplateLabel.setText(constants.ServerTemplate());
        serverTemplateLabel.setShowRequiredIndicator(true);

        processListLabel.setText(constants.ProcessId());
        processListLabel.setShowRequiredIndicator(true);

        amountOfTasksLabel.setText(constants.Amount_Of_Tasks());
        amountOfTasksLabel.setShowRequiredIndicator(true);
        amountOfTasksText.setText("1");

        generateMockInstancesButton.setText(constants.Generate_Mock_Instances());
        resetButton.setText(constants.Reset());

        serverTemplate.addValueChangeHandler(e -> presenter.onServerTemplateSelected(serverTemplate.getValue()));
    }

    @EventHandler("resetButton")
    public void resetClick(ClickEvent e) {
        serverTemplate.setValue("");
        amountOfTasksText.setText("1");
        clearProcessList();
    }

    @EventHandler("generateMockInstancesButton")
    public void generateMockInstancesButton(ClickEvent e) {
        presenter.generateMockInstances(serverTemplate.getValue(), processList.getValue(), Integer.parseInt(amountOfTasksText.getText()));
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public void addServerTemplates(final Set<String> serverTemplateIds) {
        serverTemplate.clear();
        serverTemplate.setValue("");

        for (final String serverTemplateId : serverTemplateIds) {
            final Option option = new Option();
            option.setText(serverTemplateId);
            option.setValue(serverTemplateId);
            serverTemplate.add(option);
        }

        serverTemplate.refresh();
    }

    @Override
    public void clearProcessList() {
        processList.clear();
        processList.setValue("");
        processList.refresh();
    }

    @Override
    public void addProcessList(final Set<String> processIds) {
        clearProcessList();

        for (final String processId : processIds) {
            final Option option = new Option();
            option.setText(processId);
            option.setValue(processId);
            processList.add(option);
        }

        processList.refresh();
    }
}