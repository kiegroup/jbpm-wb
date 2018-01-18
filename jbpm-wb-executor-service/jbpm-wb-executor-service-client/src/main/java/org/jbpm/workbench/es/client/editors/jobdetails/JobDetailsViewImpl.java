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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.TextArea;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.es.model.ErrorSummary;
import org.jbpm.workbench.es.model.RequestParameterSummary;
import org.jbpm.workbench.es.model.RequestSummary;
import org.uberfire.client.mvp.PlaceManager;

import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Dependent
@Templated
@Bundle("/org/jbpm/workbench/es/client/i18n/Constants.properties")
public class JobDetailsViewImpl implements JobDetailsPresenter.JobDetailsView {

    @Inject
    @DataField("container")
    Div container;

    @Inject
    @DataField("job-details")
    JobBasicDetailsViewImpl basicJobDetails;

    @Inject
    @DataField("job-errors")
    TextArea jobErrorTextArea;

    @Inject
    @DataField("errorControlGroup")
    Div errorControlGroup;

    @Inject
    PlaceManager placeManager;

    @Inject
    @DataField("params-form-group")
    private Div paramsFormGroup;

    @Inject
    @Bound
    @ListContainer("tbody")
    @DataField("job-params")
    @SuppressWarnings("unused")
    private ListComponent<RequestParameterSummary, JobParameterListViewImpl> jobParameters;

    @Inject
    @AutoBound
    private DataBinder<List<RequestParameterSummary>> jobParameterList;

    @Override
    public void init(final JobDetailsPresenter presenter) {
    }

    @Override
    public void setBasicDetails(RequestSummary requestSummary) {
        basicJobDetails.setValue(requestSummary);
        errorControlGroup.setHidden(true);
    }

    @Override
    public void setParameters(List<RequestParameterSummary> requestParameterSummaries) {
        if (requestParameterSummaries.size() > 0) {
            removeCSSClass(paramsFormGroup,
                           "hidden");
        }

        jobParameterList.setModel(requestParameterSummaries);
    }

    @Override
    public void setErrors(List<ErrorSummary> errors) {
        errorControlGroup.setHidden(false);
        String textAreaContent = "";
        for (ErrorSummary error : errors) {
            textAreaContent += error.getMessage() + "\n" +
                    error.getStacktrace() + "\n\n";
        }
        this.jobErrorTextArea.setValue(textAreaContent);
    }

    @Override
    public HTMLElement getElement() {
        return container;
    }
}