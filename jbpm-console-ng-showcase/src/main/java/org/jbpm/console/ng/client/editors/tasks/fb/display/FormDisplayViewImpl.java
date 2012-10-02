/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.console.ng.client.editors.tasks.fb.display;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.shared.fb.events.FormRenderedEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Main view. 
 */
@Dependent
@Templated(value="FormDisplayViewImpl.html")
public class FormDisplayViewImpl extends Composite
    implements
    FormDisplayPresenter.FormBuilderView {


    @Inject
    private PlaceManager                     placeManager;
    private FormDisplayPresenter             presenter;
    @Inject
    @DataField
    public VerticalPanel                    formView;
    @Inject
    @DataField
    public TextBox                           userIdText;
    @Inject
    @DataField
    public TextBox                           taskIdText;
    @Inject
    @DataField
    public Button                            renderButton;
    @Inject
    private Event<NotificationEvent>         notification;
    
    @Inject 
    private Identity identity;

    @EventHandler("renderButton")
    public void renderAction(ClickEvent e) {
        presenter.renderForm( new Long( taskIdText.getText() ) );

    }

    public void renderForm(@Observes FormRenderedEvent formRendered) {
        formView.add( new HTMLPanel( formRendered.getForm() ) );

    }
    @OnStart
    public void onStart(final PlaceRequest p) {
        
        long taskId = Long.parseLong(p.getParameter("taskID","0"));
        userIdText.setText( identity.getName() );
        taskIdText.setText( String.valueOf( taskId ) );
        presenter.renderForm( new Long( taskIdText.getText() ) );
    }
    
    @OnReveal
    public void onReveal() {
        final PlaceRequest p = placeManager.getCurrentPlaceRequest();
        long taskId = Long.parseLong(p.getParameter("taskID","0"));
        userIdText.setText( identity.getName() );
        taskIdText.setText( String.valueOf( taskId ) );
        presenter.renderForm( new Long( taskIdText.getText() ) );
    }

    @Override
    public void init(FormDisplayPresenter presenter) {
        this.presenter = presenter;

    }

    public String getUserId() {
        return userIdText.getText();
    }

    public void displayNotification(String text) {
        notification.fire( new NotificationEvent( text ) );
    }
}
