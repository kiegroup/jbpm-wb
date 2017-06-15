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
package org.jbpm.workbench.wi.client.editors.deployment.descriptor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DeploymentDescriptorEditorPresenterTest {

    private CallerMock<DDEditorService> callerMockDDEditorService;

    @Mock
    private DDEditorService ddEditorServiceMock;

    @Mock
    DeploymentDescriptorViewImpl view;


    DeploymentDescriptorEditorPresenter presenter;

    @Before
    public void setUp() throws Exception {
        callerMockDDEditorService = new CallerMock<DDEditorService>(ddEditorServiceMock);
        presenter = new DeploymentDescriptorEditorPresenter(view,
                callerMockDDEditorService, mock(TranslationService.class)) {
            {
                kieView = mock(KieEditorWrapperView.class);
                overviewWidget = mock(OverviewWidgetPresenter.class);
                versionRecordManager = mock(VersionRecordManager.class);
                concurrentUpdateSessionInfo = null;
            }

            protected void makeMenuBar() {

            }

            protected void addSourcePage() {

            }
        };
    }

    @Test
    public void testDeploymentDescriptorEditorSetup() throws Exception {
        presenter.onStartup(mock(ObservablePath.class), mock(PlaceRequest.class));

        verify(view).setSourceTabReadOnly(true);
        verify(view).setup();
        verify(view, times(RuntimeStrategy.values().length)).addRuntimeStrategy(anyString(), anyString());
        verify(view, times(2)).addPersistenceMode(anyString(), anyString());
        verify(view, times(3)).addAuditMode(anyString(), anyString());

    }

}