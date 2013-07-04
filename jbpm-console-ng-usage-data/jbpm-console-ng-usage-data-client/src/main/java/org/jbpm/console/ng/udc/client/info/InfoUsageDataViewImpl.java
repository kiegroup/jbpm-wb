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

package org.jbpm.console.ng.udc.client.info;

import java.util.Comparator;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.udc.client.i8n.Constants;
import org.jbpm.console.ng.udc.client.util.ResizableHeader;
import org.jbpm.console.ng.udc.model.InfoUsageDataSummary;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

@Dependent
@Templated(value = "InfoUsageDataViewImpl.html")
public class InfoUsageDataViewImpl extends Composite implements InfoUsageDataPresenter.InfoUsageDataEventView {
    
    private Constants constants = GWT.create(Constants.class);

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    @DataField
    public FlowPanel infoViewContainer;
    
    private InfoUsageDataPresenter presenter;
    
    public DataGrid<InfoUsageDataSummary> infoListGrid;

    public SimplePager pager;

    private ListHandler<InfoUsageDataSummary> sortHandler;

    private MultiSelectionModel<InfoUsageDataSummary> selectionModel;

    @Override
    public void init(InfoUsageDataPresenter presenter) {
        this.presenter = presenter;
        initializeGridView();
        refreshInfoUsageData();
    }

    private void initializeGridView() {
        infoViewContainer.clear();
        infoListGrid = new DataGrid<InfoUsageDataSummary>();
        infoListGrid.setStyleName("table table-bordered table-striped table-hover");
        pager = new SimplePager();
        pager.setStyleName("pagination pagination-right pull-right");
        pager.setDisplay(infoListGrid);
        pager.setPageSize(30);

        infoViewContainer.add(infoListGrid);
        infoViewContainer.add(pager);

        infoListGrid.setHeight("250px");
        // Set the message to display when the table is empty.
        infoListGrid.setEmptyTableWidget(new Label(constants.No_Usage_Audited()));

        // Attach a column sort handler to the ListDataProvider to sort the
        // list.
        sortHandler = new ColumnSortEvent.ListHandler<InfoUsageDataSummary>(presenter.getAllInfoUsageSummaries());

        infoListGrid.addColumnSortHandler(sortHandler);

        infoListGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<InfoUsageDataSummary> createCheckboxManager());

        initTableColumns(selectionModel);
        presenter.addDataDisplay(infoListGrid);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initTableColumns(final SelectionModel<InfoUsageDataSummary> selectionModel) {
        // Module.
        Column<InfoUsageDataSummary, String> moduleNameColumn = new Column<InfoUsageDataSummary, String>(new TextCell()) {
            @Override
            public String getValue(InfoUsageDataSummary object) {
                GWT.log("**getModule  "+object.getModule());
                return object.getModule();
            }
        };
        moduleNameColumn.setSortable(true);

        infoListGrid.addColumn(moduleNameColumn, new ResizableHeader(constants.Module(), infoListGrid, moduleNameColumn));
        sortHandler.setComparator(moduleNameColumn, new Comparator<InfoUsageDataSummary>() {
            @Override
            public int compare(InfoUsageDataSummary o1, InfoUsageDataSummary o2) {
                return o1.getModule().compareTo(o2.getModule());
            }
        });
        infoListGrid.setColumnWidth(moduleNameColumn, "180px");

        // Components.
        Column<InfoUsageDataSummary, String> componentsNameColumn = new Column<InfoUsageDataSummary, String>(new TextCell()) {
            @Override
            public String getValue(InfoUsageDataSummary object) {
                GWT.log("**getComponents "+object.getComponents());
                return object.getComponents();
            }
        };
        componentsNameColumn.setSortable(true);

        infoListGrid.addColumn(componentsNameColumn, new ResizableHeader(constants.Component(), infoListGrid,
                componentsNameColumn));
        sortHandler.setComparator(componentsNameColumn, new Comparator<InfoUsageDataSummary>() {
            @Override
            public int compare(InfoUsageDataSummary o1, InfoUsageDataSummary o2) {
                return o1.getComponents().compareTo(o2.getComponents());
            }
        });

    }

    @Override
    public void refreshInfoUsageData() {
        presenter.refreshInfoUsageData();
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }
    
    @Override
    public MultiSelectionModel<InfoUsageDataSummary> getSelectionModel() {
        return selectionModel;
    }

}
