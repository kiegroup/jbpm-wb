/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestDetails;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.kie.server.api.model.instance.RequestInfoInstance;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class RequestDetailsMapper implements Function<RequestInfoInstance, RequestDetails> {

    @Override
    public RequestDetails apply(final RequestInfoInstance requestInfoInstance) {
        final Optional<RequestInfoInstance> request = ofNullable(requestInfoInstance);
        if(request.isPresent() == false){
            return null;
        }

        final RequestSummary summary = request.map(new RequestSummaryMapper()).get();
        final List<ErrorSummary> errors = request.map(r -> r.getErrors()).map(e -> e.getItems()).orElse(emptyList()).stream().map(new ErrorSummaryMapper()).collect(toList());
        final List<RequestParameterSummary> params = request.map(r -> r.getData()).orElse(emptyMap()).entrySet().stream()
                .map(e -> new RequestParameterSummary(e.getKey(), String.valueOf(e.getValue())))
                .collect(toList());
        return new RequestDetails(summary, errors, params);
    }
}
