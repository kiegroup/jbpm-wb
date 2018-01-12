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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor2;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

@Templated
public class DeploymentsSectionView implements DeploymentsSectionPresenter.View {

    @Inject
    private TranslationService translationService;

    @TranslationKey(defaultValue = "")
    public static final String DeploymentsXmlConcurrentUpdate = "DeploymentsXmlConcurrentUpdate";

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

    @Inject
    @Named("tbody")
    @DataField("marshalling-strategies")
    private HTMLTableSectionElement marshallingStrategiesTable;

    @Inject
    @DataField("add-marshalling-strategy-button")
    private HTMLButtonElement addMarshallingStrategyButton;

    @Inject
    @Named("tbody")
    @DataField("event-listeners")
    private HTMLTableSectionElement eventListenersTable;

    @Inject
    @DataField("add-event-listener-button")
    private HTMLButtonElement addEventListenerButton;

    @Inject
    @Named("tbody")
    @DataField("globals")
    private HTMLTableSectionElement globalsTable;

    @Inject
    @DataField("add-global-button")
    private HTMLButtonElement addGlobalButton;

    @Inject
    @Named("tbody")
    @DataField("required-roles")
    private HTMLTableSectionElement requiredRolesTable;

    @Inject
    @DataField("add-required-role-button")
    private HTMLButtonElement addRequiredRoleButton;

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    private DeploymentsSectionPresenter presenter;

    @Override
    public void init(final DeploymentsSectionPresenter presenter) {
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

    @EventHandler("add-marshalling-strategy-button")
    public void onAddMarshallingStrategyButtonClicked(final ClickEvent ignore) {
        presenter.openNewMarshallingStrategyModal();
    }

    @EventHandler("add-event-listener-button")
    public void onAddEventListenerButtonClicked(final ClickEvent ignore) {
        presenter.openNewEventListenerModal();
    }

    @EventHandler("add-global-button")
    public void onAddGlobalButtonClicked(final ClickEvent ignore) {
        presenter.openNewGlobalModal();
    }

    @EventHandler("add-required-role-button")
    public void onAddRequiredRoleButtonClicked(final ClickEvent ignore) {
        presenter.openNewRequiredRoleModal();
    }

    @Override
    public Element getEventListenersTable() {
        return eventListenersTable;
    }

    @Override
    public Element getMarshallingStrategiesTable() {
        return marshallingStrategiesTable;
    }

    @Override
    public Element getGlobalsTable() {
        return globalsTable;
    }

    @Override
    public Element getRequiredRolesTable() {
        return requiredRolesTable;
    }

    @Override
    public void setPersistenceUnitName(final String persistenceUnitName) {
        this.persistenceUnitName.value = persistenceUnitName;
    }

    @Override
    public void setAuditPersistenceUnitName(final String auditPersistenceUnitName) {
        this.auditPersistenceUnitName.value = auditPersistenceUnitName;
    }

    @Override
    public Element getRuntimeStrategiesContainer() {
        return runtimeStrategies;
    }

    @Override
    public Element getAuditModesContainer() {
        return auditModes;
    }

    @Override
    public String getConcurrentUpdateMessage() {
        return translationService.format(DeploymentsXmlConcurrentUpdate);
    }

    @Override
    public Element getPersistenceModesContainer() {
        return persistenceModes;
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }
}
