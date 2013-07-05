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

package org.jbpm.console.ng.udc.client.usagelist;

import java.util.List;
import java.util.Queue;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.udc.client.event.UsageEvent;
import org.jbpm.console.ng.udc.client.i8n.Constants;
import org.jbpm.console.ng.udc.model.UsageEventSummary;
import org.jbpm.console.ng.udc.service.UsageServiceEntryPoint;
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
@WorkbenchScreen(identifier = "Usage Data Collector")
public class UsageDataPresenter {

    private Constants constants = GWT.create(Constants.class);

    @Inject
    private ActionHistoryView view;

    @Inject
    private Caller<UsageServiceEntryPoint> usageDataService;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    @WorkbenchPartView
    public UberView<UsageDataPresenter> getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.List_Usage_Data();
    }

    private List<UsageEventSummary> allUsageEventSummaries;

    private ListDataProvider<UsageEventSummary> dataProvider = new ListDataProvider<UsageEventSummary>();

    public interface ActionHistoryView extends UberView<UsageDataPresenter> {

        void displayNotification(String text);

        MultiSelectionModel<UsageEventSummary> getSelectionModel();

        TextBox getSearchBox();

        void refreshUsageDataCollector();

        void clearUsageData();

        void showInfoUsageData();

        void exportTxtEvents();
    }

    public void saveNewHumanEvent(@Observes UsageEvent humaEvent) {
        usageDataService.call(new RemoteCallback<Queue<UsageEventSummary>>() {
            @Override
            public void callback(Queue<UsageEventSummary> events) {
                allUsageEventSummaries = Lists.newArrayList(events);
            }
        }).saveNewUsageDataEvent(buildHumanEventSummary(humaEvent));
    }

    public List<UsageEventSummary> getAllEventsSummaries() {
        return allUsageEventSummaries;
    }

    public void refreshUsageDataCollector() {
        usageDataService.call(new RemoteCallback<Queue<UsageEventSummary>>() {
            @Override
            public void callback(Queue<UsageEventSummary> events) {
                if (events != null) {
                    allUsageEventSummaries = Lists.newArrayList(events);
                }
                filterEvents(view.getSearchBox().getText());
            }
        }).getAllUsageData();
    }
    
    public void clearUsageData() {
        usageDataService.call(new RemoteCallback<Queue<UsageEventSummary>>() {
            @Override
            public void callback(Queue<UsageEventSummary> events) {
                allUsageEventSummaries = Lists.newArrayList();
                filterEvents(view.getSearchBox().getText());
                view.displayNotification(constants.Clear_Msj());
            }
        }).clearUsageDataCollector();
    }

    public void showInfoUsageData() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Info Usage Data");
        placeManager.goTo(placeRequestImpl);
    }

    public void filterEvents(String text) {
        if (text.equals("")) {
            if (allUsageEventSummaries != null) {
                dataProvider.getList().clear();
                dataProvider.setList(Lists.newArrayList(allUsageEventSummaries));
                dataProvider.refresh();
            }
        } else {
            if (allUsageEventSummaries != null) {
                List<UsageEventSummary> tasks = Lists.newArrayList(allUsageEventSummaries);
                List<UsageEventSummary> filteredTasksSimple = Lists.newArrayList();
                for (UsageEventSummary ts : tasks) {
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

    public void addDataDisplay(HasData<UsageEventSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    /**
     * TODO Export all human events to .txt
     */
    public void exportToTxt() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Export Usage Data");
        placeManager.goTo(placeRequestImpl);
    }

    private UsageEventSummary buildHumanEventSummary(UsageEvent humaEvent) {
        return new UsageEventSummary(humaEvent.getKey(), humaEvent.getEvent().getComponent(), humaEvent.getEvent().getAction(),
                humaEvent.getUser(), humaEvent.getStatus().toString(), humaEvent.getLevel().toString(), humaEvent.getEvent()
                        .getModule());
    }
    
}
