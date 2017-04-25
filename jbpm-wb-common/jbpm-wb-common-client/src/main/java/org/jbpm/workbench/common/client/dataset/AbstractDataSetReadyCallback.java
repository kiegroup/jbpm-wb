/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.common.client.dataset;

import com.google.gwt.core.client.GWT;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.common.client.list.base.AbstractListView;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

public abstract class AbstractDataSetReadyCallback implements DataSetReadyCallback {

    private DefaultWorkbenchErrorCallback errorCallback;

    private ErrorPopupPresenter errorPopup;

    private AbstractListView.BasicListView view;

    private String UUID;

    public AbstractDataSetReadyCallback(final ErrorPopupPresenter errorPopup,
                                        final AbstractListView.BasicListView view,
                                        final String UUID) {
        this(errorPopup,
             view,
             UUID,
             new DefaultWorkbenchErrorCallback());
    }

    public AbstractDataSetReadyCallback(final ErrorPopupPresenter errorPopup,
                                        final AbstractListView.BasicListView view,
                                        final String UUID,
                                        final DefaultWorkbenchErrorCallback errorCallback) {
        this.errorPopup = errorPopup;
        this.view = view;
        this.UUID = UUID;
        this.errorCallback = errorCallback;
    }

    @Override
    public void notFound() {
        view.hideBusyIndicator();
        errorPopup.showMessage(Constants.INSTANCE.DataSetNotFound(UUID));
        GWT.log("DataSet with UUID [ " + UUID + " ] not found.");
    }

    @Override
    public boolean onError(final ClientRuntimeError error) {
        view.hideBusyIndicator();
        errorCallback.error(null,
                            error.getThrowable());
        GWT.log("DataSet with UUID [ " + UUID + " ] error: ",
                error.getThrowable());
        return false;
    }

}
