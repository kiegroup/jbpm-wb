/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.console.ng.client.editors.tasks.fb.display;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;
import org.jbpm.console.ng.shared.fb.FormServiceEntryPoint;
import org.jbpm.console.ng.shared.fb.events.FormRenderedEvent;
import org.jbpm.console.ng.shared.model.TaskSummary;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Form Display")
public class FormDisplayPresenter {

    @Inject
    private FormBuilderView               view;
    @Inject
    private Caller<FormServiceEntryPoint> formServices;
    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;
    @Inject
    private Event<FormRenderedEvent>      formRendered;
    @Inject
    private Identity                      identity;
    @Inject
    private PlaceManager                  placeManager;

    private PlaceRequest place;

    public interface FormBuilderView
            extends
            UberView<FormDisplayPresenter> {

        void displayNotification( String text );

        long getTaskId();

        void setTaskId( long taskId );

        VerticalPanel getFormView();

        Label getTaskNameText();

        Label getTaskDescriptionText();
    }

    @PostConstruct
    public void init() {
        publish( this );
        publishGetFormValues();
    }

    @OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

    public void renderForm( final long taskId ) {

        formServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( String form ) {
                view.getFormView().clear();
                view.getFormView().add( new HTMLPanel( form ) );
                taskServices.call( new RemoteCallback<TaskSummary>() {
                    @Override
                    public void callback( TaskSummary task ) {
                        view.getTaskNameText().setText( task.getName() );
                        view.getTaskDescriptionText().setText( task.getDescription() );
                    }
                } ).getTaskDetails( taskId );
            }
        } ).getFormDisplayTask( taskId );

    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Form Display";
    }

    @WorkbenchPartView
    public UberView<FormDisplayPresenter> getView() {
        return view;
    }

    public void completeTask( String values ) {
        final Map<String, String> params = getUrlParameters( values );
        final Map<String, Object> objParams = new HashMap<String, Object>( params );
        taskServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Form for Task Id: " + params.get( "taskId" ) + " was completed!" );

            }
        } ).complete( Long.parseLong( params.get( "taskId" ) ), identity.getName(), objParams );

    }

    public void startTask( String values ) {
        final Map<String, String> params = getUrlParameters( values );
        taskServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Task Id: " + params.get( "taskId" ) + " was started!" );
                renderForm( Long.parseLong( params.get( "taskId" ).toString() ) );
            }
        } ).start( Long.parseLong( params.get( "taskId" ) ), identity.getName() );

    }

    public void saveTaskState( String values ) {
        final Map<String, String> params = getUrlParameters( values );
        taskServices.call( new RemoteCallback<Long>() {
            @Override
            public void callback( Long contentId ) {
                view.displayNotification( "Task Id: " + params.get( "taskId" ) + " State was Saved! ContentId : " + contentId );
                renderForm( Long.parseLong( params.get( "taskId" ).toString() ) );
            }
        } ).saveContent( Long.parseLong( params.get( "taskId" ).toString() ), params );

    }

    // Set up the JS-callable signature as a global JS function.
    private native void publish( FormDisplayPresenter fdp ) /*-{

        $wnd.completeTask = function (from) {
            fdp.@org.jbpm.console.ng.client.editors.tasks.fb.display.FormDisplayPresenter::completeTask(Ljava/lang/String;)(from);
        }

        $wnd.startTask = function (from) {
            fdp.@org.jbpm.console.ng.client.editors.tasks.fb.display.FormDisplayPresenter::startTask(Ljava/lang/String;)(from);
        }
        $wnd.saveTaskState = function (from) {
            fdp.@org.jbpm.console.ng.client.editors.tasks.fb.display.FormDisplayPresenter::saveTaskState(Ljava/lang/String;)(from);
        }

    }-*/;

    private native void publishGetFormValues() /*-{
        $wnd.getFormValues = function (form) {
            var params = '';
            for (i = 0; i < form.elements.length; i++) {
                var fieldName = form.elements[i].name;
                var fieldValue = form.elements[i].value;
                params += fieldName + '=' + fieldValue + '&';
            }
            return params;
        };

    }-*/;

    public static Map<String, String> getUrlParameters( String values ) {
        Map<String, String> params = new HashMap<String, String>();
        for ( String param : values.split( "&" ) ) {
            String pair[] = param.split( "=" );
            String key = pair[ 0 ];
            String value = "";
            if ( pair.length > 1 ) {
                value = pair[ 1 ];
            }
            params.put( key, value );
        }
        return params;
    }

    @OnReveal
    public void onReveal() {
        long taskId = Long.parseLong( place.getParameter( "taskId", "0" ).toString() );
        view.setTaskId( taskId );
        renderForm( taskId );
    }
}
