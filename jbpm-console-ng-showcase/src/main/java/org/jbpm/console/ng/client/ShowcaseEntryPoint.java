/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.client;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.client.i18n.Constants;
import org.jbpm.console.ng.cm.client.perspectives.CaseInstanceListPerspective;
import org.jbpm.dashboard.renderer.service.DashboardURLBuilder;
import org.kie.workbench.common.screens.search.client.menu.SearchMenuBuilder;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.entrypoint.DefaultWorkbenchEntryPoint;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.views.pfly.menu.MainBrand;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@EntryPoint
public class ShowcaseEntryPoint extends DefaultWorkbenchEntryPoint {

    protected Constants constants = Constants.INSTANCE;

    protected SyncBeanManager iocManager;

    protected User identity;

    protected DefaultWorkbenchFeaturesMenusHelper menusHelper;

    protected WorkbenchMenuBarPresenter menuBar;

    @Inject
    public ShowcaseEntryPoint( final Caller<AppConfigService> appConfigService,
                               final Caller<PlaceManagerActivityService> pmas,
                               final ActivityBeansCache activityBeansCache,
                               final SyncBeanManager iocManager,
                               final User identity,
                               final DefaultWorkbenchFeaturesMenusHelper menusHelper,
                               final WorkbenchMenuBarPresenter menuBar ) {
        super( appConfigService, pmas, activityBeansCache );
        this.iocManager = iocManager;
        this.identity = identity;
        this.menusHelper = menusHelper;
        this.menuBar = menuBar;
    }

    @Override
    protected void setupMenu() {

        final AbstractWorkbenchPerspectiveActivity defaultPerspective = menusHelper.getDefaultPerspectiveActivity();
        final Menus menus = MenuFactory
                .newTopLevelMenu( constants.Home() ).place( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) ).endMenu()
                .newTopLevelMenu( constants.Authoring() ).withItems( getAuthoringViews() ).endMenu()
                .newTopLevelMenu( constants.Deploy() ).withItems( getDeploymentViews() ).endMenu()
                .newTopLevelMenu( constants.Case_Management() ).withItems( getCaseManagementViews() ).endMenu()
                .newTopLevelMenu( constants.Process_Management() ).withItems( getProcessManagementViews() ).endMenu()
                .newTopLevelMenu( constants.Work() ).withItems( getWorkViews() ).endMenu()
                .newTopLevelMenu( constants.Dashboards() ).withItems( getDashboardsViews() ).endMenu()
                .newTopLevelMenu( constants.Extensions() ).withItems( menusHelper.getExtensionsViews() ).endMenu()
                .newTopLevelMenu( constants.Experimental() ).withItems( getExperimentalViews() ).endMenu()
                .newTopLevelCustomMenu( iocManager.lookupBean( SearchMenuBuilder.class ).getInstance() ).endMenu()
                .build();

        menuBar.addMenus( menus );

        menusHelper.addRolesMenuItems();
        menusHelper.addGroupsMenuItems();
        menusHelper.addWorkbenchConfigurationMenuItem();
        menusHelper.addUtilitiesMenuItems();
    }

    protected List<? extends MenuItem> getAuthoringViews() {
        final List<MenuItem> result = new ArrayList<>( 1 );

        result.add( MenuFactory.newSimpleItem( constants.Process_Authoring() ).perspective( constants.Authoring() ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    protected List<? extends MenuItem> getCaseManagementViews() {
        final List<MenuItem> result = new ArrayList<>( 1 );

        result.add( MenuFactory.newSimpleItem( constants.Cases() ).perspective( CaseInstanceListPerspective.PERSPECTIVE_ID ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    protected List<? extends MenuItem> getProcessManagementViews() {
        final List<MenuItem> result = new ArrayList<>( 3 );

        result.add( MenuFactory.newSimpleItem( constants.Process_Definitions() ).perspective( "Process Definitions" ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Process_Instances() ).perspective( "DataSet Process Instances With Variables" ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Process_Instances_Admin() ).perspective( "Process Admin" ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    protected List<? extends MenuItem> getExperimentalViews() {
        final List<MenuItem> result = new ArrayList<>( 3 );

        result.add( MenuFactory.newSimpleItem( constants.Grid_Base_Test() ).perspective( "Grid Base Test" ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Logs() ).perspective( "Logs" ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Documents() ).perspective( "Documents Perspective" ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    protected List<? extends MenuItem> getDeploymentViews() {
        final List<MenuItem> result = new ArrayList<>( 3 );

        result.add( MenuFactory.newSimpleItem( constants.Execution_Servers() ).place( new DefaultPlaceRequest( "ServerManagementPerspective" ) ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Jobs() ).perspective( "Jobs" ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    protected List<? extends MenuItem> getWorkViews() {
        final List<MenuItem> result = new ArrayList<>( 4 );

        result.add( MenuFactory.newSimpleItem( constants.Tasks_List() ).perspective( "DataSet Tasks" ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Tasks_List_Admin() ).perspective( "Tasks Admin" ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Data_Sets() ).perspective( "DataSetAuthoringPerspective" ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    protected List<? extends MenuItem> getDashboardsViews() {
        final List<MenuItem> result = new ArrayList<>( 2 );

        result.add( MenuFactory.newSimpleItem( constants.Process_Dashboard() ).perspective( "DashboardPerspective" ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Business_Dashboard() ).respondsWith( () -> Window.open( DashboardURLBuilder.getDashboardURL( "/dashbuilder/workspace", null, LocaleInfo.getCurrentLocale().getLocaleName() ), "_blank", "" ) ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    @Produces
    @ApplicationScoped
    public MainBrand createBrandLogo() {
        return () -> new Image( AppResource.INSTANCE.images().logo() );
    }
}
