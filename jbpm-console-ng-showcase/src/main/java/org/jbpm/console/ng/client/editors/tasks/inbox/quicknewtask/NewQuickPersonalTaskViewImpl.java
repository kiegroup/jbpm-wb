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
package org.jbpm.console.ng.client.editors.tasks.inbox.quicknewtask;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.client.editors.tasks.inbox.events.UserTaskEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class NewQuickPersonalTaskViewImpl extends Composite
    implements
    NewQuickPersonalTaskPresenter.InboxView {

    interface NewQuickPersonalTaskViewImplBinder
        extends
        UiBinder<Widget, NewQuickPersonalTaskViewImpl> {
    }

    private static NewQuickPersonalTaskViewImplBinder uiBinder = GWT.create( NewQuickPersonalTaskViewImplBinder.class );

    @Inject
    private PlaceManager                              placeManager;

    private NewQuickPersonalTaskPresenter             presenter;
    @UiField
    public Button                                     addTaskButton;
    @UiField
    public TextBox                                    userText;
    @UiField
    public TextBox                                    taskNameText;
    @Inject
    private Event<NotificationEvent>                  notification;
    @Inject
    private Event<UserTaskEvent>                      userTaskChanges;

    @Override
    public void init(NewQuickPersonalTaskPresenter presenter) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );

    }

    @UiHandler("addTaskButton")
    public void addTaskButton(ClickEvent e) {
        presenter.addQuickTask( userText.getText(),
                                    taskNameText.getText() );
    }

    public void displayNotification(String text) {
        notification.fire( new NotificationEvent( text ) );
        userTaskChanges.fire( new UserTaskEvent( userText.getText() ) );
    }
}
