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
package org.jbpm.console.ng.client.editors.tasks.statistics;


import org.jbpm.console.ng.client.editors.tasks.inbox.quicknewtask.*;
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
import org.jbpm.console.ng.client.editors.tasks.inbox.events.UserTaskEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

@Dependent
public class PersonalTasksStatisticsViewImpl extends Composite implements PersonalTasksStatisticsPresenter.InboxView {

    @Inject
    private UiBinder<Widget, PersonalTasksStatisticsViewImpl> uiBinder;
    @Inject
    private PlaceManager placeManager;
    private PersonalTasksStatisticsPresenter presenter;
    @UiField
    public Button refreshButton;
    @UiField
    public TextBox userText;
    
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<UserTaskEvent> userTaskChanges;
    
    @Override
    public void init(PersonalTasksStatisticsPresenter presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));

    }

    @UiHandler("refreshButton")
    public void refreshButton(ClickEvent e) {
        presenter.refreshGraphs(userText.getText());  
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
        userTaskChanges.fire(new UserTaskEvent(userText.getText()));
    }
}
