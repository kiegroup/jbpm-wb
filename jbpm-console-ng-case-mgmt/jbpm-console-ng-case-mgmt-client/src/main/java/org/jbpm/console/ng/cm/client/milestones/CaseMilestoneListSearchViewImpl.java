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

import org.jboss.errai.common.client.dom.Button;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.StateSync;


import org.jboss.errai.ui.shared.api.annotations.AutoBound;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.cm.client.util.AbstractView;
import org.jbpm.console.ng.cm.util.CaseMilestoneSearchRequest;


import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated("CaseMilestoneListViewImpl.html#search-actions")
public class CaseMilestoneListSearchViewImpl extends AbstractView<CaseMilestoneListPresenter> {

    @Inject
    @DataField("search-actions")
    private Div actions;

    @Inject
    @DataField("sort-alpha-asc")
    private Button sortAlphaAsc;

    @Inject
    @DataField("sort-alpha-desc")
    private Button sortAlphaDesc;

    @Inject
    @AutoBound
    private DataBinder<CaseMilestoneSearchRequest> searchRequest;

    @Override
    public HTMLElement getElement() {
        return actions;
    }

    private CaseMilestoneListPresenter presenter;

    @Override
    public void init(final CaseMilestoneListPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        searchRequest.setModel(new CaseMilestoneSearchRequest(), StateSync.FROM_MODEL);
        searchRequest.addPropertyChangeHandler( e -> presenter.searchCaseMilestones() );
        onSortAlphaDesc(null);
    }

    public CaseMilestoneSearchRequest getCaseInstanceSearchRequest() {
        return searchRequest.getModel();
    }

    @EventHandler("sort-alpha-asc")
    public void onSortAlphaAsc(final @ForEvent("click") MouseEvent event) {
        onSortChange(sortAlphaAsc, sortAlphaDesc, false);
    }

    @EventHandler("sort-alpha-desc")
    public void onSortAlphaDesc(final @ForEvent("click") MouseEvent event) {
        onSortChange(sortAlphaDesc, sortAlphaAsc, true);
    }

    private void onSortChange(final HTMLElement toHide, final HTMLElement toShow, final Boolean sortByAsc){
        addCSSClass(toHide, "hidden");
        removeCSSClass(toShow, "hidden");
        final CaseMilestoneSearchRequest model = searchRequest.getWorkingModel();
        model.setSortByAsc(sortByAsc);
    }

}