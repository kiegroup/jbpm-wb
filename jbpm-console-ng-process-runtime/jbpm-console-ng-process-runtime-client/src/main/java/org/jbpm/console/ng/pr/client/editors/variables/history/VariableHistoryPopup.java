/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.jbpm.console.ng.gc.client.util.DateUtils;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.util.DataGridUtils;
import org.jbpm.console.ng.bd.model.ProcessVariableSummary;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.ext.widgets.common.client.tables.PopoverTextCell;
import org.uberfire.mvp.Command;

@Dependent
public class VariableHistoryPopup extends BaseModal {

    interface Binder
            extends
            UiBinder<Widget, VariableHistoryPopup> {

    }

    @UiField
    public FormControlStatic variableNameTextBox;

    @UiField
    public DataGrid<ProcessVariableSummary> processVarListGrid;

    @UiField
    public Pagination pagination;

    private final Constants instance = Constants.INSTANCE;

    private ListDataProvider<ProcessVariableSummary> dataProvider = new ListDataProvider<ProcessVariableSummary>();

    public SimplePager pager;

    private ColumnSortEvent.ListHandler<ProcessVariableSummary> sortHandler;

    private static Binder uiBinder = GWT.create( Binder.class );

    public VariableHistoryPopup() {
        setTitle( Constants.INSTANCE.History() );

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

    public VariableHistoryPopup(DataGrid<ProcessVariableSummary> processVarListGrid,
                                Pagination pagination,
                                FormControlStatic variableNameTextBox) {
        this.processVarListGrid =processVarListGrid;
        this.pagination = pagination;
        this.variableNameTextBox =variableNameTextBox;

        init();
    }

    public void init() {
        pager = new SimplePager( SimplePager.TextLocation.CENTER, false, true );
        pagination.rebuild( pager );

        // Set the message to display when the table is empty.
        processVarListGrid.setEmptyTableWidget( new HTMLPanel( instance.No_History_For_This_Variable() ) );

        sortHandler = new ColumnSortEvent.ListHandler<ProcessVariableSummary>( dataProvider.getList() );
        processVarListGrid.addColumnSortHandler( sortHandler );

        // Create a Pager to control the table.

        pager.setDisplay( processVarListGrid );
        pager.setPageSize( 5 );

        // Setting the RangeChangeHandler
        processVarListGrid.addRangeChangeHandler( new RangeChangeEvent.Handler() {
            @Override
            public void onRangeChange( RangeChangeEvent event ) {
                pagination.rebuild( pager );
            }
        } );

        // Value.
        com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String> valueColumn = new com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String>( new PopoverTextCell() ) {

            @Override
            public String getValue( ProcessVariableSummary object ) {
                return object.getNewValue()!=null? object.getNewValue():"" ;
            }
        };

        processVarListGrid.addColumn( valueColumn, instance.Value() );
        valueColumn.setSortable( true );
        sortHandler.setComparator( valueColumn, new Comparator<ProcessVariableSummary>() {
            @Override
            public int compare( ProcessVariableSummary o1,
                                ProcessVariableSummary o2 ) {
                String o1NewValue = o1.getNewValue() != null ? o1.getNewValue() :"";
                String o2NewValue = o2.getNewValue() != null ? o2.getNewValue() :"";
                return o1NewValue.compareTo( o2NewValue );
            }
        } );

        // Old Value.
        com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String> oldValueColumn = new com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String>( new PopoverTextCell() ) {

            @Override
            public String getValue( ProcessVariableSummary object ) {
                return object.getOldValue()!=null? object.getOldValue():"" ;
            }
        };
        oldValueColumn.setSortable( true );

        processVarListGrid.addColumn( oldValueColumn, instance.Previous_Value() );
        sortHandler.setComparator( oldValueColumn, new Comparator<ProcessVariableSummary>() {
            @Override
            public int compare( ProcessVariableSummary o1,
                                ProcessVariableSummary o2 ) {
                String o1OldValue = o1.getOldValue() != null ? o1.getOldValue() :"";
                String o2OldValue = o2.getOldValue() != null ? o2.getOldValue() :"";
                return o1OldValue.compareTo(o2OldValue);
            }
        } );

        // Last Time Changed Date.
        com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String> lastTimeChangedColumn = new com.google.gwt.user.cellview.client.Column<ProcessVariableSummary, String>( new TextCell() ) {

            @Override
            public void render( Cell.Context context,
                                ProcessVariableSummary variableSummary,
                                SafeHtmlBuilder sb ) {

                String title = DateUtils.getDateTimeStr(new Date(variableSummary.getTimestamp()));
                sb.append( DataGridUtils.createDivStart( title ) );
                super.render( context, variableSummary, sb );
                sb.append( DataGridUtils.createDivEnd() );
            }

            @Override
            public String getValue( ProcessVariableSummary variable ) {
                return DataGridUtils.trimToColumnWidth( processVarListGrid, this, DateUtils.getDateTimeStr(new Date(variable.getTimestamp())) );
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

        processVarListGrid.addColumn( lastTimeChangedColumn, instance.Last_Modification() );

        dataProvider.addDataDisplay( processVarListGrid );
    }

    public void show( final String variableId, final List<ProcessVariableSummary> processVariableSummaries ) {
        this.variableNameTextBox.setText( variableId );
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                loadVariableHistory(processVariableSummaries);
                refreshTable();
            }
        });
        super.show();
    }

    public void closePopup() {
        hide();
    }

    public void refreshTable() {
        processVarListGrid.getColumnSortList().clear();
        processVarListGrid.getColumnSortList().push( new ColumnSortList.ColumnSortInfo( processVarListGrid.getColumn( 2 ), false ) );
        processVarListGrid.redraw();
    }

    public void loadVariableHistory(final List<ProcessVariableSummary> processVariableSummaries) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(processVariableSummaries);
        dataProvider.flush();
        pagination.rebuild(pager);
    }

    public ColumnSortEvent.ListHandler<ProcessVariableSummary> getSortHandler(){
        return sortHandler;
    }

}
