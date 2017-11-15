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

package org.jbpm.workbench.es.client.editors.jobdetails;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.workbench.common.client.util.DateTimeConverter;
import org.jbpm.workbench.es.client.util.JobStatusConverter;
import org.jbpm.workbench.es.model.RequestSummary;

@Dependent
@Templated(value = "JobBasicDetailsViewImpl.html")
public class JobBasicDetailsViewImpl implements TakesValue<RequestSummary>,
                                                IsElement {

    @Inject
    @DataField("businesskey_div")
    Div businesskeyDiv;

    @Inject
    @DataField("deployment_id_div")
    Div deploymentIdDiv;

    @Inject
    @DataField("job-jobId")
    @Bound
    @SuppressWarnings("unused")
    private Span jobId;

    @Inject
    @DataField("job-time")
    @Bound(converter = DateTimeConverter.class)
    @SuppressWarnings("unused")
    private Span time;

    @Inject
    @DataField("job-status")
    @Bound(converter = JobStatusConverter.class)
    @SuppressWarnings("unused")
    private Span status;

    @Inject
    @DataField("job-commandName")
    @Bound
    @SuppressWarnings("unused")
    private Span commandName;

    @Inject
    @DataField("job-message")
    @Bound
    @SuppressWarnings("unused")
    private Span message;

    @Inject
    @DataField("job-key")
    @Bound
    @SuppressWarnings("unused")
    private Span key;

    @Inject
    @DataField("job-retries")
    @Bound
    @SuppressWarnings("unused")
    private Span retries;

    @Inject
    @DataField("job-executions")
    @Bound
    @SuppressWarnings("unused")
    private Span executions;

    @Inject
    @DataField("job-deploymentId")
    @Bound
    @SuppressWarnings("unused")
    private Span deploymentId;

    @Inject
    @DataField("container")
    private Div container;

    @Inject
    @AutoBound
    private DataBinder<RequestSummary> requestSummaryDataBinder;

    @Override
    public RequestSummary getValue() {
        return requestSummaryDataBinder.getModel();
    }

    @Override
    public void setValue(RequestSummary requestSummary) {
        requestSummaryDataBinder.setModel(requestSummary);

        businesskeyDiv.setHidden(requestSummary.getKey() == null);
        deploymentIdDiv.setHidden(requestSummary.getDeploymentId() == null);
    }

    @Override
    public HTMLElement getElement() {
        return container;
    }
}