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

package org.jbpm.workbench.es.client.editors.errordetails;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextArea;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.BooleanConverter;
import org.jbpm.workbench.common.client.util.DateTimeConverter;
import org.jbpm.workbench.es.client.util.ExecutionErrorTypeConverter;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;

@Dependent
@Templated(value = "ExecutionErrorBasicDetailsViewImpl.html")
public class ExecutionErrorBasicDetailsViewImpl implements TakesValue<ExecutionErrorSummary>,
                                                           IsElement {

    @Inject
    @DataField("error-errorId")
    @Bound
    @SuppressWarnings("unused")
    private Span errorId;

    @Inject
    @DataField("error-type")
    @Bound(converter = ExecutionErrorTypeConverter.class)
    @SuppressWarnings("unused")
    private Span type;

    @Inject
    @DataField("error-deploymentId")
    @Bound
    @SuppressWarnings("unused")
    private Span deploymentId;

    @Inject
    @DataField("error-processInstanceId")
    @Bound
    @SuppressWarnings("unused")
    private Span processInstanceId;

    @Inject
    @DataField("error-processId")
    @Bound
    @SuppressWarnings("unused")
    private Span processId;

    @Inject
    @DataField("error-activityId")
    @Bound
    @SuppressWarnings("unused")
    private Span activityId;

    @Inject
    @DataField("error-activityName")
    @Bound
    @SuppressWarnings("unused")
    private Span activityName;

    @Inject
    @DataField("error-jobId")
    @Bound
    @SuppressWarnings("unused")
    private Span jobId;

    @Inject
    @DataField("error-errorMessage")
    @Bound
    @SuppressWarnings("unused")
    private Span errorMessage;

    @Inject
    @DataField("error-error")
    @Bound
    @SuppressWarnings("unused")
    private TextArea error;

    @Inject
    @DataField("error-acknowledged")
    @Bound(converter = BooleanConverter.class)
    @SuppressWarnings("unused")
    private Span acknowledged;

    @Inject
    @DataField("error-acknowledgedBy")
    @Bound
    @SuppressWarnings("unused")
    private Span acknowledgedBy;

    @Inject
    @DataField("error-acknowledgedAt")
    @Bound(converter = DateTimeConverter.class)
    @SuppressWarnings("unused")
    private Span acknowledgedAt;

    @Inject
    @DataField("error-errorDate")
    @Bound(converter = DateTimeConverter.class)
    @SuppressWarnings("unused")
    private Span errorDate;

    @Inject
    @DataField("container")
    private Div container;

    @Inject
    @AutoBound
    private DataBinder<ExecutionErrorSummary> errorSummaryDataBinder;

    @Override
    public ExecutionErrorSummary getValue() {
        return errorSummaryDataBinder.getModel();
    }

    @Override
    public void setValue(ExecutionErrorSummary errorSummary) {
        errorSummaryDataBinder.setModel(errorSummary);
    }

    @Override
    public HTMLElement getElement() {
        return container;
    }
}