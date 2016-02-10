/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskEventSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskAuditService;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.paging.PageResponse;

@Dependent
public class TaskLogsPresenter {

    public interface TaskLogsView extends IsWidget {

        void init( final TaskLogsPresenter presenter );

        void displayNotification( final String text );

        void setLogTextAreaText( final String text );

    }

    private TaskLogsView view;

    private Caller<TaskAuditService> taskAuditService;

    private long currentTaskId = 0;

    private Constants constants = Constants.INSTANCE;

    @Inject
    public TaskLogsPresenter( final TaskLogsView view, final Caller<TaskAuditService> taskAuditService ) {
        this.view = view;
        this.taskAuditService = taskAuditService;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public IsWidget getView() {
        return view;
    }

    public void refreshLogs() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "taskId", currentTaskId );
        QueryFilter filter = new PortableQueryFilter( 0, 0, false, "", "", false, "", params );
        taskAuditService.call( new RemoteCallback<PageResponse<TaskEventSummary>>() {
            @Override
            public void callback( PageResponse<TaskEventSummary> events ) {
                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                DateTimeFormat format = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm" );
                for ( TaskEventSummary tes : events.getPageRowList() ) {
                    String timeStamp = format.format( tes.getLogTime() );
                    if(tes.getType().equals("UPDATED")){
                        safeHtmlBuilder.appendEscapedLines(timeStamp + ": Task " + tes.getType() + " (" + tes.getMessage() + ") \n");
                    }else {
                        safeHtmlBuilder.appendEscapedLines(timeStamp + ": Task - " + tes.getType() + " (" + tes.getUserId() + ") \n");
                    }
                }
                view.setLogTextAreaText( safeHtmlBuilder.toSafeHtml().asString() );
            }

        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                ErrorPopup.showMessage( constants.UnexpectedError(throwable.getMessage()) );
                return true;
            }
        } ).getData( filter );

    }

    public void onTaskSelectionEvent( @Observes final TaskSelectionEvent event ) {
        this.currentTaskId = event.getTaskId();
        refreshLogs();
    }

    public void onTaskRefreshedEvent( @Observes final TaskRefreshedEvent event ) {
        if ( currentTaskId == event.getTaskId() ) {
            refreshLogs();
        }
    }
}
