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

package org.jbpm.workbench.es.backend.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.jbpm.workbench.ks.integration.AbstractKieServerService;
import org.jbpm.workbench.es.model.RequestDetails;
import org.jbpm.workbench.es.service.ExecutorService;
import org.kie.server.api.model.admin.ExecutionErrorInstance;
import org.kie.server.api.model.instance.JobRequestInstance;
import org.kie.server.api.model.instance.RequestInfoInstance;
import org.kie.server.client.JobServicesClient;
import org.kie.server.client.admin.ProcessAdminServicesClient;

import static java.util.Optional.ofNullable;

@Service
@ApplicationScoped
public class RemoteExecutorServiceImpl extends AbstractKieServerService implements ExecutorService {

    @Override
    public RequestDetails getRequestDetails(String serverTemplateId,
                                            String deploymentId,
                                            Long requestId) {
        JobServicesClient jobClient = null;
        if (deploymentId != null && !deploymentId.isEmpty()) {
            jobClient = getClient(serverTemplateId,
                                  deploymentId,
                                  JobServicesClient.class);
        } else {
            jobClient = getClient(serverTemplateId,
                                  JobServicesClient.class);
        }
        Optional<RequestInfoInstance> request = ofNullable(jobClient.getRequestById(deploymentId,
                                                                                    requestId,
                                                                                    true,
                                                                                    true));

        return request.map(new RequestDetailsMapper()).orElse(null);
    }

    @Override
    public Long scheduleRequest(String serverTemplateId,
                                String commandName,
                                Date date,
                                Map<String, String> ctx) {
        JobServicesClient jobClient = getClient(serverTemplateId,
                                                JobServicesClient.class);
        HashMap<String, Object> data = new HashMap<>();
        if (ctx != null && !ctx.isEmpty()) {
            data = new HashMap<String, Object>(ctx);
        }
        JobRequestInstance jobRequest = JobRequestInstance.builder()
                .command(commandName)
                .data(data)
                .scheduledDate(date)
                .build();
        return jobClient.scheduleRequest((String)data.get("containerId"), jobRequest);
    }

    @Override
    public void cancelRequest(String serverTemplateId,
                              String deploymentId,
                              Long requestId) {
                
        JobServicesClient jobClient = getClient(serverTemplateId,
                                                JobServicesClient.class);
        
        jobClient.cancelRequest(deploymentId, requestId);
    
    }

    @Override
    public void requeueRequest(String serverTemplateId,
                               String deploymentId,
                               Long requestId) {
        JobServicesClient jobClient = getClient(serverTemplateId,
                                                    JobServicesClient.class);
        jobClient.requeueRequest(deploymentId, requestId);
        
    }

    @Override
    public void acknowledgeError(String serverTemplateId,
                                 String deploymentId,
                                 String... errorId) {
        ProcessAdminServicesClient processAdminServicesClient = getClient(serverTemplateId,
                                                                          ProcessAdminServicesClient.class);
        processAdminServicesClient.acknowledgeError(deploymentId,
                                                    errorId);
    }

    @Override
    public ExecutionErrorSummary getError(String serverTemplateId,
                                          String deploymentId,
                                          String errorId) {
        ProcessAdminServicesClient processAdminServicesClient = getClient(serverTemplateId,
                                                                          ProcessAdminServicesClient.class);
        Optional<ExecutionErrorInstance> executionErrorInstance = ofNullable(processAdminServicesClient.getError(deploymentId,
                                                                                                                 errorId));
        return executionErrorInstance.map(new ExecutionErrorSummaryMapper()).orElse(null);
    }
}