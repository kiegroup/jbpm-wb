/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.ht.forms.backend.server;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.integration.AbstractKieServerService;
import org.jbpm.console.ng.ga.forms.service.FormServiceEntryPoint;
import org.jbpm.document.Document;
import org.jbpm.formModeler.kie.services.form.FormManagerService;
import org.jbpm.formModeler.kie.services.form.FormProvider;
import org.jbpm.formModeler.kie.services.form.TaskDefinition;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.DocumentServicesClient;
import org.kie.server.client.KieServicesException;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UIServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@ApplicationScoped
public class FormServiceEntryPointImpl extends AbstractKieServerService implements FormServiceEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(FormServiceEntryPointImpl.class);

    @Inject
    private FormManagerService formManagerService;

    private Set<FormProvider> providers;

    @Inject
    @Any
    private Instance<FormProvider> providersInjected;

    @PostConstruct
    public void prepare() {
        Set<FormProvider> providers = new TreeSet<FormProvider>(new Comparator<FormProvider>() {

            @Override
            public int compare(FormProvider o1, FormProvider o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
        for (FormProvider p : providersInjected) {
            providers.add(p);
        }

        this.providers = providers;
    }

    @Override
    public String getFormDisplayTask(String serverTemplateId, String domainId, long taskId) {
        String registrationKey = serverTemplateId + "@" + domainId + "@" + System.currentTimeMillis();

        DocumentServicesClient documentClient = getClient(serverTemplateId, domainId, DocumentServicesClient.class);

        // get form content
        UIServicesClient uiServicesClient = getClient(serverTemplateId, domainId, UIServicesClient.class);

        // get task with inputs and outputs
        UserTaskServicesClient taskClient = getClient(serverTemplateId, domainId, UserTaskServicesClient.class);
        TaskInstance task = taskClient.getTaskInstance(domainId, taskId, true, true, false);
        if (task == null) {
            throw new RuntimeException("No task found for id " + taskId);
        }

        TaskDefinition taskInstance = new TaskDefinition();
        taskInstance.setId(task.getId());
        taskInstance.setName(task.getName());
        taskInstance.setDescription(task.getDescription());
        taskInstance.setFormName(task.getFormName());
        taskInstance.setDeploymentId(registrationKey);
        taskInstance.setProcessId(task.getProcessId());

        taskInstance.setStatus(task.getStatus());

        // prepare render context
        Map<String, Object> renderContext = new HashMap<String, Object>();
        renderContext.put("task", taskInstance);
        renderContext.put("marshallerContext", new ContentMarshallerContext(null, getKieServicesClient(serverTemplateId, domainId).getClassLoader()));


        Map<String, Object> inputs = processData(documentClient, task.getInputData());
        if (inputs != null && !inputs.isEmpty()) {
            renderContext.put("inputs", inputs);
            renderContext.putAll(inputs);
        }

        Map<String, Object> outputs = processData(documentClient, task.getOutputData());
        if (outputs != null && !outputs.isEmpty()) {
            renderContext.put("outputs", outputs);
            renderContext.putAll(outputs);

            taskInstance.setOutputIncluded(true);
        }

        try {
            String formContent = uiServicesClient.getTaskForm(domainId, taskId);
            if (formContent != null) {
                formManagerService.registerForm(registrationKey, task.getFormName() +"-taskform.form", formContent);
            }
        } catch (KieServicesException e) {
            logger.debug("Unable to find process form in remote server due to {}", e.getMessage());
        }
        try {
            for (FormProvider provider : providers) {
                String template = provider.render(task.getName(), taskInstance, null, renderContext);
                if (template != null && !template.trim().isEmpty()) {
                    return template;
                }
            }
        } finally {
            formManagerService.unRegisterForms(registrationKey);
        }

        return "";
    }

    @Override
    public String getFormDisplayProcess(String serverTemplateId, String domainId, String processId) {

        ProcessServicesClient processClient = getClient(serverTemplateId, domainId, ProcessServicesClient.class);

        ProcessDefinition processDefinition = processClient.getProcessDefinition(domainId, processId);

        org.jbpm.formModeler.kie.services.form.ProcessDefinition processDesc = new org.jbpm.formModeler.kie.services.form.ProcessDefinition();
        processDesc.setId(processDefinition.getId());
        processDesc.setName(processDefinition.getName());
        processDesc.setPackageName(processDefinition.getPackageName());
        processDesc.setDeploymentId(serverTemplateId + "@" + processDefinition.getContainerId() + "@" + System.currentTimeMillis());

        Map<String, String> processData = processDefinition.getProcessVariables();

        if (processData == null) {
            processData = new HashMap<String, String>();
        }

        Map<String, Object> renderContext = new HashMap<String, Object>();
        renderContext.put("process", processDesc);
        renderContext.put("outputs", processData);
        renderContext.put("marshallerContext", new ContentMarshallerContext(null, getKieServicesClient(serverTemplateId, domainId).getClassLoader()));

        UIServicesClient uiServicesClient = getClient(serverTemplateId, domainId, UIServicesClient.class);

        try {
            String formContent = uiServicesClient.getProcessForm(domainId, processId);
            if (formContent != null) {

                formManagerService.registerForm(processDesc.getDeploymentId(), processDesc.getId() + "-taskform.form", formContent);

            }
        } catch (KieServicesException e) {
            logger.debug("Unable to find process form in remote server due to {}", e.getMessage());
        }
        try {

            for (FormProvider provider : providers) {
                String template = provider.render(processDesc.getName(), processDesc, renderContext);
                if (template != null && !template.trim().isEmpty()) {
                    return template;
                }
            }
        } finally {
            formManagerService.unRegisterForms(processDesc.getDeploymentId());
        }
        logger.warn("Unable to find form to render for process '{}'", processDesc.getName());
        return "";

    }

    protected Map<String, Object> processData(DocumentServicesClient documentClient, Map<String, Object> data) {

        if (data == null || data.isEmpty()) {
            return data;
        }

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof Document) {
                Document document = ((Document) entry.getValue());
                document.setLink(documentClient.getDocumentLink(document.getIdentifier()));
            }
        }

        return data;
    }

}
