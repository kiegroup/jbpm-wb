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

package org.jbpm.console.ng.es.client.editors.quicknewjob;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Focusable;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchPopup(identifier = "Quick New Job")
public class QuickNewJobPresenter {

    public interface QuickNewJobView extends UberView<QuickNewJobPresenter> {

        Focusable getJobNameText();

        void removeRow( RequestParameterSummary parameter );

        void addRow( RequestParameterSummary parameter );

        void displayNotification( String notification );
    }

    @Inject
    QuickNewJobView view;
    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    @Inject
    private Event<RequestChangedEvent> requestCreatedEvent;
    private PlaceRequest place;

    public QuickNewJobPresenter() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Quick New Job";
    }

    @WorkbenchPartView
    public UberView<QuickNewJobPresenter> getView() {
        return view;
    }

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    public void removeParameter( RequestParameterSummary parameter ) {
        view.removeRow( parameter );
    }

    public void addNewParameter() {
        view.addRow( new RequestParameterSummary( "click to edit", "click to edit" ) );
    }

    public void createJob( String jobName,
                           Date dueDate,
                           String jobType,
                           Integer numberOfTries,
                           List<RequestParameterSummary> parameters ) {

        Map<String, String> ctx = new HashMap<String, String>();
        if ( parameters != null ) {
            for ( RequestParameterSummary param : parameters ) {
                ctx.put( param.getKey(), param.getValue() );
            }
        }
        ctx.put( "retries", String.valueOf( numberOfTries ) ); // TODO make legacy keys hard to repeat by accident
        ctx.put( "jobName", jobName ); // TODO make legacy keys hard to repeat by accident

        executorServices.call( new RemoteCallback<Long>() {
            @Override
            public void callback( Long requestId ) {
                view.displayNotification( "Request Schedulled: " + requestId );
                requestCreatedEvent.fire( new RequestChangedEvent( requestId ) );
                close();
            }
        } ).scheduleRequest( jobType, dueDate, ctx );

    }

    @OnOpen
    public void onOpen() {
        view.getJobNameText().setFocus( true );
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

}
