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
package org.jbpm.console.ng.ht.forms.client.editors.taskform.standalone;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.shared.GWT;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ht.forms.client.editors.taskform.FormDisplayPresenter;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.events.EditPanelEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

@Dependent
@WorkbenchScreen(identifier = "StandaloneFormDisplay")
public class StandaloneFormDisplayPresenter extends FormDisplayPresenter {
    private Constants constants = GWT.create(Constants.class);

    public static final String SUCCESS_CODE = "success";
    public static final String ERROR_CODE = "error";

    protected String opener;

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartView
    public UberView<FormDisplayPresenter> getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Form();
    }

    @PostConstruct
    public void init() {
        super.init();
    }

    @OnOpen
    public void onOpen() {
        super.onOpen();
        opener = place.getParameter("opener", null);

        if (opener != null) injectEventListener(this);
    }


    protected void eventListener(String origin, String request) {
        if (origin == null || !origin.endsWith("//" + opener)) return;

        ActionRequest actionRequest = JsonUtils.safeEval(request);

        if (ACTION_START_PROCESS.equals(actionRequest.getAction())) startProcess();
        else if (ACTION_CLAIM_TASK.equals(actionRequest.getAction())) claimTask();
        else if (ACTION_START_TASK.equals(actionRequest.getAction())) startTask();
        else if (ACTION_RELEASE_TASK.equals(actionRequest.getAction())) releaseTask();
        else if (ACTION_SAVE_TASK.equals(actionRequest.getAction())) saveTaskState();
        else if (ACTION_COMPLETE_TASK.equals(actionRequest.getAction())) completeTask();
    }

    @Override
    protected RemoteCallback<Long> getStartProcessCallback() {
        if (opener == null) return super.getStartProcessCallback();
        return new RemoteCallback<Long>() {
            @Override
            public void callback(Long processInstanceId) {
                String notification = "Process Id: " + processInstanceId + " started!";
                view.displayNotification(notification);
                newProcessInstanceEvent.fire(new NewProcessInstanceEvent(currentDomainId, processInstanceId, currentProcessId,"", 1));
                close();
                notifyOpener(SUCCESS_CODE, notification);
            }
        };
    }

    @Override
    protected ErrorCallback<Message> getUnexpectedErrorCallback() {
        if (opener == null) return super.getUnexpectedErrorCallback();
        return new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message, Throwable throwable ) {
                String notification = "Unexpected error encountered : " + throwable.getMessage();
                ErrorPopup.showMessage(notification);
                notifyOpener(ERROR_CODE, notification);
                return true;
            }
        };
    }

    @Override
    protected RemoteCallback getClaimTaskCallback() {
        if (opener == null) return super.getClaimTaskCallback();
        return new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                String notification = "Task Id: " + currentTaskId + " was claimed!";
                view.displayNotification(notification);
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
                notifyOpener(SUCCESS_CODE, notification);
            }
        };
    }

    @Override
    protected RemoteCallback getStartTaskRemoteCallback() {
        if (opener == null) return super.getStartTaskRemoteCallback();
        return new RemoteCallback<Long>() {
            @Override
            public void callback(Long processInstanceId) {
                String notification = "Task Id: " + currentTaskId + " was started!";
                view.displayNotification(notification);
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
                notifyOpener(SUCCESS_CODE, notification);
            }
        };
    }

    @Override
    protected RemoteCallback getSaveTaskStateCallback() {
        if (opener == null) return super.getSaveTaskStateCallback();
        return new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                String notification = "Task Id: " + currentTaskId + " State was Saved! ContentId : " + contentId;
                view.displayNotification(notification);
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
                notifyOpener(SUCCESS_CODE, notification);
            }
        };
    }

    @Override
    protected RemoteCallback getReleaseTaskRemoteCallback() {
        if (opener == null) return super.getReleaseTaskRemoteCallback();
        return new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                String notification = "Task Id: " + currentTaskId + " was released!";
                view.displayNotification(notification);
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
                notifyOpener(SUCCESS_CODE, notification);
            }
        };
    }

    @Override
    protected RemoteCallback<Void> getCompleteTaskRemoteCallback() {
        if (opener == null) return super.getCompleteTaskRemoteCallback();
        return new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                String notification = "Form for Task Id: " + currentTaskId + " was completed!";
                view.displayNotification(notification);
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                taskServices.call(new RemoteCallback<Boolean>() {
                    @Override
                    public void callback(Boolean response) {
                        if( !response ){
                            editPanelEvent.fire( new EditPanelEvent( currentTaskId ) );
                        }
                    }
                }).existInDatabase(currentTaskId);
                notifyOpener(SUCCESS_CODE, notification);
            }
        };
    }

    private native void injectEventListener(FormDisplayPresenter fdp) /*-{
        function postMessageListener(e) {
            fdp.@org.jbpm.console.ng.ht.forms.client.editors.taskform.standalone.StandaloneFormDisplayPresenter::eventListener(Ljava/lang/String;Ljava/lang/String;)(e.origin, e.data);
        }

        if ($wnd.addEventListener) {
            $wnd.addEventListener("message", postMessageListener, false);
        } else {
            $wnd.attachEvent("onmessage", postMessageListener, false);
        }
    }-*/;

    private native void notifyOpener(String status, String message) /*-{
        var response = '{"status":"' + status + '", "message":"' + message + '"}'
        $wnd.top.postMessage(response, $wnd.location.href);
    }-*/;

    @Override
    protected boolean showButtons() {
        return opener == null;
    }
}
