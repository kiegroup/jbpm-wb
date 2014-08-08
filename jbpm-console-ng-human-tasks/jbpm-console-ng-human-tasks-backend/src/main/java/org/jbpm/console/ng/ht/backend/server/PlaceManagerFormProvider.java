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
package org.jbpm.console.ng.ht.backend.server;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jbpm.console.ng.ht.model.events.RenderFormEvent;
import org.jbpm.kie.services.impl.form.FormProvider;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.task.model.Task;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
public class PlaceManagerFormProvider implements FormProvider {

    @Inject
    private Event<RenderFormEvent> renderForm;
    
    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String render(String name, ProcessDefinition process, Map<String, Object> renderContext) {
        Map<String, String> params = new HashMap<String, String>(renderContext.size());
        for (String key : renderContext.keySet()) {
            if (!(renderContext.get(key) instanceof Task) && !key.equals("marshallerContext")) {
                params.put(key, renderContext.get(key).toString());
            } 
            
        }
        if(process != null){
            params.put("processId", process.getId());
        }
        renderForm.fire(new RenderFormEvent(name, params));
        return "";
    }

    @Override
    public String render(String name, Task task, ProcessDefinition process, Map<String, Object> renderContext) {
        Map<String, String> params = new HashMap<String, String>(renderContext.size());
        for (String key : renderContext.keySet()) {
            if (!(renderContext.get(key) instanceof Task) && !key.equals("marshallerContext")) {
                params.put(key, renderContext.get(key).toString());
            } 
        }
        if(task!=null){
            params.put("taskId", task.getId().toString());    
        }
        if(process != null){
            params.put("processId", process.getId());
        }
        renderForm.fire(new RenderFormEvent(name, params));
        return "";
    }

}
