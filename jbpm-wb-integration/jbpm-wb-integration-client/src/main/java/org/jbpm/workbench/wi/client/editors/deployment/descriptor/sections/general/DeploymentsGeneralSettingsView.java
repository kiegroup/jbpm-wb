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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.general;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.AuditMode;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.PersistenceMode;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.RuntimeStrategy;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieEnumSelectElement;

@Templated
public class DeploymentsGeneralSettingsView implements SectionView<DeploymentsGeneralSettingsPresenter> {

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    @Inject
    @DataField("runtime-strategies")
    private KieEnumSelectElement<RuntimeStrategy> runtimeStrategiesSelect;

    @Inject
    @DataField("persistence-unit-name")
    private HTMLInputElement persistenceUnitName;

    @Inject
    @DataField("persistence-modes")
    private KieEnumSelectElement<PersistenceMode> persistenceModesSelect;

    @Inject
    @DataField("audit-persistence-unit-name")
    private HTMLInputElement auditPersistenceUnitName;

    @Inject
    @DataField("audit-modes")
    private KieEnumSelectElement<AuditMode> auditModesSelect;

    private DeploymentsGeneralSettingsPresenter presenter;

    @Override
    public void init(final DeploymentsGeneralSettingsPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("persistence-unit-name")
    public void onPersistenceUnitNameChanged(final ChangeEvent ignore) {
        presenter.setPersistenceUnitName(persistenceUnitName.value);
    }

    @EventHandler("audit-persistence-unit-name")
    public void onAuditPersistenceUnitNameChanged(final ChangeEvent ignore) {
        presenter.setAuditPersistenceUnitName(auditPersistenceUnitName.value);
    }

    public void setPersistenceUnitName(final String persistenceUnitName) {
        this.persistenceUnitName.value = persistenceUnitName;
    }

    public void setAuditPersistenceUnitName(final String auditPersistenceUnitName) {
        this.auditPersistenceUnitName.value = auditPersistenceUnitName;
    }

    public void setupAuditModeSelect(final DeploymentDescriptorModel model) {
        auditModesSelect.setup(
                AuditMode.values(),
                AuditMode.valueOf(model.getAuditMode()),
                auditMode -> {
                    model.setAuditMode(auditMode.name());
                    presenter.fireChangeEvent();
                });
    }

    public void setupPersistenceModesSelect(final DeploymentDescriptorModel model) {
        persistenceModesSelect.setup(
                PersistenceMode.values(),
                PersistenceMode.valueOf(model.getPersistenceMode()),
                persistenceMode -> {
                    model.setPersistenceMode(persistenceMode.name());
                    presenter.fireChangeEvent();
                });
    }

    public void setupRuntimeStrategiesSelect(final DeploymentDescriptorModel model) {
        runtimeStrategiesSelect.setup(
                RuntimeStrategy.values(),
                RuntimeStrategy.valueOf(model.getRuntimeStrategy()),
                runtimeStrategy -> {
                    model.setRuntimeStrategy(runtimeStrategy.name());
                    presenter.fireChangeEvent();
                });
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }
}
