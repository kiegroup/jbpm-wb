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
package org.jbpm.console.ng.client.editors.tasks.inbox.addtaskcontent.erraiui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskSelectionEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated(value="AddTaskContentViewImpl.html")
public class AddTaskContentViewImpl extends Composite
    implements
    AddTaskContentPresenter.InboxView {


    @Inject
    private PlaceManager                        placeManager;
    private AddTaskContentPresenter             presenter;
    @Inject
    @DataField
    public Button                               saveContentButton;
    @Inject
    @DataField
    public Button                               addRowButton;
    @Inject
    @DataField
    public Button                               refreshContentButton;
    @Inject
    @DataField
    public TextBox                              taskIdText;
    @Inject
    @DataField
    public VerticalPanel                       contentPanel;
    @Inject
    @DataField
    public VerticalPanel                       outputPanel;
    @Inject
    private Event<NotificationEvent>            notification;
    private Map<TextBox, TextBox>               textBoxs = new HashMap<TextBox, TextBox>();

    @Override
    public void init(AddTaskContentPresenter presenter) {
        this.presenter = presenter;
        
    }

    @EventHandler("addRowButton")
    public void addTaskButton(ClickEvent e) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        TextBox keyTextBox = new TextBox();
        
        TextBox valueTextBox = new TextBox();
        
        horizontalPanel.add( keyTextBox );
        horizontalPanel.add( valueTextBox );
        textBoxs.put( keyTextBox,
                      valueTextBox );
        contentPanel.add( horizontalPanel );
    }

    @EventHandler("saveContentButton")
    public void saveContentButton(ClickEvent e) {
        Map<String, String> values = new HashMap<String, String>();
        for ( Entry<TextBox, TextBox> entry : textBoxs.entrySet() ) {
            values.put( entry.getKey().getText(),
                        entry.getValue().getText() );
        }
        presenter.saveContent( new Long( taskIdText.getText() ),
                               values );
    }

    @EventHandler("refreshContentButton")
    public void getContentButton(ClickEvent e) {
        presenter.getContentByTaskId( new Long( taskIdText.getText() ) );
    }

    public void displayNotification(String text) {
        notification.fire( new NotificationEvent( text ) );
    }

    public void receiveSelectedNotification(@Observes TaskSelectionEvent event) {
        taskIdText.setText( String.valueOf( event.getTaskId() ) );
        presenter.getContentByTaskId( new Long( taskIdText.getText() ) );
    }

    public VerticalPanel getContentPanel() {
        return contentPanel;
    }

    public VerticalPanel getOutputPanel() {
        return outputPanel;
    }

}
