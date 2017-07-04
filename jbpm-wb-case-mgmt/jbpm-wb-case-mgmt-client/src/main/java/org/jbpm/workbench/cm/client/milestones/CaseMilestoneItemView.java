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

package org.jbpm.workbench.cm.client.milestones;

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
import org.jbpm.workbench.cm.client.resources.i18n.Constants;
import org.jbpm.workbench.cm.client.util.AbstractView;

import org.jbpm.workbench.cm.client.util.CaseMilestoneStatus;
import org.jbpm.workbench.cm.client.util.DateConverter;
import org.jbpm.workbench.cm.model.CaseMilestoneSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated
public class CaseMilestoneItemView extends AbstractView<CaseMilestoneListPresenter> implements TakesValue<CaseMilestoneSummary>,
                                                                                               IsElement {

    @Inject
    protected TranslationService translationService;

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

        switch (milestoneStatus) {
            case AVAILABLE: {
                showMilestoneStatus(translationService.format(Constants.MILESTONES_HAS_NOT_BEEN_COMPLETED),
                                    "fa-flag",
                                    "fa-process-flow-bxms");
                break;
            }
            case COMPLETED: {
                String achievedAtStr = DateConverter.getDateStr(model.getAchievedAt());
                showMilestoneStatus(translationService.format(Constants.MILESTONES_HAS_BEEN_COMPLETED,
                                                              (achievedAtStr.isEmpty() ? "" : "(" + achievedAtStr + ")")),
                                    "fa-check",
                                    "kie-milestones__icon--completed");
                addCSSClass(this.listGroupItem,
                            "kie-milestones__item--completed");
                break;
            }
            case TERMINATED: {
                String achievedAtStr = DateConverter.getDateStr(model.getAchievedAt());
                showMilestoneStatus(translationService.format(Constants.MILESTONES_HAS_BEEN_TERMINATED,
                                                              (achievedAtStr.isEmpty() ? "" : "(" + achievedAtStr + ")")),
                                    "fa-close",
                                    "kie-milestones__icon--terminated");
                addCSSClass(this.listGroupItem,
                            "kie-milestones__item--terminated");
                break;
            }
        }
    }

    public void showMilestoneStatus(final String tooltipTitle,
                                    final String... stylesClass) {
        status.setAttribute("data-original-title",
                            tooltipTitle);
        for (String styleClass : stylesClass) {
            addCSSClass(this.status,
                        styleClass);
        }
    }
}
