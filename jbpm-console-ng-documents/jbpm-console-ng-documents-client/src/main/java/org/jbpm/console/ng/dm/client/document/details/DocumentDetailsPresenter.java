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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.dm.client.i18n.Constants;
import org.jbpm.console.ng.dm.model.CMSContentSummary;
import org.jbpm.console.ng.dm.model.events.DocumentDefSelectionEvent;
import org.jbpm.console.ng.dm.service.DocumentServiceEntryPoint;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.split.WorkbenchSplitLayoutPanel;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Document Details")
public class DocumentDetailsPresenter {

	private PlaceRequest place;

	public interface DocumentDetailsView extends
			UberView<DocumentDetailsPresenter> {

		void displayNotification(String text);

		HTML getDocumentNameText();

		HTML getDocumentIdText();

		Button getOpenDocumentButton();
	}

	private Menus menus;

	private static String linkURL = "http://127.0.0.1:8888/documentview"; // TODO
																			// not
																			// hardcoded
																			// please!

	@Inject
	private PlaceManager placeManager;
	
	private Constants constants = GWT.create(Constants.class);

	@Inject
	private DocumentDetailsView view;

	@Inject
	private Caller<DocumentServiceEntryPoint> dataServices;

	private CMSContentSummary document;

	public DocumentDetailsPresenter() {
		makeMenuBar();
	}

	@DefaultPosition
	public Position getPosition() {
		return CompassPosition.EAST;
	}

	@OnStartup
	public void onStartup(final PlaceRequest place) {
		this.place = place;
	}

	@WorkbenchPartTitle
	public String getTitle() {
		return "Document Details";
	}

	@WorkbenchPartView
	public UberView<DocumentDetailsPresenter> getView() {
		return view;
	}

	private void changeStyleRow(String processDefName, String processDefVersion) {
		// processDefStyleEvent.fire( new ProcessDefStyleEvent( processDefName,
		// processDefVersion ) );
	}

	public void refreshProcessDef(final String documentId) {
		dataServices.call(new RemoteCallback<CMSContentSummary>() {
			@Override
			public void callback(CMSContentSummary content) {
				document = content;
				view.getDocumentIdText().setText(content.getId());
				view.getDocumentNameText().setText(content.getName());
			}
		}, new ErrorCallback<Message>() {
			@Override
			public boolean error(Message message, Throwable throwable) {
				org.kie.uberfire.client.common.popups.errors.ErrorPopup.showMessage("Unexpected error encountered : "
						+ throwable.getMessage());
				return true;
			}
		}).getDocument(documentId);
	}

	@OnOpen
	public void onOpen() {
		WorkbenchSplitLayoutPanel splitPanel = (WorkbenchSplitLayoutPanel) view
				.asWidget().getParent().getParent().getParent().getParent()
				.getParent().getParent().getParent().getParent().getParent()
				.getParent().getParent();
		splitPanel.setWidgetMinSize(splitPanel.getWidget(0), 500);
	}

	public void onDocumentDefSelectionEvent(
			@Observes DocumentDefSelectionEvent event) {
		refreshProcessDef(event.getDocumentId());
	}

	public void downloadDocument() {
		if (document != null) {
			Window.open(linkURL + "?documentId=" + document.getId()
					+ "&documentName=" + document.getName(),
					"_blank", "");
		}
	}

	@WorkbenchMenu
	public Menus getMenus() {
		return menus;
	}

	private void makeMenuBar() {
		// menus = MenuFactory
		// .newTopLevelMenu( constants.New_Instance()).respondsWith(new
		// Command() {
		// @Override
		// public void execute() {
		// PlaceRequest placeRequestImpl = new DefaultPlaceRequest(
		// "Form Display Popup" );
		// placeRequestImpl.addParameter( "processId",
		// view.getProcessIdText().getText() );
		// placeRequestImpl.addParameter( "domainId",
		// view.getDeploymentIdText().getText() );
		// placeRequestImpl.addParameter( "processName",
		// view.getProcessNameText().getText() );
		// placeManager.goTo( placeRequestImpl );
		// }
		// }).endMenu()
		// .newTopLevelMenu( constants.Options())
		// .withItems(getOptions())
		// .endMenu()
		// .newTopLevelMenu( constants.Refresh() )
		// .respondsWith( new Command() {
		// @Override
		// public void execute() {
		// refreshProcessDef( view.getDeploymentIdText().getText(),
		// view.getProcessNameText().getText() );
		// view.displayNotification(
		// constants.Process_Definition_Details_Refreshed() );
		// }
		// } )
		// .endMenu().build();

	}

	private List<MenuItem> getOptions() {
		// List<MenuItem> menuItems = new ArrayList<MenuItem>(2);
		//
		// menuItems.add( MenuFactory.newSimpleItem(
		// constants.View_Process_Model()).respondsWith( new Command() {
		// @Override
		// public void execute() {
		// PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Designer"
		// );
		//
		// // if ( view.getEncodedProcessSource() != null ) {
		// placeRequestImpl.addParameter( "readOnly", "true" );
		// //placeRequestImpl.addParameter( "encodedProcessSource",
		// view.getEncodedProcessSource() );
		// placeRequestImpl.addParameter("processId",
		// view.getProcessIdText().getText());
		// placeRequestImpl.addParameter("deploymentId",
		// view.getDeploymentIdText().getText());
		//
		// //}
		// placeManager.goTo( view.getProcessAssetPath(), placeRequestImpl );
		// }
		// } ).endMenu().build().getItems().get( 0 ) );
		//
		// menuItems.add( MenuFactory.newSimpleItem(
		// constants.View_Process_Instances()).respondsWith( new Command() {
		// @Override
		// public void execute() {
		// PlaceRequest placeRequestImpl = new DefaultPlaceRequest(
		// "Process Instances" );
		// placeRequestImpl.addParameter( "processName",
		// view.getProcessNameText().getText() );
		// placeManager.goTo( placeRequestImpl );
		// }
		// } ).endMenu().build().getItems().get( 0 ) );
		//
		//
		// return menuItems;
		return null;
	}

}
