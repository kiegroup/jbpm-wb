package org.jbpm.console.ng.pr.client.editors.instance.signal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

@Dependent
@Templated(value = "ProcessInstanceSignalViewImpl.html")
public class ProcessInstanceSignalViewImpl extends Composite implements
        ProcessInstanceSignalPresenter.PopupView {

    private ProcessInstanceSignalPresenter presenter;
    @Inject
    @DataField
    public Button signalButton;
    @Inject
    @DataField
    public Button clearButton;
    @Inject
    @DataField
    public TextBox eventText;
    
    @DataField
    public SuggestBox signalRefText;
    @Inject
    private Event<NotificationEvent> notification;
    public List<Long> processInstanceIds = new ArrayList<Long>();
    private Constants constants = GWT.create(Constants.class);
    private MultiWordSuggestOracle oracle;

    public ProcessInstanceSignalViewImpl() {
        oracle = new MultiWordSuggestOracle();
        signalRefText = new SuggestBox(oracle);
    }

    @Override
    public void init(ProcessInstanceSignalPresenter presenter) {
        this.presenter = presenter;

    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @EventHandler("signalButton")
    public void signalButton(ClickEvent e) {

        for (Long processInstanceId : this.processInstanceIds) {
            presenter.signalProcessInstance(processInstanceId);
            displayNotification("Signal of process instance " + processInstanceId + " signal " + signalRefText.getText() + " event " + eventText.getText());
        }
    }

    @EventHandler("clearButton")
    public void clearButton(ClickEvent e) {
        signalRefText.setValue("");
        eventText.setValue("");
    }

    @Override
    public void addProcessInstanceId(long processInstanceId) {
        this.processInstanceIds.add(processInstanceId);
    }

    public String getSignalRefText() {
        return signalRefText.getText();
    }

    public String getEventText() {
        return eventText.getText();
    }

    @Override
    public void setAvailableSignals(Collection<String> signals) {
        oracle.addAll(signals);
    }
}
