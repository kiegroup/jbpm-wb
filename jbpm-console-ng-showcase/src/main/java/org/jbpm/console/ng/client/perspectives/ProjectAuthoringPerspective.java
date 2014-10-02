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

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.jbpm.console.ng.client.i18n.Constants;
import org.kie.workbench.common.screens.projecteditor.client.menu.ProjectMenu;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
@WorkbenchPerspective(identifier = "Authoring")
public class ProjectAuthoringPerspective {

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private PlaceManager placeManager;

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private NewResourcesMenu newResourcesMenu;

    @Inject
    private ProjectMenu projectMenu;

    private String projectRootPath;

    private MenuItem ddMenuItem = MenuFactory.newSimpleItem( Constants.INSTANCE.DeploymentDescriptor() ).respondsWith(
            new Command() {
                @Override
                public void execute() {

                    placeManager.goTo( PathFactory.newPath( "kie-deployment-descriptor.xml",
                                                            projectRootPath + "/src/main/resources/META-INF/kie-deployment-descriptor.xml" ) );

                }
            }

                                                                                                                     ).endMenu().build().getItems().get( 0 );

    public ProjectAuthoringPerspective() {
    }

    public void onProjectContextChanged( @Observes final ProjectContextChangeEvent event ) {
        if ( event.getProject() != null ) {
            projectRootPath = event.getProject().getRootPath().toURI();
            ddMenuItem.setEnabled( true );
        } else {
            ddMenuItem.setEnabled( false );
        }
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        p.setName( "Project Authoring Perspective" );

        final PanelDefinition west = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        west.setWidth( 300 );
        west.setMinWidth( 200 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "org.kie.guvnor.explorer" ) ) );
        p.getRoot().insertChild( CompassPosition.WEST, west );
        return p;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu( "Projects" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "org.kie.guvnor.explorer" );
                    }
                } )
                .endMenu()

                .newTopLevelMenu( "New" )
                .withItems( newResourcesMenu.getMenuItems() )
                .endMenu()

                .newTopLevelMenu( "Tools" )
                .withItems( getToolsMenuItems() )
                .endMenu()

                .build();
    }

    private List<MenuItem> getToolsMenuItems() {
        List<MenuItem> toolsMenuItems = projectMenu.getMenuItems();
        toolsMenuItems.add( ddMenuItem );

        return toolsMenuItems;
    }

}
