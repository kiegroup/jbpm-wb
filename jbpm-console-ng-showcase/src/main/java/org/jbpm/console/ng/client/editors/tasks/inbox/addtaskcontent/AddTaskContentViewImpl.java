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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.enterprise.event.Event;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

@Dependent
public class AddTaskContentViewImpl extends Composite implements AddTaskContentPresenter.InboxView {

    @Inject
    private UiBinder<Widget, AddTaskContentViewImpl> uiBinder;
    @Inject
    private PlaceManager placeManager;
    private AddTaskContentPresenter presenter;
    @UiField
    public Button saveContentButton;
    @UiField
    public Button addRowButton;
    @UiField
    public TextBox taskIdText;
    @UiField
    public ScrollPanel scrollPanel;
    @UiField
    public VerticalPanel contentPanel;
    @Inject
    private Event<NotificationEvent> notification;
    
    private Map<TextBox, TextBox> textBoxs = new HashMap<TextBox, TextBox>();
    
    @Override
    public void init(AddTaskContentPresenter presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("addRowButton")
    public void addTaskButton(ClickEvent e) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        TextBox keyTextBox = new TextBox();
        TextBox valueTextBox = new TextBox();
        horizontalPanel.add(keyTextBox);
        horizontalPanel.add(valueTextBox);
        textBoxs.put(keyTextBox, valueTextBox);
        contentPanel.add(horizontalPanel);
    }
    @UiHandler("saveContentButton")
    public void saveContentButton(ClickEvent e) {
        Map<String, String> values = new HashMap<String, String>();
        for(Entry<TextBox, TextBox> entry : textBoxs.entrySet()){
            values.put(entry.getKey().getText(), entry.getValue().getText());
        }
        presenter.saveContent(new Long(taskIdText.getText()), values);
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }
}
