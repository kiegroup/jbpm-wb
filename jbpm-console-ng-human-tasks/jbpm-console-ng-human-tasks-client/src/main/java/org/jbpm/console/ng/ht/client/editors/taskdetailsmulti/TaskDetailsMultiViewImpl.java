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
package org.jbpm.console.ng.ht.client.editors.taskdetailsmulti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.ht.client.i18n.Constants;

@Dependent
public class TaskDetailsMultiViewImpl extends AbstractTabbedDetailsView<TaskDetailsMultiPresenter>
                                      implements TaskDetailsMultiPresenter.TaskDetailsMultiView{

  interface Binder
          extends
          UiBinder<Widget, TaskDetailsMultiViewImpl> {

  }
  private static Binder uiBinder = GWT.create(Binder.class);

  private Constants constants = GWT.create(Constants.class);

  @Override
  public void init(final TaskDetailsMultiPresenter presenter) {
    super.init(presenter);
  }

  @Override
  public void initTabs() {
    tabPanel.addTab("Generic Form Display", constants.Work());
    tabPanel.addTab("Task Details", constants.Details());
    tabPanel.addTab("Task Assignments", constants.Assignments());
    tabPanel.addTab("Task Comments", constants.Comments());
    tabPanel.addTab("Task Logs", constants.Logs());
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
          presenter.goToTaskFormTab();
        } else if (selectedIndex == 1) {
          presenter.goToTaskDetailsTab();
        } else if (selectedIndex == 2) {
          presenter.goToTaskAssignmentsTab();
        } else if (selectedIndex == 3) {
          presenter.goToTaskCommentsTab();
        } else if (selectedIndex == 4) {
          presenter.goToTaskLogsTab();
        }
                
      }
    });

    tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

      @Override
      public void onSelection(SelectionEvent<Integer> event) {
        if (event.getSelectedItem() == 0) {
          presenter.goToTaskFormTab();
        } else if (event.getSelectedItem() == 1) {
          presenter.goToTaskDetailsTab();
        } else if (event.getSelectedItem() == 2) {
          presenter.goToTaskAssignmentsTab();
        } else if (event.getSelectedItem() == 3) {
          presenter.goToTaskCommentsTab();
        } else if (event.getSelectedItem() == 4) {
          presenter.goToTaskLogsTab();
        }
      }
    });

  }

}
