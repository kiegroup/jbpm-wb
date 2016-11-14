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

package org.jbpm.console.ng.cm.client.roles;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated
public class CaseRolesViewImpl implements CaseRolesPresenter.CaseRolesView {

    @Inject
    @DataField("roles")
    private Div rolesContainer;

    @Inject
    @DataField("role-list")
    private Div roles;

    @Inject
    @DataField("footer")
    private Div footer;

    private Command userAddCommand;

    private Command groupAddCommand;

    @Inject
    private ManagedInstance<CaseRoleItemView> provider;

    @Override
    public void init(final CaseRolesPresenter presenter) {
    }

    @Override
    public void removeAllRoles() {
        removeAllChildren(roles);
    }

    @Override
    public void enableNewRoleAssignments() {
        removeCSSClass(footer, "hidden");
    }

    @Override
    public void disableNewRoleAssignments() {
        addCSSClass(footer, "hidden");
    }

    @Override
    public void setUserAddCommand(final Command command) {
        this.userAddCommand = command;
    }

    @Override
    public void setGroupAddCommand(final Command command) {
        this.groupAddCommand = command;
    }

    @Override
    public void addUser(final String userName, final String roleName, final CaseRolesPresenter.CaseRoleAction... actions) {
        addRoleVIew(userName, roleName, "pficon-user", actions);
    }

    @Override
    public void addGroup(final String groupName, final String roleName, final CaseRolesPresenter.CaseRoleAction... actions) {
        addRoleVIew(groupName, roleName, "pficon-users", actions);
    }

    private void addRoleVIew(final String name, final String roleName, final String iconType, final CaseRolesPresenter.CaseRoleAction... actions) {
        final CaseRoleItemView roleItemView = provider.get();
        roleItemView.setRoleName(roleName);
        roleItemView.setName(name);
        roleItemView.setIconType(iconType);
        for (CaseRolesPresenter.CaseRoleAction action : actions) {
            roleItemView.addAction(action);
        }
        roles.appendChild(roleItemView.getElement());
    }

    @EventHandler("user-add")
    public void onUserAddClick(@ForEvent("click") Event e) {
        if (userAddCommand != null) {
            userAddCommand.execute();
        }
    }

    @EventHandler("group-add")
    public void onGroupAddClick(@ForEvent("click") Event e) {
        if (groupAddCommand != null) {
            groupAddCommand.execute();
        }
    }

    @Override
    public HTMLElement getElement() {
        return rolesContainer;
    }
}