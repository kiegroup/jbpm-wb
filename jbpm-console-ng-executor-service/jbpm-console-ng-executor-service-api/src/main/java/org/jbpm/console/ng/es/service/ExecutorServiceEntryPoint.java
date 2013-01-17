/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.es.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestSummary;


/**
 *
 */
@Remote
public interface ExecutorServiceEntryPoint {
    
    public List<RequestSummary> getQueuedRequests();

    public List<RequestSummary> getCompletedRequests();

    public List<RequestSummary> getInErrorRequests();

    public List<RequestSummary> getCancelledRequests();

    public List<ErrorSummary> getAllErrors();

    public List<RequestSummary> getAllRequests();

    public int clearAllRequests();

    public int clearAllErrors();

    public Long scheduleRequest(String commandName, Map<String, String> ctx);
    
    public Long scheduleRequest(String commandId, Date date, Map<String, String> ctx);

    public void cancelRequest(Long requestId);

    public void init();

    public void destroy();

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
