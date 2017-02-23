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

package org.jbpm.workbench.cm.client.pagination;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated
public class PaginationViewImpl implements IsElement {

    @Inject
    @DataField("pagination")
    private Div pagination;

    @Inject
    @DataField("nextPage")
    Anchor nextPage;

    @Inject
    @DataField("prevPage")
    Anchor prevPage;

    List allElementsList = new ArrayList();
    int currentPage;
    int pageSize;
    PageList pageList;

    public void init(List allElementsList, PageList pageList, int pageSize) {
        this.allElementsList = allElementsList;
        this.pageList = pageList;
        this.pageSize = pageSize;
        setVisibleItemsList(0);
    }

    protected void setVisibleItemsList(int currentPage) {
        this.currentPage = currentPage;
        int allItemsSize = allElementsList.size();
        List visibleItems;
        boolean hasPrevPage = false;
        boolean hasNextPage = false;

        if (currentPage != 0) {
            hasPrevPage = true;
        }

        if (pageSize * (currentPage + 1) < allItemsSize) {
            hasNextPage = true;
            visibleItems = allElementsList.subList(pageSize * currentPage, pageSize * (currentPage + 1));
        } else {
            visibleItems = allElementsList.subList(pageSize * currentPage, allItemsSize);
        }

        boolean showPagination = false;
        removeCSSClass(pageList.getScrollBox(), "kie-end-scroll");
        removeCSSClass(pageList.getScrollBox(), "kie-start-scroll");
        removeCSSClass(pageList.getScrollBox(), "kie-both-scroll");

        if (hasNextPage) {
            showPagination = true;
            removeCSSClass(nextPage, "disabled");
            if (hasPrevPage) {
                addCSSClass(pageList.getScrollBox(), "kie-both-scroll");
            } else {
                addCSSClass(pageList.getScrollBox(), "kie-end-scroll");
            }
        } else {
            addCSSClass(nextPage, "disabled");
        }
        if (hasPrevPage) {
            showPagination = true;
            removeCSSClass(prevPage, "disabled");
            addCSSClass(pageList.getScrollBox(), "kie-start-scroll");
        } else {
            addCSSClass(prevPage, "disabled");
        }
        pagination.setHidden(!showPagination);
        if (visibleItems.size() == 1) {
            addCSSClass(pageList.getScrollBox(), "kie-scrollbox-show-overflow");
        } else {
            removeCSSClass(pageList.getScrollBox(), "kie-scrollbox-show-overflow");
        }
        pageList.setVisibleItems(visibleItems);
    }

    @EventHandler("nextPage")
    @SuppressWarnings("unsued")
    public void onNextPageClick(@ForEvent("click") final Event event) {
        if (!hasCSSClass(nextPage, "disabled")) {
            setVisibleItemsList(currentPage + 1);
        }
    }

    @EventHandler("prevPage")
    @SuppressWarnings("unsued")
    public void onPrevPageClick(@ForEvent("click") final Event event) {
        if (!hasCSSClass(prevPage, "disabled")) {
            setVisibleItemsList(currentPage - 1);
        }
    }

    public HTMLElement getElement() {
        return pagination;
    }


    public interface PageList {

        void setVisibleItems(List visibleItems);

        Div getScrollBox();

    }
}