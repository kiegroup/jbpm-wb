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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.dashboard.renderer.service.DashboardRendererService;
import org.jbpm.dashboard.renderer.service.ConnectionStatus;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "DashboardPanel")
public class DashboardPanelPresenter {

    final private String DASHBOARD_URL = Window.Location.getProtocol()+"//" + Window.Location.getHost()+ "/dashbuilder/workspace/jbpm-dashboard?embedded=true";

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

    @OnStart
    public void isAppOnline() {
        rendererService.call(new RemoteCallback<ConnectionStatus>() {
            @Override
            public void callback(ConnectionStatus  connectionStatus) {
                view.initContext(connectionStatus, DASHBOARD_URL);
            }
        }).getAppStatus(DASHBOARD_URL);
    }

}

