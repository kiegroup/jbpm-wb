package org.jbpm.console.ng.bd.client.editors.kbases.list;

///*
// * Copyright 2012 JBoss Inc
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.jbpm.console.ng.client.editors.knowledge.kbases.list;
//
//import org.jbpm.console.ng.client.editors.tasks.inbox.personal.list.*;
//import java.util.Comparator;
//import java.util.Set;
//
//import javax.enterprise.context.Dependent;
//import javax.enterprise.event.Event;
//import javax.enterprise.event.Observes;
//import javax.inject.Inject;
//
//import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskChangedEvent;
//import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskSelectionEvent;
//import org.jbpm.console.ng.client.editors.tasks.inbox.events.UserTaskEvent;
//import org.jbpm.console.ng.client.model.TaskSummary;
//import org.uberfire.client.mvp.PlaceManager;
//import org.uberfire.client.workbench.widgets.events.NotificationEvent;
//
//
//import com.google.gwt.cell.client.ButtonCell;
//import com.google.gwt.cell.client.CheckboxCell;
//import com.google.gwt.cell.client.EditTextCell;
//import com.google.gwt.cell.client.FieldUpdater;
//import com.google.gwt.cell.client.NumberCell;
//import com.google.gwt.cell.client.TextCell;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.safehtml.shared.SafeHtmlUtils;
//import com.google.gwt.user.cellview.client.Column;
//import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
//import com.google.gwt.user.cellview.client.DataGrid;
//import com.google.gwt.user.cellview.client.SafeHtmlHeader;
//import com.google.gwt.user.cellview.client.SimplePager;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.CheckBox;
//import com.google.gwt.user.client.ui.Composite;
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.TextBox;
//import com.google.gwt.view.client.DefaultSelectionEventManager;
//import com.google.gwt.view.client.MultiSelectionModel;
//import com.google.gwt.view.client.SelectionChangeEvent;
//import com.google.gwt.view.client.SelectionModel;
//import org.jboss.errai.ui.shared.api.annotations.DataField;
//import org.jboss.errai.ui.shared.api.annotations.EventHandler;
//import org.jboss.errai.ui.shared.api.annotations.Templated;
//import org.jbpm.console.ng.client.util.ResizableHeader;
//import org.uberfire.security.Identity;
//import org.uberfire.shared.mvp.impl.PlaceRequestImpl;
//
//import org.jbpm.console.ng.client.i18n.Constants;
//
//@Dependent
//@Templated(value = "KBasesListViewImpl.html")
//public class KBasesListViewImpl extends Composite
//        implements
//        KBasesListPresenter.InboxView {
//
//    @Inject
//    private Identity identity;
//    @Inject
//    private PlaceManager placeManager;
//    private KBasesListPresenter presenter;
//    @Inject
//    @DataField
//    public Button filterKbaseButton;
//    @Inject
//    @DataField
//    public Button addKbaseButton;
//    @Inject
//    @DataField
//    public Button removeKbaseButton;
//    @Inject
//    @DataField
//    public TextBox filterText;
//    @Inject
//    @DataField
//    public DataGrid<KBaseSummary> kbasesListGrid;
//    @Inject
//    @DataField
//    public SimplePager pager;
//    private Set<KBaseSummary> selectedKBases;
//    @Inject
//    private Event<NotificationEvent> notification;
//    @Inject
//    private Event<KBaseSelectionEvent> taskSelection;
//    private ListHandler<KBaseSummary> sortHandler;
//
//    private Constants constants = GWT.create(Constants.class);
//
//    @Override
//    public void init(KBasesListPresenter presenter) {
//        this.presenter = presenter;
//
//
//        kbasesListGrid.setWidth("100%");
//        kbasesListGrid.setHeight("200px");
//
//        // Set the message to display when the table is empty.
//        kbasesListGrid.setEmptyTableWidget(new Label(constants.No_KBases_Available()));
//
//        // Attach a column sort handler to the ListDataProvider to sort the list.
//        sortHandler =
//                new ListHandler<KBaseSummary>(presenter.getDataProvider().getList());
//        kbasesListGrid.addColumnSortHandler(sortHandler);
//
//        // Create a Pager to control the table.
//
//        pager.setDisplay(kbasesListGrid);
//        pager.setPageSize(6);
//
//        // Add a selection model so we can select cells.
//        final MultiSelectionModel<TaskSummary> selectionModel =
//                new MultiSelectionModel<KBaseSummary>();
//        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//            public void onSelectionChange(SelectionChangeEvent event) {
//                selectedKBases = selectionModel.getSelectedSet();
//                for (TaskSummary ts : selectedKBases) {
//                    taskSelection.fire(new KBaseSelectionEvent(ts.getId()));
//                }
//            }
//        });
//
//        kbasesListGrid.setSelectionModel(selectionModel,
//                DefaultSelectionEventManager
//                .<KBaseSummary>createCheckboxManager());
//
//        initTableColumns(selectionModel);
//
//        filterText.setText(identity.getName());
//
//        presenter.addDataDisplay(kbasesListGrid);
//
//    }
//
//    public void recieveStatusChanged(@Observes UserTaskEvent event) {
//        
//
//        presenter.refreshTasks(event.getUserId(),
//                isChecked);
//        filterText.setText(event.getUserId());
//    }
//
//    @EventHandler("refreshTasksButton")
//    public void refreshTasksButton(ClickEvent e) {
//        
//        presenter.refreshTasks(filterText.getText(),
//                isChecked);
//    }
//
//    @EventHandler("startTaskButton")
//    public void startTaskButton(ClickEvent e) {
//        if (selectedKBases.isEmpty()) {
//            displayNotification(constants.Please_Select_at_least_one_Task_to_Execute_a_Quick_Action());
//            return;
//        }
//        presenter.startTasks(selectedKBases,
//                filterText.getText());
//    }
//
//    @EventHandler("completeTaskButton")
//    public void completeTaskButton(ClickEvent e) {
//        if (selectedKBases.isEmpty()) {
//            displayNotification(constants.Please_Select_at_least_one_Task_to_Execute_a_Quick_Action());
//            return;
//        }
//        presenter.completeTasks(selectedKBases,
//                filterText.getText());
//
//    }
//
//    @EventHandler("releaseTaskButton")
//    public void releaseTaskButton(ClickEvent e) {
//        if (selectedKBases.isEmpty()) {
//            displayNotification(constants.Please_Select_at_least_one_Task_to_Execute_a_Quick_Action());
//            return;
//        }
//        presenter.releaseTasks(selectedKBases,
//                filterText.getText());
//    }
//
//    private void initTableColumns(final SelectionModel<TaskSummary> selectionModel) {
//        // Checkbox column. This table will uses a checkbox column for selection.
//        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
//        // mouse selection.
//
//        Column<TaskSummary, Boolean> checkColumn =
//                new Column<TaskSummary, Boolean>(new CheckboxCell(true,
//                false)) {
//                    @Override
//                    public Boolean getValue(TaskSummary object) {
//                        // Get the value from the selection model.
//                        return selectionModel.isSelected(object);
//                    }
//                };
//        kbasesListGrid.addColumn(checkColumn,
//                SafeHtmlUtils.fromSafeConstant("<br/>"));
//
//
//        // First name.
//        Column<TaskSummary, Number> taskIdColumn =
//                new Column<TaskSummary, Number>(new NumberCell()) {
//                    @Override
//                    public Number getValue(TaskSummary object) {
//                        return object.getId();
//                    }
//                };
//        taskIdColumn.setSortable(true);
//        sortHandler.setComparator(taskIdColumn,
//                new Comparator<TaskSummary>() {
//                    public int compare(TaskSummary o1,
//                            TaskSummary o2) {
//                        return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
//                    }
//                });
//        kbasesListGrid.addColumn(taskIdColumn,
//                new ResizableHeader(constants.Id(), kbasesListGrid, taskIdColumn));
//
//
//        // Task name.
//        Column<TaskSummary, String> taskNameColumn =
//                new Column<TaskSummary, String>(new EditTextCell()) {
//                    @Override
//                    public String getValue(TaskSummary object) {
//                        return object.getName();
//                    }
//                };
//        taskNameColumn.setSortable(true);
//        sortHandler.setComparator(taskNameColumn,
//                new Comparator<TaskSummary>() {
//                    public int compare(TaskSummary o1,
//                            TaskSummary o2) {
//                        return o1.getName().compareTo(o2.getName());
//                    }
//                });
//        kbasesListGrid.addColumn(taskNameColumn,
//                new ResizableHeader(constants.Task(), kbasesListGrid, taskNameColumn));
//
//
//        // Task priority.
//        Column<TaskSummary, Number> taskPriorityColumn =
//                new Column<TaskSummary, Number>(new NumberCell()) {
//                    @Override
//                    public Number getValue(TaskSummary object) {
//                        return object.getPriority();
//                    }
//                };
//        taskPriorityColumn.setSortable(true);
//        sortHandler.setComparator(taskPriorityColumn,
//                new Comparator<TaskSummary>() {
//                    public int compare(TaskSummary o1,
//                            TaskSummary o2) {
//                        return Integer.valueOf(o1.getPriority()).compareTo(o2.getPriority());
//                    }
//                });
//        kbasesListGrid.addColumn(taskPriorityColumn,
//                new ResizableHeader(constants.Priority(), kbasesListGrid, taskPriorityColumn));
//
//
//        // Status.
//        Column<TaskSummary, String> statusColumn = new Column<TaskSummary, String>(new TextCell()) {
//            @Override
//            public String getValue(TaskSummary object) {
//                return object.getStatus();
//            }
//        };
//        statusColumn.setSortable(true);
//        sortHandler.setComparator(statusColumn,
//                new Comparator<TaskSummary>() {
//                    public int compare(TaskSummary o1,
//                            TaskSummary o2) {
//                        return o1.getStatus().compareTo(o2.getStatus());
//                    }
//                });
//
//        kbasesListGrid.addColumn(statusColumn,
//                new ResizableHeader(constants.Status(), kbasesListGrid, statusColumn));
//
//        // Due Date.
//        Column<TaskSummary, String> dueDateColumn = new Column<TaskSummary, String>(new TextCell()) {
//            @Override
//            public String getValue(TaskSummary object) {
//                if (object.getExpirationTime() != null) {
//                    return object.getExpirationTime().toString();
//                }
//                return "";
//            }
//        };
//        dueDateColumn.setSortable(true);
//
//        kbasesListGrid.addColumn(dueDateColumn,
//                new ResizableHeader(constants.Due_On(), kbasesListGrid, dueDateColumn));
//
//
//        // Task parent id.
//        Column<TaskSummary, String> taskParentIdColumn =
//                new Column<TaskSummary, String>(new TextCell()) {
//                    @Override
//                    public String getValue(TaskSummary object) {
//                        return (object.getParentId() > 0) ? String.valueOf(object.getParentId()) : constants.No_Parent();
//                    }
//                };
//        taskParentIdColumn.setSortable(true);
//        sortHandler.setComparator(taskParentIdColumn,
//                new Comparator<TaskSummary>() {
//                    public int compare(TaskSummary o1,
//                            TaskSummary o2) {
//                        return Integer.valueOf(o1.getParentId()).compareTo(o2.getParentId());
//                    }
//                });
//        kbasesListGrid.addColumn(taskParentIdColumn,
//                new ResizableHeader(constants.Parent(), kbasesListGrid, taskParentIdColumn));
//
//
//        Column<TaskSummary, String> editColumn =
//                new Column<TaskSummary, String>(new ButtonCell()) {
//                    @Override
//                    public String getValue(TaskSummary task) {
//                        return "Edit";
//                    }
//                };
//
//        editColumn.setFieldUpdater(new FieldUpdater<TaskSummary, String>() {
//            @Override
//            public void update(int index,
//                    TaskSummary task,
//                    String value) {
//
//                PlaceRequestImpl placeRequestImpl = new PlaceRequestImpl(constants.Task_Edit_Perspective());
//                placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));
//                placeManager.goTo(placeRequestImpl);
//
//            }
//        });
//
//        kbasesListGrid.addColumn(editColumn,
//                new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant(constants.Edit())));
//
//
//        Column<TaskSummary, String> workColumn =
//                new Column<TaskSummary, String>(new ButtonCell()) {
//                    @Override
//                    public String getValue(TaskSummary task) {
//                        return "Work";
//                    }
//                };
//
//        workColumn.setFieldUpdater(new FieldUpdater<TaskSummary, String>() {
//            @Override
//            public void update(int index,
//                    TaskSummary task,
//                    String value) {
//                PlaceRequestImpl placeRequestImpl = new PlaceRequestImpl(constants.Form_Perspective());
//                placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));
//
//                placeManager.goTo(placeRequestImpl);
//
//            }
//        });
//
//        kbasesListGrid.addColumn(workColumn,
//                new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant(constants.Work())));
//
//
//    }
//
//    public void displayNotification(String text) {
//        notification.fire(new NotificationEvent(text));
//    }
//
//    public TextBox getUserText() {
//        return filterText;
//    }
//
//    public void onTaskSelected(@Observes TaskChangedEvent taskChanged) {
//        Boolean isChecked = showCompletedCheck.getValue();
//        presenter.refreshTasks(taskChanged.getUserId(),
//                isChecked);
//
//    }
//
//    public CheckBox getShowCompletedCheck() {
//        return showCompletedCheck;
//    }
//
//    public DataGrid<TaskSummary> getDataGrid() {
//        return kbasesListGrid;
//    }
//
//    public ListHandler<TaskSummary> getSortHandler() {
//        return sortHandler;
//    }
//}
