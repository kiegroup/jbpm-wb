/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.gc.client.gridexp;

import java.util.Comparator;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.*;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.gc.client.experimental.customGrid.ColumnSelectionWidget;
import org.jbpm.console.ng.gc.client.experimental.pagination.DataMockSummary;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.jbpm.console.ng.gc.client.util.ResizableHeader;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "GridExpListViewImpl.html")
public class GridExpListViewImpl extends Composite
        implements GridExpListPresenter.GridExpListView,
        RequiresResize {

    @Inject
    private PlaceManager placeManager;

    private GridExpListPresenter presenter;

    @Inject
    @DataField
    public LayoutPanel listContainer;

    @DataField
    public ColumnSelectionWidget columnSelector = new ColumnSelectionWidget();

    @Inject
    @DataField
    public DataGrid<DataMockSummary> listGrid;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);

    protected ListHandler<DataMockSummary> sortHandler;

    public GridExpListViewImpl() {}

    @Override
    public void init(final GridExpListPresenter presenter) {
        this.presenter = presenter;
        listContainer.clear();
        listGrid = new DataGrid<DataMockSummary>();
        listGrid.setStyleName("table table-bordered table-striped table-hover");
        listGrid.setEmptyTableWidget(new HTMLPanel(constants.No_Items_Found()));

        sortHandler = new ColumnSortEvent.ListHandler<DataMockSummary>(presenter.getDataProvider().getList());
        listGrid.getColumnSortList().setLimit(1);
        listGrid.addColumnSortHandler(sortHandler);

        listContainer.add(listGrid);
        this.initGridColumns();
        this.refreshItems();
        presenter.addDataDisplay(listGrid);

        columnSelector.setVisible(true);
        columnSelector.setDataGrid("testGrid1", listGrid);
    }

    public void initGridColumns() {
        idColumn();
        column1Column();
        column2Column();
        column3Column();
        column4Column();
    }

    private void idColumn() {
        Column<DataMockSummary, String> columnIdColumn = new Column<DataMockSummary, String>(
                new TextCell()) {

            @Override
            public void render(Cell.Context context, DataMockSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getId();
                sb.append(org.jbpm.console.ng.gc.client.util.DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(org.jbpm.console.ng.gc.client.util.DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(DataMockSummary unit) {
                return org.jbpm.console.ng.gc.client.util.DataGridUtils.trimToColumnWidth(listGrid, this, unit.getId());
            }
        };
        columnIdColumn.setSortable(true);
        sortHandler.setComparator(columnIdColumn, new Comparator<DataMockSummary>() {
            @Override
            public int compare(DataMockSummary o1, DataMockSummary o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        listGrid.addColumn(columnIdColumn, new ResizableHeader("ID",  listGrid, columnIdColumn));
        listGrid.setColumnWidth(columnIdColumn, "300px");
    }

    private void column1Column() {
        Column<DataMockSummary, String> column1Column = new Column<DataMockSummary, String>(
                new TextCell()) {

            @Override
            public void render(Cell.Context context, DataMockSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getColumn1();
                sb.append(org.jbpm.console.ng.gc.client.util.DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(org.jbpm.console.ng.gc.client.util.DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(DataMockSummary unit) {
                return org.jbpm.console.ng.gc.client.util.DataGridUtils.trimToColumnWidth(listGrid, this, unit.getColumn1());
            }
        };
        column1Column.setSortable(true);
        sortHandler.setComparator(column1Column, new Comparator<DataMockSummary>() {
            @Override
            public int compare(DataMockSummary o1, DataMockSummary o2) {
                return o1.getColumn1().compareTo(o2.getColumn1());
            }
        });
        listGrid.addColumn(column1Column, new ResizableHeader("Column1",  listGrid, column1Column));
        listGrid.setColumnWidth(column1Column, "100px");
    }

    private void column2Column() {
        Column<DataMockSummary, String> column2Column = new Column<DataMockSummary, String>(
                new TextCell()) {

            @Override
            public void render(Cell.Context context, DataMockSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getColumn2();
                sb.append(org.jbpm.console.ng.gc.client.util.DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(org.jbpm.console.ng.gc.client.util.DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(DataMockSummary unit) {
                return org.jbpm.console.ng.gc.client.util.DataGridUtils.trimToColumnWidth(listGrid, this, unit.getColumn2());
            }
        };
        column2Column.setSortable(true);
        sortHandler.setComparator(column2Column, new Comparator<DataMockSummary>() {
            @Override
            public int compare(DataMockSummary o1, DataMockSummary o2) {
                return o1.getColumn2().compareTo(o2.getColumn2());
            }
        });
        listGrid.addColumn(column2Column, new ResizableHeader("Column2",  listGrid, column2Column));
        listGrid.setColumnWidth(column2Column, "100px");
    }

    private void column3Column() {
        Column<DataMockSummary, String> column3Column = new Column<DataMockSummary, String>(
                new TextCell()) {

            @Override
            public void render(Cell.Context context, DataMockSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getColumn3();
                sb.append(org.jbpm.console.ng.gc.client.util.DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(org.jbpm.console.ng.gc.client.util.DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(DataMockSummary unit) {
                return org.jbpm.console.ng.gc.client.util.DataGridUtils.trimToColumnWidth(listGrid, this, unit.getColumn3());
            }
        };
        column3Column.setSortable(true);
        sortHandler.setComparator(column3Column, new Comparator<DataMockSummary>() {
            @Override
            public int compare(DataMockSummary o1, DataMockSummary o2) {
                return o1.getColumn3().compareTo(o2.getColumn3());
            }
        });
        listGrid.addColumn(column3Column, new ResizableHeader("Column3", listGrid, column3Column));
        listGrid.setColumnWidth(column3Column, "100px");
    }

    private void column4Column() {
        Column<DataMockSummary, String> column4Column = new Column<DataMockSummary, String>(
                new TextCell()) {

            @Override
            public void render(Cell.Context context, DataMockSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getColumn4();
                sb.append(org.jbpm.console.ng.gc.client.util.DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(org.jbpm.console.ng.gc.client.util.DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(DataMockSummary unit) {
                return org.jbpm.console.ng.gc.client.util.DataGridUtils.trimToColumnWidth(listGrid, this, unit.getColumn4());
            }
        };
        column4Column.setSortable(true);
        sortHandler.setComparator(column4Column, new Comparator<DataMockSummary>() {
            @Override
            public int compare(DataMockSummary o1, DataMockSummary o2) {
                return o1.getColumn4().compareTo(o2.getColumn4());
            }
        });
        listGrid.addColumn(column4Column, new ResizableHeader("Column4",  listGrid, column4Column));
        listGrid.setColumnWidth(column4Column, "100px");
    }

    @Override
    public void onResize() {
        if ((getParent().getOffsetHeight() - 120) > 0) {
            listContainer.setHeight(getParent().getOffsetHeight() - 120 + "px");
        }
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }
//
//    public ListHandler<DataMockSummary> getSortHandler() {
//        return sortHandler;
//    }

    public void refreshItems() {
        presenter.refreshList();
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
