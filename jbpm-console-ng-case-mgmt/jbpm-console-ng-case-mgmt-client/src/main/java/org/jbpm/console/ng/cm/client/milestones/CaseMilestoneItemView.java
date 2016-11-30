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

package org.jbpm.console.ng.cm.client.milestones;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.cm.client.util.AbstractView;

import org.jbpm.console.ng.cm.client.util.CaseMilestoneStatus;
import org.jbpm.console.ng.cm.client.util.DateConverter;
import org.jbpm.console.ng.cm.model.CaseMilestoneSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;

@Dependent
@Templated
public class CaseMilestoneItemView extends AbstractView<CaseMilestoneListPresenter> implements TakesValue<CaseMilestoneSummary>, IsElement {

    @Inject
    @DataField("milestone-name")
    @Bound
    Span name;

    @Inject
    @DataField("milestone-status")
    Span status;

    @Inject
    @DataField("list-group-item")
    Div listGroupItem;

    @Inject
    @AutoBound
    private DataBinder<CaseMilestoneSummary> caseMilestoneSummary;

    @Inject
    protected TranslationService translationService;

    @PostConstruct
    public void init() {
        tooltip(status);
    }

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    @Override
    public CaseMilestoneSummary getValue() {
        return caseMilestoneSummary.getModel();
    }

    @Override
    public void setValue(final CaseMilestoneSummary model) {
        this.caseMilestoneSummary.setModel(model);
        final CaseMilestoneStatus milestoneStatus = CaseMilestoneStatus.fromStatus(model.getStatus());

        String statusStr = convertStatusToStr(model.getStatus());
        switch (milestoneStatus) {
            case AVAILABLE: {
                showMilestoneStatus(statusStr, "", "label", "label-info");
                break;
            }
            case COMPLETED: {
                String achievedAtStr = DateConverter.getDateStr(model.getAchievedAt());
                showMilestoneStatus("", statusStr + (achievedAtStr.isEmpty() ? "" : "(" + achievedAtStr + ")"),
                        "pficon", "pficon-ok");
                break;
            }
            case TERMINATED: {
                String achievedAtStr = DateConverter.getDateStr(model.getAchievedAt());
                showMilestoneStatus("", statusStr + (achievedAtStr.isEmpty() ? "" : "(" + achievedAtStr + ")"),
                        "pficon", "pficon-error-circle-o");
                break;
            }
        }
    }

    public void showMilestoneStatus(final String statusText, final String tooltipTitle, final String... stylesClass) {
        status.setTextContent(statusText);
        status.setAttribute("data-original-title", tooltipTitle);
        for (String styleClass : stylesClass) {
            addCSSClass(this.status, styleClass);
        }
    }

    public String convertStatusToStr(final String modelValue) {
        if (modelValue == null) {
            return "";
        } else {
            return translationService.format(CaseMilestoneStatus.fromStatus(modelValue).getStatus());
        }
    }

}
