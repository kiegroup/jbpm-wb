package org.jbpm.console.ng.pr.client.editors.instance.details;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.pr.client.editors.instance.details.log.NodeHistoryPopup;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.NodeInstanceLogSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.workbench.events.NotificationEvent;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

@Dependent
public class ProcessInstanceNodeHistoryViewImpl extends AbstractListView<NodeInstanceLogSummary, ProcessInstanceDetailsPresenter> implements
        ProcessInstanceDetailsPresenter.ProcessInstanceNodeHistoryView {

    interface ProcessInstanceNodeHistoryViewImplBinder extends UiBinder<Widget, ProcessInstanceNodeHistoryViewImpl> {

    }

    private static ProcessInstanceNodeHistoryViewImplBinder uiBinder = GWT.create( ProcessInstanceNodeHistoryViewImplBinder.class );

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private NodeHistoryPopup taskHistoryPopup;

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void initGrid( final ProcessInstanceDetailsPresenter presenter ) {

        List<String> bannedColumns = new ArrayList<String>();

        bannedColumns.add( constants.Node_Type() );
        bannedColumns.add( constants.Name() );

        List<String> initColumns = new ArrayList<String>();
        initColumns.add( constants.Node_Type() );
        initColumns.add( constants.Name() );

        super.init( presenter, new GridGlobalPreferences( "ProcessInstanceNodeHistoryGrid", initColumns, bannedColumns ) );

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
        actionsColumn = initActionsColumn();
        List<ColumnMeta<NodeInstanceLogSummary>> columnMetas = new ArrayList<ColumnMeta<NodeInstanceLogSummary>>();
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( type, constants.Node_Type() ) );
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( name, constants.Name() ) );
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( timestamp, constants.Log_Time() ) );
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( actionsColumn, constants.Actions() ) );
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

    private Column initActionsColumn() {

        List<HasCell<NodeInstanceLogSummary, ?>> cells = new LinkedList<HasCell<NodeInstanceLogSummary, ?>>();

        cells.add( new NodeHistoryActionHasCell( constants.Node_History(), new Delegate<NodeInstanceLogSummary>() {

            @Override
            public void execute( NodeInstanceLogSummary node ) {
                taskHistoryPopup.show( node.getProcessInstanceId(), node.getNodeId(), node.getNodeName() );

            }
        } ) );

        CompositeCell<NodeInstanceLogSummary> cell = new CompositeCell<NodeInstanceLogSummary>( cells );
        Column<NodeInstanceLogSummary, NodeInstanceLogSummary> actionsColumn = new Column<NodeInstanceLogSummary, NodeInstanceLogSummary>( cell ) {

            @Override
            public NodeInstanceLogSummary getValue( NodeInstanceLogSummary object ) {
                return object;
            }
        };
        return actionsColumn;
    }

    private class NodeHistoryActionHasCell implements HasCell<NodeInstanceLogSummary, NodeInstanceLogSummary> {

        private ActionCell<NodeInstanceLogSummary> cell;

        public NodeHistoryActionHasCell(String text, Delegate<NodeInstanceLogSummary> delegate) {
            cell = new ActionCell<NodeInstanceLogSummary>( text, delegate ) {

                @Override
                public void render( Cell.Context context, NodeInstanceLogSummary value, SafeHtmlBuilder sb ) {
                    if ( value.getType().equals( NodeInstanceLogSummary.NODE_TYPE_HUMANTASK ) || value.getType().equals( NodeInstanceLogSummary.NODE_TYPE_WORKITEMNODE ) ) {
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<a href='javascript:;' class='btn btn-mini' style='margin-right:5px;' title='"+constants.History()+"'>"+constants.History()+"</a>");
                        sb.append(mysb.toSafeHtml());
                    }
                }
            };
        }

        @Override
        public Cell<NodeInstanceLogSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<NodeInstanceLogSummary, NodeInstanceLogSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public NodeInstanceLogSummary getValue( NodeInstanceLogSummary object ) {
            return object;
        }
    }

    @Override
    public void init( ProcessInstanceDetailsPresenter presenter ) {
        // TODO Auto-generated method stub
        
    }
}
