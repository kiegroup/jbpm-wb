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

package org.jbpm.console.ng.es.backend.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.integration.AbstractKieServerService;
import org.jbpm.console.ng.es.model.RequestDetails;
import org.jbpm.console.ng.es.service.ExecutorService;
import org.kie.server.api.model.instance.JobRequestInstance;
import org.kie.server.api.model.instance.RequestInfoInstance;
import org.kie.server.client.JobServicesClient;

import static java.util.Optional.ofNullable;

@Service
@ApplicationScoped
public class RemoteExecutorServiceImpl extends AbstractKieServerService implements ExecutorService {

    @Override
    public RequestDetails getRequestDetails(String serverTemplateId, Long requestId) {
        JobServicesClient jobClient = getClient(serverTemplateId, JobServicesClient.class);

        Optional<RequestInfoInstance> request = ofNullable(jobClient.getRequestById(requestId, true, true));

        return request.map(new RequestDetailsMapper()).orElse(null);
    }

    @Override
    public Long scheduleRequest(String serverTemplateId, String commandName, Date date, Map<String, String> ctx) {
        JobServicesClient jobClient = getClient(serverTemplateId, JobServicesClient.class);
        HashMap<String, Object> data = new HashMap<>();
        if (ctx != null && !ctx.isEmpty()) {
            data = new HashMap<String, Object>(ctx);
        }
        JobRequestInstance jobRequest = JobRequestInstance.builder()
                .command(commandName)
                .data(data)
                .scheduledDate(date)
                .build();
        return jobClient.scheduleRequest(jobRequest);
    }

    @Override
    public void cancelRequest(String serverTemplateId, Long requestId) {
        JobServicesClient jobClient = getClient(serverTemplateId, JobServicesClient.class);
        jobClient.cancelRequest(requestId);
    }

    @Override
    public void requeueRequest(String serverTemplateId, Long requestId) {
        JobServicesClient jobClient = getClient(serverTemplateId, JobServicesClient.class);
        jobClient.requeueRequest(requestId);
    }

}