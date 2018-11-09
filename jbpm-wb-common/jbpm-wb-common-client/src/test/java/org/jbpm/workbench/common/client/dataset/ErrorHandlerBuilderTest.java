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

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ErrorHandlerBuilderTest {

    @Mock
    DefaultWorkbenchErrorCallback errorCallback;

    @InjectMocks
    @Spy
    ErrorHandlerBuilder dataSetReadyCallback;

    @Before
    public void setup() {
        doNothing().when(dataSetReadyCallback).showErrorMessage(any());
    }

    @Test
    public void testNotFound() {
        final Command command = mock(Command.class);
        dataSetReadyCallback.setEmptyResultsCallback(command);

        dataSetReadyCallback.notFound();

        verify(command).execute();
        verify(dataSetReadyCallback).showErrorMessage(any());
        verify(errorCallback,
               never()).error(any());
    }

    @Test
    public void testOnError() {
        final Command command = mock(Command.class);
        dataSetReadyCallback.setEmptyResultsCallback(command);

        dataSetReadyCallback.onError(mock(ClientRuntimeError.class));

        verify(command).execute();
        verify(errorCallback).error(any());
        verify(dataSetReadyCallback,
               never()).showErrorMessage(any());
    }

    @Test
    public void testCallback() {
        final Consumer<DataSet> consumer = mock(Consumer.class);
        dataSetReadyCallback.setCallback(consumer);

        dataSetReadyCallback.callback(mock(DataSet.class));

        verify(consumer).accept(any());
        verify(errorCallback,
               never()).error(any());
        verify(dataSetReadyCallback,
               never()).showErrorMessage(any());
    }
}