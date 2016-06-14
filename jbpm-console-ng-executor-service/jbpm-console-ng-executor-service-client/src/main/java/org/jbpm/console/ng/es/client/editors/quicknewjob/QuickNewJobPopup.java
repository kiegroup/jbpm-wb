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

package org.jbpm.console.ng.es.client.editors.quicknewjob;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.IntegerBox;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.es.client.i18n.Constants;

import org.jbpm.console.ng.es.model.RequestDataSetConstants;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.jbpm.console.ng.gc.client.util.UTCDateBox;
import org.jbpm.console.ng.gc.client.util.UTCTimeBox;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.ext.widgets.common.client.tables.ResizableHeader;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class QuickNewJobPopup extends BaseModal {

    interface Binder
            extends
            UiBinder<Widget, QuickNewJobPopup> {

    }
    private final Constants constants = Constants.INSTANCE;

    @UiField
    public TabPanel tabPanel;

    @UiField
    public TabListItem basicTab;

    @UiField
    public TabListItem advancedTab;

    @UiField
    public TabPane basicTabPane;

    @UiField
    public TabPane advancedTabPane;

    @UiField
    public FormGroup jobNameControlGroup;

    @UiField
    public TextBox jobNameText;

    @UiField
    HelpBlock jobNameHelpInline;

    @UiField
    public FormGroup jobDueDateControlGroup;

    @UiField
    public UTCDateBox jobDueDate;

    @UiField
    public UTCTimeBox jobDueDateTime;

    @UiField
    HelpBlock jobDueDateHelpBlock;

    @UiField
    public FormGroup jobTypeControlGroup;

    @UiField
    public TextBox jobTypeText;

    @UiField
    HelpBlock jobTypeHelpInline;

    @UiField
    public FormGroup jobRetriesControlGroup;

    @UiField
    public IntegerBox jobRetriesNumber;

    @UiField
    HelpBlock jobRetriesHelpInline;

    @UiField
    public Button newParametersButton;

    @UiField
    public DataGrid<RequestParameterSummary> myParametersGrid;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public FormGroup errorMessagesGroup;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;

    @Inject
    private Event<RequestChangedEvent> requestCreatedEvent;

    private ListDataProvider<RequestParameterSummary> dataProvider = new ListDataProvider<RequestParameterSummary>();

    private static Binder uiBinder = GWT.create( Binder.class );

    public QuickNewJobPopup() {
        setTitle( Constants.INSTANCE.New_Job() );

        setBody( uiBinder.createAndBindUi( QuickNewJobPopup.this ) );

        basicTab.setDataTargetWidget( basicTabPane );
        advancedTab.setDataTargetWidget( advancedTabPane );

        jobDueDate.getDateBox().setContainer( this );

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

    public void setExecutorService(Caller<ExecutorServiceEntryPoint> executorServices){
        this.executorServices = executorServices;
    }

    public void show() {
        cleanForm();
        super.show();
    }

    private void okButton() {
        if ( validateForm() ) {
            createJob();
        }
    }

    public void init() {

        newParametersButton.setText( constants.Add_Parameter() );

        myParametersGrid.setHeight( "200px" );

        // Set the message to display when the table is empty.
        myParametersGrid.setEmptyTableWidget( new Label( constants.No_Parameters_added_yet() ) );

        initGridColumns();

        newParametersButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                addNewParameter();
            }
        } );

    }

    public void cleanForm() {
        basicTab.setActive( true );
        basicTabPane.setActive( true );
        advancedTab.setActive( false );
        advancedTabPane.setActive( false );
        basicTab.showTab();

        long nextDueDate = System.currentTimeMillis() + 1800 * 1000;
        // 30' minutes in the future
        jobDueDate.setEnabled( true );
        jobDueDate.setValue( nextDueDate );
        jobDueDateTime.setValue( UTCDateBox.date2utc( new Date( nextDueDate ) ) );
        jobNameText.setText( "" );
        jobTypeText.setText( "" );
        jobRetriesNumber.setText( "0" );

        dataProvider.getList().clear();
    }

    private void cleanErrorMessages() {
        jobNameControlGroup.setValidationState( ValidationState.NONE );
        jobNameHelpInline.setText( "" );
        jobDueDateControlGroup.setValidationState( ValidationState.NONE );
        jobDueDateHelpBlock.setText( "" );
        jobTypeControlGroup.setValidationState( ValidationState.NONE );
        jobTypeHelpInline.setText( "" );
        jobRetriesControlGroup.setValidationState( ValidationState.NONE );
        jobRetriesHelpInline.setText( "" );
    }

    public void closePopup() {
        cleanForm();
        hide();
    }

    boolean validateForm() {
        boolean valid = true;
        cleanErrorMessages();
        if ( jobNameText.getText() == null || jobNameText.getText().trim().isEmpty() ) {
            jobNameControlGroup.setValidationState( ValidationState.ERROR );
            jobNameHelpInline.setText( Constants.INSTANCE.The_Job_Must_Have_A_BusinessKey() );
            valid = false;
        } else {
            jobNameControlGroup.setValidationState( ValidationState.SUCCESS );

        }

        if ( UTCDateBox.utc2date( jobDueDate.getValue() + jobDueDateTime.getValue() ).before( new Date() ) ) {
            jobDueDateControlGroup.setValidationState( ValidationState.ERROR );
            jobDueDateHelpBlock.setText( Constants.INSTANCE.The_Job_Must_Have_A_Due_Date_In_The_Future() );
            valid = false;
        } else {
            jobDueDateControlGroup.setValidationState( ValidationState.SUCCESS );

        }
        if ( jobTypeText.getText() == null || jobTypeText.getText().trim().isEmpty() ) {
            jobTypeControlGroup.setValidationState( ValidationState.ERROR );
            jobTypeHelpInline.setText( Constants.INSTANCE.The_Job_Must_Have_A_Type() );
            valid = false;
        } else {
            jobTypeControlGroup.setValidationState( ValidationState.SUCCESS );

        }
        if ( jobRetriesNumber.getValue() == null || jobRetriesNumber.getValue() < 0 ) {
            jobRetriesControlGroup.setValidationState( ValidationState.ERROR );
            jobRetriesHelpInline.setText( Constants.INSTANCE.The_Job_Must_Have_A_Positive_Number_Of_Reties() );
            valid = false;
        } else {
            jobRetriesControlGroup.setValidationState( ValidationState.SUCCESS );

        }
        if ( !valid ) {
            basicTab.showTab();
        }
        return valid;
    }

    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    public void makeRowEditable( RequestParameterSummary parameter ) {
        myParametersGrid.getSelectionModel().setSelected( parameter, true );
    }

    public void removeRow( RequestParameterSummary parameter ) {
        dataProvider.getList().remove( parameter );
    }

    public void addRow( RequestParameterSummary parameter ) {
        dataProvider.getList().add( parameter );
    }

    private void initGridColumns() {
        Column<RequestParameterSummary, String> paramKeyColumn = new Column<RequestParameterSummary, String>( new EditTextCell() ) {
            @Override
            public String getValue( RequestParameterSummary rowObject ) {
                return rowObject.getKey();
            }
        };
        paramKeyColumn.setFieldUpdater( new FieldUpdater<RequestParameterSummary, String>() {
            @Override
            public void update( int index,
                                RequestParameterSummary object,
                                String value ) {
                object.setKey( value );
                dataProvider.getList().set( index, object );
            }
        } );
        myParametersGrid.addColumn( paramKeyColumn, new ResizableHeader<RequestParameterSummary>( constants.Key(), myParametersGrid,
                                                                                                  paramKeyColumn ) );

        Column<RequestParameterSummary, String> paramValueColumn = new Column<RequestParameterSummary, String>(
                new EditTextCell() ) {
            @Override
            public String getValue( RequestParameterSummary rowObject ) {
                return rowObject.getValue();
            }
        };
        paramValueColumn.setFieldUpdater( new FieldUpdater<RequestParameterSummary, String>() {
            @Override
            public void update( int index,
                                RequestParameterSummary object,
                                String value ) {
                object.setValue( value );
                dataProvider.getList().set( index, object );
            }
        } );
        myParametersGrid.addColumn( paramValueColumn, new ResizableHeader<RequestParameterSummary>( constants.Value(), myParametersGrid,
                                                                                                    paramValueColumn ) );

        // actions (icons)
        final ButtonCell buttonCell = new ButtonCell( ButtonType.DANGER, IconType.TRASH );
        final Column<RequestParameterSummary, String> actionsColumn = new Column<RequestParameterSummary, String>( buttonCell ) {
            @Override
            public String getValue( final RequestParameterSummary object ) {
                return Constants.INSTANCE.Remove();
            }
        };
        actionsColumn.setFieldUpdater( new FieldUpdater<RequestParameterSummary, String>() {
            @Override
            public void update( int index, RequestParameterSummary object, String value ) {
                removeParameter( object );
            }
        });
        actionsColumn.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );

        myParametersGrid.addColumn( actionsColumn, constants.Actions() );
        myParametersGrid.setColumnWidth( actionsColumn, 90, Style.Unit.PX );

        dataProvider.addDataDisplay( myParametersGrid );
    }

    public void removeParameter( RequestParameterSummary parameter ) {
        removeRow( parameter );
    }

    public void addNewParameter() {
        addRow( new RequestParameterSummary(constants.ClickToEdit(),constants.ClickToEdit()) );
    }

    public void createJob() {
        createJob( jobNameText.getText(), UTCDateBox.utc2date( jobDueDate.getValue() + jobDueDateTime.getValue() ),
                jobTypeText.getText(), jobRetriesNumber.getValue(), dataProvider.getList() );
    }

    public void createJob( String jobName,
                           Date dueDate,
                           String jobType,
                           Integer numberOfTries,
                           List<RequestParameterSummary> parameters ) {

        Map<String, String> ctx = new HashMap<String, String>();
        if ( parameters != null ) {
            for ( RequestParameterSummary param : parameters ) {
                ctx.put( param.getKey(), param.getValue() );
            }
        }
        ctx.put(RequestDataSetConstants.COLUMN_RETRIES, String.valueOf( numberOfTries ) ); // TODO make legacy keys hard to repeat by accident
        ctx.put(RequestDataSetConstants.COLUMN_BUSINESSKEY, jobName ); // TODO make legacy keys hard to repeat by accident

        executorServices.call( new RemoteCallback<Long>() {
            @Override
            public void callback( Long requestId ) {
                cleanForm();
                displayNotification( constants.RequestScheduled(requestId) );
                requestCreatedEvent.fire( new RequestChangedEvent( requestId ) );
                closePopup();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {

                if ( throwable instanceof IllegalArgumentException ) {
                    jobTypeControlGroup.setValidationState( ValidationState.ERROR );
                    jobTypeHelpInline.setText( Constants.INSTANCE.The_Job_Must_Have_A_Valid_Type() );
                    return true;
                }
                errorMessages.setText( throwable.getMessage() );
                errorMessagesGroup.setValidationState( ValidationState.ERROR );
                basicTab.showTab();
                return true;
            }
        } ).scheduleRequest( jobType, dueDate, ctx );

    }

}
