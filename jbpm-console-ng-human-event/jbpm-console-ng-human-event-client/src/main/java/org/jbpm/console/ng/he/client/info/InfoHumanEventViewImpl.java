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

package org.jbpm.console.ng.he.client.info;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.workbench.events.NotificationEvent;

import com.google.gwt.user.client.ui.Composite;

@Dependent
@Templated(value = "InfoHumanEventViewImpl.html")
public class InfoHumanEventViewImpl extends Composite implements InfoHumanEventPresenter.InfoHumanEventView {
    
    @Inject
    private Event<NotificationEvent> notification;
    
    @Inject 
    private InfoHumanEventPresenter presenter;

    @Override
    public void init(InfoHumanEventPresenter presenter) {
        this.presenter = presenter;
        
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

}
