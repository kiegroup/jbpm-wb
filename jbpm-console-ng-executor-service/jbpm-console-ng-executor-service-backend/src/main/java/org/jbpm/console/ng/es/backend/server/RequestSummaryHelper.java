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
import java.util.List;

import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.entities.STATUS;

/**
 *

 */
public class RequestSummaryHelper {
    public static List<RequestSummary> adaptRequestList(List<RequestInfo> requests){
        List<RequestSummary> requestSummaries = new ArrayList<RequestSummary>(requests.size());
        for(RequestInfo request : requests){
            requestSummaries.add(new RequestSummary(request.getId(), request.getTime(), request.getStatus().name(),
                    request.getCommandName(), request.getMessage(), request.getKey()));
        }
        return requestSummaries;
    }
    
    public static List<ErrorSummary> adaptErrorList(List<ErrorInfo> errors){
        List<ErrorSummary> errorSummaries = new ArrayList<ErrorSummary>(errors.size());
        for(ErrorInfo error : errors){
            errorSummaries.add(new ErrorSummary(error.getId(), error.getTime(), error.getMessage(),
                    error.getStacktrace(), error.getRequestInfo().getId()));
        }
        return errorSummaries;
    }
    
    public static List<STATUS> adaptStatusList(List<String> statuses) {
    	List<STATUS> statusList = new ArrayList<STATUS>(statuses.size());
    	for (String status : statuses) {
    		statusList.add(STATUS.valueOf(status));
    	}
    	return statusList;
    }
}
