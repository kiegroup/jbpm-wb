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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.items;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ParametersModalTest {

    @Mock
    private ParametersModalView view;

    @Mock
    private ParametersModal.ParametersListPresenter parametersListPresenter;

    private ParametersModal parametersModal;

    @Before
    public void before() {
        parametersModal = spy(new ParametersModal(view,
                                                  parametersListPresenter));
    }

    @Test
    public void testSetup() {
        doNothing().when(parametersModal).superSetup();

        parametersModal.setup(emptyList(), mock(ObjectPresenter.class));

        verify(parametersModal).superSetup();
        verify(parametersListPresenter).setup(any(), any(), any());
    }

    @Test
    public void testAdd() {
        final ObjectPresenter parentPresenter = mock(ObjectPresenter.class);
        parametersModal.parentPresenter = parentPresenter;

        parametersModal.add();

        verify(parametersListPresenter).add(any());
        verify(parentPresenter).signalParameterAddedOrRemoved();
    }
}