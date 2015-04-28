package org.jbpm.console.ng.pr.client.editors.instance.details;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.NodeInstanceLogSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.workbench.events.NotificationEvent;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

@Dependent
public class ProcessInstanceLogViewImpl extends AbstractListView<NodeInstanceLogSummary, ProcessInstanceDetailsPresenter> implements
        ProcessInstanceDetailsPresenter.ProcessInstanceLogView {

    interface ProcessInstanceLogViewImplBinder extends UiBinder<Widget, ProcessInstanceLogViewImpl> {

    }

    private static ProcessInstanceLogViewImplBinder uiBinder = GWT.create( ProcessInstanceLogViewImplBinder.class );

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void initGrid( final ProcessInstanceDetailsPresenter presenter ) {

        List<String> bannedColumns = new ArrayList<String>();

        bannedColumns.add( constants.Node_Type() );
        bannedColumns.add( constants.Name() );

        List<String> initColumns = new ArrayList<String>();
        initColumns.add( constants.Node_Type() );
        initColumns.add( constants.Name() );
        //        initColumns.add(constants.Actions());

        super.init( presenter, new GridGlobalPreferences( "ProcessInstanceLogsGrid", initColumns, bannedColumns ) );

        listGrid.setEmptyTableCaption( constants.No_Variables_Available() );

        selectionModel = new NoSelectionModel<NodeInstanceLogSummary>();
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {

                boolean close = false;
                if ( selectedRow == -1 ) {
                    listGrid.setRowStyles( selectedStyles );
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();

                } else if ( listGrid.getKeyboardSelectedRow() != selectedRow ) {
                    listGrid.setRowStyles( selectedStyles );
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                } else {
                    close = true;
                }

                selectedItem = selectionModel.getLastSelectedObject();

            }
        } );

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager( new DefaultSelectionEventManager.EventTranslator<NodeInstanceLogSummary>() {

                    @Override
                    public boolean clearCurrentSelection( CellPreviewEvent<NodeInstanceLogSummary> event ) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent( CellPreviewEvent<NodeInstanceLogSummary> event ) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if ( BrowserEvents.CLICK.equals( nativeEvent.getType() ) ) {
                            // Ignore if the event didn't occur in the correct column.
                            if ( listGrid.getColumnIndex( actionsColumn ) == event.getColumn() ) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }

                        }

                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }

                } );

        listGrid.setSelectionModel( selectionModel, noActionColumnManager );
        listGrid.setRowStyles( selectedStyles );
    }

    @Override
    public void initColumns() {
        Column name = initNodeNameColumn();
        Column type = initNodeTypeColumn();
        Column timestamp = initNodeTimestampColumn();
        Column processInstanceId = initProcessInstanceIdColumn();
        //        actionsColumn = initActionsColumn();
        List<ColumnMeta<NodeInstanceLogSummary>> columnMetas = new ArrayList<ColumnMeta<NodeInstanceLogSummary>>();
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( type, constants.Node_Type() ) );
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( name, constants.Name() ) );
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( timestamp, constants.Log_Time() ) );
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( processInstanceId, constants.Process_Instance_ID() ) );

        listGrid.addColumns( columnMetas );

    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    private Column initNodeNameColumn() {
        Column<NodeInstanceLogSummary, String> naodeNameColumn = new Column<NodeInstanceLogSummary, String>( new TextCell() ) {

            @Override
            public String getValue( NodeInstanceLogSummary object ) {
                return object.getNodeName();
            }
        };
        return naodeNameColumn;
    }

    private Column initNodeTypeColumn() {
        Column<NodeInstanceLogSummary, String> nodeTypeColumn = new Column<NodeInstanceLogSummary, String>( new TextCell() ) {

            @Override
            public String getValue( NodeInstanceLogSummary object ) {
                return object.getType();
            }
        };
        return nodeTypeColumn;
    }

    private Column initNodeTimestampColumn() {
        Column<NodeInstanceLogSummary, String> nodeTimestampColumn = new Column<NodeInstanceLogSummary, String>( new TextCell() ) {

            @Override
            public String getValue( NodeInstanceLogSummary object ) {
                return object.getTimestamp();
            }
        };
        return nodeTimestampColumn;
    }

    private Column initProcessInstanceIdColumn() {
        Column<NodeInstanceLogSummary, String> processInstanceIdColumn = new Column<NodeInstanceLogSummary, String>( new TextCell() ) {

            @Override
            public String getValue( NodeInstanceLogSummary object ) {
                return String.valueOf( object.getProcessInstanceId() );
            }
        };
        return processInstanceIdColumn;
    }

    private Column initActionsColumn() {

        return null;
    }

}
