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
package org.jbpm.console.ng.client.editors.process.instance.details.basic;

import org.jbpm.console.ng.client.editors.process.definition.details.basic.*;
import com.google.gwt.event.dom.client.ClickEvent;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;



import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated(value = "ProcessInstanceDetailsViewImpl.html")
public class ProcessInstanceDetailsViewImpl extends Composite
        implements
        ProcessInstanceDetailsPresenter.InboxView {

    
    private ProcessInstanceDetailsPresenter presenter;
  
    @Inject
    @DataField
    public TextBox processNameText;

  
    @Inject
    @DataField
    public Button refreshButton;
    
   
    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(ProcessInstanceDetailsPresenter presenter) {
        this.presenter = presenter;


       

     
    }

   
   
    @EventHandler("refreshButton")
    public void refreshButton(ClickEvent e) {
        displayNotification(processNameText.getText());
    }

    public TextBox getProcessNameText() {
        return processNameText;
    }

 

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }



  
}
