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
package org.jbpm.console.ng.pr.client.editors.definition.details;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Window;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;



import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.console.ng.pr.client.i18n.Constants;

@Dependent
@Templated(value = "ProcessDefDetailsViewImpl.html")
public class ProcessDefDetailsViewImpl extends Composite
        implements
        ProcessDefDetailsPresenter.InboxView {

  @Inject
  private PlaceManager placeManager;
  private ProcessDefDetailsPresenter presenter;
  @Inject
  @DataField
  public TextBox processNameText;
  @Inject
  @DataField
  public TextBox nroOfHumanTasksText;
  @Inject
  @DataField
  public ListBox humanTasksListBox;
  @Inject
  @DataField
  public ListBox usersGroupsListBox;
  @Inject
  @DataField
  public ListBox processDataListBox;
  @Inject
  @DataField
  public ListBox subprocessListBox;
  @Inject
  @DataField
  public Button refreshButton;
  @Inject
  @DataField
  public Button viewProcessInstancesButton;
  @Inject
  @DataField
  public Button createProcessInstanceButton;
  @Inject
  @DataField
  public Button openProcessDesignerButton;
  @Inject
  private Event<NotificationEvent> notification;
  private Constants constants = GWT.create(Constants.class);

  @Override
  public void init(ProcessDefDetailsPresenter presenter) {
    this.presenter = presenter;
    this.humanTasksListBox.setVisibleItemCount(5);
    this.usersGroupsListBox.setVisibleItemCount(5);
    this.processDataListBox.setVisibleItemCount(5);

    this.subprocessListBox.addDoubleClickHandler(new DoubleClickHandler() {
      @Override
      public void onDoubleClick(DoubleClickEvent event) {
        ListBox source = (ListBox) event.getSource();
        String processId = source.getValue(source.getSelectedIndex());
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(constants.Process_Definition_Details_Perspective());
        placeRequestImpl.addParameter("processId", processId);
        placeManager.goTo(placeRequestImpl);
      }
    });
  }

  @EventHandler("refreshButton")
  public void refreshButton(ClickEvent e) {
    presenter.refreshProcessDef(processNameText.getText());
  }

  @EventHandler("createProcessInstanceButton")
  public void createProcessInstance(ClickEvent e) {
    PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display");
    System.out.println("Opening form for process id = " + processNameText.getText());
    placeRequestImpl.addParameter("processId", processNameText.getText());
    placeManager.goTo(placeRequestImpl);
  }

  @EventHandler("viewProcessInstancesButton")
  public void viewProcessInstancesButton(ClickEvent e) {
    PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Process Instance List");
    placeRequestImpl.addParameter("processDefId", processNameText.getText());
    placeManager.goTo(placeRequestImpl);

  }

  //http://localhost:8080/designer/editor?profile=jbpm&pp=&uuid=git://designer-repo/examples/release/release.bpmn
  @EventHandler("openProcessDesignerButton")
  public void openProcessDesignerButton(ClickEvent e) {
    presenter.openProcessDiagram(processNameText.getText());
      
     

  }

  public TextBox getProcessNameText() {
    return processNameText;
  }

  public TextBox getNroOfHumanTasksText() {
    return nroOfHumanTasksText;
  }

  public ListBox getHumanTasksListBox() {
    return humanTasksListBox;
  }

  public ListBox getUsersGroupsListBox() {
    return usersGroupsListBox;
  }

  public ListBox getProcessDataListBox() {
    return processDataListBox;
  }

  public ListBox getSubprocessListBox() {
    return subprocessListBox;
  }

  public void displayNotification(String text) {
    notification.fire(new NotificationEvent(text));
  }
}
