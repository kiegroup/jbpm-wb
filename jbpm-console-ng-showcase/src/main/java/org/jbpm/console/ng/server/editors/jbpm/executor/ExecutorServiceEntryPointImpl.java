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
package org.jbpm.console.ng.server.editors.jbpm.executor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.shared.ExecutorServiceEntryPoint;
import org.jbpm.console.ng.shared.model.ErrorSummary;
import org.jbpm.console.ng.shared.model.RequestSummary;
import org.jbpm.executor.api.CommandContext;

/**
 *
 *
 */
@Service
@ApplicationScoped
public class ExecutorServiceEntryPointImpl implements ExecutorServiceEntryPoint {

    @Inject
    org.jbpm.executor.ExecutorServiceEntryPoint executor;

    public List<RequestSummary> getQueuedRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getQueuedRequests());
    }

    public List<RequestSummary> getCompletedRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getCompletedRequests());
    }

    public List<RequestSummary> getInErrorRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getInErrorRequests());
    }

    public List<RequestSummary> getCancelledRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getCancelledRequests());
    }

    public List<ErrorSummary> getAllErrors() {
        return RequestSummaryHelper.adaptErrorList(executor.getAllErrors());
    }

    public List<RequestSummary> getAllRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getAllRequests());
    }

    public int clearAllRequests() {
        return executor.clearAllRequests();
    }

    public int clearAllErrors() {
        return executor.clearAllErrors();
    }

    public Long scheduleRequest(String commandName, Map<String, String> ctx) {
        CommandContext commandContext = null;
        if (ctx != null && !ctx.isEmpty()) {
            commandContext = new CommandContext(new HashMap<String, Object>(ctx));
        }
        return executor.scheduleRequest(commandName, commandContext);
    }

    public Long scheduleRequest(String commandName, Date date, Map<String, String> ctx) {
        CommandContext commandContext = null;
        if (ctx != null && !ctx.isEmpty()) {
            commandContext = new CommandContext(new HashMap<String, Object>(ctx));
        }
        return executor.scheduleRequest(commandName, date, commandContext);
    }

    public void cancelRequest(Long requestId) {
        executor.cancelRequest(requestId);
    }

    public void init() {
        executor.init();
    }

    public void destroy() {
        executor.destroy();
    }

    public int getInterval() {
        return executor.getInterval();
    }

    public void setInterval(int waitTime) {
        executor.setInterval(waitTime);
    }

    public int getRetries() {
        return executor.getRetries();
    }

    public void setRetries(int defaultNroOfRetries) {
        executor.setRetries(defaultNroOfRetries);
    }

    public int getThreadPoolSize() {
        return executor.getThreadPoolSize();
    }

    public void setThreadPoolSize(int nroOfThreads) {
        executor.setThreadPoolSize(nroOfThreads);
    }

    public List<RequestSummary> getPendingRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getPendingRequests());
    }

    public List<RequestSummary> getPendingRequestById(Long id) {
        return RequestSummaryHelper.adaptRequestList(executor.getPendingRequestById(id));
    }

    public List<RequestSummary> getRunningRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getRunningRequests());
    }

    public List<RequestSummary> getFutureQueuedRequests() {
        return RequestSummaryHelper.adaptRequestList(executor.getFutureQueuedRequests());
    }
}
