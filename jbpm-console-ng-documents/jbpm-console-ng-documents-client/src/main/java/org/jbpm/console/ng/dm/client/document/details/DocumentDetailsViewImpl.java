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

package org.jbpm.console.ng.dm.client.document.details;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.dm.client.i18n.Constants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

@Dependent
@Templated(value = "DocumentDetailsViewImpl.html")
public class DocumentDetailsViewImpl extends Composite implements
		DocumentDetailsPresenter.DocumentDetailsView {

	@Inject
	private PlaceManager placeManager;

	private DocumentDetailsPresenter presenter;
	
	private Constants constants = GWT.create(Constants.class);

	@Inject
	@DataField
	public HTML documentIdText;

	@Inject
	@DataField
	public HTML documentNameText;

	@Inject
	@DataField
	public Label documentNameLabel;

	@Inject
	@DataField
	public Label documentIdLabel;

	@Inject
	@DataField
	public Label documentPathLabel;
	
	@Inject
    @DataField
    public Button openDocumentButton;

	@Inject
	private Event<NotificationEvent> notification;

	private Path processAssetPath;

	private String encodedProcessSource;

	@Override
	public void init(final DocumentDetailsPresenter presenter) {
		this.presenter = presenter;

		documentIdLabel.setText(constants.DocumentID());
		documentNameLabel.setText(constants.DocumentName());
		openDocumentButton.setText(constants.DownloadButton());
		documentPathLabel.setText(constants.DownloadButtonLabel());
	}

	@Override
	public void displayNotification(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public HTML getDocumentNameText() {
		return documentNameText;
	}

	@Override
	public HTML getDocumentIdText() {
		return documentIdText;
	}

	@Override
	public Button getOpenDocumentButton() {
		return openDocumentButton;
	}
	
	@EventHandler("openDocumentButton")
    public void openDocumentButton( ClickEvent e ) {
        presenter.downloadDocument();
    }

}
