/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.InputGroupButton;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.gc.client.util.UTCDateBox;
import org.jbpm.console.ng.gc.client.util.UTCTimeBox;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.events.NewTaskEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class QuickNewTaskPopup extends BaseModal {

    interface Binder
            extends
            UiBinder<Widget, QuickNewTaskPopup> {

    }

    @UiField
    public TabPanel tabPanel;

    @UiField
    public TabListItem basicTab;

    @UiField
    public TabListItem taskformTab;

    @UiField
    public TabListItem advancedTab;

    @UiField
    public TabPane basicTabPane;

    @UiField
    public TabPane taskformTabPane;

    @UiField
    public TabPane advancedTabPane;

    @UiField
    public Button addUserButton;

    @UiField
    public Button addGroupButton;

    @UiField
    public TextBox taskNameText;

    @UiField
    public FormGroup taskNameControlGroup;

    @UiField
    public UTCDateBox dueDate;

    @UiField
    public UTCTimeBox dueDateTime;

    @UiField
    public HelpBlock taskNameHelpLabel;

    @UiField
    public ListBox taskPriorityListBox;

    @UiField
    public FieldSet controlsPanel;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public FormGroup errorMessagesGroup;

    @UiField
    public ListBox taskFormDeploymentId;

    @UiField
    public FormGroup taskFormDeploymentIdControlGroup;

    @UiField
    public HelpBlock taskFormDeploymentIdHelpLabel;

    @UiField
    public ListBox taskFormName;

    @UiField
    public FormGroup taskFormNameControlGroup;

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

    private static Binder uiBinder = GWT.create( Binder.class );

    private HandlerRegistration textKeyPressHandler;

    private final List<FormGroup> userControlGroups = new ArrayList<FormGroup>();

    private final List<FormGroup> groupControlGroups = new ArrayList<FormGroup>();

    private String[] priorities = { "0 - " + Constants.INSTANCE.High(),
            "1", "2", "3", "4", "5 - " + Constants.INSTANCE.Medium(),
            "6", "7", "8", "9", "10 - " + Constants.INSTANCE.Low() };

    private Long processInstanceId = -1L;

    public QuickNewTaskPopup() {
        setTitle( Constants.INSTANCE.New_Task() );

        setBody( uiBinder.createAndBindUi( this ) );

        basicTab.setDataTargetWidget( basicTabPane );
        taskformTab.setDataTargetWidget( taskformTabPane );
        advancedTab.setDataTargetWidget( advancedTabPane );

        dueDate.getDateBox().setContainer( this );

        init();
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( Constants.INSTANCE.Create(),
                          new Command() {
                              @Override
                              public void execute() {
                                  okButton();
                              }
                          }, IconType.PLUS,
                          ButtonType.PRIMARY );

        add( footer );
    }

    public void show( Long processInstanceId ) {
        show();
        this.processInstanceId = processInstanceId;

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

        basicTab.showTab();
        basicTab.setActive( true );
        advancedTab.setActive( false );

        userControlGroups.clear();
        groupControlGroups.clear();
        clearErrorMessages();
        taskNameText.setValue( "" );

        long day = new Long( 24 * 60 * 60 * 1000 );
        long now = System.currentTimeMillis();
        dueDate.setEnabled( true );
        dueDate.setValue( day + now );
        dueDateTime.setValue( UTCTimeBox.getValueForNextHour() );

        setSelectedValue( taskPriorityListBox, "0" );

        addUserControl( true );
        refreshUserGroupControls();
        taskNameText.setFocus( true );

        taskFormDeploymentId.clear();
        setSelectedValue( taskFormDeploymentId, "" );

        taskFormName.clear();
        setSelectedValue( taskFormName, "" );

        this.processInstanceId = -1L;
    }

    public void closePopup() {
        cleanForm();
        hide();
    }

    protected boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();

        if ( taskNameText.getText() != null && taskNameText.getText().trim().length() == 0 ) {
            basicTab.showTab();
            taskNameText.setFocus( true );

            taskNameHelpLabel.setText( Constants.INSTANCE.Task_Must_Have_A_Name() );
            taskNameControlGroup.setValidationState( ValidationState.ERROR );
            valid = false;
        } else {
            taskNameControlGroup.setValidationState( ValidationState.SUCCESS );
        }
        return valid;
    }

    private void refreshUserGroupControls() {
        final List<Widget> widgets2Remove = new ArrayList<Widget>( controlsPanel.getWidgetCount() );
        for ( int i = 0; i < controlsPanel.getWidgetCount(); i++ ) {
            if ( "yes".equals( controlsPanel.getWidget( i ).getElement().getPropertyString( "isDynamic" ) ) ) {
                widgets2Remove.add( controlsPanel.getWidget( i ) );
            }
        }

        for ( final Widget widget : widgets2Remove ) {
            controlsPanel.remove( widget );
        }

        widgets2Remove.clear();

        for ( FormGroup userGroupControl : userControlGroups ) {
            controlsPanel.add( userGroupControl );
        }

        for ( FormGroup groupGroupControl : groupControlGroups ) {
            controlsPanel.add( groupGroupControl );
        }

    }

    private void addUserControl( final Boolean addCurrentUser ) {
        final FormGroup userControlGroup = new FormGroup();
        userControlGroup.getElement().setPropertyString( "isDynamic", "yes" );
        userControlGroup.add( new FormLabel() {{
            setText( Constants.INSTANCE.User() );
            addStyleName( ColumnSize.MD_3.getCssName() );
            setFor( "userTextBox" );
        }} );
        userControlGroup.add( new Column( ColumnSize.MD_9 ) {{
            add( new InputGroup() {{
                add( new TextBox() {{
                    setName( "userTextBox" );
                    if ( addCurrentUser ) {
                        setText( identity.getIdentifier() );
                    }
                }} );
                add( new InputGroupButton() {{
                    add( new Button() {{
                        setIcon( IconType.TRASH );
                        setType( ButtonType.DANGER );
                        setTitle( Constants.INSTANCE.Remove_User() );
                        addClickHandler( new ClickHandler() {
                            @Override
                            public void onClick( ClickEvent event ) {
                                userControlGroups.remove( userControlGroup );
                                refreshUserGroupControls();
                            }
                        } );
                    }} );
                }} );
            }} );
        }} );

        userControlGroups.add( userControlGroup );
    }

    private void addGroupControl() {
        final FormGroup groupControlGroup = new FormGroup();
        groupControlGroup.getElement().setPropertyString( "isDynamic", "yes" );
        groupControlGroup.add( new FormLabel() {{
            setText( Constants.INSTANCE.Group() );
            addStyleName( ColumnSize.MD_3.getCssName() );
            setFor( "groupTextBox" );
        }} );
        groupControlGroup.add( new Column( ColumnSize.MD_9 ) {{
            add( new InputGroup() {{
                add( new TextBox() {{
                    setName( "groupTextBox" );
                }} );
                add( new InputGroupButton() {{
                    add( new Button() {{
                        setIcon( IconType.MINUS );
                        setTitle( Constants.INSTANCE.Remove_User() );
                        addClickHandler( new ClickHandler() {
                            @Override
                            public void onClick( ClickEvent event ) {
                                groupControlGroups.remove( groupControlGroup );
                                refreshUserGroupControls();
                            }
                        } );
                    }} );
                }} );
            }} );
        }} );

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
            errorMessagesGroup.setValidationState( ValidationState.ERROR );
            advancedTab.showTab();
        } else {
            addTask( users, groups,
                     taskNameText.getText(), taskPriorityListBox.getSelectedIndex(),
                     dueDate.getValue(), dueDateTime.getValue(), taskFormName.getSelectedValue(),
                     taskFormDeploymentId.getSelectedValue(), processInstanceId );
        }

    }

    public void addTask( final List<String> users,
                         List<String> groups,
                         final String taskName,
                         int priority,
                         long dueDate,
                         long dueDateTime,
                         String taskFormName,
                         String deploymentId,
                         Long processInstanceId
                       ) {
        Date due = UTCDateBox.utc2date( dueDate + dueDateTime );

        boolean start = false;
        boolean claim = false;

        if ( users != null && !users.isEmpty() ) {
            if ( users.contains( identity.getIdentifier() ) ) {    // Current user in introduced users
                start = true;
            }
        }

/*

        taskOperationsService.call( new RemoteCallback<Long>() {
            @Override
            public void callback( Long taskId ) {
                cleanForm();
                refreshNewTask( taskId, taskName, Constants.INSTANCE.TaskCreatedWithId( String.valueOf( taskId ) ) );
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                errorMessages.setText( throwable.getMessage() );
                errorMessagesGroup.setValidationState( ValidationState.ERROR );
                //ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).addQuickTask( taskName, priority, due, users, groups, identity.getIdentifier(), start, claim,
                          taskFormName, deploymentId, processInstanceId );
*/
    }

    private void refreshNewTask( Long taskId,
                                 String taskName,
                                 String msj ) {
        displayNotification( msj );
        newTaskEvent.fire( new NewTaskEvent( taskId, taskName ) );
        closePopup();
    }

    public List<String> getTextBoxValues( List<FormGroup> controlGroups ) {
        List<String> filledValues = new ArrayList<String>();
        for ( final FormGroup userGroupControl : controlGroups ) {
            getTextBoxValues( userGroupControl, filledValues );
        }
        return filledValues;
    }

    private void getTextBoxValues( final Widget widget,
                                  final List<String> values ) {
        if ( widget instanceof ComplexPanel ) {
            for ( Widget child : (ComplexPanel) widget ) {
                getTextBoxValues( child, values );
            }
        } else if ( widget instanceof TextBox ) {
            final String value = ( (TextBox) widget ).getText().trim();
            if ( !value.isEmpty() ) {
                values.add( value );
            }
        }
    }

    private void clearErrorMessages() {
        errorMessages.setText( "" );
        taskNameHelpLabel.setText( "" );
        taskNameControlGroup.setValidationState( ValidationState.NONE );
    }

    void setSelectedValue( final ListBox listbox,
                           final String value ) {
        for ( int i = 0; i < listbox.getItemCount(); i++ ) {
            if ( listbox.getValue( i ).equals( value ) ) {
                listbox.setSelectedIndex( i );
                return;
            }
        }
    }

}
