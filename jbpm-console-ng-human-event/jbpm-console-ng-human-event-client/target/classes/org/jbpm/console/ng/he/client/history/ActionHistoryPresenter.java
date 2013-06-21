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
@WorkbenchScreen(identifier = "Actions Histories")
public class ActionHistoryPresenter {

    private List<HumanEventSummary> allEventsSummaries;

    private ListDataProvider<HumanEventSummary> dataProvider = new ListDataProvider<HumanEventSummary>();

    public enum HumanEventType {
        PERSONAL, ACTIVE, GROUP, ALL
    }

    public enum EventType {
        HISTORY
    }

    public List<HumanEventSummary> getAllEventsSummaries() {
        return allEventsSummaries;
    }

    public interface ActionHistoryView extends UberView<ActionHistoryPresenter> {

        void displayNotification(String text);

        MultiSelectionModel<HumanEventSummary> getSelectionModel();

        TextBox getSearchBox();

        void refreshHumanEvents();
    }

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

    private Constants constants = GWT.create(Constants.class);

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.List_Human_Event();
    }

    public void saveNewEventHistory(@Observes HumanEventSummary pointHistory) {
        humanEventServices.call(new RemoteCallback<Queue<HumanEventSummary>>() {
            @Override
            public void callback(Queue<HumanEventSummary> events) {
                allEventsSummaries = pasarAListProvisorio(events);
            }
        }).saveNewHumanEvent(pointHistory);
    }

    public void refreshEvents(Date date, HumanEventType eventType) {
        switch (eventType) {
        case ACTIVE:
            refreshHumanEvent(date);
            break;
        case PERSONAL:
            refreshHumanEvent(date);
            break;
        case GROUP:
            refreshHumanEvent(date);
            break;
        case ALL:
            refreshHumanEvent(date);
            break;
        default:
            throw new IllegalStateException("Unrecognized event type '" + eventType + "'!");
        }
    }

    public void refreshHumanEvent(Date date) {
        humanEventServices.call(new RemoteCallback<Queue<HumanEventSummary>>() {
            @Override
            public void callback(Queue<HumanEventSummary> events) {
                allEventsSummaries = pasarAListProvisorio(events);
                filterTasks(view.getSearchBox().getText());
            }
        }).getAllHumanEvent();
    }

    private List<HumanEventSummary> pasarAListProvisorio(Queue<HumanEventSummary> events) {
        List<HumanEventSummary> eventsList = new ArrayList<HumanEventSummary>();
        for (HumanEventSummary ev : events) {
            eventsList.add(ev);
        }
        return eventsList;
    }

    public void filterTasks(String text) {
        if (text.equals("")) {
            if (allEventsSummaries != null) {
                dataProvider.getList().clear();
                dataProvider.setList(new ArrayList<HumanEventSummary>(allEventsSummaries));
                dataProvider.refresh();

            }
        } else {
            if (allEventsSummaries != null) {
                List<HumanEventSummary> tasks = new ArrayList<HumanEventSummary>(allEventsSummaries);
                List<HumanEventSummary> filteredTasksSimple = new ArrayList<HumanEventSummary>();
                for (HumanEventSummary ts : tasks) {
                    if (ts.getDescriptionEvent().toLowerCase().contains(text.toLowerCase())) {
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

}
