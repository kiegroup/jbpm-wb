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
package org.jbpm.console.ng.pr.client.editors.instance.userTask.history;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.model.ProcessVariableSummary;
import org.jbpm.console.ng.pr.model.UserTaskSummary;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
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

@Dependent
public class UserTaskListHistoryViewImpl extends AbstractListView<UserTaskSummary, UserTaskListHistoryPresenter>
        implements UserTaskListHistoryPresenter.UserTaskListHistoryView {

    interface Binder
            extends
            UiBinder<Widget, UserTaskListHistoryViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    private Constants constants = GWT.create(Constants.class);

    private ProcessRuntimeImages images = GWT.create(ProcessRuntimeImages.class);

    private Column actionsColumn;

    @Override
    public void init(final UserTaskListHistoryPresenter presenter) {
        List<String> bannedColumns = new ArrayList<String>();

        bannedColumns.add(constants.Name());
        bannedColumns.add(constants.User_Task_Description());
        bannedColumns.add(constants.Actions());
        List<String> initColumns = new ArrayList<String>();
        initColumns.add(constants.Name());
        initColumns.add(constants.User_Task_Description());
        initColumns.add(constants.Actions());

        super.init(presenter, new GridGlobalPreferences("UserTaskListHistoryGrid", initColumns, bannedColumns));

        listGrid.setEmptyTableCaption(constants.No_Variables_Available());

        selectionModel = new NoSelectionModel<UserTaskSummary>();
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
                .createCustomManager(new DefaultSelectionEventManager.EventTranslator<UserTaskSummary>() {

                    @Override
                    public boolean clearCurrentSelection(CellPreviewEvent<UserTaskSummary> event) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<UserTaskSummary> event) {
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
        Column name = initTaskNameColumn();
        Column description = initTaskDescriptionColumn();
        Column status = initTaskStatusColumn();
        Column createdOn = initTaskCreatedOnColumn();
        Column dueDate = initTaskDueColumn();
        Column owner = initTaskActualownerIdColumn();
        Column createbyId = initTaskCreateByIdColumn();
        actionsColumn = initActionsColumn();
        List<ColumnMeta<UserTaskSummary>> columnMetas = new ArrayList<ColumnMeta<UserTaskSummary>>();
        columnMetas.add(new ColumnMeta<UserTaskSummary>(name, constants.Name()));
        columnMetas.add(new ColumnMeta<UserTaskSummary>(description, constants.User_Task_Description()));
        columnMetas.add(new ColumnMeta<UserTaskSummary>(status, constants.User_Task_Status()));
        columnMetas.add(new ColumnMeta<UserTaskSummary>(owner, constants.User_Task_Owner()));
        columnMetas.add(new ColumnMeta<UserTaskSummary>(createbyId, constants.User_Task_CreatedBy()));
        columnMetas.add(new ColumnMeta<UserTaskSummary>(createdOn, constants.User_Task_CreatedOn()));
        columnMetas.add(new ColumnMeta<UserTaskSummary>(dueDate, constants.User_Task_DueDate()));
        columnMetas.add(new ColumnMeta<UserTaskSummary>(actionsColumn, constants.Actions()));
       
        listGrid.addColumns(columnMetas);
    }

    private Column initTaskNameColumn() {
        Column<UserTaskSummary, String> taskNameColumn = new Column<UserTaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(UserTaskSummary object) {
                return object.getName();
            }
        };
        taskNameColumn.setSortable(true);
        taskNameColumn.setDataStoreName("t.name");
        return taskNameColumn;
    }
    
    private Column initTaskDescriptionColumn() {
        Column<UserTaskSummary, String> descriptionColumn = new Column<UserTaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(UserTaskSummary object) {
                return object.getDescription();
            }
        };
        descriptionColumn.setSortable(true);
        descriptionColumn.setDataStoreName("t.description");
        return descriptionColumn;
    }
    private Column initTaskCreateByIdColumn() {
        Column<UserTaskSummary, String> descriptionColumn = new Column<UserTaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(UserTaskSummary object) {
                return object.getCreatedBy();
            }
        };
        descriptionColumn.setSortable(true);
        descriptionColumn.setDataStoreName("t.createdby_id");
        return descriptionColumn;
    }
    
    private Column initTaskActualownerIdColumn() {
        Column<UserTaskSummary, String> descriptionColumn = new Column<UserTaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(UserTaskSummary object) {
                return object.getOwner();
            }
        };
        descriptionColumn.setSortable(true);
        descriptionColumn.setDataStoreName("t.actualowner_id");
        return descriptionColumn;
    }
    
    private Column initTaskStatusColumn() {
        Column<UserTaskSummary, String> statusColumn = new Column<UserTaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(UserTaskSummary object) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable(true);
        statusColumn.setDataStoreName("t.taskData.status");
        return statusColumn;
    }
    
    private Column initTaskCreatedOnColumn() {
        Column<UserTaskSummary, String> createdOnDateColumn = new Column<UserTaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(UserTaskSummary object) {
                if (object.getCreatedOn() != null) {
                    Date createdOn = object.getCreatedOn();
                    DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
                    return format.format(createdOn);
                }
                return "";
            }
        };
        createdOnDateColumn.setSortable(true);
        createdOnDateColumn.setDataStoreName("t.taskData.createdOn");
        return createdOnDateColumn;
    }
    
    private Column initTaskDueColumn() {
        Column<UserTaskSummary, String> dueDateColumn = new Column<UserTaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(UserTaskSummary object) {
                if (object.getExpirationTime() != null) {
                    Date expirationTime = object.getExpirationTime();
                    DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
                    return format.format(expirationTime);
                }
                return "";
            }
        };
        dueDateColumn.setSortable(true);
        dueDateColumn.setDataStoreName("t.taskData.expirationTime");
        return dueDateColumn;
    }
    private Column initActionsColumn() {

        List<HasCell<UserTaskSummary, ?>> cells = new LinkedList<HasCell<UserTaskSummary, ?>>();

        cells.add(new UserTaskHistoryActionHasCell("User_Task_History", new Delegate<UserTaskSummary>() {
            @Override
            public void execute(UserTaskSummary variable) {
//                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Edit Variable Popup");
//                placeRequestImpl.addParameter("processInstanceId", Long.toString(variable.getProcessInstanceId()));
//                placeRequestImpl.addParameter("variableId", variable.getVariableId());
//                placeRequestImpl.addParameter("value", variable.getNewValue());
//
//                placeManager.goTo(placeRequestImpl);
            }
        }));


        CompositeCell<UserTaskSummary> cell = new CompositeCell<UserTaskSummary>(cells);
        Column<UserTaskSummary, UserTaskSummary> actionsColumn = new Column<UserTaskSummary, UserTaskSummary>(cell) {
            @Override
            public UserTaskSummary getValue(UserTaskSummary object) {
                return object;
            }
        };
        return actionsColumn;
    }

    public void formClosed(@Observes BeforeClosePlaceEvent closed) {
        if ("Edit Variable Popup".equals(closed.getPlace().getIdentifier())) {
            presenter.refreshGrid();
        }
    }

    private class UserTaskHistoryActionHasCell implements HasCell<UserTaskSummary, UserTaskSummary> {

        private ActionCell<UserTaskSummary> cell;

        public UserTaskHistoryActionHasCell(String text,
                                         Delegate<UserTaskSummary> delegate) {
            cell = new ActionCell<UserTaskSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context,
                		UserTaskSummary value,
                                   SafeHtmlBuilder sb) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.editGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.User_Task_History() + "'>");
                        mysb.append(imageProto.getSafeHtml());
                        mysb.appendHtmlConstant("</span>");
                        sb.append(mysb.toSafeHtml());
                }
            };
        }

        @Override
        public Cell<UserTaskSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<UserTaskSummary, UserTaskSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public UserTaskSummary getValue(UserTaskSummary object) {
            return object;
        }

    }

 }
