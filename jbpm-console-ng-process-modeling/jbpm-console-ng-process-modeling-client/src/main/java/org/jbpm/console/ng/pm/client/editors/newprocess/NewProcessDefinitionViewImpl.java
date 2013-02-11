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
package org.jbpm.console.ng.pm.client.editors.newprocess;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

//
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.security.Identity;

@Dependent
@Templated(value = "NewProcessDefinitionViewImpl.html")
public class NewProcessDefinitionViewImpl extends Composite
        implements
        NewProcessDefinitionPresenter.NewProcessDefinitionView {

  @Inject
  private Identity identity;
  @Inject
  private PlaceManager placeManager;
  private NewProcessDefinitionPresenter presenter;
  @Inject
  @DataField
  public Button newProcessDefinitionButton;
  @Inject
  @DataField
  public TextBox processDefinitionPathText;
 
  @Inject
  private Event<NotificationEvent> notification;

  @Override
  public void init(NewProcessDefinitionPresenter presenter) {
    this.presenter = presenter;
    KeyPressHandler keyPressHandlerText = new KeyPressHandler() {
      public void onKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == 13) {
          createProcessDefinitionFile();
        }
      }
    };
    processDefinitionPathText.addKeyPressHandler(keyPressHandlerText);

    KeyPressHandler keyPressHandlerCheck = new KeyPressHandler() {
      public void onKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == 13) {
          createProcessDefinitionFile();
        }
      }
    };


  }

  @EventHandler("newProcessDefinitionButton")
  public void newProcessDefinitionButton(ClickEvent e) {
    createProcessDefinitionFile();
  }

  public void displayNotification(String text) {
    notification.fire(new NotificationEvent(text));

  }

 
  private void createProcessDefinitionFile() {
    presenter.createNewProcess(processDefinitionPathText.getText());

  }
  
    
  
}
