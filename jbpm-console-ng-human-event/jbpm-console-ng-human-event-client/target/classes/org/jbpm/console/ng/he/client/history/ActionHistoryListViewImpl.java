/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.console.ng.he.client.history;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.he.client.history.ActionHistoryPresenter.HumanEventType;
import org.jbpm.console.ng.he.client.history.ActionHistoryPresenter.HumanEventView;
import org.jbpm.console.ng.he.client.i8n.Constants;
import org.jbpm.console.ng.he.client.util.ResizableHeader;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;

@Dependent
@Templated(value = "ActionHistoryListViewImpl.html")
public class ActionHistoryListViewImpl extends Composite implements
		ActionHistoryPresenter.ActionHistoryView {
	private Constants constants = GWT.create(Constants.class);

	@Inject
	@DataField
	public NavLink showAllTasksNavLink;

	@Inject
	@DataField
	public NavLink showPersonalTasksNavLink;

	@Inject
	@DataField
	public NavLink showGroupTasksNavLink;

	@DataField
	public Heading taskCalendarViewLabel = new Heading(4);

	@Inject
	@DataField
	public FlowPanel tasksViewContainer;

	@Inject
	@DataField
	public IconAnchor refreshIcon;

	@Inject
	private Event<NotificationEvent> notification;

	private ActionHistoryPresenter presenter;

	private HumanEventView currentView = HumanEventView.DAY;

	public DataGrid<TaskSummary> myTaskListGrid;

	public SimplePager pager;
	private Set<TaskSummary> selectedTasks;
	private ListHandler<TaskSummary> sortHandler;
	private MultiSelectionModel<TaskSummary> selectionModel;
	@Inject
	private Event<TaskSelectionEvent> taskSelection;

	@Inject
	private TaskListMultiDayBox taskListMultiDayBox;

	private Date currentDate;
	private HumanEventType currentEventHumanType = HumanEventType.ACTIVE;

	@Override
	public TaskListMultiDayBox getTaskListMultiDayBox() {
		return taskListMultiDayBox;
	}

	@Override
	public void refreshHumanEvents() {
		presenter
				.refreshEvents(currentDate, currentView, currentEventHumanType);
	}

	@Override
	public void init(ActionHistoryPresenter presenter) {
		this.presenter = presenter;

		refreshIcon.setTitle(constants.Refresh());
		refreshIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refreshHumanEvents();
				displayNotification(constants.Tasks_Refreshed());
			}
		});

		taskListMultiDayBox.init();
		// taskListMultiDayBox.setPresenter( presenter );
		currentDate = new Date();

		// By Default we will start in Grid View
		initializeGridView();

		// Filters
		showPersonalTasksNavLink.setText(constants.Personal());
		showPersonalTasksNavLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showPersonalTasksNavLink.setStyleName("active");
				showGroupTasksNavLink.setStyleName("");
				showAllTasksNavLink.setStyleName("");
				currentEventHumanType = HumanEventType.PERSONAL;
				refreshHumanEvents();

			}
		});

		showGroupTasksNavLink.setText(constants.Group());
		showGroupTasksNavLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showGroupTasksNavLink.setStyleName("active");
				showPersonalTasksNavLink.setStyleName("");
				showAllTasksNavLink.setStyleName("");
				currentEventHumanType = HumanEventType.GROUP;
				refreshHumanEvents();

			}
		});

		showAllTasksNavLink.setText(constants.All());
		showAllTasksNavLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showGroupTasksNavLink.setStyleName("");
				showPersonalTasksNavLink.setStyleName("");
				showAllTasksNavLink.setStyleName("active");
				currentEventHumanType = HumanEventType.ALL;
				refreshHumanEvents();

			}
		});

		taskCalendarViewLabel
				.setText("Human Events" /* constants.Tasks_List() */);
		taskCalendarViewLabel.setStyleName("");

		refreshHumanEvents();

	}

	private void initializeGridView() {
		tasksViewContainer.clear();
		currentView = HumanEventView.GRID;
		myTaskListGrid = new DataGrid<TaskSummary>();
		myTaskListGrid
				.setStyleName("table table-bordered table-striped table-hover");
		pager = new SimplePager();
		pager.setStyleName("pagination pagination-right pull-right");
		pager.setDisplay(myTaskListGrid);
		pager.setPageSize(30);

		tasksViewContainer.add(myTaskListGrid);
		tasksViewContainer.add(pager);

		myTaskListGrid.setHeight("350px");
		// Set the message to display when the table is empty.
		myTaskListGrid.setEmptyTableWidget(new Label(constants
				.No_Pending_Tasks_Enjoy()));

		// Attach a column sort handler to the ListDataProvider to sort the
		// list.
		sortHandler = new ColumnSortEvent.ListHandler<TaskSummary>(
				presenter.getAllTaskSummaries());

		myTaskListGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		selectionModel = new MultiSelectionModel<TaskSummary>();
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						selectedTasks = selectionModel.getSelectedSet();
						for (TaskSummary ts : selectedTasks) {
							taskSelection.fire(new TaskSelectionEvent(ts
									.getId()));
						}
					}
				});

		myTaskListGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager
						.<TaskSummary> createCheckboxManager());

		initTableColumns(selectionModel);
		presenter.addDataDisplay(myTaskListGrid);

	}

	private void initTableColumns(
			final SelectionModel<TaskSummary> selectionModel) {
		// Checkbox column. This table will uses a checkbox column for
		// selection.
		// Alternatively, you can call dataGrid.setSelectionEnabled(true) to
		// enable
		// mouse selection.

		Column<TaskSummary, Boolean> checkColumn = new Column<TaskSummary, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(TaskSummary object) {
				// Get the value from the selection model.
				return selectionModel.isSelected(object);
			}
		};
		myTaskListGrid.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		myTaskListGrid.setColumnWidth(checkColumn, "40px");

		// Id
		Column<TaskSummary, Number> taskIdColumn = new Column<TaskSummary, Number>(
				new NumberCell()) {
			@Override
			public Number getValue(TaskSummary object) {
				return object.getId();
			}
		};
		taskIdColumn.setSortable(true);
		myTaskListGrid.setColumnWidth(taskIdColumn, "40px");

		myTaskListGrid.addColumn(taskIdColumn,
				new ResizableHeader(constants.Id(), myTaskListGrid,
						taskIdColumn));
		sortHandler.setComparator(taskIdColumn, new Comparator<TaskSummary>() {
			@Override
			public int compare(TaskSummary o1, TaskSummary o2) {
				return Long.valueOf(o1.getId()).compareTo(
						Long.valueOf(o2.getId()));
			}
		});

		// Human event.
		Column<TaskSummary, String> taskNameColumn = new Column<TaskSummary, String>(
				new TextCell()) {
			@Override
			public String getValue(TaskSummary object) {
				return object.getName();
			}
		};
		taskNameColumn.setSortable(true);

		myTaskListGrid.addColumn(taskNameColumn, new ResizableHeader(
				"Human Event"/* constants.Task() */, myTaskListGrid,
				taskNameColumn));
		sortHandler.setComparator(taskNameColumn,
				new Comparator<TaskSummary>() {
					@Override
					public int compare(TaskSummary o1, TaskSummary o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});

		// Task priority.
		/*
		 * Column<TaskSummary, Number> taskPriorityColumn = new
		 * Column<TaskSummary, Number>(new NumberCell()) {
		 * 
		 * @Override public Number getValue(TaskSummary object) { return
		 * object.getPriority(); } }; taskPriorityColumn.setSortable(true);
		 * myTaskListGrid.addColumn(taskPriorityColumn, new
		 * ResizableHeader(constants.Priority(), myTaskListGrid,
		 * taskPriorityColumn));
		 * myTaskListGrid.setColumnWidth(taskPriorityColumn, "100px");
		 * sortHandler.setComparator(taskPriorityColumn, new
		 * Comparator<TaskSummary>() {
		 * 
		 * @Override public int compare(TaskSummary o1, TaskSummary o2) { return
		 * Integer.valueOf(o1.getPriority()).compareTo(o2.getPriority()); } });
		 */

		// Status.
		/*
		 * Column<TaskSummary, String> statusColumn = new Column<TaskSummary,
		 * String>(new TextCell()) {
		 * 
		 * @Override public String getValue(TaskSummary object) { return
		 * object.getStatus(); } }; statusColumn.setSortable(true);
		 * 
		 * myTaskListGrid.addColumn(statusColumn, new
		 * ResizableHeader(constants.Status(), myTaskListGrid, statusColumn));
		 * sortHandler.setComparator(statusColumn, new Comparator<TaskSummary>()
		 * {
		 * 
		 * @Override public int compare(TaskSummary o1, TaskSummary o2) { return
		 * o1.getStatus().compareTo(o2.getStatus()); } });
		 * myTaskListGrid.setColumnWidth(statusColumn, "100px");
		 */

		// Due Date.
		Column<TaskSummary, String> dueDateColumn = new Column<TaskSummary, String>(
				new TextCell()) {
			@Override
			public String getValue(TaskSummary object) {
				if (object.getExpirationTime() != null) {
					return object.getExpirationTime().toString();
				}
				return "";
			}
		};
		dueDateColumn.setSortable(true);

		myTaskListGrid.addColumn(dueDateColumn,
				new ResizableHeader("Time" /* constants.Due_On() */,
						myTaskListGrid, dueDateColumn));
		sortHandler.setComparator(dueDateColumn, new Comparator<TaskSummary>() {
			@Override
			public int compare(TaskSummary o1, TaskSummary o2) {
				if (o1.getExpirationTime() == null
						|| o2.getExpirationTime() == null) {
					return 0;
				}
				return o1.getExpirationTime().compareTo(o2.getExpirationTime());
			}
		});

		List<HasCell<TaskSummary, ?>> cells = new LinkedList<HasCell<TaskSummary, ?>>();
		/*
		 * cells.add(new StartActionHasCell("Start", new
		 * ActionCell.Delegate<TaskSummary>() {
		 * 
		 * @Override public void execute(TaskSummary task) { List<Long> tasks =
		 * new ArrayList<Long>(1); tasks.add(task.getId());
		 * presenter.startTasks(tasks, identity.getName()); } }));
		 */

		/*
		 * cells.add(new CompleteActionHasCell("Complete", new
		 * ActionCell.Delegate<TaskSummary>() {
		 * 
		 * @Override public void execute(TaskSummary task) { List<Long> tasks =
		 * new ArrayList<Long>(1); tasks.add(task.getId());
		 * presenter.completeTasks(tasks, identity.getName()); } }));
		 */

		/*
		 * cells.add(new ClaimActionHasCell("Claim", new
		 * ActionCell.Delegate<TaskSummary>() {
		 * 
		 * @Override public void execute(TaskSummary task) { List<Long> tasks =
		 * new ArrayList<Long>(1); tasks.add(task.getId());
		 * presenter.claimTasks(tasks, identity.getName()); } }));
		 */

		/*
		 * cells.add(new ReleaseActionHasCell("Release", new
		 * ActionCell.Delegate<TaskSummary>() {
		 * 
		 * @Override public void execute(TaskSummary task) { List<Long> tasks =
		 * new ArrayList<Long>(1); tasks.add(task.getId());
		 * presenter.releaseTasks(tasks, identity.getName()); } }));
		 */

		/*
		 * cells.add(new DetailsHasCell("Edit", new
		 * ActionCell.Delegate<TaskSummary>() {
		 * 
		 * @Override public void execute(TaskSummary task) { PlaceRequest
		 * placeRequestImpl = new DefaultPlaceRequest("Task Details Popup");
		 * placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));
		 * placeManager.goTo(placeRequestImpl); } }));
		 */

		/*
		 * cells.add(new PopupActionHasCell("Work Popup", new
		 * ActionCell.Delegate<TaskSummary>() {
		 * 
		 * @Override public void execute(TaskSummary task) { PlaceRequest
		 * placeRequestImpl = new DefaultPlaceRequest("Form Display");
		 * placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));
		 * 
		 * placeManager.goTo(placeRequestImpl); } }));
		 */

		CompositeCell<TaskSummary> cell = new CompositeCell<TaskSummary>(cells);
		Column<TaskSummary, TaskSummary> actionsColumn = new Column<TaskSummary, TaskSummary>(
				cell) {
			@Override
			public TaskSummary getValue(TaskSummary object) {
				return object;
			}
		};
		myTaskListGrid.addColumn(actionsColumn, constants.Actions());

	}

	@Override
	public void displayNotification(String text) {
		notification.fire(new NotificationEvent(text));
	}

	@Override
	public MultiSelectionModel<TaskSummary> getSelectionModel() {
		return selectionModel;
	}

}
