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
package org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;


import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractMultiGridView;
import org.jbpm.console.ng.df.client.list.base.DataSetEditorManager;
import org.jbpm.console.ng.gc.client.util.TaskUtils;
import org.jbpm.console.ng.ht.client.editors.quicknewtask.QuickNewTaskPopup;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.NewTaskEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.*;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;
import org.jboss.errai.security.shared.api.Group;
import static org.jbpm.console.ng.ht.util.TaskRoleDefinition.TASK_ROLE_ADMINISTRATOR;
import static org.jbpm.console.ng.ht.util.TaskRoleDefinition.TASK_ROLE_POTENTIALOWNER;

@Dependent
public class DataSetTasksListGridViewImpl extends AbstractMultiGridView<TaskSummary, DataSetTasksListGridPresenter>
        implements DataSetTasksListGridPresenter.DataSetTaskListView {

    interface Binder
            extends
            UiBinder<Widget, DataSetTasksListGridViewImpl> {

    }
    public static String DATASET_TASK_LIST_PREFIX = "DataSetTaskListGrid" ;
    public static String HUMAN_TASKS_DATASET ="jbpmHumanTasks";

    public static final String COLUMN_ACTIVATIONTIME = "activationTime";
    public static final String COLUMN_ACTUALOWNER = "actualOwner";
    public static final String COLUMN_CREATEDBY = "createdBy";
    public static final String COLUMN_CREATEDON = "createdOn";
    public static final String COLUMN_DEPLOYMENTID = "deploymentId";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DUEDATE = "dueDate";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PARENTID = "parentId";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_PROCESSID = "processId";
    public static final String COLUMN_PROCESSINSTANCEID = "processInstanceId";
    public static final String COLUMN_PROCESSSESSIONID = "processSessionId";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TASKID = "taskId";
    public static final String COLUMN_WORKITEMID = "workItemId";
    public static final String COLUMN_POTENTIALOWNERS = "potentialOwners";
    public static final String COLUMN_BUSINESSADMINISTRATORS = "businessAdministrators";

    private static Binder uiBinder = GWT.create(Binder.class);

    private final Constants constants = GWT.create(Constants.class);

    @Inject
    private Event<TaskSelectionEvent> taskSelected;

    @Inject
    private QuickNewTaskPopup quickNewTaskPopup;

    @Inject
    private NewTabFilterPopup newTabFilterPopup;

    @Inject
    private DataSetEditorManager dataSetEditorManager;


    @Override
    public void init(final DataSetTasksListGridPresenter presenter) {

        final List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(constants.Task());
        final List<String> initColumns = new ArrayList<String>();
        initColumns.add(constants.Task());
        initColumns.add(constants.Description());
        initColumns.add( constants.Actions() );
        final Button button = new Button();
        button.setText( "+" );
        button.addClickHandler(new ClickHandler() {
            public void onClick( ClickEvent event ) {
                final String key = getValidKeyForAdditionalListGrid(DATASET_TASK_LIST_PREFIX+"_");

                Command addNewGrid = new Command() {
                    @Override
                    public void execute() {

                        final ExtendedPagedTable<TaskSummary> extendedPagedTable = createGridInstance(  new GridGlobalPreferences( key, initColumns, bannedColumns ), key );

                        presenter.addDataDisplay( extendedPagedTable );
                        extendedPagedTable.setDataProvider(presenter.getDataProvider() );

                        filterPagedTable.createNewTab( extendedPagedTable, key, button,new Command() {
                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter( key );
                            }
                        } ) ;
                        applyFilterOnPresenter( key );


                    }
                };
                FilterSettings tableSettings = createTableSettingsPrototype();
                tableSettings.setKey( key );
                dataSetEditorManager.showTableSettingsEditor( filterPagedTable, Constants.INSTANCE.New_TaskList(), tableSettings, addNewGrid );

            }
        } );
        super.init(presenter, new GridGlobalPreferences(DATASET_TASK_LIST_PREFIX, initColumns, bannedColumns),button);

    }

    public void initSelectionModel (){
        final ExtendedPagedTable<TaskSummary> extendedPagedTable = getListGrid();
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

        extendedPagedTable.setEmptyTableCaption(constants.No_Tasks_Found());

        selectionModel = new NoSelectionModel<TaskSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                boolean close = false;
                if (selectedRow == -1) {
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.setRowStyles( selectedStyles );
                    extendedPagedTable.redraw();

                } else if (extendedPagedTable.getKeyboardSelectedRow() != selectedRow) {
                    extendedPagedTable.setRowStyles( selectedStyles );
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();
                } else {
                    close = true;
                }

                selectedItem = selectionModel.getLastSelectedObject();

                DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest("Task Details Multi");
                PlaceStatus status = placeManager.getStatus(defaultPlaceRequest);
                boolean logOnly = false;
                if(selectedItem.getStatus().equals("Completed") && selectedItem.isLogOnly()){
                    logOnly = true;
                }
                if (status == PlaceStatus.CLOSE) {
                    placeManager.goTo(defaultPlaceRequest);
                    taskSelected.fire(new TaskSelectionEvent(selectedItem.getTaskId(), selectedItem.getTaskName(), selectedItem.isForAdmin(), logOnly));
                } else if (status == PlaceStatus.OPEN && !close) {
                    taskSelected.fire(new TaskSelectionEvent(selectedItem.getTaskId(), selectedItem.getTaskName(), selectedItem.isForAdmin(), logOnly));
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
                            if (extendedPagedTable.getColumnIndex(actionsColumn) == event.getColumn()) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }
                        }
                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }
                });
        extendedPagedTable.setSelectionModel( selectionModel, noActionColumnManager );
        extendedPagedTable.setRowStyles( selectedStyles );

    }

    @Override
    public void initExtraButtons(ExtendedPagedTable extendedPagedTable) {
     /*   Button newTaskButton = new Button();
        newTaskButton.setTitle(constants.New_Task());
        newTaskButton.setIcon( IconType.PLUS_SIGN );
        newTaskButton.setTitle( Constants.INSTANCE.New_Task() );
        newTaskButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                quickNewTaskPopup.show();
            }
        });

        extendedPagedTable.getRightActionsToolbar().clear();
        extendedPagedTable.getRightActionsToolbar().add(newTaskButton);
        */

    }

    @Override
    public void initColumns( ExtendedPagedTable extendedPagedTable ) {
        initCellPreview(extendedPagedTable);
        Column taskIdColumn = initTaskIdColumn();
        Column taskNameColumn = initTaskNameColumn();
        Column descriptionColumn = initTaskDescriptionColumn();
        Column taskPriorityColumn = initTaskPriorityColumn();
        Column statusColumn = initTaskStatusColumn();
        Column createdOnDateColumn = initTaskCreatedOnColumn();
        Column dueDateColumn = initTaskDueColumn();
        Column potOwnersColumn = initTaskPotentialOwnersColumn();
        Column businessAdminColumn = initTaskBusinessAdministratorsColumn();
        actionsColumn = initActionsColumn(extendedPagedTable);

        List<ColumnMeta<TaskSummary>> columnMetas = new ArrayList<ColumnMeta<TaskSummary>>();
        columnMetas.add(new ColumnMeta<TaskSummary>(taskIdColumn, constants.Id()));
        columnMetas.add(new ColumnMeta<TaskSummary>(taskNameColumn, constants.Task()));
        columnMetas.add(new ColumnMeta<TaskSummary>(descriptionColumn, constants.Description()));
        columnMetas.add(new ColumnMeta<TaskSummary>(taskPriorityColumn, constants.Priority()));
        columnMetas.add(new ColumnMeta<TaskSummary>(statusColumn, constants.Status()));
        columnMetas.add(new ColumnMeta<TaskSummary>(createdOnDateColumn, "CreatedOn"));
        columnMetas.add(new ColumnMeta<TaskSummary>(dueDateColumn, "DueOn"));
        columnMetas.add(new ColumnMeta<TaskSummary>(potOwnersColumn, constants.Potential_Owners()));
        columnMetas.add(new ColumnMeta<TaskSummary>(businessAdminColumn, constants.Administrators()));
        columnMetas.add(new ColumnMeta<TaskSummary>(actionsColumn, constants.Actions()));
        extendedPagedTable.addColumns(columnMetas);
    }


    private void initCellPreview(final ExtendedPagedTable extendedPagedTable) {
        extendedPagedTable.addCellPreviewHandler(new CellPreviewEvent.Handler<TaskSummary>() {

            @Override
            public void onCellPreview(final CellPreviewEvent<TaskSummary> event) {

                if (BrowserEvents.MOUSEOVER.equalsIgnoreCase(event.getNativeEvent().getType())) {
                    onMouseOverGrid(extendedPagedTable,event);
                }

            }
        });

    }

    private void onMouseOverGrid(ExtendedPagedTable extendedPagedTable,final CellPreviewEvent<TaskSummary> event) {
        TaskSummary task = event.getValue();

        if (task.getDescription() != null) {
            extendedPagedTable.setTooltip(extendedPagedTable.getKeyboardSelectedRow(), event.getColumn(), task.getDescription());
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
        taskIdColumn.setDataStoreName(COLUMN_TASKID);
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
        taskNameColumn.setDataStoreName(COLUMN_NAME);
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
        descriptionColumn.setDataStoreName( COLUMN_DESCRIPTION );
        return descriptionColumn;
    }
    
    private Column initTaskPotentialOwnersColumn() {
        Column<TaskSummary, String> potentialOwnersColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getPotentialOwners();
            }
        };
        potentialOwnersColumn.setSortable(true);
        potentialOwnersColumn.setDataStoreName( COLUMN_POTENTIALOWNERS );
        return potentialOwnersColumn;
    }
    
    private Column initTaskBusinessAdministratorsColumn() {
        Column<TaskSummary, String> businessAdministratorsColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getBusinessAdministrators();
            }
        };
        businessAdministratorsColumn.setSortable(true);
        businessAdministratorsColumn.setDataStoreName( COLUMN_BUSINESSADMINISTRATORS );
        return businessAdministratorsColumn;
    }

    private Column initTaskPriorityColumn() {
        Column<TaskSummary, Number> taskPriorityColumn = new Column<TaskSummary, Number>(new NumberCell()) {
            @Override
            public Number getValue(TaskSummary object) {
                return object.getPriority();
            }
        };
        taskPriorityColumn.setSortable(true);
        taskPriorityColumn.setDataStoreName(COLUMN_PRIORITY);
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
        statusColumn.setDataStoreName(COLUMN_STATUS);
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
        createdOnDateColumn.setDataStoreName(COLUMN_CREATEDON);
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
        dueDateColumn.setDataStoreName(COLUMN_DUEDATE);
        return dueDateColumn;
    }


    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event) {
        presenter.refreshGrid();
    }

    private Column initActionsColumn(final ExtendedPagedTable extendedPagedTable) {
        List<HasCell<TaskSummary, ?>> cells = new LinkedList<HasCell<TaskSummary, ?>>();
        cells.add(new ClaimActionHasCell(constants.Claim(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {

                presenter.claimTask(task.getTaskId(), identity.getIdentifier(), task.getDeploymentId());
                taskSelected.fire(new TaskSelectionEvent(task.getTaskId(), task.getTaskName()));
                extendedPagedTable.refresh();
            }
        }));

        cells.add(new ReleaseActionHasCell(constants.Release(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {

                presenter.releaseTask(task.getTaskId(), identity.getIdentifier());
                taskSelected.fire(new TaskSelectionEvent(task.getTaskId(), task.getTaskName()));
                extendedPagedTable.refresh();
            }
        }));

        cells.add(new CompleteActionHasCell(constants.Complete(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                placeManager.goTo("Task Details Multi");
                boolean logOnly = false;
                if(task.getStatus().equals("Completed") && task.isLogOnly()){
                    logOnly = true;
                }
                taskSelected.fire(new TaskSelectionEvent(task.getTaskId(), task.getName(), task.isForAdmin(), logOnly));
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

        selectionModel.setSelected( new TaskSummary( newTask.getNewTaskId(), newTask.getNewTaskName() ), true );
    }

    protected class CompleteActionHasCell implements HasCell<TaskSummary, TaskSummary> {

        private ActionCell<TaskSummary> cell;

        public CompleteActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
            cell = new ActionCell<TaskSummary>(text, delegate) {
                @Override
                public void render(Context context, TaskSummary value, SafeHtmlBuilder sb) {
                    if (value.getActualOwner() != null && value.getStatus().equals("InProgress")) {
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<a href='javascript:;' class='btn btn-mini' style='margin-right:5px;' title='"+constants.Complete()+"'>"+constants.Complete()+"</a>");
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
                public void render(Context context, TaskSummary value, SafeHtmlBuilder sb) {
                    if (value.getStatus().equals("Ready")) {
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<a href='javascript:;' class='btn btn-mini' style='margin-right:5px;' title='"+constants.Claim()+"'>"+constants.Claim()+"</a>");
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
                public void render(Context context, TaskSummary value, SafeHtmlBuilder sb) {
                    if (value.getActualOwner() != null && value.getActualOwner().equals(identity.getIdentifier())
                            && (value.getStatus().equals("Reserved") || value.getStatus().equals("InProgress"))) {
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<a href='javascript:;' class='btn btn-mini' style='margin-right:5px;' title='"+constants.Release()+"'>"+constants.Release()+"</a>");
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

    public void initDefaultFilters(GridGlobalPreferences preferences ,Button createTabButton){

        List<String> states;

        //Filter status Active
        states= TaskUtils.getStatusByType( TaskUtils.TaskType.ACTIVE );
        initGenericTabFilter( preferences, DATASET_TASK_LIST_PREFIX+"_0", Constants.INSTANCE.Active(), "Filter " + Constants.INSTANCE.Active(), states,TASK_ROLE_POTENTIALOWNER );


        //Filter status Personal
        states= TaskUtils.getStatusByType( TaskUtils.TaskType.PERSONAL );
        initGenericTabFilter( preferences, DATASET_TASK_LIST_PREFIX+"_1", Constants.INSTANCE.Personal(), "Filter " + Constants.INSTANCE.Personal(), states, TASK_ROLE_POTENTIALOWNER );

        //Filter status Group
        states= TaskUtils.getStatusByType( TaskUtils.TaskType.GROUP );
       initGroupTabFilter( preferences, DATASET_TASK_LIST_PREFIX+"_2", Constants.INSTANCE.Group(), "Filter " + Constants.INSTANCE.Group(), states, TASK_ROLE_POTENTIALOWNER );

        //Filter status All
        states= TaskUtils.getStatusByType( TaskUtils.TaskType.ALL );
        initGenericTabFilter( preferences, DATASET_TASK_LIST_PREFIX+"_3", Constants.INSTANCE.All(), "Filter " + Constants.INSTANCE.All(), states,TASK_ROLE_POTENTIALOWNER );

        //Filter status Admin
        states= TaskUtils.getStatusByType( TaskUtils.TaskType.ADMIN );
        initAdminTabFilter( preferences, DATASET_TASK_LIST_PREFIX+"_4", Constants.INSTANCE.Task_Admin(), "Filter " + Constants.INSTANCE.Task_Admin(), states, TASK_ROLE_ADMINISTRATOR );

        filterPagedTable.addAddTableButton( createTabButton );

        getMultiGridPreferencesStore().setSelectedGrid( DATASET_TASK_LIST_PREFIX + "_0" );
        filterPagedTable.setSelectedTab();
        applyFilterOnPresenter( DATASET_TASK_LIST_PREFIX + "_0" );
        

    }
    private void initGroupTabFilter(GridGlobalPreferences preferences, final String key, String tabName,
                               String tabDesc, List<String> states, String role){
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset("jbpmHumanTasks");
        List<Comparable> names = new ArrayList<Comparable>();

        for(String s : states){
            names.add(s);
        }
        builder.filter( COLUMN_STATUS, equalsTo( COLUMN_STATUS, names )  );
        
        Set<Group> groups = identity.getGroups();

        
        builder.filter( COLUMN_ACTUALOWNER, equalsTo("") );
        List<Comparable> gs = new ArrayList<Comparable>();
        for(Group g : groups){
            gs.add(g.getName());
        }
        builder.filter( COLUMN_POTENTIALOWNERS, equalsTo(COLUMN_POTENTIALOWNERS, gs) );
       

        builder.setColumn( COLUMN_ACTIVATIONTIME, "Activation Time", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_ACTUALOWNER, constants.Actual_Owner());
        builder.setColumn( COLUMN_CREATEDBY,"CreatedBy" );
        builder.setColumn( COLUMN_CREATEDON , "Created on", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_DEPLOYMENTID, "DeploymentId" );
        builder.setColumn( COLUMN_DESCRIPTION, constants.Description() );
        builder.setColumn( COLUMN_POTENTIALOWNERS, constants.Potential_Owners());
        builder.setColumn( COLUMN_BUSINESSADMINISTRATORS, constants.Administrators() );
        builder.setColumn( COLUMN_DUEDATE, "Due Date", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_NAME, constants.Task() );
        builder.setColumn( COLUMN_PARENTID,  "ParentId");
        builder.setColumn( COLUMN_PRIORITY, "Priority" );
        builder.setColumn( COLUMN_PROCESSID, "ProcessId" );
        builder.setColumn( COLUMN_PROCESSINSTANCEID, "ProcessInstanceId" );
        builder.setColumn( COLUMN_PROCESSSESSIONID, "ProcessSesionId" );
        builder.setColumn( COLUMN_STATUS, constants.Status() );
        builder.setColumn( COLUMN_TASKID, constants.Id() );
        builder.setColumn( COLUMN_WORKITEMID, "WorkItemId" );

        builder.filterOn( true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault( COLUMN_CREATEDON, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>(  );

        tabSettingsValues.put( FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr(tableSettings  ));
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName() );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription() );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<TaskSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns()), key );
        currentListGrid = extendedPagedTable;
        presenter.addDataDisplay( extendedPagedTable );
        extendedPagedTable.setDataProvider(presenter.getDataProvider() );

        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
            @Override
            public void execute() {
                currentListGrid = extendedPagedTable;
                applyFilterOnPresenter( key );
            }
        } ) ;
    }
    
    private void initAdminTabFilter(GridGlobalPreferences preferences, final String key, String tabName,
                               String tabDesc, List<String> states, String role){
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset("jbpmHumanTasks");
        List<Comparable> names = new ArrayList<Comparable>();

        for(String s : states){
            names.add(s);
        }
        builder.filter( COLUMN_STATUS, equalsTo( COLUMN_STATUS, names )  );
        
        Set<Group> groups = identity.getGroups();
        

        List<Comparable> gs = new ArrayList<Comparable>();
        for(Group g : groups){
            gs.add(g.getName());
        }
        gs.add(identity.getIdentifier());
        
        builder.filter( equalsTo(COLUMN_BUSINESSADMINISTRATORS, gs) );

        builder.setColumn( COLUMN_ACTIVATIONTIME, "Activation Time", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_ACTUALOWNER, constants.Actual_Owner());
        builder.setColumn( COLUMN_CREATEDBY,"CreatedBy" );
        builder.setColumn( COLUMN_CREATEDON , "Created on", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_DEPLOYMENTID, "DeploymentId" );
        builder.setColumn( COLUMN_DESCRIPTION, constants.Description() );
        builder.setColumn( COLUMN_POTENTIALOWNERS, constants.Potential_Owners());
        builder.setColumn( COLUMN_BUSINESSADMINISTRATORS, constants.Administrators() );
        builder.setColumn( COLUMN_DUEDATE, "Due Date", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_NAME, constants.Task() );
        builder.setColumn( COLUMN_PARENTID,  "ParentId");
        builder.setColumn( COLUMN_PRIORITY, "Priority" );
        builder.setColumn( COLUMN_PROCESSID, "ProcessId" );
        builder.setColumn( COLUMN_PROCESSINSTANCEID, "ProcessInstanceId" );
        builder.setColumn( COLUMN_PROCESSSESSIONID, "ProcessSesionId" );
        builder.setColumn( COLUMN_STATUS, constants.Status() );
        builder.setColumn( COLUMN_TASKID, constants.Id() );
        builder.setColumn( COLUMN_WORKITEMID, "WorkItemId" );

        builder.filterOn( true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault( COLUMN_CREATEDON, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>(  );

        tabSettingsValues.put( FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr(tableSettings  ));
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName() );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription() );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<TaskSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns()), key );
        currentListGrid = extendedPagedTable;
        presenter.addDataDisplay( extendedPagedTable );
        extendedPagedTable.setDataProvider(presenter.getDataProvider() );

        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
            @Override
            public void execute() {
                currentListGrid = extendedPagedTable;
                applyFilterOnPresenter( key );
            }
        } ) ;
    }
    
    private void initGenericTabFilter(GridGlobalPreferences preferences, final String key, String tabName,
                               String tabDesc, List<String> states, String role){

        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_DATASET);
        List<Comparable> names = new ArrayList<Comparable>();

        for(String s :states){
            names.add(s);
        }
        builder.filter( equalsTo( COLUMN_STATUS, names )  );


        builder.setColumn( COLUMN_ACTIVATIONTIME, "Activation Time", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_ACTUALOWNER, constants.Actual_Owner());
        builder.setColumn( COLUMN_CREATEDBY,"CreatedBy" );
        builder.setColumn( COLUMN_CREATEDON , "Created on", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_DEPLOYMENTID, "DeploymentId" );
        builder.setColumn( COLUMN_DESCRIPTION, constants.Description() );
        builder.setColumn( COLUMN_POTENTIALOWNERS, constants.Potential_Owners());
        builder.setColumn( COLUMN_BUSINESSADMINISTRATORS, constants.Administrators() );
        builder.setColumn( COLUMN_DUEDATE, "Due Date", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_NAME, constants.Task() );
        builder.setColumn( COLUMN_PARENTID,  "ParentId");
        builder.setColumn( COLUMN_PRIORITY, "Priority" );
        builder.setColumn( COLUMN_PROCESSID, "ProcessId" );
        builder.setColumn( COLUMN_PROCESSINSTANCEID, "ProcessInstanceId" );
        builder.setColumn( COLUMN_PROCESSSESSIONID, "ProcessSesionId" );
        builder.setColumn( COLUMN_STATUS, constants.Status() );
        builder.setColumn( COLUMN_TASKID, constants.Id() );
        builder.setColumn( COLUMN_WORKITEMID, "WorkItemId" );

        builder.filterOn( true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault( COLUMN_CREATEDON, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>(  );

        tabSettingsValues.put( FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr(tableSettings  ));
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName() );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription() );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<TaskSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns()), key );
        currentListGrid = extendedPagedTable;
        presenter.addDataDisplay( extendedPagedTable );
        extendedPagedTable.setDataProvider(presenter.getDataProvider() );

        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
            @Override
            public void execute() {
                currentListGrid = extendedPagedTable;
                applyFilterOnPresenter( key );
            }
        } ) ;

    }

    public void applyFilterOnPresenter(HashMap<String, Object> params){

        String tableSettingsJSON = (String) params.get( FILTER_TABLE_SETTINGS );
        FilterSettings tableSettings = dataSetEditorManager.getStrToTableSettings( tableSettingsJSON );
        presenter.filterGrid( tableSettings);

    }

    public void applyFilterOnPresenter(String key) {
        initSelectionModel();
        applyFilterOnPresenter( filterPagedTable.getMultiGridPreferencesStore().getGridSettings( key ) );
    }

 /*   public void applyFilterOnPresenter(String key) {
        initSelectionModel();
        presenter.filterGrid( getTableSettingsByKey( key ));
    }
*/
    /*-------------------------------------------------*/
    /*---              DashBuilder                   --*/
    /*-------------------------------------------------*/


    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_DATASET);

      /*  builder.setColumn( COLUMN_TASKID, constants.Id() );
        builder.setColumn( COLUMN_NAME, constants.Task() );
        builder.setColumn( COLUMN_DESCRIPTION, constants.Description() );
        builder.setColumn( COLUMN_PRIORITY, "Priority" );
        builder.setColumn( COLUMN_STATUS, constants.Status() );
        builder.setColumn( COLUMN_CREATEDON , "Created on", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_DUEDATE, "Due Date", "MMM dd E, yyyy" );
       */

        builder.setColumn( COLUMN_ACTIVATIONTIME, "Activation Time", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_ACTUALOWNER, constants.Actual_Owner());
        builder.setColumn( COLUMN_CREATEDBY,"CreatedBy" );
        builder.setColumn( COLUMN_CREATEDON , "Created on", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_DEPLOYMENTID, "DeploymentId" );
        builder.setColumn( COLUMN_DESCRIPTION, constants.Description() );
        builder.setColumn( COLUMN_POTENTIALOWNERS, constants.Potential_Owners());
        builder.setColumn( COLUMN_BUSINESSADMINISTRATORS, constants.Administrators() );
        builder.setColumn( COLUMN_DUEDATE, "Due Date", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_NAME, constants.Task() );
        builder.setColumn( COLUMN_PARENTID,  "ParentId");
        builder.setColumn( COLUMN_PRIORITY, "Priority" );
        builder.setColumn( COLUMN_PROCESSID, "ProcessId" );
        builder.setColumn( COLUMN_PROCESSINSTANCEID, "ProcessInstanceId" );
        builder.setColumn( COLUMN_PROCESSSESSIONID, "ProcessSesionId" );
        builder.setColumn( COLUMN_STATUS, constants.Status() );
        builder.setColumn( COLUMN_TASKID, constants.Id() );
        builder.setColumn( COLUMN_WORKITEMID, "WorkItemId" );

        builder.filterOn( true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault( COLUMN_CREATEDON, DESCENDING );
        builder.tableWidth(1000);


        return  builder.buildSettings();

    }
    public int getRefreshValue(){
        return getMultiGridPreferencesStore().getRefreshInterval();
    }

    public void saveRefreshValue(int newValue){
        filterPagedTable.saveNewRefreshInterval( newValue );
    }

    public void restoreTabs() {
        ArrayList<String> existingGrids = getMultiGridPreferencesStore().getGridsId();
        ArrayList<String> allTabs= new ArrayList<String>( existingGrids.size() );


        if ( existingGrids != null && existingGrids.size() > 0 ) {

            for ( int i = 0; i < existingGrids.size(); i++ ) {
                allTabs.add( existingGrids.get( i ) );
            }

            for ( int i = 0; i < allTabs.size(); i++ ) {
                filterPagedTable.removeTab( allTabs.get(i) );
            }

        }
        filterPagedTable.tabPanel.remove( 0 );
        initDefaultFilters(currentGlobalPreferences,createTabButton );
    }


}
