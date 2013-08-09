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

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ErrorInfo;
import org.kie.internal.executor.api.RequestInfo;
import org.kie.internal.executor.api.STATUS;

public class RequestSummaryHelper {

    public static List<RequestSummary> adaptRequestList(List<RequestInfo> requests) {
        List<RequestSummary> requestSummaries = new ArrayList<RequestSummary>(requests.size());
        for (RequestInfo request : requests) {
            requestSummaries.add(adaptRequest(request));
        }
        return requestSummaries;
    }

    public static RequestSummary adaptRequest(RequestInfo request) {
        return new RequestSummary(request.getId(), request.getTime(), request.getStatus().name(), request.getCommandName(),
                request.getMessage(), request.getKey());
    }

    public static List<ErrorSummary> adaptErrorList(List<? extends ErrorInfo> errors) {
        List<ErrorSummary> errorSummaries = new ArrayList<ErrorSummary>(errors.size());
        for (ErrorInfo error : errors) {
            errorSummaries.add(new ErrorSummary(error.getId(), error.getTime(), error.getMessage(), error.getStacktrace(),
                    ((org.jbpm.executor.entities.ErrorInfo) error).getRequestInfo().getId()));
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

    public static List<RequestParameterSummary> adaptInternalMap(RequestInfo request) {
        ByteArrayInputStream bain = new ByteArrayInputStream(request.getRequestData());
        List<RequestParameterSummary> retval = new ArrayList<RequestParameterSummary>();
        try {
            ObjectInputStream oin = new ObjectInputStream(bain);
            CommandContext ctx = (CommandContext) oin.readObject();
            Map<String, Object> map = ctx.getData();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                retval.add(new RequestParameterSummary(entry.getKey(), String.valueOf(entry.getValue())));
            }
        } catch (Exception e) {
            // TODO handle exception
        }
        return retval;
    }
}
