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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.console.ng.pr.client.i18n.Constants;

@Dependent
@Templated(value = "ProcessDefDetailsViewImpl.html")
public class ProcessDefDetailsViewImpl extends Composite
        implements
        ProcessDefDetailsPresenter.ProcessDefDetailsView {

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
  public TextBox domainIdText;
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
  
  private Path processAssetPath;

  @Override
  public void init(ProcessDefDetailsPresenter presenter) {
    this.presenter = presenter;
    this.humanTasksListBox.setVisibleItemCount(5);
    this.humanTasksListBox.setEnabled(false);
    this.usersGroupsListBox.setVisibleItemCount(5);
    this.usersGroupsListBox.setEnabled(false);
    this.processDataListBox.setVisibleItemCount(5);
    this.processDataListBox.setEnabled(false);
    this.processNameText.setEnabled(false);
    this.domainIdText.setEnabled(false);
    nroOfHumanTasksText.setEnabled(false);

    this.subprocessListBox.addDoubleClickHandler(new DoubleClickHandler() {
      @Override
      public void onDoubleClick(DoubleClickEvent event) {
        ListBox source = (ListBox) event.getSource();
        String processId = source.getValue(source.getSelectedIndex());
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(constants.Process_Definition_Details());
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
    placeRequestImpl.addParameter("processId", processNameText.getText());
    placeRequestImpl.addParameter("domainId", domainIdText.getText());
    placeManager.goTo(placeRequestImpl);
  }

  @EventHandler("viewProcessInstancesButton")
  public void viewProcessInstancesButton(ClickEvent e) {
    PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Process Instance List");
    placeRequestImpl.addParameter("processDefId", processNameText.getText());
    placeManager.goTo(placeRequestImpl);

  }

  @EventHandler("openProcessDesignerButton")
  public void openProcessDesignerButton(ClickEvent e) {    
      placeManager.goTo(processAssetPath);

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

  public TextBox getDomainIdText() {
      return domainIdText;
  }
  
  public void displayNotification(String text) {
    notification.fire(new NotificationEvent(text));
  }

  @Override
  public void setProcessAssetPath(Path processAssetPath) {
      this.processAssetPath = processAssetPath;
  }


}
