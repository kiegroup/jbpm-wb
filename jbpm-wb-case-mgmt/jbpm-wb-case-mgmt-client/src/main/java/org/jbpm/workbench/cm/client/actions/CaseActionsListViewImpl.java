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

package org.jbpm.workbench.cm.client.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.pagination.PaginationViewImpl;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.model.CaseActionSummary;

import static java.util.stream.Collectors.toList;
import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated
public class CaseActionsListViewImpl extends AbstractView<CaseActionsPresenter>
        implements CaseActionsPresenter.CaseActionsListView,
                   PaginationViewImpl.PageList<CaseActionSummary> {

    private static int PAGE_SIZE = 3;

    @Inject
    @DataField("simple-list")
    private Div simpleList;

    @Inject
    @DataField("actions-list-header-image")
    private Span actionsListHeaderImage;

    @Inject
    @DataField("actions-list-header-text")
    private Span actionsListHeaderText;

    @Inject
    @DataField("actions-list-header-counter")
    private Span actionsListHeaderCounter;

    @Inject
    @DataField("empty-list-item")
    private Div emptyContainer;

    @Inject
    @DataField("pagination")
    private PaginationViewImpl pagination;

    @Inject
    @DataField("scrollbox")
    private Div scrollbox;

    @Inject
    @Bound
    @DataField("actions-list")
    private ListComponent<CaseActionSummary, CaseActionItemView> tasks;

    @Inject
    @AutoBound
    private DataBinder<List<CaseActionSummary>> caseActionList;

    @Override
    public void init(final CaseActionsPresenter presenter) {
        this.presenter = presenter;
        tasks.addComponentCreationHandler(v -> v.init(presenter));
    }

    public void setCaseActionList(final List<CaseActionSummary> caseActionList) {
        pagination.init(caseActionList,
                        this,
                        PAGE_SIZE);
        if (caseActionList.isEmpty()) {
            removeCSSClass(emptyContainer,
                           "hidden");
        } else {
            addCSSClass(emptyContainer,
                        "hidden");
        }
        actionsListHeaderCounter.setTextContent(String.valueOf(caseActionList.size()));
    }

    @Override
    public void removeAllTasks() {
        caseActionList.setModel(new ArrayList<>());
    }

    public void updateActionsHeader(final String heatherText,
                                    final String... stylesClass) {
        actionsListHeaderText.setTextContent(heatherText);
        for (String styleClass : stylesClass) {
            addCSSClass(this.actionsListHeaderImage,
                        styleClass);
        }
    }

    @Override
    public void setVisibleItems(List<CaseActionSummary> visibleItems) {
        this.caseActionList.setModel(visibleItems);
        setActions(visibleItems);
        int tasksSize = visibleItems.size();
        if (tasksSize > 1) {
            tasks.getComponent(tasksSize - 1).setLastElementStyle();
        }
    }

    private void setActions(List<CaseActionSummary> visibleItems) {
        List<CaseActionItemView> caseActionItemViews = visibleItems.stream()
                                                                   .map(task -> tasks.getComponent(task))
                                                                   .filter(Optional::isPresent)
                                                                   .map(Optional::get)
                                                                   .collect(toList());
        caseActionItemViews.forEach(action -> presenter.setAction(action));
    }

    @Override
    public Div getScrollBox() {
        return scrollbox;
    }

    @Override
    public HTMLElement getElement() {
        return simpleList;
    }
}