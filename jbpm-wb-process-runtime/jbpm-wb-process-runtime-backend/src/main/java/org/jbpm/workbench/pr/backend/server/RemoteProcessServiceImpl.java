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

package org.jbpm.workbench.pr.backend.server;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.ks.integration.AbstractKieServerService;
import org.jbpm.workbench.pr.backend.server.model.RemoteCorrelationKey;
import org.jbpm.workbench.pr.service.ProcessService;
import org.kie.internal.process.CorrelationKey;
import org.kie.server.client.ProcessServicesClient;

@Service
@ApplicationScoped
public class RemoteProcessServiceImpl extends AbstractKieServerService implements ProcessService {

    @Override
    public void abortProcessInstance(String serverTemplateId,
                                     String containerId,
                                     Long processInstanceId) {
        ProcessServicesClient client = getClient(serverTemplateId,
                                                 containerId,
                                                 ProcessServicesClient.class);

        client.abortProcessInstance(containerId,
                                    processInstanceId);
    }

    @Override
    public void abortProcessInstances(String serverTemplateId,
                                      Map<String, List<Long>> containerInstances) {
        ProcessServicesClient client = getClient(serverTemplateId,
                                                 ProcessServicesClient.class);
        containerInstances.forEach((container, instances) ->
                                           client.abortProcessInstances(container,
                                                                        instances)
        );
    }

    @Override
    public Long startProcess(String serverTemplateId,
                             String containerId,
                             String processId,
                             String correlationKey,
                             Map<String, Object> params) {
        ProcessServicesClient client = getClient(serverTemplateId,
                                                 containerId,
                                                 ProcessServicesClient.class);

        if (correlationKey != null && !correlationKey.isEmpty()) {

            CorrelationKey actualCorrelationKey = new RemoteCorrelationKey(correlationKey);

            return client.startProcess(containerId,
                                       processId,
                                       actualCorrelationKey,
                                       params);
        }

        return client.startProcess(containerId,
                                   processId,
                                   params);
    }

    @Override
    public List<String> getAvailableSignals(String serverTemplateId,
                                            String containerId,
                                            Long processInstanceId) {
        ProcessServicesClient client = getClient(serverTemplateId,
                                                 containerId,
                                                 ProcessServicesClient.class);

        return client.getAvailableSignals(containerId,
                                          processInstanceId);
    }

    @Override
    public void signalProcessInstance(String serverTemplateId,
                                      String containerId,
                                      Long processInstanceId,
                                      String signal,
                                      Object event) {
        ProcessServicesClient client = getClient(serverTemplateId,
                                                 containerId,
                                                 ProcessServicesClient.class);

        client.signalProcessInstance(containerId,
                                     processInstanceId,
                                     signal,
                                     event);
    }

    @Override
    public void signalProcessInstances(String serverTemplateId,
                                       List<String> containers,
                                       List<Long> processInstanceId,
                                       String signal,
                                       Object event) {
        if (new HashSet<String>(containers).size() == 1) {
            ProcessServicesClient client = getClient(serverTemplateId,
                                                     containers.get(0),
                                                     ProcessServicesClient.class);
            client.signalProcessInstances(containers.get(0),
                                          processInstanceId,
                                          signal,
                                          event);
        } else {
            for (int i = 0; i < processInstanceId.size(); i++) {
                ProcessServicesClient client = getClient(serverTemplateId,
                                                         containers.get(i),
                                                         ProcessServicesClient.class);
                client.signalProcessInstance(containers.get(i),
                                             processInstanceId.get(i),
                                             signal,
                                             event);
            }
        }
    }

    @Override
    public void setProcessVariable(String serverTemplateId,
                                   String containerId,
                                   long processInstanceId,
                                   String variableName,
                                   String value) {
        ProcessServicesClient client = getClient(serverTemplateId,
                                                 containerId,
                                                 ProcessServicesClient.class);

        client.setProcessVariable(containerId,
                                  processInstanceId,
                                  variableName,
                                  value);
    }
}