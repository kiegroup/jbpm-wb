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

package org.jbpm.workbench.cm.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.UncaughtExceptionHandler;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.menu.UserMenu;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBar;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.cm.client.resources.i18n.ShowcaseConstants.LOG_OUT;
import static org.jbpm.workbench.cm.client.resources.i18n.ShowcaseConstants.ROLE;

@Bundle( "resources/i18n/ShowcaseConstants.properties" )
@EntryPoint
public class ShowcaseEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowcaseEntryPoint.class);

    @Inject
    protected UtilityMenuBar utilityMenuBar;

    @Inject
    protected UserMenu userMenu;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected User identity;

    @Inject
    protected TranslationService translationService;

    @Inject
    protected Caller<AuthenticationService> authService;

    @AfterInitialization
    public void startDefaultWorkbench() {
        hideLoadingPopup();
    }

    public void setupMenu( @Observes final ApplicationReadyEvent event ) {
        final Menus utilityMenus =
                MenuFactory.newTopLevelCustomMenu(userMenu)
                        .endMenu()
                        .build();

        utilityMenuBar.addMenus(utilityMenus);

        addRolesMenuItems();
    }

    public void addRolesMenuItems() {
        for (Menus roleMenus : getRoles()) {
            userMenu.addMenus(roleMenus);
        }
    }

    public List<Menus> getRoles() {
        final Set<Role> roles = identity.getRoles();
        final List<Menus> result = new ArrayList<>(roles.size());

        result.add(MenuFactory.newSimpleItem(translationService.format(LOG_OUT)).respondsWith(new LogoutCommand()).endMenu().build());
        for (final Role role : roles) {
            if (!role.getName().equals("IS_REMEMBER_ME")) {
                result.add(MenuFactory.newSimpleItem(translationService.format(ROLE) + ": " + role.getName()).endMenu().build());
            }
        }

        return result;
    }

    public void hideLoadingPopup() {
        @SuppressWarnings("GwtToHtmlReferences")
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

    @UncaughtExceptionHandler
    public void handleUncaughtException( final Throwable t ) {
        LOGGER.error( "Uncaught exception encountered", t );
    }

    protected class LogoutCommand implements Command {

        @Override
        public void execute() {
            authService.call((Void) -> doRedirect(getRedirectURL())).logout();
        }

        void doRedirect(final String url) {
            redirect(url);
        }

        String getRedirectURL() {
            final String gwtModuleBaseURL = getGWTModuleBaseURL();
            final String gwtModuleName = getGWTModuleName();
            final String url = gwtModuleBaseURL.replaceFirst("/" + gwtModuleName + "/", "/logout.jsp");
            return url;
        }

        String getGWTModuleBaseURL() {
            return GWT.getModuleBaseURL();
        }

        String getGWTModuleName() {
            return GWT.getModuleName();
        }

    }

    public static native void redirect(String url)/*-{
        $wnd.location = url;
    }-*/;

}