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

package org.jbpm.console.ng.udc.client.info;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.udc.client.i8n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

@Dependent
@Templated(value = "InfoUsageDataViewImpl.html")
public class InfoUsageDataViewImpl extends Composite implements InfoUsageDataPresenter.InfoHumanEventView {
    private Constants constants = GWT.create(Constants.class);
    
    @Inject
    private Event<NotificationEvent> notification;
    
    @Inject 
    private InfoUsageDataPresenter presenter;
    
    @Override
    public void init(InfoUsageDataPresenter presenter) {
        this.presenter = presenter;
        showComponentAudited();
    }
    
    @Override
    public void showComponentAudited(){
        presenter.showComponentAudited();
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

}
