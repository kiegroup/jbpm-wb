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
package org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers;

import org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.util.PlaceManagerFormActivitySearcher;
import org.jbpm.console.ng.ht.model.events.RenderFormEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Map;
import javax.enterprise.event.Event;
import org.uberfire.ext.widgets.common.client.forms.GetFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.RequestFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.SetFormParamsEvent;

/**
 *
 * @author salaboy
 */
@Dependent
public class PlaceManagerTaskDisplayerImpl extends AbstractHumanTaskFormDisplayer {

    @Inject
    private PlaceManagerFormActivitySearcher placeManagerFormActivitySearcher;

    @Inject
    private Event<SetFormParamsEvent> setFormParamsEvent;

    @Inject
    private Event<RequestFormParamsEvent> requestFormParamsEvent;

    public PlaceManagerTaskDisplayerImpl() {
    }

    @Override
    protected void initDisplayer() {

    }

    @Override
    public boolean supportsContent(String content) {
        return content.contains("handledByPlaceManagerFormProvider");
    }

    public void complete() {
        requestFormParamsEvent.fire(new RequestFormParamsEvent("completeTask"));
        close();
    }

    public void saveState() {
        requestFormParamsEvent.fire(new RequestFormParamsEvent("saveTask"));
    }

    @Override
    protected void startFromDisplayer() {
        start();
    }

    @Override
    protected void claimFromDisplayer() {
        claim();
    }

    @Override
    protected void releaseFromDisplayer() {
        release();
    }

    @Override
    public int getPriority() {
        return 2;
    }

    public void onFormRender(@Observes RenderFormEvent event) {
        String taskName = (String) event.getParams().get("TaskName");
        String taskStatus = (String) event.getParams().get("taskStatus");
        if (taskName == null || taskName.equals("")) {
            return;
        }

        formContainer.setWidth("100%");
        formContainer.setHeight("400px");
        placeManagerFormActivitySearcher.findFormActivityWidget(taskName, null, formContainer);

        if (taskStatus.equals("InProgress")) {
            setFormParamsEvent.fire(new SetFormParamsEvent(event.getParams(), false));
        } else {
            setFormParamsEvent.fire(new SetFormParamsEvent(event.getParams(), true));
        }

    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    protected void completeFromDisplayer() {
        requestFormParamsEvent.fire(new RequestFormParamsEvent("completeTask"));

    }

    private void completeOrSaveFromEvent(@Observes GetFormParamsEvent event) {
        if (event.getAction().equals("completeTask")) {
            complete(event.getParams());
        } else if (event.getAction().equals("saveTask")) {
            saveState(event.getParams());
        }
    }

    @Override
    protected void saveStateFromDisplayer() {
        requestFormParamsEvent.fire(new RequestFormParamsEvent("saveTask"));

    }

}
