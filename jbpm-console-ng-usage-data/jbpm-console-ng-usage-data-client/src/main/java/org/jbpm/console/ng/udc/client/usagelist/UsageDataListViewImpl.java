/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.console.ng.udc.client.usagelist;

import java.util.Comparator;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.udc.client.i8n.Constants;
import org.jbpm.console.ng.udc.client.util.ResizableHeader;
import org.jbpm.console.ng.udc.client.util.UtilUsageData;
import org.jbpm.console.ng.udc.model.UsageEventSummary;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

@Dependent
@Templated(value = "UsageDataListViewImpl.html")
public class UsageDataListViewImpl extends Composite implements UsageDataPresenter.ActionHistoryView {
    private Constants constants = GWT.create(Constants.class);

    @Inject
    @DataField
    public NavLink clearEventsNavLink;

    @Inject
    @DataField
    public NavLink exportEventsNavLink;

    @Inject
    @DataField
    public TextBox searchBox;

    @Inject
    @DataField
    public NavLink showInfoEventsNavLink;

    @DataField
    public Heading taskCalendarViewLabel = new Heading(4);

    @Inject
    @DataField
    public FlowPanel eventsViewContainer;

    @Inject
    @DataField
    public IconAnchor refreshIcon;

    @Inject
    private Event<NotificationEvent> notification;

    private UsageDataPresenter presenter;

    public DataGrid<UsageEventSummary> myEventListGrid;

    public SimplePager pager;

    private ListHandler<UsageEventSummary> sortHandler;

    private MultiSelectionModel<UsageEventSummary> selectionModel;

    @Override
    public void init(UsageDataPresenter presenter) {
        this.presenter = presenter;

        refreshIcon.setTitle(constants.Refresh());
        refreshIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                refreshUsageDataCollector();
                searchBox.setText("");
                displayNotification(constants.Events_Refreshed());
            }
        });

        // By Default we will start in Grid View
        initializeGridView();

        clearEventsNavLink.setText(constants.Clear());
        clearEventsNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clearEventsNavLink.setStyleName("active");
                showInfoEventsNavLink.setStyleName("");
                exportEventsNavLink.setStyleName("");
                clearUsageData();
            }
        });

        showInfoEventsNavLink.setText(constants.Info());
        showInfoEventsNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showInfoEventsNavLink.setStyleName("active");
                clearEventsNavLink.setStyleName("");
                exportEventsNavLink.setStyleName("");
                showInfoUsageData();
            }
        });

        exportEventsNavLink.setText(constants.Export_Csv());
        exportEventsNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showInfoEventsNavLink.setStyleName("");
                clearEventsNavLink.setStyleName("");
                exportEventsNavLink.setStyleName("active");
                exportTxtEvents();
            }
        });

        taskCalendarViewLabel.setText(constants.List_Usage_Data());
        taskCalendarViewLabel.setStyleName("");

        searchBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 || event.getNativeKeyCode() == 32) {
                    displayNotification("Filter Event: |" + searchBox.getText() + "|");
                    filterEvents(searchBox.getText());
                }

            }
        });

        refreshUsageDataCollector();

    }

    public void filterEvents(String text) {
        presenter.filterEvents(text);
    }

    private void initializeGridView() {
        eventsViewContainer.clear();
        myEventListGrid = new DataGrid<UsageEventSummary>();
        myEventListGrid.setStyleName("table table-bordered table-striped table-hover");
        pager = new SimplePager();
        pager.setStyleName("pagination pagination-right pull-right");
        pager.setDisplay(myEventListGrid);
        pager.setPageSize(30);

        eventsViewContainer.add(myEventListGrid);
        eventsViewContainer.add(pager);

        myEventListGrid.setHeight("350px");
        // Set the message to display when the table is empty.
        myEventListGrid.setEmptyTableWidget(new Label(constants.No_Usage_Data()));

        // Attach a column sort handler to the ListDataProvider to sort the
        // list.
        sortHandler = new ColumnSortEvent.ListHandler<UsageEventSummary>(presenter.getAllEventsSummaries());

        myEventListGrid.addColumnSortHandler(sortHandler);

        myEventListGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<UsageEventSummary> createCheckboxManager());

        initTableColumns(selectionModel);
        presenter.addDataDisplay(myEventListGrid);

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initTableColumns(final SelectionModel<UsageEventSummary> selectionModel) {
        // Timestamp.
        Column<UsageEventSummary, String> timeColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                if (object.getTimestamp() != null) {
                    return UtilUsageData.getDateTime(object.getTimestamp(), UtilUsageData.patternDateTime);
                }
                return "";
            }
        };
        timeColumn.setSortable(true);

        myEventListGrid.addColumn(timeColumn, new ResizableHeader(constants.Time(), myEventListGrid, timeColumn));
        sortHandler.setComparator(timeColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                if (o1.getTimestamp() == null || o2.getTimestamp() == null) {
                    return 0;
                }
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });

        // Module.
        Column<UsageEventSummary, String> moduleNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getModule();
            }
        };
        moduleNameColumn.setSortable(true);

        myEventListGrid.addColumn(moduleNameColumn, new ResizableHeader(constants.Module(), myEventListGrid, moduleNameColumn));
        sortHandler.setComparator(moduleNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getModule().compareTo(o2.getModule());
            }
        });
        
        myEventListGrid.setColumnWidth(moduleNameColumn, "200px");

        // User.
        Column<UsageEventSummary, String> userNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getUser();
            }
        };
        userNameColumn.setSortable(true);

        myEventListGrid.addColumn(userNameColumn, new ResizableHeader(constants.User(), myEventListGrid, userNameColumn));
        sortHandler.setComparator(userNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getUser().compareTo(o2.getUser());
            }
        });

        // Component.
        Column<UsageEventSummary, String> componentNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getComponent();
            }
        };
        componentNameColumn.setSortable(true);

        myEventListGrid.addColumn(componentNameColumn, new ResizableHeader(constants.Component(), myEventListGrid,
                componentNameColumn));
        sortHandler.setComparator(componentNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getComponent().compareTo(o2.getComponent());
            }
        });

        // Action.
        Column<UsageEventSummary, String> actionNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getAction();
            }
        };
        actionNameColumn.setSortable(true);

        myEventListGrid
                .addColumn(actionNameColumn, new ResizableHeader(constants.Actions(), myEventListGrid, actionNameColumn));
        sortHandler.setComparator(actionNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getAction().compareTo(o2.getAction());
            }
        });

        // key
        Column<UsageEventSummary, String> taskIdColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getKey();
            }
        };
        taskIdColumn.setSortable(true);
        myEventListGrid.setColumnWidth(taskIdColumn, "80px");

        myEventListGrid.addColumn(taskIdColumn, new ResizableHeader(constants.Id_Event(), myEventListGrid, taskIdColumn));
        sortHandler.setComparator(taskIdColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        // Level.
        Column<UsageEventSummary, String> levelNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getLevel();
            }
        };
        levelNameColumn.setSortable(true);

        myEventListGrid.addColumn(levelNameColumn, new ResizableHeader(constants.Level(), myEventListGrid, levelNameColumn));
        sortHandler.setComparator(levelNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getLevel().compareTo(o2.getLevel());
            }
        });
        myEventListGrid.setColumnWidth(levelNameColumn, "140px");

        // Status.
        Column<UsageEventSummary, String> statusNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getStatus();
            }
        };
        statusNameColumn.setSortable(true);

        myEventListGrid.addColumn(statusNameColumn, new ResizableHeader(constants.Status(), myEventListGrid, statusNameColumn));
        sortHandler.setComparator(statusNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getStatus().compareTo(o2.getStatus());
            }
        });
        myEventListGrid.setColumnWidth(statusNameColumn, "140px");

    }

    @Override
    public void refreshUsageDataCollector() {
        presenter.refreshUsageDataCollector();
    }

    @Override
    public void clearUsageData() {
        presenter.clearUsageData();
    }

    @Override
    public void showInfoUsageData() {
        presenter.showInfoUsageData();
    }

    @Override
    public void exportTxtEvents() {
        presenter.exportToTxt();
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public MultiSelectionModel<UsageEventSummary> getSelectionModel() {
        return selectionModel;
    }

    public TextBox getSearchBox() {
        return searchBox;
    }
}
