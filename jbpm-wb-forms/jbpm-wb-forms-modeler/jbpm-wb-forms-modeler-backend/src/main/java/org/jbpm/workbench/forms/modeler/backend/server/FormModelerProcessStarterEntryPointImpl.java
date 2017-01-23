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
package org.jbpm.workbench.forms.modeler.backend.server;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.forms.modeler.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.pr.service.ProcessService;
import org.jbpm.formModeler.api.client.FormRenderContextManager;

@Service
@ApplicationScoped
public class FormModelerProcessStarterEntryPointImpl implements FormModelerProcessStarterEntryPoint {
    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Inject
    private ProcessService processService;

    @Inject
    private TaskService taskService;

    @Override
    public Long startProcessFromRenderContext(String ctxUID, String serverTemplateId, String domainId, String processId, String correlationKey, Long parentProcessInstanceId) {
        Map<String, Object> params = formRenderContextManager.getFormRenderContext(ctxUID).getOutputData();
        formRenderContextManager.removeContext(ctxUID);

        return processService.startProcess(serverTemplateId, domainId, processId, correlationKey, params);

    }

    @Override
    public Long saveTaskStateFromRenderContext(String ctxUID, String serverTemplateId, String containerId, Long taskId, boolean clearStatus) {
        Map<String, Object> params = formRenderContextManager.getFormRenderContext(ctxUID).getOutputData();
        if (clearStatus) formRenderContextManager.removeContext(ctxUID);
        taskService.saveTaskContent(serverTemplateId, containerId, taskId, params);

        return -1l;
    }

    @Override
    public Long saveTaskStateFromRenderContext(String ctxUID, String serverTemplateId, String containerId, Long taskId) {
        return saveTaskStateFromRenderContext(ctxUID, serverTemplateId, containerId, taskId, false);
    }

    @Override
    public void completeTaskFromContext(String ctxUID, String serverTemplateId, String containerId, Long taskId) {
        Map<String, Object> params = formRenderContextManager.getFormRenderContext(ctxUID).getOutputData();
        formRenderContextManager.removeContext(ctxUID);
        taskService.completeTask(serverTemplateId, containerId, taskId, params);
    }

    @Override
    public void clearContext(String ctxUID) {
        formRenderContextManager.removeContext(ctxUID);
    }
}
