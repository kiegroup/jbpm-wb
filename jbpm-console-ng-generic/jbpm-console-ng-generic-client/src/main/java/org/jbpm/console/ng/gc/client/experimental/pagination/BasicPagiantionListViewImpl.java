/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.gc.client.experimental.pagination;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.SelectionChangeEvent;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.jbpm.console.ng.gc.client.list.base.BaseViewImpl;
import org.jbpm.console.ng.gc.client.resources.GenericImages;
import org.jbpm.console.ng.gc.client.util.DataGridUtils;
import org.jbpm.console.ng.gc.client.util.ResizableHeader;
import org.uberfire.client.common.BusyPopup;

@Dependent
@Templated(value = "BasicPaginationListViewImpl.html")
public class BasicPagiantionListViewImpl extends BaseViewImpl<DataMockSummary, BasicPaginationListPresenter>
        implements BasicPaginationListPresenter.BasicPaginationListView {

    private static final String DEPLOYMENT_CONFIRM = "Are you sure that you want to undeploy the deployment unit?";

    private static final String ALL_DEPLOYMENT_CONFIRM = "Are you sure that you want to undeploy all the deployments selected?";

    private Constants constants = GWT.create(Constants.class);

    private GenericImages images = GWT.create(GenericImages.class);

    @Override
    public void init(final BasicPaginationListPresenter presenter) {
        DELETE_ACTION_IMAGE = images.deleteGridIcon();
        MSJ_NO_ITEMS_FOUND = constants.No_Items_Found();
        initializeComponents(presenter, presenter.getDataProvider(), GridSelectionModel.MULTI);
    }

    @Override
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
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(DataMockSummary unit) {
                return DataGridUtils.trimToColumnWidth(listGrid, this, unit.getId());
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
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(DataMockSummary unit) {
                return DataGridUtils.trimToColumnWidth(listGrid, this, unit.getColumn1());
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
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(DataMockSummary unit) {
                return DataGridUtils.trimToColumnWidth(listGrid, this, unit.getColumn2());
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
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(DataMockSummary unit) {
                return DataGridUtils.trimToColumnWidth(listGrid, this, unit.getColumn3());
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
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(DataMockSummary unit) {
                return DataGridUtils.trimToColumnWidth(listGrid, this, unit.getColumn4());
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

    private void actionsColumn() {
        List<HasCell<DataMockSummary, ?>> cells = new LinkedList<HasCell<DataMockSummary, ?>>();

        cells.add(new DeleteActionHasCell("Remove", new Delegate<DataMockSummary>() {
            @Override
            public void execute(DataMockSummary data) {
                if (itemsSelected != null && itemsSelected.size() > 1) {
                    if (Window.confirm(ALL_DEPLOYMENT_CONFIRM)) {
                        for (DataMockSummary item : itemsSelected) {
                            // TODO it should call a new method with a List
                            // param
                            presenter.deleteColumn(data.getId());
                        }
                        setMultiSelectionModel();
                    }
                } else {
                    if (Window.confirm(DEPLOYMENT_CONFIRM)) {
                        presenter.deleteColumn(data.getId());
                    }
                }

            }
        }));

        CompositeCell<DataMockSummary> cell = new CompositeCell<DataMockSummary>(cells);
        Column<DataMockSummary, DataMockSummary> actionsColumn = new Column<DataMockSummary, DataMockSummary>(
                cell) {
            @Override
            public DataMockSummary getValue(DataMockSummary object) {
                return object;
            }
        };
        listGrid.addColumn(actionsColumn, new ResizableHeader(constants.Actions(), listGrid, actionsColumn));
        listGrid.setColumnWidth(actionsColumn, "70px");
    }

//    public void refreshOnChangedUnit(@Observes DeployedUnitChangedEvent event) {
//        refreshItems();
//    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public void refreshItems() {
        presenter.refreshItems();
    }

    @Override
    public void multiSelectionModelChange(SelectionChangeEvent event, Set<DataMockSummary> selectedKieSession) {
        for (DataMockSummary unit : selectedKieSession) {
            //
        }
    }

    @Override
    public void simpleSelectionModelChange(SelectionChangeEvent event, DataMockSummary selectedItemSelectionModel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setGridEvents() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initializeLeftButtons() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initializeRightButtons() {
        // TODO Auto-generated method stub
    }

    @Override
    public void addHandlerPager() {
        // TODO Auto-generated method stub
    }
}