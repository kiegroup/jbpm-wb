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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.integration.AbstractKieServerService;
import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestDetails;
import org.jbpm.console.ng.es.model.RequestKey;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.service.ExecutorService;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ga.service.GenericServiceEntryPoint;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.server.api.model.instance.JobRequestInstance;
import org.kie.server.api.model.instance.RequestInfoInstance;
import org.kie.server.client.JobServicesClient;
import org.uberfire.paging.PageResponse;

@Service
@ApplicationScoped
public class RemoteExecutorServiceImpl extends AbstractKieServerService implements ExecutorService,GenericServiceEntryPoint<RequestKey, RequestSummary> {

    @Override
    public RequestDetails getRequestDetails(String serverTemplateId, Long requestId) {
        JobServicesClient jobClient = getClient(serverTemplateId, JobServicesClient.class);

        RequestInfoInstance request = jobClient.getRequestById(requestId, true, true);

        RequestSummary summary = RequestSummaryHelper.adaptRequest(request);
        List<ErrorSummary> errors = RequestSummaryHelper.adaptErrorInstanceList(request.getErrors().getItems());
        List<RequestParameterSummary> params = RequestSummaryHelper.adaptInternalMap(request);
        return new RequestDetails(summary, errors, params);
    }

    @Override
    public Long scheduleRequest(String serverTemplateId, String commandName, Map<String, String> ctx) {
        JobServicesClient jobClient = getClient(serverTemplateId, JobServicesClient.class);
        HashMap<String, Object> data = new HashMap<>();
        if (ctx != null && !ctx.isEmpty()) {
            data = new HashMap<String, Object>(ctx);
        }
        JobRequestInstance jobRequest = JobRequestInstance.builder()
                .command(commandName)
                .data(data)
                .build();

        return jobClient.scheduleRequest(jobRequest);
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

    @Override
    public PageResponse<RequestSummary> getData(QueryFilter filter) {
        PageResponse<RequestSummary> response = new PageResponse<RequestSummary>();
        List<RequestSummary> requestSummarys = getRequests(filter);
        response.setStartRowIndex(filter.getOffset());
        response.setTotalRowSize(requestSummarys.size()-1);
        if(requestSummarys.size() > filter.getCount()){
            response.setTotalRowSizeExact(false);
        } else{
            response.setTotalRowSizeExact(true);
        }

        if (!requestSummarys.isEmpty() && requestSummarys.size() > (filter.getCount() + filter.getOffset())) {
            response.setPageRowList(new ArrayList<RequestSummary>(requestSummarys.subList(filter.getOffset(), filter.getOffset() + filter.getCount())));
            response.setLastPage(false);

        } else {
            response.setPageRowList(new ArrayList<RequestSummary>(requestSummarys));
            response.setLastPage(true);

        }
        return response;
    }

    private List<RequestSummary> getRequests(QueryFilter filter) {
        List<String> states = null;
        if (filter.getParams() != null) {
            states = (List<String>) filter.getParams().get("states");
        }

        Collection<RequestInfo> requestInfoList = null;
        if (states == null || states.isEmpty()) {
            states = new ArrayList<String>();
            states.add(STATUS.QUEUED.toString());
            states.add(STATUS.RUNNING.toString());
            states.add(STATUS.RETRYING.toString());
            states.add(STATUS.ERROR.toString());
            states.add(STATUS.DONE.toString());
            states.add(STATUS.CANCELLED.toString());
        }

        JobServicesClient jobClient = getClient((String)filter.getParams().get("serverTemplateId"), JobServicesClient.class);
        List<RequestInfoInstance> jobs = jobClient.getRequestsByStatus(states, 0, 100);


        List<RequestSummary> requestSummarys = new ArrayList<RequestSummary>(requestInfoList.size());
        for(RequestInfoInstance requestInfo : jobs){
            if (filter.getParams().get("textSearch") == null || ((String) filter.getParams().get("textSearch")).isEmpty()) {
                requestSummarys.add( RequestSummaryHelper.adaptRequest( requestInfo ) );
            }else if(requestInfo.getCommandName().toLowerCase().contains((String) filter.getParams().get("textSearch"))){
                requestSummarys.add( RequestSummaryHelper.adaptRequest( requestInfo ) );
            }
            
        }
        return requestSummarys;
    }

}