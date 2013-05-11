/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.console.ng.es.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestDetails;
import org.jbpm.console.ng.es.model.RequestSummary;

@Remote
public interface ExecutorServiceEntryPoint {

    public List<RequestSummary> getQueuedRequests();

    public List<RequestSummary> getCompletedRequests();

    public List<RequestSummary> getInErrorRequests();

    public List<RequestSummary> getCancelledRequests();

    public List<ErrorSummary> getAllErrors();

    public List<RequestSummary> getAllRequests();

    public List<RequestSummary> getRequestsByStatus(List<String> statuses);

    public RequestDetails getRequestDetails(Long requestId);

    public int clearAllRequests();

    public int clearAllErrors();

    public Long scheduleRequest(String commandName, Map<String, String> ctx);

    public Long scheduleRequest(String commandId, Date date, Map<String, String> ctx);

    public void cancelRequest(Long requestId);

    public void init();

    public void destroy();

    public Boolean isActive();

    public Boolean startStopService(int waitTime, int nroOfThreads);

    public int getInterval();

    public void setInterval(int waitTime);

    public int getRetries();

    public void setRetries(int defaultNroOfRetries);

    public int getThreadPoolSize();

    public void setThreadPoolSize(int nroOfThreads);

    public List<RequestSummary> getPendingRequests();

    public List<RequestSummary> getPendingRequestById(Long id);

    public List<RequestSummary> getRunningRequests();

    public List<RequestSummary> getFutureQueuedRequests();

}
