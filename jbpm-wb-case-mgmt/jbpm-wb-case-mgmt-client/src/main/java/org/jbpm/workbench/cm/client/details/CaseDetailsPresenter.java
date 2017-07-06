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
package org.jbpm.workbench.cm.client.details;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.TakesValue;
import org.jbpm.workbench.cm.client.resources.i18n.Constants;
import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;

@Dependent
@WorkbenchScreen(identifier = CaseDetailsPresenter.SCREEN_ID)
public class CaseDetailsPresenter extends AbstractCaseInstancePresenter<CaseDetailsPresenter.CaseDetailsView> {

    public static final String SCREEN_ID = "Case Details Screen";

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(Constants.CASE_DETAILS);
    }

    @Override
    protected void clearCaseInstance() {
        view.setValue(new CaseInstanceSummary());
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        view.setValue(cis);
    }

    public interface CaseDetailsView extends UberElement<CaseDetailsPresenter>,
                                             TakesValue<CaseInstanceSummary> {

    }
}