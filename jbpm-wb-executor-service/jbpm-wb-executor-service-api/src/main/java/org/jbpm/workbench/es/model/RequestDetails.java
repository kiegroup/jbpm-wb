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

package org.jbpm.workbench.es.model;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@Portable
public class RequestDetails implements Serializable {

    private RequestSummary request;
    private List<ErrorSummary> errors;
    private List<RequestParameterSummary> params;

    public RequestDetails() {
    }

    public RequestDetails(RequestSummary request,
                          List<ErrorSummary> errors,
                          List<RequestParameterSummary> params) {
        this();
        this.request = request;
        setErrors(errors);
        setParams(params);
    }

    public RequestSummary getRequest() {
        return request;
    }

    public void setRequest(RequestSummary request) {
        this.request = request;
    }

    public List<ErrorSummary> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorSummary> errors) {
        this.errors = ofNullable(errors).orElse(emptyList());
    }

    public List<RequestParameterSummary> getParams() {
        return params;
    }

    public void setParams(List<RequestParameterSummary> params) {
        this.params = ofNullable(params).orElse(emptyList());
    }
}
