/*
 * Copyright 2012 JBoss by Red Hat.
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


import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ht.forms.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.formModeler.api.client.FormRenderContextManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@Service
@ApplicationScoped
public class FormModelerProcessStarterEntryPointImpl implements FormModelerProcessStarterEntryPoint {
    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Inject
    private KieSessionEntryPoint kieSessionEntryPoint;

    @Inject
    private TaskServiceEntryPoint taskServiceEntryPoint;

    public Long startProcessFromRenderContext(String ctxUID, String domainId, String processId) {
        Map params = formRenderContextManager.getFormRenderContext(ctxUID).getOutputData();
        formRenderContextManager.removeContext(ctxUID);
        return kieSessionEntryPoint.startProcess(domainId, processId, params);
    }

    @Override
    public Long saveTaskStateFromRenderContext(String ctxUID, Long taskId, boolean clearStatus) {
        Map params = formRenderContextManager.getFormRenderContext(ctxUID).getOutputData();
        if (clearStatus) formRenderContextManager.removeContext(ctxUID);
        return taskServiceEntryPoint.saveContent(taskId, params);
    }

    @Override
    public Long saveTaskStateFromRenderContext(String ctxUID, Long taskId) {
        return saveTaskStateFromRenderContext(ctxUID, taskId, false);
    }

    @Override
    public void completeTaskFromContext(String ctxUID, Long taskId, String identityName) {
        Map params = formRenderContextManager.getFormRenderContext(ctxUID).getOutputData();
        formRenderContextManager.removeContext(ctxUID);
        taskServiceEntryPoint.complete(taskId,  identityName, params);
    }

    @Override
    public void clearContext(String ctxUID) {
        formRenderContextManager.removeContext(ctxUID);
    }
}
