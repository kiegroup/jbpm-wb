/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.items;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.requiredroles.DeploymentsRequiredRolesPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.UberElementalListItem;

@Dependent
public class RequiredRolesListItemPresenter extends ListItemPresenter<String, DeploymentsRequiredRolesPresenter, RequiredRolesListItemPresenter.View> {

    private String role;
    DeploymentsRequiredRolesPresenter parentPresenter;

    @Inject
    public RequiredRolesListItemPresenter(final View view) {
        super(view);
    }

    @Override
    public RequiredRolesListItemPresenter setup(final String role,
                                                final DeploymentsRequiredRolesPresenter parentPresenter) {
        this.role = role;
        this.parentPresenter = parentPresenter;

        view.init(this);
        view.setRole(role);

        return this;
    }

    @Override
    public void remove() {
        super.remove();
        parentPresenter.fireChangeEvent();
    }

    @Override
    public String getObject() {
        return role;
    }

    public interface View extends UberElementalListItem<RequiredRolesListItemPresenter>,
                                  IsElement {

        void setRole(final String role);
    }
}
