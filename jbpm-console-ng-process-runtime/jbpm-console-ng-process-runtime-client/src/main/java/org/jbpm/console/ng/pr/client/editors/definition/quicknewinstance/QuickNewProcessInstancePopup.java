/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.pr.client.editors.definition.quicknewinstance;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.service.ProcessDefinitionService;
import org.uberfire.paging.PageResponse;

@Dependent
public class QuickNewProcessInstancePopup extends BaseModal {

    interface Binder
            extends
            UiBinder<Widget, QuickNewProcessInstancePopup> {

    }

    @UiField
    public TabPanel tabPanel;

    @UiField
    public Tab basicTab;

    @UiField
    public Tab formTab;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;

    @UiField
    public ListBox processDeploymentIdListBox;

    @UiField
    public ControlGroup processDeploymentIdControlGroup;

    @UiField
    public HelpBlock processDeploymentIdHelpLabel;

    @UiField
    public ListBox processDefinitionsListBox;

    @UiField
    public ControlGroup processDefinitionsControlGroup;

    @UiField
    public HelpBlock processDefinitionsHelpLabel;

    @Inject
    User identity;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<ProcessDefinitionService> processDefinitionService;

    protected QueryFilter currentFilter;

    private static Binder uiBinder = GWT.create(Binder.class);


    private Long parentProcessInstanceId = -1L;

    public QuickNewProcessInstancePopup() {
        setTitle(Constants.INSTANCE.Start());

        add(uiBinder.createAndBindUi(this));

        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton(Constants.INSTANCE.Start(),
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY);

        add(footer);
    }

    public void show(Long parentProcessInstanceId) {
        show();
        this.parentProcessInstanceId = parentProcessInstanceId;

    }

    public void show() {
        cleanForm();
        loadFormValues();
        super.show();
    }

    private void okButton() {
        if (validateForm()) {
            createNewProcessInstance();

        }
    }

    protected void loadFormValues() {
        final Map<String, List<String>> dropDowns = new HashMap<String, List<String>>();
        currentFilter = new PortableQueryFilter(0,
                10,
                false, "",
                "",
                true);
        processDefinitionService.call(new RemoteCallback<List<ProcessSummary>>() {
            @Override
            public void callback(List<ProcessSummary> processSummaries) {

                for (ProcessSummary sum : processSummaries) {
                    if (dropDowns.get(sum.getDeploymentId()) == null) {
                        dropDowns.put(sum.getDeploymentId(), new ArrayList<String>());
                    }
                    dropDowns.get(sum.getDeploymentId()).add(sum.getProcessDefName());
                }

                processDeploymentIdListBox.addItem("--------");
                for (String deploymentId : dropDowns.keySet()) {
                    processDeploymentIdListBox.addItem(deploymentId);
                }

            }
        }).getAll(currentFilter);

        processDeploymentIdListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {

                processDefinitionsListBox.clear();
                processDefinitionsListBox.addItem("-------");
                int selected = processDeploymentIdListBox.getSelectedIndex();

                if (dropDowns.get(processDeploymentIdListBox.getValue(selected)) != null) {
                    for (String processDef : dropDowns.get(processDeploymentIdListBox.getValue(selected))) {
                        processDefinitionsListBox.addItem(processDef);
                    }
                }

            }
        });
    }

    public void cleanForm() {

        tabPanel.selectTab(0);
        basicTab.setActive(true);
        formTab.setActive(false);

        clearErrorMessages();

        processDeploymentIdListBox.clear();
        processDeploymentIdListBox.setSelectedValue("");

        processDefinitionsListBox.clear();
        processDefinitionsListBox.setSelectedValue("");

        this.parentProcessInstanceId = -1L;
    }

    public void closePopup() {
        cleanForm();
        hide();
        super.hide();
    }

    private boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();

        return valid;
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    private void createNewProcessInstance() {

        if (processDefinitionsListBox.getSelectedIndex() == 0) {

            errorMessages.setText(Constants.INSTANCE.Select());
            errorMessagesGroup.setType(ControlGroupType.ERROR);
            tabPanel.selectTab(0);
            basicTab.setActive(true);
            formTab.setActive(false);
        } else {
            tabPanel.selectTab(1);
            basicTab.setActive(false);
            formTab.setActive(true);
            GWT.log("Jumping to Form Tab");
        }

    }

    private void refreshNewTask(Long taskId, String taskName, String msj) {
        displayNotification(msj);

        closePopup();
    }

    private void clearErrorMessages() {
        errorMessages.setText("");

    }

}
