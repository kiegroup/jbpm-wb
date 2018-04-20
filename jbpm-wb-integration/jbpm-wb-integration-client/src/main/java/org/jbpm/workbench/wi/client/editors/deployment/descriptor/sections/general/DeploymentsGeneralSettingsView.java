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
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;

@Templated
public class DeploymentsGeneralSettingsView implements SectionView<DeploymentsGeneralSettingsPresenter> {

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    @Inject
    @DataField("runtime-strategies")
    private HTMLDivElement runtimeStrategies;

    @Inject
    @DataField("persistence-unit-name")
    private HTMLInputElement persistenceUnitName;

    @Inject
    @DataField("persistence-modes")
    private HTMLDivElement persistenceModes;

    @Inject
    @DataField("audit-persistence-unit-name")
    private HTMLInputElement auditPersistenceUnitName;

    @Inject
    @DataField("audit-modes")
    private HTMLDivElement auditModes;

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

    public Element getRuntimeStrategiesContainer() {
        return runtimeStrategies;
    }

    public Element getAuditModesContainer() {
        return auditModes;
    }

    public Element getPersistenceModesContainer() {
        return persistenceModes;
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }
}
