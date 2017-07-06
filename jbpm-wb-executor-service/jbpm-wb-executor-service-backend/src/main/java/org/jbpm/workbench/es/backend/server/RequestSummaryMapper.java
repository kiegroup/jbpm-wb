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

import java.util.function.Function;

import org.jbpm.workbench.es.model.RequestSummary;
import org.kie.server.api.model.instance.RequestInfoInstance;

public class RequestSummaryMapper implements Function<RequestInfoInstance, RequestSummary> {

    @Override
    public RequestSummary apply(final RequestInfoInstance request) {
        if (request == null) {
            return null;
        }
        return new RequestSummary(request.getId(),
                                  request.getScheduledDate(),
                                  request.getStatus(),
                                  request.getCommandName(),
                                  request.getMessage(),
                                  request.getBusinessKey(),
                                  request.getRetries(),
                                  request.getExecutions(),
                                  null,
                                  null,
                                  null);
    }
}