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

package org.jbpm.console.ng.he.client.listevents;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.he.client.event.UserInteractionEvent;
import org.jbpm.console.ng.he.client.i8n.Constants;
import org.jbpm.console.ng.he.client.util.UtilEvent;
import org.jbpm.console.ng.he.model.UserInteractionSummary;
import org.jbpm.console.ng.he.service.EventServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

@Dependent
@WorkbenchScreen(identifier = "Human Events")
public class HumanEventPresenter {

    private Constants constants = GWT.create(Constants.class);

    @Inject
    private ActionHistoryView view;

    @Inject
    private Caller<EventServiceEntryPoint> humanEventServices;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    @WorkbenchPartView
    public UberView<HumanEventPresenter> getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.List_Human_Event();
    }

    private List<UserInteractionSummary> allEventsSummaries;

    private ListDataProvider<UserInteractionSummary> dataProvider = new ListDataProvider<UserInteractionSummary>();

    public interface ActionHistoryView extends UberView<HumanEventPresenter> {

        void displayNotification(String text);

        MultiSelectionModel<UserInteractionSummary> getSelectionModel();

        TextBox getSearchBox();

        void refreshHumanEvents();

        void clearHumanEvents();

        void showInfoEvents();

        void exportTxtEvents();
    }

    public void saveNewHumanEvent(@Observes UserInteractionEvent humaEvent) {
        humanEventServices.call(new RemoteCallback<Queue<UserInteractionSummary>>() {
            @Override
            public void callback(Queue<UserInteractionSummary> events) {
                allEventsSummaries = Lists.newArrayList(events);
            }
        }).saveNewHumanEvent(buildHumanEventSummary(humaEvent));
    }

    public List<UserInteractionSummary> getAllEventsSummaries() {
        return allEventsSummaries;
    }

    public void refreshHumanEvent() {
        humanEventServices.call(new RemoteCallback<Queue<UserInteractionSummary>>() {
            @Override
            public void callback(Queue<UserInteractionSummary> events) {
                if (events != null) {
                    allEventsSummaries = Lists.newArrayList(events);
                }
                filterEvents(view.getSearchBox().getText());
            }
        }).getAllHumanEvent();
    }

    public void clearHumanEvents() {
        humanEventServices.call(new RemoteCallback<Queue<UserInteractionSummary>>() {
            @Override
            public void callback(Queue<UserInteractionSummary> events) {
                // TODO ver de sacar el retorno ya que mi servicio es void
                allEventsSummaries = Lists.newArrayList();
                filterEvents(view.getSearchBox().getText());
                view.displayNotification(constants.Clear_Msj());
            }
        }).clearHumanEvent();
    }

    public void showInfoEvents() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Info Human Events");
        placeManager.goTo(placeRequestImpl);
    }

    public void filterEvents(String text) {
        if (text.equals("")) {
            if (allEventsSummaries != null) {
                dataProvider.getList().clear();
                dataProvider.setList(Lists.newArrayList(allEventsSummaries));
                dataProvider.refresh();
            }
        } else {
            if (allEventsSummaries != null) {
                List<UserInteractionSummary> tasks = Lists.newArrayList(allEventsSummaries);
                List<UserInteractionSummary> filteredTasksSimple = Lists.newArrayList();
                for (UserInteractionSummary ts : tasks) {
                    if (ts.getComponent().toLowerCase().contains(text.toLowerCase())) {
                        filteredTasksSimple.add(ts);
                    }
                }
                dataProvider.getList().clear();
                dataProvider.setList(filteredTasksSimple);
                dataProvider.refresh();
            }
        }

    }

    public void addDataDisplay(HasData<UserInteractionSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    /**
     * TODO Export all human events to .txt
     */
    public void exportToTxt() {
        if (allEventsSummaries == null || allEventsSummaries.isEmpty()) {
            view.displayNotification(constants.No_Human_Events());
        } else {
            try {
                UtilEvent.exportEventsToTxt(identity.getName(), allEventsSummaries);
            } catch (IOException e) {
                view.displayNotification(constants.Error_Export_File());
            }
        }
    }

    private UserInteractionSummary buildHumanEventSummary(UserInteractionEvent humaEvent) {
        return new UserInteractionSummary(humaEvent.getKey(), humaEvent.getEvent().getComponent(), humaEvent.getEvent().getAction(),
                humaEvent.getUser(), humaEvent.getStatus().toString(), humaEvent.getLevel().toString(), humaEvent.getEvent()
                        .getModule());
    }
    
}
