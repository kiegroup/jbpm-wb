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
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.pagination.PaginationViewImpl;
import org.uberfire.mvp.Command;

import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated
public class CaseRolesViewImpl implements CaseRolesPresenter.CaseRolesView,PaginationViewImpl.PageList {
    public static int PAGE_SIZE = 3;

    @Inject
    @DataField("roles")
    private Div rolesContainer;

    @Inject
    @DataField("roles-badge")
    Span rolesBadge;

    @Inject
    @DataField("role-list")
    private Div roles;

    @Inject
    @DataField("user-add")
    private Button userAddButton;

    @Inject
    @DataField("scrollbox")
    private Div scrollbox;

    @Inject
    @DataField("pagination")
    private PaginationViewImpl pagination;

    List allElementsList = new ArrayList();

    private Command userAddCommand;

    @Inject
    private ManagedInstance<CaseRoleItemView> provider;

    @Override
    public void init(final CaseRolesPresenter presenter) {

    }

    @Override
    public void removeAllRoles() {
        removeAllChildren(roles);
        allElementsList = new ArrayList();
    }

    @Override
    public void enableNewRoleAssignments() {
        removeCSSClass(userAddButton, "hidden");
    }

    @Override
    public void disableNewRoleAssignments() {
        addCSSClass(userAddButton, "hidden");
    }

    @Override
    public void setUserAddCommand(final Command command) {
        this.userAddCommand = command;
    }

    @Override
    public void addUser(final String userName, final String roleName, final CaseRolesPresenter.CaseRoleAction... actions) {
        addRoleView(userName, roleName, "pficon-user", actions);
    }

    @Override
    public void addGroup(final String groupName, final String roleName, final CaseRolesPresenter.CaseRoleAction... actions) {
        addRoleView(groupName, roleName, "pficon-users", actions);
    }

    private void  addRoleView(final String name, final String roleName, final String iconType, final CaseRolesPresenter.CaseRoleAction... actions) {
        final CaseRoleItemView roleItemView = provider.get();
        roleItemView.setRoleName(roleName);
        roleItemView.setName(name);
        roleItemView.setIconType(iconType);
        for (CaseRolesPresenter.CaseRoleAction action : actions) {
            roleItemView.addAction(action);
        }
        allElementsList.add(roleItemView);
    }

    @Override
    public void setupPagination() {
        rolesBadge.setTextContent(String.valueOf(allElementsList.size()));
        pagination.init(allElementsList, this, PAGE_SIZE);
    }

    @Override
    public void setVisibleItems(List visibleItems) {
        removeAllChildren(roles);
        int visibleItemsSize = visibleItems.size();
        if(visibleItemsSize>0){
            visibleItems.forEach(e -> ((CaseRoleItemView)e).setLastElementStyle(false));
            ((CaseRoleItemView)visibleItems.get(visibleItemsSize-1)).setLastElementStyle(true);
        }
        visibleItems.forEach(e -> roles.appendChild(((CaseRoleItemView)e).getElement()) );
    }

    @Override
    public Div getScrollBox() {
        return scrollbox;
    }

    @EventHandler("user-add")
    public void onUserAddClick(@ForEvent("click") Event e) {
        if (userAddCommand != null) {
            userAddCommand.execute();
        }
    }

    @Override
    public HTMLElement getElement() {
        return rolesContainer;
    }
}