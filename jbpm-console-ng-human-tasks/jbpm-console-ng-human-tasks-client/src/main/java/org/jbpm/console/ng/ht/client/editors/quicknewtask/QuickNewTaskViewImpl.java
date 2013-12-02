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

package org.jbpm.console.ng.ht.client.editors.quicknewtask;


import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.client.util.UTCDateBox;
import org.jbpm.console.ng.ht.client.util.UTCTimeBox;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "QuickNewTaskViewImpl.html")
public class QuickNewTaskViewImpl extends Composite implements QuickNewTaskPresenter.QuickNewTaskView {

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private QuickNewTaskPresenter presenter;

    @Inject
    @DataField
    public Button addTaskButton;

    @Inject
    @DataField
    public Button addUserButton;
    
    @Inject
    @DataField
    public Button addGroupButton;
    
    @Inject
    public TextBox taskNameText;
    
    @Inject
    @DataField
    public ControlGroup taskNameControlGroup;
    
    
    @Inject
    @DataField
    public ControlGroup userGroupControlGroup;

    @Inject
    @DataField
    public UTCDateBox dueDate;
    
    @Inject
    @DataField
    public UTCTimeBox dueDateTime;
    
    @Inject
    public HelpInline taskNameHelpLabel;
    
    @Inject
    public HelpInline userGroupHelpLabel;

    @Inject
    @DataField
    public ListBox taskPriorityListBox;

    @Inject
    @DataField
    public CheckBox assignToMeTaskCheck;

    @Inject
    @DataField
    public ControlLabel dueDateLabel;

    @Inject
    @DataField
    public ControlLabel advancedLabel;

    @Inject
    public ControlLabel taskNameLabel;

    @Inject
    @DataField
    public ControlLabel taskPriorityLabel;
    
    @Inject
    @DataField
    public FlowPanel usersGroupsControlsPanel;
    
    @Inject
    @DataField
    public ControlLabel assignToMeTaskCheckLabel;

    @Inject
    private Event<NotificationEvent> notification;

    private HandlerRegistration textKeyPressHandler;

    private HandlerRegistration checkKeyPressHandler;

    private Constants constants = GWT.create( Constants.class );
    
    private final List<ControlGroup> userControlGroups = new ArrayList<ControlGroup>();
    
    private final List<ControlGroup> groupControlGroups = new ArrayList<ControlGroup>();
    
    private String[] priorities = { "0 - High", "1", "2", "3", "4", "5 - Medium", "6", "7", "8", "9", "10 - Low" };

    @Override
    public void init( QuickNewTaskPresenter presenter ) {
        this.presenter = presenter;

        Controls taskNameControls = new Controls();
        taskNameControls.add(taskNameText);
        taskNameControls.add(taskNameHelpLabel);
        taskNameControlGroup.add(taskNameLabel);
        taskNameControlGroup.add(taskNameControls);
        
        userGroupControlGroup.add(userGroupHelpLabel);
       

        long day = new Long(24 * 60 * 60 * 1000);
        long now = System.currentTimeMillis();
        dueDate.setEnabled(true);
        
        dueDate.setValue( day + now  );
        
        dueDateTime.setValue(UTCTimeBox.getValueForNextHour());
        
        initializeUserGroupControls();
        refreshUserGroupControls();
        
        KeyPressHandler keyPressHandlerText = new KeyPressHandler() {
            @Override
            public void onKeyPress( KeyPressEvent event ) {
                if ( event.getNativeEvent().getKeyCode() == 13 ) {
                    addTask();
                }
            }
        };
        textKeyPressHandler = taskNameText.addKeyPressHandler( keyPressHandlerText );

        KeyPressHandler keyPressHandlerCheck = new KeyPressHandler() {
            @Override
            public void onKeyPress( KeyPressEvent event ) {
                if ( event.getNativeEvent().getKeyCode() == 13 ) {
                    addTask();
                }
            }
        };
        checkKeyPressHandler = assignToMeTaskCheck.addKeyPressHandler( keyPressHandlerCheck );
        assignToMeTaskCheck.setName("quickTaskCheck");
        
        assignToMeTaskCheckLabel.add(new HTMLPanel(constants.Auto_Assign_To_Me()));
       
        
        taskNameText.setFocus( true );

        for ( String priority : priorities ) {
            taskPriorityListBox.addItem( priority );
        }

        addTaskButton.setText( constants.Create() );
        dueDateLabel.add( new HTMLPanel( constants.Due_On() ) );
        taskPriorityLabel.add( new HTMLPanel( constants.Priority() ) );
        
        addUserButton.setText(constants.Add_User());
        addGroupButton.setText(constants.Add_Group());
        

        advancedLabel.add( new HTMLPanel( constants.Advanced() ) );
        taskNameLabel.add( new HTMLPanel( constants.Task_Name() ) );
        
        

    }
    
    private void initializeUserGroupControls(){
         // Init control group with the logged user
        final ControlGroup loggedUserControlGroup = new ControlGroup();
        
        ControlLabel loggedUserControlLabel = new ControlLabel(constants.User());
        loggedUserControlLabel.setFor("loggedUserTextBox");
        
        Controls loggedUserControls = new Controls();
  
        TextBox userTextBox = new TextBox();
        userTextBox.setName("loggedUserTextBox");
        userTextBox.setText(identity.getName());
        loggedUserControls.add(userTextBox);
        
        Button removeUserButton = new Button();
        removeUserButton.setIcon(IconType.MINUS_SIGN);
        removeUserButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                userControlGroups.remove(loggedUserControlGroup);
                refreshUserGroupControls();
            }
        });
        
        loggedUserControls.add(removeUserButton);
        
        loggedUserControlGroup.add(loggedUserControlLabel);
        loggedUserControlGroup.add(loggedUserControls);
        
        userControlGroups.add(loggedUserControlGroup);
    
        
    
    }
    
    private void refreshUserGroupControls(){
        
        usersGroupsControlsPanel.clear();
        for(ControlGroup userGroupControl : userControlGroups){
            usersGroupsControlsPanel.add(userGroupControl);
        }
        
        for(ControlGroup groupGroupControl : groupControlGroups){
            usersGroupsControlsPanel.add(groupGroupControl);
        }
        
        
    
    }
    
    private void addUserControl(){
    
        final ControlGroup userControlGroup = new ControlGroup();
     
        ControlLabel userControlLabel = new ControlLabel(constants.User());
        userControlLabel.setFor("userTextBox");
        Controls userControls = new Controls();
        TextBox userTextBox = new TextBox();
        userTextBox.setName("userTextBox");
        userControls.add(userTextBox);
        
        Button removeUserButton = new Button();
        removeUserButton.setIcon(IconType.MINUS_SIGN);
        removeUserButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                userControlGroups.remove(userControlGroup);
                refreshUserGroupControls();
            }
        });
        
        userControls.add(removeUserButton);
        
        userControlGroup.add(userControlLabel);
        userControlGroup.add(userControls);
        
        userControlGroups.add(userControlGroup);
        
        
    }
    
    
    private void addGroupControl(){
    
        final ControlGroup groupControlGroup = new ControlGroup();
     
        ControlLabel groupControlLabel = new ControlLabel(constants.Group());
        groupControlLabel.setFor("groupTextBox");
        

        Controls groupControls = new Controls();
  
        TextBox groupTextBox = new TextBox();
        groupTextBox.setName("groupTextBox");
        groupControls.add(groupTextBox);
        
        Button removeGroupButton = new Button();
        removeGroupButton.setIcon(IconType.MINUS_SIGN);
        removeGroupButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                groupControlGroups.remove(groupControlGroup);
                refreshUserGroupControls();
            }
        });
        groupControls.add(removeGroupButton);
        
        groupControlGroup.add(groupControlLabel);
        groupControlGroup.add(groupControls);
        
        groupControlGroups.add(groupControlGroup);
        
        
    }

    @EventHandler("addTaskButton")
    public void addTaskButton( ClickEvent e ) {
        if(userControlGroups.isEmpty() && groupControlGroups.isEmpty()){
            initializeUserGroupControls();
            refreshUserGroupControls();
            userGroupHelpLabel.setText(constants.Provide_User_Or_Group());
            userGroupControlGroup.setType(ControlGroupType.ERROR);
            
        }else{
            addTask();
        }
    }
    
    @EventHandler("addUserButton")
    public void addUserButton( ClickEvent e ) {
       addUserControl();
       refreshUserGroupControls();
    }
    
    
    @EventHandler("addGroupButton")
    public void addGroupButton( ClickEvent e ) {
       addGroupControl();
       refreshUserGroupControls();
    }


    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public TextBox getTaskNameText() {
        return taskNameText;
    }

    private void addTask() {
        if ( !taskNameText.getText().equals( "" ) ) {
            addTaskButton.setEnabled( false );
            checkKeyPressHandler.removeHandler();
            textKeyPressHandler.removeHandler();
            List<String> users = new ArrayList<String>();
            List<String> groups = new ArrayList<String>();
            
            for(ControlGroup userGroupControl : userControlGroups){
                int widgetCount = userGroupControl.getWidgetCount();
                for(int i = 0; i < widgetCount; i ++){
                    Widget widget = userGroupControl.getWidget(i);
                    if(widget instanceof Controls){
                        int controlsCount = ((Controls)widget).getWidgetCount();
                        for(int j = 0; j < controlsCount; j ++){
                            Widget internalWidget = ((Controls)widget).getWidget(j);
                            if(internalWidget instanceof TextBox){
                                String userString = ((TextBox)internalWidget).getText();
                                if(userString != null && !userString.equals("")){
                                    users.add(userString);
                                }
                            }
                        }
                    }
                }
            }
            
            for(ControlGroup groupGroupControl : groupControlGroups){
                int widgetCount = groupGroupControl.getWidgetCount();
                for(int i = 0; i < widgetCount; i ++){
                    Widget widget = groupGroupControl.getWidget(i);
                    if(widget instanceof Controls){
                        int controlsCount = ((Controls)widget).getWidgetCount();
                        for(int j = 0; j < controlsCount; j ++){
                            Widget internalWidget = ((Controls)widget).getWidget(j);
                            if(internalWidget instanceof TextBox){
                                String groupString = ((TextBox)internalWidget).getText();
                                if(groupString != null && !groupString.equals("")){
                                    groups.add(groupString);
                                }
                            }
                        }
                    }
                }
            }
            
            
         
            presenter.addTask( users, groups,
                                taskNameText.getText(), taskPriorityListBox.getSelectedIndex(),
                               assignToMeTaskCheck.getValue(), dueDate.getValue() , dueDateTime.getValue() );
        } else {
            displayNotification( constants.Task_Must_Have_A_Name() );
            taskNameText.setFocus(true);
            taskNameText.setErrorLabel(taskNameHelpLabel);
            taskNameControlGroup.setType(ControlGroupType.ERROR);
            taskNameHelpLabel.setText(constants.Task_Must_Have_A_Name());
        }
    }

    @Override
    public Button getAddTaskButton() {
        return addTaskButton;
    }

}
