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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.configuration;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ItemObjectModelFactory;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.NamedObjectItemPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.Resolver;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.sections.SectionView;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.doublevalue.AddDoubleValueModal;
import org.uberfire.client.promise.Promises;

@Dependent
public class DeploymentsConfigurationPresenter extends Section<DeploymentDescriptorModel> {

    @Inject
    private DeploymentsConfigurationView view;

    @Inject
    private ConfigurationsListPresenter configurationPresenters;

    @Inject
    private AddDoubleValueModal addConfigurationModal;

    @Inject
    private ItemObjectModelFactory itemObjectModelFactory;

    @Inject
    public DeploymentsConfigurationPresenter(final Event<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent,
                                             final MenuItem<DeploymentDescriptorModel> menuItem,
                                             final Promises promises) {

        super(settingsSectionChangeEvent, menuItem, promises);
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Promise<Void> setup(final DeploymentDescriptorModel model) {

        addConfigurationModal.setup(LibraryConstants.AddConfiguration, LibraryConstants.Name, LibraryConstants.Value);

        if (model.getConfiguration() == null) {
            model.setConfiguration(new ArrayList<>());
        }

        configurationPresenters.setup(
                view.getConfigurationsTable(),
                model.getConfiguration(),
                (global, presenter) -> presenter.setup(global, this));

        return promises.resolve();
    }

    public void openNewConfigurationModal() {
        addConfigurationModal.show(this::addConfiguration);
    }

    void addConfiguration(final String name, final String value) {
        configurationPresenters.add(itemObjectModelFactory.newItemObjectModel(name, value));
        fireChangeEvent();
    }

    @Override
    public SectionView getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return configurationPresenters.getObjectsList().hashCode();
    }

    @Dependent
    public static class ConfigurationsListPresenter extends ListPresenter<ItemObjectModel, NamedObjectItemPresenter> {

        @Inject
        public ConfigurationsListPresenter(final ManagedInstance<NamedObjectItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
