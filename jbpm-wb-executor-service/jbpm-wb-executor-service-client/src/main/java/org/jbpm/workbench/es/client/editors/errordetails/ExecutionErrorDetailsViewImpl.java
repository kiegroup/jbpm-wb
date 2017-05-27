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

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
@Templated
@Bundle("/org/jbpm/workbench/es/client/i18n/Constants.properties")
public class ExecutionErrorDetailsViewImpl implements ExecutionErrorDetailsPresenter.ExecErrorDetailsView {

    @Inject
    @DataField("container")
    Div container;

    @Inject
    @DataField("error-details")
    ExecutionErrorBasicDetailsViewImpl errorDetails;

    @Inject
    PlaceManager placeManager;

    @Override
    public void init(final ExecutionErrorDetailsPresenter presenter) {
    }

    @Override
    public void setValue(ExecutionErrorSummary errorSummary) {
        errorDetails.setValue(errorSummary);
    }

    @Override
    public HTMLElement getElement() {
        return container;
    }
}