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

package org.jbpm.console.ng.pr.client.editors.variables.history;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import java.util.Comparator;
import java.util.Date;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.util.DataGridUtils;
import org.jbpm.console.ng.pr.model.ProcessVariableSummary;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "VariableHistoryViewImpl.html")
public class VariableHistoryViewImpl extends Composite implements VariableHistoryPresenter.PopupView {

    private Constants constants = GWT.create( Constants.class );

    private long processInstanceId;

    private String variableId;

    private VariableHistoryPresenter presenter;

    @Inject
    @DataField
    public Label variableHistoryLabel;

    @Inject
    @DataField
    public TextBox variableNameText;
    
    @Inject
    @DataField
    public FlowPanel listContainer;

    @Inject
    @DataField
    public DataGrid<ProcessVariableSummary> processVarListGrid;

    private ListHandler<ProcessVariableSummary> sortHandler;
    
    public SimplePager pager;

    @Inject
    private Event<NotificationEvent> notification;
  
    @Override
    public void init( VariableHistoryPresenter presenter ) {
        this.presenter = presenter;
        listContainer.add( processVarListGrid );
        pager = new SimplePager(SimplePager.TextLocation.CENTER, false, true);
        pager.setStyleName("pagination pagination-right pull-right");
        listContainer.add( pager );

        processVarListGrid.setHeight( "200px" );
        // Set the message to display when the table is empty.
        
        processVarListGrid.setEmptyTableWidget( new HTMLPanel(constants.No_History_For_This_Variable()) );

        sortHandler = new ListHandler<ProcessVariableSummary>( presenter.getDataProvider().getList() );
        processVarListGrid.addColumnSortHandler( sortHandler );

        // Create a Pager to control the table.

        pager.setDisplay( processVarListGrid );
        pager.setPageSize( 5 );

        // Value.
        Column<ProcessVariableSummary, String> valueColumn = new Column<ProcessVariableSummary, String>( new TextCell() ) {

            @Override
            public void render(Cell.Context context, ProcessVariableSummary variableSummary, SafeHtmlBuilder sb) {
                String title = variableSummary.getNewValue();
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, variableSummary, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue( ProcessVariableSummary object ) {
                return DataGridUtils.trimToColumnWidth(processVarListGrid, this, object.getNewValue());
            }
        };

        processVarListGrid.addColumn( valueColumn, constants.Value() );
        valueColumn.setSortable( true );
        sortHandler.setComparator( valueColumn, new Comparator<ProcessVariableSummary>() {
            @Override
            public int compare( ProcessVariableSummary o1,
                                ProcessVariableSummary o2 ) {
                return o1.getNewValue().compareTo( o2.getNewValue() );
            }
        } );
            
        
        // Old Value.
        Column<ProcessVariableSummary, String> oldValueColumn = new Column<ProcessVariableSummary, String>( new TextCell() ) {

            @Override
            public void render(Cell.Context context, ProcessVariableSummary variableSummary, SafeHtmlBuilder sb) {
                String title = variableSummary.getOldValue();
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, variableSummary, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue( ProcessVariableSummary object ) {
                return DataGridUtils.trimToColumnWidth(processVarListGrid, this, object.getOldValue());
            }
        };
        oldValueColumn.setSortable( true );
        

        processVarListGrid.addColumn( oldValueColumn, constants.Previous_Value() );
        sortHandler.setComparator( oldValueColumn, new Comparator<ProcessVariableSummary>() {
            @Override
            public int compare( ProcessVariableSummary o1,
                                ProcessVariableSummary o2 ) {
                return o1.getOldValue().compareTo( o2.getOldValue());
            }
        } );

        // Last Time Changed Date.
        Column<ProcessVariableSummary, String> lastTimeChangedColumn = new Column<ProcessVariableSummary, String>( new TextCell() ) {

            @Override
            public void render(Cell.Context context, ProcessVariableSummary variableSummary, SafeHtmlBuilder sb) {
                Date lastMofidication = new Date(variableSummary.getTimestamp());
                DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");

                String title = format.format(lastMofidication);
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, variableSummary, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue( ProcessVariableSummary variable ) {
                Date lastMofidication = new Date(variable.getTimestamp());
                DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
                return DataGridUtils.trimToColumnWidth(processVarListGrid, this, format.format(lastMofidication));
            }
        };
        lastTimeChangedColumn.setSortable( true );
        sortHandler.setComparator( lastTimeChangedColumn, new Comparator<ProcessVariableSummary>() {
            @Override
            public int compare( ProcessVariableSummary o1,
                                ProcessVariableSummary o2 ) {
                return new Long(o1.getTimestamp()).compareTo( new Long(o2.getTimestamp()));
            }
        } );

        processVarListGrid.addColumn( lastTimeChangedColumn,  constants.Last_Modification() );

        presenter.addDataDisplay( processVarListGrid );

        variableHistoryLabel.setText( constants.Variable_History() );
        variableNameText.setReadOnly(true);



    }

    @Override
    public void refreshTable() {
        processVarListGrid.getColumnSortList().push(new ColumnSortList.ColumnSortInfo(processVarListGrid.getColumn(2), false));
        processVarListGrid.redraw();
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public void setProcessInstanceId( long processInstanceId ) {
        this.processInstanceId = processInstanceId;

    }

    @Override
    public void setVariableId( String variableId ) {
        this.variableId = variableId;
        this.variableNameText.setText( variableId );
    }

    @Override
    public long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public String getVariableId() {
        return variableId;
    }

}
