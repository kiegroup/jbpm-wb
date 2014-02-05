/**
 * Copyright (C) 2012 JBoss Inc
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
package org.jbpm.dashboard.renderer.client.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.dashboard.renderer.service.DashboardRendererService;
import org.jbpm.dashboard.renderer.service.ConnectionStatus;
import org.jbpm.dashboard.renderer.service.DashboardURLBuilder;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "DashboardPanel")
public class DashboardPanelPresenter {

    /** The URL for the jBPM dashbuilder. */
    final private String DASHBOARD_URL_PREFFIX = Window.Location.getProtocol()+"//" + Window.Location.getHost()+ "/dashbuilder/workspace/";
    final private String DASHBOARD_URL_SUFFIX = "/jbpm-dashboard?embedded=true&refresh=true";

    public interface DashboardView
            extends
            IsWidget {

        void initContext(ConnectionStatus status, String url);
    }

    @Inject
    Caller<DashboardRendererService> rendererService;

    @Inject
    DashboardView view;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Dashboard Panel";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    @OnStartup
    public void isAppOnline() {
        final String dashboardUrl = DashboardURLBuilder.getDashboardURL(DASHBOARD_URL_PREFFIX, DASHBOARD_URL_SUFFIX, LocaleInfo.getCurrentLocale().getLocaleName());
        GWT.log("URL for jBPM dashboard: " + dashboardUrl);
        rendererService.call(new RemoteCallback<ConnectionStatus>() {
            @Override
            public void callback(ConnectionStatus  connectionStatus) {
                view.initContext(connectionStatus, dashboardUrl);
            }
        }).getAppStatus(dashboardUrl);
    }

}

