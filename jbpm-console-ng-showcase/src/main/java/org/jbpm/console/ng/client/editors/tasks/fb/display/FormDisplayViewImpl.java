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

import com.google.gwt.core.client.GWT;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.shared.fb.events.FormRenderedEvent;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.console.ng.client.i18n.Constants;

/**
 * Main view. 
 */
@Dependent
@Templated(value="FormDisplayViewImpl.html")
public class FormDisplayViewImpl extends Composite
    implements
    FormDisplayPresenter.FormBuilderView {


    
    private FormDisplayPresenter             presenter;
    @Inject
    @DataField
    public VerticalPanel                    formView;
    
    public long taskId;
    
    @Inject
    private Event<NotificationEvent>         notification;
    
    private Constants constants = GWT.create(Constants.class);

    
  

  

    public void renderForm(@Observes FormRenderedEvent formRendered) {
        formView.add(new HTMLPanel(formRendered.getForm()));

    }
   
    
  

    @Override
    public void init(FormDisplayPresenter presenter) {
        this.presenter = presenter;

    }


    public void displayNotification(String text) {
        notification.fire( new NotificationEvent( text ) );
    }

   

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

   
    
}
