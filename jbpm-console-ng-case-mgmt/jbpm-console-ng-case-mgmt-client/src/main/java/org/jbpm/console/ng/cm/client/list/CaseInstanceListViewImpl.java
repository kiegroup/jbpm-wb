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

package org.jbpm.console.ng.cm.client.list;

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
import org.jbpm.console.ng.cm.client.util.AbstractView;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.util.CaseInstanceSearchRequest;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Dependent
@Templated(stylesheet = "CaseInstanceListViewImpl.css")
public class CaseInstanceListViewImpl extends AbstractView<CaseInstanceListPresenter> implements CaseInstanceListPresenter.CaseInstanceListView {

    @Inject
    @DataField("search-actions")
    private CaseInstanceListSearchViewImpl actions;

    @Inject
    @DataField("empty-list-item")
    private Div emptyContainer;

    @Inject
    @DataField("list-view")
    private Div viewContainer;

    @Inject
    @AutoBound
    private DataBinder<List<CaseInstanceSummary>> caseInstanceList;

    @Inject
    @Bound
    @DataField("list-container")
    private ListComponent<CaseInstanceSummary, CaseInstanceViewImpl> list;

    @Override
    public void init(CaseInstanceListPresenter presenter) {
        super.init(presenter);
        actions.init(presenter);
        list.addComponentCreationHandler(v -> v.init(presenter));
    }

    @Override
    public CaseInstanceSearchRequest getCaseInstanceSearchRequest() {
        return actions.getCaseInstanceSearchRequest();
    }

    @Override
    public void setCaseInstanceList(final List<CaseInstanceSummary> caseInstanceList) {
        this.caseInstanceList.setModel(caseInstanceList);
        if (caseInstanceList.isEmpty()) {
            removeCSSClass(emptyContainer, "hidden");
        } else {
            addCSSClass(emptyContainer, "hidden");
        }
    }

    @Override
    public HTMLElement getElement() {
        return viewContainer;
    }

}