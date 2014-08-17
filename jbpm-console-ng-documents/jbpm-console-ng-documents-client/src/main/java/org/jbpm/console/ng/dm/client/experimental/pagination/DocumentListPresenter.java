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

package org.jbpm.console.ng.dm.client.experimental.pagination;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.dm.model.CMSContentSummary;
import org.jbpm.console.ng.dm.model.events.DocumentDefSelectionEvent;
import org.jbpm.console.ng.dm.model.events.DocumentRemoveSearchEvent;
import org.jbpm.console.ng.dm.model.events.DocumentsListSearchEvent;
import org.jbpm.console.ng.dm.model.events.DocumentsParentSearchEvent;
import org.jbpm.console.ng.dm.model.events.NewDocumentEvent;
import org.jbpm.console.ng.dm.service.DocumentServiceEntryPoint;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

@Dependent
@WorkbenchScreen(identifier = "Documents Presenter")
public class DocumentListPresenter {

	public interface DocumentListView extends UberView<DocumentListPresenter> {

		void displayNotification(String text);

		String getCurrentFilter();

		void setCurrentFilter(String filter);

		DataGrid<CMSContentSummary> getDataGrid();

		void showBusyIndicator(String message);

		void hideBusyIndicator();
	}

	private static String linkURL = "http://127.0.0.1:8888/documentview"; // TODO
																			// not
																			// hardcoded
																			// please!

	private Menus menus;

	@Inject
	private DocumentListView view;

	@Inject
	private PlaceManager placeManager;

	@Inject
	private Caller<DocumentServiceEntryPoint> dataServices;

	@Inject
	private Event<DocumentDefSelectionEvent> documentDefSelected;

	List<CMSContentSummary> currentDocuments = null;

	CMSContentSummary currentCMSContentSummary;

	private ListDataProvider<CMSContentSummary> dataProvider = new ListDataProvider<CMSContentSummary>();

	private Constants constants = GWT.create(Constants.class);

	@WorkbenchPartTitle
	public String getTitle() {
		return "Documents";
	}

	@WorkbenchPartView
	public UberView<DocumentListPresenter> getView() {
		return view;
	}

	public DocumentListPresenter() {
		makeMenuBar();
	}

	public void refreshDocumentList(String id) {
		dataServices.call(new RemoteCallback<List<CMSContentSummary>>() {
			@Override
			public void callback(List<CMSContentSummary> documents) {
				currentDocuments = documents;
				if (documents.size() > 0) {
					CMSContentSummary first = documents.get(0);
					if (first != null) {
						currentCMSContentSummary = first.getParent();
					}
				}
				filterProcessList(view.getCurrentFilter());
			}
		}).getDocuments(id);
	}

	public void filterProcessList(String filter) {

		dataProvider.getList().clear();
		dataProvider.getList().addAll(
				new ArrayList<CMSContentSummary>(currentDocuments));
		dataProvider.refresh();

	}

	public void addDataDisplay(HasData<CMSContentSummary> display) {
		dataProvider.addDataDisplay(display);
	}

	public ListDataProvider<CMSContentSummary> getDataProvider() {
		return dataProvider;
	}

	public void refreshData() {
		dataProvider.refresh();
	}

	@OnOpen
	public void onOpen() {
		currentCMSContentSummary = null;
		refreshDocumentList(null);
	}

	@OnFocus
	public void onFocus() {
		if (currentCMSContentSummary != null) {
			refreshDocumentList(currentCMSContentSummary.getId());
		} else {
			refreshDocumentList(null);
		}
	}

	@WorkbenchMenu
	public Menus getMenus() {
		return menus;
	}

	private void makeMenuBar() {
		menus = MenuFactory.newTopLevelMenu(constants.Refresh())
				.respondsWith(new Command() {
					@Override
					public void execute() {
						refreshDocumentList(null);
						view.setCurrentFilter("");
						view.displayNotification("Refresh complete.");
					}
				}).endMenu().newTopLevelMenu("Configure Repository")
				.respondsWith(new Command() {
					@Override
					public void execute() {
						CMSContentSummary document = null;
						PlaceStatus instanceDetailsStatus = placeManager
								.getStatus(new DefaultPlaceRequest(
										"CMIS Configuration"));
						if (instanceDetailsStatus == PlaceStatus.OPEN) {
							placeManager.closePlace("CMIS Configuration");
						}
						placeManager.goTo("CMIS Configuration");
					}
				}).endMenu().build();

	}

	public void onProcessDefSelectionEvent(
			@Observes DocumentsListSearchEvent event) {
		if (event.getSummary().getContentType().toString()
				.equalsIgnoreCase("FOLDER")) {
			currentCMSContentSummary = event.getSummary();
			this.refreshDocumentList(event.getSummary().getId());
		} else {
			// it is a document!
			CMSContentSummary document = null;
			PlaceStatus instanceDetailsStatus = placeManager
					.getStatus(new DefaultPlaceRequest("Document Details"));
			if (instanceDetailsStatus == PlaceStatus.OPEN) {
				placeManager.closePlace("Document Details");
			}
			document = event.getSummary();
			placeManager.goTo("Document Details");
			documentDefSelected.fire(new DocumentDefSelectionEvent(document
					.getId()));
		}
		// Window.open(linkURL + "?documentId=" + event.getSummary().getId()
		// + "&documentName=" + event.getSummary().getName(),
		// "_blank", "");
	}

	public void onDocumentsParentSelectionEvent(
			@Observes DocumentsParentSearchEvent event) {
		if (currentCMSContentSummary != null) {
			if (currentCMSContentSummary.getParent() != null) {
				this.refreshDocumentList(currentCMSContentSummary.getParent()
						.getId());
			} else {
				this.refreshDocumentList(null);
			}
		}
	}

	public void onDocumentRemoveEvent(@Observes DocumentRemoveSearchEvent event) {
		this.dataServices.call(new RemoteCallback<Void>() {
			@Override
			public void callback(Void response) {
				refreshDocumentList(currentCMSContentSummary.getId());
			}
		}).removeDocument(event.getSummary().getId());
	}

	public void onDocumentAddedEvent(@Observes NewDocumentEvent event) {
		refreshDocumentList(currentCMSContentSummary.getId());
	}
}
