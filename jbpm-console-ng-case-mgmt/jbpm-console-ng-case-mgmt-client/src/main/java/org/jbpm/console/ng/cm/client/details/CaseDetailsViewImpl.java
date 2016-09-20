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

package org.jbpm.console.ng.cm.client.details;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.LabelType;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class CaseDetailsViewImpl extends Composite implements CaseDetailsPresenter.CaseDetailsView {

    @Inject
    @DataField
    Paragraph caseIdText;

    @Inject
    @DataField
    Paragraph caseDescriptionText;

    @Inject
    @DataField
    Label caseStatusText;

    @Inject
    @DataField
    Paragraph caseStartText;

    @Inject
    @DataField
    Paragraph caseCompleteText;

    @Inject
    @DataField
    Paragraph caseOwnerText;

    @Override
    public void init(final CaseDetailsPresenter presenter) {
    }

    @Override
    public void setCaseDescription(final String text) {
        caseDescriptionText.setText(text);
    }

    @Override
    public void setCaseStatus(final String status) {
        caseStatusText.setType(LabelType.DEFAULT);
        caseStatusText.setText(status);
    }

    @Override
    public void setCaseId(final String caseId) {
        caseIdText.setText(caseId);
    }

    @Override
    public void setCaseStartedAt(final String date) {
        caseStartText.setText(date);
    }

    @Override
    public void setCaseCompletedAt(final String date) {
        caseCompleteText.setText(date);
    }

    @Override
    public void setCaseOwner(final String owner) {
        caseOwnerText.setText(owner);
    }
}