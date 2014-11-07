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

package org.jbpm.console.ng.es.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestDetails;
import org.jbpm.console.ng.es.model.RequestKey;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ga.service.GenericServiceEntryPoint;
import org.jbpm.executor.RequeueAware;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutorService;
import org.kie.internal.executor.api.RequestInfo;
import org.kie.internal.executor.api.STATUS;
import org.uberfire.paging.PageResponse;

@Service
@ApplicationScoped
public class ExecutorServiceEntryPointImpl implements ExecutorServiceEntryPoint ,GenericServiceEntryPoint<RequestKey, RequestSummary> {

    @Inject
    ExecutorService executor;

    @Override
    public List<RequestSummary> getQueuedRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getQueuedRequests());
    }

    @Override
    public List<RequestSummary> getCompletedRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getCompletedRequests());
    }

    @Override
    public List<RequestSummary> getInErrorRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getInErrorRequests());
    }

    @Override
    public List<RequestSummary> getCancelledRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getCancelledRequests());
    }

    @Override
    public List<ErrorSummary> getAllErrors() {
        return RequestSummaryHelper.adaptErrorList(executor.getAllErrors());
    }

    @Override
    public List<RequestSummary> getAllRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getAllRequests());
    }

    @Override
    public List<RequestSummary> getRequestsByStatus(List<String> statuses) {
        List<STATUS> statusList = RequestSummaryHelper.adaptStatusList(statuses);
        return RequestSummaryHelper.adaptRequestList(executor.getRequestsByStatus(statusList));
    }

    @Override
    public RequestDetails getRequestDetails(Long requestId) {
        RequestInfo request = executor.getRequestById(requestId);
        RequestSummary summary = RequestSummaryHelper.adaptRequest(request);
        List<ErrorSummary> errors = RequestSummaryHelper.adaptErrorList(request.getErrorInfo());
        List<RequestParameterSummary> params = RequestSummaryHelper.adaptInternalMap(request);
        return new RequestDetails(summary, errors, params);
    }

    @Override
    public int clearAllRequests() {
        return executor.clearAllRequests();
    }

    @Override
    public int clearAllErrors() {
        return executor.clearAllErrors();
    }

    @Override
    public Long scheduleRequest(String commandName, Map<String, String> ctx) {
        CommandContext commandContext = null;
        if (ctx != null && !ctx.isEmpty()) {
            commandContext = new CommandContext(new HashMap<String, Object>(ctx));
        }
        return executor.scheduleRequest(commandName, commandContext);
    }

    @Override
    public Long scheduleRequest(String commandName, Date date, Map<String, String> ctx) {
        CommandContext commandContext = null;
        if (ctx != null && !ctx.isEmpty()) {
            commandContext = new CommandContext(new HashMap<String, Object>(ctx));
        }
        return executor.scheduleRequest(commandName, date, commandContext);
    }

    @Override
    public void cancelRequest(Long requestId) {
        executor.cancelRequest(requestId);
    }

    @Override
    public void requeueRequest(Long requestId) {
        ((RequeueAware)executor).requeueById(requestId);
    }

    @Override
    public void init() {
        executor.init();
    }

    @Override
    public void destroy() {
        executor.destroy();
    }

    @Override
    public Boolean isActive() {
        return executor.isActive();
    }

    @Override
    public Boolean startStopService(int waitTime, int nroOfThreads) {
        executor.setInterval(waitTime);
        executor.setThreadPoolSize(nroOfThreads);
        if (executor.isActive()) {
            executor.destroy();
        } else {
            executor.init();
        }
        return executor.isActive();
    }

    @Override
    public int getInterval() {
        return executor.getInterval();
    }

    @Override
    public void setInterval(int waitTime) {
        executor.setInterval(waitTime);
    }

    @Override
    public int getRetries() {
        return executor.getRetries();
    }

    @Override
    public void setRetries(int defaultNroOfRetries) {
        executor.setRetries(defaultNroOfRetries);
    }

    @Override
    public int getThreadPoolSize() {
        return executor.getThreadPoolSize();
    }

    @Override
    public void setThreadPoolSize(int nroOfThreads) {
        executor.setThreadPoolSize(nroOfThreads);
    }

    @Override
    public List<RequestSummary> getPendingRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getPendingRequests());
    }

    @Override
    public List<RequestSummary> getPendingRequestById(Long id) {
        return RequestSummaryHelper.adaptRequestList(executor.getPendingRequestById(id));
    }

    @Override
    public List<RequestSummary> getRunningRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getRunningRequests());
    }

    @Override
    public List<RequestSummary> getFutureQueuedRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getFutureQueuedRequests());
    }

    @Override
    public PageResponse<RequestSummary> getData(QueryFilter filter) {
        PageResponse<RequestSummary> response = new PageResponse<RequestSummary>();
        List<String> states = null;
        if (filter.getParams() != null) {
            states = (List<String>) filter.getParams().get("states");
        }
        Collection<RequestInfo> requestInfoList = null;
        if (states == null || states.isEmpty()) {
            requestInfoList = executor.getAllRequests(); 
        }else{
            List<STATUS> statusList = RequestSummaryHelper.adaptStatusList(states);
            requestInfoList =executor.getRequestsByStatus(statusList);
        }
        List<RequestSummary> requestSummarys = new ArrayList<RequestSummary>(requestInfoList.size()); ;
        for(RequestInfo requestInfo:requestInfoList){
            if (filter.getParams().get("textSearch") == null || ((String) filter.getParams().get("textSearch")).isEmpty()) {
                requestSummarys.add( RequestSummaryHelper.adaptRequest( requestInfo ) );
            }else if(requestInfo.getCommandName().toLowerCase().contains((String) filter.getParams().get("textSearch"))){
                requestSummarys.add( RequestSummaryHelper.adaptRequest( requestInfo ) );
            }
            
        }
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

    @Override
    public RequestSummary getItem(RequestKey key) {
        // TODO Auto-generated method stub
        return null;
    }
}
