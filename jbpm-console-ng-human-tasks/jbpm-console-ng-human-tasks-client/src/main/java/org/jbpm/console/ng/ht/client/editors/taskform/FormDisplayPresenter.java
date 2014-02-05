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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.bus.client.api.messaging.Message;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.EditPanelEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskStyleEvent;
import org.jbpm.console.ng.ht.model.fb.events.FormRenderedEvent;
import org.jbpm.console.ng.ht.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.console.ng.ht.service.FormServiceEntryPoint;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.kie.workbench.common.widgets.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchScreen(identifier = "Form Display")
public class FormDisplayPresenter {
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
    private Event<TaskRefreshedEvent> taskRefreshed;
    
    @Inject
    private Event<NewProcessInstanceEvent> newProcessInstanceEvent;
    
    @Inject
    private Event<EditPanelEvent> editPanelEvent;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    private String formCtx;
    
    private long currentTaskId = 0;
    
    private String currentProcessId;
    
    private String currentDomainId;
    
    @Inject
    private Event<TaskStyleEvent> taskStyleEvent;
    

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

        view.loadForm(form);

        final boolean modelerForm = view.isFormModeler();

        formCtx = form;

        taskServices.call( new RemoteCallback<TaskSummary>() {
            @Override
            public void callback( final TaskSummary task ) {
                view.getOptionsDiv().clear();
                FlowPanel wrapperFlowPanel = new FlowPanel();
                wrapperFlowPanel.setStyleName( "wrapper form-actions" );
                view.getOptionsDiv().add( wrapperFlowPanel );
                
                if ( task.getStatus().equals( "Ready" ) ) {
                    ClickHandler click = new ClickHandler() {
                            @Override
                            public native void onClick( ClickEvent event )/*-{
                                $wnd.claimTask($wnd.getFormValues($doc.getElementById("form-data")));
                            }-*/;
                        };
                    Button claimButton = new Button();
                    claimButton.setType(ButtonType.PRIMARY);
                    claimButton.setText(constants.Claim());
                    claimButton.addClickHandler(click);
                    wrapperFlowPanel.add( claimButton );
                    view.getOptionsDiv().add( wrapperFlowPanel );
                }
                
                if ( task.getStatus().equals( "Reserved" ) && task.getActualOwner().equals(identity.getName()) ) {
                    
                    ClickHandler click, release;
                     release = new ClickHandler() {
                            @Override
                            public native void onClick( ClickEvent event )/*-{
                                $wnd.releaseTask($wnd.getFormValues($doc.getElementById("form-data")));
                            }-*/;
                    };
                    
                    if (modelerForm)
                        click = new ClickHandler() {
                            @Override
                            public void onClick( ClickEvent event ) {
                                if (view.isFormModeler()) startFormModelerTask(currentTaskId, identity.getName());
                                else startTask(currentTaskId, identity.getName());
                            };
                        };
                    else
                        click = new ClickHandler() {
                            @Override
                            public native void onClick( ClickEvent event )/*-{
                                $wnd.startTask($wnd.getFormValues($doc.getElementById("form-data")));
                            }-*/;
                        };
                    
                    Button releaseButton = new Button();
                    releaseButton.setText(constants.Release());
                    releaseButton.addClickHandler(release);
                    wrapperFlowPanel.add( releaseButton );
                    
                    Button startButton = new Button();
                    startButton.setType(ButtonType.PRIMARY);
                    startButton.setText(constants.Start());
                    startButton.addClickHandler(click);
                    wrapperFlowPanel.add( startButton );
                    
                    
                    view.getOptionsDiv().add( wrapperFlowPanel );
                } else if ( task.getStatus().equals( "InProgress" ) && task.getActualOwner().equals(identity.getName()) ) {
                    
                    ClickHandler save, complete, release;
                    
                    release = new ClickHandler() {
                            @Override
                            public native void onClick( ClickEvent event )/*-{
                                $wnd.releaseTask($wnd.getFormValues($doc.getElementById("form-data")));
                            }-*/;
                    };
                    
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
                    
                    Button saveButton = new Button();
                    saveButton.setText(constants.Save());
                    saveButton.addClickHandler(save);
                    wrapperFlowPanel.add( saveButton );
                    
                    Button releaseButton = new Button();
                    releaseButton.setText(constants.Release());
                    releaseButton.addClickHandler(release);
                    wrapperFlowPanel.add( releaseButton );
                    
                    Button completeButton = new Button();
                    completeButton.setType(ButtonType.PRIMARY);
                    completeButton.setText(constants.Complete());
                    completeButton.addClickHandler(complete);
                    
                    wrapperFlowPanel.add( completeButton );
                    view.getOptionsDiv().add( wrapperFlowPanel );
                    taskStyleEvent.fire( new TaskStyleEvent( task.getId() ) );
                }
            }
        }, new ErrorCallback<Message>() {
               @Override
               public boolean error( Message message, Throwable throwable ) {
                   ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                   return true;
               }
           } ).getTaskDetails(currentTaskId);
    }

    public void renderProcessForm() {
        
        formServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( String form ) {
                view.loadForm(form);
                final boolean modelerForm = view.isFormModeler();

                formCtx = form;

                dataServices.call( new RemoteCallback<ProcessSummary>() {
                    @Override
                    public void callback( ProcessSummary summary ) {
                        FocusPanel wrapperFlowPanel = new FocusPanel();
                        wrapperFlowPanel.setStyleName( "wrapper form-actions" );
                       
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
                        Button startButton = new Button();
                        startButton.setText(constants.Start());
                        startButton.addClickHandler(start);
                    
                        wrapperFlowPanel.add( startButton );
                        view.getOptionsDiv().add( wrapperFlowPanel );
                    }
                } ).getProcessDesc(currentProcessId);
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       }).getFormDisplayProcess(currentDomainId, currentProcessId);
        
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
    public UberView<FormDisplayPresenter> getView() {
        return view;
    }

    public void completeTask(String values) {
        final Map<String, String> params = getUrlParameters(values);
        final Map<String, Object> objParams = new HashMap<String, Object>(params);
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Form for Task Id: " + params.get("taskId") + " was completed!");
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                dispose();
                
                taskServices.call(new RemoteCallback<Boolean>() {
                    @Override
                    public void callback(Boolean response) {
                        if( !response ){
                            editPanelEvent.fire( new EditPanelEvent( currentTaskId ) );
                        }
                    }
                }).existInDatabase(currentTaskId);
                
            }
        }, new ErrorCallback<Message>() {
          @Override
          public boolean error( Message message, Throwable throwable ) {
              ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
              return true;
          }
      }).complete(Long.parseLong(params.get("taskId")), identity.getName(), objParams);

    }

    public void saveTaskState(final Map<String, String> values) {
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                view.displayNotification("Task Id: " + currentTaskId + " State was Saved! ContentId : " + contentId);
                renderTaskForm();
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).saveContent(currentTaskId, values);
    }

    public void saveTaskState(String values) {
        final Map<String, String> params = getUrlParameters(values);
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                view.displayNotification("Task Id: " + params.get("taskId") + " State was Saved! ContentId : " + contentId);
                renderTaskForm();
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
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
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
            }
        }, new ErrorCallback<Message>() {
          @Override
          public boolean error( Message message, Throwable throwable ) {
              ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
              return true;
          }
      }).start(taskId, identity);
    }

    public void startTask( String values ) {
        final Map<String, String> params = getUrlParameters( values );
        taskServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Task Id: " + params.get( "taskId" ) + " was started!" );
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       } ).start( Long.parseLong( params.get( "taskId" ).toString() ), identity.getName() );

    }
    
    public void releaseTask( String values ) {
        taskServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Task Id: " + currentTaskId + " was released!" );
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       } ).release( currentTaskId, identity.getName() );

    }
    
    public void claimTask( String values ) {
        taskServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Task Id: " + currentTaskId + " was claimed!" );
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       } ).claim( currentTaskId, identity.getName() );

    }

    protected void saveTaskState() {
        renderContextServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                view.displayNotification("Task Id: " + currentTaskId + " State was Saved! ContentId : " + contentId);
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                renderTaskForm();
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       }).saveTaskStateFromRenderContext(formCtx, currentTaskId);
    }

    protected void changeActionTab() {
        renderContextServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                dispose();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest(view.getAction());
                placeRequestImpl.addParameter("taskId", String.valueOf(currentTaskId));
                placeManager.goTo(placeRequestImpl);
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       }).saveTaskStateFromRenderContext(formCtx, currentTaskId, true);
    }

    protected void changeTab(String tabId) {
        dispose();
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(tabId);
        placeRequestImpl.addParameter("taskId", String.valueOf(currentTaskId));
        placeManager.goTo(placeRequestImpl);
    }

    protected void completeTask() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Form for Task Id: " + currentTaskId + " was completed!");
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                dispose();
            }
        }, new ErrorCallback<Message>() {
               @Override
               public boolean error( Message message, Throwable throwable ) {
                   ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                   return true;
               }
           }).completeTaskFromContext(formCtx, currentTaskId, identity.getName());
    }

    protected void startProcess() {
        renderContextServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long processInstanceId) {
                view.displayNotification("Process Id: " + processInstanceId + " started!");
                newProcessInstanceEvent.fire(new NewProcessInstanceEvent(currentDomainId, processInstanceId, currentProcessId));
                close();
 
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       }).startProcessFromRenderContext(formCtx, currentDomainId, currentProcessId);
    }

    public void startProcess(String values) {
        final Map<String, String> params = getUrlParameters(values);

        sessionServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long processInstanceId) {
                view.displayNotification("Process Id: " + processInstanceId + " started!");
                newProcessInstanceEvent.fire(new NewProcessInstanceEvent(currentDomainId, processInstanceId, currentProcessId));
                close();
               

            }
        }, new ErrorCallback<Message>() {
             @Override
             public boolean error( Message message, Throwable throwable ) {
                 ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                 return true;
             }
         }).startProcess(currentDomainId, params.get("processId").toString(), params);

    }

    // Set up the JS-callable signature as a global JS function.
    private native void publish( FormDisplayPresenter fdp )/*-{
        $wnd.completeTask = function (from) {
            fdp.@org.jbpm.console.ng.ht.client.editors.taskform.FormDisplayPresenter::completeTask(Ljava/lang/String;)(from);
        }

        $wnd.startTask = function (from) {
            fdp.@org.jbpm.console.ng.ht.client.editors.taskform.FormDisplayPresenter::startTask(Ljava/lang/String;)(from);
        }
            
        $wnd.claimTask = function (from) {
            fdp.@org.jbpm.console.ng.ht.client.editors.taskform.FormDisplayPresenter::claimTask(Ljava/lang/String;)(from);
        }
            
        $wnd.releaseTask = function (from) {
            fdp.@org.jbpm.console.ng.ht.client.editors.taskform.FormDisplayPresenter::releaseTask(Ljava/lang/String;)(from);
        }

        $wnd.saveTaskState = function (from) {
            fdp.@org.jbpm.console.ng.ht.client.editors.taskform.FormDisplayPresenter::saveTaskState(Ljava/lang/String;)(from);
        }

        $wnd.startProcess = function (from) {
            fdp.@org.jbpm.console.ng.ht.client.editors.taskform.FormDisplayPresenter::startProcess(Ljava/lang/String;)(from);
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
        if(currentTaskId == event.getTaskId()){
            if (currentTaskId != -1) {
                renderTaskForm();
            } else if (!currentProcessId.equals("none")) {
                renderProcessForm();
            }
        }
    }
}
