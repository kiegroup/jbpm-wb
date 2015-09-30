package org.jbpm.console.ng.pr.client.editors.instance.details.log;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.util.DataGridUtils;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ht.model.TaskEventSummary;
import org.jbpm.console.ng.ht.service.TaskAuditService;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.events.NotificationEvent;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;

public class NodeHistoryPopup extends BaseModal {

    interface NodeHistoryBinder
            extends
            UiBinder<Widget, NodeHistoryPopup> {

    }

    @UiField
    public FormControlStatic currentNodeIdTextBox;

    @UiField
    public FormControlStatic currentNodeNameTextBox;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public FormGroup errorMessagesGroup;

    @UiField
    public DataGrid<TaskEventSummary> nodeListGrid;

    @UiField
    public Pagination pagination;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<TaskAuditService> taskAuditService;

    private ListDataProvider<TaskEventSummary> dataProvider = new ListDataProvider<TaskEventSummary>();

    public SimplePager pager;

    private ColumnSortEvent.ListHandler<TaskEventSummary> sortHandler;

    private static NodeHistoryBinder uiBinder = GWT.create( NodeHistoryBinder.class );

    private long processInstanceId;

    private long nodeId = -1;

    public NodeHistoryPopup() {
        setTitle( Constants.INSTANCE.Node_History() );

        setBody( uiBinder.createAndBindUi( this ) );
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
        pager = new SimplePager( SimplePager.TextLocation.CENTER, false, true );
        pagination.rebuild( pager );

        // Set the message to display when the table is empty.
        nodeListGrid.setEmptyTableWidget( new HTMLPanel( Constants.INSTANCE.No_History_For_This_Variable() ) );

        sortHandler = new ColumnSortEvent.ListHandler<TaskEventSummary>( dataProvider.getList() );
        nodeListGrid.addColumnSortHandler( sortHandler );

        // Create a Pager to control the table.

        pager.setDisplay( nodeListGrid );
        pager.setPageSize( 5 );

        // Setting the RangeChangeHandler
        nodeListGrid.addRangeChangeHandler( new RangeChangeEvent.Handler() {

            @Override
            public void onRangeChange( RangeChangeEvent event ) {
                pagination.rebuild( pager );
            }
        } );

        initColumn();

        dataProvider.addDataDisplay( nodeListGrid );

    }

    private void initColumn() {
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
                return format.format( taskEventSummary.getLogTime() );
            }
        };

        nodeListGrid.addColumn( logTimeColumn,  Constants.INSTANCE.Log_Time()  );

        logTimeColumn.setSortable( true );
        sortHandler.setComparator( logTimeColumn, new Comparator<TaskEventSummary>() {
            @Override
            public int compare( TaskEventSummary o1,
                    TaskEventSummary o2 ) {
                return o1.getLogTime().compareTo( o2.getLogTime() );
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
                return object.getType();
            }
        };
        nodeListGrid.addColumn( typeColumn, Constants.INSTANCE.Type()  );

        typeColumn.setSortable( true );
        sortHandler.setComparator( typeColumn, new Comparator<TaskEventSummary>() {
            @Override
            public int compare( TaskEventSummary o1,
                    TaskEventSummary o2 ) {
                return o1.getType().compareTo( o2.getType() );
            }
        } );
        
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
                return title;
            }
        };
        nodeListGrid.addColumn( OwnerColumn, Constants.INSTANCE.Owner()  );
        OwnerColumn.setSortable( true );
        sortHandler.setComparator( OwnerColumn, new Comparator<TaskEventSummary>() {
            @Override
            public int compare( TaskEventSummary o1,
                    TaskEventSummary o2 ) {
                return o1.getUserId().compareTo( o2.getUserId() );
            }
        } );
    }

    public void show( long processInstanceId, long taskId, String taskName ) {
        this.processInstanceId = processInstanceId;
        this.currentNodeIdTextBox.setText( String.valueOf( taskId ) );
        this.nodeId = taskId;
        this.currentNodeNameTextBox.setText( taskName );
        cleanForm();
        super.show();
    }
    
    public void cleanForm() {
        cleanErrorMessages();
        this.addShownHandler( new ModalShownHandler() {

            @Override
            public void onShown( ModalShownEvent shownEvent ) {
                refreshTable();
            }
        } );
        loadVariableHistory();
    }

    private void cleanErrorMessages() {
        errorMessages.setText( "" );
        errorMessagesGroup.setValidationState( ValidationState.NONE );
    }

    public void closePopup() {
        hide();
    }

    public void refreshTable() {
        nodeListGrid.getColumnSortList().push( new ColumnSortList.ColumnSortInfo( nodeListGrid.getColumn( 2 ), false ) );
        nodeListGrid.redraw();
    }

    public void loadVariableHistory() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "taskId", this.nodeId );
        QueryFilter filter = new PortableQueryFilter( 0, 0, false, "", "", false, "", params );
        taskAuditService.call( new RemoteCallback<PageResponse<TaskEventSummary>>() {

            @Override
            public void callback( PageResponse<TaskEventSummary> events ) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll( events.getPageRowList() );
                dataProvider.flush();
                pagination.rebuild( pager );
            }
        }, new ErrorCallback<Message>() {

            @Override
            public boolean error( Message message, Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getData( filter );
    }
}
