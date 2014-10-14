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

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.util.PlaceManagerFormActivitySearcher;
import org.jbpm.console.ng.ht.model.events.RenderFormEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.enterprise.event.Event;
import org.kie.uberfire.client.forms.GetFormParamsEvent;
import org.kie.uberfire.client.forms.RequestFormParamsEvent;
import org.kie.uberfire.client.forms.SetFormParamsEvent;

/**
 *
 * @author salaboy
 */
@Dependent
public class PlaceManagerStartProcessDisplayerImpl extends AbstractStartProcessFormDisplayer {

    @Inject
    private Caller<KieSessionEntryPoint> sessionServices;

    @Inject
    private PlaceManagerFormActivitySearcher placeManagerFormActivitySearcher;
    
    @Inject
    private Event<SetFormParamsEvent> setFormParamsEvent;
    
    @Inject
    private Event<RequestFormParamsEvent> requestFormParamsEvent;

    public PlaceManagerStartProcessDisplayerImpl() {

    }
    

    @Override
    protected void initDisplayer() {
        
    }

    @Override
    public boolean supportsContent(String content) {
        return content.contains("handledByPlaceManagerFormProvider");
    }

    @Override
    public int getPriority() {
        return 2;
    }

    public void onFormRender(@Observes RenderFormEvent event) {
        String processId = (String)event.getParams().get("processId");
        if (processId == null || processId.equals("") || !event.getParams().get("TaskName").equals("")) {
            return;
        }
        
        formContainer.setWidth("100%");
        formContainer.setHeight("400px");
        placeManagerFormActivitySearcher.findFormActivityWidget(processId, null, formContainer);
        setFormParamsEvent.fire(new SetFormParamsEvent(event.getParams(), false));
            
       
    }

    @Override
    public void close() {
        super.close();
    }

    public void startProcess() {
        requestFormParamsEvent.fire(new RequestFormParamsEvent());
    }
    
    public void startProcessCallback(@Observes GetFormParamsEvent event){
        sessionServices.call(getStartProcessRemoteCallback(), getUnexpectedErrorCallback())
                .startProcess(deploymentId, processDefId, event.getParams());
    }

    @Override
    protected void startProcessFromDisplayer() {
        startProcess();
    }

}
