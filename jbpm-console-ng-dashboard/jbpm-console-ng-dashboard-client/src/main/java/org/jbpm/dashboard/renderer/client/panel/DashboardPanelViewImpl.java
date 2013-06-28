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
import com.google.gwt.dom.client.Node;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.dashboard.renderer.service.ConnectionStatus;
import org.jbpm.dashboard.renderer.client.panel.i18n.Constants;

public class DashboardPanelViewImpl extends Composite implements DashboardPanelPresenter.DashboardView {

    private Constants constants = GWT.create(Constants.class);

    interface DashboardViewBinder
            extends
            UiBinder<VerticalPanel, DashboardPanelViewImpl> {

    }

    private static DashboardViewBinder uiBinder = GWT.create(DashboardViewBinder.class);

    @UiField
    public VerticalPanel panel;

    @UiField
    public Frame frame;

    @UiField
    public HTML message;

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));
        frame.getElement().setId(DOM.createUniqueId());
    }

    public void initContext(ConnectionStatus status, String url){
        if (status.getStatus()==200){
            frame.setUrl(url);
            message.setVisible(false);
        }else{
            frame.setVisible(false);
            message.setHTML(constants.Instructions());
        }
    }

}
