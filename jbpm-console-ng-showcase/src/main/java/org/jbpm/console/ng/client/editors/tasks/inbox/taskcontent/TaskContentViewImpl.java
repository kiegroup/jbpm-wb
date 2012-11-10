/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.client.editors.tasks.inbox.taskcontent;

import com.google.gwt.core.client.GWT;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.security.Identity;

import org.jbpm.console.ng.client.i18n.Constants;

@Dependent
@Templated(value = "TaskContentViewImpl.html#form")
public class TaskContentViewImpl extends Composite
        implements
        TaskContentPresenter.InboxView {

    @Inject
    private Identity identity;
    private TaskContentPresenter presenter;
    @Inject
    @DataField
    public Button saveContentButton;
    @Inject
    @DataField
    public Button addRowButton;
    @Inject
    @DataField
    public Button refreshContentButton;
    public long taskId;
    @Inject
    @DataField
    public VerticalPanel contentPanel;
    @Inject
    @DataField
    public VerticalPanel outputPanel;
    @Inject
    private Event<NotificationEvent> notification;
    private Map<TextBox, TextBox> textBoxs = new HashMap<TextBox, TextBox>();
    
    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(TaskContentPresenter presenter) {
        this.presenter = presenter;

    }

    @EventHandler("addRowButton")
    public void addRowButton(ClickEvent e) {
        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setStyleName("group-row value-pair");
        TextBox keyTextBox = new TextBox();

        TextBox valueTextBox = new TextBox();

        flowPanel.add(keyTextBox);
        flowPanel.add(valueTextBox);
        textBoxs.put(keyTextBox,
                valueTextBox);
        contentPanel.add(flowPanel);
    }

    @EventHandler("saveContentButton")
    public void saveContentButton(ClickEvent e) {
        saveContent(true);
    }

    @EventHandler("refreshContentButton")
    public void getContentButton(ClickEvent e) {
        presenter.getContentByTaskId(taskId);
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public VerticalPanel getContentPanel() {
        return contentPanel;
    }

    public VerticalPanel getOutputPanel() {
        return outputPanel;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public Map<TextBox, TextBox> getTextBoxs() {
        return textBoxs;
    }

    public void saveContent(boolean notify) throws NumberFormatException {
        Map<String, String> values = new HashMap<String, String>();
        for (Entry<TextBox, TextBox> entry : textBoxs.entrySet()) {
            values.put(entry.getKey().getText(),
                    entry.getValue().getText());
        }
        presenter.saveContent(taskId,
                values, notify);
    }
}
