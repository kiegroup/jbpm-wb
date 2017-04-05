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

package org.jbpm.workbench.cm.client.stages;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.model.CaseStageSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated
public class CaseStagesViewImpl extends AbstractView<CaseStagesPresenter> implements CaseStagesPresenter.CaseStagesView {

    @Inject
    @DataField("stages")
    private Div stagesContainer;

    @Inject
    @Bound
    @DataField("stages-list")
    private ListComponent<CaseStageSummary, CaseStageItemViewImpl> stages;

    @Inject
    @AutoBound
    private DataBinder<List<CaseStageSummary>> caseStageList;


    @Override
    public void init(final CaseStagesPresenter presenter) {
        super.init(presenter);
        stages.addComponentCreationHandler(v -> v.init(presenter));
    }

    @Override
    public void setCaseStagesList(final List<CaseStageSummary> caseStageList) {
        this.caseStageList.setModel(caseStageList);
    }

    @Override
    public void removeAllStages() {
        caseStageList.setModel(new ArrayList<>());
    }

    @Override
    public HTMLElement getElement() {
        return stagesContainer;
    }

}