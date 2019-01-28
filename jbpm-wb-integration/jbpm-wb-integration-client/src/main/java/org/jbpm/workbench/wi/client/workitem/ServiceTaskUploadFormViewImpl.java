/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.workitem;

import org.gwtbootstrap3.client.shared.event.ModalHideEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.client.ui.gwt.FormPanel;
import org.jbpm.workbench.wi.client.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.common.client.common.FormStyleLayout;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ServiceTaskUploadFormViewImpl
        extends BaseModal implements ServiceTaskUploadFormView {
    
    private static final String FORM_ELEMENT_SERVICE_TASK_ULOAD = "fileServiceTaskUploadElement";

    private FormStyleLayout form = new FormStyleLayout();

    private Presenter presenter;

    protected FileUpload uploader;

    public ServiceTaskUploadFormViewImpl() {
        this.setTitle(Constants.INSTANCE.ServiceTaskUpload());
        this.setBody(doUploadForm());
        
        ModalFooter footer = new ModalFooter();
        Button cancelButton =  new Button(Constants.INSTANCE.Cancel());
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        footer.add(cancelButton);

        this.add(footer);
    }

    private Form doUploadForm() {
        form.setAction(getWebContext() + "/jbpm/servicetasks");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        form.setType(FormType.HORIZONTAL);

        form.addSubmitCompleteHandler(new Form.SubmitCompleteHandler() {
            public void onSubmitComplete(final Form.SubmitCompleteEvent event) {
                presenter.handleSubmitComplete(event);
            }
        });

        uploader = new FileUpload(() -> {
            if (presenter.isFileNameValid()) {
                form.submit();
            }
        });

        uploader.setName(FORM_ELEMENT_SERVICE_TASK_ULOAD);

        form.addAttribute("File",
                          uploader);
        
        return form;
    }

    private String getWebContext() {
        String context = GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/",
                                                        "");
        if (context.endsWith("/")) {
            context = context.substring(0,
                                        context.length() - 1);
        }
        return context;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        
        this.addHideHandler((ModalHideEvent evt) -> this.presenter.onCloseCommand().execute());
    }

    @Override
    public void showUploadingBusy() {
        BusyPopup.showMessage(Constants.INSTANCE.Uploading());
    }

    @Override
    public void hideUploadingBusy() {
        BusyPopup.close();
    }

    @Override
    public void showSelectFileUploadWarning() {
        showErrorMessage(Constants.INSTANCE.SelectFileUpload());
    }

    @Override
    public void showUnsupportedFileTypeWarning() {
        showErrorMessage(Constants.INSTANCE.UnsupportedFileType());
    }

    @Override
    public void showInvalidJarNoPomWarning() {
        showErrorMessage(Constants.INSTANCE.InvalidJarNotPom());
    }

    @Override
    public void showInvalidPomWarning() {
        showErrorMessage(Constants.INSTANCE.InvalidPom());
    }

    @Override
    public void showUploadFailedError() {
        showErrorMessage(Constants.INSTANCE.UploadFailed() + Constants.INSTANCE.InternalUploadError());
    }

    @Override
    public String getFileName() {
        return uploader.getFilename();
    }

    @Override
    public String getSuccessInstallMessage() {
        return Constants.INSTANCE.AddTaskSuccess();
    }
    
    private void showErrorMessage(final String message) {
        ErrorPopup.showMessage(message);
    }

}
