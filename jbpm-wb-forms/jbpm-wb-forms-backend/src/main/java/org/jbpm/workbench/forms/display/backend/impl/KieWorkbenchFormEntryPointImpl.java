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

package org.jbpm.workbench.forms.display.backend.impl;

import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.forms.display.backend.provider.ProcessFormsValuesProcessor;
import org.jbpm.workbench.forms.display.backend.provider.TaskFormValuesProcessor;
import org.jbpm.workbench.forms.display.service.KieWorkbenchFormsEntryPoint;
import org.jbpm.workbench.pr.service.ProcessService;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;

@Service
@Dependent
public class KieWorkbenchFormEntryPointImpl implements KieWorkbenchFormsEntryPoint {

    private ProcessService processService;

    private TaskService taskService;

    private ProcessFormsValuesProcessor processRenderingSettingsInterpreter;

    private TaskFormValuesProcessor taskRenderingSettingsInterpreter;

    private BackendFormRenderingContextManager contextManager;

    @Inject
    public KieWorkbenchFormEntryPointImpl( ProcessService processService,
                                           TaskService taskService,
                                           ProcessFormsValuesProcessor processRenderingSettingsInterpreter,
                                           TaskFormValuesProcessor taskRenderingSettingsInterpreter,
                                           BackendFormRenderingContextManager contextManager ) {
        this.processService = processService;
        this.taskService = taskService;
        this.processRenderingSettingsInterpreter = processRenderingSettingsInterpreter;
        this.taskRenderingSettingsInterpreter = taskRenderingSettingsInterpreter;
        this.contextManager = contextManager;
    }

    @Override
    public Long startProcessFromRenderContext( Long timestamp,
                                               Map<String, Object> formData, String serverTemplateId,
                                               String containerId,
                                               String processId,
                                               String correlationKey ) {

        Map<String, Object> data = processRenderingSettingsInterpreter.generateRuntimeValuesMap( timestamp, formData );
        clearContext( timestamp );
        return processService.startProcess( serverTemplateId, containerId, processId, correlationKey, data );
    }

    @Override
    public void saveTaskStateFromRenderContext( Long timestamp,
                                                Map<String, Object> formData,
                                                String serverTemplateId,
                                                String containerId,
                                                Long taskId ) {
        Map<String, Object> data = taskRenderingSettingsInterpreter.generateRuntimeValuesMap( timestamp, formData );
        clearContext( timestamp );
        taskService.saveTaskContent( serverTemplateId, containerId, taskId, data );
    }


    @Override
    public void completeTaskFromContext( Long timestamp,
                                         Map<String, Object> formData,
                                         String serverTemplateId,
                                         String containerId,
                                         Long taskId ) {
        Map<String, Object> data = taskRenderingSettingsInterpreter.generateRuntimeValuesMap( timestamp, formData );
        clearContext( timestamp );
        taskService.completeTask( serverTemplateId, containerId, taskId, data );
    }

    @Override
    public void clearContext( long timestamp ) {
        contextManager.removeContext( timestamp );
    }
}
