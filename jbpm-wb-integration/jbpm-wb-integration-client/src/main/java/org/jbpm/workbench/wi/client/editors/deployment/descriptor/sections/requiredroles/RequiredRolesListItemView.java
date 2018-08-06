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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.requiredroles;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("#root")
public class RequiredRolesListItemView implements RequiredRolesListItemPresenter.View {

    @Inject
    @Named("span")
    @DataField("role")
    private HTMLElement role;

    @Inject
    @DataField("remove-button")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private HTMLAnchorElement removeButton;

    @Inject
    @DataField("edit-button")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private HTMLAnchorElement modifyButton;
    
    private RequiredRolesListItemPresenter presenter;

    @Override
    public void init(final RequiredRolesListItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("remove-button")
    public void onRemoveButtonClicked(final ClickEvent ignore) {
        presenter.remove();
    }
    
    @EventHandler("edit-button")
    public void onEditButtonClicked(final ClickEvent ignore) {
        presenter.openRequiredRoleModal();
    }

    @Override
    public void setRole(final String role) {
        this.role.textContent = role;
    }
}
