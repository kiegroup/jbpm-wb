/*
 * Copyright 2012 JBoss Inc
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

package org.jbpm.console.ng.pr.client.editors.definition.diagram;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "ProcessDiagramPopupViewImpl.html")
public class ProcessDiagramPopupViewImpl extends Composite implements ProcessDiagramPopupPresenter.InboxView {

    private ProcessDiagramPopupPresenter presenter;

    @Inject
    @DataField
    public TextBox processDefIdText;

    @Inject
    @DataField
    public TextBox processInstanceIdText;

    @Inject
    @DataField
    public TextBox processPackageNameText;

    @Inject
    @DataField
    public TextBox processVersionText;

    @Inject
    @DataField
    public TextBox processDiagramURLText;

    @Inject
    @DataField
    public Button generateUrlButton;

    @Inject
    @DataField
    public Button closeButton;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( ProcessDiagramPopupPresenter presenter ) {
        this.presenter = presenter;

    }

    @EventHandler("generateUrlButton")
    public void generateUrlButton( ClickEvent e ) {
        presenter.generateURL( processDefIdText.getText(), Long.parseLong( processInstanceIdText.getText() ),
                               processPackageNameText.getText(), processVersionText.getText() );
    }

    @EventHandler("closeButton")
    public void closeButton( ClickEvent e ) {
        presenter.close();
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public TextBox getProcessDefIdText() {
        return processDefIdText;
    }

    @Override
    public TextBox getProcessDiagramURLText() {
        return processDiagramURLText;
    }

    @Override
    public TextBox getProcessPackageNameText() {
        return processPackageNameText;
    }

    @Override
    public TextBox getProcessVersionText() {
        return processVersionText;
    }

    @Override
    public Button getGenerateUrlButton() {
        return generateUrlButton;
    }

    @Override
    public TextBox getProcessInstanceIdText() {
        return processInstanceIdText;
    }

}
