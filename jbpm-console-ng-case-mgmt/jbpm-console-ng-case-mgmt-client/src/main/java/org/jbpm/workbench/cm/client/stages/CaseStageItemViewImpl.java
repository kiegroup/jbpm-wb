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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.client.util.CaseStageStatus;
import org.jbpm.workbench.cm.model.CaseStageSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated(stylesheet = "CaseStageItemViewImpl.css")
public class CaseStageItemViewImpl extends AbstractView<CaseStagesPresenter> implements TakesValue<CaseStageSummary>, IsElement {

    @Inject
    @DataField("stage-name")
    @Bound
    Span name;

    @Inject
    @DataField("stage-status")
    Span status;

    @Inject
    @DataField("list-group-item")
    ListItem listGroupItem;

    @Inject
    @AutoBound
    private DataBinder<CaseStageSummary> caseStageSummary;

    @Inject
    protected TranslationService translationService;

    @PostConstruct
    public void init() {
        tooltip(status);
        tooltip(name);
    }
    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    @Override
    public CaseStageSummary getValue() {
        return caseStageSummary.getModel();
    }

    @Override
    public void setValue(final CaseStageSummary model) {
        this.caseStageSummary.setModel(model);
        final CaseStageStatus stageStatus = CaseStageStatus.fromStatus(model.getStatus());

        String statusStr = convertStatusToStr(stageStatus.getStatus());
        switch(stageStatus){
            case COMPLETED:{
                showStageStatus( statusStr, "pficon", "pficon-ok");
                break;
            }
            case AVAILABLE:{
                showStageStatus( statusStr, "fa", "fa-circle-o");
                break;
            }
            case CANCELED:{
                showStageStatus( statusStr, "pficon", "pficon-error-circle-o");
                break;
            }
        }
    }

    public void showStageStatus(final String tooltipTitle, final String ... stylesClass) {
        name.setAttribute("data-original-title", tooltipTitle);
        status.setAttribute("data-original-title", tooltipTitle);
        for(String styleClass :stylesClass){
            addCSSClass(this.status, styleClass);
        }
    }

    public String convertStatusToStr(final String modelValue){
        if (modelValue == null) {
            return "";
        } else {
            return translationService.format(CaseStageStatus.fromStatus(modelValue).getStatus());
        }
    }

}