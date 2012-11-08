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
package org.jbpm.console.ng.client.editors.tasks.inbox.quick.subtask;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.shared.events.TaskChangedEvent;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated(value = "QuickNewSubTaskViewImpl.html")
public class QuickNewSubTaskViewImpl extends Composite
        implements
        QuickNewSubTaskPresenter.InboxView {

   
    private QuickNewSubTaskPresenter presenter;
    @Inject
    @DataField
    public Button createSubTaskButton;
    @Inject
    @DataField
    public TextBox subTaskNameText;
    @Inject
    @DataField
    public TextBox subTaskAsigneeText;
    @Inject
    @DataField
    public TextBox parentTaskIdText;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<TaskChangedEvent> taskChanged;

    @Override
    public void init(QuickNewSubTaskPresenter presenter) {
        this.presenter = presenter;

    }

    @EventHandler("createSubTaskButton")
    public void createSubTaskButton(ClickEvent e) {
        presenter.addSubTask(Long.parseLong(parentTaskIdText.getText()),
                subTaskAsigneeText.getText(),
                subTaskNameText.getText());

    }

    public TextBox getParentTaskIdText() {
        return parentTaskIdText;
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

}
