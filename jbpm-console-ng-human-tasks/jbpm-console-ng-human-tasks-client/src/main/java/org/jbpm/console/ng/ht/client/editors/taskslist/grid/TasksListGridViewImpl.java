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
package org.jbpm.console.ng.ht.client.editors.taskslist.grid;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.client.resources.HumanTasksImages;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.NewTaskEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Dependent
public class TasksListGridViewImpl extends AbstractListView<TaskSummary, TasksListGridPresenter>
        implements TasksListGridPresenter.TaskListView {

    interface Binder
            extends
            UiBinder<Widget, TasksListGridViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    private final Constants constants = GWT.create(Constants.class);
    private final HumanTasksImages images = GWT.create(HumanTasksImages.class);

    @Inject
    private Event<TaskSelectionEvent> taskSelected;

    private Button activeFilterButton;

    private Button personalFilterButton;

    private Button groupFilterButton;

    private Button allFilterButton;

    private Button adminFilterButton;

    @Override
    public void init(final TasksListGridPresenter presenter) {
        List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(constants.Task());
        List<String> initColumns = new ArrayList<String>();
        initColumns.add(constants.Task());
        initColumns.add(constants.Description());
        super.init(presenter, new GridGlobalPreferences("TaskListGrid", initColumns, bannedColumns));

        selectedStyles = new RowStyles<TaskSummary>() {

            @Override
            public String getStyleNames(TaskSummary row, int rowIndex) {
                if (rowIndex == selectedRow) {
                    return "selected";
                } else {
                    if (row.getStatus().equals("InProgress") || row.getStatus().equals("Ready")) {
                        if (row.getPriority() == 5) {
                            return "five";
                        } else if (row.getPriority() == 4) {
                            return "four";
                        } else if (row.getPriority() == 3) {
                            return "three";
                        } else if (row.getPriority() == 2) {
                            return "two";
                        } else if (row.getPriority() == 1) {
                            return "one";
                        }
                    } else if (row.getStatus().equals("Completed")) {
                        return "completed";
                    }

                }
                return null;
            }
        };

        listGrid.setEmptyTableCaption(constants.No_Tasks_Found());
        selectionModel = new NoSelectionModel<TaskSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                boolean close = false;
                if (selectedRow == -1) {
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.setRowStyles(selectedStyles);
                    listGrid.redraw();

                } else if (listGrid.getKeyboardSelectedRow() != selectedRow) {
                    listGrid.setRowStyles(selectedStyles);
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                } else {
                    close = true;
                }

                selectedItem = selectionModel.getLastSelectedObject();

                DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest("Task Details Multi");
                PlaceStatus status = placeManager.getStatus(defaultPlaceRequest);
                if (status == PlaceStatus.CLOSE) {
                    placeManager.goTo(defaultPlaceRequest);
                    taskSelected.fire(new TaskSelectionEvent(selectedItem.getTaskId(), selectedItem.getTaskName(), selectedItem.isForAdmin()));
                } else if (status == PlaceStatus.OPEN && !close) {
                    taskSelected.fire(new TaskSelectionEvent(selectedItem.getTaskId(), selectedItem.getTaskName(), selectedItem.isForAdmin()));
                } else if (status == PlaceStatus.OPEN && close) {
                    placeManager.closePlace("Task Details Multi");
                }

            }
        });

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager(new DefaultSelectionEventManager.EventTranslator<TaskSummary>() {

                    @Override
                    public boolean clearCurrentSelection(CellPreviewEvent<TaskSummary> event) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<TaskSummary> event) {
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
        initExtraButtons();
        initFiltersBar();
    }

    private void initFiltersBar() {
        HorizontalPanel filtersBar = new HorizontalPanel();
        Label filterLabel = new Label();
        filterLabel.setStyleName("");
        filterLabel.setText(constants.Filters() + ": ");

        activeFilterButton = new Button();
        activeFilterButton.setIcon(IconType.FILTER);
        activeFilterButton.setSize(ButtonSize.SMALL);
        activeFilterButton.setText(constants.Active());
        activeFilterButton.setEnabled(false);
        activeFilterButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                activeFilterButton.setEnabled(false);
                personalFilterButton.setEnabled(true);
                groupFilterButton.setEnabled(true);
                allFilterButton.setEnabled(true);
                adminFilterButton.setEnabled(true);
                presenter.refreshActiveTasks();
                closePlace("Task Details Multi");
            }
        });

        personalFilterButton = new Button();
        personalFilterButton.setIcon(IconType.FILTER);
        personalFilterButton.setSize(ButtonSize.SMALL);
        personalFilterButton.setText(constants.Personal());
        personalFilterButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                activeFilterButton.setEnabled(true);
                personalFilterButton.setEnabled(false);
                groupFilterButton.setEnabled(true);
                adminFilterButton.setEnabled(true);
                allFilterButton.setEnabled(true);
                presenter.refreshPersonalTasks();
                closePlace("Task Details Multi");
            }
        });

        groupFilterButton = new Button();
        groupFilterButton.setIcon(IconType.FILTER);
        groupFilterButton.setSize(ButtonSize.SMALL);
        groupFilterButton.setText(constants.Group());
        groupFilterButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                activeFilterButton.setEnabled(true);
                personalFilterButton.setEnabled(true);
                groupFilterButton.setEnabled(false);
                adminFilterButton.setEnabled(true);
                allFilterButton.setEnabled(true);
                presenter.refreshGroupTasks();
                closePlace("Task Details Multi");
            }
        });

        allFilterButton = new Button();
        allFilterButton.setIcon(IconType.FILTER);
        allFilterButton.setSize(ButtonSize.SMALL);
        allFilterButton.setText(constants.All());
        allFilterButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                activeFilterButton.setEnabled(true);
                personalFilterButton.setEnabled(true);
                groupFilterButton.setEnabled(true);
                adminFilterButton.setEnabled(true);
                allFilterButton.setEnabled(false);
                presenter.refreshAllTasks();
                closePlace("Task Details Multi");
            }
        });

        adminFilterButton = new Button();
        adminFilterButton.setIcon(IconType.FILTER);
        adminFilterButton.setSize(ButtonSize.SMALL);
        adminFilterButton.setText(constants.Task_Admin());
        adminFilterButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                activeFilterButton.setEnabled(true);
                personalFilterButton.setEnabled(true);
                groupFilterButton.setEnabled(true);
                allFilterButton.setEnabled(true);
                adminFilterButton.setEnabled(false);
                presenter.refreshAdminTasks();
                closePlace("Task Details Multi");
            }
        });

        filtersBar.add(filterLabel);
        ButtonGroup filtersButtonGroup = new ButtonGroup(activeFilterButton, personalFilterButton,
                groupFilterButton, allFilterButton, adminFilterButton);

        filtersBar.add(filtersButtonGroup);

        listGrid.getCenterToolbar().add(filtersBar);

    }

    private void initExtraButtons() {
        Button newTaskButton = new Button();
        newTaskButton.setTitle(constants.New_Task());
        newTaskButton.setIcon(IconType.PLUS_SIGN);
        newTaskButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                placeManager.goTo(new DefaultPlaceRequest("Quick New Task"));
            }
        });
        listGrid.getLeftToolbar().add(newTaskButton);

    }

    @Override
    public void initColumns() {
        initCellPreview();
        Column taskIdColumn = initTaskIdColumn();
        Column taskNameColumn = initTaskNameColumn();
        Column descriptionColumn = initTaskDescriptionColumn();
        Column taskPriorityColumn = initTaskPriorityColumn();
        Column statusColumn = initTaskStatusColumn();
        Column createdOnDateColumn = initTaskCreatedOnColumn();
        Column dueDateColumn = initTaskDueColumn();
        actionsColumn = initActionsColumn();

        List<ColumnMeta<TaskSummary>> columnMetas = new ArrayList<ColumnMeta<TaskSummary>>();
        columnMetas.add(new ColumnMeta<TaskSummary>(taskIdColumn, constants.Id()));
        columnMetas.add(new ColumnMeta<TaskSummary>(taskNameColumn, constants.Task()));
        columnMetas.add(new ColumnMeta<TaskSummary>(descriptionColumn, constants.Description()));
        columnMetas.add(new ColumnMeta<TaskSummary>(taskPriorityColumn, constants.Priority()));
        columnMetas.add(new ColumnMeta<TaskSummary>(statusColumn, constants.Status()));
        columnMetas.add(new ColumnMeta<TaskSummary>(createdOnDateColumn, "CreatedOn"));
        columnMetas.add(new ColumnMeta<TaskSummary>(dueDateColumn, "DueOn"));
        columnMetas.add(new ColumnMeta<TaskSummary>(actionsColumn, constants.Actions()));
        listGrid.addColumns(columnMetas);
    }

    private void initCellPreview() {
        listGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<TaskSummary>() {

            @Override
            public void onCellPreview(final CellPreviewEvent<TaskSummary> event) {

                if (BrowserEvents.MOUSEOVER.equalsIgnoreCase(event.getNativeEvent().getType())) {
                    onMouseOverGrid(event);
                }

            }
        });

    }

    private void onMouseOverGrid(final CellPreviewEvent<TaskSummary> event) {
        TaskSummary task = event.getValue();

        if (task.getDescription() != null) {
            listGrid.setTooltip(listGrid.getKeyboardSelectedRow(), event.getColumn(), task.getDescription());
        }
    }

    private Column initTaskIdColumn() {
        Column<TaskSummary, Number> taskIdColumn = new Column<TaskSummary, Number>(new NumberCell()) {
            @Override
            public Number getValue(TaskSummary object) {
                return object.getTaskId();
            }
        };
        taskIdColumn.setSortable(true);
        
        return taskIdColumn;
    }

    private Column initTaskNameColumn() {
        Column<TaskSummary, String> taskNameColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getTaskName();
            }
        };
        taskNameColumn.setSortable(true);
        
        return taskNameColumn;
    }

    private Column initTaskDescriptionColumn() {
        Column<TaskSummary, String> descriptionColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getDescription();
            }
        };
        descriptionColumn.setSortable(true);
        return descriptionColumn;
    }

    private Column initTaskPriorityColumn() {
        Column<TaskSummary, Number> taskPriorityColumn = new Column<TaskSummary, Number>(new NumberCell()) {
            @Override
            public Number getValue(TaskSummary object) {
                return object.getPriority();
            }
        };
        taskPriorityColumn.setSortable(true);
        return taskPriorityColumn;
    }

    private Column initTaskStatusColumn() {
        Column<TaskSummary, String> statusColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable(true);
        return statusColumn;
    }

    private Column initTaskCreatedOnColumn() {
        Column<TaskSummary, String> createdOnDateColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                if (object.getCreatedOn() != null) {
                    Date createdOn = object.getCreatedOn();
                    DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
                    return format.format(createdOn);
                }
                return "";
            }
        };
        createdOnDateColumn.setSortable(true);
        return createdOnDateColumn;
    }

    private Column initTaskDueColumn() {
        Column<TaskSummary, String> dueDateColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                if (object.getExpirationTime() != null) {
                    Date expirationTime = object.getExpirationTime();
                    DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
                    return format.format(expirationTime);
                }
                return "";
            }
        };
        dueDateColumn.setSortable(true);
       
        return dueDateColumn;
    }

    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event) {
        presenter.refreshGrid();
    }

    private Column initActionsColumn() {
        List<HasCell<TaskSummary, ?>> cells = new LinkedList<HasCell<TaskSummary, ?>>();
        cells.add(new ClaimActionHasCell(constants.Claim(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {

                presenter.claimTask(task.getTaskId(), identity.getIdentifier());
                taskSelected.fire(new TaskSelectionEvent(task.getTaskId(), task.getTaskName()));
                listGrid.refresh();
            }
        }));

        cells.add(new ReleaseActionHasCell(constants.Release(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {

                presenter.releaseTask(task.getTaskId(), identity.getIdentifier());
                taskSelected.fire(new TaskSelectionEvent(task.getTaskId(), task.getTaskName()));
                listGrid.refresh();
            }
        }));

        cells.add(new CompleteActionHasCell(constants.Complete(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                placeManager.goTo("Task Details Multi");
                taskSelected.fire(new TaskSelectionEvent(task.getTaskId(), task.getName(), task.isForAdmin()));
            }
        }));

        CompositeCell<TaskSummary> cell = new CompositeCell<TaskSummary>(cells);
        Column<TaskSummary, TaskSummary> actionsColumn = new Column<TaskSummary, TaskSummary>(cell) {
            @Override
            public TaskSummary getValue(TaskSummary object) {
                return object;
            }
        };
        return actionsColumn;

    }

    public void refreshNewTask(@Observes NewTaskEvent newTask) {
        presenter.refreshGrid();
        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));
        if (status == PlaceStatus.OPEN) {
            taskSelected.fire(new TaskSelectionEvent(newTask.getNewTaskId(), newTask.getNewTaskName()));
        } else {
            placeManager.goTo("Task Details Multi");
            taskSelected.fire(new TaskSelectionEvent(newTask.getNewTaskId(), newTask.getNewTaskName()));
        }

        selectionModel.setSelected(new TaskSummary(newTask.getNewTaskId(), newTask.getNewTaskName()), true);
    }

    protected class CompleteActionHasCell implements HasCell<TaskSummary, TaskSummary> {

        private ActionCell<TaskSummary> cell;

        public CompleteActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
            cell = new ActionCell<TaskSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
                    if (value.getActualOwner() != null && value.getStatus().equals("InProgress")) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.completeGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Complete() + "' style='margin-right:5px;'>");
                        mysb.append(imageProto.getSafeHtml());
                        mysb.appendHtmlConstant("</span>");
                        sb.append(mysb.toSafeHtml());
                    }
                }
            };
        }

        @Override
        public Cell<TaskSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public TaskSummary getValue(TaskSummary object) {
            return object;
        }
    }

    protected class ClaimActionHasCell implements HasCell<TaskSummary, TaskSummary> {

        private ActionCell<TaskSummary> cell;

        public ClaimActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
            cell = new ActionCell<TaskSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
                    if (value.getStatus().equals("Ready")) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.releaseGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Claim() + "' style='margin-right:5px;'>");
                        mysb.append(imageProto.getSafeHtml());
                        mysb.appendHtmlConstant("</span>");
                        sb.append(mysb.toSafeHtml());
                    }
                }
            };
        }

        @Override
        public Cell<TaskSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public TaskSummary getValue(TaskSummary object) {
            return object;
        }
    }

    protected class ReleaseActionHasCell implements HasCell<TaskSummary, TaskSummary> {

        private ActionCell<TaskSummary> cell;

        public ReleaseActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
            cell = new ActionCell<TaskSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
                    if (value.getActualOwner() != null && value.getActualOwner().equals(identity.getIdentifier())
                            && (value.getStatus().equals("Reserved") || value.getStatus().equals("InProgress"))) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.claimGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Release() + "' style='margin-right:5px;'>");
                        mysb.append(imageProto.getSafeHtml());
                        mysb.appendHtmlConstant("</span>");
                        sb.append(mysb.toSafeHtml());
                    }
                }
            };
        }

        @Override
        public Cell<TaskSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public TaskSummary getValue(TaskSummary object) {
            return object;
        }
    }

    private PlaceStatus getPlaceStatus(String place) {
        DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(place);
        PlaceStatus status = placeManager.getStatus(defaultPlaceRequest);
        return status;
    }

    private void closePlace(String place) {
        if (getPlaceStatus(place) == PlaceStatus.OPEN) {
            placeManager.closePlace(place);
        }
    }
}
