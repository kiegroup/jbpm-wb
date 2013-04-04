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
package org.jbpm.console.ng.pr.client.editors.definition.list;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.console.ng.pr.client.util.ResizableHeader;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ShowcaseImages;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.ProcessDefSelectionEvent;

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
    public Button reloadRepoButton;
    @Inject
    @DataField
    public DataGrid<ProcessSummary> processdefListGrid;
    @Inject
    @DataField 
    public FlowPanel listContainer;
    @Inject
    @DataField
    public SimplePager pager;
    private Set<ProcessSummary> selectedProcessDef;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<ProcessDefSelectionEvent> processSelection;
    private ListHandler<ProcessSummary> sortHandler;
    
    private Constants constants = GWT.create(Constants.class);
    private ShowcaseImages images = GWT.create(ShowcaseImages.class);

    @Override
    public void init(ProcessDefinitionListPresenter presenter) {
        this.presenter = presenter;

       
        listContainer.add(processdefListGrid);
        listContainer.add(pager);
        
        processdefListGrid.setHeight("350px");

        // Set the message to display when the table is empty.
        processdefListGrid.setEmptyTableWidget(new Label(constants.No_Process_Definitions_Available()));

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler =
                new ListHandler<ProcessSummary>(presenter.getDataProvider().getList());
        processdefListGrid.addColumnSortHandler(sortHandler);

        // Create a Pager to control the table.

        pager.setDisplay(processdefListGrid);
        pager.setPageSize(10);

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
    
    @EventHandler("reloadRepoButton")
    public void reloadRepoButton(ClickEvent e) {
        presenter.reloadRepository();
    }
    
    private void initTableColumns(final SelectionModel<ProcessSummary> selectionModel) {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

//        Column<ProcessSummary, Boolean> checkColumn =
//                new Column<ProcessSummary, Boolean>(new CheckboxCell(true,
//                false)) {
//            @Override
//            public Boolean getValue(ProcessSummary object) {
//                // Get the value from the selection model.
//                return selectionModel.isSelected(object);
//            }
//        };
//        
//        processdefListGrid.addColumn(checkColumn,
//                SafeHtmlUtils.fromSafeConstant("<br/>"));
//        processdefListGrid.setColumnWidth(checkColumn, "40px");

        // Id.
        Column<ProcessSummary, String> processIdColumn =
                new Column<ProcessSummary, String>(new TextCell()) {
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
                return o1.getId().compareTo(o2.getId());
            }
        });
        processdefListGrid.addColumn(processIdColumn,
                new ResizableHeader(constants.Id(), processdefListGrid, processIdColumn));


        // Process Id String.
        Column<ProcessSummary, String> processNameColumn =
                new Column<ProcessSummary, String>(new TextCell()) {
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
                new ResizableHeader(constants.Name(), processdefListGrid, processNameColumn));

        // Process PKG.
        Column<ProcessSummary, String> processPkgColumn =
                new Column<ProcessSummary, String>(new TextCell()) {
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
                new ResizableHeader(constants.Package(), processdefListGrid, processPkgColumn));
        
         // Process Domain Id.
        Column<ProcessSummary, String> processDomainIdColumn =
                new Column<ProcessSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessSummary object) {
                return object.getDomainId();
            }
        };
        processDomainIdColumn.setSortable(true);
        sortHandler.setComparator(processDomainIdColumn,
                new Comparator<ProcessSummary>() {
            public int compare(ProcessSummary o1,
                    ProcessSummary o2) {
                return o1.getDomainId().compareTo(o2.getDomainId());
            }
        });
        processdefListGrid.addColumn(processDomainIdColumn,
                new ResizableHeader("Domain", processdefListGrid, processDomainIdColumn));
        processdefListGrid.setColumnWidth(processDomainIdColumn, "90px");

        // Version Type 
        Column<ProcessSummary, String> versionColumn =
                new Column<ProcessSummary, String>(new TextCell()) {
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
                Integer version1 = ((o1.getVersion() == null || o1.getVersion().equals("")))?0:Integer.valueOf(o1.getVersion());
                Integer version2 = ((o2.getVersion() == null || o2.getVersion().equals("")))?0:Integer.valueOf(o2.getVersion());
                return version1.compareTo(version2);
            }
        });
        processdefListGrid.addColumn(versionColumn,
                new ResizableHeader(constants.Version(), processdefListGrid, versionColumn));
        processdefListGrid.setColumnWidth(versionColumn, "90px");
        
        // actions (icons)
        List<HasCell<ProcessSummary, ?>> cells = new LinkedList<HasCell<ProcessSummary, ?>>();

        cells.add(new StartActionHasCell("Start process", new Delegate<ProcessSummary>() {
            @Override
            public void execute(ProcessSummary process) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display");
                placeRequestImpl.addParameter("processId", process.getId());
                placeRequestImpl.addParameter("domainId", process.getDomainId());
                placeManager.goTo(placeRequestImpl);
            }
        }));

        cells.add(new DetailsActionHasCell("Details", new Delegate<ProcessSummary>() {
            @Override
            public void execute(ProcessSummary process) {

                PlaceRequest placeRequestImpl = new DefaultPlaceRequest(constants.Process_Definition_Details());
                placeRequestImpl.addParameter("processId", process.getId());
                placeRequestImpl.addParameter("domainId", process.getDomainId());
                placeManager.goTo(placeRequestImpl);
            }
        }));

        CompositeCell<ProcessSummary> cell = new CompositeCell<ProcessSummary>(cells);
      Column<ProcessSummary, ProcessSummary> actionsColumn = new Column<ProcessSummary, ProcessSummary>(cell) {
                                                        @Override
                                                        public ProcessSummary getValue(ProcessSummary object) {
                                                            return object;
                                                        }
                                                    };
        processdefListGrid.addColumn(actionsColumn, "Actions");
        processdefListGrid.setColumnWidth(actionsColumn, "70px");
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
    
    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    private class StartActionHasCell implements HasCell<ProcessSummary, ProcessSummary> {

        private ActionCell<ProcessSummary> cell;

        public StartActionHasCell(String text, Delegate<ProcessSummary> delegate) {
            cell = new ActionCell<ProcessSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, ProcessSummary value, SafeHtmlBuilder sb) {

                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.startIcon());
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='Start'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };
        }

        @Override
        public Cell<ProcessSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessSummary, ProcessSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessSummary getValue(ProcessSummary object) {
            return object;
        }
    }

    private class DetailsActionHasCell implements HasCell<ProcessSummary, ProcessSummary> {

        private ActionCell<ProcessSummary> cell;

        public DetailsActionHasCell(String text, Delegate<ProcessSummary> delegate) {
            cell = new ActionCell<ProcessSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, ProcessSummary value, SafeHtmlBuilder sb) {

                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.detailsIcon());
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='Details'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };
        }

        @Override
        public Cell<ProcessSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessSummary, ProcessSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessSummary getValue(ProcessSummary object) {
            return object;
        }
    }
}
