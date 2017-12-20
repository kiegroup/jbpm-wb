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

package org.jbpm.workbench.forms.client.display.process;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.ModalSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.select.client.ui.OptGroup;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.forms.client.display.GenericFormDisplayer;
import org.jbpm.workbench.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.workbench.forms.client.display.views.FormDisplayerView;
import org.jbpm.workbench.forms.client.i18n.Constants;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.stream.Collectors.groupingBy;

@Dependent
public class QuickNewProcessInstancePopup extends BaseModal implements FormDisplayerView {

    public static final String FIELD_ID_PROCESSNAME = "ProcessName";
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    public FlowPanel processForm;

    @UiField
    public FlowPanel basicForm;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public FormGroup errorMessagesGroup;

    @UiField
    public FormGroup processDefinitionsControlGroup;

    @UiField
    public Select processDefinitionsListBox;

    @UiField
    public HelpBlock processDefinitionsHelpLabel;

    @UiField
    public FlowPanel body;

    @Inject
    protected StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    @Inject
    User identity;

    GenericModalFooter footer = GWT.create(GenericModalFooter.class);

    @Inject
    private Event<NotificationEvent> notification;

    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    private Long parentProcessInstanceId = -1L;

    private Command onCloseCommand;

    private boolean initialized = false;

    private GenericFormDisplayer currentDisplayer;

    private String serverTemplateId;

    private String deploymentId;

    private String processId;

    public QuickNewProcessInstancePopup() {
        super();
        setTitle(Constants.INSTANCE.Start_process_instance());
        setBody(uiBinder.createAndBindUi(this));
        setSize(ModalSize.LARGE);
    }

    public void show(Long parentProcessInstanceId) {
        show();
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public void show(String serverTemplateId) {

        init();
        loadFormValues(serverTemplateId);

        processForm.setVisible(false);
        basicForm.setVisible(true);
        super.show();
    }

    private void okButton() {
        if (validateForm()) {
            createNewProcessInstance();
        }
    }

    protected void loadFormValues(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
        processDefinitionsListBox.clear();

        processRuntimeDataService.call((List<ProcessSummary> processSummaries) -> {

            //Skip case definitions (isDynamic == true)
            Map<String, List<ProcessSummary>> defs = processSummaries.stream().filter(p -> p.isDynamic() == false).collect(groupingBy(ProcessSummary::getDeploymentId));

            defs.keySet().stream().sorted().forEach(deploymentId -> {
                final OptGroup group = GWT.create(OptGroup.class);
                group.setLabel(deploymentId);

                defs.get(deploymentId).stream().sorted().forEach(p -> {
                    final Option option = GWT.create(Option.class);
                    option.setText(p.getProcessDefId());
                    option.setValue(p.getProcessDefId());
                    group.add(option);
                });

                processDefinitionsListBox.add(group);
            });

            Scheduler.get().scheduleDeferred(() -> processDefinitionsListBox.refresh());
        }).getProcesses(serverTemplateId,
                        0,
                        Integer.MAX_VALUE,
                        FIELD_ID_PROCESSNAME,
                        true);
    }

    protected boolean validateForm() {
        if (processDefinitionsListBox.getSelectedItem() == null) {

            errorMessages.setText(Constants.INSTANCE.Select_Process());
            errorMessagesGroup.setValidationState(ValidationState.ERROR);

            return false;
        } else {
            clearErrorMessages();
            return true;
        }
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    protected void createNewProcessInstance() {
        final Option selectedItem = processDefinitionsListBox.getSelectedItem();
        deploymentId = ((OptGroup) selectedItem.getParent()).getLabel();
        processId = selectedItem.getValue();

        processForm.setVisible(true);
        basicForm.setVisible(false);

        ProcessDisplayerConfig config = new ProcessDisplayerConfig(new ProcessDefinitionKey(serverTemplateId,
                                                                                            deploymentId,
                                                                                            processId),
                                                                   processId);
        startProcessDisplayProvider.setup(config,
                                          this);
    }

    private void clearErrorMessages() {
        errorMessages.setText("");
    }

    protected void init() {

        removeFooter(this);
        footer = new GenericModalFooter();
        footer.addButton(Constants.INSTANCE.Start(),
                         new Command() {
                             @Override
                             public void execute() {
                                 okButton();
                             }
                         },
                         IconType.PLUS,
                         ButtonType.PRIMARY);

        add(footer);

        onCloseCommand = new Command() {
            @Override
            public void execute() {
                closePopup();
            }
        };

        this.addHiddenHandler(new ModalHiddenHandler() {
            @Override
            public void onHidden(ModalHiddenEvent hiddenEvent) {
                if (initialized) {
                    closePopup();
                }
            }
        });
    }

    @Override
    public void display(GenericFormDisplayer displayer) {
        currentDisplayer = displayer;

        body.clear();
        body.add(displayer.getContainer());
        ((AbstractStartProcessFormDisplayer) displayer)
                .setParentProcessInstanceId(this.parentProcessInstanceId);

        removeFooter(this);
        footer = new GenericModalFooter();
        if (displayer.getOpener() == null) {
            footer.add(displayer.getFooter());
        }
        add(footer);

        initialized = true;
    }

    public void closePopup() {
        initialized = false;
        hide();
        super.hide();
    }

    private void removeFooter(final ComplexPanel panel) {
        for (Widget widget : panel) {
            if (widget instanceof ModalFooter) {
                widget.removeFromParent();
                break;
            } else if (widget instanceof ComplexPanel) {
                removeFooter((ComplexPanel) widget);
            }
        }
    }

    @Override
    public Command getOnCloseCommand() {
        return onCloseCommand;
    }

    @Override
    public void setOnCloseCommand(Command onCloseCommand) {
        this.onCloseCommand = onCloseCommand;
    }

    @Override
    public GenericFormDisplayer getCurrentDisplayer() {
        return currentDisplayer;
    }

    @Inject
    public void setProcessRuntimeDataService(Caller<ProcessRuntimeDataService> processRuntimeDataService) {
        this.processRuntimeDataService = processRuntimeDataService;
    }

    interface Binder
            extends
            UiBinder<Widget, QuickNewProcessInstancePopup> {

    }
}
