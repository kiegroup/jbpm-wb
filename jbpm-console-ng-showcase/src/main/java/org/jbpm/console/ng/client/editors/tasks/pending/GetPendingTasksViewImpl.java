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
package org.jbpm.console.ng.client.editors.tasks.pending;


import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.client.editors.tasks.inbox.events.UserTaskEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

//
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.shared.fb.events.FormLoadedEvent;

@Dependent
@Templated(value = "GetPendingTasksViewImpl.html")
public class GetPendingTasksViewImpl extends Composite
        implements
        GetPendingTasksPresenter.InboxView {

    @Inject
    private PlaceManager placeManager;
    private GetPendingTasksPresenter presenter;
    @Inject
    @DataField
    public Button pendingTasksButton;
   
    @Inject
    @DataField
    public Button loadFormButton;
    
    @Inject
    @DataField
    public TextBox userText;
    
    @Inject
    @DataField 
    public TextArea formTextArea;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<UserTaskEvent> userTaskChanges;
    
    @Inject
    private Event<FormLoadedEvent> formLoadedEvents;

    @Override
    public void init(GetPendingTasksPresenter presenter) {
        this.presenter = presenter;

    }

    @EventHandler("pendingTasksButton")
    public void addTaskButton(ClickEvent e) {
        presenter.getPendingTasks(userText.getText());
    }

    @EventHandler("loadFormButton")
    public void loadFormButton(ClickEvent e) {
        formLoadedEvents.fire(new FormLoadedEvent(formTextArea.getText()));
    }

    
    public void displayNotification(String userId) {
        notification.fire(new NotificationEvent(userId));
        
    }
}
