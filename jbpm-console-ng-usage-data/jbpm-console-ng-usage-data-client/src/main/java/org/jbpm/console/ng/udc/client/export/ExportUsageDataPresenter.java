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

package org.jbpm.console.ng.udc.client.export;

import java.util.List;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.udc.client.i8n.Constants;
import org.jbpm.console.ng.udc.client.util.UtilUsageData;
import org.jbpm.console.ng.udc.model.UsageEventSummary;
import org.jbpm.console.ng.udc.service.UsageServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;

@Dependent
@WorkbenchPopup(identifier = "Export Usage Data")
public class ExportUsageDataPresenter {

    public ExportUsageDataPresenter() {
    }

    public interface ExportUsageDataEventView extends UberView<ExportUsageDataPresenter> {
        void displayNotification(String text);
    }

    private Constants constants = GWT.create(Constants.class);

    @Inject
    ExportUsageDataEventView view;

    @Inject
    Identity identity;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    @Inject
    private Caller<UsageServiceEntryPoint> usageDataService;
    
    private String textFormatCsv;

    private List<UsageEventSummary> allUsageEventSummaries;

    private PlaceRequest place;

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Info_Usage_Data();
    }

    @WorkbenchPartView
    public UberView<ExportUsageDataPresenter> getView() {
        return view;
    }

    @PostConstruct
    public void init() {
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }

    public void formatInfoCsv() {
        textFormatCsv = "";
        usageDataService.call(new RemoteCallback<Queue<UsageEventSummary>>() {
            @Override
            public void callback(Queue<UsageEventSummary> events) {
                if (events != null) {
                    allUsageEventSummaries = Lists.newArrayList(events);
                    setFormatCsv();
                }
            }
        }).getAllUsageData();
    }

    private void setFormatCsv(){
    	StringBuilder formatCsv = new StringBuilder();
    	for (UsageEventSummary usage : allUsageEventSummaries) {
            formatCsv.append(UtilUsageData.getRowFormatted(usage));
        }
    	textFormatCsv = formatCsv.toString(); 
    }

	public String getTextFormtCsv() {
		return textFormatCsv;
	}

}
