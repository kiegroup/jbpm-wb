/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.pr.forms.client.display.displayers.process;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ga.forms.display.FormDisplayerConfig;
import org.jbpm.console.ng.ga.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.gc.forms.client.display.displayers.util.ActionRequest;
import org.jbpm.console.ng.gc.forms.client.display.displayers.util.JSNIHelper;
import org.jbpm.console.ng.pr.forms.client.i18n.Constants;
import org.jbpm.console.ng.pr.forms.client.display.process.api.StartProcessFormDisplayer;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractStartProcessFormDisplayer implements StartProcessFormDisplayer {

    public static final String ACTION_START_PROCESS = "startProcess";

    protected Constants constants = GWT.create(Constants.class);

    protected FormPanel container = GWT.create( FormPanel.class );
    protected FlowPanel formContainer = GWT.create( FlowPanel.class );
    protected FlowPanel footerButtons = GWT.create( FlowPanel.class );

    protected TextBox correlationKey = GWT.create( TextBox.class );
    protected Label correlationKeyLabel;

    protected String formContent;

    protected String deploymentId;
    protected String processDefId;
    protected String processName;
    protected String opener;
    protected FormContentResizeListener resizeListener;
    protected Long parentProcessInstanceId;

    private Command onClose;

    private Command onRefresh;

    @Inject
    protected ErrorPopupPresenter errorPopup;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    protected Event<NewProcessInstanceEvent> newProcessInstanceEvent;

    protected Caller<KieSessionEntryPoint> sessionServices;

    @Inject
    protected JSNIHelper jsniHelper;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @PostConstruct
    protected void init() {
        container.getElement().setId("form-data");
    }

    @Override
    public void init(FormDisplayerConfig<ProcessDefinitionKey> config, Command onClose, Command onRefreshCommand, FormContentResizeListener resizeContentListener) {
        this.deploymentId = config.getKey().getDeploymentId();
        this.processDefId = config.getKey().getProcessId();
        this.formContent = config.getFormContent();
        this.opener = config.getFormOpener();
        this.onClose = onClose;
        this.onRefresh = onRefreshCommand;
        this.resizeListener = resizeContentListener;

        container.clear();
        formContainer.clear();
        footerButtons.clear();

        container.add(formContainer);

        correlationKey = new TextBox();


        Button startButton = new Button(constants.Submit(), new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                startProcessFromDisplayer();
            }
        });
        startButton.setType(ButtonType.PRIMARY);
        footerButtons.add(startButton);

        dataServices.call(new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback(ProcessSummary summary) {
                processName = summary.getProcessDefName();
                FocusPanel wrapperFlowPanel = new FocusPanel();
                wrapperFlowPanel.setStyleName("wrapper form-actions");

                if (opener != null) {
                    injectEventListener(AbstractStartProcessFormDisplayer.this);
                }

                initDisplayer();

                doResize();
            }
        }).getProcessDesc(deploymentId, processDefId);
    }

    protected abstract void initDisplayer();

    public void doResize() {
        if (resizeListener != null) resizeListener.resize(formContainer.getOffsetWidth(), formContainer.getOffsetHeight());
    }

    protected ErrorCallback<Message> getUnexpectedErrorCallback() {
        return new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                String notification = Constants.INSTANCE.UnexpectedError( throwable.getMessage() );
                errorPopup.showMessage(notification);
                jsniHelper.notifyErrorMessage(opener, notification);
                return true;
            }
        };
    }

    @Override
    public Panel getContainer() {
        return container;
    }

    @Override
    public IsWidget getFooter() {
        return footerButtons;
    }

    @Override
    public void startProcess(Map<String, Object> params) {
        if( parentProcessInstanceId > 0 ){
            sessionServices.call(getStartProcessRemoteCallback(), getUnexpectedErrorCallback())
                    .startProcess(deploymentId, processDefId, correlationKey.getValue(), params, parentProcessInstanceId);

        } else {
            sessionServices.call(getStartProcessRemoteCallback(), getUnexpectedErrorCallback())
                    .startProcess(deploymentId, processDefId, correlationKey.getValue(), params);
        }
    }

    protected RemoteCallback<Long> getStartProcessRemoteCallback() {
        return new RemoteCallback<Long>() {
            @Override
            public void callback(Long processInstanceId) {
                newProcessInstanceEvent.fire(new NewProcessInstanceEvent(deploymentId, processInstanceId, processDefId, processName, 1));
                final String message = Constants.INSTANCE.ProcessStarted( processInstanceId );
                jsniHelper.notifySuccessMessage(opener, message );
                notificationEvent.fire( new NotificationEvent( message, NotificationEvent.NotificationType.SUCCESS ) );
                close();
            }
        };
    }

    @Override
    public void addOnCloseCallback(Command callback) {
        this.onClose = callback;
    }

    @Override
    public void addOnRefreshCallback(Command callback) {
        this.onRefresh = callback;
    }

    @Override
    public void addResizeFormContent(FormContentResizeListener resizeListener) {
        this.resizeListener = resizeListener;
    }

    @Override
    public void close() {
        if (this.onClose != null) {
            this.onClose.execute();
        }
        clearStatus();
    }

    protected void clearStatus() {
        formContent = null;
        opener = null;
        deploymentId = null;
        processDefId = null;
        processName = null;

        container.clear();
        formContainer.clear();
        footerButtons.clear();

        onClose = null;
        onRefresh = null;
        resizeListener = null;
    }


    protected void eventListener(String origin, String request) {
        if (origin == null || !origin.endsWith("//" + opener)) {
            return;
        }

        ActionRequest actionRequest = JsonUtils.safeEval(request);

        if (ACTION_START_PROCESS.equals(actionRequest.getAction())) {
            startProcessFromDisplayer();
        }
    }

    private native void injectEventListener(AbstractStartProcessFormDisplayer fdp) /*-{
        function postMessageListener(e) {
            fdp.@org.jbpm.console.ng.pr.forms.client.display.displayers.process.AbstractStartProcessFormDisplayer::eventListener(Ljava/lang/String;Ljava/lang/String;)(e.origin, e.data);
        }

        if ($wnd.addEventListener) {
            $wnd.addEventListener("message", postMessageListener, false);
        } else {
            $wnd.attachEvent("onmessage", postMessageListener, false);
        }
    }-*/;

    @Override
    public String getOpener() {
        return opener;
    }

    protected String getCorrelationKey() {
        return correlationKey.getText();
    }

    public Long getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(Long parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    @Inject
    public void setSessionServices( final Caller<KieSessionEntryPoint> sessionServices ) {
        this.sessionServices = sessionServices;
    }
}
