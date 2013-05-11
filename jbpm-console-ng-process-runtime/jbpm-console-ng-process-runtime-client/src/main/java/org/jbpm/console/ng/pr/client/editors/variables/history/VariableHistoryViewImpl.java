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

package org.jbpm.console.ng.pr.client.editors.variables.history;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.SimplePager;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.util.ResizableHeader;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import org.jbpm.console.ng.pr.model.VariableSummary;

@Dependent
@Templated(value = "VariableHistoryViewImpl.html")
public class VariableHistoryViewImpl extends Composite implements VariableHistoryPresenter.PopupView {
    private Constants constants = GWT.create(Constants.class);

    private long processInstanceId;

    private String variableId;

    private VariableHistoryPresenter presenter;

    @Inject
    @DataField
    public Label variableHistoryLabel;

    @Inject
    @DataField
    public Label variableNameText;

    @Inject
    @DataField
    public FlowPanel listContainer;

    @Inject
    @DataField
    public DataGrid<VariableSummary> processVarListGrid;

    @Inject
    @DataField
    public SimplePager pager;

    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(VariableHistoryPresenter presenter) {
        this.presenter = presenter;

        listContainer.add(processVarListGrid);
        listContainer.add(pager);

        processVarListGrid.setHeight("200px");
        // Set the message to display when the table is empty.
        Label emptyTable = new Label(constants.No_History_For_This_Variable());
        emptyTable.setStyleName("");
        processVarListGrid.setEmptyTableWidget(emptyTable);

        // Create a Pager to control the table.

        pager.setDisplay(processVarListGrid);
        pager.setPageSize(5);

        // Value.
        Column<VariableSummary, String> valueColumn = new Column<VariableSummary, String>(new TextCell()) {
            @Override
            public String getValue(VariableSummary object) {
                return object.getNewValue();
            }
        };

        processVarListGrid.addColumn(valueColumn, new ResizableHeader(constants.Value(), processVarListGrid, valueColumn));

        // Value.
        Column<VariableSummary, String> oldValueColumn = new Column<VariableSummary, String>(new TextCell()) {
            @Override
            public String getValue(VariableSummary object) {
                return object.getOldValue();
            }
        };

        processVarListGrid.addColumn(oldValueColumn, new ResizableHeader(constants.Previous_Value(), processVarListGrid,
                oldValueColumn));

        // Last Time Changed Date.
        Column<VariableSummary, String> dueDateColumn = new Column<VariableSummary, String>(new TextCell()) {
            @Override
            public String getValue(VariableSummary object) {

                return object.getTimestamp();

            }
        };
        dueDateColumn.setSortable(true);

        processVarListGrid.addColumn(dueDateColumn, new ResizableHeader(constants.Last_Modification(), processVarListGrid,
                dueDateColumn));

        presenter.addDataDisplay(processVarListGrid);

        variableHistoryLabel.setText(constants.Variable_History());
        variableHistoryLabel.setStyleName("");

    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;

    }

    @Override
    public void setVariableId(String variableId) {
        this.variableId = variableId;
        this.variableNameText.setText(variableId);
    }

    @Override
    public long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public String getVariableId() {
        return variableId;
    }

}
