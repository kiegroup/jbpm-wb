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
package org.jbpm.console.ng.es.client.editors.requestlist;


import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;



import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import java.util.Comparator;
import java.util.Set;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.client.util.ResizableHeader;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.model.events.RequestSelectionEvent;

import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;


@Dependent
@Templated(value = "RequestListViewImpl.html")
public class RequestListViewImpl extends Composite
        implements
        RequestListPresenter.InboxView {

    @Inject
    private PlaceManager placeManager;
    private RequestListPresenter presenter;
    @Inject
    @DataField
    public Button createRequestButton;
    @Inject
    @DataField
    public Button refreshRequestsButton;
    @Inject
    @DataField
    public FlowPanel listContainer;
    
    @Inject
    @DataField
    public Button initButton;
    
    @Inject
    @DataField
    public DataGrid<RequestSummary> myRequestListGrid;
    @Inject
    @DataField
    public SimplePager pager;
    @Inject
    @DataField
    public CheckBox showCompletedCheck;
    
    private Set<RequestSummary> selectedRequests;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<RequestSelectionEvent> requestSelection;
    private ListHandler<RequestSummary> sortHandler;
    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(RequestListPresenter presenter) {
        this.presenter = presenter;
        
        listContainer.add(myRequestListGrid);
        listContainer.add(pager);
        
        myRequestListGrid.setHeight("350px");

//         Set the message to display when the table is empty.
        myRequestListGrid.setEmptyTableWidget(new Label(constants.Hooray_you_don_t_have_any_pending_Task__()));

//         Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler =
                new ListHandler<RequestSummary>(presenter.getDataProvider().getList());
        myRequestListGrid.addColumnSortHandler(sortHandler);

//         Create a Pager to control the table.

        pager.setDisplay(myRequestListGrid);
        pager.setPageSize(6);

//         Add a selection model so we can select cells.
        final MultiSelectionModel<RequestSummary> selectionModel =
                new MultiSelectionModel<RequestSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedRequests = selectionModel.getSelectedSet();
                for (RequestSummary r : selectedRequests) {
                    requestSelection.fire(new RequestSelectionEvent(r.getId()));
                }
            }
        });

        myRequestListGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager
                .<RequestSummary>createCheckboxManager());

        initTableColumns(selectionModel);



        presenter.addDataDisplay(myRequestListGrid);

    }

   

    @EventHandler("refreshRequestsButton")
    public void refreshRequestsButton(ClickEvent e) {
        Boolean isChecked = showCompletedCheck.getValue();
        presenter.refreshRequests(isChecked);
    }
    
    @EventHandler("initButton")
    public void initButton(ClickEvent e) {
        
        presenter.init();
    }
    
    @EventHandler("createRequestButton")
    public void createRequestButton(ClickEvent e) {
        presenter.createRequest();
    }

    private void initTableColumns(final SelectionModel<RequestSummary> selectionModel) {
//         Checkbox column. This table will uses a checkbox column for selection.
//         Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
//         mouse selection.

        Column<RequestSummary, Boolean> checkColumn =
                new Column<RequestSummary, Boolean>(new CheckboxCell(true,
                false)) {
            @Override
            public Boolean getValue(RequestSummary object) {
                // Get the value from the selection model.
                return selectionModel.isSelected(object);
            }
        };
        myRequestListGrid.addColumn(checkColumn,
                SafeHtmlUtils.fromSafeConstant("<br/>"));
        myRequestListGrid.setColumnWidth(checkColumn, "40px");

        // Id
        Column<RequestSummary, Number> taskIdColumn =
                new Column<RequestSummary, Number>(new NumberCell()) {
            @Override
            public Number getValue(RequestSummary object) {
                return object.getId();
            }
        };
        taskIdColumn.setSortable(true);
        sortHandler.setComparator(taskIdColumn,
                new Comparator<RequestSummary>() {
            public int compare(RequestSummary o1,
                    RequestSummary o2) {
                return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
            }
        });
        
        myRequestListGrid.addColumn(taskIdColumn,
                new ResizableHeader(constants.Id(), myRequestListGrid, taskIdColumn));
        myRequestListGrid.setColumnWidth(taskIdColumn, "40px");

        // Task name.
        Column<RequestSummary, String> taskNameColumn =
                new Column<RequestSummary, String>(new EditTextCell()) {
            @Override
            public String getValue(RequestSummary object) {
                return object.getCommandName();
            }
        };
        taskNameColumn.setSortable(true);
        sortHandler.setComparator(taskNameColumn,
                new Comparator<RequestSummary>() {
            public int compare(RequestSummary o1,
                    RequestSummary o2) {
                return o1.getCommandName().compareTo(o2.getCommandName());
            }
        });
        myRequestListGrid.addColumn(taskNameColumn,
                new ResizableHeader("Job Name", myRequestListGrid, taskNameColumn));


        // Status
        Column<RequestSummary, String> statusColumn =
                new Column<RequestSummary, String>(new EditTextCell()) {
            @Override
            public String getValue(RequestSummary object) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable(true);
        sortHandler.setComparator(statusColumn,
                new Comparator<RequestSummary>() {
            public int compare(RequestSummary o1,
                    RequestSummary o2) {
                return o1.getStatus().compareTo(o2.getStatus());
            }
        });
        myRequestListGrid.addColumn(statusColumn,
                new ResizableHeader("Status", myRequestListGrid, taskNameColumn));
        myRequestListGrid.setColumnWidth(statusColumn, "100px");


        // Due Date.
        Column<RequestSummary, String> dueDateColumn = new Column<RequestSummary, String>(new TextCell()) {
            @Override
            public String getValue(RequestSummary object) {
                if (object.getTime() != null) {
                    return object.getTime().toString();
                }
                return "";
            }
        };
        dueDateColumn.setSortable(true);

        myRequestListGrid.addColumn(dueDateColumn,
                new ResizableHeader(constants.Due_On(), myRequestListGrid, dueDateColumn));





        Column<RequestSummary, String> detailsColumn =
                new Column<RequestSummary, String>(new ButtonCell()) {
            @Override
            public String getValue(RequestSummary task) {
                return "Details";
            }
        };

        detailsColumn.setFieldUpdater(new FieldUpdater<RequestSummary, String>() {
            @Override
            public void update(int index,
                    RequestSummary request,
                    String value) {
                placeManager.goTo(new DefaultPlaceRequest(constants.Request_Details_Perspective_Errai()));
                requestSelection.fire(new RequestSelectionEvent(request.getId()));
            }
        });

        myRequestListGrid.addColumn(detailsColumn,
                new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant(constants.Details())));
        myRequestListGrid.setColumnWidth(detailsColumn, "100px");


    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

   

    public CheckBox getShowCompletedCheck() {
        return showCompletedCheck;
    }

    public DataGrid<RequestSummary> getDataGrid() {
        return myRequestListGrid;
    }

    public ListHandler<RequestSummary> getSortHandler() {
        return sortHandler;
    }
}
