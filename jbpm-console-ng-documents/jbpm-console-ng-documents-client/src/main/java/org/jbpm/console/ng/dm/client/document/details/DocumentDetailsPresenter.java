/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.dm.model.CMSContentSummary;
import org.jbpm.console.ng.dm.model.events.DocumentDefSelectionEvent;
import org.jbpm.console.ng.dm.service.DocumentServiceEntryPoint;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.split.WorkbenchSplitLayoutPanel;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Document Details")
public class DocumentDetailsPresenter {

    public interface DocumentDetailsView extends
            UberView<DocumentDetailsPresenter> {

        void displayNotification(String text);

        HTML getDocumentNameText();

        HTML getDocumentIdText();

        Button getOpenDocumentButton();
    }

    private Menus menus;

    private static String linkURL = "http://127.0.0.1:8888/documentview"; // TODO not hardcoded please!

    @Inject
    private DocumentDetailsView view;

    @Inject
    private Caller<DocumentServiceEntryPoint> dataServices;

    private CMSContentSummary document;

    public DocumentDetailsPresenter() {
    }

    @DefaultPosition
    public Position getPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Document Details";
    }

    @WorkbenchPartView
    public UberView<DocumentDetailsPresenter> getView() {
        return view;
    }

    public void refreshProcessDef(final String documentId) {
        dataServices.call(
                new RemoteCallback<CMSContentSummary>() {
                    @Override
                    public void callback(CMSContentSummary content) {
                        document = content;
                        view.getDocumentIdText().setText(content.getId());
                        view.getDocumentNameText().setText(content.getName());
                    }
                }
        ).getDocument(documentId);
    }

    @OnOpen
    public void onOpen() {
        WorkbenchSplitLayoutPanel splitPanel = (WorkbenchSplitLayoutPanel) view
                .asWidget().getParent().getParent().getParent().getParent()
                .getParent().getParent().getParent().getParent().getParent()
                .getParent().getParent();
        splitPanel.setWidgetMinSize(splitPanel.getWidget(0), 500);
    }

    public void onDocumentDefSelectionEvent(@Observes DocumentDefSelectionEvent event) {
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

}