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
package org.jbpm.console.ng.client.editors.process.instance.list;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.runtime.process.ProcessInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.client.i18n.Constants;
import org.jbpm.console.ng.client.util.ResizableHeader;
import org.jbpm.console.ng.shared.events.ProcessSelectionEvent;
import org.jbpm.console.ng.shared.model.ProcessInstanceSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;

@Dependent
@Templated(value = "ProcessInstanceListViewImpl.html")
public class ProcessInstanceListViewImpl extends Composite
        implements
        ProcessInstanceListPresenter.InboxView {

    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    
    private ProcessInstanceListPresenter presenter;
    
    @Inject
    @DataField
    public TextBox filterKSessionText;
    
    @Inject
    @DataField
    public Button filterKSessionButton;
    
    @Inject
    @DataField
    public Button deleteButton;
    
    @Inject
    @DataField
    public Button terminateButton;
    
    @Inject
    @DataField
    public Button signalButton;

    @Inject
    @DataField
    public DataGrid<ProcessInstanceSummary> processInstanceListGrid;

    @Inject
    @DataField
    public SimplePager pager;
    
    
    private Set<ProcessInstanceSummary> selectedProcessInstances;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<ProcessSelectionEvent> processSelection;
    private ListHandler<ProcessInstanceSummary> sortHandler;
    private Constants constants = GWT.create(Constants.class);
    
    @Override
    public void init(ProcessInstanceListPresenter presenter) {
        this.presenter = presenter;


        processInstanceListGrid.setWidth("100%");
        processInstanceListGrid.setHeight("200px");

        // Set the message to display when the table is empty.
        processInstanceListGrid.setEmptyTableWidget(new Label(constants.No_Process_Instances_Available()));

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler =
                new ListHandler<ProcessInstanceSummary>(presenter.getDataProvider().getList());
        processInstanceListGrid.addColumnSortHandler(sortHandler);

        // Create a Pager to control the table.

        pager.setDisplay(processInstanceListGrid);
        pager.setPageSize(6);

        // Add a selection model so we can select cells.
        final MultiSelectionModel<ProcessInstanceSummary> selectionModel =
                new MultiSelectionModel<ProcessInstanceSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedProcessInstances = selectionModel.getSelectedSet();
                for (ProcessInstanceSummary ts : selectedProcessInstances) {
                    processSelection.fire(new ProcessSelectionEvent(ts.getId()));
                }
            }
        });

        processInstanceListGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager
                .<ProcessInstanceSummary>createCheckboxManager());

        initTableColumns(selectionModel);

        

        presenter.addDataDisplay(processInstanceListGrid);

    }

    

    @EventHandler("filterKSessionButton")
    public void filterKSessionButton(ClickEvent e) {
        presenter.refreshProcessList(filterKSessionText.getText());
    }
    
    @EventHandler("deleteButton")
    public void deleteButton(ClickEvent e) {
        displayNotification(constants.Deleting_Process_Instance());
    }
    
    @EventHandler("terminateButton")
    public void terminateButton(ClickEvent e) {
        displayNotification(constants.Terminating_Process_Instance());
    }
    
    @EventHandler("signalButton")
    public void signalButton(ClickEvent e) {
        displayNotification(constants.Signaling_Process_Instance());
    }

   
    private void initTableColumns(final SelectionModel<ProcessInstanceSummary> selectionModel) {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

        Column<ProcessInstanceSummary, Boolean> checkColumn =
                new Column<ProcessInstanceSummary, Boolean>(new CheckboxCell(true,
                false)) {
                    @Override
                    public Boolean getValue(ProcessInstanceSummary object) {
                        // Get the value from the selection model.
                        return selectionModel.isSelected(object);
                    }
                };
        processInstanceListGrid.addColumn(checkColumn,
                SafeHtmlUtils.fromSafeConstant("<br/>"));


        // Id.
        Column<ProcessInstanceSummary, Number> processInstanceIdColumn =
                new Column<ProcessInstanceSummary, Number>(new NumberCell()) {
                    @Override
                    public Number getValue(ProcessInstanceSummary object) {
                        return object.getId();
                    }
                };
        processInstanceIdColumn.setSortable(true);
        sortHandler.setComparator(processInstanceIdColumn,
                new Comparator<ProcessInstanceSummary>() {
                    public int compare(ProcessInstanceSummary o1,
                            ProcessInstanceSummary o2) {
                        return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
                    }
                });
        processInstanceListGrid.addColumn(processInstanceIdColumn,
                new ResizableHeader(constants.Id(), processInstanceListGrid, processInstanceIdColumn));


        // Process Id String.
        Column<ProcessInstanceSummary, String> processIdColumn =
                new Column<ProcessInstanceSummary, String>(new TextCell()) {
                    @Override
                    public String getValue(ProcessInstanceSummary object) {
                        return object.getProcessId();
                    }
                };
        processIdColumn.setSortable(true);
        sortHandler.setComparator(processIdColumn,
                new Comparator<ProcessInstanceSummary>() {
                    public int compare(ProcessInstanceSummary o1,
                            ProcessInstanceSummary o2) {
                        return o1.getProcessId().compareTo(o2.getProcessId());
                    }
                });
        processInstanceListGrid.addColumn(processIdColumn,
                new ResizableHeader(constants.Process_Id(), processInstanceListGrid, processIdColumn));

         // Process Name.
        Column<ProcessInstanceSummary, String> processNameColumn =
                new Column<ProcessInstanceSummary, String>(new TextCell()) {
                    @Override
                    public String getValue(ProcessInstanceSummary object) {
                        return object.getProcessName();
                    }
                };
        processNameColumn.setSortable(true);
        sortHandler.setComparator(processNameColumn,
                new Comparator<ProcessInstanceSummary>() {
                    public int compare(ProcessInstanceSummary o1,
                            ProcessInstanceSummary o2) {
                        return o1.getProcessId().compareTo(o2.getProcessId());
                    }
                });
        processInstanceListGrid.addColumn(processNameColumn,
                new ResizableHeader(constants.Process_Name(), processInstanceListGrid, processNameColumn));
        
        // Process Version.
        Column<ProcessInstanceSummary, String> processVersionColumn =
                new Column<ProcessInstanceSummary, String>(new TextCell()) {
                    @Override
                    public String getValue(ProcessInstanceSummary object) {
                        return object.getProcessVersion();
                    }
                };
                processVersionColumn.setSortable(true);
        sortHandler.setComparator(processVersionColumn,
                new Comparator<ProcessInstanceSummary>() {
                    public int compare(ProcessInstanceSummary o1,
                            ProcessInstanceSummary o2) {
                        return o1.getProcessVersion().compareTo(o2.getProcessVersion());
                    }
                });
        processInstanceListGrid.addColumn(processVersionColumn,
                new ResizableHeader(constants.Process_Version(), processInstanceListGrid, processVersionColumn));
        
        // Process State 
        Column<ProcessInstanceSummary, String> processStateColumn =
                new Column<ProcessInstanceSummary, String>(new TextCell()) {
                    @Override
                    public String getValue(ProcessInstanceSummary object) {
                        String statusStr = "Unknown";
                        switch (object.getState()) {
                        case ProcessInstance.STATE_ACTIVE:
                            statusStr = "Active";
                            break;
                        case ProcessInstance.STATE_ABORTED:
                            statusStr = "Aborted";
                            break;
                        case ProcessInstance.STATE_COMPLETED:
                            statusStr = "Completed";
                            break;
                        case ProcessInstance.STATE_PENDING:
                            statusStr = "Pending";
                            break;
                        case ProcessInstance.STATE_SUSPENDED:
                            statusStr = "Suspended";
                            break;

                        default:
                            break;
                        }
                        
                        return statusStr;
                    }
                };
        processStateColumn.setSortable(true);
        sortHandler.setComparator(processStateColumn,
                new Comparator<ProcessInstanceSummary>() {
                    public int compare(ProcessInstanceSummary o1,
                            ProcessInstanceSummary o2) {
                        return Integer.valueOf(o1.getState()).compareTo(o2.getState());
                    }
                });
        processInstanceListGrid.addColumn(processStateColumn,
                new ResizableHeader(constants.State(), processInstanceListGrid, processStateColumn));
        
        // start time
       Column<ProcessInstanceSummary, String> startTimeColumn =
               new Column<ProcessInstanceSummary, String>(new TextCell()) {
                   @Override
                   public String getValue(ProcessInstanceSummary object) {
                       return new Date(object.getStartTime()).toString();
                   }
               };
       startTimeColumn.setSortable(true);
       sortHandler.setComparator(startTimeColumn,
               new Comparator<ProcessInstanceSummary>() {
                   public int compare(ProcessInstanceSummary o1,
                           ProcessInstanceSummary o2) {
                       return Long.valueOf(o1.getStartTime()).compareTo(Long.valueOf(o2.getStartTime()));
                   }
               });
       processInstanceListGrid.addColumn(startTimeColumn,
               new ResizableHeader(constants.Process_Instance_Start_Time(), processInstanceListGrid, startTimeColumn));
        

        Column<ProcessInstanceSummary, String> detailsColumn =
                new Column<ProcessInstanceSummary, String>(new ButtonCell()) {
                    @Override
                    public String getValue(ProcessInstanceSummary task) {
                        return "Details";
                    }
                };
                


        detailsColumn.setFieldUpdater(new FieldUpdater<ProcessInstanceSummary, String>() {
            @Override
            public void update(int index,
                    ProcessInstanceSummary process,
                    String value) {

                DefaultPlaceRequest placeRequestImpl = new DefaultPlaceRequest(constants.Process_Instance_Details_Perspective());
                placeRequestImpl.addParameter("processInstanceId", Long.toString(process.getId()));
                placeRequestImpl.addParameter("processDefId", process.getProcessId());
                placeManager.goTo(placeRequestImpl);

            }
        });
        
        processInstanceListGrid.addColumn(detailsColumn,
                new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant(constants.Details())));


    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }


    public DataGrid<ProcessInstanceSummary> getDataGrid() {
        return processInstanceListGrid;
    }

    public ListHandler<ProcessInstanceSummary> getSortHandler() {
        return sortHandler;
    }

    public TextBox getSessionIdText() {
       return filterKSessionText;
    }
    
}
