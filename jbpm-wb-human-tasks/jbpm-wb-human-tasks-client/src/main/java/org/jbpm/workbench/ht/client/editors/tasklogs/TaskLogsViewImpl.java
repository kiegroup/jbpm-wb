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

package org.jbpm.workbench.ht.client.editors.tasklogs;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import org.gwtbootstrap3.client.ui.Label;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.ht.client.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskLogsViewImpl.html")
public class TaskLogsViewImpl extends Composite implements TaskLogsPresenter.TaskLogsView {

    @Inject
    @DataField
    public HTML logTextArea;

    @Inject
    @DataField
    public Label logTextLabel;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( final TaskLogsPresenter presenter ) {
        logTextLabel.setText( constants.Task_Log() );
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public void setLogTextAreaText( final String text ) {
        logTextArea.setHTML( text );
    }

}
