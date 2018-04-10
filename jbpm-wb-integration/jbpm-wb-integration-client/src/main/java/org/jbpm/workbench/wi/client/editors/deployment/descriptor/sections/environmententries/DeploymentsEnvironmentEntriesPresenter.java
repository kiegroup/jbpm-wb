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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.environmententries;

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
public class DeploymentsEnvironmentEntriesPresenter extends Section<DeploymentDescriptorModel> {

    private final DeploymentsEnvironmentEntriesView view;
    private final EnvironmentEntriesListPresenter environmentEntriesPresenters;
    private final AddDoubleValueModal addEnvironmentEntryModal;
    private final ItemObjectModelFactory itemObjectModelFactory;

    @Inject
    public DeploymentsEnvironmentEntriesPresenter(final Event<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent,
                                                  final MenuItem<DeploymentDescriptorModel> menuItem,
                                                  final Promises promises,
                                                  final DeploymentsEnvironmentEntriesView view,
                                                  final EnvironmentEntriesListPresenter environmentEntriesPresenters,
                                                  final AddDoubleValueModal addEnvironmentEntryModal,
                                                  final ItemObjectModelFactory itemObjectModelFactory) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.environmentEntriesPresenters = environmentEntriesPresenters;
        this.addEnvironmentEntryModal = addEnvironmentEntryModal;
        this.itemObjectModelFactory = itemObjectModelFactory;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Promise<Void> setup(final DeploymentDescriptorModel model) {

        addEnvironmentEntryModal.setup(LibraryConstants.AddEnvironmentEntry, LibraryConstants.Name, LibraryConstants.Value);

        if (model.getEnvironmentEntries() == null) {
            model.setEnvironmentEntries(new ArrayList<>());
        }

        environmentEntriesPresenters.setup(
                view.getEnvironmentEntriesTable(),
                model.getEnvironmentEntries(),
                (environmentEntry, presenter) -> presenter.setup(environmentEntry, this));

        return promises.resolve();
    }

    public void openNewEnvironmentEntryModal() {
        addEnvironmentEntryModal.show(this::addEnvironmentEntry);
    }

    void addEnvironmentEntry(final String name, final String value) {
        environmentEntriesPresenters.add(itemObjectModelFactory.newItemObjectModel(name, value));
        fireChangeEvent();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return environmentEntriesPresenters.getObjectsList().hashCode();
    }

    @Dependent
    public static class EnvironmentEntriesListPresenter extends ListPresenter<ItemObjectModel, NamedObjectItemPresenter> {

        @Inject
        public EnvironmentEntriesListPresenter(final ManagedInstance<NamedObjectItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
