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

package org.jbpm.workbench.pr.client.editors.instance.signal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.SuggestBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "ProcessInstanceSignalViewImpl.html")
public class ProcessInstanceSignalViewImpl extends Composite implements ProcessInstanceSignalPresenter.PopupView {

    @Inject
    @DataField
    public Button signalButton;

    @Inject
    @DataField
    public Button clearButton;

    @Inject
    @DataField
    public FormLabel signalRefLabel;

    @Inject
    @DataField
    public FormLabel eventLabel;

    @Inject
    @DataField
    public TextBox eventText;

    @DataField
    public SuggestBox signalRefText;

    public List<Long> processInstanceIds = new ArrayList<Long>();

    private Constants constants = GWT.create(Constants.class);

    private ProcessInstanceSignalPresenter presenter;

    @Inject
    private Event<NotificationEvent> notification;

    private MultiWordSuggestOracle oracle;

    public ProcessInstanceSignalViewImpl() {
        oracle = new MultiWordSuggestOracle();
        signalRefText = new SuggestBox(oracle);
    }

    @Override
    public void init(ProcessInstanceSignalPresenter presenter) {
        this.presenter = presenter;
        clearButton.setText(constants.Clear());
        signalButton.setText(constants.Signal());
        signalRefLabel.setText(constants.Signal_Name());
        eventLabel.setText(constants.Signal_Data());
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @EventHandler("signalButton")
    public void signalButton(ClickEvent e) {

        for (Long processInstanceId : this.processInstanceIds) {

            displayNotification(constants.Signalling_Process_Instance() + processInstanceId + " " + constants.Signal() + " = "
                                        + signalRefText.getText() + " - " + constants.Signal_Data() + " = " + eventText.getText());
        }
        presenter.signalProcessInstances(this.processInstanceIds);
    }

    @EventHandler("clearButton")
    public void clearButton(ClickEvent e) {
        signalRefText.setValue("");
        eventText.setValue("");
    }

    @Override
    public void addProcessInstanceId(long processInstanceId) {
        this.processInstanceIds.add(processInstanceId);
    }

    @Override
    public String getSignalRefText() {
        return signalRefText.getText();
    }

    @Override
    public String getEventText() {
        return eventText.getText();
    }

    @Override
    public void setAvailableSignals(Collection<String> signals) {
        oracle.addAll(signals);
    }
}
