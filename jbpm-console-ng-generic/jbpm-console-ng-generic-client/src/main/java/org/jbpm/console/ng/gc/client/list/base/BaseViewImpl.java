/*
* Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.console.ng.gc.client.list.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jbpm.console.ng.gc.client.util.DataGridUtils;
import org.jbpm.console.ng.gc.client.util.DataGridUtils.ActionsCRUD;
import org.jbpm.console.ng.gc.client.util.ResizableHeader;
import org.jbpm.console.ng.ht.model.GenericSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public abstract class BaseViewImpl<T extends GenericSummary, P> extends GenericActions<T> implements GridViewContainer<T>,
        ButtonsPanelContainer, PagerContainer, RequiresResize {

    private static final String WIDTH_COLUMN_CHECKBOX = "40px";

    protected P presenter;

    @Inject
    protected Identity identity;

    protected DataGrid<T> listGrid;

    protected ListHandler<T> sortHandler;

    private String currentFilter = "";

    @Inject
    private Event<NotificationEvent> notification;

    /* Layout */
    @Inject
    @DataField
    public LayoutPanel viewContainer;

    /* pager */
    @Inject
    @DataField
    public SimplePager pager;

    @Inject
    protected PlaceManager placeManager;

    protected MultiSelectionModel<T> selectionModel;

    protected Set<T> itemsSelected;

    // msjs
    protected String MSJ_NO_ITEMS_FOUND = genericConstants.No_Items_Found();

    protected void initializeComponents(P presenter, ListDataProvider<T> dataProvider, GridSelectionModel gridSelectionModel) {
        this.presenter = presenter;
        this.initializeGridView(dataProvider, gridSelectionModel);
        this.initializeLeftButtons();
        this.initializeRightButtons();
    }

    protected void initializeGridView(ListDataProvider<T> dataProvider, GridSelectionModel gridSelectionModel) {
        viewContainer.clear();
        listGrid = new DataGrid<T>();
        listGrid.setStyleName(GRID_STYLE);
        this.initPager();
        viewContainer.add(listGrid);
        listGrid.setEmptyTableWidget(new HTMLPanel(MSJ_NO_ITEMS_FOUND));
        sortHandler = new ColumnSortEvent.ListHandler<T>(dataProvider.getList());
        listGrid.getColumnSortList().setLimit(1);
        this.setSelectionModel(gridSelectionModel);
        this.setGridEvents();
        this.initGridColumns();
        listGrid.addColumnSortHandler(sortHandler);
        dataProvider.addDataDisplay(listGrid);
        this.refreshItems();
    }

    private void initPager() {
        pager.setDisplay(listGrid);
        pager.setStyleName(STYLE_PAGER);
        pager.setPageSize(DataGridUtils.pageSize);
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public DataGrid<T> getListGrid() {
        return listGrid;
    }

    @Override
    public void onResize() {
        if ((getParent().getOffsetHeight() - 120) > 0) {
            viewContainer.setHeight(getParent().getOffsetHeight() - 120 + "px");
            viewContainer.setWidth(getParent().getOffsetWidth() + "px");
        }

    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(String currentFilter) {
        this.currentFilter = currentFilter;
    }
    
    public ListHandler<T> getSortHandler() {
        return sortHandler;
    }

    protected void actionsColumns() {
        List<HasCell<T, ?>> cells = new LinkedList<HasCell<T, ?>>();

        cells.add(new ReadActionHasCell(ActionsCRUD.READ.getDescription(), new ActionCell.Delegate<T>() {
            @Override
            public void execute(T item) {
                Long id = (Long) item.getId();
                DataGridUtils.paintRowSelected(listGrid, String.valueOf(id));
                readItem(id);
            }
        }));

        cells.add(new UpdateActionHasCell(ActionsCRUD.UPDATE.getDescription(), new ActionCell.Delegate<T>() {
            @Override
            public void execute(T item) {
                Long id = (Long) item.getId();
                DataGridUtils.paintRowSelected(listGrid, String.valueOf(id));
                updateItem(id);
            }
        }));

        cells.add(new DeleteActionHasCell(ActionsCRUD.DELETE.getDescription(), new ActionCell.Delegate<T>() {
            @Override
            public void execute(T item) {
                Long id = (Long) item.getId();
                DataGridUtils.paintRowSelected(listGrid, String.valueOf(id));
                deleteItem(id);
            }
        }));

        CompositeCell<T> cell = new CompositeCell<T>(cells);
        Column<T, T> actionsColumn = new Column<T, T>(cell) {
            @Override
            public T getValue(T object) {
                return object;
            }
        };
        listGrid.addColumn(actionsColumn, new ResizableHeader(genericConstants.Actions(), listGrid, actionsColumn));
        listGrid.setColumnWidth(actionsColumn, "120px");
    }

    @Override
    public void setSelectionModel(GridSelectionModel gridSelectionModel) {
        switch (gridSelectionModel) {
        case SIMPLE:
            setSimpleSelectionModel();
            break;
        case MULTI:
            setMultiSelectionModel();
            break;
        }

    }

    private void setSimpleSelectionModel() {
        GWT.log("init simple");
        final SingleSelectionModel<T> selectionModel = new SingleSelectionModel<T>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                T item = selectionModel.getSelectedObject();
                if (item != null) {
                    DataGridUtils.paintRowSelected(listGrid, String.valueOf(item.getId()));
                }
                simpleSelectionModelChange(event, item);
            }
        });
        listGrid.setSelectionModel((SelectionModel<? super T>) selectionModel);
    }

    protected void setMultiSelectionModel() {
        if(itemsSelected == null){
            this.addRowSelector();
        }
        final MultiSelectionModel<T> selectionModel = new MultiSelectionModel<T>();
        itemsSelected = null;
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                itemsSelected = selectionModel.getSelectedSet();
                for (T item : itemsSelected) {
                    DataGridUtils.paintRowSelected(listGrid, String.valueOf(item.getId()));
                }
                multiSelectionModelChange(event, itemsSelected);
            }
        });
        listGrid.setSelectionModel((SelectionModel<? super T>) selectionModel,
                DefaultSelectionEventManager.<T> createCheckboxManager());
        this.selectionModel = selectionModel;
    }

    private void addRowSelector() {
        final Column<T, Boolean> rowSelectColumn = new Column<T, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(T value) {
                return value != null ? selectionModel.isSelected(value) : false;
            }
        };
        listGrid.addColumn(rowSelectColumn, new ResizableHeader("", listGrid, rowSelectColumn));
        listGrid.setColumnWidth(rowSelectColumn, WIDTH_COLUMN_CHECKBOX);
    }
    
    @Override
    protected void createItem() {
        ((BaseGenericCRUD) presenter).createItem();
    }
    
    @Override
    protected void deleteItem(Long id) {
        ((BaseGenericCRUD) presenter).deleteItem(id);
    }
    
    @Override
    protected void updateItem(Long id) {
        ((BaseGenericCRUD) presenter).updateItem(id);
    }
    
    @Override
    protected void readItem(Long id) {
        ((BaseGenericCRUD) presenter).readItem(id);
    }
    
}