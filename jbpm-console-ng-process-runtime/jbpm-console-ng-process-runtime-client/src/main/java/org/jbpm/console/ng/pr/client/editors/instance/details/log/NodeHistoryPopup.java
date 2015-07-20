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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.ht.model.TaskEventSummary;
import org.jbpm.console.ng.ht.service.TaskAuditService;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.util.DataGridUtils;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.mvp.Command;
import org.uberfire.paging.PageResponse;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

@Dependent
public class NodeHistoryPopup extends BaseModal {

    interface NodeHistoryPopupBinder extends UiBinder<Widget, NodeHistoryPopup> {

    }

    @UiField
    public TextBox currentNodeIdTextBox;

    @UiField
    public TextBox currentNodeNameTextBox;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;

    @UiField
    public FlowPanel listContainer;

    private ExtendedPagedTable<TaskEventSummary> nodePagedTable;

    private AsyncDataProvider<TaskEventSummary> dataProvider;
    @Inject
    private Caller<TaskAuditService> taskAuditService;

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    private GridGlobalPreferences preferences;

    private static NodeHistoryPopupBinder uiBinder = GWT.create( NodeHistoryPopupBinder.class );

    private long processInstanceId;

    private long nodeId = -1;

    public NodeHistoryPopup() {
        setTitle( Constants.INSTANCE.Node_History() );

        add( uiBinder.createAndBindUi( this ) );

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

    @PostConstruct
    public void init() {
        initDataProvider();
        listContainer.clear();
        List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add( Constants.INSTANCE.Log_Time() );
        bannedColumns.add( Constants.INSTANCE.Node_Type() );
        bannedColumns.add( Constants.INSTANCE.Owner() );
        List<String> initColumns = new ArrayList<String>();
        initColumns.add( Constants.INSTANCE.Log_Time() );
        initColumns.add( Constants.INSTANCE.Node_Type() );
        initColumns.add( Constants.INSTANCE.Owner() );
        preferences = new GridGlobalPreferences( "NodeHistoryGrid", initColumns, bannedColumns );
        nodePagedTable = new ExtendedPagedTable<TaskEventSummary>( 10, preferences );
        nodePagedTable.setShowLastPagerButton( true );
        nodePagedTable.setShowFastFordwardPagerButton( true );
        listContainer.add( nodePagedTable );
        dataProvider.addDataDisplay( nodePagedTable );
        nodePagedTable.setDataProvider( dataProvider );
        
        preferencesService.call( new RemoteCallback<GridPreferencesStore>() {

            @Override
            public void callback( GridPreferencesStore preferencesStore ) {
                nodePagedTable.setPreferencesService( preferencesService );
                if ( preferencesStore == null ) {
                    nodePagedTable.setGridPreferencesStore( new GridPreferencesStore( preferences ) );
                } else {
                    nodePagedTable.setGridPreferencesStore( preferencesStore );
                }
                initColumns();
                initGenericToolBar();
                nodePagedTable.loadPageSizePreferences();
            }
        } ).loadUserPreferences( preferences.getKey(), UserPreferencesType.GRIDPREFERENCES );
        nodePagedTable.setEmptyTableCaption( Constants.INSTANCE.No_History_For_This_Process() );
    }

    private void initColumns() {
        List<ColumnMeta<TaskEventSummary>> columnMetas = new ArrayList<ColumnMeta<TaskEventSummary>>();

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
        columnMetas.add( new ColumnMeta<TaskEventSummary>( logTimeColumn, Constants.INSTANCE.Log_Time() ) );

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
        columnMetas.add( new ColumnMeta<TaskEventSummary>( typeColumn, Constants.INSTANCE.Type() ) );

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
        columnMetas.add( new ColumnMeta<TaskEventSummary>( OwnerColumn, Constants.INSTANCE.Owner() ) );
        nodePagedTable.addColumns( columnMetas );
    }

    private void initDataProvider() {
        dataProvider = new AsyncDataProvider<TaskEventSummary>() {

            @Override
            protected void onRangeChanged( HasData<TaskEventSummary> display ) {
                loadTaskHistory();
            }
        };
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
        this.addShownHandler( new ShownHandler() {

            @Override
            public void onShown( ShownEvent shownEvent ) {
                refreshGrid();
            }
        } );
    }

    private void cleanErrorMessages() {
        errorMessages.setText( "" );
        errorMessagesGroup.setType( ControlGroupType.NONE );
    }

    public void closePopup() {
        hide();
        super.hide();
    }

    public void loadTaskHistory() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "taskId", this.nodeId );
        QueryFilter filter = new PortableQueryFilter( 0, 0, false, "", "", false, "", params );
        taskAuditService.call( new RemoteCallback<PageResponse<TaskEventSummary>>() {

            @Override
            public void callback( PageResponse<TaskEventSummary> events ) {
                dataProvider.updateRowCount( events.getTotalRowSize(), events.isTotalRowSizeExact() );
                dataProvider.updateRowData( events.getStartRowIndex(), events.getPageRowList() );
                nodePagedTable.redraw();
            }
        }, new ErrorCallback<Message>() {

            @Override
            public boolean error( Message message, Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getData( filter );
    }

    private void initGenericToolBar() {
        Button refreshButton = new Button();
        refreshButton.setIcon( IconType.REFRESH );
        refreshButton.setTitle( Constants.INSTANCE.Refresh() );
        refreshButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                refreshGrid();
            }
        } );
        nodePagedTable.getRightToolbar().add( refreshButton );
    }

    private void refreshGrid() {
        if ( dataProvider.getDataDisplays().size() > 0 ) {
            HasData<TaskEventSummary> next = dataProvider.getDataDisplays().iterator().next();
            next.setVisibleRangeAndClearData( next.getVisibleRange(), true );
        }
    }
}
