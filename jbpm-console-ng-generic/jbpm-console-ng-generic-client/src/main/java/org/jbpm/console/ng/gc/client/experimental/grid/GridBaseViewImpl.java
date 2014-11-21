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
package org.jbpm.console.ng.gc.client.experimental.grid;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;


import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ga.model.DataMockSummary;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;

import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.kie.uberfire.client.common.BusyPopup;
import org.kie.uberfire.client.tables.ColumnMeta;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

import java.util.ArrayList;
import java.util.List;

@Dependent
@Templated(value = "GridBaseViewImpl.html")
public class GridBaseViewImpl extends Composite
        implements GridBasePresenter.GridBaseListView,
        RequiresResize {

    @Inject
    private PlaceManager placeManager;

    private GridBasePresenter presenter;

    @Inject
    @DataField
    public LayoutPanel listContainer;

    public ExtendedPagedTable<DataMockSummary> listGrid;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);

    

    public GridBaseViewImpl() {}

    @Override
    public void init(final GridBasePresenter presenter) {
        this.presenter = presenter;
        listContainer.clear();
        listGrid = new ExtendedPagedTable<DataMockSummary>(10, null);
        
        presenter.addDataDisplay(listGrid);
        listContainer.add(listGrid);
        this.initGridColumns();
        
        Button refreshButton = new Button();
        refreshButton.setIcon(IconType.REFRESH);
        refreshButton.addClickHandler(new ClickHandler() {

          @Override
          public void onClick(ClickEvent event) {
            presenter.refreshList();
          }
        });
        listGrid.getToolbar().add(refreshButton);
        Button createServerDataButton = new Button();
        createServerDataButton.setText("Create Server Side Data");
        createServerDataButton.addClickHandler(new ClickHandler() {

          @Override
          public void onClick(ClickEvent event) {
            presenter.createData();
          }
        });
        
        listGrid.getToolbar().add(createServerDataButton);
       
    }

    public ExtendedPagedTable<DataMockSummary> getListGrid() {
      return listGrid;
    }
    
    

    public void initGridColumns() {
        Column columnIdColumn = idColumn();
        Column column1Column = column1Column();
        Column column2Column = column2Column();
        Column column3Column = column3Column();
        Column column4Column = column4Column();

        List<ColumnMeta<DataMockSummary>> columnMetas = new ArrayList<ColumnMeta<DataMockSummary>>();
        columnMetas.add(new ColumnMeta<DataMockSummary>(columnIdColumn, "ID"));
        columnMetas.add(new ColumnMeta<DataMockSummary>(column1Column, "Column1"));
        columnMetas.add(new ColumnMeta<DataMockSummary>(column2Column, "Column2"));
        columnMetas.add(new ColumnMeta<DataMockSummary>(column3Column, "Column3"));
        columnMetas.add(new ColumnMeta<DataMockSummary>(column4Column, "Column4"));
        listGrid.addColumns(columnMetas);
    }

    private Column idColumn() {
        Column<DataMockSummary, String> columnIdColumn = new Column<DataMockSummary, String>(
                new TextCell()) {

            @Override
            public String getValue(DataMockSummary unit) {
              return unit.getId();
            }
        };
        columnIdColumn.setSortable(true);
        return columnIdColumn;
    }

    private Column column1Column() {
        Column<DataMockSummary, String> column1Column = new Column<DataMockSummary, String>(
                new TextCell()) {

           

            @Override
            public String getValue(DataMockSummary unit) {
              return unit.getColumn1();
            }
        };
        column1Column.setSortable(true);
        return column1Column;
    }

    private Column column2Column() {
        Column<DataMockSummary, String> column2Column = new Column<DataMockSummary, String>(
                new TextCell()) {

           
            @Override
            public String getValue(DataMockSummary unit) {
              return unit.getColumn2();
            }
        };
        column2Column.setSortable(true);
        return column2Column;
    }

    private Column column3Column() {
        Column<DataMockSummary, String> column3Column = new Column<DataMockSummary, String>(
                new TextCell()) {

            @Override
            public String getValue(DataMockSummary unit) {
               return unit.getColumn3();
            }
        };
        column3Column.setSortable(true);
        return column3Column;
    }

    private Column column4Column() {
        Column<DataMockSummary, String> column4Column = new Column<DataMockSummary, String>(
                new TextCell()) {


            @Override
            public String getValue(DataMockSummary unit) {
                  return unit.getColumn4();
            }
        };
        column4Column.setSortable(true);
        return column4Column;
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

//    public ListHandler<DataMockSummary> getSortHandler() {
//        return listGrid.getSortHandler();
//    }

   

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
