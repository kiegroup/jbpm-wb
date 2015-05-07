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

package org.jbpm.console.ng.pr.client.editors.instance.details.log;

import com.github.gwtbootstrap.client.ui.*;
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
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ht.model.TaskEventSummary;
import org.jbpm.console.ng.ht.service.TaskAuditService;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.util.DataGridUtils;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Dependent
public class NodeHistoryPopup extends BaseModal {

    interface NodeHistoryPopupBinder extends UiBinder<Widget, NodeHistoryPopup> {

    }

    @UiField
    public TextBox currentTaskIdTextBox;

    @UiField
    public TextBox currentTaskNameTextBox;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;

    @UiField
    public DataGrid<TaskEventSummary> taskEventListGrid;

    @UiField
    public FlowPanel listContainer;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<TaskAuditService> taskAuditService;

    private ListDataProvider<TaskEventSummary> dataProvider = new ListDataProvider<TaskEventSummary>();

    public SimplePager pager;

    private ColumnSortEvent.ListHandler<TaskEventSummary> sortHandler;

    private static NodeHistoryPopupBinder uiBinder = GWT.create( NodeHistoryPopupBinder.class );

    private long processInstanceId;

    public NodeHistoryPopup() {
        setTitle( Constants.INSTANCE.Node_History() );

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

        listContainer.add( taskEventListGrid );
        pager = new SimplePager( SimplePager.TextLocation.CENTER, false, true );
        pager.setStyleName( "pagination pagination-right pull-right" );
        listContainer.add( pager );

        taskEventListGrid.setHeight( "200px" );

        taskEventListGrid.setEmptyTableWidget( new HTMLPanel( Constants.INSTANCE.No_History_For_This_Process() ) );

        sortHandler = new ColumnSortEvent.ListHandler<TaskEventSummary>( dataProvider.getList() );
        taskEventListGrid.addColumnSortHandler( sortHandler );

        pager.setDisplay( taskEventListGrid );
        pager.setPageSize( 10 );

        com.google.gwt.user.cellview.client.Column<TaskEventSummary, String> taskIdColumn = new com.google.gwt.user.cellview.client.Column<TaskEventSummary, String>( new TextCell() ) {

            @Override
            public void render( Cell.Context context, TaskEventSummary taskEventSummary, SafeHtmlBuilder sb ) {
                String title = taskEventSummary.getTaskId().toString();
                sb.append( DataGridUtils.createDivStart( title ) );
                super.render( context, taskEventSummary, sb );
                sb.append( DataGridUtils.createDivEnd() );
            }

            @Override
            public String getValue( TaskEventSummary object ) {
                return DataGridUtils.trimToColumnWidth( taskEventListGrid, this, object.getTaskId().toString() );
            }
        };

        taskEventListGrid.addColumn( taskIdColumn, Constants.INSTANCE.Id() );
        taskIdColumn.setSortable( true );
        sortHandler.setComparator( taskIdColumn, new Comparator<TaskEventSummary>() {

            @Override
            public int compare( TaskEventSummary o1, TaskEventSummary o2 ) {
                return o1.getTaskId().compareTo( o2.getTaskId() );
            }
        } );

        com.google.gwt.user.cellview.client.Column<TaskEventSummary, String> typeColumn = new com.google.gwt.user.cellview.client.Column<TaskEventSummary, String>( new TextCell() ) {

            @Override
            public void render( Cell.Context context, TaskEventSummary taskEventSummary, SafeHtmlBuilder sb ) {
                String title = taskEventSummary.getType();
                sb.append( DataGridUtils.createDivStart( title ) );
                super.render( context, taskEventSummary, sb );
                sb.append( DataGridUtils.createDivEnd() );
            }

            @Override
            public String getValue( TaskEventSummary object ) {
                return DataGridUtils.trimToColumnWidth( taskEventListGrid, this, object.getType() );
            }
        };
        typeColumn.setSortable( true );

        taskEventListGrid.addColumn( typeColumn, Constants.INSTANCE.Type() );
        sortHandler.setComparator( typeColumn, new Comparator<TaskEventSummary>() {

            @Override
            public int compare( TaskEventSummary o1, TaskEventSummary o2 ) {
                return o1.getType().compareTo( o2.getType() );
            }
        } );

        com.google.gwt.user.cellview.client.Column<TaskEventSummary, String> logTimeColumn = new com.google.gwt.user.cellview.client.Column<TaskEventSummary, String>( new TextCell() ) {

            @Override
            public void render( Cell.Context context, TaskEventSummary taskEventSummary, SafeHtmlBuilder sb ) {
                DateTimeFormat format = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm" );
                String title = format.format( taskEventSummary.getLogTime() );
                sb.append( DataGridUtils.createDivStart( title ) );
                super.render( context, taskEventSummary, sb );
                sb.append( DataGridUtils.createDivEnd() );
            }

            @Override
            public String getValue( TaskEventSummary taskEventSummary ) {
                DateTimeFormat format = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm" );
                return DataGridUtils.trimToColumnWidth( taskEventListGrid, this, format.format( taskEventSummary.getLogTime() ) );
            }
        };
        logTimeColumn.setSortable( true );
        sortHandler.setComparator( logTimeColumn, new Comparator<TaskEventSummary>() {

            @Override
            public int compare( TaskEventSummary o1,
                    TaskEventSummary o2 ) {
                return o1.getLogTime().compareTo( o2.getLogTime() );
            }
        } );

        taskEventListGrid.addColumn( logTimeColumn, Constants.INSTANCE.Log_Time() );

        com.google.gwt.user.cellview.client.Column<TaskEventSummary, String> OwnerColumn = new com.google.gwt.user.cellview.client.Column<TaskEventSummary, String>( new TextCell() ) {

            @Override
            public void render( Cell.Context context, TaskEventSummary taskEventSummary, SafeHtmlBuilder sb ) {
                String title = taskEventSummary.getUserId();
                if ( taskEventSummary.getType().equals( "ADDED" ) ) {
                    title = "System";
                }
                sb.append( DataGridUtils.createDivStart( title ) );
                super.render( context, taskEventSummary, sb );
                sb.append( DataGridUtils.createDivEnd() );
            }

            @Override
            public String getValue( TaskEventSummary taskEventSummary ) {
                String title = taskEventSummary.getUserId();
                if ( taskEventSummary.getType().equals( "ADDED" ) ) {
                    title = "System";
                }
                return DataGridUtils.trimToColumnWidth( taskEventListGrid, this, title );
            }
        };
        OwnerColumn.setSortable( true );
        sortHandler.setComparator( OwnerColumn, new Comparator<TaskEventSummary>() {

            @Override
            public int compare( TaskEventSummary o1, TaskEventSummary o2 ) {
                return o1.getLogTime().compareTo( o2.getLogTime() );
            }
        } );

        taskEventListGrid.addColumn( OwnerColumn, Constants.INSTANCE.Owner() );

        dataProvider.addDataDisplay( taskEventListGrid );
    }

    public void show( long processInstanceId, long taskId, String taskName ) {
        this.processInstanceId = processInstanceId;
        this.currentTaskIdTextBox.setText( String.valueOf( taskId ) );
        this.currentTaskNameTextBox.setText( taskName );
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
        loadTaskHistory();
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
        taskEventListGrid.getColumnSortList().push( new ColumnSortList.ColumnSortInfo( taskEventListGrid.getColumn( 2 ), false ) );
        taskEventListGrid.redraw();
    }

    public void loadTaskHistory() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "taskId", Long.parseLong( currentTaskIdTextBox.getText() ) );
        QueryFilter filter = new PortableQueryFilter( 0, 0, false, "", "", false, "", params );
        taskAuditService.call( new RemoteCallback<PageResponse<TaskEventSummary>>() {

            @Override
            public void callback( PageResponse<TaskEventSummary> events ) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll( events.getPageRowList() );
                dataProvider.refresh();
            }
        }, new ErrorCallback<Message>() {

            @Override
            public boolean error( Message message,
                    Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getData( filter );
    }

}
