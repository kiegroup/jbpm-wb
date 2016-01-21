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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.common.services.shared.security.KieWorkbenchPolicy;
import org.guvnor.common.services.shared.security.KieWorkbenchSecurityService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.jbpm.console.ng.client.i18n.Constants;
import org.jbpm.console.ng.ga.forms.service.PlaceManagerActivityService;
import org.jbpm.dashboard.renderer.service.DashboardURLBuilder;
import org.kie.workbench.common.screens.search.client.menu.SearchMenuBuilder;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.menu.AboutMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.WorkbenchConfigurationMenuBuilder;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.menu.MainBrand;
import org.uberfire.client.views.pfly.menu.UserMenu;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBar;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@EntryPoint
public class ShowcaseEntryPoint {

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    public User identity;

    @Inject
    private KieWorkbenchACL kieACL;

    @Inject
    private Caller<KieWorkbenchSecurityService> kieSecurityService;

    @Inject
    private Caller<PlaceManagerActivityService> pmas;

    @Inject
    private ActivityBeansCache activityBeansCache;

    @Inject
    private Caller<AppConfigService> appConfigService;

    @Inject
    private UtilityMenuBar utilityMenuBar;

    @Inject
    private UserMenu userMenu;

    @Inject
    private Caller<AuthenticationService> authService;

    @AfterInitialization
    public void startApp() {
        kieSecurityService.call( new RemoteCallback<String>() {
            public void callback( final String str ) {
                KieWorkbenchPolicy policy = new KieWorkbenchPolicy( str );
                kieACL.activatePolicy( policy );
                setupMenu();
                loadPreferences();
                hideLoadingPopup();
            }
        } ).loadPolicy();

        List<String> allActivities = activityBeansCache.getActivitiesById();
        pmas.call().initActivities( allActivities );
    }

    private void loadPreferences() {
        appConfigService.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( final Map<String, String> response ) {
                ApplicationPreferences.setUp( response );
            }
        } ).loadPreferences();
    }

    private void setupMenu() {

        final AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
        final Menus menus = MenuFactory
                .newTopLevelMenu( constants.Home() ).respondsWith( new Command() {
                    @Override
                    public void execute() {
                        if ( defaultPerspective != null ) {
                            placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
                        } else {
                            Window.alert( "Default perspective not found." );
                        }
                    }
                } ).endMenu()
                .newTopLevelMenu( constants.Authoring() ).withItems( getAuthoringViews() ).endMenu()
                .newTopLevelMenu( constants.Deploy() ).withItems( getDeploymentViews() ).endMenu()
                .newTopLevelMenu( "Case Management" ).withItems( getCaseMGMTViews() ).endMenu()
                .newTopLevelMenu( constants.Process_Management() ).withItems( getProcessMGMTViews() ).endMenu()
                .newTopLevelMenu( constants.Work() ).withItems( getWorkViews() ).endMenu()
                .newTopLevelMenu( constants.Dashboards() ).withItems( getDashboardsViews() ).endMenu()
                .newTopLevelMenu( constants.Experimental() ).withItems( getExperimentalViews() ).endMenu()
                .newTopLevelCustomMenu( iocManager.lookupBean( SearchMenuBuilder.class ).getInstance() ).endMenu()
                .build();

        menubar.addMenus( menus );

        for ( Menus roles : getRoles() ) {
            userMenu.addMenus( roles );
        }

        for ( Menus groups : getGroups() ) {
            userMenu.addMenus( groups );
        }

        final Menus utilityMenus =
                MenuFactory.newTopLevelCustomMenu( iocManager.lookupBean( WorkbenchConfigurationMenuBuilder.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( iocManager.lookupBean( AboutMenuBuilder.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( iocManager.lookupBean( ResetPerspectivesMenuBuilder.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( userMenu )
                        .endMenu()
                        .build();

        utilityMenuBar.addMenus( utilityMenus );
    }

    private List<Menus> getRoles() {
        final List<Menus> result = new ArrayList<Menus>( identity.getRoles().size() );
        for ( final Role role : identity.getRoles() ) {
            if ( !role.getName().equals( "IS_REMEMBER_ME" ) ) {
                result.add( MenuFactory.newSimpleItem( constants.Role() + ": " + role.getName() ).endMenu().build() );
            }
        }
        result.add( MenuFactory.newSimpleItem( constants.LogOut() ).respondsWith( new LogoutCommand() ).endMenu().build() );
        return result;
    }

    private List<Menus> getGroups() {
        final List<Menus> result = new ArrayList<Menus>( identity.getGroups().size() );
        for ( final Group group : identity.getGroups() ) {
            result.add( MenuFactory.newSimpleItem( constants.Group() + ": " + group.getName() ).endMenu().build() );
        }
        return result;
    }

    private List<? extends MenuItem> getAuthoringViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 1 );

        result.add( MenuFactory.newSimpleItem( constants.Process_Authoring() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Authoring" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    private List<? extends MenuItem> getCaseMGMTViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 1 );

        result.add( MenuFactory.newSimpleItem( "Cases" ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Cases" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );


        return result;
    }

    private List<? extends MenuItem> getProcessMGMTViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 4 );

        result.add( MenuFactory.newSimpleItem( constants.Process_Definitions() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Process Definitions" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( " Variables" ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "DataSet Process Instances Variables" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( "Process With Variables" ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "DataSet Process Instances With Variables" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( constants.Process_Instances() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "DataSet Process Instances" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( constants.Process_Instances_Admin() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Process Admin" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    private List<? extends MenuItem> getExperimentalViews() {

        final List<MenuItem> result = new ArrayList<MenuItem>( 4 );


        result.add( MenuFactory.newSimpleItem( "Grid Base Test" ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Grid Base Test" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( constants.Logs() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Logs" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( "Documents" ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Documents Perspective" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    private List<? extends MenuItem> getDeploymentViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 3 );

        result.add( MenuFactory.newSimpleItem( constants.Deployments() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Deployments" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( constants.Jobs() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Jobs" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( constants.Asset_Management() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Asset Management" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );


       /* result.add( MenuFactory.newSimpleItem( "Data Sets (new)" ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "DataSetAuthoringPerspective" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );
        */
        result.add( MenuFactory.newSimpleItem( "DataSet Task List" ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "DataSet Tasks" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );
        return result;
    }

    private List<? extends MenuItem> getWorkViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 4 );

        result.add( MenuFactory.newSimpleItem( constants.Tasks_List() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Tasks" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( constants.Tasks_List_Admin() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "Tasks Admin" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );


        result.add( MenuFactory.newSimpleItem( "Data Sets (new)" ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "DataSetAuthoringPerspective" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( "DataSet Task List" ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "DataSet Tasks" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );
        return result;
    }

    private List<? extends MenuItem> getDashboardsViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 1 );
        result.add( MenuFactory.newSimpleItem( constants.Process_Dashboard() ).respondsWith( new Command() {

            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "DashboardPerspective" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        final String dashbuilderURL = DashboardURLBuilder.getDashboardURL( "/dashbuilder/workspace", null, LocaleInfo.getCurrentLocale().getLocaleName() );
        result.add( MenuFactory.newSimpleItem( constants.Business_Dashboard() ).respondsWith( new Command() {
            @Override
            public void execute() {
                Window.open( dashbuilderURL, "_blank", "" );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    private AbstractWorkbenchPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager
                .lookupBeans( AbstractWorkbenchPerspectiveActivity.class );
        final Iterator<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop:
        while ( perspectivesIterator.hasNext() ) {
            final IOCBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
                break outer_loop;
            } else {
                iocManager.destroyBean( instance );
            }
        }
        return defaultPerspective;
    }

    // Fade out the "Loading application" pop-up

    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {
            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    private class LogoutCommand implements Command {

        @Override
        public void execute() {
            authService.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void response ) {
                    redirect( GWT.getHostPageBaseURL() + "jbpm-console.html" );
                }
            } ).logout();
        }
    }

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

    @Produces
    @ApplicationScoped
    public MainBrand createBrandLogo() {
        return new MainBrand() {
            @Override
            public Widget asWidget() {
                return new Image( AppResource.INSTANCE.images().logo() );
            }
        };
    }

}
