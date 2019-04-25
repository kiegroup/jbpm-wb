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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.workbench.wi.client.workitem.project.ProjectServiceTasksPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.SettingsSections;
import org.kie.workbench.common.screens.library.client.settings.sections.branchmanagement.BranchManagementPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.dependencies.DependenciesPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects.ExternalDataObjectsPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.generalsettings.GeneralSettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.KnowledgeBasesPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.persistence.PersistencePresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.validation.ValidationPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;

@Dependent
public class CustomizedSettingsSections implements SettingsSections {

    private final GeneralSettingsPresenter generalSettingsSection;
    private final DependenciesPresenter dependenciesSettingsSection;
    private final KnowledgeBasesPresenter knowledgeBasesSettingsSection;
    private final ExternalDataObjectsPresenter externalDataObjectsSettingsSection;
    private final ValidationPresenter validationSettingsSection;
    private final DeploymentsSectionPresenter deploymentsSettingsSection;
    private final PersistencePresenter persistenceSettingsSection;
    private final ProjectServiceTasksPresenter serviceTasksSection;
    private final BranchManagementPresenter branchManagementPresenter;

    @Inject
    public CustomizedSettingsSections(final GeneralSettingsPresenter generalSettingsSection,
                                      final DependenciesPresenter dependenciesSettingsSection,
                                      final KnowledgeBasesPresenter knowledgeBasesSettingsSection,
                                      final ExternalDataObjectsPresenter externalDataObjectsSettingsSection,
                                      final ValidationPresenter validationSettingsSection,
                                      final DeploymentsSectionPresenter deploymentsSettingsSection,
                                      final PersistencePresenter persistenceSettingsSection,
                                      final ProjectServiceTasksPresenter serviceTasksSection,
                                      final BranchManagementPresenter branchManagementPresenter) {

        this.generalSettingsSection = generalSettingsSection;
        this.dependenciesSettingsSection = dependenciesSettingsSection;
        this.knowledgeBasesSettingsSection = knowledgeBasesSettingsSection;
        this.externalDataObjectsSettingsSection = externalDataObjectsSettingsSection;
        this.validationSettingsSection = validationSettingsSection;
        this.deploymentsSettingsSection = deploymentsSettingsSection;
        this.persistenceSettingsSection = persistenceSettingsSection;
        this.serviceTasksSection = serviceTasksSection;
        this.branchManagementPresenter = branchManagementPresenter;
    }

    @Override
    public List<Section<ProjectScreenModel>> getList() {
        return Arrays.asList(
                generalSettingsSection,
                dependenciesSettingsSection,
                knowledgeBasesSettingsSection,
                externalDataObjectsSettingsSection,
                validationSettingsSection,
                serviceTasksSection,
                deploymentsSettingsSection,
                persistenceSettingsSection,
                branchManagementPresenter
        );
    }
}
