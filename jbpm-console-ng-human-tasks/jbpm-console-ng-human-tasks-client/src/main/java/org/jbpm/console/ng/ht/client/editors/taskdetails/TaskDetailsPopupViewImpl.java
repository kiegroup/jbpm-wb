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
package org.jbpm.console.ng.ht.client.editors.taskdetails;

import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;



import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "TaskDetailsPopupViewImpl.html")
public class TaskDetailsPopupViewImpl extends Composite
        implements
        TaskDetailsPopupPresenter.TaskDetailsPopupView {

    private TaskDetailsPopupPresenter presenter;
//    @Inject
//    @DataField
//    private Label subSectionDescriptionDiv;
//    @Inject
//    @DataField
//    private Label subSectionDescriptionCollapseDiv;
//    @Inject
//    @DataField
//    private Label subSectionProcessContextDiv;
//    @Inject
//    @DataField
//    private Label subSectionProcessContextCollapseDiv;
    
//    @Inject
//    @DataField
//    public Label goToWork;
    
//    @Inject
//    @DataField
//    private Label subSectionSubTaskStrategiesDiv;
//    
//    @Inject
//    @DataField
//    private Label subSectionSubTaskStrategiesCollapseDiv;        
//            
    @Inject
    @DataField
    public Label taskIdText;
    @Inject
    @DataField
    public TextBox userText;
    @Inject
    @DataField
    public Label taskNameText;
    @Inject
    @DataField
    public TextBox processInstanceIdText;
    @Inject
    @DataField
    public TextBox processIdText;
    @Inject
    @DataField
    public TextBox taskStatusText;
    @Inject
    @DataField
    public TextArea taskDescriptionTextArea;
    @Inject
    @DataField
    public ListBox taskPriorityListBox;
    @Inject
    @DataField
    public ListBox subTaskStrategyListBox;
    @Inject
    @DataField
    public DateBox dueDate;
    @Inject
    @DataField
    public Button closeButton;
    @Inject
    @DataField
    public Button pIDetailsButton;
    @Inject
    private PlaceManager placeManager;
    private String[] subTaskStrategies = {"NoAction", "EndParentOnAllSubTasksEnd", "SkipAllSubTasksOnParentSkip"};
    private String[] priorities = {"0 - High", "1", "2", "3", "4", "5 - Medium", "6", "7", "8", "9", "10 - Low"};
    
    @Inject
    @DataField
    public UnorderedList navBarUL;
    
    @Inject
    private Event<NotificationEvent> notification;
    

    @Override
    public void init(TaskDetailsPopupPresenter presenter) {
        this.presenter = presenter;


        for (String strategy : subTaskStrategies) {
            subTaskStrategyListBox.addItem(strategy);

        }

        for (String priority : priorities) {
            taskPriorityListBox.addItem(priority);

        }


    }

//    @EventHandler("subSectionDescriptionCollapseDiv")
//    public void subSectionDescriptionCollapseDiv(ClickEvent e) {
//        if (subSectionDescriptionDiv.getStyleName().equals("sub-section")) {
//            subSectionDescriptionDiv.setStyleName("sub-section collapsed");
//        } else if (subSectionDescriptionDiv.getStyleName().equals("sub-section collapsed")) {
//            subSectionDescriptionDiv.setStyleName("sub-section");
//        }
//
//    }
//
//    @EventHandler("subSectionProcessContextCollapseDiv")
//    public void subSectionProcessContextCollapseDiv(ClickEvent e) {
//        if (subSectionProcessContextDiv.getStyleName().equals("sub-section")) {
//            subSectionProcessContextDiv.setStyleName("sub-section collapsed");
//        } else if (subSectionProcessContextDiv.getStyleName().equals("sub-section collapsed")) {
//            subSectionProcessContextDiv.setStyleName("sub-section");
//        }
//
//    }
//    
//    @EventHandler("subSectionSubTaskStrategiesCollapseDiv")
//    public void subSectionSubTaskStrategiesCollapseDiv(ClickEvent e) {
//        if (subSectionSubTaskStrategiesDiv.getStyleName().equals("sub-section")) {
//            subSectionSubTaskStrategiesDiv.setStyleName("sub-section collapsed");
//        } else if (subSectionSubTaskStrategiesDiv.getStyleName().equals("sub-section collapsed")) {
//            subSectionSubTaskStrategiesDiv.setStyleName("sub-section");
//        }
//
//    }
    
//    @EventHandler("goToWork")
//    public void goToWork(ClickEvent e) {
//        presenter.close();
//        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display");
//        placeRequestImpl.addParameter("taskId", taskIdText.getText());
//        placeManager.goTo(placeRequestImpl);
//
//    }
//
//    @EventHandler("updateButton")
//    public void updateTaskButton(ClickEvent e) {
//        presenter.updateTask(Long.parseLong(taskIdText.getText()), taskNameText.getText(),
//                taskDescriptionTextArea.getText(), userText.getText(),
//                subTaskStrategyListBox.getItemText(subTaskStrategyListBox.getSelectedIndex()),
//                dueDate.getValue(),
//                taskPriorityListBox.getSelectedIndex());
//
//    }

    @EventHandler("closeButton")
    public void closeButton(ClickEvent e) {
        presenter.close();
    }


    @EventHandler("pIDetailsButton")
    public void pIDetailsButton(ClickEvent e) {
        presenter.close();
        presenter.goToProcessInstanceDetails();
    }

    public TextBox getUserText() {
        return userText;
    }

    public Label getTaskIdText() {
        return taskIdText;
    }

    public Label getTaskNameText() {
        return taskNameText;
    }

    public TextArea getTaskDescriptionTextArea() {
        return taskDescriptionTextArea;
    }

    public ListBox getTaskPriorityListBox() {
        return taskPriorityListBox;
    }

    public DateBox getDueDate() {
        return dueDate;
    }

    public ListBox getSubTaskStrategyListBox() {
        return subTaskStrategyListBox;
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public String[] getSubTaskStrategies() {
        return subTaskStrategies;
    }

    public String[] getPriorities() {
        return priorities;
    }

    public TextBox getTaskStatusText() {
        return taskStatusText;
    }

    public TextBox getProcessInstanceIdText() {
        return processInstanceIdText;
    }

    public TextBox getProcessIdText() {
        return processIdText;
    }
    
    public Button getpIDetailsButton() {
        return pIDetailsButton;
    }

    public UnorderedList getNavBarUL() {
      return navBarUL;
    }
    
    
}
