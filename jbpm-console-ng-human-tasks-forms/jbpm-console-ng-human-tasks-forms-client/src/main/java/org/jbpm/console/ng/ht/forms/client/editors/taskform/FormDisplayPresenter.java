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

package org.jbpm.console.ng.ht.forms.client.editors.taskform;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.EditPanelEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskStyleEvent;
import org.jbpm.console.ng.ht.forms.model.events.FormRenderedEvent;
import org.jbpm.console.ng.ht.forms.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.console.ng.ht.forms.service.FormServiceEntryPoint;
import org.jbpm.console.ng.ht.model.events.RenderFormEvent;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;

@Dependent
@WorkbenchScreen(identifier = "Form Display")
public class FormDisplayPresenter {
    private Constants constants = GWT.create(Constants.class);

    public static final String ACTION_START_PROCESS = "startProcess";
    public static final String ACTION_CLAIM_TASK = "claimTask";
    public static final String ACTION_START_TASK = "startTask";
    public static final String ACTION_RELEASE_TASK = "releaseTask";
    public static final String ACTION_SAVE_TASK = "saveTask";
    public static final String ACTION_COMPLETE_TASK = "completeTask";

    @Inject
    protected FormDisplayView view;

    @Inject
    private Caller<FormServiceEntryPoint> formServices;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Caller<FormModelerProcessStarterEntryPoint> renderContextServices;

    @Inject
    Caller<KieSessionEntryPoint> sessionServices;

    @Inject
    protected Caller<TaskServiceEntryPoint> taskServices;

    @Inject
    private Event<FormRenderedEvent> formRendered;

    @Inject
    protected Event<TaskRefreshedEvent> taskRefreshed;

    @Inject
    protected Event<NewProcessInstanceEvent> newProcessInstanceEvent;

    @Inject
    protected Event<EditPanelEvent> editPanelEvent;
    
    @Inject
    private ActivityManager activityManager;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    protected PlaceRequest place;

    protected String formCtx;

    protected boolean isFormModelerForm;

    protected long currentTaskId = 0;

    protected String currentProcessId;

    protected String currentDomainId;

    @Inject
    protected Event<TaskStyleEvent> taskStyleEvent;
    
    private boolean loadForm = true;

    public interface FormDisplayView extends UberView<FormDisplayPresenter> {

        void displayNotification(String text);

        FlowPanel getOptionsDiv();

        void loadContext(String ctxUID);

        void submitStartProcessForm();

        void submitChangeTab(String tab);

        void submitSaveTaskStateForm();

        void submitCompleteTaskForm();

        void submitForm();

        String getAction();

        VerticalPanel getFormView();
        
        FormRendererWidget getFormRenderer();

        void loadForm(String form);

        boolean isFormModeler();

    }

    @PostConstruct
    public void init() {
        publish( this );
        publishGetFormValues();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    public void renderTaskForm() {
        formServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( String form ) {
                initTaskForm(form);
            }
        } ).getFormDisplayTask(currentTaskId);
    }

    protected void initTaskForm(String form) {
        
        if (form == null || form.length() == 0) {
            return;
        }
        view.loadForm(form);

        isFormModelerForm = view.isFormModeler();
        if(loadForm){
            view.loadForm(form);
        }

        formCtx = form;

        taskServices.call( new RemoteCallback<TaskSummary>() {
            @Override
            public void callback( final TaskSummary task ) {
                view.getOptionsDiv().clear();
                FlowPanel wrapperFlowPanel = new FlowPanel();
                wrapperFlowPanel.setStyleName( "wrapper form-actions" );
                view.getOptionsDiv().add( wrapperFlowPanel );

                if (task == null) return;

                if (!showButtons()) {
                    view.getOptionsDiv().setVisible(false);
                    return;
                }

                if ( task.getStatus().equals( "Ready" ) ) {
                    Button claimButton = new Button();
                    claimButton.setType(ButtonType.PRIMARY);
                    claimButton.setText(constants.Claim());
                    claimButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            claimTask();
                        }
                    });
                    wrapperFlowPanel.add( claimButton );
                    view.getOptionsDiv().add( wrapperFlowPanel );
                }

                if ( task.getStatus().equals( "Reserved" ) && task.getActualOwner().equals(identity.getName()) ) {

                    Button releaseButton = new Button();
                    releaseButton.setText(constants.Release());
                    releaseButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            releaseTask();
                        }
                    });
                    wrapperFlowPanel.add( releaseButton );

                    Button startButton = new Button();
                    startButton.setType(ButtonType.PRIMARY);
                    startButton.setText(constants.Start());
                    startButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            startTask();
                        }
                    });
                    wrapperFlowPanel.add( startButton );


                    view.getOptionsDiv().add( wrapperFlowPanel );
                } else if ( task.getStatus().equals( "InProgress" ) && task.getActualOwner().equals(identity.getName()) ) {
                    Button saveButton = new Button();
                    saveButton.setText(constants.Save());
                    saveButton.addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            saveTaskState();
                        }
                    });
                    wrapperFlowPanel.add( saveButton );

                    Button releaseButton = new Button();
                    releaseButton.setText(constants.Release());
                    releaseButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            releaseTask();
                        }
                    });
                    wrapperFlowPanel.add( releaseButton );

                    Button completeButton = new Button();
                    completeButton.setType(ButtonType.PRIMARY);
                    completeButton.setText(constants.Complete());
                    completeButton.addClickHandler(new ClickHandler(){
                        @Override
                        public void onClick(ClickEvent event) {
                            completeTask();
                        }
                    });

                    wrapperFlowPanel.add( completeButton );
                    view.getOptionsDiv().add( wrapperFlowPanel );
                    taskStyleEvent.fire( new TaskStyleEvent( task.getId() ) );
                }
            }
        }, getUnexpectedErrorCallback()).getTaskDetails(currentTaskId);
    }

    public void renderProcessForm() {

        formServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( String form ) {
                view.loadForm(form);
                isFormModelerForm = view.isFormModeler();
                
                if(loadForm){
                    view.loadForm(form);
                    formCtx = form;
                }

                dataServices.call( new RemoteCallback<ProcessSummary>() {
                    @Override
                    public void callback( ProcessSummary summary ) {
                        FocusPanel wrapperFlowPanel = new FocusPanel();
                        wrapperFlowPanel.setStyleName( "wrapper form-actions" );

                        if (!showButtons()) {
                            view.getOptionsDiv().setVisible(false);
                            return;
                        }

                        ClickHandler start =  new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                startProcess();
                            }
                        };

                        Button startButton = new Button();
                        startButton.setText(constants.Start());
                        startButton.addClickHandler(start);

                        wrapperFlowPanel.add( startButton );
                        view.getOptionsDiv().add( wrapperFlowPanel );
                    }
                } ).getProcessDesc(currentProcessId);
            }
        }, getUnexpectedErrorCallback()).getFormDisplayProcess(currentDomainId, currentProcessId);

    }

    public void onFormSubmitted(@Observes FormSubmittedEvent event) {
        if (event.isMine(formCtx)) {
            if (event.getContext().getErrors() == 0) {
                if(ACTION_START_PROCESS.equals(view.getAction())) {
                    doStartProcess();
                } else if (ACTION_SAVE_TASK.equals(view.getAction())) {
                    doSaveTaskState();
                } else if (ACTION_COMPLETE_TASK.equals(view.getAction())) {
                    doCompleteTask();
                }
            }
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Form();
    }

    @WorkbenchPartView
    public UberView<FormDisplayPresenter> getView() {
        return view;
    }

    protected RemoteCallback<Void> getCompleteTaskRemoteCallback() {
        return new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Form for Task Id: " + currentTaskId + " was completed!");
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));

                taskServices.call(new RemoteCallback<Boolean>() {
                    @Override
                    public void callback(Boolean response) {
                        if( !response ){
                            editPanelEvent.fire( new EditPanelEvent( currentTaskId ) );
                        }
                    }
                }).existInDatabase(currentTaskId);

            }
        };
    }

    public void completeTask(String values) {
        final Map<String, String> params = getUrlParameters(values);
        final Map<String, Object> objParams = new HashMap<String, Object>(params);
        taskServices.call(getCompleteTaskRemoteCallback(), getUnexpectedErrorCallback()).complete(currentTaskId, identity.getName(), objParams);

    }

    public void claimTask() {
        if (isFormModelerForm) claimTaskFromFromModelerForm();
        else doClaimTask();
    }

    public void startTask() {
        if (isFormModelerForm) startTaskFromFromModelerForm();
        else doStartTask();
    }

    public void releaseTask() {
        if(isFormModelerForm) releaseTaskFromFormModelerForm();
        else doReleaseTask();
    }

    public void completeTask() {
        if (isFormModelerForm) view.submitCompleteTaskForm();
        else completeTaskFromHTMLForm();
    }

    public void saveTaskState() {
        if (isFormModelerForm) view.submitSaveTaskStateForm();
        else saveTaskStateFromHTMLForm();
    }

    protected native void completeTaskFromHTMLForm()/*-{
        $wnd.completeTask($wnd.getFormValues($doc.getElementById("form-data")));
    }-*/;

    protected native void saveTaskStateFromHTMLForm()/*-{
        $wnd.saveTaskState($wnd.getFormValues($doc.getElementById("form-data")));
    }-*/;


    protected ErrorCallback<Message> getUnexpectedErrorCallback() {
        return new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message, Throwable throwable ) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        };
    }

    protected RemoteCallback getSaveTaskStateCallback() {
        return new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                view.displayNotification("Task Id: " + currentTaskId + " State was Saved! ContentId : " + contentId);
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
            }
        };
    }

    public void saveTaskState(String values) {
        final Map<String, String> params = getUrlParameters(values);
        taskServices.call(getSaveTaskStateCallback(), getUnexpectedErrorCallback()).saveContent(currentTaskId, params);
    }

    public void startTaskFromFromModelerForm() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                doStartTask();
            }
        }).clearContext(formCtx);
    }

    protected RemoteCallback getStartTaskRemoteCallback() {
        return new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Task Id: " + currentTaskId + " was started!");
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
            }
        };
    }

    public void doStartTask() {
        taskServices.call(getStartTaskRemoteCallback(), getUnexpectedErrorCallback()).start(currentTaskId, identity.getName());
    }

    protected void releaseTaskFromFormModelerForm() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                doReleaseTask();
            }
        }).clearContext(formCtx);
    }

    protected RemoteCallback getReleaseTaskRemoteCallback() {
        return new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Task Id: " + currentTaskId + " was released!" );
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
            }
        };
    }

    protected void doReleaseTask() {
        taskServices.call(getReleaseTaskRemoteCallback(), getUnexpectedErrorCallback()).release( currentTaskId, identity.getName() );

    }

    protected void claimTaskFromFromModelerForm() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                doClaimTask();
            }
        }).clearContext(formCtx);
    }

    protected RemoteCallback getClaimTaskCallback() {
        return new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Task Id: " + currentTaskId + " was claimed!" );
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
            }
        };
    }

    protected void doClaimTask() {
        taskServices.call(getClaimTaskCallback(), getUnexpectedErrorCallback()).claim(currentTaskId, identity.getName());
    }

    protected void doSaveTaskState() {
        renderContextServices.call(getSaveTaskStateCallback(), getUnexpectedErrorCallback()).saveTaskStateFromRenderContext(formCtx, currentTaskId);
    }

    protected void doCompleteTask() {
        renderContextServices.call(getCompleteTaskRemoteCallback(), getUnexpectedErrorCallback()).completeTaskFromContext(formCtx, currentTaskId, identity.getName());
    }

    protected void startProcess() {
        if (isFormModelerForm) {
            view.submitStartProcessForm();
        } else {
            startProcessFromHTMLForm();
        }
    }

    protected native void startProcessFromHTMLForm() /*-{
        $wnd.startProcess($wnd.getFormValues($doc.getElementById("form-data")));
    }-*/;


    protected RemoteCallback<Long> getStartProcessCallback() {
        return new RemoteCallback<Long>() {
            @Override
            public void callback(Long processInstanceId) {
                view.displayNotification("Process Id: " + processInstanceId + " started!");
                newProcessInstanceEvent.fire(new NewProcessInstanceEvent(currentDomainId, processInstanceId, currentProcessId));
                close();
            }
        };
    }

    protected void doStartProcess() {
        renderContextServices.call(getStartProcessCallback(), getUnexpectedErrorCallback()).startProcessFromRenderContext(formCtx, currentDomainId, currentProcessId);
    }

    public void startProcess(String values) {
        final Map<String, String> params = getUrlParameters(values);

        sessionServices.call(getStartProcessCallback(), getUnexpectedErrorCallback()).startProcess(currentDomainId, currentProcessId, params);

    }

    // Set up the JS-callable signature as a global JS function.
    protected native void publish( FormDisplayPresenter fdp )/*-{
        $wnd.completeTask = function (from) {
            fdp.@org.jbpm.console.ng.ht.forms.client.editors.taskform.FormDisplayPresenter::completeTask(Ljava/lang/String;)(from);
        }

        $wnd.saveTaskState = function (from) {
            fdp.@org.jbpm.console.ng.ht.forms.client.editors.taskform.FormDisplayPresenter::saveTaskState(Ljava/lang/String;)(from);
        }

        $wnd.startProcess = function (from) {
            fdp.@org.jbpm.console.ng.ht.forms.client.editors.taskform.FormDisplayPresenter::startProcess(Ljava/lang/String;)(from);
        }
    }-*/;

    protected native void publishGetFormValues() /*-{
        $wnd.getFormValues = function (form) {
            var params = '';

            for (i = 0; i < form.elements.length; i++) {
                var fieldName = form.elements[i].name;
                var fieldValue = form.elements[i].value;
                if (fieldName != '') {
                    params += fieldName + '=' + fieldValue + '&';
                }
            }
            return params;
        };
    }-*/;

    public static Map<String, String> getUrlParameters(String values) {
        Map<String, String> params = new HashMap<String, String>();
        for (String param : values.split("&")) {
            String pair[] = param.split("=");
            String key = pair[0];
            String value = "";
            if (pair.length > 1) {
                value = pair[1];
            }
            if (!key.startsWith("btn_")) {
                params.put(key, value);
            }
        }

        return params;
    }

    @OnOpen
    public void onOpen() {
        currentTaskId = Long.parseLong(place.getParameter("taskId", "-1").toString());
        currentProcessId = place.getParameter("processId", "none").toString();
        currentDomainId = place.getParameter("domainId", "none").toString();

        if (currentTaskId != -1) {
            renderTaskForm();
        } else if (!currentProcessId.equals("none")) {
            renderProcessForm();
        }
    }

    public void dispose() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                formCtx = null;
                if (currentTaskId != -1) {
                    renderTaskForm();
                    taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                } else if (!currentProcessId.equals("none")) {
                    renderProcessForm();
                }

            }
        }).clearContext(formCtx);
    }

    public void close() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                formCtx = null;
                closePlaceEvent.fire(new BeforeClosePlaceEvent(FormDisplayPresenter.this.place));
            }
        }).clearContext(formCtx);
    }

    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event){
        if(currentTaskId == event.getTaskId()) {
            renderContextServices.call(new RemoteCallback<Void>() {
                @Override
                public void callback(Void response) {
                    formCtx = null;
                    if (currentTaskId != -1) {
                        renderTaskForm();
                    } else if (!currentProcessId.equals("none")) {
                        renderProcessForm();
                    }

                }
            }).clearContext(formCtx);
        }
    }

    protected boolean showButtons() {
        return true;
    }
}
