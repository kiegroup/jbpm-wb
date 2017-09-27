/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.client.screens;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.client.i18n.ProcessAdminConstants;
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
    public FormLabel correlationKeyLabel;

    @Inject
    @DataField
    public TextBox correlationKeyText;

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
    @Bound
    @ListContainer("tbody")
    @DataField("variables")
    @SuppressWarnings("unused")
    private ListComponent<ProcessVariableSummary, ProcessVariableSummaryViewImpl> processVariables;

    @Inject
    @AutoBound
    private DataBinder<List<ProcessVariableSummary>> processVariablesList;

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

        correlationKeyLabel.setText(constants.Correlation_Key());

        generateMockInstancesButton.setText(constants.Generate_Mock_Instances());
        resetButton.setText(constants.Reset());

        serverTemplate.addValueChangeHandler(e -> presenter.onServerTemplateSelected(serverTemplate.getValue()));

        processList.addValueChangeHandler(e -> presenter.onProcessSelected(serverTemplate.getValue(),
                                                                           processList.getValue()));
    }

    @EventHandler("resetButton")
    public void resetClick(ClickEvent e) {
        serverTemplate.setValue("");
        amountOfTasksText.setText("1");
        correlationKeyText.setText("");
        clearProcessList();
    }

    @EventHandler("generateMockInstancesButton")
    public void generateMockInstancesButton(ClickEvent e) {
        presenter.generateMockInstances(serverTemplate.getValue(),
                                        processList.getValue(),
                                        Integer.parseInt(amountOfTasksText.getText()),
                                        correlationKeyText.getText(),
                                        processVariablesList.getModel());
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public void addServerTemplates(final Set<String> serverTemplateIds) {
        serverTemplate.clear();

        for (final String serverTemplateId : serverTemplateIds) {
            final Option option = GWT.create(Option.class);
            option.setText(serverTemplateId);
            option.setValue(serverTemplateId);
            serverTemplate.add(option);
        }

        Scheduler.get().scheduleDeferred(() -> {
            serverTemplate.refresh();
            serverTemplate.setValue("");
            serverTemplateIds.stream().findFirst().ifPresent(serverTemplateId -> serverTemplate.setValue(serverTemplateId, true));
        });
    }

    @Override
    public void addProcessVariables(final List<ProcessVariableSummary> variables) {
        processVariablesList.setModel(variables);
    }

    @Override
    public void clearProcessList() {
        processList.clear();
        processList.setValue("");
        processList.refresh();
        processVariablesList.setModel(Collections.emptyList());
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


        Scheduler.get().scheduleDeferred(() -> {
            processList.refresh();
            processList.setValue("");
            processIds.stream().findFirst().ifPresent(processId -> processList.setValue(processId, true));
        });
    }
}