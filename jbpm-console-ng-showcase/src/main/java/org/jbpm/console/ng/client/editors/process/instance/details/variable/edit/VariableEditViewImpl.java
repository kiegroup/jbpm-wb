package org.jbpm.console.ng.client.editors.process.instance.details.variable.edit;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.client.i18n.Constants;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

@Dependent
@Templated(value = "VariableEditViewImpl.html")
public class VariableEditViewImpl extends Composite implements
        VariableEditPresenter.PopupView {
    
    private long processInstanceId;
    private String variableId;
    private String variableText;
    
    
    private VariableEditPresenter presenter;
    
    @Inject
    @DataField
    public TextBox variableTextBox;
    @Inject
    @DataField
    public Button closeButton;
    @Inject
    @DataField
    public Button saveButton;
    @Inject
    @DataField
    public Button clearButton;
    @Inject
    private Event<NotificationEvent> notification;
    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(VariableEditPresenter presenter) {
        this.presenter = presenter;

    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
        
    }

    @Override
    public long getProcessInstanceId() {
        return this.processInstanceId;
    }

    @Override
    public String getVariableText() {
        return this.variableText;
    }

    @Override
    public void setVariableText(String value) {
        this.variableText = value;
        this.variableTextBox.setText(value);
    }

    @Override
    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    @Override
    public String getVariableId() {
        return this.variableId;
    }
    
    @EventHandler("clearButton")
    public void clearButton(ClickEvent e) {
        variableTextBox.setValue("");
    }

    @EventHandler("closeButton")
    public void closeButton(ClickEvent e) {
        presenter.close();
    }
    
    @EventHandler("saveButton")
    public void saveButton(ClickEvent e) {

        // TODO do not hardcode business key for session
        presenter.setProcessVariable("default", variableTextBox.getText());
        displayNotification("Variable updated " + variableId);
        
    }

}
