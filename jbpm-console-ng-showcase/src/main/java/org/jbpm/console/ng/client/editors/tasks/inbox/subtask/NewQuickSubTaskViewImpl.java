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
package org.jbpm.console.ng.client.editors.tasks.inbox.subtask;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskChangedEvent;
import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskSelectionEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

@Dependent
public class NewQuickSubTaskViewImpl extends Composite implements NewQuickSubTaskPresenter.InboxView {

    @Inject
    private UiBinder<Widget, NewQuickSubTaskViewImpl> uiBinder;
    @Inject
    private PlaceManager placeManager;
    
    private NewQuickSubTaskPresenter presenter;

    @UiField
    public Button createSubTaskButton;
    @UiField
    public TextBox subTaskNameText;
    @UiField
    public TextBox subTaskAsigneeText;
    @UiField
    public TextBox parentTaskIdText;

    @Inject
    private Event<NotificationEvent> notification;
    
    @Inject
    private Event<TaskChangedEvent> taskChanged;

    @Override
    public void init(NewQuickSubTaskPresenter presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
        
    }

    @UiHandler("createSubTaskButton")
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

     public void receiveSelectedNotification(@Observes TaskSelectionEvent event){
        parentTaskIdText.setText(String.valueOf(event.getTaskId()));
    }
  
}
