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
package org.jbpm.console.ng.bd.client.editors.session.list;

import com.github.gwtbootstrap.client.ui.Button;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.cell.client.ActionCell;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.security.Identity;

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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.client.resources.ShowcaseImages;
import org.jbpm.console.ng.bd.client.util.ResizableHeader;
import org.jbpm.console.ng.bd.model.events.KieSessionSelectionEvent;

@Dependent
@Templated(value = "KieSessionsListViewImpl.html")
public class KieSessionsListViewImpl extends Composite
        implements
        KieSessionsListPresenter.KieSessionsListView {

    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    private KieSessionsListPresenter presenter;
    @Inject
    @DataField
    public TextBox groupText;
    
    @Inject
    @DataField
    public TextBox artifactText;
   
    @Inject
    @DataField
    public TextBox versionText;
    
    @Inject
    @DataField
    public TextBox kbaseNameText;
    
    @Inject
    @DataField
    public TextBox kieSessionNameText;
    
    @Inject
    @DataField
    public Button newSessionButton;
    

    @Inject
    @DataField
    public DataGrid<String> ksessionsListGrid;

    @Inject
    @DataField 
    public FlowPanel listContainerKsessions;

    @Inject
    @DataField
    public SimplePager pagerKsessions;
    
    private Set<String> selectedKieSession;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<KieSessionSelectionEvent> kieSessionSelection;
    private ListHandler<String> sortHandler;
    
    private Constants constants = GWT.create(Constants.class);
    private ShowcaseImages images = GWT.create(ShowcaseImages.class);

    @Override
    public void init(KieSessionsListPresenter presenter) {
        this.presenter = presenter;

       
        listContainerKsessions.add(ksessionsListGrid);
        listContainerKsessions.add(pagerKsessions);
        
        ksessionsListGrid.setHeight("350px");
        //         Set the message to display when the table is empty.
        Label emptyTable = new Label(constants.No_Deployment_Units_Available());
        emptyTable.setStyleName("");
        ksessionsListGrid.setEmptyTableWidget(emptyTable);

//         Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler =
                new ListHandler<String>(presenter.getDataProvider().getList());
        ksessionsListGrid.addColumnSortHandler(sortHandler);

        // Create a Pager to control the table.

        pagerKsessions.setDisplay(ksessionsListGrid);
        pagerKsessions.setPageSize(10);

        // Add a selection model so we can select cells.
        final MultiSelectionModel<String> selectionModel =
                new MultiSelectionModel<String>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedKieSession = selectionModel.getSelectedSet();
                for (String kieSession : selectedKieSession) {
                    
                }
            }
        });

        ksessionsListGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager
                .<String>createCheckboxManager());

        initTableColumns(selectionModel);



        presenter.addDataDisplay(ksessionsListGrid);

    }

    @EventHandler("newSessionButton")
    public void newSessionButton(ClickEvent e) {
        presenter.newKieSessionButton(groupText.getText(), artifactText.getText(), 
                      versionText.getText(), kbaseNameText.getText(), kieSessionNameText.getText());
    }
    
    

    private void initTableColumns(final SelectionModel<String> selectionModel) {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

        Column<String, Boolean> checkColumn =
                new Column<String, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(String object) {
                // Get the value from the selection model.
                return selectionModel.isSelected(object);
            }
        };
        
        ksessionsListGrid.addColumn(checkColumn,
                SafeHtmlUtils.fromSafeConstant("<br/>"));
        ksessionsListGrid.setColumnWidth(checkColumn, "40px");

        // Id.
        Column<String, String> sessionIdColumn =
                new Column<String, String>(new TextCell()) {
            @Override
            public String getValue(String object) {
                return object;
            }
        };
        sessionIdColumn.setSortable(true);
        sortHandler.setComparator(sessionIdColumn,
                new Comparator<String>() {
            @Override
            public int compare(String o1,
                    String o2) {
                return o1.compareTo(o2);
            }
        });
        ksessionsListGrid.addColumn(sessionIdColumn,
                new ResizableHeader("Id", ksessionsListGrid, sessionIdColumn));

        // actions (icons)
        List<HasCell<String, ?>> cells = new LinkedList<HasCell<String, ?>>();

        cells.add(new DeleteActionHasCell("Delete Kie Session", new Delegate<String>() {
            @Override
            public void execute(String session) {
//                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display");
//                System.out.println("Opening form for process id = "+process.getId());
//                placeRequestImpl.addParameter("processId", process.getId());
//                placeRequestImpl.addParameter("sessionId", String.valueOf(process.getSessionId()));
//                placeManager.goTo(placeRequestImpl);
                displayNotification("Session "+session+ "needs to be deleted here!!");
            }
        }));

        cells.add(new DetailsActionHasCell("Details", new Delegate<String>() {
            @Override
            public void execute(String session) {

//                PlaceRequest placeRequestImpl = new DefaultPlaceRequest(constants.Process_Definition_Details());
//                placeRequestImpl.addParameter("processId", process.getId());
//                placeRequestImpl.addParameter("sessionId", Integer.toString(process.getSessionId()));
//                placeManager.goTo(placeRequestImpl);
              displayNotification("Session "+session+ " go to details here!!");
            }
        }));

        CompositeCell<String> cell = new CompositeCell<String>(cells);
        Column<String, String> actionsColumn = new Column<String, String>(cell) {
                                                        @Override
                                                        public String getValue(String object) {
                                                            return object;
                                                        }
                                                    };
        ksessionsListGrid.addColumn(actionsColumn, "Actions");
        ksessionsListGrid.setColumnWidth(actionsColumn, "70px");
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public DataGrid<String> getDataGrid() {
        return ksessionsListGrid;
    }

    public ListHandler<String> getSortHandler() {
        return sortHandler;
    }

    

    private class DeleteActionHasCell implements HasCell<String, String> {

        private ActionCell<String> cell;

        public DeleteActionHasCell(String text, Delegate<String> delegate) {
            cell = new ActionCell<String>(text, delegate) {
                @Override
                public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {

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
        public Cell<String> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<String, String> getFieldUpdater() {
            return null;
        }

        @Override
        public String getValue(String object) {
            return object;
        }
    }

    private class DetailsActionHasCell implements HasCell<String, String> {

        private ActionCell<String> cell;

        public DetailsActionHasCell(String text, Delegate<String> delegate) {
            cell = new ActionCell<String>(text, delegate) {
                @Override
                public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {

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
        public Cell<String> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<String, String> getFieldUpdater() {
            return null;
        }

        @Override
        public String getValue(String object) {
            return object;
        }
    }
}
