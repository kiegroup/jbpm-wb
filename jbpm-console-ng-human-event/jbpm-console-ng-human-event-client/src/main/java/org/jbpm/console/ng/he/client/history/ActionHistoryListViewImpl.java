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

package org.jbpm.console.ng.he.client.history;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.he.client.history.ActionHistoryPresenter.HumanEventType;
import org.jbpm.console.ng.he.client.i8n.Constants;
import org.jbpm.console.ng.he.client.util.ResizableHeader;
import org.jbpm.console.ng.he.model.HumanEventSummary;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.cell.client.NumberCell;
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
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;

@Dependent
@Templated(value = "ActionHistoryListViewImpl.html")
public class ActionHistoryListViewImpl extends Composite implements ActionHistoryPresenter.ActionHistoryView {
    private Constants constants = GWT.create(Constants.class);

    @Inject
    @DataField
    public NavLink showAllTasksNavLink;

    @Inject
    @DataField
    public NavLink showPersonalTasksNavLink;
    
    @Inject
    @DataField
    public TextBox searchBox;

    @Inject
    @DataField
    public NavLink showGroupTasksNavLink;

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

    private ActionHistoryPresenter presenter;

    public DataGrid<HumanEventSummary> myEventListGrid;

    public SimplePager pager;
    private Set<HumanEventSummary> selectedTasks;
    private ListHandler<HumanEventSummary> sortHandler;
    private MultiSelectionModel<HumanEventSummary> selectionModel;
    @Inject
    private Event<TaskSelectionEvent> taskSelection;

    private Date currentDate;
    private HumanEventType currentEventHumanType = HumanEventType.ACTIVE;

    @Override
    public void refreshHumanEvents() {
        presenter.refreshEvents(currentDate, currentEventHumanType);
    }

    @Override
    public void init(ActionHistoryPresenter presenter) {
        this.presenter = presenter;

        refreshIcon.setTitle(constants.Refresh());
        refreshIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                refreshHumanEvents();
                searchBox.setText("");
                displayNotification(constants.Events_Refreshed());
            }
        });

        currentDate = new Date();

        // By Default we will start in Grid View
        initializeGridView();

        // Filters
        showPersonalTasksNavLink.setText(constants.Personal());
        showPersonalTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showPersonalTasksNavLink.setStyleName("active");
                showGroupTasksNavLink.setStyleName("");
                showAllTasksNavLink.setStyleName("");
                currentEventHumanType = HumanEventType.PERSONAL;
                refreshHumanEvents();

            }
        });

        showGroupTasksNavLink.setText(constants.Group());
        showGroupTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showGroupTasksNavLink.setStyleName("active");
                showPersonalTasksNavLink.setStyleName("");
                showAllTasksNavLink.setStyleName("");
                currentEventHumanType = HumanEventType.GROUP;
                refreshHumanEvents();

            }
        });

        showAllTasksNavLink.setText(constants.All());
        showAllTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showGroupTasksNavLink.setStyleName("");
                showPersonalTasksNavLink.setStyleName("");
                showAllTasksNavLink.setStyleName("active");
                currentEventHumanType = HumanEventType.ALL;
                refreshHumanEvents();

            }
        });

        taskCalendarViewLabel.setText(constants.List_Human_Event());
        taskCalendarViewLabel.setStyleName("");
        
        searchBox.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 || event.getNativeKeyCode() == 32) {
                    displayNotification("Filter: |" + searchBox.getText() + "|");
                    filterTasks(searchBox.getText());
                }

            }
        });

        refreshHumanEvents();

    }
    
    public void filterTasks(String text) {
        presenter.filterTasks(text);
    }
    

    private void initializeGridView() {
        eventsViewContainer.clear();
        //currentView = HumanEventView.GRID;
        myEventListGrid = new DataGrid<HumanEventSummary>();
        myEventListGrid.setStyleName("table table-bordered table-striped table-hover");
        pager = new SimplePager();
        pager.setStyleName("pagination pagination-right pull-right");
        pager.setDisplay(myEventListGrid);
        pager.setPageSize(30);

        eventsViewContainer.add(myEventListGrid);
        eventsViewContainer.add(pager);

        myEventListGrid.setHeight("350px");
        // Set the message to display when the table is empty.
        myEventListGrid.setEmptyTableWidget(new Label(constants.No_Human_Events()));

        // Attach a column sort handler to the ListDataProvider to sort the
        // list.
        sortHandler = new ColumnSortEvent.ListHandler<HumanEventSummary>(presenter.getAllEventsSummaries());

        myEventListGrid.addColumnSortHandler(sortHandler);

        // Add a selection model so we can select cells.
        selectionModel = new MultiSelectionModel<HumanEventSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedTasks = selectionModel.getSelectedSet();
                for (HumanEventSummary ts : selectedTasks) {
                    taskSelection.fire(new TaskSelectionEvent(ts.getId()));
                }
            }
        });

        myEventListGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<HumanEventSummary> createCheckboxManager());

        initTableColumns(selectionModel);
        presenter.addDataDisplay(myEventListGrid);

    }

    private void initTableColumns(final SelectionModel<HumanEventSummary> selectionModel) {
        // idEvent
        Column<HumanEventSummary, Number> taskIdColumn = new Column<HumanEventSummary, Number>(new NumberCell()) {
            @Override
            public Number getValue(HumanEventSummary object) {
                return object.getId();
            }
        };
        taskIdColumn.setSortable(true);
        myEventListGrid.setColumnWidth(taskIdColumn, "80px");

        myEventListGrid.addColumn(taskIdColumn, new ResizableHeader(constants.Id_Event(), myEventListGrid, taskIdColumn));
        sortHandler.setComparator(taskIdColumn, new Comparator<HumanEventSummary>() {
            @Override
            public int compare(HumanEventSummary o1, HumanEventSummary o2) {
                return Long.valueOf(o1.getIdEvent()).compareTo(Long.valueOf(o2.getIdEvent()));
            }
        });

        // Human event.
        Column<HumanEventSummary, String> taskNameColumn = new Column<HumanEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(HumanEventSummary object) {
                return object.getDescriptionEvent();
            }
        };
        taskNameColumn.setSortable(true);

        myEventListGrid.addColumn(taskNameColumn, new ResizableHeader(constants.Human_Event(), myEventListGrid, taskNameColumn));
        sortHandler.setComparator(taskNameColumn, new Comparator<HumanEventSummary>() {
            @Override
            public int compare(HumanEventSummary o1, HumanEventSummary o2) {
                return o1.getDescriptionEvent().compareTo(o2.getDescriptionEvent());
            }
        });

        // Type event.
        Column<HumanEventSummary, String> typeNameColumn = new Column<HumanEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(HumanEventSummary object) {
                return object.getDescriptionEvent();
            }
        };
        taskNameColumn.setSortable(true);

        myEventListGrid.addColumn(typeNameColumn, new ResizableHeader(constants.Type_Event(), myEventListGrid, typeNameColumn));
        sortHandler.setComparator(typeNameColumn, new Comparator<HumanEventSummary>() {
            @Override
            public int compare(HumanEventSummary o1, HumanEventSummary o2) {
                return o1.getTypeEvent().compareTo(o2.getTypeEvent());
            }
        });

        // Time.
        Column<HumanEventSummary, String> dueDateColumn = new Column<HumanEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(HumanEventSummary object) {
                if (object.getEventTime() != null) {
                    return object.getEventTime().toString();
                }
                return "";
            }
        };
        dueDateColumn.setSortable(true);

        myEventListGrid.addColumn(dueDateColumn, new ResizableHeader(constants.Time(), myEventListGrid, dueDateColumn));
        sortHandler.setComparator(dueDateColumn, new Comparator<HumanEventSummary>() {
            @Override
            public int compare(HumanEventSummary o1, HumanEventSummary o2) {
                if (o1.getEventTime() == null || o2.getEventTime() == null) {
                    return 0;
                }
                return o1.getEventTime().compareTo(o2.getEventTime());
            }
        });
        


    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public MultiSelectionModel<HumanEventSummary> getSelectionModel() {
        return selectionModel;
    }
    
    public TextBox getSearchBox() {
        return searchBox;
    }
}
