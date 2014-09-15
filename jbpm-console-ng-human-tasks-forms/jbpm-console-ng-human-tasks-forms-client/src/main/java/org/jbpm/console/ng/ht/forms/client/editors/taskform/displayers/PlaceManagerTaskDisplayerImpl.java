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

import org.kie.uberfire.client.forms.FormDisplayerView;
import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.util.PlaceManagerFormActivitySearcher;
import org.jbpm.console.ng.ht.model.events.RenderFormEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Map;

/**
 *
 * @author salaboy
 */
@Dependent
public class PlaceManagerTaskDisplayerImpl extends AbstractHumanTaskFormDisplayer {

    @Inject
    private PlaceManagerFormActivitySearcher placeManagerFormActivitySearcher;
    
    private FormDisplayerView fdp;

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
        Map<String, Object> params = fdp.getOutputMap();

        complete(params);
        close();
    }


    public void saveState() {
        Map<String, Object> params = fdp.getOutputMap();
        saveState(params);
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
        String taskName = (String)event.getParams().get("TaskName");
        String taskStatus = (String)event.getParams().get("taskStatus");
        if (taskName == null || taskName.equals("")) {
            return;
        }
        IsWidget widget = placeManagerFormActivitySearcher.findFormActivityWidget(taskName, null);

        formContainer.clear();
        if (widget != null) {
            formContainer.add(widget);
            fdp = (FormDisplayerView)widget;
            if(taskStatus.equals("Ready") || taskStatus.equals("Reserved")){
                fdp.setReadOnly(true);
            }else if(taskStatus.equals("InProgress")){
                fdp.setReadOnly(false);
            }
            fdp.setInputMap(event.getParams());
        }
    }

    @Override
    public void close() {
        super.close();
        placeManagerFormActivitySearcher.closeFormActivity();
    }

    @Override
    protected void completeFromDisplayer() {
        complete();
    }

    @Override
    protected void saveStateFromDisplayer() {
        saveState();
    }

}
