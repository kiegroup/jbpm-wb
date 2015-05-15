/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.dm.client.document.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.dm.client.i18n.Constants;
import org.jbpm.console.ng.dm.model.CMSContentSummary;
import org.jbpm.console.ng.dm.model.events.DocumentDefSelectionEvent;
import org.jbpm.console.ng.dm.model.events.DocumentRemoveSearchEvent;
import org.jbpm.console.ng.dm.model.events.DocumentsHomeSearchEvent;
import org.jbpm.console.ng.dm.model.events.DocumentsListSearchEvent;
import org.jbpm.console.ng.dm.model.events.DocumentsParentSearchEvent;
import org.jbpm.console.ng.dm.model.events.NewDocumentEvent;
import org.jbpm.console.ng.dm.service.DocumentServiceEntryPoint;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;

@Dependent
@WorkbenchScreen(identifier = "Documents Presenter")
public class DocumentListPresenter extends AbstractScreenListPresenter<CMSContentSummary> {



    public interface DocumentListView extends ListView<CMSContentSummary, DocumentListPresenter> {

        void updatePathLink();

    }

    @Inject
    private DocumentListView view;

    @Inject
    private Caller<DocumentServiceEntryPoint> dataServices;

    @Inject
    private Event<DocumentDefSelectionEvent> documentDefSelected;

    List<CMSContentSummary> currentDocuments = null;

    String id = null;

    CMSContentSummary currentCMSContentSummary;

    private Constants constants = GWT.create(Constants.class);

    public DocumentListPresenter() {
        super();
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData(Range visibleRange) {
        ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
        if (currentFilter == null) {
            currentFilter = new PortableQueryFilter(visibleRange.getStart(), visibleRange.getLength(), false, "",
                    (columnSortList.size() > 0) ? columnSortList.get(0).getColumn().getDataStoreName() : "",
                    (columnSortList.size() > 0) ? columnSortList.get(0).isAscending() : true);
        }
        // If we are refreshing after a search action, we need to go
        // back to offset 0
        if (currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
                || currentFilter.getParams().get("textSearch") == null
                || currentFilter.getParams().get("textSearch").equals("")) {
            currentFilter.setOffset(visibleRange.getStart());
            currentFilter.setCount(visibleRange.getLength());
        } else {
            currentFilter.setOffset(0);
            currentFilter.setCount(view.getListGrid().getPageSize());
        }
        currentFilter.setOrderBy((columnSortList.size() > 0) ? columnSortList.get(0).getColumn().getDataStoreName()
                : "");
        currentFilter.setIsAscending((columnSortList.size() > 0) ? columnSortList.get(0).isAscending() : true);
        if (id != null) {
            Map<String, Object> params = currentFilter.getParams();
            if (params == null){
                params = new HashMap<String, Object>();
                currentFilter.setParams(params);
            }
            currentFilter.getParams().put("id", id);
        }

        dataServices.call(new RemoteCallback<List<CMSContentSummary>>() {
            @Override
            public void callback(List<CMSContentSummary> response) {
                view.hideBusyIndicator();
                dataProvider.updateRowCount(response.size(), true);
                dataProvider.updateRowData(0, response);
                updateRefreshTimer();

                List<CMSContentSummary> documents = response;
                if (documents.size() > 0) {
                    CMSContentSummary first = documents.get(0);
                    if (first != null) {
                        currentCMSContentSummary = first.getParent();
                    }
                }
                view.updatePathLink();

            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.hideBusyIndicator();
                view.displayNotification("Error: Getting documents from CMIS Server: " + message);
                GWT.log(throwable.toString());
                return true;
            }
        }).getDocuments(id);
    }


    private void refreshDocumentList(String id) {
        this.id = id;
        refreshGrid();
    }

    public void onProcessDefSelectionEvent(@Observes DocumentsListSearchEvent event) {
        if (event.getSummary().getContentType().toString().equalsIgnoreCase("FOLDER")) {
            currentCMSContentSummary = event.getSummary();
            this.refreshDocumentList(event.getSummary().getId());
        } else {
            // it is a document!
            CMSContentSummary document = null;
            PlaceStatus instanceDetailsStatus = placeManager.getStatus(new DefaultPlaceRequest("Document Details"));
            if (instanceDetailsStatus == PlaceStatus.OPEN) {
                placeManager.closePlace("Document Details");
            }
            document = event.getSummary();
            placeManager.goTo("Document Details");
            documentDefSelected.fire(new DocumentDefSelectionEvent(document.getId()));
        }
        // Window.open(linkURL + "?documentId=" + event.getSummary().getId()
        // + "&documentName=" + event.getSummary().getName(),
        // "_blank", "");
    }

    public void onDocumentsParentSelectionEvent(@Observes DocumentsParentSearchEvent event) {
        if (currentCMSContentSummary != null) {
            if (currentCMSContentSummary.getParent() != null) {
                this.refreshDocumentList(currentCMSContentSummary.getParent().getId());
            } else {
                this.refreshDocumentList(null);
            }
        }
    }

    public void onDocumentsHomeSelectionEvent(@Observes DocumentsHomeSearchEvent event) {
        this.refreshDocumentList(null);
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

    @OnOpen
    public void onOpen() {
        id = null;
        currentCMSContentSummary = null;
        refreshDocumentList(null);
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @OnFocus
    public void onFocus() {
        refreshDocumentList(null);
    }

    @WorkbenchPartView
    public UberView<DocumentListPresenter> getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.DocumentsList();
    }
}
