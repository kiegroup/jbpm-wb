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
package org.jbpm.console.ng.dm.client.experimental.pagination;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.dm.model.CMSContentSummary;
import org.jbpm.console.ng.dm.model.events.DocumentRemoveSearchEvent;
<<<<<<< HEAD
import org.jbpm.console.ng.dm.model.events.DocumentsHomeSearchEvent;
=======
>>>>>>> 8263f9d4750445cdeebbdf2df5e28ac67f8f1e91
import org.jbpm.console.ng.dm.model.events.DocumentsListSearchEvent;
import org.jbpm.console.ng.dm.model.events.DocumentsParentSearchEvent;
import org.kie.uberfire.client.common.BusyPopup;
import org.kie.uberfire.client.tables.ResizableHeader;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;

@Dependent
@Templated(value = "DocumentListViewImpl.html")
public class DocumentListViewImpl extends ActionsCellDocuments implements
		DocumentListPresenter.DocumentListView, RequiresResize {

	@Inject
	private Identity identity;

	@Inject
	private PlaceManager placeManager;

	private DocumentListPresenter presenter;

	private String currentFilter = "";

	private Constants constants = GWT.create(Constants.class);

	@Inject
	@DataField
	public DataGrid<CMSContentSummary> processdefListGrid;

	@Inject
	@DataField
	public LayoutPanel listContainer;

	@Inject
	@DataField
	public NavLink parentLink;

	@Inject
	@DataField
	public Anchor pathLink;

	@Inject
	@DataField
	public NavLink homeLink;

	@Inject
	@DataField
	public NavLink newLink;

	@DataField
	public SimplePager pager;

	@Inject
	private Event<DocumentsListSearchEvent> selectDocEvent;

	@Inject
	private Event<DocumentsParentSearchEvent> parentDocEvent;
<<<<<<< HEAD
	
	@Inject
	private Event<DocumentsHomeSearchEvent> homeDocEvent;
=======
>>>>>>> 8263f9d4750445cdeebbdf2df5e28ac67f8f1e91

	@Inject
	private Event<DocumentRemoveSearchEvent> removeDocEvent;

	@Inject
	private Event<NotificationEvent> notification;

	private ListHandler<CMSContentSummary> sortHandler;

	public DocumentListViewImpl() {
		pager = new SimplePager(SimplePager.TextLocation.LEFT, false, true);
	}

	@Override
	public String getCurrentFilter() {
		return currentFilter;
	}

	@Override
	public void setCurrentFilter(String currentFilter) {
		this.currentFilter = currentFilter;
	}

	@Override
	public void init(final DocumentListPresenter presenter) {
		this.presenter = presenter;
		this.pathLink.setText("/");
		listContainer.add(processdefListGrid);
		pager.setDisplay(processdefListGrid);
		pager.setPageSize(10);

		// Set the message to display when the table is empty.
		Label emptyTable = new Label("Empty");
		emptyTable.setStyleName("");
		processdefListGrid.setEmptyTableWidget(emptyTable);

		parentLink.setText("Parent");
		parentLink.setStyleName("");
		parentLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				parentLink.setStyleName("active");

				if (BrowserEvents.CLICK.equalsIgnoreCase(event.getNativeEvent()
						.getType())) {
					parentDocEvent.fire(new DocumentsParentSearchEvent());
					parentLink.setStyleName("");
					pathLink.setText(presenter.currentCMSContentSummary
							.getParent().getPath());
				}
			}
		});

		homeLink.setText("Home");
		homeLink.setStyleName("");
		homeLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
<<<<<<< HEAD
				parentLink.setStyleName("active");

				if (BrowserEvents.CLICK.equalsIgnoreCase(event.getNativeEvent()
						.getType())) {
					parentDocEvent.fire(new DocumentsParentSearchEvent());
					parentLink.setStyleName("");
				}
=======
				// TODO
>>>>>>> 8263f9d4750445cdeebbdf2df5e28ac67f8f1e91
			}
		});

		newLink.setText("New");
		newLink.setStyleName("");
		newLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DefaultPlaceRequest req = new DefaultPlaceRequest(
						"New Document");
				String folder = (presenter.currentCMSContentSummary == null) ? "/"
						: presenter.currentCMSContentSummary	
								.getPath();
				req.addParameter("folder", folder);
				placeManager.goTo(req);
			}
		});

		// Attach a column sort handler to the ListDataProvider to sort the
		// list.
		sortHandler = new ListHandler<CMSContentSummary>(presenter
				.getDataProvider().getList());
		processdefListGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		final MultiSelectionModel<CMSContentSummary> selectionModel = new MultiSelectionModel<CMSContentSummary>();
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						Set<CMSContentSummary> selectedProcessDef = selectionModel
								.getSelectedSet();
						for (CMSContentSummary pd : selectedProcessDef) {
							selectDocEvent
									.fire(new DocumentsListSearchEvent(pd));
						}
					}
				});

		processdefListGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager
						.<CMSContentSummary> createCheckboxManager());

		initTableColumns(selectionModel);

		presenter.addDataDisplay(processdefListGrid);

	}

	private void initTableColumns(
			final SelectionModel<CMSContentSummary> selectionModel) {

		processdefListGrid
				.addCellPreviewHandler(new CellPreviewEvent.Handler<CMSContentSummary>() {

					@Override
					public void onCellPreview(
							final CellPreviewEvent<CMSContentSummary> event) {
						CMSContentSummary summary = event.getValue();

						if (BrowserEvents.DBLCLICK.equalsIgnoreCase(event
								.getNativeEvent().getType())) {
							selectDocEvent.fire(new DocumentsListSearchEvent(
									summary));
							pathLink.setText(summary.getPath());
						}
					}
				});

		// Name String.
		Column<CMSContentSummary, String> processNameColumn = new Column<CMSContentSummary, String>(
				new TextCell()) {
			@Override
			public String getValue(CMSContentSummary object) {
				return object.getName();
			}
		};
		processNameColumn.setSortable(true);
		sortHandler.setComparator(processNameColumn,
				new Comparator<CMSContentSummary>() {
					@Override
					public int compare(CMSContentSummary o1,
							CMSContentSummary o2) {
						return o1.getName().toLowerCase()
								.compareTo(o2.getName().toLowerCase());
					}
				});

		processdefListGrid.addColumn(processNameColumn, new ResizableHeader<CMSContentSummary>(
				"Name", processdefListGrid, processNameColumn));

		// Version Type
		Column<CMSContentSummary, String> idColumn = new Column<CMSContentSummary, String>(
				new TextCell()) {
			@Override
			public String getValue(CMSContentSummary object) {
				return object.getId();
			}
		};

		processdefListGrid.addColumn(idColumn, new ResizableHeader("ID",
				processdefListGrid, idColumn));

		// actions (icons)
		List<HasCell<CMSContentSummary, ?>> cells = new LinkedList<HasCell<CMSContentSummary, ?>>();

		cells.add(new GoHasCell("Go", new Delegate<CMSContentSummary>() {
			@Override
			public void execute(CMSContentSummary process) {
				selectDocEvent.fire(new DocumentsListSearchEvent(process));
				pathLink.setText(process.getPath());
			}
		}));

		cells.add(new RemoveHasCell("Remove",
				new Delegate<CMSContentSummary>() {
					@Override
					public void execute(CMSContentSummary process) {
						removeDocEvent.fire(new DocumentRemoveSearchEvent(
								process));
					}
				}));

		CompositeCell<CMSContentSummary> cell = new CompositeCell<CMSContentSummary>(
				cells);
		Column<CMSContentSummary, CMSContentSummary> actionsColumn = new Column<CMSContentSummary, CMSContentSummary>(
				cell) {
			@Override
			public CMSContentSummary getValue(CMSContentSummary object) {
				return object;
			}
		};
		processdefListGrid.addColumn(actionsColumn, new ResizableHeader(
				"Actions", processdefListGrid, actionsColumn));
		processdefListGrid.setColumnWidth(actionsColumn, "70px");
	}

	@Override
	public void onResize() {
		if ((getParent().getOffsetHeight() - 120) > 0) {
			listContainer.setHeight(getParent().getOffsetHeight() - 120 + "px");
		}
	}

	//
	// public void changeRowSelected(@Observes ProcessDefStyleEvent
	// processDefStyleEvent) {
	// if (processDefStyleEvent.getProcessDefName() != null) {
	// DataGridUtils.paintRowSelected(processdefListGrid,
	// processDefStyleEvent.getProcessDefName(),
	// processDefStyleEvent.getProcessDefVersion());
	// }
	// }

	@Override
	public void displayNotification(String text) {
		notification.fire(new NotificationEvent(text));
	}

	@Override
	public DataGrid<CMSContentSummary> getDataGrid() {
		return processdefListGrid;
	}

	public ListHandler<CMSContentSummary> getSortHandler() {
		return sortHandler;
	}

	@Override
	public void showBusyIndicator(final String message) {
		BusyPopup.showMessage(message);
	}

	@Override
	public void hideBusyIndicator() {
		BusyPopup.close();
	}

<<<<<<< HEAD
	@Override
	public void updatePathLink() {
		String path = presenter.currentCMSContentSummary.getPath();
		if (path != null && !path.equals("")) { 
			pathLink.setText(path);
		} else
		{
			pathLink.setText("/");
		}
	}

=======
>>>>>>> 8263f9d4750445cdeebbdf2df5e28ac67f8f1e91
}
