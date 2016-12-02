/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.ht.forms.modeler.client.editors.taskform.displayers.test;

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.ga.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.gc.forms.client.display.displayers.util.JSNIHelper;
import org.jbpm.console.ng.ht.forms.modeler.client.editors.taskform.displayers.FormModellerStartProcessDisplayerImpl;
import org.jbpm.console.ng.ht.forms.modeler.display.impl.FormModelerFormRenderingSettings;
import org.jbpm.console.ng.ht.forms.modeler.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.console.ng.pr.events.NewProcessInstanceEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;
import org.uberfire.workbench.events.NotificationEvent;

public class TestFormModellerStartProcessDisplayerImpl extends FormModellerStartProcessDisplayerImpl {

    public TestFormModellerStartProcessDisplayerImpl( FormRendererWidget formRenderer, Caller<FormModelerProcessStarterEntryPoint> renderContextServices ) {
        super( formRenderer, renderContextServices );
    }

    public void setRenderingSettings( FormModelerFormRenderingSettings settings ) {
        this.renderingSettings = settings;
    }

    public void setResizeListener( FormContentResizeListener listener ) {
        this.resizeListener = listener;
    }

    public void setNewProcessInstanceEvent( Event<NewProcessInstanceEvent> newProcessInstanceEvent ) {
        this.newProcessInstanceEvent = newProcessInstanceEvent;
    }

    public void setJSNIHelper( JSNIHelper JSNIHelper ) {
        this.jsniHelper = JSNIHelper;
    }

    public void setNotificationEvent( Event<NotificationEvent> notificationEvent ) {
        this.notificationEvent = notificationEvent;
    }
}
