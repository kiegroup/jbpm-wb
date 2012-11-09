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
package org.jbpm.console.ng.client.editors.process.definition.list;

import java.util.Comparator;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
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
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.shared.events.ProcessDefSelectionEvent;
import org.jbpm.console.ng.shared.model.ProcessSummary;
import org.jbpm.console.ng.client.util.ResizableHeader;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "ProcessDefinitionListViewImpl.html")
public class ProcessDefinitionListViewImpl extends Composite
        implements
        ProcessDefinitionListPresenter.InboxView {

    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    
    private ProcessDefinitionListPresenter presenter;
    
    @Inject
    @DataField
    public TextBox filterKSessionText;
    
    @Inject
    @DataField
    public Button filterKSessionButton;

    @Inject
    @DataField
    public DataGrid<ProcessSummary> processdefListGrid;

    @Inject
    @DataField
    public SimplePager pager;
    
    
    private Set<ProcessSummary> selectedProcessDef;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<ProcessDefSelectionEvent> processSelection;
    private ListHandler<ProcessSummary> sortHandler;

    @Override
    public void init(ProcessDefinitionListPresenter presenter) {
        this.presenter = presenter;


        processdefListGrid.setWidth("100%");
        processdefListGrid.setHeight("200px");

        // Set the message to display when the table is empty.
        processdefListGrid.setEmptyTableWidget(new Label("No Process Definitions Available"));

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler =
                new ListHandler<ProcessSummary>(presenter.getDataProvider().getList());
        processdefListGrid.addColumnSortHandler(sortHandler);

        // Create a Pager to control the table.

        pager.setDisplay(processdefListGrid);
        pager.setPageSize(6);

        // Add a selection model so we can select cells.
        final MultiSelectionModel<ProcessSummary> selectionModel =
                new MultiSelectionModel<ProcessSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedProcessDef = selectionModel.getSelectedSet();
                for (ProcessSummary pd : selectedProcessDef) {
                    processSelection.fire(new ProcessDefSelectionEvent(pd.getId()));
                }
            }
        });

        processdefListGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager
                .<ProcessSummary>createCheckboxManager());

        initTableColumns(selectionModel);

        

        presenter.addDataDisplay(processdefListGrid);

    }

    

    @EventHandler("filterKSessionButton")
    public void filterKSessionButton(ClickEvent e) {
        presenter.refreshProcessList(filterKSessionText.getText());
    }

   
    private void initTableColumns(final SelectionModel<ProcessSummary> selectionModel) {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

        Column<ProcessSummary, Boolean> checkColumn =
                new Column<ProcessSummary, Boolean>(new CheckboxCell(true,
                false)) {
                    @Override
                    public Boolean getValue(ProcessSummary object) {
                        // Get the value from the selection model.
                        return selectionModel.isSelected(object);
                    }
                };
        processdefListGrid.addColumn(checkColumn,
                SafeHtmlUtils.fromSafeConstant("<br/>"));


        // Id.
        Column<ProcessSummary, String> processIdColumn =
                new Column<ProcessSummary, String>(new EditTextCell()) {
                    @Override
                    public String getValue(ProcessSummary object) {
                        return object.getId();
                    }
                };
        processIdColumn.setSortable(true);
        sortHandler.setComparator(processIdColumn,
                new Comparator<ProcessSummary>() {
                    public int compare(ProcessSummary o1,
                            ProcessSummary o2) {
                        return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
                    }
                });
        processdefListGrid.addColumn(processIdColumn,
                new ResizableHeader("Id", processdefListGrid, processIdColumn));


        // Process Id String.
        Column<ProcessSummary, String> processNameColumn =
                new Column<ProcessSummary, String>(new EditTextCell()) {
                    @Override
                    public String getValue(ProcessSummary object) {
                        return object.getName();
                    }
                };
        processNameColumn.setSortable(true);
        sortHandler.setComparator(processNameColumn,
                new Comparator<ProcessSummary>() {
                    public int compare(ProcessSummary o1,
                            ProcessSummary o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
        processdefListGrid.addColumn(processNameColumn,
                new ResizableHeader("Name", processdefListGrid, processNameColumn));

         // Process Name.
        Column<ProcessSummary, String> processPkgColumn =
                new Column<ProcessSummary, String>(new EditTextCell()) {
                    @Override
                    public String getValue(ProcessSummary object) {
                        return object.getPackageName();
                    }
                };
        processPkgColumn.setSortable(true);
        sortHandler.setComparator(processPkgColumn,
                new Comparator<ProcessSummary>() {
                    public int compare(ProcessSummary o1,
                            ProcessSummary o2) {
                        return o1.getPackageName().compareTo(o2.getPackageName());
                    }
                });
        processdefListGrid.addColumn(processPkgColumn,
                new ResizableHeader("Package", processdefListGrid, processPkgColumn));
        
        
        // Process Type 
        Column<ProcessSummary, String> processTypeColumn =
                new Column<ProcessSummary, String>(new EditTextCell()) {
                    @Override
                    public String getValue(ProcessSummary object) {
                        return object.getType();
                    }
                };
        processTypeColumn.setSortable(true);
        sortHandler.setComparator(processTypeColumn,
                new Comparator<ProcessSummary>() {
                    public int compare(ProcessSummary o1,
                            ProcessSummary o2) {
                        return o1.getType().compareTo(o2.getType());
                    }
                });
        processdefListGrid.addColumn(processTypeColumn,
                new ResizableHeader("Type", processdefListGrid, processTypeColumn));

        
         // Version Type 
        Column<ProcessSummary, String> versionColumn =
                new Column<ProcessSummary, String>(new EditTextCell()) {
                    @Override
                    public String getValue(ProcessSummary object) {
                        return object.getVersion();
                    }
                };
        versionColumn.setSortable(true);
        sortHandler.setComparator(versionColumn,
                new Comparator<ProcessSummary>() {
                    public int compare(ProcessSummary o1,
                            ProcessSummary o2) {
                        return o1.getVersion().compareTo(o2.getVersion());
                    }
                });
        processdefListGrid.addColumn(versionColumn,
                new ResizableHeader("Version", processdefListGrid, versionColumn));


        Column<ProcessSummary, String> newInstanceColumn =
                new Column<ProcessSummary, String>(new ButtonCell()) {
                    @Override
                    public String getValue(ProcessSummary task) {
                        return "Start Process";
                    }
                };

        newInstanceColumn.setFieldUpdater(new FieldUpdater<ProcessSummary, String>() {
            @Override
            public void update(int index,
                    ProcessSummary process,
                    String value) {
                presenter.startProcessInstance(process.getId());

            }
        });

        processdefListGrid.addColumn(newInstanceColumn,
                new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Actions")));

        Column<ProcessSummary, String> detailsColumn =
                new Column<ProcessSummary, String>(new ButtonCell()) {
                    @Override
                    public String getValue(ProcessSummary task) {
                        return "Details";
                    }
                };

        detailsColumn.setFieldUpdater(new FieldUpdater<ProcessSummary, String>() {
            @Override
            public void update(int index,
                    ProcessSummary process,
                    String value) {
                
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Process Definition Details Perspective" );  
                placeRequestImpl.addParameter("processId", process.getId());
                placeManager.goTo( placeRequestImpl);

            }
        });

        processdefListGrid.addColumn(detailsColumn,
                new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Details")));
        


    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }


    public DataGrid<ProcessSummary> getDataGrid() {
        return processdefListGrid;
    }

    public ListHandler<ProcessSummary> getSortHandler() {
        return sortHandler;
    }

    public TextBox getSessionIdText() {
       return filterKSessionText;
    }
}
