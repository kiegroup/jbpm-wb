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

import org.gwtbootstrap3.client.ui.base.form.AbstractForm;
import org.jbpm.workbench.wi.client.workitem.ServiceTaskUploadFormView.Presenter;
import org.uberfire.client.mvp.UberView;

import com.google.gwt.user.client.Command;

public interface ServiceTaskUploadFormView extends UberView<Presenter> {

    interface Presenter {

        void handleSubmitComplete(AbstractForm.SubmitCompleteEvent event);

        boolean isFileNameValid();
        
        Command onCloseCommand();
    }

    String getFileName();

    void showSelectFileUploadWarning();

    void showUnsupportedFileTypeWarning();

    void showInvalidJarNoPomWarning();

    void showInvalidPomWarning();

    void showUploadFailedError();

    void showUploadingBusy();

    void hideUploadingBusy();
    
    String getSuccessInstallMessage();

    String getSkippedMessage(String serviceTaskNames);

    void show();

    void hide();
    
}
