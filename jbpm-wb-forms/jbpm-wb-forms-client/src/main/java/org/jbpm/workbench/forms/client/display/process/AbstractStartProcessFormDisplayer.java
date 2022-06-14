/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.forms.client.display.process;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import org.gwtbootstrap3.client.shared.event.HideEvent;
import org.gwtbootstrap3.client.shared.event.HideHandler;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.forms.client.display.api.StartProcessFormDisplayer;
import org.jbpm.workbench.forms.client.i18n.Constants;
import org.jbpm.workbench.forms.display.FormDisplayerConfig;
import org.jbpm.workbench.forms.display.FormRenderingSettings;
import org.jbpm.workbench.pr.events.NewProcessInstanceEvent;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.service.ProcessService;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractStartProcessFormDisplayer<S extends FormRenderingSettings> implements StartProcessFormDisplayer<S> {

    public static final String ACTION_START_PROCESS = "startProcess";

    protected Constants constants = GWT.create(Constants.class);

    protected FormPanel container = GWT.create(FormPanel.class);
    protected FlowPanel formContainer = GWT.create(FlowPanel.class);
    protected FlowPanel footerButtons = GWT.create(FlowPanel.class);
    protected TextBox correlationKey = GWT.create(TextBox.class);

    protected S renderingSettings;

    protected String serverTemplateId;
    protected String deploymentId;
    protected String processDefId;
    protected String processName;
    protected Long parentProcessInstanceId;

    @Inject
    protected ErrorPopupPresenter errorPopup;

    @Inject
    protected Event<NewProcessInstanceEvent> newProcessInstanceEvent;

    protected Caller<ProcessService> processService;

    @Inject
    protected Event<NotificationEvent> notificationEvent;

    protected FormDisplayerConfig<ProcessDefinitionKey, S> config;

    private Command onClose;

    private Command onRefresh;

    @PostConstruct
    protected void init() {
        container.getElement().setId("form-data");
    }

    @Override
    public void init(FormDisplayerConfig<ProcessDefinitionKey, S> config,
                     Command onClose,
                     Command onRefreshCommand) {
        initConfigs(config,
                    onClose,
                    onRefreshCommand);

        container.clear();
        formContainer.clear();
        footerButtons.clear();

        container.add(formContainer);

        correlationKey = new TextBox();

        Button startButton = new Button(constants.Submit(),
                                        new ClickHandler() {
                                            @Override
                                            public void onClick(ClickEvent event) {
                                                startProcessFromDisplayer();
                                            }
                                        });
        startButton.setType(ButtonType.PRIMARY);
        footerButtons.add(startButton);
        
        FocusPanel wrapperFlowPanel = new FocusPanel();
        wrapperFlowPanel.setStyleName("wrapper form-actions");

        initDisplayer();

        initFormWithCorrelationKey();
    }

    protected void initFormWithCorrelationKey() {
        final PanelGroup accordion = new PanelGroup();
        accordion.setId(DOM.createUniqueId());

        accordion.add(new org.gwtbootstrap3.client.ui.Panel() {{
            final PanelCollapse collapse = new PanelCollapse() {{
                setIn(false);
                addHideHandler(new HideHandler() {
                    @Override
                    public void onHide(final HideEvent hideEvent) {
                        hideEvent.stopPropagation();
                    }
                });
                add(new PanelBody() {{
                    add(correlationKey);
                }});
            }};
            add(new PanelHeader() {{
                add(new Heading(HeadingSize.H4) {{
                    add(new Anchor() {{
                        setText(constants.Correlation_Key());
                        setDataToggle(Toggle.COLLAPSE);
                        setDataParent(accordion.getId());
                        setDataTargetWidget(collapse);
                    }});
                }});
            }});
            add(collapse);
        }});

        accordion.add(new org.gwtbootstrap3.client.ui.Panel() {{
            final PanelCollapse collapse = new PanelCollapse() {{
                setIn(true);
                addHideHandler(new HideHandler() {
                    @Override
                    public void onHide(final HideEvent hideEvent) {
                        hideEvent.stopPropagation();
                    }
                });
                add(new PanelBody() {{
                    add(getFormWidget());
                }});
            }};
            add(new PanelHeader() {{
                add(new Heading(HeadingSize.H4) {{
                    add(new Anchor() {{
                        setText(constants.Form());
                        setDataToggle(Toggle.COLLAPSE);
                        setDataParent(accordion.getId());
                        setDataTargetWidget(collapse);
                    }});
                }});
            }});
            add(collapse);
        }});

        formContainer.add(accordion);
    }

    public void initConfigs(FormDisplayerConfig<ProcessDefinitionKey, S> config,
                            Command onClose,
                            Command onRefreshCommand){
        this.config = config;
        this.serverTemplateId = config.getKey().getServerTemplateId();
        this.deploymentId = config.getKey().getDeploymentId();
        this.processDefId = config.getKey().getProcessId();
        this.processName = config.getKey().getProcessDefName();
        this.renderingSettings = config.getRenderingSettings();
        this.onClose = onClose;
        this.onRefresh = onRefreshCommand;
    }

    protected abstract void initDisplayer();

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
        processService.call(getStartProcessRemoteCallback())
                .startProcess(serverTemplateId,
                              deploymentId,
                              processDefId,
                              correlationKey.getValue(),
                              params);
    }

    protected RemoteCallback<Long> getStartProcessRemoteCallback() {
        return new RemoteCallback<Long>() {
            @Override
            public void callback(Long processInstanceId) {
                newProcessInstanceEvent.fire(new NewProcessInstanceEvent(serverTemplateId,
                                                                         deploymentId,
                                                                         processInstanceId,
                                                                         processDefId,
                                                                         processName));
                final String message = Constants.INSTANCE.ProcessStarted(processInstanceId);
                notificationEvent.fire(new NotificationEvent(message,
                                                             NotificationEvent.NotificationType.SUCCESS));
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

    public void refresh() {
        if (this.onRefresh != null) {
            this.onRefresh.execute();
        }
    }

    @Override
    public void close() {
        if (this.onClose != null) {
            this.onClose.execute();
        }
        clearStatus();
    }

    protected void clearStatus() {
        renderingSettings = null;
        deploymentId = null;
        processDefId = null;
        processName = null;

        container.clear();
        formContainer.clear();
        footerButtons.clear();

        onClose = null;
        onRefresh = null;
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
    public void setProcessService(Caller<ProcessService> processService) {
        this.processService = processService;
    }
}
