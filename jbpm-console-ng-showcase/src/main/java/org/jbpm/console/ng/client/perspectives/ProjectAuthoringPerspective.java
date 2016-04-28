/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.client.perspectives;

import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.kie.workbench.common.widgets.client.menu.RepositoryMenu;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@WorkbenchPerspective(identifier = "Authoring")
public class ProjectAuthoringPerspective {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private NewResourcesMenu newResourcesMenu;

    @Inject
    private RepositoryMenu repositoryMenu;

    @Inject
    private AuthoringWorkbenchDocks docks;

    @PostConstruct
    public void setup() {
        docks.setup("Authoring", new DefaultPlaceRequest("org.kie.guvnor.explorer"));
    }


    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        p.setName("Project Authoring Perspective");
        return p;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu("Projects")
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo("org.kie.guvnor.explorer");
                    }
                })
                .endMenu()

                .newTopLevelMenu("New")
                .withItems(newResourcesMenu.getMenuItems())
                .endMenu()

                .newTopLevelMenu("Repository")
                .withItems(repositoryMenu.getMenuItems())
                .endMenu()

                .build();
    }

}
