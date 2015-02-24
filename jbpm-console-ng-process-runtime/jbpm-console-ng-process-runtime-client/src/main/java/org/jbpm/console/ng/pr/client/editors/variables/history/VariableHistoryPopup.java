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

package org.jbpm.console.ng.pr.client.editors.variables.history;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.util.DataGridUtils;
import org.jbpm.console.ng.pr.model.ProcessVariableSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Dependent
public class VariableHistoryPopup extends BaseModal {
    interface Binder
            extends
            UiBinder<Widget, VariableHistoryPopup> {

    }

    @UiField
    public TextBox variableNameTextBox;


    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;

    @UiField
    public DataGrid<ProcessVariableSummary> processVarListGrid;

    @UiField
    public FlowPanel listContainer;


    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    private ListDataProvider<ProcessVariableSummary> dataProvider = new ListDataProvider<ProcessVariableSummary>();


    public SimplePager pager;

    private ColumnSortEvent.ListHandler<ProcessVariableSummary> sortHandler;


    private static Binder uiBinder = GWT.create( Binder.class );

    private long processInstanceId;

    public VariableHistoryPopup() {
        setTitle( Constants.INSTANCE.Variable_History() );

        add( uiBinder.createAndBindUi( this ) );
        init();
        final GenericModalFooter footer = new GenericModalFooter();

        footer.addButton( Constants.INSTANCE.Ok(),
                new Command() {
                    @Override
                    public void execute() {
                        closePopup();
                    }
                }, null,
                ButtonType.PRIMARY );

        add( footer );
    }

    public void init() {

        listContainer.add( processVarListGrid );
        pager = new SimplePager( SimplePager.TextLocation.CENTER, false, true );
        pager.setStyleName( "pagination pagination-right pull-right" );
        listContainer.add( pager );

        processVarListGrid.setHeight( "200px" );
        // Set the message to display when the table is empty.

        processVarListGrid.setEmptyTableWidget( new HTMLPanel( Constants.INSTANCE.No_History_For_This_Variable() ) );

        sortHandler = new ColumnSortEvent.ListHandler<ProcessVariableSummary>( dataProvider.getList() );
        processVarListGrid.addColumnSortHandler( sortHandler );

        // Create a Pager to control the table.

        pager.setDisplay( processVarListGrid );
        pager.setPageSize( 5 );

        // Value.
        com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String> valueColumn = new com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String>( new TextCell() ) {

            @Override
            public void render( Cell.Context context, ProcessVariableSummary variableSummary, SafeHtmlBuilder sb ) {
                String title = variableSummary.getNewValue();
                sb.append( DataGridUtils.createDivStart( title ) );
                super.render( context, variableSummary, sb );
                sb.append( DataGridUtils.createDivEnd() );
            }

            @Override
            public String getValue( ProcessVariableSummary object ) {
                return DataGridUtils.trimToColumnWidth( processVarListGrid, this, object.getNewValue() );
            }
        };

        processVarListGrid.addColumn( valueColumn, Constants.INSTANCE.Value() );
        valueColumn.setSortable( true );
        sortHandler.setComparator( valueColumn, new Comparator<ProcessVariableSummary>() {
            @Override
            public int compare( ProcessVariableSummary o1,
                                ProcessVariableSummary o2 ) {
                return o1.getNewValue().compareTo( o2.getNewValue() );
            }
        } );


        // Old Value.
        com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String> oldValueColumn = new com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String>( new TextCell() ) {

            @Override
            public void render( Cell.Context context, ProcessVariableSummary variableSummary, SafeHtmlBuilder sb ) {
                String title = variableSummary.getOldValue();
                sb.append( DataGridUtils.createDivStart( title ) );
                super.render( context, variableSummary, sb );
                sb.append( DataGridUtils.createDivEnd() );
            }

            @Override
            public String getValue( ProcessVariableSummary object ) {
                return DataGridUtils.trimToColumnWidth( processVarListGrid, this, object.getOldValue() );
            }
        };
        oldValueColumn.setSortable( true );


        processVarListGrid.addColumn( oldValueColumn, Constants.INSTANCE.Previous_Value() );
        sortHandler.setComparator( oldValueColumn, new Comparator<ProcessVariableSummary>() {
            @Override
            public int compare( ProcessVariableSummary o1,
                                ProcessVariableSummary o2 ) {
                return o1.getOldValue().compareTo( o2.getOldValue() );
            }
        } );

        // Last Time Changed Date.
        com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String> lastTimeChangedColumn = new com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String>( new TextCell() ) {

            @Override
            public void render( Cell.Context context, ProcessVariableSummary variableSummary, SafeHtmlBuilder sb ) {
                Date lastModification = new Date( variableSummary.getTimestamp() );
                DateTimeFormat format = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm" );

                String title = format.format( lastModification );
                sb.append( DataGridUtils.createDivStart( title ) );
                super.render( context, variableSummary, sb );
                sb.append( DataGridUtils.createDivEnd() );
            }

            @Override
            public String getValue( ProcessVariableSummary variable ) {
                Date lastModification = new Date( variable.getTimestamp() );
                DateTimeFormat format = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm" );
                return DataGridUtils.trimToColumnWidth( processVarListGrid, this, format.format( lastModification ) );
            }
        };
        lastTimeChangedColumn.setSortable( true );
        sortHandler.setComparator( lastTimeChangedColumn, new Comparator<ProcessVariableSummary>() {
            @Override
            public int compare( ProcessVariableSummary o1,
                                ProcessVariableSummary o2 ) {
                return new Long( o1.getTimestamp() ).compareTo( new Long( o2.getTimestamp() ) );
            }
        } );

        processVarListGrid.addColumn( lastTimeChangedColumn, Constants.INSTANCE.Last_Modification() );

        dataProvider.addDataDisplay( processVarListGrid );
    }

    public void show( long processInstanceId, String variableId ) {
        this.processInstanceId = processInstanceId;
        this.variableNameTextBox.setText( variableId );

        cleanForm();
        super.show();
    }

    public void cleanForm() {
        cleanErrorMessages();
        this.addShownHandler( new ShownHandler() {
            @Override
            public void onShown( ShownEvent shownEvent ) {
                refreshTable();
            }
        } );
        loadVariableHistory();
    }

    private void cleanErrorMessages() {
        errorMessages.setText( "" );
        errorMessagesGroup.setType( ControlGroupType.NONE );
    }

    public void closePopup() {
        hide();
        super.hide();
    }

    public void refreshTable() {
        processVarListGrid.getColumnSortList().push( new ColumnSortList.ColumnSortInfo( processVarListGrid.getColumn( 2 ), false ) );
        processVarListGrid.redraw();
    }

    public void loadVariableHistory() {
        dataServices.call( new RemoteCallback<List<ProcessVariableSummary>>() {
            @Override
            public void callback( List<ProcessVariableSummary> processInstances ) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll( processInstances );
                dataProvider.refresh();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message, Throwable throwable ) {
                errorMessages.setText( throwable.getMessage() );
                errorMessagesGroup.setType( ControlGroupType.ERROR );
                //ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getVariableHistory( processInstanceId, variableNameTextBox.getText() );
    }


}
