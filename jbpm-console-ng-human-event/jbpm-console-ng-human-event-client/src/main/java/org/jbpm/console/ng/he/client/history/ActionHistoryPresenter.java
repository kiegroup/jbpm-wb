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
import java.util.List;
import java.util.Queue;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.he.client.i8n.Constants;
import org.jbpm.console.ng.he.model.HumanEventSummary;
import org.jbpm.console.ng.he.service.EventServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

@Dependent
@WorkbenchScreen(identifier = "Human Events")
public class ActionHistoryPresenter {
    
	private Constants constants = GWT.create(Constants.class);

	@Inject
	private ActionHistoryView view;

	@Inject
	private Identity identity;

	@Inject
	private Caller<EventServiceEntryPoint> humanEventServices;
	

	@WorkbenchPartView
	public UberView<ActionHistoryPresenter> getView() {
		return view;
	}

	@WorkbenchPartTitle
	public String getTitle() {
		return constants.List_Human_Event();
	}

	public enum HumanEventType {
		PERSONAL, ACTIVE, GROUP, ALL, EXPORT
	}

	private List<HumanEventSummary> allEventsSummaries;

	private ListDataProvider<HumanEventSummary> dataProvider = new ListDataProvider<HumanEventSummary>();

	public interface ActionHistoryView extends UberView<ActionHistoryPresenter> {

		void displayNotification(String text);

		MultiSelectionModel<HumanEventSummary> getSelectionModel();

		TextBox getSearchBox();

		void refreshHumanEvents();
	}

    public void saveNewEventHistory(@Observes HumanEventSummary pointHistory) {
		humanEventServices.call(new RemoteCallback<Queue<HumanEventSummary>>() {
			@Override
			public void callback(Queue<HumanEventSummary> events) {
				allEventsSummaries = new ArrayList<HumanEventSummary>(events);
			}
		}).saveNewHumanEvent(pointHistory);
	}
	
	public List<HumanEventSummary> getAllEventsSummaries() {
		return allEventsSummaries;
	}

	public void refreshEvents(Date date, HumanEventType eventType) {
		switch (eventType) {
		case ACTIVE:
			refreshHumanEvent();
			break;
		case PERSONAL:
			// TODO undefine
			refreshHumanEvent();
			break;
		case GROUP:
			// TODO undefine
			refreshHumanEvent();
			break;
		case ALL:
			// TODO undefine
			refreshHumanEvent();
			break;
		default:
			throw new IllegalStateException("Unrecognized event type '"
					+ eventType + "'!");
		}
	}

	public void refreshHumanEvent() {
		humanEventServices.call(new RemoteCallback<Queue<HumanEventSummary>>() {
			@Override
			public void callback(Queue<HumanEventSummary> events) {
				if(events!=null){
					allEventsSummaries = new ArrayList<HumanEventSummary>(events);
				}
				filterEvents(view.getSearchBox().getText());
			}
		}).getAllHumanEvent();
	}

	public void filterEvents(String text) {
		if (text.equals("")) {
			if (allEventsSummaries != null) {
				dataProvider.getList().clear();
				dataProvider.setList(new ArrayList<HumanEventSummary>(
						allEventsSummaries));
				dataProvider.refresh();

			}
		} else {
			if (allEventsSummaries != null) {
				List<HumanEventSummary> tasks = new ArrayList<HumanEventSummary>(
						allEventsSummaries);
				List<HumanEventSummary> filteredTasksSimple = new ArrayList<HumanEventSummary>();
				for (HumanEventSummary ts : tasks) {
					if (ts.getDescriptionEvent().toLowerCase()
							.contains(text.toLowerCase())) {
						filteredTasksSimple.add(ts);
					}
				}
				dataProvider.getList().clear();
				dataProvider.setList(filteredTasksSimple);
				dataProvider.refresh();
			}
		}

	}

	public void addDataDisplay(HasData<HumanEventSummary> display) {
		dataProvider.addDataDisplay(display);
	}

	public void exportToTxt() {
		/*
		 * File f;
		 * 
		 * //TODO sacar a un metodo SimpleDateFormat formato = new
		 * SimpleDateFormat("dd.MM.yyyy"); String fechaAc = formato.format(new
		 * Date()); f = new
		 * File("/tmp/archivo_"+fechaAc+"-"+identity.getName()+".txt");
		 * 
		 * 
		 * 
		 * try { FileWriter w = new FileWriter(f); BufferedWriter bw = new
		 * BufferedWriter(w); PrintWriter wr = new PrintWriter(bw);
		 * wr.write("Human Events user:" + identity.getName()); for
		 * (HumanEventSummary human : allEventsSummaries) {
		 * 
		 * wr.append(human.getDescriptionEvent() + " - " +
		 * human.getTypeEvent()); wr.append("/n"); } wr.close(); bw.close(); }
		 * catch (IOException e) {
		 * 
		 * }
		 */

	}
	
	

}
