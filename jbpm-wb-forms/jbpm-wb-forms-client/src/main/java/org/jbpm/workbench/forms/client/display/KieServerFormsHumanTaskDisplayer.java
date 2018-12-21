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
import javax.inject.Inject;

import org.jbpm.workbench.forms.client.display.task.AbstractHumanTaskFormDisplayer;
import org.jbpm.workbench.forms.display.FormDisplayerConfig;
import org.jbpm.workbench.forms.display.api.KieServerFormRenderingSettings;
import org.jbpm.workbench.ht.model.TaskKey;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.uberfire.mvp.Command;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;

@Dependent
public class KieServerFormsHumanTaskDisplayer extends AbstractHumanTaskFormDisplayer<KieServerFormRenderingSettings> {

    private Frame inlineFrame = GWT.create(Frame.class);

    @Inject
    public KieServerFormsHumanTaskDisplayer() {
    }

    @Override
    public void init(FormDisplayerConfig<TaskKey, KieServerFormRenderingSettings> config,
                     Command onCloseCommand,
                     Command onRefreshCommand) {
        super.init(config,
                   onCloseCommand,
                   onRefreshCommand);
    }

    @Override
    protected void initDisplayer() {
        completeTaskCallback(this);
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
        
        formContainer.add(div);
    }
    
    public void notifyAboutComplete() {
        taskRefreshed.fire(new TaskRefreshedEvent(serverTemplateId,
                                                  deploymentId,
                                                  taskId));
        close();
    }
    
    public static native void completeTaskCallback(KieServerFormsHumanTaskDisplayer dp)/*-{
        $wnd.afterTaskCompleted = function () {
            dp.@org.jbpm.workbench.forms.client.display.KieServerFormsHumanTaskDisplayer::notifyAboutComplete()();
        }
    }-*/;

    @Override
    protected void completeFromDisplayer() {
        
    }

    @Override
    protected void saveStateFromDisplayer() {
        
    }

    @Override
    protected void startFromDisplayer() {
        
    }

    @Override
    protected void claimFromDisplayer() {
        
    }

    @Override
    protected void releaseFromDisplayer() {
        
    }

    @Override
    protected void clearRenderingSettings() {
        
    }

    @Override
    public Class<KieServerFormRenderingSettings> getSupportedRenderingSettings() {
        return KieServerFormRenderingSettings.class;
    }

    @Override
    public boolean appendFooter() {
        return false;
    }
}
