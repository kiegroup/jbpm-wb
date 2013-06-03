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
import com.google.gwt.user.client.Window;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;

import org.jbpm.dashboard.renderer.service.ConnectionStatus;
import org.jbpm.dashboard.renderer.client.panel.i18n.Constants;

@Dependent
@Templated(value = "DashboardPanelView.html")
public class DashboardPanelViewImpl extends Composite implements DashboardPanelPresenter.DashboardView {

    private Constants constants = GWT.create(Constants.class);

    @Inject
    @DataField
    public Frame frame;

    @Inject
    @DataField
    public HTML message;

    private DashboardPanelPresenter presenter;

    final private String dashboardURL = Window.Location.getProtocol()+"//" + Window.Location.getHost()+"/dashbuilder/workspace/jbpm-dashboard?embedded=true";

    private long id;

    @PostConstruct
    protected void init() {
    }

    @Override
    public void init(DashboardPanelPresenter presenter) {
        this.presenter = presenter;
        System.out.println("Dashboard URL: "+dashboardURL);
        presenter.isAppOnline(dashboardURL);
    }

    public void initContext(ConnectionStatus connectionStatus){
        if (connectionStatus.getStatus()==200){
            frame.setUrl(dashboardURL);
            message.setVisible(false);
        }else{
            frame.setVisible(false);
            message.setHTML(constants.Instructions());
        }
    }

}
