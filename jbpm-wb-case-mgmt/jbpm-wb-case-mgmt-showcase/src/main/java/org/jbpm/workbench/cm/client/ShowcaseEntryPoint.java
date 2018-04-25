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

package org.jbpm.workbench.cm.client;

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
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.jbpm.workbench.cm.client.perspectives.CaseInstanceListPerspective;
import org.kie.server.api.exception.KieServicesHttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.client.views.pfly.menu.UserMenu;
import org.uberfire.client.views.pfly.widgets.ErrorPopup;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.cm.client.resources.i18n.ShowcaseConstants.*;

@Bundle("resources/i18n/ShowcaseConstants.properties")
@EntryPoint
public class ShowcaseEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowcaseEntryPoint.class);

    @Inject
    protected WorkbenchMegaMenuPresenter menuBar;

    @Inject
    protected UserMenu userMenu;

    @Inject
    protected TranslationService translationService;

    @Inject
    protected Caller<AuthenticationService> authService;

    @Inject
    protected ErrorPopup errorPopup;

    public static native void redirect(String url)/*-{
        $wnd.location = url;
    }-*/;

    @AfterInitialization
    public void startDefaultWorkbench() {
        hideLoadingPopup();
    }

    public void setupMenu(@Observes final ApplicationReadyEvent event) {
        final Menus menus =
                MenuFactory
                        .newTopLevelMenu(translationService.format(CASE_LIST)).perspective(CaseInstanceListPerspective.PERSPECTIVE_ID).endMenu()
                        .newTopLevelCustomMenu(userMenu).endMenu()
                        .build();

        addUserMenus();

        menuBar.addMenus(menus);
    }

    public void addUserMenus() {
        final Menus userMenus = MenuFactory
                .newTopLevelMenu(translationService.format(LOG_OUT))
                .respondsWith(new LogoutCommand())
                .endMenu()
                .build();

        userMenu.addMenus(userMenus);
    }

    public void hideLoadingPopup() {
        @SuppressWarnings("GwtToHtmlReferences")
        final Element e = RootPanel.get("loading").getElement();

        new Animation() {
            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }
        }.run(500);
    }

    @UncaughtExceptionHandler
    public void handleUncaughtException(final Throwable t) {
        if (t instanceof KieServicesHttpException) {
            final KieServicesHttpException kieException = (KieServicesHttpException) t;
            if (kieException.getHttpCode() == 401) {
                errorPopup.showError(translationService.format(KIE_SERVER_ERROR_401));
            } else if (kieException.getHttpCode() == 403) {
                errorPopup.showError(translationService.format(KIE_SERVER_ERROR_403));
            } else {
                errorPopup.showError(translationService.format(GENERIC_EXCEPTION,
                                                               kieException.getExceptionMessage()));
            }
        } else {
            errorPopup.showError(translationService.format(GENERIC_EXCEPTION,
                                                           t.getMessage()));
        }
        LOGGER.error("Uncaught exception encountered",
                     t);
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
            final String url = gwtModuleBaseURL.replaceFirst("/" + gwtModuleName + "/",
                                                             "/logout.jsp");
            return url;
        }

        String getGWTModuleBaseURL() {
            return GWT.getModuleBaseURL();
        }

        String getGWTModuleName() {
            return GWT.getModuleName();
        }
    }
}