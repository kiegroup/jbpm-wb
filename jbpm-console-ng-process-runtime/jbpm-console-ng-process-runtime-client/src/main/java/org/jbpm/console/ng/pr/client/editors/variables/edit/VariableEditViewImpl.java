package org.jbpm.console.ng.pr.client.editors.variables.edit;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;

import com.google.gwt.user.client.ui.Composite;



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
    public Label variableIdLabel;
    
    @Inject
    @DataField
    public Label variableIdUILabel;
    
     @Inject
    @DataField
    public Label variableTextLabel;

    
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
        clearButton.setText(constants.Clear());
        saveButton.setText(constants.Save());
        variableIdUILabel.setText(constants.Variables_Name());
        variableTextLabel.setText(constants.Variable_Value());

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
 
    @EventHandler("saveButton")
    public void saveButton(ClickEvent e) {

        // TODO do not hardcode business key for session
        presenter.setProcessVariable(variableTextBox.getText());
        displayNotification("Variable updated " + variableId);
        
    }

  @Override
  public void setVariableIdLabel(String variableId) {
    variableIdLabel.setText(variableId);
  }

}
