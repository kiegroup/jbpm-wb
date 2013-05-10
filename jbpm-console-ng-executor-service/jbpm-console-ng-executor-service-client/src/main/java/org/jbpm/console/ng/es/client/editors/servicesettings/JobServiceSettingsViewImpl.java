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
package org.jbpm.console.ng.es.client.editors.servicesettings;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

@Dependent
@Templated(value = "JobServiceSettingsViewImpl.html")
public class JobServiceSettingsViewImpl extends Composite 
		implements JobServiceSettingsPresenter.JobServiceSettingsView {

    @Inject
    @DataField
    public IntegerBox numberOfExecutorsText;
    @Inject
    @DataField
    public TextBox frequencyText;
    @Inject
    @DataField
    public Button startStopButton;
    @Inject
    @DataField
    public Label startedLabel;
    @Inject
    Event<NotificationEvent> notificationEvents;
	private JobServiceSettingsPresenter presenter;
	private Constants constants = GWT.create(Constants.class);
	
	@Override
    public void init(JobServiceSettingsPresenter p) {
		this.presenter = p;
		this.presenter.init();
	}

    @EventHandler("startStopButton")
    public void startStopButton(ClickEvent e) {
    	presenter.initService(numberOfExecutorsText.getValue(), frequencyText.getText());
    }
    
	@Override
    public void displayNotification(String notification) {
		notificationEvents.fire(new NotificationEvent(notification));
	}
	
	@Override
    public Focusable getNumberOfExecutorsText() {
		return numberOfExecutorsText;
	}
	
	@Override
    public void setFrequencyText(String frequency) {
		this.frequencyText.setValue(frequency);
	}
	
	@Override
    public void setNumberOfExecutors(Integer numberOfExecutors) {
		this.numberOfExecutorsText.setValue(numberOfExecutors);
	}
	
	@Override
    public void setStartedLabel(Boolean started) {
		this.startedLabel.setText(started ? constants.Started() : constants.Stopped());
	}
	
	@Override
	public void alert(String message) {
		Window.alert(message); //TODO improve??
	}
}
