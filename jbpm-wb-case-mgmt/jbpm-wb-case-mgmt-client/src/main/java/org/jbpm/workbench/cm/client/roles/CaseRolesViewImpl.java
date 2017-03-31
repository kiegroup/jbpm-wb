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

package org.jbpm.workbench.cm.client.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.events.CaseRoleAssignmentListLoadEvent;
import org.jbpm.workbench.cm.client.pagination.PaginationViewImpl;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.client.util.CaseRolesAssignmentFilterBy;
import org.jbpm.workbench.cm.client.util.Select;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.*;


@Dependent
@Templated
public class CaseRolesViewImpl  extends AbstractView<CaseRolesPresenter>
        implements CaseRolesPresenter.CaseRolesView, PaginationViewImpl. PageList {
    public static int PAGE_SIZE = 3;

    @Inject
    @DataField("roles")
    private Div rolesContainer;

    @Inject
    @DataField("roles-badge")
    Span rolesBadge;

    @Inject
    @DataField("filter-select")
    private Select filterSelect;

    @Inject
    @DataField("scrollbox")
    private Div scrollbox;

    @Inject
    @DataField("empty-list-item")
    private Div emptyContainer;

    @Inject
    @DataField("pagination")
    private PaginationViewImpl pagination;

    @Inject
    private TranslationService translationService;

    List <CaseRoleAssignmentSummary> allFilteredElements = new ArrayList();

    @Inject
    @Bound
    @DataField("role-list")
    private ListComponent<CaseRoleAssignmentSummary, CaseRoleItemView> roleAssignments;

    @Inject
    @AutoBound
    private DataBinder<List<CaseRoleAssignmentSummary>> caseRolesList;

    private javax.enterprise.event.Event<CaseRoleAssignmentListLoadEvent> roleAssignmentListLoadEvent;

    @Inject
    public void setRoleAssignmentListLoadEvent(final javax.enterprise.event.Event<CaseRoleAssignmentListLoadEvent> roleAssignmentListLoadEvent) {
        this.roleAssignmentListLoadEvent = roleAssignmentListLoadEvent;
    }

    @Override
    public void init(final CaseRolesPresenter presenter) {
        this.presenter = presenter;
        roleAssignments.addComponentCreationHandler(v -> v.init(presenter));
        roleAssignments.addComponentDestructionHandler(v -> v.unLinkEvent());
    }

    @PostConstruct
    public void init() {
        for(CaseRolesAssignmentFilterBy filterBy : CaseRolesAssignmentFilterBy.values()){
            filterSelect.addOption(translationService.format(filterBy.getLabel()), filterBy.getLabel());
        }
        filterSelect.refresh();
    }


    public void setRolesAssignmentList(final List<CaseRoleAssignmentSummary> caseRoleAssignmentSummaryList) {
        allFilteredElements = caseRoleAssignmentSummaryList;
        if (caseRoleAssignmentSummaryList.isEmpty()) {
            removeCSSClass(emptyContainer, "hidden");
        } else {
            addCSSClass(emptyContainer, "hidden");
        }
        rolesBadge.setTextContent(String.valueOf(allFilteredElements.size()));
        pagination.init(allFilteredElements, this, PAGE_SIZE);
    }

    @Override
    public void removeAllRoles() {
        roleAssignments.deselectAll();
        caseRolesList.setModel(Collections.emptyList());
    }

    public String getFilterValue() {
        return filterSelect.getValue();
    }

    @Override
    public void resetPagination() {
        pagination.setCurrentPage(0);
    }

    @Override
    public void setVisibleItems(List visibleItems) {
        removeAllRoles();
        this.caseRolesList.setModel(visibleItems);
        int maxWidth =scrollbox.getBoundingClientRect().getWidth().intValue() - 70;
        roleAssignmentListLoadEvent.fire(new CaseRoleAssignmentListLoadEvent(maxWidth));
        int rolesListSize = visibleItems.size();
        if(rolesListSize > 0){
            roleAssignments.getComponent(rolesListSize-1).setLastElementStyle();
        }
    }

    @Override
    public Div getScrollBox() {
        return scrollbox;
    }

    @EventHandler("filter-select")
    public void onRolesAssignmentFilterChange(@ForEvent("change") Event e) {
        resetPagination();
        presenter.filterElements();
    }

    @Override
    public HTMLElement getElement() {
        return rolesContainer;
    }
}