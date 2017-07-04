/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.client.editors.variables.list;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.AbstractListView;
import org.jbpm.workbench.common.client.util.ButtonActionCell;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.pr.client.editors.variables.edit.VariableEditPopup;
import org.jbpm.workbench.pr.client.editors.variables.history.VariableHistoryPopup;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.ProcessVariableSummary;
import org.jbpm.workbench.pr.events.ProcessInstancesUpdateEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.PopoverTextCell;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class ProcessVariableListViewImpl extends AbstractListView<ProcessVariableSummary, ProcessVariableListPresenter>
        implements ProcessVariableListPresenter.ProcessVariableListView {

    public static final String COL_ID_VARID = "varId";
    public static final String COL_ID_VARVALUE = "varValue";
    public static final String COL_ID_VARTYPE = "varType";
    public static final String COL_ID_LASTMOD = "lastMod";
    public static final String COL_ID_ACTIONS = "Actions";

    @Inject
    public VariableEditPopup variableEditPopup;

    @Inject
    public VariableHistoryPopup variableHistoryPopup;

    private Constants constants = Constants.INSTANCE;

    private Column actionsColumn;

    @Override
    public void init(final ProcessVariableListPresenter presenter) {
        List<String> bannedColumns = new ArrayList<String>();

        bannedColumns.add(COL_ID_VARID);
        bannedColumns.add(COL_ID_VARVALUE);
        bannedColumns.add(COL_ID_ACTIONS);
        List<String> initColumns = new ArrayList<String>();
        initColumns.add(COL_ID_VARID);
        initColumns.add(COL_ID_VARVALUE);
        initColumns.add(COL_ID_ACTIONS);

        super.init(presenter,
                   new GridGlobalPreferences("ProcessVariablesGrid",
                                             initColumns,
                                             bannedColumns));

        listGrid.setEmptyTableCaption(constants.No_Variables_Available());

        selectionModel = new NoSelectionModel<ProcessVariableSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {

                if (selectedRow == -1) {
                    listGrid.setRowStyles(selectedStyles);
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                } else if (listGrid.getKeyboardSelectedRow() != selectedRow) {
                    listGrid.setRowStyles(selectedStyles);
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                }

                selectedItem = selectionModel.getLastSelectedObject();
            }
        });

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager(new DefaultSelectionEventManager.EventTranslator<ProcessVariableSummary>() {

                    @Override
                    public boolean clearCurrentSelection(CellPreviewEvent<ProcessVariableSummary> event) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<ProcessVariableSummary> event) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if (BrowserEvents.CLICK.equals(nativeEvent.getType()) &&
                                // Ignore if the event didn't occur in the correct column.
                                listGrid.getColumnIndex(actionsColumn) == event.getColumn()) {
                            return DefaultSelectionEventManager.SelectAction.IGNORE;
                        }
                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }
                });

        listGrid.setSelectionModel(selectionModel,
                                   noActionColumnManager);
        listGrid.setRowStyles(selectedStyles);
    }

    @Override
    public void initColumns(ExtendedPagedTable extendedPagedTable) {
        Column<ProcessVariableSummary, ?> variableId = initProcessVariableIdColumn();
        Column<ProcessVariableSummary, ?> valueColumn = initProcessVariableValueColumn();
        Column<ProcessVariableSummary, ?> typeColumn = initProcessVariableTypeColumn();
        Column<ProcessVariableSummary, ?> lastModificationColumn = initProcessVariableLastModifiedColumn();
        actionsColumn = initActionsColumn();

        List<ColumnMeta<ProcessVariableSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessVariableSummary>>();

        columnMetas.add(new ColumnMeta<ProcessVariableSummary>(variableId,
                                                               constants.Name()));
        columnMetas.add(new ColumnMeta<ProcessVariableSummary>(valueColumn,
                                                               constants.Value()));
        columnMetas.add(new ColumnMeta<ProcessVariableSummary>(typeColumn,
                                                               constants.Type()));
        columnMetas.add(new ColumnMeta<ProcessVariableSummary>(lastModificationColumn,
                                                               constants.Last_Modification()));
        columnMetas.add(new ColumnMeta<ProcessVariableSummary>(actionsColumn,
                                                               constants.Actions()));
        extendedPagedTable.addColumns(columnMetas);
    }

    private Column<ProcessVariableSummary, ?> initProcessVariableIdColumn() {
        // Id
        Column<ProcessVariableSummary, String> variableId = new Column<ProcessVariableSummary, String>(new TextCell()) {

            @Override
            public String getValue(ProcessVariableSummary object) {
                return object.getVariableId();
            }
        };
        variableId.setSortable(true);
        variableId.setDataStoreName(COL_ID_VARID);

        return variableId;
    }

    private Column<ProcessVariableSummary, ?> initProcessVariableValueColumn() {
        // Value.
        Column<ProcessVariableSummary, String> valueColumn = new Column<ProcessVariableSummary, String>(new PopoverTextCell()) {

            @Override
            public String getValue(ProcessVariableSummary object) {
                return (object.getNewValue() != null ? object.getNewValue() : "");
            }
        };
        valueColumn.setSortable(true);
        valueColumn.setDataStoreName(COL_ID_VARVALUE);
        return valueColumn;
    }

    public Column<ProcessVariableSummary, String> initProcessVariableTypeColumn() {

        // Type.
        Column<ProcessVariableSummary, String> typeColumn = new Column<ProcessVariableSummary, String>(new TextCell()) {

            @Override
            public String getValue(ProcessVariableSummary object) {
                return object.getType();
            }
        };
        typeColumn.setSortable(true);
        typeColumn.setDataStoreName(COL_ID_VARTYPE);
        return typeColumn;
    }

    private Column<ProcessVariableSummary, ?> initProcessVariableLastModifiedColumn() {
        // Last Time Changed Date.
        Column<ProcessVariableSummary, String> lastModificationColumn = new Column<ProcessVariableSummary, String>(new TextCell()) {

            @Override
            public String getValue(ProcessVariableSummary object) {
                return DateUtils.getDateTimeStr(new Date(object.getTimestamp()));
            }
        };
        lastModificationColumn.setSortable(true);
        lastModificationColumn.setDataStoreName(COL_ID_LASTMOD);
        return lastModificationColumn;
    }

    private Column initActionsColumn() {

        List<HasCell<ProcessVariableSummary, ?>> cells = new LinkedList<HasCell<ProcessVariableSummary, ?>>();

        cells.add(new EditVariableActionHasCell(constants.Edit(),
                                                new Delegate<ProcessVariableSummary>() {
                                                    @Override
                                                    public void execute(ProcessVariableSummary variable) {
                                                        variableEditPopup.show(variable.getServerTemplateId(),
                                                                               variable.getDeploymentId(),
                                                                               variable.getProcessInstanceId(),
                                                                               variable.getVariableId(),
                                                                               (variable.getNewValue() != null ? variable.getNewValue() : ""));
                                                    }
                                                }));

        cells.add(new VariableHistoryActionHasCell(constants.History(),
                                                   new Delegate<ProcessVariableSummary>() {
                                                       @Override
                                                       public void execute(final ProcessVariableSummary variable) {
                                                           showBusyIndicator(constants.Loading());
                                                           presenter.loadVariableHistory(new ParameterizedCommand<List<ProcessVariableSummary>>() {
                                                                                             @Override
                                                                                             public void execute(final List<ProcessVariableSummary> processVariableSummaries) {
                                                                                                 hideBusyIndicator();
                                                                                                 variableHistoryPopup.show(variable.getVariableId(),
                                                                                                                           processVariableSummaries);
                                                                                             }
                                                                                         },
                                                                                         variable.getVariableId());
                                                       }
                                                   }));

        CompositeCell<ProcessVariableSummary> cell = new CompositeCell<ProcessVariableSummary>(cells);
        Column<ProcessVariableSummary, ProcessVariableSummary> actionsColumn = new Column<ProcessVariableSummary, ProcessVariableSummary>(cell) {
            @Override
            public ProcessVariableSummary getValue(ProcessVariableSummary object) {
                return object;
            }
        };
        actionsColumn.setDataStoreName(COL_ID_ACTIONS);
        return actionsColumn;
    }

    public void formClosed(@Observes ProcessInstancesUpdateEvent closed) {
        presenter.refreshGrid();
    }

    protected class EditVariableActionHasCell extends ButtonActionCell<ProcessVariableSummary> {

        public EditVariableActionHasCell(final String text,
                                         final ActionCell.Delegate<ProcessVariableSummary> delegate) {
            super(text,
                  delegate);
        }

        @Override
        public void render(final Cell.Context context,
                           final ProcessVariableSummary value,
                           final SafeHtmlBuilder sb) {
            if (presenter.getProcessInstanceStatus() == ProcessInstance.STATE_ACTIVE) {
                super.render(context,
                             value,
                             sb);
            }
        }
    }

    protected class VariableHistoryActionHasCell extends ButtonActionCell<ProcessVariableSummary> {

        public VariableHistoryActionHasCell(final String text,
                                            final ActionCell.Delegate<ProcessVariableSummary> delegate) {
            super(text,
                  delegate);
        }

        @Override
        public void render(final Cell.Context context,
                           final ProcessVariableSummary value,
                           final SafeHtmlBuilder sb) {
            super.render(context,
                         value,
                         sb);
        }
    }
}