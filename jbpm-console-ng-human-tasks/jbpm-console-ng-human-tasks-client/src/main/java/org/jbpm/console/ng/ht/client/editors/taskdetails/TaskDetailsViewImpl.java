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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import java.util.Date;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.gc.client.util.UTCDateBox;
import org.jbpm.console.ng.gc.client.util.UTCTimeBox;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskDetailsViewImpl.html")
public class TaskDetailsViewImpl extends Composite implements TaskDetailsPresenter.TaskDetailsView {

    private TaskDetailsPresenter presenter;

    @Inject
    @DataField
    public TextBox userText;

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
    public Label logTextLabel;
     
    @Inject
    @DataField
    public Label taskLogsLabel;
    
    @Inject
    @DataField
    public HTML logTextArea;

    @Inject
    @DataField
    public UTCDateBox dueDate;
    
    
    @Inject
    @DataField
    public UTCTimeBox dueDateTime;

    @Inject
    @DataField
    public Button updateTaskButton;

    @Inject
    @DataField
    public ControlLabel taskStatusLabel;

    @Inject
    @DataField
    public ControlLabel userLabel;

    @Inject
    @DataField
    public ControlLabel dueDateLabel;

    @Inject
    @DataField
    public ControlLabel taskPriorityLabel;


    @Inject
    @DataField
    public ControlLabel taskDescriptionLabel;

    @Inject
    @DataField
    public ControlLabel detailsAccordionLabel;
    
    @Inject
    private PlaceManager placeManager;
    
    private String[] priorities = { "0 - High", "1", "2", "3", "4", "5 - Medium", "6", "7", "8", "9", "10 - Low" };


    @Inject
    private Event<NotificationEvent> notification;
    // Commented out until we add the posibility of adding sub tasks
    // private String[] subTaskStrategies = {"NoAction", "EndParentOnAllSubTasksEnd", "SkipAllSubTasksOnParentSkip"};

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( TaskDetailsPresenter presenter ) {
        this.presenter = presenter;
        
        logTextLabel.setText( constants.Task_Log() );
        // Commented out until we add the posibility of adding sub tasks
        // for (String strategy : subTaskStrategies) {
        // subTaskStrategyListBox.addItem(strategy);
        //
        // }

        for ( String priority : priorities ) {
            taskPriorityListBox.addItem( priority );

        }

        taskStatusLabel.add( new HTMLPanel( constants.Status() ) );
        userLabel.add( new HTMLPanel( constants.User() ) );
        dueDateLabel.add( new HTMLPanel( constants.Due_On() ) );

        taskPriorityLabel.add( new HTMLPanel( constants.Priority() ) );

        taskDescriptionLabel.add( new HTMLPanel( constants.Description() ) );
        detailsAccordionLabel.add( new HTMLPanel( constants.Details()) );
        taskLogsLabel.setText( constants.Logs() );
        taskLogsLabel.setStyleName( "" );
        
        updateTaskButton.setText( constants.Update() );
        
    }

    @EventHandler("updateTaskButton")
    public void updateTaskButton( ClickEvent e ) {
        
        presenter.updateTask( taskDescriptionTextArea.getText(),
                              userText.getText(),
                              // subTaskStrategyListBox.getItemText(subTaskStrategyListBox.getSelectedIndex()),
                              (dueDate.getValue() != null && dueDateTime.getValue() != null)?UTCDateBox.utc2date(dueDate.getValue() + dueDateTime.getValue()):null,
                              taskPriorityListBox.getSelectedIndex() );

    }
    

    @Override
    public TextBox getUserText() {
        return userText;
    }

    @Override
    public TextArea getTaskDescriptionTextArea() {
        return taskDescriptionTextArea;
    }

    @Override
    public ListBox getTaskPriorityListBox() {
        return taskPriorityListBox;
    }

    @Override
    public UTCDateBox getDueDate() {
        return dueDate;
    }

    // Commented out until we add the posibility of adding sub tasks
    // public ListBox getSubTaskStrategyListBox() {
    // return subTaskStrategyListBox;
    // }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    // Commented out until we add the posibility of adding sub tasks
    // public String[] getSubTaskStrategies() {
    // return subTaskStrategies;
    // }

    @Override
    public String[] getPriorities() {
        return priorities;
    }

    @Override
    public TextBox getTaskStatusText() {
        return taskStatusText;
    }

    
    @Override
    public HTML getLogTextArea() {
        return logTextArea;
    }

    @Override
    public UTCTimeBox getDueDateTime() {
        return dueDateTime;
    }

    @Override
    public Button getUpdateTaskButton() {
        return updateTaskButton;
    }

}
