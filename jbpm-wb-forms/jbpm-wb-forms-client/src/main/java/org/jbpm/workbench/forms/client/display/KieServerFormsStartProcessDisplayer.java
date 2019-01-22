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

package org.jbpm.workbench.forms.client.display;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.workbench.forms.client.display.process.AbstractStartProcessFormDisplayer;
import org.jbpm.workbench.forms.client.i18n.Constants;
import org.jbpm.workbench.forms.display.FormDisplayerConfig;
import org.jbpm.workbench.forms.display.api.KieServerFormRenderingSettings;
import org.jbpm.workbench.pr.events.NewCaseInstanceEvent;
import org.jbpm.workbench.pr.events.NewProcessInstanceEvent;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class KieServerFormsStartProcessDisplayer extends AbstractStartProcessFormDisplayer<KieServerFormRenderingSettings> {

    @Inject
    protected Event<NewCaseInstanceEvent> newCaseInstanceEvent;
    
    private Frame inlineFrame = GWT.create(Frame.class);
    

    @Override
    public Class<KieServerFormRenderingSettings> getSupportedRenderingSettings() {
        return KieServerFormRenderingSettings.class;
    }

    @Override
    protected void initDisplayer() {
        startProcessCallback(this);
        startCaseCallback(this);
    }
    
    @Override
    public void init(FormDisplayerConfig<ProcessDefinitionKey, KieServerFormRenderingSettings> config,
                     Command onClose,
                     Command onRefreshCommand) {
        initConfigs(config,
                    onClose,
                    onRefreshCommand);

        container.clear();
        formContainer.clear();
        footerButtons.clear();

        container.add(formContainer);
        
        initDisplayer();

        formContainer.add(getFormWidget());
    }

    @Override
    public IsWidget getFormWidget() {                
        
        inlineFrame.setWidth("100%");
        inlineFrame.setHeight("100%");
        inlineFrame.getElement().setPropertyBoolean("webkitallowfullscreen",
                                                    true);
        inlineFrame.getElement().setPropertyBoolean("mozallowfullscreen",
                                                    true);
        inlineFrame.getElement().setPropertyBoolean("allowfullscreen",
                                                    true);
        inlineFrame.getElement().getStyle().setBorderWidth(0,
                                                           Style.Unit.PX);
        inlineFrame.getElement().getStyle().setPosition(Position.ABSOLUTE);
        inlineFrame.getElement().getStyle().setTop(0, Unit.PX);
        inlineFrame.getElement().getStyle().setLeft(0, Unit.PX);
        
        inlineFrame.setUrl(renderingSettings.getUrl());
        
        FlowPanel div = GWT.create(FlowPanel.class);
        div.getElement().getStyle().setPosition(Position.RELATIVE);
        div.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        div.getElement().getStyle().setPaddingTop(40, Unit.PCT);
        div.add(inlineFrame);
        return div;
    }

    @Override
    public void startProcessFromDisplayer() {
        
    }
    
    public void notifyAboutStartProcess(String id) {
        Long processInstanceId = Long.parseLong(id);
        newProcessInstanceEvent.fire(new NewProcessInstanceEvent(serverTemplateId,
                                                                 deploymentId,
                                                                 processInstanceId,
                                                                 processDefId,
                                                                 processName));
        final String message = Constants.INSTANCE.ProcessStarted(processInstanceId.longValue());
        notificationEvent.fire(new NotificationEvent(message,
                                                     NotificationEvent.NotificationType.SUCCESS));
        close();
    }
    
   public void notifyAboutStartCase(String id) {
       newCaseInstanceEvent.fire(new NewCaseInstanceEvent(serverTemplateId,
                                                          deploymentId,
                                                          id,
                                                          processDefId,
                                                          processName));
       final String message = Constants.INSTANCE.CaseStarted(id);
       notificationEvent.fire(new NotificationEvent(message,
                                                    NotificationEvent.NotificationType.SUCCESS));

       close();
   }
    
    public static native void startProcessCallback(KieServerFormsStartProcessDisplayer dp)/*-{
        $wnd.afterProcessStarted = function (processInstanceId) {
            dp.@org.jbpm.workbench.forms.client.display.KieServerFormsStartProcessDisplayer::notifyAboutStartProcess(Ljava/lang/String;)(processInstanceId);
        }
    }-*/;
    
    public static native void startCaseCallback(KieServerFormsStartProcessDisplayer dp)/*-{
        $wnd.afterCaseStarted = function (caseId) {
            dp.@org.jbpm.workbench.forms.client.display.KieServerFormsStartProcessDisplayer::notifyAboutStartCase(Ljava/lang/String;)(caseId);
        }
    }-*/;

    @Override
    public boolean appendFooter() {
        return false;
    }
}
