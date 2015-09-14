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
package org.jbpm.console.ng.pr.client.editors.instance.list.variables.dash;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.*;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.console.ng.df.client.list.base.DataSetEditorManager;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractMultiGridView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;

import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;

import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.*;
import org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance.QuickNewProcessInstancePopup;

@Dependent
public class DataSetProcessInstanceWithVariablesListViewImpl extends AbstractMultiGridView<ProcessInstanceSummary, DataSetProcessInstanceWithVariablesListPresenter>
        implements DataSetProcessInstanceWithVariablesListPresenter.DataSetProcessInstanceWithVariablesListView {

    interface Binder
            extends
            UiBinder<Widget, DataSetProcessInstanceWithVariablesListViewImpl> {

    }

    public static final String PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX = "DS_ProcessInstancesWithVariablesIncludedGrid";
    public static final String PROCESS_INSTANCES_DATASET_ID = "jbpmProcessInstances";

    public static final String COLUMN_PROCESSINSTANCEID = "processInstanceId";
    public static final String COLUMN_PROCESSID = "processId";
    public static final String COLUMN_START = "start_date";
    public static final String COLUMN_END = "end_date";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_PARENTPROCESSINSTANCEID = "parentProcessInstanceId";
    public static final String COLUMN_OUTCOME = "outcome";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_IDENTITY = "user_identity";
    public static final String COLUMN_PROCESSVERSION = "processVersion";
    public static final String COLUMN_PROCESSNAME = "processName";
    public static final String COLUMN_CORRELATIONKEY = "correlationKey";
    public static final String COLUMN_EXTERNALID = "externalId";
    public static final String COLUMN_PROCESSINSTANCEDESCRIPTION = "processInstanceDescription";

    public static final String PROCESS_INSTANCE_WITH_VARIABLES_DATASET = "jbpmProcessInstancesWithVariables";

    public static final String PROCESS_INSTANCE_ID = "pid";
    public static final String PROCESS_NAME = "pname";
    public static final String VARIABLE_ID = "varid";
    public static final String VARIABLE_NAME = "varname";
    public static final String VARIABLE_VALUE = "varvalue";

    private Constants constants = GWT.create(Constants.class);

    private List<ProcessInstanceSummary> selectedProcessInstances = new ArrayList<ProcessInstanceSummary>();

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceSelected;

    @Inject
    private NewTabFilterPopup newTabFilterPopup;

    @Inject
    private DataSetEditorManager dataSetEditorManager;

    private Column actionsColumn;

    private NavLink bulkAbortNavLink;
    private NavLink bulkSignalNavLink;

    @Inject
    private QuickNewProcessInstancePopup newProcessInstancePopup;

    private void controlBulkOperations() {
        if (selectedProcessInstances != null && selectedProcessInstances.size() > 0) {
            bulkAbortNavLink.setDisabled(false);
            bulkSignalNavLink.setDisabled(false);
        } else {
            bulkAbortNavLink.setDisabled(true);
            bulkSignalNavLink.setDisabled(true);
        }
    }

    @Override
    public void init(final DataSetProcessInstanceWithVariablesListPresenter presenter) {
        final List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(constants.Select());
        bannedColumns.add(constants.Id());
        bannedColumns.add(constants.Name());
        bannedColumns.add(constants.Process_Instance_Description());
        bannedColumns.add(constants.Actions());
        final List<String> initColumns = new ArrayList<String>();
        initColumns.add(constants.Select());
        initColumns.add(constants.Id());
        initColumns.add(constants.Name());
        initColumns.add(constants.Process_Instance_Description());
        initColumns.add(constants.Version());
        initColumns.add(constants.Actions());
        initColumns.add(constants.Version());

        final Button button = new Button();
        button.setText("+");

        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                final String key = getValidKeyForAdditionalListGrid(PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_");

                Command addNewGrid = new Command() {
                    @Override
                    public void execute() {

                        final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable = createGridInstance(new GridGlobalPreferences(key, initColumns, bannedColumns), key);

                        presenter.addDataDisplay(extendedPagedTable);
                        extendedPagedTable.setDataProvider(presenter.getDataProvider());

                        filterPagedTable.createNewTab(extendedPagedTable, key, button, new Command() {
                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter(key);
                            }
                        });
                        applyFilterOnPresenter(key);

                    }
                };
                FilterSettings tableSettings = createTableSettingsPrototype();
                tableSettings.setKey(key);
                dataSetEditorManager.showTableSettingsEditor(filterPagedTable, Constants.INSTANCE.New_Process_InstanceList(), tableSettings, addNewGrid);

            }
        });

        super.init(presenter, new GridGlobalPreferences(PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX, initColumns, bannedColumns), button);

    }

    @Override
    public void initSelectionModel() {

        final ExtendedPagedTable extendedPagedTable = getListGrid();
        extendedPagedTable.setEmptyTableCaption(constants.No_Process_Instances_Found());
        extendedPagedTable.getRightActionsToolbar().clear();
        initExtraButtons(extendedPagedTable);
        initBulkActions(extendedPagedTable);
        selectionModel = new NoSelectionModel<ProcessInstanceSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {

                boolean close = false;
                if (selectedRow == -1) {
                    extendedPagedTable.setRowStyles(selectedStyles);
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();

                } else if (extendedPagedTable.getKeyboardSelectedRow() != selectedRow) {
                    extendedPagedTable.setRowStyles(selectedStyles);
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();
                } else {
                    close = true;
                }

                selectedItem = selectionModel.getLastSelectedObject();

                PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Process Instance Details Multi"));

                if (status == PlaceStatus.CLOSE) {
                    placeManager.goTo("Process Instance Details Multi");
                    processInstanceSelected.fire(new ProcessInstanceSelectionEvent(selectedItem.getDeploymentId(),
                            selectedItem.getProcessInstanceId(), selectedItem.getProcessId(),
                            selectedItem.getProcessName(), selectedItem.getState()));
                } else if (status == PlaceStatus.OPEN && !close) {
                    processInstanceSelected.fire(new ProcessInstanceSelectionEvent(selectedItem.getDeploymentId(),
                            selectedItem.getProcessInstanceId(), selectedItem.getProcessId(),
                            selectedItem.getProcessName(), selectedItem.getState()));
                } else if (status == PlaceStatus.OPEN && close) {
                    placeManager.closePlace("Process Instance Details Multi");
                }

            }
        });

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager(new DefaultSelectionEventManager.EventTranslator<ProcessInstanceSummary>() {

                    @Override
                    public boolean clearCurrentSelection(CellPreviewEvent<ProcessInstanceSummary> event) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<ProcessInstanceSummary> event) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                            // Ignore if the event didn't occur in the correct column.
                            if (extendedPagedTable.getColumnIndex(actionsColumn) == event.getColumn()) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }
                            //Extension for checkboxes
                            Element target = nativeEvent.getEventTarget().cast();
                            if ("input".equals(target.getTagName().toLowerCase())) {
                                final InputElement input = target.cast();
                                if ("checkbox".equals(input.getType().toLowerCase())) {
                                    // Synchronize the checkbox with the current selection state.
                                    if (!selectedProcessInstances.contains(event.getValue())) {
                                        selectedProcessInstances.add(event.getValue());
                                        input.setChecked(true);
                                    } else {
                                        selectedProcessInstances.remove(event.getValue());
                                        input.setChecked(false);
                                    }
                                    controlBulkOperations();
                                    return DefaultSelectionEventManager.SelectAction.IGNORE;
                                }
                            }
                        }

                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }

                });

        extendedPagedTable.setSelectionModel(selectionModel, noActionColumnManager);
        extendedPagedTable.setRowStyles(selectedStyles);
    }

    @Override
    public void initColumns(ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable) {

        Column checkColumn = initChecksColumn();
        Column processInstanceIdColumn = initProcessInstanceIdColumn();
        Column processNameColumn = initProcessNameColumn();
        Column processInitiatorColumn = initInitiatorColumn();
        Column processVersionColumn = initProcessVersionColumn();
        Column processStateColumn = initProcessStateColumn();
        Column startTimeColumn = initStartDateColumn();
        Column descriptionColumn = initDescriptionColumn();
        actionsColumn = initActionsColumn();

        List<ColumnMeta<ProcessInstanceSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessInstanceSummary>>();
        columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(checkColumn, constants.Select()));
        columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(processInstanceIdColumn, constants.Id()));
        columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(processNameColumn, constants.Name()));
        columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(descriptionColumn, constants.Process_Instance_Description()));
        columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(processInitiatorColumn, constants.Initiator()));
        columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(processVersionColumn, constants.Version()));
        columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(processStateColumn, constants.State()));
        columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(startTimeColumn, constants.Start_Date()));
        columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(actionsColumn, constants.Actions()));

        List<GridColumnPreference> columPreferenceList = extendedPagedTable.getGridPreferencesStore().getColumnPreferences();

        for(GridColumnPreference colPref : columPreferenceList){
            if(!isColumnAdded( columnMetas,colPref.getName())){
                Column genericColumn = initGenericColumn(colPref.getName());
                genericColumn.setSortable( false );
                columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(genericColumn, colPref.getName() , true, true));
            }
        }
        extendedPagedTable.addColumns(columnMetas);
    }

    private boolean isColumnAdded(List<ColumnMeta<ProcessInstanceSummary>> columnMetas, String caption){
        if(caption !=null ) {
            for ( ColumnMeta<ProcessInstanceSummary> colMet : columnMetas ) {
                if ( caption.equals( colMet.getCaption() ) ) return true;
            }
        }
        return false;
    }

    public void addDomainSpecifColumns(ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable, Set<String> columns) {
        GWT.log("Adding COlumns : "+columns);
        extendedPagedTable.storeColumnToPreferences();

        HashMap modifiedCaptions= new HashMap <String, String>(  );
        ArrayList<ColumnMeta> existingExtraColumns= new ArrayList<ColumnMeta>(  );
        for (ColumnMeta<ProcessInstanceSummary> cm : extendedPagedTable.getColumnMetaList()) {
            if(cm.isExtraColumn()){
                existingExtraColumns.add(cm );
            } else if(columns.contains( cm.getCaption())){      //exist a column with the same caption
                for (String c : columns) {
                    if(c.equals( cm.getCaption() )){
                        modifiedCaptions.put( c,"Var_"+c );
                    }
                }
            }
        }
        for(ColumnMeta colMet : existingExtraColumns){
            if(!columns.contains( colMet.getCaption()  )) {
                extendedPagedTable.removeColumnMeta( colMet );
            } else{
                columns.remove( colMet.getCaption() );
            }
        }

        List<ColumnMeta<ProcessInstanceSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessInstanceSummary>>();
        String caption="";
        for (String c : columns) {
            caption=c;
            if(modifiedCaptions.get(c)!= null){
                caption = (String) modifiedCaptions.get(c);
            }
            Column genericColumn = initGenericColumn(c);
            genericColumn.setSortable( false );

            columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(genericColumn, caption , true, true));
        }

        extendedPagedTable.addColumns(columnMetas);

    }

    private Column initGenericColumn(final String key) {

        Column<ProcessInstanceSummary, String> genericColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessInstanceSummary object) {
                return object.getDomainDataValue(key);
            }
        };
        genericColumn.setSortable(true);
        genericColumn.setDataStoreName(key);

        return genericColumn;
    }

    public void initExtraButtons(final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable) {
        /*   Button newInstanceButton = new Button();
         newInstanceButton.setTitle(constants.New_Instance());
         newInstanceButton.setIcon( IconType.PLUS_SIGN );
         newInstanceButton.setTitle( Constants.INSTANCE.New_Instance() );
         newInstanceButton.addClickHandler(new ClickHandler() {
         @Override
         public void onClick(ClickEvent event) {
         newProcessInstancePopup.show();
         }
         });
         extendedPagedTable.getRightActionsToolbar().add(newInstanceButton);*/
    }

    private void initBulkActions(final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable) {
        SplitDropdownButton bulkActions = new SplitDropdownButton();
        bulkActions.setText(constants.Bulk_Actions());
        bulkAbortNavLink = new NavLink(constants.Bulk_Abort());
        bulkAbortNavLink.setIcon(IconType.REMOVE_SIGN);
        bulkAbortNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.bulkAbort(selectedProcessInstances);
                selectedProcessInstances.clear();
                extendedPagedTable.redraw();
            }
        });

        bulkSignalNavLink = new NavLink(constants.Bulk_Signal());
        bulkSignalNavLink.setIcon(IconType.BELL);
        bulkSignalNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.bulkSignal(selectedProcessInstances);
                selectedProcessInstances.clear();
                extendedPagedTable.redraw();
            }
        });

        bulkActions.add(bulkAbortNavLink);
        bulkActions.add(bulkSignalNavLink);

        extendedPagedTable.getRightActionsToolbar().add(bulkActions);

        controlBulkOperations();
    }

    private Column initProcessInstanceIdColumn() {
        // Process Instance Id.
        Column<ProcessInstanceSummary, String> processInstanceIdColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessInstanceSummary object) {
                return String.valueOf(object.getProcessInstanceId());
            }
        };
        processInstanceIdColumn.setSortable(true);
        processInstanceIdColumn.setDataStoreName(COLUMN_PROCESSINSTANCEID);

        return processInstanceIdColumn;
    }

    private Column initProcessNameColumn() {
        // Process Name.
        Column<ProcessInstanceSummary, String> processNameColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessInstanceSummary object) {
                return object.getProcessName();
            }
        };
        processNameColumn.setSortable(true);
        processNameColumn.setDataStoreName(COLUMN_PROCESSNAME);

        return processNameColumn;
    }

    private Column initInitiatorColumn() {
        Column<ProcessInstanceSummary, String> processInitiatorColumn = new Column<ProcessInstanceSummary, String>(
                new TextCell()) {
                    @Override
                    public String getValue(ProcessInstanceSummary object) {
                        return object.getInitiator();
                    }
                };
        processInitiatorColumn.setSortable(true);
        processInitiatorColumn.setDataStoreName(COLUMN_IDENTITY);

        return processInitiatorColumn;
    }

    private Column initProcessVersionColumn() {
        // Process Version.
        Column<ProcessInstanceSummary, String> processVersionColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessInstanceSummary object) {
                return object.getProcessVersion();
            }
        };
        processVersionColumn.setSortable(true);
        processVersionColumn.setDataStoreName(COLUMN_PROCESSVERSION);

        return processVersionColumn;
    }

    private Column initProcessStateColumn() {
        // Process State
        Column<ProcessInstanceSummary, String> processStateColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessInstanceSummary object) {
                String statusStr = constants.Unknown();
                switch (object.getState()) {
                    case ProcessInstance.STATE_ACTIVE:
                        statusStr = constants.Active();
                        break;
                    case ProcessInstance.STATE_ABORTED:
                        statusStr = constants.Aborted();
                        break;
                    case ProcessInstance.STATE_COMPLETED:
                        statusStr = constants.Completed();
                        break;
                    case ProcessInstance.STATE_PENDING:
                        statusStr = constants.Pending();
                        break;
                    case ProcessInstance.STATE_SUSPENDED:
                        statusStr = constants.Suspended();
                        break;

                    default:
                        break;
                }

                return statusStr;
            }
        };
        processStateColumn.setSortable(true);
        processStateColumn.setDataStoreName(COLUMN_STATUS);

        return processStateColumn;
    }

    private Column initStartDateColumn() {
        // start time
        Column<ProcessInstanceSummary, String> startTimeColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessInstanceSummary object) {
                Date startTime = object.getStartTime();
                if (startTime != null) {
                    DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
                    return format.format(startTime);
                }
                return "";
            }
        };
        startTimeColumn.setSortable(true);
        startTimeColumn.setDataStoreName(COLUMN_START);

        return startTimeColumn;
    }

    private Column initActionsColumn() {
        List<HasCell<ProcessInstanceSummary, ?>> cells = new LinkedList<HasCell<ProcessInstanceSummary, ?>>();

        cells.add(new SignalActionHasCell(constants.Signal(), new Delegate<ProcessInstanceSummary>() {
            @Override
            public void execute(ProcessInstanceSummary processInstance) {

                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Signal Process Popup");
                placeRequestImpl.addParameter("processInstanceId", Long.toString(processInstance.getProcessInstanceId()));

                placeManager.goTo(placeRequestImpl);
            }
        }));

        cells.add(new AbortActionHasCell(constants.Abort(), new Delegate<ProcessInstanceSummary>() {
            @Override
            public void execute(ProcessInstanceSummary processInstance) {
                if (Window.confirm("Are you sure that you want to abort the process instance?")) {
                    presenter.abortProcessInstance(processInstance.getProcessInstanceId());
                }
            }
        }));

        CompositeCell<ProcessInstanceSummary> cell = new CompositeCell<ProcessInstanceSummary>(cells);
        Column<ProcessInstanceSummary, ProcessInstanceSummary> actionsColumn = new Column<ProcessInstanceSummary, ProcessInstanceSummary>(
                cell) {
                    @Override
                    public ProcessInstanceSummary getValue(ProcessInstanceSummary object) {
                        return object;
                    }
                };
        return actionsColumn;

    }

    private Column initChecksColumn() {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.
        Column<ProcessInstanceSummary, Boolean> checkColumn = new Column<ProcessInstanceSummary, Boolean>(new CheckboxCell(
                true, false)) {
                    @Override
                    public Boolean getValue(ProcessInstanceSummary object) {
                        // Get the value from the selection model.
                        return selectedProcessInstances.contains(object);
                    }
                };

        return checkColumn;
    }

    private Column initDescriptionColumn() {
        // start time
        Column<ProcessInstanceSummary, String> descriptionColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessInstanceSummary object) {
                return object.getProcessInstanceDescription();
            }
        };
        descriptionColumn.setSortable(true);
        descriptionColumn.setDataStoreName(COLUMN_PROCESSINSTANCEDESCRIPTION);
        return descriptionColumn;
    }

    public void onProcessInstanceSelectionEvent(@Observes ProcessInstancesWithDetailsRequestEvent event) {
        placeManager.goTo("Process Instance Details Multi");
        processInstanceSelected.fire(new ProcessInstanceSelectionEvent(event.getDeploymentId(),
                event.getProcessInstanceId(), event.getProcessDefId(),
                event.getProcessDefName(), event.getProcessInstanceStatus()));
    }

    private class AbortActionHasCell implements HasCell<ProcessInstanceSummary, ProcessInstanceSummary> {

        private ActionCell<ProcessInstanceSummary> cell;

        public AbortActionHasCell(String text,
                Delegate<ProcessInstanceSummary> delegate) {
            cell = new ActionCell<ProcessInstanceSummary>(text, delegate) {
                @Override
                public void render(Context context,
                        ProcessInstanceSummary value,
                        SafeHtmlBuilder sb) {
                    if (value.getState() == ProcessInstance.STATE_ACTIVE) {
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<a href='javascript:;' class='btn btn-mini' style='margin-right:5px;' title='" + constants.Abort() + "'>" + constants.Abort() + "</a>&nbsp;");
                        sb.append(mysb.toSafeHtml());
                    }
                }
            };
        }

        @Override
        public Cell<ProcessInstanceSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessInstanceSummary, ProcessInstanceSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessInstanceSummary getValue(ProcessInstanceSummary object) {
            return object;
        }
    }

    private class SignalActionHasCell implements HasCell<ProcessInstanceSummary, ProcessInstanceSummary> {

        private ActionCell<ProcessInstanceSummary> cell;

        public SignalActionHasCell(String text,
                Delegate<ProcessInstanceSummary> delegate) {
            cell = new ActionCell<ProcessInstanceSummary>(text, delegate) {
                @Override
                public void render(Context context,
                        ProcessInstanceSummary value,
                        SafeHtmlBuilder sb) {
                    if (value.getState() == ProcessInstance.STATE_ACTIVE) {
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<a href='javascript:;' class='btn btn-mini' style='margin-right:5px;' title='" + constants.Signal() + "'>" + constants.Signal() + "</a>");
                        sb.append(mysb.toSafeHtml());
                    }
                }
            };
        }

        @Override
        public Cell<ProcessInstanceSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessInstanceSummary, ProcessInstanceSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessInstanceSummary getValue(ProcessInstanceSummary object) {
            return object;
        }
    }

    public void formClosed(@Observes BeforeClosePlaceEvent closed) {
        if ("Signal Process Popup".equals(closed.getPlace().getIdentifier())) {
            presenter.refreshGrid();
        }
    }

    public void initDefaultFilters(GridGlobalPreferences preferences, Button createTabButton) {

        List<String> states = new ArrayList<String>();

        //Filter status Active
        states.add(String.valueOf(ProcessInstance.STATE_ACTIVE));
        initGenericTabFilter(preferences, PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_0", Constants.INSTANCE.Active(), "Filter " + Constants.INSTANCE.Active(), states, "", "");

        //Filter status completed
        states = new ArrayList<String>();
        states.add(String.valueOf(ProcessInstance.STATE_COMPLETED));
        initGenericTabFilter(preferences, PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_1", Constants.INSTANCE.Completed(), "Filter " + Constants.INSTANCE.Completed(), states, "", "");
        
        //Filter status completed
        states = new ArrayList<String>();
        states.add(String.valueOf(ProcessInstance.STATE_ABORTED));
        initGenericTabFilter(preferences, PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_2", Constants.INSTANCE.Aborted(), "Filter " + Constants.INSTANCE.Aborted(), states, "", "");

        filterPagedTable.addAddTableButton(createTabButton);
        getMultiGridPreferencesStore().setSelectedGrid(PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_0");
        filterPagedTable.setSelectedTab();
        applyFilterOnPresenter(PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_0");

    }

    private void initGenericTabFilter(GridGlobalPreferences preferences, final String key, String tabName,
            String tabDesc, List<String> states, String processDefinition, String initiator) {

        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(PROCESS_INSTANCES_DATASET_ID);
        List<Comparable> names = new ArrayList<Comparable>();

        for (String s : states) {
            names.add(s);
        }
        builder.filter(equalsTo(COLUMN_STATUS, names));

        builder.setColumn(COLUMN_PROCESSINSTANCEID, "processInstanceId");
        builder.setColumn(COLUMN_PROCESSID, "processId");
        builder.setColumn(COLUMN_START, "start", "MMM dd E, yyyy");
        builder.setColumn(COLUMN_END, "end", "MMM dd E, yyyy");
        builder.setColumn(COLUMN_STATUS, "status");
        builder.setColumn(COLUMN_PARENTPROCESSINSTANCEID, "parentProcessInstanceId");
        builder.setColumn(COLUMN_OUTCOME, "outcome");
        builder.setColumn(COLUMN_DURATION, "duration");
        builder.setColumn(COLUMN_IDENTITY, "identity");
        builder.setColumn(COLUMN_PROCESSVERSION, "processVersion");
        builder.setColumn(COLUMN_PROCESSNAME, "processName");
        builder.setColumn(COLUMN_CORRELATIONKEY, "CorrelationKey");
        builder.setColumn(COLUMN_EXTERNALID, "externalId");
        builder.setColumn(COLUMN_PROCESSINSTANCEDESCRIPTION, "processInstanceDescription");

        builder.filterOn(true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(COLUMN_START, DESCENDING);

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setKey(key);
        tableSettings.setTableName(tabName);
        tableSettings.setTableDescription(tabDesc);

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

        tabSettingsValues.put(FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr(tableSettings));
        tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName());
        tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription());

        filterPagedTable.saveNewTabSettings(key, tabSettingsValues);

        final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable = createGridInstance(new GridGlobalPreferences(key, preferences.getInitialColumns(), preferences.getBannedColumns()), key);
        currentListGrid = extendedPagedTable;
        presenter.addDataDisplay(extendedPagedTable);
        extendedPagedTable.setDataProvider(presenter.getDataProvider());

        filterPagedTable.addTab(extendedPagedTable, key, new Command() {
            @Override
            public void execute() {
                currentListGrid = extendedPagedTable;
                applyFilterOnPresenter(key);
            }
        });

    }

    public void applyFilterOnPresenter(HashMap<String, Object> params) {

        String tableSettingsJSON = (String) params.get(FILTER_TABLE_SETTINGS);
        FilterSettings tableSettings = dataSetEditorManager.getStrToTableSettings(tableSettingsJSON);
        presenter.filterGrid(tableSettings);

    }

    @Override
    public FilterSettings getVariablesTableSettings(String processName) {
        String tableSettingsJSON = "{\n"
                + "    \"type\": \"TABLE\",\n"
                + "    \"filter\": {\n"
                + "        \"enabled\": \"true\",\n"
                + "        \"selfapply\": \"true\",\n"
                + "        \"notification\": \"true\",\n"
                + "        \"listening\": \"true\"\n"
                + "    },\n"
                + "    \"table\": {\n"
                + "        \"sort\": {\n"
                + "            \"enabled\": \"true\",\n"
                + "            \"columnId\": \"varname\",\n"
                + "            \"order\": \"DESCENDING\"\n"
                + "        }\n"
                + "    },\n"
                + "    \"dataSetLookup\": {\n"
                + "        \"dataSetUuid\": \"jbpmProcessInstancesWithVariables\",\n"
                + "        \"rowCount\": \"-1\",\n"
                + "        \"rowOffset\": \"0\",\n";
        if (processName != null) {
            tableSettingsJSON += "        \"filterOps\":[{\"columnId\":\"pname\", \"functionType\":\"EQUALS_TO\", \"terms\":[\"" + processName + "\"]}],";
        }
        tableSettingsJSON += "        \"groupOps\": [\n"
                + "            {\n"
                + "                \"groupFunctions\": [\n"
                + "                    {\n"
                + "                        \"sourceId\": \"pid\",\n"
                + "                        \"columnId\": \"pid\"\n"
                + "                    },\n"
                + "                    {\n"
                + "                        \"sourceId\": \"pname\",\n"
                + "                        \"columnId\": \"pname\"\n"
                + "                    },\n"
                + "                    {\n"
                + "                        \"sourceId\": \"varid\",\n"
                + "                        \"columnId\": \"varid\"\n"
                + "                    },\n"
                + "                    {\n"
                + "                        \"sourceId\": \"varname\",\n"
                + "                        \"columnId\": \"varname\"\n"
                + "                    },\n"
                + "                    {\n"
                + "                        \"sourceId\": \"varvalue\",\n"
                + "                        \"columnId\": \"varvalue\"\n"
                + "                    }\n"
                + "                ],\n"
                + "                \"join\": \"false\"\n"
                + "            }\n"
                + "        ]\n"
                + "    },\n"
                + "    \"columns\": [\n"
                + "        {\n"
                + "            \"id\": \"pid\",\n"
                + "            \"name\": \"processInstanceId\"\n"
                + "        },\n"
                + "        {\n"
                + "            \"id\": \"pname\",\n"
                + "            \"name\": \"processName\"\n"
                + "        },\n"
                + "        {\n"
                + "            \"id\": \"varid\",\n"
                + "            \"name\": \"variableID\"\n"
                + "        },\n"
                + "        {\n"
                + "            \"id\": \"varname\",\n"
                + "            \"name\": \"variableName\"\n"
                + "        },\n"
                + "        {\n"
                + "            \"id\": \"varvalue\",\n"
                + "            \"name\": \"variableValue\"\n"
                + "        }\n"
                + "    ],\n"
                + "    \"tableName\": \"Filtered\",\n"
                + "    \"tableDescription\": \"Filtered Desc\",\n"
                + "    \"tableEditEnabled\": \"false\"\n"
                + "}";
        
        return dataSetEditorManager.getStrToTableSettings(tableSettingsJSON);
    }

    public void applyFilterOnPresenter(String key) {
        initSelectionModel();
        applyFilterOnPresenter(filterPagedTable.getMultiGridPreferencesStore().getGridSettings(key));
    }

    /*-------------------------------------------------*/
    /*---              DashBuilder                   --*/
    /*-------------------------------------------------*/
    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(PROCESS_INSTANCES_DATASET_ID);

        /*  builder.setColumn( COLUMN_TASKID, constants.Id() );
         builder.setColumn( COLUMN_NAME, constants.Task() );
         builder.setColumn( COLUMN_DESCRIPTION, constants.Description() );
         builder.setColumn( COLUMN_PRIORITY, "Priority" );
         builder.setColumn( COLUMN_STATUS, constants.Status() );
         builder.setColumn( COLUMN_CREATEDON , "Created on", "MMM dd E, yyyy" );
         builder.setColumn( COLUMN_DUEDATE, "Due Date", "MMM dd E, yyyy" );
         */
        builder.setColumn(COLUMN_PROCESSINSTANCEID, "processInstanceId");
        builder.setColumn(COLUMN_PROCESSID, "processId");
        builder.setColumn(COLUMN_START, "start", "MMM dd E, yyyy");
        builder.setColumn(COLUMN_END, "end", "MMM dd E, yyyy");
        builder.setColumn(COLUMN_STATUS, "status");
        builder.setColumn(COLUMN_PARENTPROCESSINSTANCEID, "parentProcessInstanceId");
        builder.setColumn(COLUMN_OUTCOME, "outcome");
        builder.setColumn(COLUMN_DURATION, "duration");
        builder.setColumn(COLUMN_IDENTITY, "identity");
        builder.setColumn(COLUMN_PROCESSVERSION, "processVersion");
        builder.setColumn(COLUMN_PROCESSNAME, "processName");
        builder.setColumn(COLUMN_CORRELATIONKEY, "CorrelationKey");
        builder.setColumn(COLUMN_EXTERNALID, "externalId");
        builder.setColumn(COLUMN_PROCESSINSTANCEDESCRIPTION, "processInstanceDescription");

        builder.filterOn(true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(COLUMN_START, DESCENDING);
        builder.tableWidth(1000);

        return builder.buildSettings();

    }

    public int getRefreshValue() {
        return getMultiGridPreferencesStore().getRefreshInterval();
    }

    public void saveRefreshValue(int newValue) {
        filterPagedTable.saveNewRefreshInterval(newValue);
    }

    public void restoreTabs() {
        ArrayList<String> existingGrids = getMultiGridPreferencesStore().getGridsId();
        ArrayList<String> allTabs = new ArrayList<String>(existingGrids.size());

        if (existingGrids != null && existingGrids.size() > 0) {

            for (int i = 0; i < existingGrids.size(); i++) {
                allTabs.add(existingGrids.get(i));
            }

            for (int i = 0; i < allTabs.size(); i++) {
                filterPagedTable.removeTab(allTabs.get(i));
            }

        }
        filterPagedTable.tabPanel.remove(0);
        initDefaultFilters(currentGlobalPreferences, createTabButton);
    }

}
