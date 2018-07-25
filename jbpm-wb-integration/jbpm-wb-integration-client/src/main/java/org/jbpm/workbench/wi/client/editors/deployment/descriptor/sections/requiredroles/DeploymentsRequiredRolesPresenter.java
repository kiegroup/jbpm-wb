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

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.uberfire.client.promise.Promises;

import elemental2.promise.Promise;

@Dependent
public class DeploymentsRequiredRolesPresenter extends Section<DeploymentDescriptorModel> {

    private DeploymentsRequiredRolesView view;
    private RemoteableClassListPresenter requiredRolesListPresenter;
    private AddSingleValueModal addRequiredRoleModal;

    @Inject
    public DeploymentsRequiredRolesPresenter(final Event<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent,
                                             final MenuItem<DeploymentDescriptorModel> menuItem,
                                             final Promises promises,
                                             final DeploymentsRequiredRolesView view,
                                             final RemoteableClassListPresenter requiredRolesListPresenter,
                                             final AddSingleValueModal addRequiredRoleModal) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.requiredRolesListPresenter = requiredRolesListPresenter;
        this.addRequiredRoleModal = addRequiredRoleModal;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Promise<Void> setup(final DeploymentDescriptorModel model) {
        addRequiredRoleModal.setup(LibraryConstants.AddRequiredRole, LibraryConstants.Role);

        if (model.getRequiredRoles() == null) {
            model.setRequiredRoles(new ArrayList<>());
        }

        requiredRolesListPresenter.setup(
                view.getRequiredRolesTable(),
                model.getRequiredRoles(),
                (requiredRole, presenter) -> presenter.setup(requiredRole, this),
                addRequiredRoleModal,
                null);

        return promises.resolve();
    }

    public void openNewRequiredRoleModal() {
        addRequiredRoleModal.show(this::addRequiredRole);
    }

    public void openModfiyRequireRoleModal(final String role) {
//        addRequiredRoleModal.getView().setValue(role);
//        addRequiredRoleModal.showEditModel(this::addRequiredRole);
    }
    
    void addRequiredRole(final String role) {
        requiredRolesListPresenter.add(role);
        fireChangeEvent();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return requiredRolesListPresenter.getObjectsList().hashCode();
    }

    @Dependent
    public static class RemoteableClassListPresenter extends SectionListPresenter<String, RequiredRolesListItemPresenter> {

        @Inject
        public RemoteableClassListPresenter(final ManagedInstance<RequiredRolesListItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

}
