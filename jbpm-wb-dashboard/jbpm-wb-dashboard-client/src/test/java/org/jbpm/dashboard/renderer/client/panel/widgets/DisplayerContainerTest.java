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

package org.jbpm.dashboard.renderer.client.panel.widgets;

import java.util.HashMap;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.displayer.client.Displayer;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DisplayerContainerTest {

    private DisplayerContainer container;

    @Mock
    private DisplayerContainerView view;

    @Mock
    private Displayer currentDisplayer;

    @Before
    public void setup() {
        container = spy(new DisplayerContainer(new HashMap<>(),
                                               false));
        container.view = view;
        container.currentDisplayer = currentDisplayer;
    }

    @Test
    public void testShowErrorWhenIsKieServerForbiddenException() {
        final ClientRuntimeError clientRuntimeError = makeClientRuntimeError(makeHttpException(403));

        container.showError(clientRuntimeError);

        verify(view).showError(DefaultWorkbenchConstants.INSTANCE.KieServerError403(),
                               clientRuntimeError.getCause());
    }

    @Test
    public void testShowErrorWhenIsKieServerUnauthorizedException() {
        final ClientRuntimeError clientRuntimeError = makeClientRuntimeError(makeHttpException(401));

        container.showError(clientRuntimeError);

        verify(view).showError(DefaultWorkbenchConstants.INSTANCE.KieServerError401(),
                               clientRuntimeError.getCause());
    }

    @Test
    public void testShowErrorWhenIsNotKieServerUnauthorizedException() {
        final ClientRuntimeError clientRuntimeError = makeClientRuntimeError(new Throwable());

        doNothing().when(container).showErrorPopup(any());

        container.showError(clientRuntimeError);

        verify(view).showEmpty(currentDisplayer);
        verify(container).showErrorPopup(DashboardConstants.INSTANCE.dashboardCouldNotBeLoaded());
    }

    private KieServicesHttpException makeHttpException(final int httpCode) {
        return new KieServicesHttpException("",
                                            httpCode,
                                            "",
                                            "");
    }

    private ClientRuntimeError makeClientRuntimeError(final Throwable throwable) {
        return new ClientRuntimeError("",
                                      throwable);
    }
}
