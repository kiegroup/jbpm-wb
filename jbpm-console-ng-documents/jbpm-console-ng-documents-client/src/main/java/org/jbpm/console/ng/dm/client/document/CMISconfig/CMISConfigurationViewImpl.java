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

package org.jbpm.console.ng.dm.client.document.CMISconfig;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.dm.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.NotificationEvent.NotificationType;

@Dependent
@Templated(value = "CMISConfigurationViewImpl.html")
public class CMISConfigurationViewImpl extends Composite implements
		CMISConfigurationPresenter.CMISConfigurationView {

	@Inject
	private PlaceManager placeManager;

	private CMISConfigurationPresenter presenter;

	private Constants constants = GWT.create(Constants.class);

	@Inject
	private Event<NotificationEvent> notification;

	@Inject
    @DataField
    public Label accordionLabel;

    @Inject
    @DataField
    public Label webServicesACLLabel;

    @Inject
    @DataField
    public TextBox webServicesACLBox;

    @Inject
    @DataField
    public Label webServicesDiscoveryLabel;

    @Inject
    @DataField
    public TextBox webServicesDiscoveryBox;

    @Inject
    @DataField
    public Label webServicesMultifilingLabel;

    @Inject
    @DataField
    public TextBox webServicesMultifilingBox;

    @Inject
    @DataField
    public Label webServicesNavigationLabel;

    @Inject
    @DataField
    public TextBox webServicesNavigationBox;

    @Inject
    @DataField
    public Label webServicesObjectLabel;

    @Inject
    @DataField
    public TextBox webServicesObjectBox;

    @Inject
    @DataField
    public Label webServicesPolicyLabel;

    @Inject
    @DataField
    public TextBox webServicesPolicyBox;

    @Inject
    @DataField
    public Label webServicesRelationshipLabel;

    @Inject
    @DataField
    public TextBox webServicesRelationshipBox;

    @Inject
    @DataField
    public Label webServicesRepositoryLabel;

    @Inject
    @DataField
    public TextBox webServicesRepositoryBox;

    @Inject
    @DataField
    public Label webServicesVersioningLabel;

    @Inject
    @DataField
    public TextBox webServicesVersioningBox;

    @Inject
    @DataField
    public Label repositoryIDLabel;

    @Inject
    @DataField
    public TextBox repositoryIDBox;

    @Inject
    @DataField
    public Label userLabel;

    @Inject
    @DataField
    public TextBox userBox;

    @Inject
    @DataField
    public Label passwordLabel;

    @Inject
    @DataField
    public TextBox passwordBox;

    @Inject
    @DataField
    public Button configureButton;

    @Inject
    @DataField
    public Button testButton;

	@Override
	public void init(final CMISConfigurationPresenter presenter) {
		this.presenter = presenter;
        accordionLabel.setText("CMIS Configuration");

        webServicesACLLabel.setText("Webservices ACL");
        webServicesDiscoveryLabel.setText("Webservices Discovery");
        webServicesMultifilingLabel.setText("Webservices Multifiling");
        webServicesNavigationLabel.setText("Webservices Navigation");
        webServicesObjectLabel.setText("Webservices Object");
        webServicesPolicyLabel.setText("Webservices Policy");
        webServicesRelationshipLabel.setText("Webservices Relationship");
        webServicesRepositoryLabel.setText("Webservices Repository");
        webServicesVersioningLabel.setText("Webservices Versioning");
        repositoryIDLabel.setText("Repository ID");
        userLabel.setText("User");
        passwordLabel.setText("Password");


        configureButton.setText("Save");
        testButton.setText("Test Connection");
	}

	@Override
	public void displayNotification(String text) {
		displayNotification(text, NotificationType.INFO);
	}

	@Override
	public void displayNotification(String text, NotificationType type) {
		notification.fire(new NotificationEvent(text,type));
	}

	@EventHandler("configureButton")
    public void configureButton( ClickEvent e ) {
        presenter.configureParameters();
    }

	@EventHandler("testButton")
    public void testButton( ClickEvent e ) {
        presenter.testConnection();
    }

	@Override
	public TextBox getWSACLTextBox() {
		return webServicesACLBox;
	}

	@Override
	public TextBox getWSDiscoveryTextBox() {
		return webServicesDiscoveryBox;
	}

	@Override
	public TextBox getWSMultifilingTextBox() {
		return webServicesMultifilingBox;
	}

	@Override
	public TextBox getWSNavigationTextBox() {
		return webServicesNavigationBox;
	}

	@Override
	public TextBox getWSObjectTextBox() {
		return webServicesObjectBox;
	}

	@Override
	public TextBox getWSPolicyTextBox() {
		return webServicesPolicyBox;
	}

	@Override
	public TextBox getWSRelationshipTextBox() {
		return webServicesRelationshipBox;
	}

	@Override
	public TextBox getWSRepositoryTextBox() {
		return webServicesRepositoryBox;
	}

	@Override
	public TextBox getWSVersioningTextBox() {
		return webServicesVersioningBox;
	}

	@Override
	public TextBox getUserTextBox() {
		return userBox;
	}

	@Override
	public TextBox getPasswordTextBox() {
		return passwordBox;
	}

	@Override
	public TextBox getRepositoryIDTextBox() {
		return repositoryIDBox;
	}

	@Override
	public Button getConfigureButton() {
		return configureButton;
	}

	@Override
	public Button getTestButton() {
		return testButton;
	}

}
