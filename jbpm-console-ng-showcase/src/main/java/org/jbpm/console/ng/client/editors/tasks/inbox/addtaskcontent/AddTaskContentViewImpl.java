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
package org.jbpm.console.ng.client.editors.tasks.inbox.addtaskcontent;

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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class AddTaskContentViewImpl extends Composite
    implements
    AddTaskContentPresenter.InboxView {

    interface AddTaskContentViewImplBinder
        extends
        UiBinder<Widget, AddTaskContentViewImpl> {
    }

    private static AddTaskContentViewImplBinder uiBinder = GWT.create( AddTaskContentViewImplBinder.class );

    @Inject
    private PlaceManager                        placeManager;
    private AddTaskContentPresenter             presenter;
    @UiField
    public Button                               saveContentButton;
    @UiField
    public Button                               addRowButton;
    @UiField
    public Button                               refreshContentButton;
    @UiField
    public TextBox                              taskIdText;
    @UiField
    public FluidContainer                       contentPanel;
    @UiField
    public FluidContainer                       outputPanel;
    @Inject
    private Event<NotificationEvent>            notification;
    private Map<TextBox, TextBox>               textBoxs = new HashMap<TextBox, TextBox>();

    @Override
    public void init(AddTaskContentPresenter presenter) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @UiHandler("addRowButton")
    public void addTaskButton(ClickEvent e) {
        FluidRow horizontalPanel = new FluidRow();
        TextBox keyTextBox = new TextBox();
        keyTextBox.setPlaceholder( " set key " );
        TextBox valueTextBox = new TextBox();
        valueTextBox.setPlaceholder( " set value " );
        horizontalPanel.add( keyTextBox );
        horizontalPanel.add( valueTextBox );
        textBoxs.put( keyTextBox,
                      valueTextBox );
        contentPanel.add( horizontalPanel );
    }

    @UiHandler("saveContentButton")
    public void saveContentButton(ClickEvent e) {
        Map<String, String> values = new HashMap<String, String>();
        for ( Entry<TextBox, TextBox> entry : textBoxs.entrySet() ) {
            values.put( entry.getKey().getText(),
                        entry.getValue().getText() );
        }
        presenter.saveContent( new Long( taskIdText.getText() ),
                               values );
    }

    @UiHandler("refreshContentButton")
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

    public FluidContainer getContentPanel() {
        return contentPanel;
    }

    public FluidContainer getOutputPanel() {
        return outputPanel;
    }

}
