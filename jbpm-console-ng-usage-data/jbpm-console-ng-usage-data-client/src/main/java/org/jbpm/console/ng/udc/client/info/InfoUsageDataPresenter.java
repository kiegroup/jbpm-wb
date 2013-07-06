/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.udc.client.info;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.udc.client.i8n.Constants;
import org.jbpm.console.ng.udc.client.util.UtilUsageData;
import org.jbpm.console.ng.udc.model.InfoUsageDataSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

@Dependent
@WorkbenchPopup(identifier = "Info Usage Data")
public class InfoUsageDataPresenter {

    public InfoUsageDataPresenter() {
    }
    
    private Constants constants = GWT.create(Constants.class);
    
    @Inject
    InfoUsageDataEventView view;

    @Inject
    Identity identity;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    
    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Info_Usage_Data();
    }

    @WorkbenchPartView
    public UberView<InfoUsageDataPresenter> getView() {
        return view;
    }

    public interface InfoUsageDataEventView extends UberView<InfoUsageDataPresenter> {

        void displayNotification(String text);

        MultiSelectionModel<InfoUsageDataSummary> getSelectionModel();

        void refreshInfoUsageData();
        

    }

    private PlaceRequest place;
    
    private ListDataProvider<InfoUsageDataSummary> dataProvider = new ListDataProvider<InfoUsageDataSummary>();
    
    private List<InfoUsageDataSummary> allInfoUsageSummaries;

    @PostConstruct
    public void init() {
    }

    public void refreshInfoUsageData(){
        List<InfoUsageDataSummary> listInfoAudited = Lists.newArrayList();
        Map<String, Set<String>> componentsAudited = UtilUsageData.getAllComponentByModule();
        for (Map.Entry<String, Set<String>> entry : componentsAudited.entrySet()) {
            InfoUsageDataSummary info = new InfoUsageDataSummary();
            info.setModule(entry.getKey());
            info.setComponents(UtilUsageData.getComponentFormated(entry.getValue()));
            listInfoAudited.add(info);
        }
        allInfoUsageSummaries = listInfoAudited;
        filterInfo("");
    }
    
    public void filterInfo(String text) {
        if (text.equals("")) {
            if (allInfoUsageSummaries != null) {
                dataProvider.getList().clear();
                dataProvider.setList(Lists.newArrayList(allInfoUsageSummaries));
                dataProvider.refresh();
            }
        } else {
            if (allInfoUsageSummaries != null) {
                List<InfoUsageDataSummary> listInfo = Lists.newArrayList(allInfoUsageSummaries);
                List<InfoUsageDataSummary> filteredInfoUsage = Lists.newArrayList();
                for (InfoUsageDataSummary ts : listInfo) {
                    if (ts.getComponents().toLowerCase().contains(text.toLowerCase())) {
                        filteredInfoUsage.add(ts);
                    }
                }
                dataProvider.getList().clear();
                dataProvider.setList(filteredInfoUsage);
                dataProvider.refresh();
            }
        }

    }
    
    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
    
    public void addDataDisplay(HasData<InfoUsageDataSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public List<InfoUsageDataSummary> getAllInfoUsageSummaries() {
        return allInfoUsageSummaries;
    }
   
}
