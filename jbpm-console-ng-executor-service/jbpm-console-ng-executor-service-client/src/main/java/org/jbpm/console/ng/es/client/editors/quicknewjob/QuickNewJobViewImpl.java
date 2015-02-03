/*
 * Copyright 2013 JBoss by Red Hat.
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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.IntegerBox;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.view.client.ListDataProvider;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.client.util.ResizableHeader;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.gc.client.util.UTCDateBox;
import org.jbpm.console.ng.gc.client.util.UTCTimeBox;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent

public class QuickNewJobViewImpl extends Composite implements QuickNewJobPresenter.QuickNewJobView {

    interface QuickNewJobViewWidgetBinder
            extends
            UiBinder<Widget, QuickNewJobViewImpl> {

    }
    
    private Constants constants = GWT.create( Constants.class );

    private QuickNewJobViewWidgetBinder uiBinder = GWT.create(QuickNewJobViewWidgetBinder.class);
    
    
    
    @UiField
    public ControlGroup jobNameControlGroup;
    
    @UiField
    public TextBox jobNameText;
    
    
    
    @UiField
    HelpInline jobNameHelpInline;

    @UiField
    public ControlGroup jobDueDateControlGroup;
    
    
    @UiField
    public HorizontalPanel jobDueSimplePanel;
    
    @Inject
    public UTCDateBox jobDueDate;

    @Inject
    public UTCTimeBox jobDueDateTime;
    
    
    
    @UiField
    HelpInline jobDueDateHelpInline;

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
    public Button createButton;

    @UiField
    public DataGrid<RequestParameterSummary> myParametersGrid;

    @Inject
    Event<NotificationEvent> notificationEvents;
    private ListDataProvider<RequestParameterSummary> dataProvider = new ListDataProvider<RequestParameterSummary>();

    private QuickNewJobPresenter presenter;

    @Override
    public void init( QuickNewJobPresenter p ) {
        
        
        this.presenter = p;
        
        initWidget(uiBinder.createAndBindUi( this ) );
        
        newParametersButton.setText(constants.Add_Parameter());
        createButton.setText(constants.Create());
        
        myParametersGrid.setHeight( "200px" );

        // Set the message to display when the table is empty.
        myParametersGrid.setEmptyTableWidget( new Label( constants.No_Parameters_added_yet() ) );

        initGridColumns();

        long now = System.currentTimeMillis() + 120 * 1000;
        jobDueDate.setEnabled(true);

        jobDueDate.setValue( now  );
        // Two minutes in the future
        jobDueDateTime.setValue(UTCDateBox.date2utc(new Date(now )));
        
        jobRetriesNumber.setText("0");
        
        jobDueSimplePanel.add(jobDueDate);
        jobDueSimplePanel.add(jobDueDateTime);
        
        
        
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

        cells.add( new ActionHasCell( "Remove", new Delegate<RequestParameterSummary>() {
            @Override
            public void execute( RequestParameterSummary parameter ) {
                presenter.removeParameter( parameter );
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

    @UiHandler("newParametersButton")
    public void newParametersButton( ClickEvent e ) {
        presenter.addNewParameter();
    }

    @UiHandler("createButton")
    public void createButton( ClickEvent e ) {
        
        if(jobNameText.getText() == null || jobNameText.getText().trim().isEmpty()){
            jobNameControlGroup.setType(ControlGroupType.ERROR);
            jobNameHelpInline.setText(constants.The_Job_Must_Have_A_Name());
            return;
        }else{
            jobNameControlGroup.setType(ControlGroupType.SUCCESS);
            
        }
            
        if(UTCDateBox.utc2date(jobDueDate.getValue() + jobDueDateTime.getValue()).before(new Date())){
            jobDueDateControlGroup.setType(ControlGroupType.ERROR);
            jobDueDateHelpInline.setText(constants.The_Job_Must_Have_A_Due_Date_In_The_Future());
            return;
        }else {
            jobDueDateControlGroup.setType(ControlGroupType.SUCCESS);
            
        }
        if(jobTypeText.getText() == null || jobTypeText.getText().trim().isEmpty()){
            jobTypeControlGroup.setType(ControlGroupType.ERROR);
            jobTypeHelpInline.setText(constants.The_Job_Must_Have_A_Type());
            return;
        }else{
            jobTypeControlGroup.setType(ControlGroupType.SUCCESS);
            
        } 
        if(jobRetriesNumber.getValue() == null || jobRetriesNumber.getValue() < 0){
            jobRetriesControlGroup.setType(ControlGroupType.ERROR);
            jobRetriesHelpInline.setText(constants.The_Job_Must_Have_A_Positive_Number_Of_Reties());
            return;
        }else{
            jobRetriesControlGroup.setType(ControlGroupType.SUCCESS);
            
        }
        presenter.createJob(jobNameText.getText(), UTCDateBox.utc2date(jobDueDate.getValue() + jobDueDateTime.getValue()),
                jobTypeText.getText(), jobRetriesNumber.getValue(), dataProvider.getList() );
        
    }

    private class ActionHasCell implements HasCell<RequestParameterSummary, RequestParameterSummary> {

        private ActionCell<RequestParameterSummary> cell;

        public ActionHasCell( String text,
                              Delegate<RequestParameterSummary> delegate ) {
            cell = new ActionCell<RequestParameterSummary>( text, delegate );
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

    @Override
    public void removeRow( RequestParameterSummary parameter ) {
        dataProvider.getList().remove( parameter );
    }

    @Override
    public void addRow( RequestParameterSummary parameter ) {
        dataProvider.getList().add( parameter );
    }

    @Override
    public void displayNotification( String notification ) {
        notificationEvents.fire( new NotificationEvent( notification ) );
    }

    @Override
    public Focusable getJobNameText() {
        return jobNameText;
    }

}
