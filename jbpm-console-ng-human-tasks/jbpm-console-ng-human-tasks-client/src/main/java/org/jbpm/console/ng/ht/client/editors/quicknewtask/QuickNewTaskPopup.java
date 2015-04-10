/*
 * Copyright 2014 JBoss Inc
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

package org.jbpm.console.ng.ht.client.editors.quicknewtask;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.gc.client.util.UTCDateBox;
import org.jbpm.console.ng.gc.client.util.UTCTimeBox;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.events.NewTaskEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Dependent
public class QuickNewTaskPopup extends BaseModal {
    interface Binder
            extends
            UiBinder<Widget, QuickNewTaskPopup> {

    }

    @UiField
    public TabPanel tabPanel;

    @UiField
    public Tab basicTab;

    @UiField
    public Tab advancedTab;

    @UiField
    public Button addUserButton;

    @UiField
    public Button addGroupButton;

    @UiField
    public TextBox taskNameText;

    @UiField
    public ControlGroup taskNameControlGroup;

    @UiField
    public UTCDateBox dueDate;

    @UiField
    public UTCTimeBox dueDateTime;

    @UiField
    public HelpBlock taskNameHelpLabel;

    @UiField
    public ListBox taskPriorityListBox;

    @UiField
    public FlowPanel usersGroupsControlsPanel;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;


    @UiField
    public TextBox taskFormDeploymentIdText;

    @UiField
    public ControlGroup taskFormDeploymentIdControlGroup;

    @UiField
    public HelpBlock taskFormDeploymentIdHelpLabel;

    @UiField
    public TextBox taskFormNameText;

    @UiField
    public ControlGroup taskFormNameControlGroup;

    @UiField
    public HelpBlock taskFormNameHelpLabel;

    @Inject
    User identity;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<TaskRefreshedEvent> taskRefreshed;

    @Inject
    private Event<NewTaskEvent> newTaskEvent;

    @Inject
    Caller<TaskOperationsService> taskOperationsService;

    private static Binder uiBinder = GWT.create( Binder.class );


    private HandlerRegistration textKeyPressHandler;

    private final List<ControlGroup> userControlGroups = new ArrayList<ControlGroup>();

    private final List<ControlGroup> groupControlGroups = new ArrayList<ControlGroup>();

    private String[] priorities = { "0 - " + Constants.INSTANCE.High(),
            "1", "2", "3", "4", "5 - " + Constants.INSTANCE.Medium(),
            "6", "7", "8", "9", "10 - " + Constants.INSTANCE.Low() };

    public QuickNewTaskPopup() {
        setTitle( Constants.INSTANCE.New_Task() );

        add( uiBinder.createAndBindUi( this ) );
        init();
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( Constants.INSTANCE.Create(),
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY );

        add( footer );
    }

    public void show() {
        cleanForm();
        super.show();
    }

    private void okButton() {
        if ( validateForm() ) {
            addTask();
        }
    }

    public void init() {

        long day = new Long( 24 * 60 * 60 * 1000 );
        long now = System.currentTimeMillis();
        dueDate.setEnabled( true );

        dueDate.setValue( day + now );

        dueDateTime.setValue( UTCTimeBox.getValueForNextHour() );

        //initializeUserGroupControls();
        refreshUserGroupControls();

        KeyPressHandler keyPressHandlerText = new KeyPressHandler() {
            @Override
            public void onKeyPress( KeyPressEvent event ) {
                clearErrorMessages();
                if ( event.getNativeEvent().getKeyCode() == 13 ) {
                    addTask();
                }
            }
        };
        textKeyPressHandler = taskNameText.addKeyPressHandler( keyPressHandlerText );

        taskNameText.setFocus( true );

        for ( String priority : priorities ) {
            taskPriorityListBox.addItem( priority );
        }
        addUserButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                addUserControl( false );
                refreshUserGroupControls();
            }
        } );

        addGroupButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                addGroupControl();
                refreshUserGroupControls();
            }
        } );
    }

    public void cleanForm() {
        tabPanel.selectTab( 0 );
        basicTab.setActive( true );
        advancedTab.setActive(false);

        userControlGroups.clear();
        groupControlGroups.clear();
        clearErrorMessages();
        taskNameText.setValue( "" );

        long day = new Long( 24 * 60 * 60 * 1000 );
        long now = System.currentTimeMillis();
        dueDate.setEnabled( true );
        dueDate.setValue( day + now );
        dueDateTime.setValue( UTCTimeBox.getValueForNextHour() );

        taskFormNameText.setValue( "" );
        taskFormDeploymentIdText.setValue( "" );

        taskPriorityListBox.setSelectedValue( "0" );

        addUserControl( true );
        refreshUserGroupControls();
        taskNameText.setFocus( true );
    }


    public void closePopup() {
        cleanForm();
        hide();
        super.hide();
    }

    private boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();

        if ( taskNameText.getText() != null && taskNameText.getText().trim().length() == 0 ) {
            tabPanel.selectTab( 0 );
            taskNameText.setFocus( true );
            taskNameText.setErrorLabel( taskNameHelpLabel );

            errorMessages.setText( Constants.INSTANCE.Task_Must_Have_A_Name() );
            errorMessagesGroup.setType( ControlGroupType.ERROR );
            taskNameHelpLabel.setText( Constants.INSTANCE.Task_Must_Have_A_Name() );
            taskNameControlGroup.setType( ControlGroupType.ERROR );
            valid = false;
        } else {
            taskNameControlGroup.setType( ControlGroupType.SUCCESS );
        }
        return valid;
    }

    private void refreshUserGroupControls() {

        usersGroupsControlsPanel.clear();
        for ( ControlGroup userGroupControl : userControlGroups ) {
            usersGroupsControlsPanel.add( userGroupControl );
        }

        for ( ControlGroup groupGroupControl : groupControlGroups ) {
            usersGroupsControlsPanel.add( groupGroupControl );
        }


    }

    private void addUserControl( Boolean addCurrentUser ) {


        final ControlGroup userControlGroup = new ControlGroup();

        ControlLabel userControlLabel = new ControlLabel( Constants.INSTANCE.User() );
        userControlLabel.setFor( "userTextBox" );

        TextBox userTextBox = new TextBox();
        userTextBox.setName( "userTextBox" );
        if ( addCurrentUser ) userTextBox.setText( identity.getIdentifier() );

        Button removeUserButton = new Button();
        removeUserButton.setIcon( IconType.MINUS_SIGN );
        removeUserButton.setTitle( Constants.INSTANCE.Remove_User() );
        removeUserButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                userControlGroups.remove( userControlGroup );
                refreshUserGroupControls();
            }
        } );

        userControlGroup.add( createHorizontalPanelForUserAndGroups( userControlLabel, userTextBox, removeUserButton ) );
        userControlGroups.add( userControlGroup );

    }


    private void addGroupControl() {

        final ControlGroup groupControlGroup = new ControlGroup();

        ControlLabel groupControlLabel = new ControlLabel( Constants.INSTANCE.Group() );
        groupControlLabel.setFor( "groupTextBox" );

        TextBox groupTextBox = new TextBox();
        groupTextBox.setName( "groupTextBox" );


        Button removeGroupButton = new Button();
        removeGroupButton.setIcon( IconType.MINUS_SIGN );
        removeGroupButton.setTitle( Constants.INSTANCE.Remove_Group() );
        removeGroupButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                groupControlGroups.remove( groupControlGroup );
                refreshUserGroupControls();
            }
        } );

        groupControlGroup.add( createHorizontalPanelForUserAndGroups( groupControlLabel, groupTextBox, removeGroupButton ) );
        groupControlGroups.add( groupControlGroup );

    }

    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    private void addTask() {
        textKeyPressHandler.removeHandler();
        List<String> users = getTextBoxValues( userControlGroups );
        List<String> groups = getTextBoxValues( groupControlGroups );

        if ( users.size() == 0 && groups.size() == 0 ) {
            userControlGroups.clear();
            groupControlGroups.clear();
            addUserControl( true );
            refreshUserGroupControls();
            errorMessages.setText( Constants.INSTANCE.Provide_User_Or_Group() );
            errorMessagesGroup.setType( ControlGroupType.ERROR );
            tabPanel.selectTab( 1 );
        } else {
            addTask( users, groups,
                    taskNameText.getText(), taskPriorityListBox.getSelectedIndex(),
                    dueDate.getValue(), dueDateTime.getValue(), taskFormNameText.getValue(),
                    taskFormDeploymentIdText.getValue() );
        }

    }


    public void addTask( final List<String> users, List<String> groups,
                         final String taskName,
                         int priority,
                         long dueDate, long dueDateTime,
                         String taskFormName,
                         String deploymentId
    ) {
        Date due = UTCDateBox.utc2date( dueDate + dueDateTime );

        boolean start = false;
        boolean claim = false;

        if ( users != null && !users.isEmpty() ) {
            if ( users.contains( identity.getIdentifier() ) ) {    // Current user in introduced users
                start = true;
            }
        }

        taskOperationsService.call( new RemoteCallback<Long>() {
            @Override
            public void callback( Long taskId ) {
                cleanForm();
                refreshNewTask( taskId, taskName, Constants.INSTANCE.TaskCreatedWithId( String.valueOf( taskId ) ) );
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message, Throwable throwable ) {
                errorMessages.setText( throwable.getMessage() );
                errorMessagesGroup.setType( ControlGroupType.ERROR );
                //ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).addQuickTask( taskName, priority, due, users, groups, identity.getIdentifier(), start, claim,
                taskFormName, deploymentId );


    }

    private void refreshNewTask( Long taskId, String taskName, String msj ) {
        displayNotification( msj );
        newTaskEvent.fire( new NewTaskEvent( taskId, taskName ) );
        closePopup();
    }

   public List<String> getTextBoxValues( List<ControlGroup> controlGroups ) {
        List<String> filledValues = new ArrayList<String>();
        String textBoxValue = "";
        for ( ControlGroup userGroupControl : controlGroups ) {
            int widgetCount = userGroupControl.getWidgetCount();
            for ( int i = 0; i < widgetCount; i++ ) {
                Widget widget = userGroupControl.getWidget( i );
                textBoxValue = ( ( TextBox ) ( ( ( HorizontalPanel ) widget ).getWidget( 1 ) ) ).getValue();
                if ( textBoxValue != null && textBoxValue.trim().length() > 0 ) {
                    filledValues.add( textBoxValue );
                }
            }
        }
        return filledValues;
    }

    public HorizontalPanel createHorizontalPanelForUserAndGroups( ControlLabel controlLabel, TextBox textBox, Button removeButton ) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setWidth( "90%" );

        horizontalPanel.add( controlLabel );
        horizontalPanel.setCellWidth( controlLabel, "10%" );
        horizontalPanel.setCellHorizontalAlignment( controlLabel, HasHorizontalAlignment.ALIGN_RIGHT );

        horizontalPanel.add( textBox );
        horizontalPanel.setCellWidth( textBox, "70%" );
        horizontalPanel.setCellHorizontalAlignment( textBox, HasHorizontalAlignment.ALIGN_CENTER );

        horizontalPanel.add( removeButton );
        horizontalPanel.setCellWidth( removeButton, "20%" );
        horizontalPanel.setCellHorizontalAlignment( removeButton, HasHorizontalAlignment.ALIGN_CENTER );

        return horizontalPanel;
    }

    private void clearErrorMessages(){
        errorMessages.setText( "" );
        taskNameHelpLabel.setText( "" );
        taskNameControlGroup.setType( ControlGroupType.NONE );
    }

}
