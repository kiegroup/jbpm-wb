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
    @DataField("nextPage")
    Anchor nextPage;

    @Inject
    @DataField("prevPage")
    Anchor prevPage;

    List allElementsList = new ArrayList();

    int currentPage;

    int pageSize;

    PageList pageList;

    @Inject
    @DataField("pagination")
    private Div pagination;

    public void init(List allElementsList,
                     PageList pageList,
                     int pageSize) {
        this.allElementsList = allElementsList;
        this.pageList = pageList;
        this.pageSize = pageSize;
        setVisibleItemsList(0);
    }

    public void setPagination(boolean showPagination) {
        pagination.setHidden(!showPagination);
    }

    private void setVisibleItemsList(int currentPage) {
        this.currentPage = currentPage;
        List visibleItems;

        boolean hasPrevPage = false;
        boolean hasNextPage = false;
        boolean showPagination = false;
        removeCSSClass(pageList.getScrollBox(),
                       "kie-end-scroll");
        removeCSSClass(pageList.getScrollBox(),
                       "kie-start-scroll");
        removeCSSClass(pageList.getScrollBox(),
                       "kie-both-scroll");

        if (currentPage != 0) {
            hasPrevPage = true;
            showPagination = true;
            removeCSSClass(prevPage,
                           "disabled");
            addCSSClass(pageList.getScrollBox(),
                        "kie-start-scroll");
        } else {
            addCSSClass(prevPage,
                        "disabled");
        }

        if (pageSize * (currentPage + 1) < allElementsList.size()) {
            hasNextPage = true;
            showPagination = true;
            visibleItems = allElementsList.subList(pageSize * currentPage,
                                                   pageSize * (currentPage + 1));
            removeCSSClass(nextPage,
                           "disabled");
        } else {
            visibleItems = allElementsList.subList(pageSize * currentPage,
                                                   allElementsList.size());
            addCSSClass(nextPage,
                        "disabled");
        }

        if (hasNextPage && hasPrevPage) {
            addCSSClass(pageList.getScrollBox(),
                        "kie-both-scroll");
        } else if (hasNextPage) {
            addCSSClass(pageList.getScrollBox(),
                        "kie-end-scroll");
        }

        setPagination(showPagination);

        if (visibleItems.size() == 1) {
            addCSSClass(pageList.getScrollBox(),
                        "kie-scrollbox-show-overflow");
        } else {
            removeCSSClass(pageList.getScrollBox(),
                           "kie-scrollbox-show-overflow");
        }

        pageList.setVisibleItems(visibleItems);
    }

    @EventHandler("nextPage")
    @SuppressWarnings("unsued")
    public void onNextPageClick(@ForEvent("click") final Event event) {
        if (!hasCSSClass(nextPage,
                         "disabled")) {
            setVisibleItemsList(currentPage + 1);
        }
    }

    @EventHandler("prevPage")
    @SuppressWarnings("unsued")
    public void onPrevPageClick(@ForEvent("click") final Event event) {
        if (!hasCSSClass(prevPage,
                         "disabled")) {
            setVisibleItemsList(currentPage - 1);
        }
    }

    public HTMLElement getElement() {
        return pagination;
    }

    public interface PageList<T> {

        void setVisibleItems(List<T> visibleItems);

        Div getScrollBox();
    }
}