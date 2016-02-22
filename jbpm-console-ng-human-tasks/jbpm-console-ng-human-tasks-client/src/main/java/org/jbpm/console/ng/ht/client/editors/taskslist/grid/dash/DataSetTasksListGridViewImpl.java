/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.security.shared.api.Group;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.console.ng.df.client.list.base.DataSetEditorManager;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractMultiGridView;
import org.jbpm.console.ng.gc.client.util.ButtonActionCell;
import org.jbpm.console.ng.gc.client.util.TaskUtils;
import org.jbpm.console.ng.ht.client.editors.quicknewtask.QuickNewTaskPopup;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.NewTaskEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.jbpm.console.ng.ht.model.TaskDataSetConstants.*;
import static org.jbpm.console.ng.ht.util.TaskRoleDefinition.*;

@Dependent
public class DataSetTasksListGridViewImpl extends AbstractMultiGridView<TaskSummary, DataSetTasksListGridPresenter>
        implements DataSetTasksListGridPresenter.DataSetTaskListView {

    public static String DATASET_TASK_LIST_PREFIX = "DataSetTaskListGrid";

    private final Constants constants = Constants.INSTANCE;
    public static final String COL_ID_ACTIONS = "actions";

    @Inject
    private Event<TaskSelectionEvent> taskSelected;

    @Inject
    private QuickNewTaskPopup quickNewTaskPopup;

    @Inject
    private NewTabFilterPopup newTabFilterPopup;

    @Inject
    private DataSetEditorManager dataSetEditorManager;

    @Override
    public void init( final DataSetTasksListGridPresenter presenter ) {

        final List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add( COLUMN_NAME );
        bannedColumns.add( COL_ID_ACTIONS );
        final List<String> initColumns = new ArrayList<String>();
        initColumns.add( COLUMN_NAME );
        initColumns.add( COLUMN_DESCRIPTION );
        initColumns.add( COL_ID_ACTIONS );
        final Button button = GWT.create(Button.class);
        button.setIcon( IconType.PLUS );
        button.setSize( ButtonSize.SMALL );
        button.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                final String key = getValidKeyForAdditionalListGrid( DATASET_TASK_LIST_PREFIX + "_" );

                Command addNewGrid = new Command() {
                    @Override
                    public void execute() {

                        final ExtendedPagedTable<TaskSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, initColumns, bannedColumns ), key );

                        extendedPagedTable.setDataProvider( presenter.getDataProvider() );

                        filterPagedTable.createNewTab( extendedPagedTable, key, button, new Command() {
                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter( key );
                            }
                        } );
                        applyFilterOnPresenter( key );

                    }
                };
                FilterSettings tableSettings = createTableSettingsPrototype();
                tableSettings.setKey( key );
                dataSetEditorManager.showTableSettingsEditor( filterPagedTable, Constants.INSTANCE.New_FilteredList(), tableSettings, addNewGrid );

            }
        } );
        super.init( presenter, new GridGlobalPreferences( DATASET_TASK_LIST_PREFIX, initColumns, bannedColumns ), button );

    }

    public void initSelectionModel() {
        final ExtendedPagedTable<TaskSummary> extendedPagedTable = getListGrid();
        selectedStyles = new RowStyles<TaskSummary>() {

            @Override
            public String getStyleNames( TaskSummary row,
                                         int rowIndex ) {
                if ( rowIndex == selectedRow ) {
                    return "selected";
                } else {
                    if ( row.getStatus().equals( "InProgress" ) || row.getStatus().equals( "Ready" ) ) {
                        if ( row.getPriority() == 5 ) {
                            return "five";
                        } else if ( row.getPriority() == 4 ) {
                            return "four";
                        } else if ( row.getPriority() == 3 ) {
                            return "three";
                        } else if ( row.getPriority() == 2 ) {
                            return "two";
                        } else if ( row.getPriority() == 1 ) {
                            return "one";
                        }
                    } else if ( row.getStatus().equals( "Completed" ) ) {
                        return "completed";
                    }

                }
                return null;
            }
        };

        extendedPagedTable.setEmptyTableCaption( constants.No_Tasks_Found() );

        selectionModel = new NoSelectionModel<TaskSummary>();
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {
                boolean close = false;
                if ( selectedRow == -1 ) {
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.setRowStyles( selectedStyles );
                    extendedPagedTable.redraw();

                } else if ( extendedPagedTable.getKeyboardSelectedRow() != selectedRow ) {
                    extendedPagedTable.setRowStyles( selectedStyles );
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();
                } else {
                    close = true;
                }

                selectedItem = selectionModel.getLastSelectedObject();

                DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest( "Task Details Multi" );
                PlaceStatus status = placeManager.getStatus( defaultPlaceRequest );
                boolean logOnly = false;
                if ( selectedItem.getStatus().equals( "Completed" ) && selectedItem.isLogOnly() ) {
                    logOnly = true;
                }
                if ( status == PlaceStatus.CLOSE ) {
                    placeManager.goTo( defaultPlaceRequest );
                    taskSelected.fire( new TaskSelectionEvent( selectedItem.getTaskId(), selectedItem.getTaskName(), selectedItem.isForAdmin(), logOnly ) );
                } else if ( status == PlaceStatus.OPEN && !close ) {
                    taskSelected.fire( new TaskSelectionEvent( selectedItem.getTaskId(), selectedItem.getTaskName(), selectedItem.isForAdmin(), logOnly ) );
                } else if ( status == PlaceStatus.OPEN && close ) {
                    placeManager.closePlace( "Task Details Multi" );
                }

            }
        } );

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager( new DefaultSelectionEventManager.EventTranslator<TaskSummary>() {

                    @Override
                    public boolean clearCurrentSelection( CellPreviewEvent<TaskSummary> event ) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent( CellPreviewEvent<TaskSummary> event ) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if ( BrowserEvents.CLICK.equals( nativeEvent.getType() ) ) {
                            // Ignore if the event didn't occur in the correct column.
                            if ( extendedPagedTable.getColumnIndex( actionsColumn ) == event.getColumn() ) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }
                        }
                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }
                } );
        extendedPagedTable.setSelectionModel( selectionModel, noActionColumnManager );
        extendedPagedTable.setRowStyles( selectedStyles );

    }

    @Override
    public void initColumns( ExtendedPagedTable extendedPagedTable ) {
        initCellPreview( extendedPagedTable );
        Column taskIdColumn = initTaskIdColumn();
        Column taskNameColumn = initTaskNameColumn();
        Column descriptionColumn = initTaskDescriptionColumn();
        Column processIdColumn = initProcessIdColumn();
        Column processInstanceIdColumn = initProcessInstanceIdColumn();
        Column taskPriorityColumn = initTaskPriorityColumn();
        Column statusColumn = initTaskStatusColumn();
        Column createdOnDateColumn = initTaskCreatedOnColumn();
        Column dueDateColumn = initTaskDueColumn();

        actionsColumn = initActionsColumn( extendedPagedTable );

        List<ColumnMeta<TaskSummary>> columnMetas = new ArrayList<ColumnMeta<TaskSummary>>();
        columnMetas.add( new ColumnMeta<TaskSummary>( taskIdColumn, constants.Id() ) );
        columnMetas.add( new ColumnMeta<TaskSummary>( taskNameColumn, constants.Task() ) );
        columnMetas.add( new ColumnMeta<TaskSummary>( descriptionColumn, constants.Description() ) );
        columnMetas.add( new ColumnMeta<TaskSummary>( processIdColumn, constants.Process_Name() ) );
        columnMetas.add( new ColumnMeta<TaskSummary>( processInstanceIdColumn, constants.Process_Id() ) );
        columnMetas.add( new ColumnMeta<TaskSummary>( taskPriorityColumn, constants.Priority() ) );
        columnMetas.add( new ColumnMeta<TaskSummary>( statusColumn, constants.Status() ) );
        columnMetas.add( new ColumnMeta<TaskSummary>( createdOnDateColumn, constants.Created_On() ) );
        columnMetas.add( new ColumnMeta<TaskSummary>( dueDateColumn, constants.Due_On() ) );

        columnMetas.add( new ColumnMeta<TaskSummary>( actionsColumn, constants.Actions() ) );
        List<GridColumnPreference> columPreferenceList = extendedPagedTable.getGridPreferencesStore().getColumnPreferences();

        for ( GridColumnPreference colPref : columPreferenceList ) {
            if ( !isColumnAdded( columnMetas, colPref.getName() ) ) {
                Column genericColumn = initGenericColumn( colPref.getName() );
                genericColumn.setSortable( false );
                columnMetas.add( new ColumnMeta<TaskSummary>( genericColumn, colPref.getName(), true, true ) );
            }
        }

        extendedPagedTable.addColumns( columnMetas );
    }

    private void initCellPreview( final ExtendedPagedTable extendedPagedTable ) {
        extendedPagedTable.addCellPreviewHandler( new CellPreviewEvent.Handler<TaskSummary>() {

            @Override
            public void onCellPreview( final CellPreviewEvent<TaskSummary> event ) {

                if ( BrowserEvents.MOUSEOVER.equalsIgnoreCase( event.getNativeEvent().getType() ) ) {
                    onMouseOverGrid( extendedPagedTable, event );
                }

            }
        } );

    }

    private void onMouseOverGrid( ExtendedPagedTable extendedPagedTable,
                                  final CellPreviewEvent<TaskSummary> event ) {
        TaskSummary task = event.getValue();

        if ( task.getDescription() != null ) {
            extendedPagedTable.setTooltip( extendedPagedTable.getKeyboardSelectedRow(), event.getColumn(), task.getDescription() );
        }
    }

    private Column initTaskIdColumn() {
        Column<TaskSummary, Number> taskIdColumn = new Column<TaskSummary, Number>( new NumberCell() ) {
            @Override
            public Number getValue( TaskSummary object ) {
                return object.getTaskId();
            }
        };
        taskIdColumn.setSortable( true );
        taskIdColumn.setDataStoreName(COLUMN_TASK_ID);
        return taskIdColumn;
    }

    private Column initTaskNameColumn() {
        Column<TaskSummary, String> taskNameColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue( TaskSummary object ) {
                return object.getTaskName();
            }
        };
        taskNameColumn.setSortable( true );
        taskNameColumn.setDataStoreName( COLUMN_NAME );
        return taskNameColumn;
    }

    private Column initTaskDescriptionColumn() {
        Column<TaskSummary, String> descriptionColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue( TaskSummary object ) {
                return object.getDescription();
            }
        };
        descriptionColumn.setSortable( true );
        descriptionColumn.setDataStoreName( COLUMN_DESCRIPTION );
        return descriptionColumn;
    }

    private Column initTaskPriorityColumn() {
        Column<TaskSummary, Number> taskPriorityColumn = new Column<TaskSummary, Number>( new NumberCell() ) {
            @Override
            public Number getValue( TaskSummary object ) {
                return object.getPriority();
            }
        };
        taskPriorityColumn.setSortable( true );
        taskPriorityColumn.setDataStoreName( COLUMN_PRIORITY );
        return taskPriorityColumn;
    }

    private Column initTaskStatusColumn() {
        Column<TaskSummary, String> statusColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue( TaskSummary object ) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable( true );
        statusColumn.setDataStoreName( COLUMN_STATUS );
        return statusColumn;
    }

    private Column initTaskCreatedOnColumn() {
        Column<TaskSummary, String> createdOnDateColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue( TaskSummary object ) {
                if ( object.getCreatedOn() != null ) {
                    Date createdOn = object.getCreatedOn();
                    DateTimeFormat format = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm" );
                    return format.format( createdOn );
                }
                return "";
            }
        };
        createdOnDateColumn.setSortable( true );
        createdOnDateColumn.setDataStoreName(COLUMN_CREATED_ON);
        return createdOnDateColumn;
    }

    private Column initTaskDueColumn() {
        Column<TaskSummary, String> dueDateColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue( TaskSummary object ) {
                if ( object.getExpirationTime() != null ) {
                    Date expirationTime = object.getExpirationTime();
                    DateTimeFormat format = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm" );
                    return format.format( expirationTime );
                }
                return "";
            }
        };
        dueDateColumn.setSortable( true );
        dueDateColumn.setDataStoreName(COLUMN_DUE_DATE);
        return dueDateColumn;
    }

    private Column initProcessIdColumn() {
        Column<TaskSummary, String> taskProcessIdColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue( TaskSummary object ) {
                return object.getProcessId();
            }
        };
        taskProcessIdColumn.setSortable( true );
        taskProcessIdColumn.setDataStoreName(COLUMN_PROCESS_ID);
        return taskProcessIdColumn;
    }

    private Column initProcessInstanceIdColumn() {
        Column<TaskSummary, Number> taskProcessInstanceIdColumn = new Column<TaskSummary, Number>( new NumberCell() ) {
            @Override
            public Number getValue( TaskSummary object ) {
                return object.getProcessInstanceId();
            }
        };
        taskProcessInstanceIdColumn.setSortable( true );
        taskProcessInstanceIdColumn.setDataStoreName(COLUMN_PROCESS_INSTANCE_ID);
        return taskProcessInstanceIdColumn;
    }

    public void onTaskRefreshedEvent( @Observes TaskRefreshedEvent event ) {
        presenter.refreshGrid();
    }

    private Column initActionsColumn( final ExtendedPagedTable extendedPagedTable ) {
        List<HasCell<TaskSummary, ?>> cells = new LinkedList<HasCell<TaskSummary, ?>>();
        cells.add( new ClaimActionHasCell( constants.Claim(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute( TaskSummary task ) {

                presenter.claimTask( task.getTaskId(), identity.getIdentifier(), task.getDeploymentId() );
                taskSelected.fire( new TaskSelectionEvent( task.getTaskId(), task.getTaskName() ) );
                extendedPagedTable.refresh();
            }
        } ) );

        cells.add( new ReleaseActionHasCell( constants.Release(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute( TaskSummary task ) {

                presenter.releaseTask( task.getTaskId(), identity.getIdentifier() );
                taskSelected.fire( new TaskSelectionEvent( task.getTaskId(), task.getTaskName() ) );
                extendedPagedTable.refresh();
            }
        } ) );

        cells.add( new CompleteActionHasCell( constants.Open(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute( TaskSummary task ) {
                placeManager.goTo( "Task Details Multi" );
                boolean logOnly = false;
                if ( task.getStatus().equals( "Completed" ) && task.isLogOnly() ) {
                    logOnly = true;
                }
                selectedRow = -1;
                taskSelected.fire( new TaskSelectionEvent( task.getTaskId(), task.getName(), task.isForAdmin(), logOnly ) );
            }
        } ) );

        CompositeCell<TaskSummary> cell = new CompositeCell<TaskSummary>( cells );
        Column<TaskSummary, TaskSummary> actionsColumn = new Column<TaskSummary, TaskSummary>( cell ) {
            @Override
            public TaskSummary getValue( TaskSummary object ) {
                return object;
            }
        };
        actionsColumn.setDataStoreName( COL_ID_ACTIONS );
        return actionsColumn;

    }

    public void refreshNewTask( @Observes NewTaskEvent newTask ) {
        presenter.refreshGrid();
        PlaceStatus status = placeManager.getStatus( new DefaultPlaceRequest( "Task Details Multi" ) );
        if ( status == PlaceStatus.OPEN ) {
            taskSelected.fire( new TaskSelectionEvent( newTask.getNewTaskId(), newTask.getNewTaskName() ) );
        } else {
            placeManager.goTo( "Task Details Multi" );
            taskSelected.fire( new TaskSelectionEvent( newTask.getNewTaskId(), newTask.getNewTaskName() ) );
        }

        selectionModel.setSelected( new TaskSummary( newTask.getNewTaskId(), newTask.getNewTaskName() ), true );
    }

    protected class CompleteActionHasCell extends ButtonActionCell<TaskSummary> {

        public CompleteActionHasCell( final String text, final ActionCell.Delegate<TaskSummary> delegate ) {
            super( text, delegate );
        }

        @Override
        public void render( final Cell.Context context, final TaskSummary value, final SafeHtmlBuilder sb ) {
            if ( value.getActualOwner() != null && value.getStatus().equals( "InProgress" ) ) {
                super.render( context, value, sb );
            }
        }
    }

    protected class ClaimActionHasCell extends ButtonActionCell<TaskSummary> {

        public ClaimActionHasCell( final String text, final ActionCell.Delegate<TaskSummary> delegate ) {
            super( text, delegate );
        }

        @Override
        public void render( final Cell.Context context, final TaskSummary value, final SafeHtmlBuilder sb ) {
            if ( value.getStatus().equals( "Ready" ) ) {
                super.render( context, value, sb );
            }
        }
    }

    protected class ReleaseActionHasCell extends ButtonActionCell<TaskSummary> {

        public ReleaseActionHasCell( final String text, final ActionCell.Delegate<TaskSummary> delegate ) {
            super( text, delegate );
        }

        @Override
        public void render( final Cell.Context context, final TaskSummary value, final SafeHtmlBuilder sb ) {
            if ( value.getActualOwner() != null && value.getActualOwner().equals( identity.getIdentifier() )
                    && ( value.getStatus().equals( "Reserved" ) || value.getStatus().equals( "InProgress" ) ) ) {
                super.render( context, value, sb );
            }
        }
    }

    private PlaceStatus getPlaceStatus( String place ) {
        DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest( place );
        PlaceStatus status = placeManager.getStatus( defaultPlaceRequest );
        return status;
    }

    private void closePlace( String place ) {
        if ( getPlaceStatus( place ) == PlaceStatus.OPEN ) {
            placeManager.closePlace( place );
        }
    }

    @Override
    public void initDefaultFilters( GridGlobalPreferences preferences,
                                    Button createTabButton ) {

        List<String> states;
        presenter.setAddingDefaultFilters( true );
        //Filter status Active
        states = TaskUtils.getStatusByType( TaskUtils.TaskType.ACTIVE );
        initOwnTabFilter( preferences, DATASET_TASK_LIST_PREFIX + "_0", Constants.INSTANCE.Active(), Constants.INSTANCE.FilterActive(), states, TASK_ROLE_POTENTIALOWNER );

        //Filter status Personal
        states = TaskUtils.getStatusByType( TaskUtils.TaskType.PERSONAL );
        initPersonalTabFilter( preferences, DATASET_TASK_LIST_PREFIX + "_1", Constants.INSTANCE.Personal(), Constants.INSTANCE.FilterPersonal(), states, TASK_ROLE_POTENTIALOWNER );

        //Filter status Group
        states = TaskUtils.getStatusByType( TaskUtils.TaskType.GROUP );
        initGroupTabFilter( preferences, DATASET_TASK_LIST_PREFIX + "_2", Constants.INSTANCE.Group(), Constants.INSTANCE.FilterGroup(), states, TASK_ROLE_POTENTIALOWNER );

        //Filter status All
        states = TaskUtils.getStatusByType( TaskUtils.TaskType.ALL );
        initOwnTabFilter( preferences, DATASET_TASK_LIST_PREFIX + "_3", Constants.INSTANCE.All(), Constants.INSTANCE.FilterAll(), states, TASK_ROLE_POTENTIALOWNER );

        //Filter status Admin
        states = TaskUtils.getStatusByType( TaskUtils.TaskType.ADMIN );
        initAdminTabFilter( preferences, DATASET_TASK_LIST_PREFIX + "_4", Constants.INSTANCE.Task_Admin(), Constants.INSTANCE.FilterTaskAdmin(), states, TASK_ROLE_ADMINISTRATOR );

        filterPagedTable.addAddTableButton( createTabButton );
        presenter.setAddingDefaultFilters( false );

        getMultiGridPreferencesStore().setSelectedGrid( DATASET_TASK_LIST_PREFIX + "_0" );
        filterPagedTable.setSelectedTab();
        applyFilterOnPresenter( DATASET_TASK_LIST_PREFIX + "_0" );

    }

    private void initGroupTabFilter( GridGlobalPreferences preferences,
                                     final String key,
                                     String tabName,
                                     String tabDesc,
                                     List<String> states,
                                     String role ) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_USER_DATASET);
        List<Comparable> names = new ArrayList<Comparable>();

        for ( String s : states ) {
            names.add( s );
        }
        builder.filter( COLUMN_STATUS, equalsTo( COLUMN_STATUS, names ) );

        Set<Group> groups = identity.getGroups();

        builder.filter(COLUMN_ACTUAL_OWNER, OR(equalsTo(""), isNull()) );
        List<ColumnFilter> condList = new ArrayList<ColumnFilter>();
        for ( Group g : groups ) {
            condList.add( FilterFactory.equalsTo( COLUMN_ORGANIZATIONAL_ENTITY, g.getName() ) );

        }
        //Adding own identity to check against potential owners
        condList.add( FilterFactory.equalsTo( COLUMN_ORGANIZATIONAL_ENTITY, identity.getIdentifier() ) );

        builder.filter( COLUMN_ORGANIZATIONAL_ENTITY, OR( condList ) );
        builder.group(COLUMN_TASK_ID);

        builder.setColumn(COLUMN_ACTIVATION_TIME, constants.ActivationTime(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_ACTUAL_OWNER, constants.Actual_Owner() );
        builder.setColumn(COLUMN_CREATED_BY, constants.CreatedBy() );
        builder.setColumn(COLUMN_CREATED_ON, constants.Created_On(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_DEPLOYMENT_ID, constants.DeploymentId() );
        builder.setColumn(COLUMN_DESCRIPTION, constants.Description());
        builder.setColumn(COLUMN_DUE_DATE, constants.DueDate(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_NAME, constants.Task());
        builder.setColumn(COLUMN_PARENT_ID, constants.ParentId() );
        builder.setColumn(COLUMN_PRIORITY, constants.Priority());
        builder.setColumn(COLUMN_PROCESS_ID, constants.Process_Id() );
        builder.setColumn(COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_Id() );
        builder.setColumn(COLUMN_PROCESS_SESSION_ID, constants.ProcessSessionId() );
        builder.setColumn(COLUMN_STATUS, constants.Status());
        builder.setColumn(COLUMN_TASK_ID, constants.Id() );
        builder.setColumn(COLUMN_WORK_ITEM_ID, constants.WorkItemId() );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault(COLUMN_CREATED_ON, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

        tabSettingsValues.put( FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr( tableSettings ) );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName() );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription() );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<TaskSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns() ), key );
        currentListGrid = extendedPagedTable;
        extendedPagedTable.setDataProvider( presenter.getDataProvider() );

        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
            @Override
            public void execute() {
                currentListGrid = extendedPagedTable;
                applyFilterOnPresenter( key );
            }
        } );
    }

    private void initAdminTabFilter( GridGlobalPreferences preferences,
                                     final String key,
                                     String tabName,
                                     String tabDesc,
                                     List<String> states,
                                     String role ) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_ADMIN_DATASET);
        List<Comparable> names = new ArrayList<Comparable>();

        for ( String s : states ) {
            names.add( s );
        }
        builder.filter( COLUMN_STATUS, equalsTo( COLUMN_STATUS, names ) );

        Set<Group> groups = identity.getGroups();

        List<ColumnFilter> condList = new ArrayList<ColumnFilter>();
        for ( Group g : groups ) {
            condList.add( FilterFactory.equalsTo( COLUMN_ORGANIZATIONAL_ENTITY, g.getName() ) );

        }
        //Adding own identity to check against potential owners
        condList.add( FilterFactory.equalsTo( COLUMN_ORGANIZATIONAL_ENTITY, identity.getIdentifier() ) );

        builder.filter( COLUMN_ORGANIZATIONAL_ENTITY, OR( condList ) );

        builder.group(COLUMN_TASK_ID);

        builder.setColumn(COLUMN_ACTIVATION_TIME, constants.ActivationTime(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_ACTUAL_OWNER, constants.Actual_Owner() );
        builder.setColumn(COLUMN_CREATED_BY, constants.CreatedBy() );
        builder.setColumn(COLUMN_CREATED_ON, constants.Created_On(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_DEPLOYMENT_ID, constants.DeploymentId() );
        builder.setColumn(COLUMN_DESCRIPTION, constants.Description());
        builder.setColumn(COLUMN_DUE_DATE, constants.DueDate(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_NAME, constants.Task());
        builder.setColumn(COLUMN_PARENT_ID, constants.ParentId() );
        builder.setColumn(COLUMN_PRIORITY, constants.Priority());
        builder.setColumn(COLUMN_PROCESS_ID, constants.Process_Id() );
        builder.setColumn(COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_Id() );
        builder.setColumn(COLUMN_PROCESS_SESSION_ID, constants.ProcessSessionId() );
        builder.setColumn(COLUMN_STATUS, constants.Status());
        builder.setColumn(COLUMN_TASK_ID, constants.Id() );
        builder.setColumn(COLUMN_WORK_ITEM_ID, constants.WorkItemId() );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault(COLUMN_CREATED_ON, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

        tabSettingsValues.put( FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr( tableSettings ) );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName() );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription() );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<TaskSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns() ), key );
        currentListGrid = extendedPagedTable;
        extendedPagedTable.setDataProvider( presenter.getDataProvider() );

        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
            @Override
            public void execute() {
                currentListGrid = extendedPagedTable;
                applyFilterOnPresenter( key );
            }
        } );
    }

    private void initPersonalTabFilter( GridGlobalPreferences preferences,
                                        final String key,
                                        String tabName,
                                        String tabDesc,
                                        List<String> states,
                                        String role ) {

        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset( HUMAN_TASKS_DATASET );
        List<Comparable> names = new ArrayList<Comparable>();

        for ( String s : states ) {
            names.add( s );
        }
        builder.filter( equalsTo( COLUMN_STATUS, names ) );
        builder.filter( equalsTo(COLUMN_ACTUAL_OWNER, identity.getIdentifier() ) );

        builder.setColumn(COLUMN_ACTIVATION_TIME, "Activation Time", "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_ACTUAL_OWNER, constants.Actual_Owner() );
        builder.setColumn(COLUMN_CREATED_BY, constants.CreatedBy() );
        builder.setColumn(COLUMN_CREATED_ON, constants.Created_On(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_DEPLOYMENT_ID, constants.DeploymentId() );
        builder.setColumn(COLUMN_DESCRIPTION, constants.Description());
        builder.setColumn(COLUMN_DUE_DATE, constants.DueDate(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_NAME, constants.Task());
        builder.setColumn(COLUMN_PARENT_ID, constants.ParentId() );
        builder.setColumn(COLUMN_PRIORITY, constants.Priority());
        builder.setColumn(COLUMN_PROCESS_ID, constants.Process_Id() );
        builder.setColumn(COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_Id() );
        builder.setColumn(COLUMN_PROCESS_SESSION_ID, constants.ProcessSessionId() );
        builder.setColumn(COLUMN_STATUS, constants.Status());
        builder.setColumn(COLUMN_TASK_ID, constants.Id() );
        builder.setColumn(COLUMN_WORK_ITEM_ID, constants.WorkItemId() );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault(COLUMN_CREATED_ON, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

        tabSettingsValues.put( FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr( tableSettings ) );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName() );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription() );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<TaskSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns() ), key );
        currentListGrid = extendedPagedTable;
        extendedPagedTable.setDataProvider( presenter.getDataProvider() );

        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
            @Override
            public void execute() {
                currentListGrid = extendedPagedTable;
                applyFilterOnPresenter( key );
            }
        } );

    }

    private void initOwnTabFilter( GridGlobalPreferences preferences,
                                   final String key,
                                   String tabName,
                                   String tabDesc,
                                   List<String> states,
                                   String role ) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_USER_DATASET);
        List<Comparable> names = new ArrayList<Comparable>();

        for ( String s : states ) {
            names.add( s );
        }
        builder.filter( COLUMN_STATUS, equalsTo( COLUMN_STATUS, names ) );

        Set<Group> groups = identity.getGroups();
        List<ColumnFilter> condList = new ArrayList<ColumnFilter>();
        for ( Group g : groups ) {
            condList.add( FilterFactory.equalsTo( COLUMN_ORGANIZATIONAL_ENTITY, g.getName() ) );

        }
        //Adding own identity to check against potential owners
        condList.add( FilterFactory.equalsTo( COLUMN_ORGANIZATIONAL_ENTITY, identity.getIdentifier() ) );

        ColumnFilter myGroupFilter = FilterFactory.AND(FilterFactory.OR(condList),
                    FilterFactory.OR(FilterFactory.equalsTo(COLUMN_ACTUAL_OWNER, ""), FilterFactory.isNull(COLUMN_ACTUAL_OWNER)));

        builder.filter( OR( myGroupFilter, FilterFactory.equalsTo(COLUMN_ACTUAL_OWNER, identity.getIdentifier() ) ) );
        builder.group(COLUMN_TASK_ID);

        builder.setColumn(COLUMN_ACTIVATION_TIME, constants.ActivationTime(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_ACTUAL_OWNER, constants.Actual_Owner() );
        builder.setColumn(COLUMN_CREATED_BY, constants.CreatedBy() );
        builder.setColumn(COLUMN_CREATED_ON, constants.Created_On(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_DEPLOYMENT_ID, constants.DeploymentId() );
        builder.setColumn(COLUMN_DESCRIPTION, constants.Description());
        builder.setColumn(COLUMN_DUE_DATE, constants.DueDate(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_NAME, constants.Task());
        builder.setColumn(COLUMN_PARENT_ID, constants.ParentId() );
        builder.setColumn(COLUMN_PRIORITY, constants.Priority());
        builder.setColumn(COLUMN_PROCESS_ID, constants.Process_Id() );
        builder.setColumn(COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_Id() );
        builder.setColumn(COLUMN_PROCESS_SESSION_ID, constants.ProcessSessionId() );
        builder.setColumn(COLUMN_STATUS, constants.Status());
        builder.setColumn(COLUMN_TASK_ID, constants.Id() );
        builder.setColumn(COLUMN_WORK_ITEM_ID, constants.WorkItemId() );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault(COLUMN_CREATED_ON, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

        tabSettingsValues.put( FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr( tableSettings ) );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName() );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription() );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<TaskSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns() ), key );
        currentListGrid = extendedPagedTable;
        extendedPagedTable.setDataProvider( presenter.getDataProvider() );

        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
            @Override
            public void execute() {
                currentListGrid = extendedPagedTable;
                applyFilterOnPresenter( key );
            }
        } );
    }

    public void applyFilterOnPresenter( HashMap<String, Object> params ) {

        String tableSettingsJSON = (String) params.get( FILTER_TABLE_SETTINGS );
        FilterSettings tableSettings = dataSetEditorManager.getStrToTableSettings( tableSettingsJSON );
        presenter.filterGrid( tableSettings );

    }

    public void applyFilterOnPresenter( String key ) {
        initSelectionModel();
        applyFilterOnPresenter( filterPagedTable.getMultiGridPreferencesStore().getGridSettings( key ) );
    }
    /*-------------------------------------------------*/
    /*---              DashBuilder                   --*/
    /*-------------------------------------------------*/

    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_USER_DATASET);
        List<Comparable> names = new ArrayList<Comparable>();

        Set<Group> groups = identity.getGroups();
        List<ColumnFilter> condList = new ArrayList<ColumnFilter>();
        for ( Group g : groups ) {
            condList.add( FilterFactory.equalsTo( COLUMN_ORGANIZATIONAL_ENTITY, g.getName() ) );

        }
        //Adding own identity to check against potential owners
        condList.add( FilterFactory.equalsTo( COLUMN_ORGANIZATIONAL_ENTITY, identity.getIdentifier() ) );

        ColumnFilter myGroupFilter = FilterFactory.AND(FilterFactory.OR(condList),
                FilterFactory.OR(FilterFactory.equalsTo(COLUMN_ACTUAL_OWNER, ""), FilterFactory.isNull(COLUMN_ACTUAL_OWNER)));

        builder.filter( OR( myGroupFilter, FilterFactory.equalsTo(COLUMN_ACTUAL_OWNER, identity.getIdentifier() ) ) );
        builder.group(COLUMN_TASK_ID);

        builder.setColumn(COLUMN_ACTIVATION_TIME, constants.ActivationTime(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_ACTUAL_OWNER, constants.Actual_Owner() );
        builder.setColumn(COLUMN_CREATED_BY, constants.CreatedBy() );
        builder.setColumn(COLUMN_CREATED_ON, constants.Created_On(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_DEPLOYMENT_ID, constants.DeploymentId() );
        builder.setColumn(COLUMN_DESCRIPTION, constants.Description());
        builder.setColumn(COLUMN_DUE_DATE, constants.DueDate(), "MMM dd E, yyyy" );
        builder.setColumn(COLUMN_NAME, constants.Task());
        builder.setColumn(COLUMN_PARENT_ID, constants.ParentId() );
        builder.setColumn(COLUMN_PRIORITY, constants.Priority());
        builder.setColumn(COLUMN_PROCESS_ID, constants.Process_Id() );
        builder.setColumn(COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_Id() );
        builder.setColumn(COLUMN_PROCESS_SESSION_ID, constants.ProcessSessionId() );
        builder.setColumn(COLUMN_STATUS, constants.Status());
        builder.setColumn(COLUMN_TASK_ID, constants.Id() );
        builder.setColumn(COLUMN_WORK_ITEM_ID, constants.WorkItemId() );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault(COLUMN_CREATED_ON, DESCENDING );

        return builder.buildSettings();

    }

    public int getRefreshValue() {
        return getMultiGridPreferencesStore().getRefreshInterval();
    }

    public void saveRefreshValue( int newValue ) {
        filterPagedTable.saveNewRefreshInterval( newValue );
    }


    private boolean isColumnAdded( List<ColumnMeta<TaskSummary>> columnMetas,
            String caption ) {
        if ( caption != null ) {
            for ( ColumnMeta<TaskSummary> colMet : columnMetas ) {
                if ( caption.equals( colMet.getColumn().getDataStoreName() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addDomainSpecifColumns( ExtendedPagedTable<TaskSummary> extendedPagedTable,
            Set<String> columns ) {

        extendedPagedTable.storeColumnToPreferences();

        HashMap modifiedCaptions = new HashMap<String, String>();
        ArrayList<ColumnMeta> existingExtraColumns = new ArrayList<ColumnMeta>();
        for ( ColumnMeta<TaskSummary> cm : extendedPagedTable.getColumnMetaList() ) {
            if ( cm.isExtraColumn() ) {
                existingExtraColumns.add( cm );
            } else if ( columns.contains( cm.getCaption() ) ) {      //exist a column with the same caption
                for ( String c : columns ) {
                    if ( c.equals( cm.getCaption() ) ) {
                        modifiedCaptions.put( c, "Var_" + c );
                    }
                }
            }
        }
        for ( ColumnMeta colMet : existingExtraColumns ) {
            if ( !columns.contains( colMet.getCaption() ) ) {
                extendedPagedTable.removeColumnMeta( colMet );
            } else {
                columns.remove( colMet.getCaption() );
            }
        }

        List<ColumnMeta<TaskSummary>> columnMetas = new ArrayList<ColumnMeta<TaskSummary>>();
        String caption = "";
        for ( String c : columns ) {
            caption = c;
            if ( modifiedCaptions.get( c ) != null ) {
                caption = (String) modifiedCaptions.get( c );
            }
            Column genericColumn = initGenericColumn( c );
            genericColumn.setSortable( false );

            columnMetas.add( new ColumnMeta<TaskSummary>( genericColumn, caption, true, true ) );
        }

        extendedPagedTable.addColumns( columnMetas );

    }


    @Override
    public FilterSettings getVariablesTableSettings( String taskName ) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_VARIABLES_DATASET);
        builder.filter(FilterFactory.equalsTo(COLUMN_NAME, taskName));

        builder.filterOn(true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(COLUMN_TASK_ID, ASCENDING);

        FilterSettings varTableSettings =builder.buildSettings();
        varTableSettings.setTablePageSize(-1);

        return varTableSettings;

    }

    private Column initGenericColumn( final String key ) {

        Column<TaskSummary, String> genericColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue( TaskSummary object ) {
                return object.getDomainDataValue( key );
            }
        };
        genericColumn.setSortable(true);
        genericColumn.setDataStoreName(key);

        return genericColumn;
    }

    public void resetDefaultFilterTitleAndDescription() {

        HashMap<String, Object> tabSettingsValues = null;

        tabSettingsValues = filterPagedTable.getMultiGridPreferencesStore().getGridSettings(DATASET_TASK_LIST_PREFIX + "_0");
        if (tabSettingsValues != null) {
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM, Constants.INSTANCE.Active());
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM, Constants.INSTANCE.FilterActive());
            filterPagedTable.saveTabSettings(DATASET_TASK_LIST_PREFIX + "_0", tabSettingsValues);
        }

        tabSettingsValues = filterPagedTable.getMultiGridPreferencesStore().getGridSettings(DATASET_TASK_LIST_PREFIX + "_1");
        if (tabSettingsValues != null) {
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM, Constants.INSTANCE.Personal());
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM, Constants.INSTANCE.FilterPersonal());
            filterPagedTable.saveTabSettings(DATASET_TASK_LIST_PREFIX + "_1", tabSettingsValues);
        }

        tabSettingsValues = filterPagedTable.getMultiGridPreferencesStore().getGridSettings(DATASET_TASK_LIST_PREFIX + "_2");
        if (tabSettingsValues != null) {
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM, Constants.INSTANCE.Group());
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM, Constants.INSTANCE.FilterGroup());
            filterPagedTable.saveTabSettings(DATASET_TASK_LIST_PREFIX + "_2", tabSettingsValues);
        }

        tabSettingsValues = filterPagedTable.getMultiGridPreferencesStore().getGridSettings(DATASET_TASK_LIST_PREFIX + "_3");
        if (tabSettingsValues != null) {
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM, Constants.INSTANCE.All());
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM, Constants.INSTANCE.FilterAll());
            filterPagedTable.saveTabSettings(DATASET_TASK_LIST_PREFIX + "_3", tabSettingsValues);
        }
        tabSettingsValues = filterPagedTable.getMultiGridPreferencesStore().getGridSettings(DATASET_TASK_LIST_PREFIX + "_4");
        if (tabSettingsValues != null) {
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM, Constants.INSTANCE.Task_Admin());
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM, Constants.INSTANCE.FilterTaskAdmin());
            filterPagedTable.saveTabSettings(DATASET_TASK_LIST_PREFIX + "_4", tabSettingsValues);
        }
    }

}
