/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.dm.client.document.newdocument;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "NewDocumentViewImpl.html")
public class NewDocumentViewImpl extends Composite implements
        NewDocumentPresenter.NewDocumentView {

    public TextBox documentNameText = new TextBox();

    public Label documentNameLabel = new Label();

    public TextBox documentFolderText = new TextBox();
    public Hidden hiddenDocumentFolderText = new Hidden();

    public Label documentFolderLabel = new Label();

    public Label newDocTypeLabel = new Label();

    public ListBox newDocType = new ListBox();

    public FileUpload fileUpload = new FileUpload();

    public Label fileUploadLabel = new Label();

    @Inject
    @DataField
    public Button createButton;

    @Inject
    @DataField
    public Form formUpload;

    @Inject
    Event<NotificationEvent> notificationEvents;

    private NewDocumentPresenter presenter;

    @Override
    public void init(NewDocumentPresenter p) {
        this.presenter = p;

        createButton.setText("Create");
        createButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                BusyPopup.showMessage("Loading...");
                formUpload.submit();
                // String type = newDocType.getValue();
                // if ("Text File".equals(type)) {
                // DocumentSummary doc = new DocumentSummary(documentNameText
                // .getText() + ".txt", null, documentFolderText.getValue());
                //
                // doc.setContent("test".getBytes());
                // presenter.createDocument(doc);
                // }
            }
        });


        documentNameText.setName("documentName");
        hiddenDocumentFolderText.setName("documentFolder");
        fileUpload.setName("file");
        newDocType.setName("documentType");

        newDocTypeLabel.setText("File Type");
        newDocTypeLabel.setStyleName("control-label");
        documentNameLabel.setText("Document Name");
        documentNameLabel.setStyleName("control-label");
        documentFolderLabel.setText("Document Folder");
        documentFolderLabel.setStyleName("control-label");
        fileUploadLabel.setText("Upload");
        fileUploadLabel.setStyleName("control-label");
        newDocType.addItem("Text File");
        newDocType.addItem("PDF");

        formUpload.setAction(getWebContext() + "/documentview/");
        VerticalPanel allFields = new VerticalPanel();


        HorizontalPanel line = new HorizontalPanel();
        line.setHorizontalAlignment(line.ALIGN_CENTER);
        line.add(documentFolderLabel);
        line.add(documentFolderText);

        line.add(hiddenDocumentFolderText);
        documentFolderText.setName("folder");
        allFields.add(line);

        line = new HorizontalPanel();
        line.setHorizontalAlignment(line.ALIGN_CENTER);
        line.add(fileUploadLabel);
        line.add(fileUpload);
        fileUpload.setName("file");
        allFields.add(line);


        formUpload.add(allFields);

        formUpload.addSubmitHandler(new Form.SubmitHandler() {
            @Override
            public void onSubmit(final Form.SubmitEvent event) {
                String fileName = fileUpload.getFilename();
                if (fileName == null || "".equals(fileName)) {
                    BusyPopup.close();
                    Window.alert("Please select a file!");
                    event.cancel();
                }
            }
        });

        formUpload.addSubmitCompleteHandler(new Form.SubmitCompleteHandler() {
            public void onSubmitComplete(final Form.SubmitCompleteEvent event) {
                if ("OK".equalsIgnoreCase(event.getResults())) {
                    BusyPopup.close();
                    Window.alert("Great!");

                    fileUpload.getElement().setPropertyString("value", "");
                    hide();
                } else if ("NO VALID POM".equalsIgnoreCase(event.getResults())) {
                    BusyPopup.close();
                } else {
                    BusyPopup.close();
                    ErrorPopup.showMessage("Something wrong: " + event.getResults());
                    hide();
                }
            }
        });

    }

    @Override
    public void displayNotification(String notification) {
        notificationEvents.fire(new NotificationEvent(notification));
    }

    private String getWebContext() {
        String context = GWT.getModuleBaseURL().replace(
                GWT.getModuleName() + "/", "");
        if (context.endsWith("/")) {
            context = context.substring(0, context.length() - 1);
        }
        return context;
    }

    public void hide() {
        presenter.close();
    }

    @Override
    public void setFolder(String folder) {
        documentFolderText.setText(folder);
        documentFolderText.setEnabled(false);
        hiddenDocumentFolderText.setValue(folder);
    }

}