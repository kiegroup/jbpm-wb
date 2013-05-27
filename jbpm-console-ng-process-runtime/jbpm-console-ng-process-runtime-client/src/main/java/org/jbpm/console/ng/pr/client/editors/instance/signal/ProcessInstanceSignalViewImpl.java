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

package org.jbpm.console.ng.pr.client.editors.instance.signal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "ProcessInstanceSignalViewImpl.html")
public class ProcessInstanceSignalViewImpl extends Composite implements ProcessInstanceSignalPresenter.PopupView {

    private Constants constants = GWT.create( Constants.class );

    private ProcessInstanceSignalPresenter presenter;
    @Inject
    @DataField
    public Button signalButton;

    @Inject
    @DataField
    public Button clearButton;

    @Inject
    @DataField
    public Label signalRefLabel;

    @Inject
    @DataField
    public Label eventLabel;

    @Inject
    @DataField
    public TextBox eventText;

    @DataField
    public SuggestBox signalRefText;

    @Inject
    private Event<NotificationEvent> notification;

    public List<Long> processInstanceIds = new ArrayList<Long>();

    private MultiWordSuggestOracle oracle;

    public ProcessInstanceSignalViewImpl() {
        oracle = new MultiWordSuggestOracle();
        signalRefText = new SuggestBox( oracle );

    }

    @Override
    public void init( ProcessInstanceSignalPresenter presenter ) {
        this.presenter = presenter;
        clearButton.setText( constants.Clear() );
        signalButton.setText( constants.Signal() );
        signalRefLabel.setText( constants.Signal_Ref() );
        eventLabel.setText( constants.Event() );
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @EventHandler("signalButton")
    public void signalButton( ClickEvent e ) {

        for ( Long processInstanceId : this.processInstanceIds ) {
            presenter.signalProcessInstance( processInstanceId );
            displayNotification( constants.Signalling_Process_Instance() + processInstanceId + " " + constants.Signal() + " = "
                                         + signalRefText.getText() + " - " + constants.Event() + " = " + eventText.getText() );
        }
    }

    @EventHandler("clearButton")
    public void clearButton( ClickEvent e ) {
        signalRefText.setValue( "" );
        eventText.setValue( "" );
    }

    @Override
    public void addProcessInstanceId( long processInstanceId ) {
        this.processInstanceIds.add( processInstanceId );
    }

    @Override
    public String getSignalRefText() {
        return signalRefText.getText();
    }

    @Override
    public String getEventText() {
        return eventText.getText();
    }

    @Override
    public void setAvailableSignals( Collection<String> signals ) {
        oracle.addAll( signals );
    }

}
