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

package org.jbpm.console.ng.ht.client.editors.taskform;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ht.client.i8n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.fb.events.FormRenderedEvent;
import org.jbpm.console.ng.ht.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.console.ng.ht.service.FormServiceEntryPoint;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceCreated;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@WorkbenchPopup(identifier = "Form Display")
public class FormDisplayPopupPresenter {
    private Constants constants = GWT.create(Constants.class);

    public static final String ACTION_START_PROCESS = "startProcess";
    public static final String ACTION_SAVE_TASK = "saveTask";
    public static final String ACTION_COMPLETE_TASK = "completeTask";
    public static final String ACTION_TASK_DETAILS = "Task Details Popup";
    public static final String ACTION_TASK_COMMENTS = "Task Comments Popup";

    @Inject
    private FormDisplayView view;

    @Inject
    private Caller<FormServiceEntryPoint> formServices;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Caller<FormModelerProcessStarterEntryPoint> renderContextServices;

    @Inject
    Caller<KieSessionEntryPoint> sessionServices;

    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;

    @Inject
    private Event<FormRenderedEvent> formRendered;

    @Inject
    Event<ProcessInstanceCreated> processInstanceCreatedEvents;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    private String formCtx;


    @Inject
    private Event<NotificationEvent> notification;

    public interface FormDisplayView extends UberView<FormDisplayPopupPresenter> {

        void displayNotification(String text);

        long getTaskId();

        void setTaskId(long taskId);

        String getProcessId();

        void setProcessId(String processId);

        void setDomainId(String domainId);

        String getDomainId();

        Label getNameText();

        Label getTaskIdText();

        FlowPanel getOptionsDiv();

        UnorderedList getNavBarUL();

        void loadContext(String ctxUID);

        void submitStartProcessForm();

        void submitChangeTab(String tab);

        void submitSaveTaskStateForm();

        void submitCompleteTaskForm();

        void submitForm();

        String getAction();

        VerticalPanel getFormView();

        void loadForm(String form);

        boolean isFormModeler();

    }

    @PostConstruct
    public void init() {
        publish( this );
        publishGetFormValues();
    }

    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }

    public void renderTaskForm(final long taskId) {
        view.getNavBarUL().clear();

        NavLink workLink = new NavLink(constants.Work());
        workLink.setStyleName("active");

        NavLink detailsLink = new NavLink(constants.Details());
        detailsLink.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (view.isFormModeler()) view.submitChangeTab(ACTION_TASK_DETAILS);
                else changeTab(ACTION_TASK_DETAILS);
            }
        });

        NavLink commentsLink = new NavLink(constants.Comments());
        commentsLink.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (view.isFormModeler()) view.submitChangeTab(ACTION_TASK_COMMENTS);
                else changeTab(ACTION_TASK_COMMENTS);
            }
        });

        view.getNavBarUL().add(workLink);
        view.getNavBarUL().add(detailsLink);
        view.getNavBarUL().add(commentsLink);

        formServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( String form ) {
                initTaskForm(form);
            }
        } ).getFormDisplayTask(taskId);
    }

    protected void initTaskForm(String form) {

        view.loadForm(form);

        final boolean modelerForm = view.isFormModeler();

        formCtx = form;

        taskServices.call( new RemoteCallback<TaskSummary>() {
            @Override
            public void callback( final TaskSummary task ) {
                view.getOptionsDiv().clear();
                FlowPanel wrapperFlowPanel = new FlowPanel();
                wrapperFlowPanel.setStyleName( "wrapper" );
                view.getOptionsDiv().add( wrapperFlowPanel );
                view.getNameText().setText( task.getName() );
                view.getTaskIdText().setText( String.valueOf( task.getId() ) );
                if ( task.getStatus().equals( "Reserved" ) ) {
                    FocusPanel startFlowPanel = new FocusPanel();
                    startFlowPanel.setStyleName( "option-button start" );
                    startFlowPanel.setTitle( "Start Task" );
                    ClickHandler click;
                    if (modelerForm)
                        click = new ClickHandler() {
                            @Override
                            public void onClick( ClickEvent event ) {
                                if (view.isFormModeler()) startFormModelerTask(view.getTaskId(), identity.getName());
                                else startTask(view.getTaskId(), identity.getName());
                            };
                        };
                    else
                        click = new ClickHandler() {
                            @Override
                            public native void onClick( ClickEvent event )/*-{
                                $wnd.startTask($wnd.getFormValues($doc.getElementById("form-data")));
                            }-*/;
                        };
                    startFlowPanel.addClickHandler(click);
                    wrapperFlowPanel.add( startFlowPanel );
                    view.getOptionsDiv().add( wrapperFlowPanel );
                } else if ( task.getStatus().equals( "InProgress" ) ) {
                    FocusPanel saveTaskFlowPanel = new FocusPanel();
                    saveTaskFlowPanel.setStyleName( "option-button save" );
                    saveTaskFlowPanel.setTitle( "Save Task" );
                    ClickHandler save, complete;
                    if (modelerForm) {
                        save = new ClickHandler() {
                            @Override
                            public void onClick( ClickEvent event ) {
                                view.submitSaveTaskStateForm();
                            };
                        };
                        complete = new ClickHandler() {
                            @Override
                            public void onClick( ClickEvent event ) {
                                view.submitCompleteTaskForm();
                            };
                        };
                    } else {
                        save = new ClickHandler() {
                            @Override
                            public native void onClick( ClickEvent event )/*-{
                                $wnd.startTask($wnd.getFormValues($doc.getElementById("form-data")));
                            }-*/;
                        };
                        complete = new ClickHandler() {
                            @Override
                            public native void onClick( ClickEvent event )/*-{
                                $wnd.completeTask($wnd.getFormValues($doc.getElementById("form-data")));
                            }-*/;
                        };
                    }
                    saveTaskFlowPanel.addClickHandler(save);
                    wrapperFlowPanel.add( saveTaskFlowPanel );
                    FocusPanel completeTaskFlowPanel = new FocusPanel();
                    completeTaskFlowPanel.setStyleName( "option-button complete" );
                    completeTaskFlowPanel.setTitle( "Complete Task" );
                    completeTaskFlowPanel.addClickHandler(complete);
                    wrapperFlowPanel.add( completeTaskFlowPanel );
                    view.getOptionsDiv().add( wrapperFlowPanel );

                }
            }
        } ).getTaskDetails(view.getTaskId());
    }

    public void renderProcessForm(final String idProcess) {
        view.getNavBarUL().clear();
        formServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( String form ) {
                view.loadForm(form);
                final boolean modelerForm = view.isFormModeler();

                formCtx = form;

                dataServices.call( new RemoteCallback<ProcessSummary>() {
                    @Override
                    public void callback( ProcessSummary summary ) {
                        view.getTaskIdText().setText( "" );
                        view.getNameText().setText( summary.getName() );
                        FocusPanel wrapperFlowPanel = new FocusPanel();
                        wrapperFlowPanel.setStyleName( "wrapper" );
                        FocusPanel startFlowPanel = new FocusPanel();
                        startFlowPanel.setStyleName( "option-button start" );
                        startFlowPanel.setTitle( "Start Process" );
                        ClickHandler start;
                        if (modelerForm)
                            start =  new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent event) {
                                    view.submitStartProcessForm();
                                }
                            };
                        else
                            start = new ClickHandler() {
                                @Override
                                public native void onClick( ClickEvent event )/*-{
                                    $wnd.startProcess($wnd.getFormValues($doc.getElementById("form-data")));
                                }-*/;
                            };
                        startFlowPanel.addClickHandler(start);
                        wrapperFlowPanel.add( startFlowPanel );
                        view.getOptionsDiv().add( wrapperFlowPanel );


                    }
                } ).getProcessDesc(idProcess);
            }
        }).getFormDisplayProcess(idProcess);

    }

    public void onFormSubmitted(@Observes FormSubmittedEvent event) {
        if (event.isMine(formCtx)) {
            if (event.getContext().getErrors() == 0) {
                if(ACTION_START_PROCESS.equals(view.getAction())) {
                    startProcess();
                } else if (ACTION_SAVE_TASK.equals(view.getAction())) {
                    saveTaskState();
                } else if (ACTION_COMPLETE_TASK.equals(view.getAction())) {
                    completeTask();
                } else if (ACTION_TASK_COMMENTS.equals(view.getAction()) || ACTION_TASK_DETAILS.equals(view.getAction())) {
                    changeActionTab();
                }
            }
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Form();
    }

    @WorkbenchPartView
    public UberView<FormDisplayPopupPresenter> getView() {
        return view;
    }

    public void completeTask(String values) {
        final Map<String, String> params = getUrlParameters(values);
        final Map<String, Object> objParams = new HashMap<String, Object>(params);
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Form for Task Id: " + params.get("taskId") + " was completed!");
                close();
            }
        }).complete(Long.parseLong(params.get("taskId")), identity.getName(), objParams);

    }

    public void saveTaskState(final Long taskId, final Map<String, String> values) {
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                view.displayNotification("Task Id: " + taskId + " State was Saved! ContentId : " + contentId);
                renderTaskForm(taskId);
            }
        }).saveContent(taskId, values);
    }

    public void saveTaskState(String values) {
        final Map<String, String> params = getUrlParameters(values);
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                view.displayNotification("Task Id: " + params.get("taskId") + " State was Saved! ContentId : " + contentId);
                renderTaskForm(Long.parseLong(params.get("taskId").toString()));
            }
        }).saveContent(Long.parseLong(params.get("taskId").toString()), params);

    }

    public void startFormModelerTask(final Long taskId, final String identity) {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                startTask(taskId, identity);
            }
        }).clearContext(formCtx);
    }

    public void startTask(final Long taskId, final String identity) {
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Task Id: " + taskId + " was started!");
                renderTaskForm(taskId);
            }
        }).start(taskId, identity);
    }

    public void startTask( String values ) {
        final Map<String, String> params = getUrlParameters( values );
        taskServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Task Id: " + params.get( "taskId" ) + " was started!" );
                renderTaskForm( Long.parseLong(params.get( "taskId" ).toString()));
            }
        } ).start( Long.parseLong( params.get( "taskId" ).toString() ), identity.getName() );

    }

    protected void saveTaskState() {
        renderContextServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                view.displayNotification("Task Id: " + view.getTaskId() + " State was Saved! ContentId : " + contentId);
                renderTaskForm(Long.valueOf(view.getTaskId()));
            }
        }).saveTaskStateFromRenderContext(formCtx, Long.valueOf(view.getTaskId()));
    }

    protected void changeActionTab() {
        renderContextServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest(view.getAction());
                placeRequestImpl.addParameter("taskId", String.valueOf(view.getTaskId()));
                placeManager.goTo(placeRequestImpl);
            }
        }).saveTaskStateFromRenderContext(formCtx, Long.valueOf(view.getTaskId()), true);
    }

    protected void changeTab(String tabId) {
        close();
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(tabId);
        placeRequestImpl.addParameter("taskId", String.valueOf(view.getTaskId()));
        placeManager.goTo(placeRequestImpl);
    }

    protected void completeTask() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothin) {
                view.displayNotification("Form for Task Id: " + view.getTaskId() + " was completed!");
                close();
            }
        }).completeTaskFromContext(formCtx, Long.valueOf(view.getTaskId()), identity.getName());
    }

    protected void startProcess() {
        renderContextServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long processInstanceId) {
                view.displayNotification("Process Id: " + processInstanceId + " started!");
                processInstanceCreatedEvents.fire(new ProcessInstanceCreated());
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Process Instance Details");
                placeRequestImpl.addParameter("processInstanceId", processInstanceId.toString());
                placeRequestImpl.addParameter("processDefId", view.getProcessId());
                placeRequestImpl.addParameter("domainId", view.getDomainId());
                placeManager.goTo(placeRequestImpl);
            }
        }).startProcessFromRenderContext(formCtx, view.getDomainId(), view.getProcessId());
    }

    public void startProcess(String values) {
        final Map<String, String> params = getUrlParameters(values);

        sessionServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long processInstanceId) {
                view.displayNotification("Process Id: " + processInstanceId + " started!");
                processInstanceCreatedEvents.fire(new ProcessInstanceCreated());
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Process Instance Details");
                placeRequestImpl.addParameter("processInstanceId", processInstanceId.toString());
                placeRequestImpl.addParameter("processDefId", params.get("processId").toString());
                placeRequestImpl.addParameter("domainId", view.getDomainId());
                placeManager.goTo(placeRequestImpl);
            }
        }).startProcess(view.getDomainId(), params.get("processId").toString(), params);

    }

    // Set up the JS-callable signature as a global JS function.
    private native void publish( FormDisplayPopupPresenter fdp )/*-{
        $wnd.completeTask = function (from) {
            fdp.@org.jbpm.console.ng.ht.client.editors.taskform.FormDisplayPopupPresenter::completeTask(Ljava/lang/String;)(from);
        }

        $wnd.startTask = function (from) {
            fdp.@org.jbpm.console.ng.ht.client.editors.taskform.FormDisplayPopupPresenter::startTask(Ljava/lang/String;)(from);
        }

        $wnd.saveTaskState = function (from) {
            fdp.@org.jbpm.console.ng.ht.client.editors.taskform.FormDisplayPopupPresenter::saveTaskState(Ljava/lang/String;)(from);
        }

        $wnd.startProcess = function (from) {
            fdp.@org.jbpm.console.ng.ht.client.editors.taskform.FormDisplayPopupPresenter::startProcess(Ljava/lang/String;)(from);
        }
    }-*/;

    private native void publishGetFormValues() /*-{
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

    @OnReveal
    public void onReveal() {
        long taskId = Long.parseLong(place.getParameter("taskId", "-1").toString());
        String processId = place.getParameter("processId", "none").toString();
        String domainId = place.getParameter("domainId", "none").toString();
        if (taskId != -1) {
            view.setTaskId(taskId);
            renderTaskForm(taskId);
        } else if (!processId.equals("none")) {
            view.setProcessId(processId);
            view.setDomainId(domainId);
            renderProcessForm(processId);
        }
    }

    public void close() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                formCtx = null;
                closePlaceEvent.fire(new BeforeClosePlaceEvent(FormDisplayPopupPresenter.this.place));
            }
        }).clearContext(formCtx);
    }
}
