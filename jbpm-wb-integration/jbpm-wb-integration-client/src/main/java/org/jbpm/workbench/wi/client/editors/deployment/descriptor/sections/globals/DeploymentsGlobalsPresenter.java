/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.globals;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ItemObjectModelFactory;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.NamedObjectItemPresenter;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.doublevalue.AddDoubleValueModal;
import org.uberfire.client.promise.Promises;

@Dependent
public class DeploymentsGlobalsPresenter extends Section<DeploymentDescriptorModel> {

    private final DeploymentsGlobalsView view;
    private final GlobalsListPresenter globalPresenters;
    private final AddDoubleValueModal addGlobalModal;
    private final ItemObjectModelFactory itemObjectModelFactory;

    @Inject
    public DeploymentsGlobalsPresenter(final Event<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent,
                                       final MenuItem<DeploymentDescriptorModel> menuItem,
                                       final Promises promises,
                                       final DeploymentsGlobalsView view,
                                       final GlobalsListPresenter globalPresenters,
                                       final AddDoubleValueModal addGlobalModal,
                                       final ItemObjectModelFactory itemObjectModelFactory) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.globalPresenters = globalPresenters;
        this.addGlobalModal = addGlobalModal;
        this.itemObjectModelFactory = itemObjectModelFactory;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Promise<Void> setup(final DeploymentDescriptorModel model) {

        addGlobalModal.setup(LibraryConstants.AddGlobal, LibraryConstants.Name, LibraryConstants.Value);

        if (model.getGlobals() == null) {
            model.setGlobals(new ArrayList<>());
        }

        globalPresenters.setup(
                view.getGlobalsTable(),
                model.getGlobals(),
                (global, presenter) -> presenter.setup(global, this));

        return promises.resolve();
    }

    public void openNewGlobalModal() {
        addGlobalModal.show(this::addGlobal);
    }

    void addGlobal(final String name, final String value) {
        globalPresenters.add(itemObjectModelFactory.newItemObjectModel(name, value));
        fireChangeEvent();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return globalPresenters.getObjectsList().hashCode();
    }

    @Dependent
    public static class GlobalsListPresenter extends ListPresenter<ItemObjectModel, NamedObjectItemPresenter> {

        @Inject
        public GlobalsListPresenter(final ManagedInstance<NamedObjectItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
