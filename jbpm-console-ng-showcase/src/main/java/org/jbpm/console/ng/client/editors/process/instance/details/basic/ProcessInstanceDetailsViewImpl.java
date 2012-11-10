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
package org.jbpm.console.ng.client.editors.process.instance.details.basic;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.SimplePager;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;



import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import java.util.Comparator;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.shared.model.VariableSummary;
import org.jbpm.console.ng.client.util.ResizableHeader;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import org.jbpm.console.ng.client.i18n.Constants;

@Dependent
@Templated(value = "ProcessInstanceDetailsViewImpl.html")
public class ProcessInstanceDetailsViewImpl extends Composite
        implements
        ProcessInstanceDetailsPresenter.InboxView {

    private ProcessInstanceDetailsPresenter presenter;
    @Inject
    @DataField
    public TextBox processIdText;
    @Inject
    @DataField
    public TextBox processNameText;
    @Inject
    @DataField
    public ListBox currentActivitiesListBox;
    @Inject
    @DataField
    public TextArea logTextArea;
    @Inject
    @DataField
    public Button refreshButton;
    @Inject
    @DataField
    public DataGrid<VariableSummary> processDataGrid;
    @Inject
    @DataField
    public SimplePager pager;
    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    private ColumnSortEvent.ListHandler<VariableSummary> sortHandler;
    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);    
    
    @Override
    public void init(ProcessInstanceDetailsPresenter presenter) {
        this.presenter = presenter;

        processDataGrid.setWidth("100%");
        processDataGrid.setHeight("200px");

        // Set the message to display when the table is empty.
        processDataGrid.setEmptyTableWidget(new Label(constants.There_is_no_variable_information_to_show()));

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler =
                new ColumnSortEvent.ListHandler<VariableSummary>(presenter.getDataProvider().getList());

        processDataGrid.addColumnSortHandler(sortHandler);

        // Create a Pager to control the table.

        pager.setDisplay(processDataGrid);
        pager.setPageSize(6);


        initTableColumns();

        presenter.addDataDisplay(processDataGrid);
    }

    @EventHandler("refreshButton")
    public void refreshButton(ClickEvent e) {
        presenter.refreshProcessInstanceData(processIdText.getText(),processNameText.getText());
    }

    public TextBox getProcessIdText() {
        return processIdText;
    }

    public ListBox getCurrentActivitiesListBox() {
        return currentActivitiesListBox;
    }

    public TextArea getLogTextArea() {
        return logTextArea;
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public TextBox getProcessNameText() {
        return processNameText;
    }

    private void initTableColumns() {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.


        // Id 
        Column<VariableSummary, String> variableId =
                new Column<VariableSummary, String>(new TextCell()) {
                    @Override
                    public String getValue(VariableSummary object) {
                        return object.getVariableId();
                    }
                };
        variableId.setSortable(true);

        processDataGrid.addColumn(variableId,
                new ResizableHeader(constants.Variable(), processDataGrid, variableId));
        sortHandler.setComparator(variableId,
                new Comparator<VariableSummary>() {
                    public int compare(VariableSummary o1,
                            VariableSummary o2) {
                        return o1.getVariableId().compareTo(o2.getVariableId());
                    }
                });

        // Value.
        Column<VariableSummary, String> valueColumn =
                new Column<VariableSummary, String>(new TextCell()) {
                    @Override
                    public String getValue(VariableSummary object) {
                        return object.getNewValue();
                    }
                };
        valueColumn.setSortable(true);

        processDataGrid.addColumn(valueColumn,
                new ResizableHeader(constants.Value(), processDataGrid, valueColumn));
        sortHandler.setComparator(valueColumn,
                new Comparator<VariableSummary>() {
                    public int compare(VariableSummary o1,
                            VariableSummary o2) {
                        return o1.getNewValue().compareTo(o2.getNewValue());
                    }
                });


        

        // Last Time Changed Date.
        Column<VariableSummary, String> dueDateColumn = new Column<VariableSummary, String>(new TextCell()) {
            @Override
            public String getValue(VariableSummary object) {

                return object.getTimestamp();

            }
        };
        dueDateColumn.setSortable(true);

        processDataGrid.addColumn(dueDateColumn,
                new ResizableHeader(constants.Last_Time_Changed(), processDataGrid, dueDateColumn));
        sortHandler.setComparator(dueDateColumn,
                new Comparator<VariableSummary>() {
                    public int compare(VariableSummary o1,
                            VariableSummary o2) {
                       
                        return o1.getTimestamp().compareTo(o2.getTimestamp());
                    }
                });

        Column<VariableSummary, String> editColumn =
                new Column<VariableSummary, String>(new ButtonCell()) {
                    @Override
                    public String getValue(VariableSummary task) {
                        return "View History";
                    }
                };

        editColumn.setFieldUpdater(new FieldUpdater<VariableSummary, String>() {
            @Override
            public void update(int index,
                    VariableSummary task,
                    String value) {

                PlaceRequest placeRequestImpl = new DefaultPlaceRequest(constants.Variable_History_Perspective());
                placeRequestImpl.addParameter("variableId", task.getVariableId());
                placeManager.goTo(placeRequestImpl);

            }
        });

        processDataGrid.addColumn(editColumn,
                new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant(constants.View_History())));




    }
}
