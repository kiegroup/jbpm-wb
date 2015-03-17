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
package org.jbpm.console.ng.pr.client.editors.variables.list;

import com.google.gwt.cell.client.*;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.pr.client.editors.variables.edit.VariableEditPopup;
import org.jbpm.console.ng.pr.client.editors.variables.history.VariableHistoryPopup;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.model.ProcessVariableSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Dependent
public class ProcessVariableListViewImpl extends AbstractListView<ProcessVariableSummary, ProcessVariableListPresenter>
        implements ProcessVariableListPresenter.ProcessVariableListView {

    interface Binder
            extends
            UiBinder<Widget, ProcessVariableListViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    private Constants constants = GWT.create(Constants.class);

    private ProcessRuntimeImages images = GWT.create(ProcessRuntimeImages.class);

    private Column actionsColumn;

    @Inject
    public VariableEditPopup variableEditPopup;

    @Inject
    public VariableHistoryPopup variableHistoryPopup;

    @Override
    public void init(final ProcessVariableListPresenter presenter) {
        List<String> bannedColumns = new ArrayList<String>();

        bannedColumns.add(constants.Name());
        bannedColumns.add(constants.Value());
        bannedColumns.add(constants.Actions());
        List<String> initColumns = new ArrayList<String>();
        initColumns.add(constants.Name());
        initColumns.add(constants.Value());
        initColumns.add(constants.Actions());

        super.init(presenter, new GridGlobalPreferences("ProcessVariablesGrid", initColumns, bannedColumns));

        listGrid.setEmptyTableCaption(constants.No_Variables_Available());

        selectionModel = new NoSelectionModel<ProcessVariableSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {

                boolean close = false;
                if (selectedRow == -1) {
                    listGrid.setRowStyles(selectedStyles);
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();

                } else if (listGrid.getKeyboardSelectedRow() != selectedRow) {
                    listGrid.setRowStyles(selectedStyles);
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                } else {
                    close = true;
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
                        if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                            // Ignore if the event didn't occur in the correct column.
                            if (listGrid.getColumnIndex(actionsColumn) == event.getColumn()) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }

                        }

                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }

                });

        listGrid.setSelectionModel(selectionModel, noActionColumnManager);
        listGrid.setRowStyles(selectedStyles);
    }

    @Override
    public void initColumns() {
        Column<ProcessVariableSummary, ?> variableId = initProcessVariableIdColumn();
        Column<ProcessVariableSummary, ?> valueColumn = initProcessVariableValueColumn();
        Column<ProcessVariableSummary, ?> typeColumn = initProcessVariableTypeColumn();
        Column<ProcessVariableSummary, ?> lastModificationColumn = initProcessVariableLastModifiedColumn();
        actionsColumn = initActionsColumn();

        List<ColumnMeta<ProcessVariableSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessVariableSummary>>();
        columnMetas.add(new ColumnMeta<ProcessVariableSummary>(variableId, constants.Name()));
        columnMetas.add(new ColumnMeta<ProcessVariableSummary>(valueColumn, constants.Value()));
        columnMetas.add(new ColumnMeta<ProcessVariableSummary>(typeColumn, constants.Type()));
        columnMetas.add(new ColumnMeta<ProcessVariableSummary>(lastModificationColumn, constants.Last_Modification()));
        columnMetas.add(new ColumnMeta<ProcessVariableSummary>(actionsColumn, constants.Actions()));
        listGrid.addColumns(columnMetas);
    }

    @Override
    public void initFilters() {

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

        return variableId;
    }

    private Column<ProcessVariableSummary, ?> initProcessVariableValueColumn() {
        // Value.
        Column<ProcessVariableSummary, String> valueColumn = new Column<ProcessVariableSummary, String>(new TextCell()) {

            @Override
            public String getValue(ProcessVariableSummary object) {
                return object.getNewValue();
            }
        };
        valueColumn.setSortable(true);

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

        return typeColumn;
    }

    private Column<ProcessVariableSummary, ?> initProcessVariableLastModifiedColumn() {
        // Last Time Changed Date.
        Column<ProcessVariableSummary, String> lastModificationColumn = new Column<ProcessVariableSummary, String>(new TextCell()) {

            @Override
            public String getValue(ProcessVariableSummary object) {

                Date lastMofidication = new Date(object.getTimestamp());
                DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
                return format.format(lastMofidication);

            }
        };
        lastModificationColumn.setSortable(true);

        return lastModificationColumn;
    }

    private Column initActionsColumn() {

        List<HasCell<ProcessVariableSummary, ?>> cells = new LinkedList<HasCell<ProcessVariableSummary, ?>>();

        cells.add(new EditVariableActionHasCell("Edit Variable", new Delegate<ProcessVariableSummary>() {
            @Override
            public void execute(ProcessVariableSummary variable) {
                variableEditPopup.show( variable.getProcessInstanceId(),variable.getVariableId(),variable.getNewValue() );

            }
        }));

        cells.add(new VariableHistoryActionHasCell("Variable History", new Delegate<ProcessVariableSummary>() {
            @Override
            public void execute(ProcessVariableSummary variable) {
                variableHistoryPopup.show( variable.getProcessInstanceId(),variable.getVariableId() );

            }
        }));

        CompositeCell<ProcessVariableSummary> cell = new CompositeCell<ProcessVariableSummary>(cells);
        Column<ProcessVariableSummary, ProcessVariableSummary> actionsColumn = new Column<ProcessVariableSummary, ProcessVariableSummary>(cell) {
            @Override
            public ProcessVariableSummary getValue(ProcessVariableSummary object) {
                return object;
            }
        };
        return actionsColumn;
    }

    public void formClosed(@Observes ProcessInstancesUpdateEvent closed) {
            presenter.refreshGrid();
    }

    private class EditVariableActionHasCell implements HasCell<ProcessVariableSummary, ProcessVariableSummary> {

        private ActionCell<ProcessVariableSummary> cell;

        public EditVariableActionHasCell(String text,
                                         Delegate<ProcessVariableSummary> delegate) {
            cell = new ActionCell<ProcessVariableSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context,
                                   ProcessVariableSummary value,
                                   SafeHtmlBuilder sb) {
                    if (presenter.getProcessInstanceStatus() == ProcessInstance.STATE_ACTIVE) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.editGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Edit_Variable() + "'>");
                        mysb.append(imageProto.getSafeHtml());
                        mysb.appendHtmlConstant("</span>");
                        sb.append(mysb.toSafeHtml());
                    }
                }
            };
        }

        @Override
        public Cell<ProcessVariableSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessVariableSummary, ProcessVariableSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessVariableSummary getValue(ProcessVariableSummary object) {
            return object;
        }

    }

    private class VariableHistoryActionHasCell implements HasCell<ProcessVariableSummary, ProcessVariableSummary> {

        private ActionCell<ProcessVariableSummary> cell;

        public VariableHistoryActionHasCell(String text,
                                            Delegate<ProcessVariableSummary> delegate) {
            cell = new ActionCell<ProcessVariableSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context,
                                   ProcessVariableSummary value,
                                   SafeHtmlBuilder sb) {

                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.historyGridIcon());
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='" + constants.Variables_History() + "'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };
        }

        @Override
        public Cell<ProcessVariableSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessVariableSummary, ProcessVariableSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessVariableSummary getValue(ProcessVariableSummary object) {
            return object;
        }

    }

}
