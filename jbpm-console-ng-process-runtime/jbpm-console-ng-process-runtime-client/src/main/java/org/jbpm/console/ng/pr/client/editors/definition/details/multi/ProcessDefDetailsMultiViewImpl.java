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
package org.jbpm.console.ng.pr.client.editors.definition.details.multi;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.pr.client.i18n.Constants;

@Dependent
public class ProcessDefDetailsMultiViewImpl extends AbstractTabbedDetailsView<ProcessDefDetailsMultiPresenter>
        implements ProcessDefDetailsMultiPresenter.ProcessDefDetailsMultiView {

  interface Binder
          extends
          UiBinder<Widget, ProcessDefDetailsMultiViewImpl> {

  }
  private static Binder uiBinder = GWT.create(Binder.class);

  private Constants constants = GWT.create(Constants.class);

  @UiField
  public Button newInstanceButton;

  @UiField
  public DropdownButton optionsDropdown;

  @UiField
  public NavLink viewProcessInstancesNavLink;
  
  @UiField
  public NavLink viewProcessModelNavLink;
  
  @UiField
  public ButtonGroup optionsButtonGroup;

  @Override
  public void init(final ProcessDefDetailsMultiPresenter presenter) {
    super.init(presenter);
    uiBinder.createAndBindUi(this);
    initToolBar();
  }

  @Override
  public void initTabs() {

    tabPanel.addTab("Definition Details", constants.Definition_Details());
 
    tabPanel.setHeight("600px");
    tabPanel.addCloseHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        presenter.closeDetails();
      }
    });
    tabPanel.addRefreshHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        int selectedIndex = tabPanel.getSelectedIndex();
        if (selectedIndex == 0) {
          presenter.goToProcessDefDetailsTab();
        }
      }
    });

    tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

      @Override
      public void onSelection(SelectionEvent<Integer> event) {
        if (event.getSelectedItem() == 0) {
          presenter.goToProcessDefDetailsTab();
        } 
      }
    });
    
  }

  public void initToolBar() {
    newInstanceButton.setText(constants.New_Instance());
    newInstanceButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        presenter.createNewProcessInstance();
      }
    });
    optionsDropdown.setText(constants.Options());
    
    viewProcessModelNavLink.setText(constants.View_Process_Model());
    viewProcessModelNavLink.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        presenter.goToProcessDefModelPopup();
      }
    });
    viewProcessInstancesNavLink.setText(constants.View_Process_Instances());
    viewProcessInstancesNavLink.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        presenter.viewProcessInstances();
      }
    });
    
    optionsDropdown.add(viewProcessInstancesNavLink);
    optionsButtonGroup.add(newInstanceButton);
    optionsButtonGroup.add(optionsDropdown);
    tabPanel.getRightToolbar().add(optionsButtonGroup);
  }

}
