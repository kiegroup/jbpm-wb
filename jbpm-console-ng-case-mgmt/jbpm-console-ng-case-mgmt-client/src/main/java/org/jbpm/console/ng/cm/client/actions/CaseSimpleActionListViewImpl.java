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

package org.jbpm.console.ng.cm.client.actions;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.cm.client.util.AbstractView;
import org.jbpm.console.ng.cm.model.CaseActionSummary;
import org.jbpm.console.ng.cm.util.CaseActionsFilterBy;

import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated(stylesheet = "CaseSimpleActionListViewImpl.css")
public class CaseSimpleActionListViewImpl extends AbstractView<CaseActionsPresenter> implements CaseActionsPresenter.CaseSimpleActionListView {

    @Inject
    @DataField("simple-list")
    Div simpleList;

    @Inject
    @DataField("actions-list-header-image")
    Span actionsListHeaderImage;

    @Inject
    @DataField("actions-list-header-text")
    Span actionsListHeaderText;

    @Inject
    @DataField("empty-list-item")
    private Div emptyContainer;

    @Inject
    @Bound
    @DataField("actions-list")
    private ListComponent<CaseActionSummary, CaseActionItemView> tasks;

    @Inject
    @AutoBound
    private DataBinder<List<CaseActionSummary>> caseActionList;

    @Inject
    private TranslationService translationService;

    @Override
    public void init(final CaseActionsPresenter presenter) {
        super.init(presenter);
        tasks.addComponentCreationHandler(v -> v.init(presenter));
    }

    public void setCaseActionList(final List<CaseActionSummary> caseActionList) {
        this.caseActionList.setModel(caseActionList);
        if (caseActionList.isEmpty()) {
            removeCSSClass(emptyContainer, "hidden");
        } else {
            addCSSClass(emptyContainer, "hidden");
        }
    }

    @Override
    public void removeAllTasks() {
        caseActionList.setModel(new ArrayList<>());
    }

    public void updateListHeader(CaseActionsFilterBy filterByOldValue, CaseActionsFilterBy filterByNewValue) {
        if (filterByOldValue != null) {
            switch (filterByOldValue) {
                case AVAILABLE: {
                    removeCSSClass(actionsListHeaderImage, "fa-flag-o");
                    removeCSSClass(actionsListHeaderImage, "availableColor");
                    break;
                }
                case IN_PROGRESS: {
                    removeCSSClass(actionsListHeaderImage, "fa-flag-checkered");
                    removeCSSClass(actionsListHeaderImage, "inProgressColor");
                    break;
                }
                case COMPLETED: {
                    removeCSSClass(actionsListHeaderImage, "fa-flag");
                    removeCSSClass(actionsListHeaderImage, "completedColor");
                    break;
                }
            }
        }
        switch (filterByNewValue) {
            case AVAILABLE: {
                updateActionsHeader(translationService.format(CaseActionsFilterBy.AVAILABLE.name()), translationService.format(CaseActionsFilterBy.AVAILABLE.name()), "fa", "fa-flag-o", "availableColor");
                break;
            }
            case IN_PROGRESS: {
                updateActionsHeader(translationService.format(CaseActionsFilterBy.IN_PROGRESS.name()), translationService.format(CaseActionsFilterBy.IN_PROGRESS.name()), "fa", "fa-flag-checkered", "inProgressColor");
                break;
            }
            case COMPLETED: {
                updateActionsHeader(translationService.format(CaseActionsFilterBy.COMPLETED.name()), translationService.format(CaseActionsFilterBy.COMPLETED.name()), "fa", "fa-flag", "completedColor");
                break;
            }
            default: {
                updateActionsHeader(translationService.format(CaseActionsFilterBy.ALL.name()), translationService.format(CaseActionsFilterBy.ALL.name()), "fa");
            }
        }
    }

    public void updateActionsHeader(final String heatherText, final String tooltipTitle, final String... stylesClass) {
        actionsListHeaderText.setTextContent(heatherText);
        actionsListHeaderImage.setAttribute("data-original-title", tooltipTitle + stylesClass);
        for (String styleClass : stylesClass) {
            addCSSClass(this.actionsListHeaderImage, styleClass);
        }
    }

    @Override
    public HTMLElement getElement() {
        return simpleList;
    }

}