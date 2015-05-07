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

package org.jbpm.console.ng.es.client.editors.quicknewjob;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.client.util.ResizableHeader;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.jbpm.console.ng.gc.client.util.UTCDateBox;
import org.jbpm.console.ng.gc.client.util.UTCTimeBox;

import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;

@Dependent
public class QuickNewJobPopup extends BaseModal {
    interface Binder
            extends
            UiBinder<Widget, QuickNewJobPopup> {

    }

    @UiField
    public TabPanel tabPanel;

    @UiField
    public Tab basicTab;

    @UiField
    public Tab advancedTab;


    @UiField
    public ControlGroup jobNameControlGroup;

    @UiField
    public TextBox jobNameText;

    @UiField
    HelpInline jobNameHelpInline;

    @UiField
    public ControlGroup jobDueDateControlGroup;

    @UiField
    public UTCDateBox jobDueDate;

    @UiField
    public UTCTimeBox jobDueDateTime;

    @UiField
    HelpBlock jobDueDateHelpBlock;

    @UiField
    public ControlGroup jobTypeControlGroup;


    @UiField
    public TextBox jobTypeText;


    @UiField
    HelpInline jobTypeHelpInline;


    @UiField
    public ControlGroup jobRetriesControlGroup;


    @UiField
    public IntegerBox jobRetriesNumber;

    @UiField
    HelpInline jobRetriesHelpInline;


    @UiField
    public Button newParametersButton;

    @UiField
    public com.google.gwt.user.cellview.client.DataGrid<RequestParameterSummary> myParametersGrid;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;

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
            createJob();
        }
    }

    public void init() {

        newParametersButton.setText( Constants.INSTANCE.Add_Parameter() );

        myParametersGrid.setHeight( "200px" );

        // Set the message to display when the table is empty.
        myParametersGrid.setEmptyTableWidget( new Label( Constants.INSTANCE.No_Parameters_added_yet() ) );

        initGridColumns();

        //long now = System.currentTimeMillis() + 120 * 1000;
        //jobDueDate.setEnabled( true );

        //jobDueDate.setValue( now );
        // Two minutes in the future
        //jobDueDateTime.setValue( UTCDateBox.date2utc( new Date( now ) ) );

        //jobRetriesNumber.setText( "0" );

        newParametersButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                addNewParameter();
            }
        } );


    }

    public void cleanForm() {
        tabPanel.selectTab( 0 );
        basicTab.setActive( true );
        advancedTab.setActive(false);

        long now = System.currentTimeMillis() + 120 * 1000;
        jobDueDate.setEnabled( true );
        jobDueDate.setValue( now );
        // Two minutes in the future
        jobDueDateTime.setValue( UTCDateBox.date2utc( new Date( now ) ) );
        jobNameText.setText( "" );
        jobTypeText.setText( "" );
        jobRetriesNumber.setText( "0" );

        dataProvider.getList().clear();
    }

    private void cleanErrorMessages(){
        jobNameControlGroup.setType( ControlGroupType.NONE );
        jobNameHelpInline.setText( "" );
        jobDueDateControlGroup.setType( ControlGroupType.NONE );
        jobDueDateHelpBlock.setText( "" );
        jobTypeControlGroup.setType( ControlGroupType.NONE );
        jobTypeHelpInline.setText( "" );
        jobRetriesControlGroup.setType( ControlGroupType.NONE );
        jobRetriesHelpInline.setText( "" );
    }

    public void closePopup() {
        cleanForm();
        hide();
        super.hide();
    }

    private boolean validateForm() {
        boolean valid = true;
        cleanErrorMessages();
        if ( jobNameText.getText() == null || jobNameText.getText().trim().isEmpty() ) {
            jobNameControlGroup.setType( ControlGroupType.ERROR );
            jobNameHelpInline.setText( Constants.INSTANCE.The_Job_Must_Have_A_Name() );
            valid = false;
        } else {
            jobNameControlGroup.setType( ControlGroupType.SUCCESS );

        }

        if ( UTCDateBox.utc2date( jobDueDate.getValue() + jobDueDateTime.getValue() ).before( new Date() ) ) {
            jobDueDateControlGroup.setType( ControlGroupType.ERROR );
            jobDueDateHelpBlock.setText( Constants.INSTANCE.The_Job_Must_Have_A_Due_Date_In_The_Future() );
            valid = false;
        } else {
            jobDueDateControlGroup.setType( ControlGroupType.SUCCESS );

        }
        if ( jobTypeText.getText() == null || jobTypeText.getText().trim().isEmpty() ) {
            jobTypeControlGroup.setType( ControlGroupType.ERROR );
            jobTypeHelpInline.setText( Constants.INSTANCE.The_Job_Must_Have_A_Type() );
            valid = false;
        } else {
            jobTypeControlGroup.setType( ControlGroupType.SUCCESS );

        }
        if ( jobRetriesNumber.getValue() == null || jobRetriesNumber.getValue() < 0 ) {
            jobRetriesControlGroup.setType( ControlGroupType.ERROR );
            jobRetriesHelpInline.setText( Constants.INSTANCE.The_Job_Must_Have_A_Positive_Number_Of_Reties() );
            valid = false;
        } else {
            jobRetriesControlGroup.setType( ControlGroupType.SUCCESS );

        }
        if ( !valid ) tabPanel.selectTab( 0 );
        return valid;
    }


    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    private class ActionHasCell implements HasCell<RequestParameterSummary, RequestParameterSummary> {

        private ActionCell<RequestParameterSummary> cell;

        public ActionHasCell( final String text,
                              ActionCell.Delegate<RequestParameterSummary> delegate ) {
            cell = new ActionCell<RequestParameterSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, RequestParameterSummary value, SafeHtmlBuilder sb) {
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<a href='javascript:;' class='btn btn-mini' style='margin-right:5px;' title='"+text+"'>"+text+"</a>");
                    sb.append(mysb.toSafeHtml());
                }
            };
        }

        @Override
        public Cell<RequestParameterSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<RequestParameterSummary, RequestParameterSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public RequestParameterSummary getValue( RequestParameterSummary object ) {
            return object;
        }
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
        myParametersGrid.addColumn( paramKeyColumn, new ResizableHeader<RequestParameterSummary>( "Key", myParametersGrid,
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
        myParametersGrid.addColumn( paramValueColumn, new ResizableHeader<RequestParameterSummary>( "Value", myParametersGrid,
                paramValueColumn ) );

        // actions (icons)
        List<HasCell<RequestParameterSummary, ?>> cells = new LinkedList<HasCell<RequestParameterSummary, ?>>();

        cells.add( new ActionHasCell( Constants.INSTANCE.Remove(), new ActionCell.Delegate<RequestParameterSummary>() {
            @Override
            public void execute( RequestParameterSummary parameter ) {
                removeParameter( parameter );
            }
        } ) );

        CompositeCell<RequestParameterSummary> cell = new CompositeCell<RequestParameterSummary>( cells );
        Column<RequestParameterSummary, RequestParameterSummary> actionsColumn = new Column<RequestParameterSummary, RequestParameterSummary>(
                cell ) {
            @Override
            public RequestParameterSummary getValue( RequestParameterSummary object ) {
                return object;
            }
        };
        myParametersGrid.addColumn( actionsColumn, "Actions" );
        myParametersGrid.setColumnWidth( actionsColumn, "70px" );

        dataProvider.addDataDisplay( myParametersGrid );
    }

    public void removeParameter( RequestParameterSummary parameter ) {
        removeRow( parameter );
    }

    public void addNewParameter() {
        addRow( new RequestParameterSummary( "click to edit", "click to edit" ) );
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
        ctx.put( "retries", String.valueOf( numberOfTries ) ); // TODO make legacy keys hard to repeat by accident
        ctx.put( "jobName", jobName ); // TODO make legacy keys hard to repeat by accident

        executorServices.call( new RemoteCallback<Long>() {
            @Override
            public void callback( Long requestId ) {
                cleanForm();
                displayNotification( "Request Schedulled: " + requestId );
                requestCreatedEvent.fire( new RequestChangedEvent( requestId ) );
                closePopup();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message, Throwable throwable ) {

                if ( throwable instanceof IllegalArgumentException ) {
                    jobTypeControlGroup.setType( ControlGroupType.ERROR );
                    jobTypeHelpInline.setText( Constants.INSTANCE.The_Job_Must_Have_A_Valid_Type() );
                    return true;
                }
                errorMessages.setText( throwable.getMessage() );
                errorMessagesGroup.setType( ControlGroupType.ERROR );
                tabPanel.selectTab( 0 );
                //ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        } ).scheduleRequest( jobType, dueDate, ctx );

    }


}
