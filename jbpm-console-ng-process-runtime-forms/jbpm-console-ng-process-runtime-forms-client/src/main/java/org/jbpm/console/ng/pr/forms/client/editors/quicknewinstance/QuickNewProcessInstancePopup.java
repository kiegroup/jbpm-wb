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
package org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ga.forms.display.GenericFormDisplayer;
import org.jbpm.console.ng.ga.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.ga.forms.display.view.FormDisplayerView;
import org.jbpm.console.ng.pr.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.console.ng.pr.forms.display.process.api.ProcessDisplayerConfig;
import org.jbpm.console.ng.pr.forms.display.process.api.StartProcessFormDisplayProvider;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.pr.forms.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.service.ProcessDefinitionService;
import org.uberfire.paging.PageResponse;

@Dependent
public class QuickNewProcessInstancePopup extends BaseModal implements FormDisplayerView {

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

    @UiField
    public FlowPanel body;

    @Inject
    User identity;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<ProcessDefinitionService> processDefinitionService;

    protected QueryFilter currentFilter;

    private static Binder uiBinder = GWT.create(Binder.class);


    private Long parentProcessInstanceId = -1L;


    @Inject
    private StartProcessFormDisplayProvider widgetPresenter;

    private Command onCloseCommand;

    private Command childCloseCommand;

    private FormContentResizeListener formContentResizeListener;

    private boolean initialized = false;


    private GenericFormDisplayer currentDisplayer;

    private int initialWidth = -1;

    private String deploymentId;

    private String processId;

    final GenericModalFooter footer = new GenericModalFooter();

    @Inject
    protected StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    public QuickNewProcessInstancePopup() {
        setTitle(Constants.INSTANCE.Start());

        add(uiBinder.createAndBindUi(this));



        tabPanel.addShowHandler(new TabPanel.ShowEvent.Handler() {
            @Override
            public void onShow(TabPanel.ShowEvent showEvent) {
                GWT.log("TAB 4: " + showEvent.getTarget().getText());
                if(showEvent.getTarget().getText().trim().equals("Basic")){
                    resetForm();

                }


            }
        });

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
                    dropDowns.get(sum.getDeploymentId()).add(sum.getProcessDefId());
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
        resetForm();
    }

    public void resetForm(){
        footer.clear();
        footer.addButton(Constants.INSTANCE.Start(),
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY);

        clearErrorMessages();

        processDeploymentIdListBox.setSelectedIndex(0);



        processDefinitionsListBox.setSelectedIndex(0);

        this.parentProcessInstanceId = -1L;

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
            deploymentId = processDeploymentIdListBox.getValue();
            processId = processDefinitionsListBox.getValue();
            GWT.log("Selected Deployment Id: "+ deploymentId);
            GWT.log("Selected Process Id: "+ processId);
            tabPanel.selectTab(1);
            basicTab.setActive(false);
            formTab.setActive(true);
            ProcessDisplayerConfig config = new ProcessDisplayerConfig( new ProcessDefinitionKey( deploymentId, processId ), processId );
            startProcessDisplayProvider.setup(config, this);

        }

    }

    private void refreshNewTask(Long taskId, String taskName, String msj) {
        displayNotification(msj);

        closePopup();
    }

    private void clearErrorMessages() {
        errorMessages.setText("");

    }

    @PostConstruct
    protected void init() {

        footer.addButton(Constants.INSTANCE.Start(),
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY);

        add(footer);
        formTab.setActive(false);
        onCloseCommand = new Command() {
            @Override
            public void execute() {
                closePopup();
            }
        };

        formContentResizeListener = new FormContentResizeListener () {
            @Override
            public void resize(int width, int height) {
                if (initialWidth == -1 && getOffsetWidth() > 0) initialWidth = getOffsetWidth();
                if (width > getOffsetWidth()) setWidth(width + 20);
                else if (initialWidth != -1) setWidth(initialWidth);
                centerVertically(getElement());
            }
        };
       // add(body);
        this.addHiddenHandler(new HiddenHandler() {
            @Override
            public void onHidden(HiddenEvent hiddenEvent) {
                if (initialized) closePopup();
            }
        });
    }

    @Override
    public void display(GenericFormDisplayer displayer) {
        setBackdrop(BackdropType.NORMAL);
        setKeyboard(true);
        setAnimation(true);
        setDynamicSafe(true);
        currentDisplayer = displayer;
        body.clear();
        footer.clear();
        formTab.add(body);
        body.add(displayer.getContainer());
        if (displayer.getOpener() == null) footer.add(displayer.getFooter());
        centerVertically(getElement());
        initialized = true;

    }

    public void closePopup() {

        cleanForm();
      //  if (childCloseCommand != null) childCloseCommand.execute();
       // setWidth("");
        initialized = false;
        super.hide();
    }

    private native void centerVertically(Element e) /*-{
        $wnd.jQuery(e).css("margin-top", (-1 * $wnd.jQuery(e).outerHeight() / 2) + "px");
    }-*/;

    @Override
    public Command getOnCloseCommand() {
        return onCloseCommand;
    }

    @Override
    public void setOnCloseCommand(Command onCloseCommand) {
        this.childCloseCommand = onCloseCommand;
    }

    @Override
    public FormContentResizeListener getResizeListener() {
        return formContentResizeListener;
    }

    @Override
    public void setResizeListener(FormContentResizeListener resizeListener) {
        formContentResizeListener = resizeListener;
    }

    @Override
    public GenericFormDisplayer getCurrentDisplayer() {
        return currentDisplayer;
    }

}
