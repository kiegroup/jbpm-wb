package org.jbpm.console.ng.pr.client.editors.instance.details;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskDisplayerConfig;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskFormDisplayProvider;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.pr.client.editors.instance.details.log.NodeHistoryPopup;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.forms.client.display.views.PopupFormDisplayerView;
import org.jbpm.console.ng.pr.model.NodeInstanceLogSummary;
import org.kie.api.runtime.process.ProcessInstance;
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
import com.google.gwt.user.client.ui.AbstractImagePrototype;
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

    @Inject
    private NodeHistoryPopup taskHistoryPopup;

    @Inject
    private HumanTaskFormDisplayProvider humanTaskFormDisplayProvider;

    @Inject
    private PopupFormDisplayerView popupView;

    private ProcessRuntimeImages images = GWT.create( ProcessRuntimeImages.class );

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void initGrid( final ProcessInstanceDetailsPresenter presenter ) {

        List<String> bannedColumns = new ArrayList<String>();

        bannedColumns.add( constants.Node_Type() );
        bannedColumns.add( constants.Name() );

        List<String> initColumns = new ArrayList<String>();
        initColumns.add( constants.Node_Type() );
        initColumns.add( constants.Name() );

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
        actionsColumn = initActionsColumn();
        List<ColumnMeta<NodeInstanceLogSummary>> columnMetas = new ArrayList<ColumnMeta<NodeInstanceLogSummary>>();
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( type, constants.Node_Type() ) );
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( name, constants.Name() ) );
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( timestamp, constants.Log_Time() ) );
        columnMetas.add( new ColumnMeta<NodeInstanceLogSummary>( processInstanceId, constants.Process_Instance_ID() ) );
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

        List<HasCell<NodeInstanceLogSummary, ?>> cells = new LinkedList<HasCell<NodeInstanceLogSummary, ?>>();

        cells.add( new TaskHistoryActionHasCell( "Task history", new Delegate<NodeInstanceLogSummary>() {

            @Override
            public void execute( NodeInstanceLogSummary node ) {
                taskHistoryPopup.show( node.getProcessInstanceId(), node.getNodeId(), node.getNodeName() );

            }
        } ) );

        cells.add( new TaskWorkDetailsActionHasCell( "Task work details", new Delegate<NodeInstanceLogSummary>() {

            @Override
            public void execute( NodeInstanceLogSummary node ) {
                popupView( node.getNodeId() );

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

    private class TaskHistoryActionHasCell implements HasCell<NodeInstanceLogSummary, NodeInstanceLogSummary> {

        private ActionCell<NodeInstanceLogSummary> cell;

        public TaskHistoryActionHasCell(String text, Delegate<NodeInstanceLogSummary> delegate) {
            cell = new ActionCell<NodeInstanceLogSummary>( text, delegate ) {

                @Override
                public void render( Cell.Context context, NodeInstanceLogSummary value, SafeHtmlBuilder sb ) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.historyGridIcon() );
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant( "<span title='" + constants.User_Task_logs() + "'>" );
                        mysb.append( imageProto.getSafeHtml() );
                        mysb.appendHtmlConstant( "</span>" );
                        sb.append( mysb.toSafeHtml() );
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

    private class TaskWorkDetailsActionHasCell implements HasCell<NodeInstanceLogSummary, NodeInstanceLogSummary> {

        private ActionCell<NodeInstanceLogSummary> cell;

        public TaskWorkDetailsActionHasCell(String text, Delegate<NodeInstanceLogSummary> delegate) {
            cell = new ActionCell<NodeInstanceLogSummary>( text, delegate ) {

                @Override
                public void render( Cell.Context context, NodeInstanceLogSummary value, SafeHtmlBuilder sb ) {
                    if ( presenter.getProcessInstanceStatus() == ProcessInstance.STATE_ACTIVE && value.isCompleted() && value.getType().equals( NodeInstanceLogSummary.NODE_TYPE_HUMANTASK ) ) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.detailsGridIcon() );
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant( "<span title='" + constants.User_Task_Work() + "'>" );
                        mysb.append( imageProto.getSafeHtml() );
                        mysb.appendHtmlConstant( "</span>" );
                        sb.append( mysb.toSafeHtml() );
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

    private void popupView( final long taskId ) {
        if ( taskId != -1 ) {
            TaskKey key = new TaskKey( taskId );
            HumanTaskDisplayerConfig config = new HumanTaskDisplayerConfig( key );
            humanTaskFormDisplayProvider.setup( config, popupView );
        }
    }
}
