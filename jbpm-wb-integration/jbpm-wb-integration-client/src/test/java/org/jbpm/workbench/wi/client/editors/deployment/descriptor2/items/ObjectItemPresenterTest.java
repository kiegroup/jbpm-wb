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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor2.items;

import com.google.common.collect.ImmutableList;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor2.DeploymentsSectionPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor2.DeploymentsSectionPresenter.MarshallingStrategiesListPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor2.model.Resolver;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.workbench.wi.dd.model.Parameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.util.KieEnumSelectElement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ObjectItemPresenterTest {

    @Mock
    private ObjectItemPresenter.View view;

    @Mock
    private ParametersModal parametersModal;

    @Mock
    private KieEnumSelectElement<Resolver> resolversSelect;

    private ObjectItemPresenter objectItemPresenter;

    @Before
    public void before() {
        objectItemPresenter = spy(new ObjectItemPresenter(view,
                                                          parametersModal,
                                                          resolversSelect));
    }

    @Test
    public void testSetup() {
        final ItemObjectModel model = spy(new ItemObjectModel(null, "Value", "reflection", ImmutableList.of(new Parameter("Foo", "Bar"))));

        objectItemPresenter.setup(model, mock(DeploymentsSectionPresenter.class));

        verify(model, never()).setParameters(any());
        verify(view).init(eq(objectItemPresenter));
        verify(view).setValue(eq("Value"));
        verify(view).setParametersCount(eq(1));
        verify(parametersModal).setup(any(), any());
        verify(resolversSelect).setup(any(), any(), eq(Resolver.REFLECTION), any());
    }

    @Test
    public void testRemove() {
        final DeploymentsSectionPresenter parentPresenter = mock(DeploymentsSectionPresenter.class);
        final MarshallingStrategiesListPresenter listPresenter = mock(MarshallingStrategiesListPresenter.class);

        objectItemPresenter.parentPresenter = parentPresenter;
        objectItemPresenter.setListPresenter(listPresenter);

        objectItemPresenter.remove();

        verify(listPresenter).remove(eq(objectItemPresenter));
        verify(parentPresenter).fireChangeEvent();
    }

    @Test
    public void testSignalParameterAddedOrRemoved() {
        final DeploymentsSectionPresenter parentPresenter = mock(DeploymentsSectionPresenter.class);
        objectItemPresenter.parentPresenter = parentPresenter;
        objectItemPresenter.model = new ItemObjectModel(null, "Value", "reflection", ImmutableList.of(new Parameter("Foo", "Bar")));

        objectItemPresenter.signalParameterAddedOrRemoved();

        verify(view).setParametersCount(eq(1));
        verify(parentPresenter).fireChangeEvent();
    }
}