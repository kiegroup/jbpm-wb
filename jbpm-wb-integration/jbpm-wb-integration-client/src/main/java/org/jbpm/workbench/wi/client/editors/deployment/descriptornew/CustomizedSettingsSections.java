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

package org.jbpm.workbench.wi.client.editors.deployment.descriptornew;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSections;
import org.kie.workbench.common.screens.library.client.settings.dependencies.DependenciesPresenter;
import org.kie.workbench.common.screens.library.client.settings.externaldataobjects.ExternalDataObjectsPresenter;
import org.kie.workbench.common.screens.library.client.settings.generalsettings.GeneralSettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.knowledgebases.KnowledgeBasesPresenter;
import org.kie.workbench.common.screens.library.client.settings.persistence.PersistencePresenter;
import org.kie.workbench.common.screens.library.client.settings.validation.ValidationPresenter;

@Dependent
public class CustomizedSettingsSections implements SettingsSections {

    private final GeneralSettingsPresenter generalSettingsSection;
    private final DependenciesPresenter dependenciesSettingsSection;
    private final KnowledgeBasesPresenter knowledgeBasesSettingsSection;
    private final ExternalDataObjectsPresenter externalDataObjectsSettingsSection;
    private final ValidationPresenter validationSettingsSection;
    private final DeploymentsSectionPresenter deploymentsSettingsSection;
    private final PersistencePresenter persistenceSettingsSection;

    @Inject
    public CustomizedSettingsSections(final GeneralSettingsPresenter generalSettingsSection,
                                      final DependenciesPresenter dependenciesSettingsSection,
                                      final KnowledgeBasesPresenter knowledgeBasesSettingsSection,
                                      final ExternalDataObjectsPresenter externalDataObjectsSettingsSection,
                                      final ValidationPresenter validationSettingsSection,
                                      final DeploymentsSectionPresenter deploymentsSettingsSection,
                                      final PersistencePresenter persistenceSettingsSection) {

        this.generalSettingsSection = generalSettingsSection;
        this.dependenciesSettingsSection = dependenciesSettingsSection;
        this.knowledgeBasesSettingsSection = knowledgeBasesSettingsSection;
        this.externalDataObjectsSettingsSection = externalDataObjectsSettingsSection;
        this.validationSettingsSection = validationSettingsSection;
        this.deploymentsSettingsSection = deploymentsSettingsSection;
        this.persistenceSettingsSection = persistenceSettingsSection;
    }

    @Override
    public List<SettingsPresenter.Section> getList() {
        return Arrays.asList(
                generalSettingsSection,
                dependenciesSettingsSection,
                knowledgeBasesSettingsSection,
                externalDataObjectsSettingsSection,
                validationSettingsSection,
                deploymentsSettingsSection,
                persistenceSettingsSection
        );
    }
}
