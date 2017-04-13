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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.jbpm.workbench.common.client.list.AbstractListView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractDataSetReadyCallbackTest {

    @Mock
    ErrorPopupPresenter errorPopup;

    @Mock
    AbstractListView.BasicListView view;

    @Mock
    DataSet dataSet;

    AbstractDataSetReadyCallback dataSetReadyCallback;

    @Before
    public void setup() {
        dataSetReadyCallback = new AbstractDataSetReadyCallback(errorPopup, view, dataSet) {
            @Override
            public void callback(DataSet dataSet) {
                //Do nothing
            }
        };
    }


    @Test
    public void testNotFound() {
        dataSetReadyCallback.notFound();

        verify(view).hideBusyIndicator();
        verify(errorPopup).showMessage(anyString());
    }

    @Test
    public void testOnError() {
        dataSetReadyCallback.onError(mock(ClientRuntimeError.class));

        verify(view).hideBusyIndicator();
        verify(errorPopup).showMessage(anyString());
    }

}