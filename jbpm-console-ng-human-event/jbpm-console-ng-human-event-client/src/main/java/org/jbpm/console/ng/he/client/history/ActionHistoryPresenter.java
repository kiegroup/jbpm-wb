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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.he.client.i8n.Constants;
import org.jbpm.console.ng.he.model.PointHistory;
import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

@Dependent
@WorkbenchScreen(identifier = "Actions Histories")
public class ActionHistoryPresenter {

	@Inject
	private ActionHistoryView view;

	@Inject
	private Identity identity;
	@Inject
	private Caller<TaskServiceEntryPoint> taskServices;

	private List<TaskSummary> allTaskSummaries;

	private Map<Day, List<TaskSummary>> currentDayTasks;

	private ListDataProvider<TaskSummary> dataProvider = new ListDataProvider<TaskSummary>();

	public enum HumanEventType {
		PERSONAL, ACTIVE, GROUP, ALL
	}

	public enum HumanEventView {
		DAY, WEEK, MONTH, GRID
	}

	public List<TaskSummary> getAllTaskSummaries() {
		return allTaskSummaries;
	}

	public interface ActionHistoryView extends UberView<ActionHistoryPresenter> {

		void displayNotification(String text);

		TaskListMultiDayBox getTaskListMultiDayBox();

		MultiSelectionModel<TaskSummary> getSelectionModel();

		// TextBox getSearchBox();

		void refreshHumanEvents();
	}

	@WorkbenchPartView
	public UberView<ActionHistoryPresenter> getView() {
		return view;
	}

	private Constants constants = GWT.create(Constants.class);

	@WorkbenchPartTitle
	public String getTitle() {
		return constants.Tasks_List();
	}

	public void saveHistory(@Observes @History PointHistory pointHistory) {
		updateHistory(pointHistory);
	}

	public enum EventType {
		HISTORY
	}

	private void updateHistory(PointHistory pointHistory) {
		/*
		 * if(actionHistory.getPoints()==null){ actionHistory.setPoints(new
		 * LinkedList<PointHistory>()); }
		 * actionHistory.getPoints().add(pointHistory);
		 */

		/*
		 * SessionContext sessionEvent =
		 * SessionContext.get(actionHistory.getPoints()); Queue<PointHistory>
		 * points = sessionEvent.getAttribute(LinkedList.class,
		 * EventType.HISTORY); if(points==null){ points = new
		 * LinkedList<PointHistory>(); }else{ points.add(pointHistory); }
		 * sessionEvent.setAttribute(EventType.HISTORY, points);
		 */
	}

	public void refreshEvents(Date date, HumanEventView eventView,
			HumanEventType eventType) {
		refreshHumanEvent(date, eventView);
		switch (eventType) {
		case ACTIVE:
			refreshHumanEvent(date, eventView);
			break;
		/*
		 * case ACTIVE: refreshActiveTasks(date, taskView); break; case GROUP:
		 * refreshGroupTasks(date, taskView); break; case ALL:
		 * refreshAllTasks(date, taskView); break;
		 */
		default:
			throw new IllegalStateException("Unrecognized event type '"
					+ eventType + "'!");
		}
	}

	public void refreshHumanEvent(Date date, HumanEventView taskView) {
		Date fromDate = new Date(); // determineFirstDateForEventViewBasedOnSpecifiedDate(date,
									// EventView);
		int daysTotal = 1; // determineNumberOfDaysForEventView(EventView);

		List<String> statuses = new ArrayList<String>(4);
		statuses.add("Ready");
		statuses.add("InProgress");
		statuses.add("Created");
		statuses.add("Reserved");
		/*
		 * if (EventView.equals(EventView.GRID)) { taskServices.call(new
		 * RemoteCallback<List<TaskSummary>>() {
		 * 
		 * @Override public void callback(List<TaskSummary> tasks) {
		 * allTaskSummaries = tasks; //
		 * filterTasks(view.getSearchBox().getText()); }
		 * }).getTasksOwnedByExpirationDateOptional(identity.getName(),
		 * statuses, fromDate, "en-UK");
		 * 
		 * } else {
		 */
		taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
			@Override
			public void callback(Map<Day, List<TaskSummary>> tasks) {
				currentDayTasks = tasks;
				// filterTasks(view.getSearchBox().getText());
			}
		}).getTasksOwnedFromDateToDateByDays(identity.getName(), statuses,
				fromDate, daysTotal, "en-UK");
		/* } */
	}

	public void filterTasks(String text) {
		if (text.equals("")) {
			if (allTaskSummaries != null) {
				dataProvider.getList().clear();
				dataProvider.setList(new ArrayList<TaskSummary>(
						allTaskSummaries));
				dataProvider.refresh();

			}
			if (currentDayTasks != null) {
				view.getTaskListMultiDayBox().clear();
				for (Day day : currentDayTasks.keySet()) {
					view.getTaskListMultiDayBox()
							.addTasksByDay(
									day,
									new ArrayList<TaskSummary>(currentDayTasks
											.get(day)));
				}
				view.getTaskListMultiDayBox().refresh();
			}
		} else {
			if (allTaskSummaries != null) {
				List<TaskSummary> tasks = new ArrayList<TaskSummary>(
						allTaskSummaries);
				List<TaskSummary> filteredTasksSimple = new ArrayList<TaskSummary>();
				for (TaskSummary ts : tasks) {
					if (ts.getName().toLowerCase().contains(text.toLowerCase())) {
						filteredTasksSimple.add(ts);
					}
				}
				dataProvider.getList().clear();
				dataProvider.setList(filteredTasksSimple);
				dataProvider.refresh();
			}
			if (currentDayTasks != null) {
				Map<Day, List<TaskSummary>> tasksCalendar = new HashMap<Day, List<TaskSummary>>(
						currentDayTasks);
				Map<Day, List<TaskSummary>> filteredTasksCalendar = new HashMap<Day, List<TaskSummary>>();
				view.getTaskListMultiDayBox().clear();
				for (Day d : tasksCalendar.keySet()) {
					if (filteredTasksCalendar.get(d) == null) {
						filteredTasksCalendar.put(d,
								new ArrayList<TaskSummary>());
					}
					for (TaskSummary ts : tasksCalendar.get(d)) {
						if (ts.getName().toLowerCase()
								.contains(text.toLowerCase())) {
							filteredTasksCalendar.get(d).add(ts);
						}
					}
				}
				for (Day day : filteredTasksCalendar.keySet()) {
					view.getTaskListMultiDayBox().addTasksByDay(
							day,
							new ArrayList<TaskSummary>(filteredTasksCalendar
									.get(day)));
				}
				view.getTaskListMultiDayBox().refresh();
			}
		}

	}

	public void claimTasks(List<Long> selectedTasks, final String userId) {
		taskServices.call(new RemoteCallback<List<TaskSummary>>() {
			@Override
			public void callback(List<TaskSummary> tasks) {
				view.displayNotification("Task(s) Claimed");
				view.refreshHumanEvents();

			}
		}).claimBatch(selectedTasks, userId);
	}

	public void releaseTasks(List<Long> selectedTasks, final String userId) {
		taskServices.call(new RemoteCallback<List<TaskSummary>>() {
			@Override
			public void callback(List<TaskSummary> tasks) {
				view.displayNotification("Task(s) Released");
				view.refreshHumanEvents();
			}
		}).releaseBatch(selectedTasks, userId);
	}

	public void addDataDisplay(HasData<TaskSummary> display) {
		dataProvider.addDataDisplay(display);
	}

}
