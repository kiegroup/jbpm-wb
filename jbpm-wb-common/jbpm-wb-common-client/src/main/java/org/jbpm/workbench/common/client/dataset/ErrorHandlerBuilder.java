/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.function.Consumer;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.Command;

@Dependent
public class ErrorHandlerBuilder implements DataSetReadyCallback {

    private DefaultWorkbenchErrorCallback errorCallback;
    private String UUID;
    private Consumer<DataSet> callback;
    private Command emptyResultsCallback;

    @Inject
    public void setErrorCallback(DefaultWorkbenchErrorCallback errorCallback) {
        this.errorCallback = errorCallback;
    }

    public void setCallback(Consumer<DataSet> callback) {
        this.callback = callback;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public void setEmptyResultsCallback(Command emptyResultsCallback) {
        this.emptyResultsCallback = emptyResultsCallback;
    }

    public ErrorHandlerBuilder withEmptyResultsCallback(final Command command) {
        this.setEmptyResultsCallback(command);
        return this;
    }

    public ErrorHandlerBuilder withUUID(final String UUID) {
        this.setUUID(UUID);
        return this;
    }

    public ErrorHandlerBuilder withDataSetCallback(final Consumer<DataSet> dataSetCallback) {
        this.setCallback(dataSetCallback);
        return this;
    }

    protected void setEmptyResults() {
        if (emptyResultsCallback != null) {
            emptyResultsCallback.execute();
        }
    }

    @Override
    public void notFound() {
        setEmptyResults();
        showErrorMessage(Constants.INSTANCE.DataSetNotFound(UUID));
        GWT.log("DataSet with UUID [ " + UUID + " ] not found.");
    }

    public void showErrorMessage(final String message){
        ErrorPopup.showMessage(message);
    }

    @Override
    public boolean onError(final ClientRuntimeError error) {
        setEmptyResults();
        errorCallback.error(error.getThrowable());
        GWT.log("DataSet with UUID [ " + UUID + " ] error: ",
                error.getThrowable());
        return false;
    }

    @Override
    public void callback(final DataSet dataSet) {
        callback.accept(dataSet);
    }

}
