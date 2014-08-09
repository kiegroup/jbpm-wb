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
package org.jbpm.console.ng.ht.client.editors.tasklogs;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.ProvidesKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.model.RuntimeLogSummary;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.client.editors.taskassignments.TaskAssignmentsPresenter;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.client.util.LogUtils.LogType;
import org.jbpm.console.ng.ht.client.util.LogUtils.LogOrder;
import org.jbpm.console.ng.ht.client.util.LogUtils;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.jbpm.console.ng.pr.model.ProcessVariableSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;

@Dependent
@WorkbenchScreen(identifier = "Task Logs")
public class TaskLogsPresenter {

    private Constants constants = GWT.create(Constants.class);

    public interface TaskLogsLogView extends UberView<TaskLogsPresenter> {

        void displayNotification(String text);

        HTML getLogTextArea();
        
        Label getProcessInstanceStatusText();
                
        Label getProcessInstanceNameText();
    }
    
    @Inject
    private PlaceManager placeManager;

    @Inject
    private TaskLogsLogView view;

    @Inject
    private Identity identity;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    private long currentTaskId = 0;
    
    @Inject
    private Event<TaskRefreshedEvent> taskRefreshed;

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Assignments();
    }

    @WorkbenchPartView
    public UberView<TaskLogsPresenter> getView() {
        return view;
    }

    @OnOpen
    public void onOpen() {

        this.currentTaskId = Long.parseLong(place.getParameter("taskId", "0").toString());

    }
    
    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event){
        if(currentTaskId == event.getTaskId()){
        }
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }


    public void refreshProcessInstanceData(final Long processInstanceId, final LogOrder logOrder, final LogType logType) {
        
        view.getProcessInstanceNameText().setText("");
        view.getProcessInstanceStatusText().setText("");
        view.getLogTextArea().setText("");
        
        dataServices.call(new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback(ProcessInstanceSummary instanceSummary) {                
                view.getProcessInstanceNameText().setText(instanceSummary.getProcessName());
                view.getProcessInstanceStatusText().setText(LogUtils.getInstanceStatus(instanceSummary.getState()));                
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message, Throwable throwable ) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).getProcessInstanceById(processInstanceId);
        
        if(LogType.TECHNICAL.equals(logType)){
            dataServices.call(new RemoteCallback<List<RuntimeLogSummary>>() {
                @Override
                public void callback(List<RuntimeLogSummary> logs) {                    
                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();

                    if(logOrder == LogOrder.DESC)
                        Collections.reverse(logs);

                    for (RuntimeLogSummary rls : logs) {           
                        safeHtmlBuilder.appendEscapedLines(rls.getTime() + ": " + rls.getLogLine() + " - " + rls.getType() + "\n");                            
                    }
                    view.getLogTextArea().setHTML(safeHtmlBuilder.toSafeHtml());
                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error( Message message, Throwable throwable ) {                    
                    ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                    return true;
                }
            }).getAllRuntimeLogs(processInstanceId);
        }else{
            dataServices.call(new RemoteCallback<List<RuntimeLogSummary>>() {
                @Override
                public void callback(List<RuntimeLogSummary> logs) {
                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                    if(logOrder == LogOrder.DESC)
                        Collections.reverse(logs);

                    for (RuntimeLogSummary rls : logs) {                       
                        safeHtmlBuilder.appendEscapedLines(rls.getTime() + ": " + rls.getLogLine() + "\n");                            
                    }
                    view.getLogTextArea().setHTML(safeHtmlBuilder.toSafeHtml());
                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error( Message message, Throwable throwable ) {                    
                    ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                    return true;
                }
            }).getBusinessLogs(processInstanceId);
        }        
    }
}
