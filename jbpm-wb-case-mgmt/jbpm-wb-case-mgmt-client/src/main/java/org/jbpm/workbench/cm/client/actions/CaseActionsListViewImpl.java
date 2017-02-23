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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.model.CaseActionSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated
public class CaseActionsListViewImpl extends AbstractView<CaseActionsPresenter> implements CaseActionsPresenter.CaseActionsListView {
    private int PAGE_SIZE = 4;

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
    @DataField("actions-list-header-counter")
    Span actionsListHeaderCounter;

    @Inject
    @DataField("empty-list-item")
    private Div emptyContainer;

    @Inject
    @DataField("pagination")
    private Div pagination;

    @Inject
    @DataField("scrollbox")
    private Div scrollbox;


    @Inject
    @DataField("nextPage")
    Anchor nextPage;

    @Inject
    @DataField("prevPage")
    Anchor prevPage;

    @Inject
    @Bound
    @DataField("actions-list")
    private ListComponent<CaseActionSummary, CaseActionItemView> tasks;

    @Inject
    @AutoBound
    private DataBinder<List<CaseActionSummary>> caseActionList;

    List<CaseActionSummary> allActionsList;
    int currentPage ;

    @Override
    public void init(final CaseActionsPresenter presenter) {
        this.presenter = presenter;
        tasks.addComponentCreationHandler(v -> v.init(presenter));
    }

    public void setCaseActionList(final List<CaseActionSummary> caseActionList) {
        allActionsList = caseActionList;
        currentPage = 0;
        setVisibleItemsList(currentPage);
        if (caseActionList.isEmpty()) {
            removeCSSClass(emptyContainer, "hidden");
        } else {
            addCSSClass(emptyContainer, "hidden");
        }
        actionsListHeaderCounter.setTextContent(String.valueOf(allActionsList.size()));
    }

    @Override
    public void removeAllTasks() {
        caseActionList.setModel(new ArrayList<>());
    }

    public void updateActionsHeader(final String heatherText, final String... stylesClass) {
        actionsListHeaderText.setTextContent(heatherText);
        for (String styleClass : stylesClass) {
            addCSSClass(this.actionsListHeaderImage, styleClass);
        }
    }

    protected void setVisibleItemsList(int currentPage){
        this.currentPage = currentPage;
        int allItemsSize = allActionsList.size();
        List<CaseActionSummary> visibleItems;
        boolean hasPrevPage=false;
        boolean hasNextPage=false;

        if(currentPage!=0) {
            hasPrevPage = true;
        }

        if( PAGE_SIZE * (currentPage +1) < allItemsSize){
            hasNextPage=true;
            visibleItems = allActionsList.subList( PAGE_SIZE * currentPage , PAGE_SIZE *(currentPage+1));
        } else {
            visibleItems=  allActionsList.subList(PAGE_SIZE * currentPage,allItemsSize);
        }

        boolean showPagination = false;
        removeCSSClass(scrollbox, "kie-end-scroll");
        removeCSSClass(scrollbox, "kie-start-scroll");
        removeCSSClass(scrollbox, "kie-both-scroll");

        if(hasNextPage) {
            showPagination = true;
            removeCSSClass(nextPage, "disabled");
            if(hasPrevPage){
                addCSSClass(scrollbox, "kie-both-scroll");
            } else {
                addCSSClass(scrollbox, "kie-end-scroll");
            }
        } else {
            addCSSClass(nextPage, "disabled");
        }
        if(hasPrevPage) {
            showPagination = true;
            removeCSSClass(prevPage, "disabled");
            addCSSClass(scrollbox, "kie-start-scroll");
        } else {
            addCSSClass(prevPage, "disabled");
        }

        pagination.setHidden(!showPagination);

        this.caseActionList.setModel(visibleItems);
        int tasksSize =visibleItems.size();
        if(tasksSize > 0){
            tasks.getComponent(tasksSize-1).setLastElementStyle();
        }
    }

    @Override
    public HTMLElement getElement() {
        return simpleList;
    }

    @EventHandler("nextPage")
    @SuppressWarnings("unsued")
    public void onNextPageClick(@ForEvent("click") final Event event) {
        if (! hasCSSClass(nextPage,"disabled")) {
            setVisibleItemsList(currentPage + 1);
        }
    }
    @EventHandler("prevPage")
    @SuppressWarnings("unsued")
    public void onPrevPageClick(@ForEvent("click") final Event event) {
        if (! hasCSSClass(prevPage,"disabled")) {
            setVisibleItemsList(currentPage - 1);
        }
    }

}