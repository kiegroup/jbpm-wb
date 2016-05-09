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
package org.jbpm.console.ng.ht.forms.backend.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gson.Gson;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.jbpm.formModeler.kie.services.form.ProcessDefinition;
import org.jbpm.formModeler.kie.services.form.TaskDefinition;
import org.jbpm.formModeler.kie.services.form.provider.AbstractFormProvider;
import org.kie.api.task.model.Task;

/**
 * @author salaboy
 */
@ApplicationScoped
public class PlaceManagerFormProvider extends AbstractFormProvider {

    private List<String> allActivities;

    @Inject
    private PlaceManagerActivityService pmas;

    @PostConstruct
    public void init() {
        this.allActivities = pmas.getAllActivities();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String render(String name, ProcessDefinition process, Map<String, Object> renderContext) {

        Map<String, Object> params = new HashMap<String, Object>(renderContext.size());
        for (String key : renderContext.keySet()) {
            if (!(renderContext.get(key) instanceof Task) && !key.equals("marshallerContext")) {
                Object paramValue = renderContext.get(key);
                if (paramValue != null) params.put(key, paramValue);
            }
        }

        if (process != null) {
            params.put("processId", process.getId());

            String destination = process.getId() + " Form";
            if (allActivities.contains(destination)) {
                return getFormRenderingInfo( destination, params );
            }
        }
        return "";
    }

    @Override
    public String render(String name, TaskDefinition task, ProcessDefinition process, Map<String, Object> renderContext) {
        Map<String, Object> params = new HashMap<String, Object>(renderContext.size());
        String taskName = (renderContext.get("TaskName") != null) ? (String) renderContext.get("TaskName") : "";
        for (String key : renderContext.keySet()) {
            if (!(renderContext.get(key) instanceof Task) && !key.equals("marshallerContext")) {
                Object paramValue = renderContext.get(key);
                if (paramValue != null) params.put(key, paramValue);
            }
        }
        if (task != null) {
            params.put("taskId", task.getId().toString());
            params.put("taskName", task.getName());
            params.put("taskDescription", task.getDescription());
            params.put("taskStatus", task.getStatus());
        }
        if (process != null) {
            params.put("processId", process.getId());
            params.put("processName", process.getName());
        }

        String destination = taskName + " Form";
        if (allActivities.contains(destination)) {
            return getFormRenderingInfo( destination, params );
        }
        return "";
    }

    protected String getFormRenderingInfo( String destination, Map<String, Object> params ) {
        Map result = new HashMap(  );
        result.put( "handler", "handledByPlaceManagerFormProvider" );
        result.put( "destination", destination );
        result.put( "params", params );
        Gson gson = new Gson();
        return gson.toJson( result );
    }
}
