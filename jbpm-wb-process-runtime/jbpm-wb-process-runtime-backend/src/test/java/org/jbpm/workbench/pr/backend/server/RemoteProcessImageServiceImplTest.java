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

package org.jbpm.workbench.pr.backend.server;

import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.UIServicesClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteProcessImageServiceImplTest {

    private static final String SVG_WITH_ACTIONS = "<svg></</g></g><text font-size=\"12\" id=\"_5D35D92A-455D-408D-9BF3-4D6C25D6B64D\" style=\"stroke-width:1;fill:rgb(177,194,214);font-family:arial;font-weight:bold\" transform=\"translate(10, 20)\" onmouseover=\"ORYX.Plugins.CanvasTitle.addToolTip('_5D35D92A-455D-408D-9BF3-4D6C25D6B64D')\" onclick=\"ORYX.Plugins.CanvasTitle.openTextualAnalysis()\">Evaluation v.1 (evaluation)</text></g></g></svg>";
    private static final String SVG_WITHOUT_ACTIONS = "<svg></</g></g><text font-size=\"12\" id=\"_5D35D92A-455D-408D-9BF3-4D6C25D6B64D\" style=\"stroke-width:1;fill:rgb(177,194,214);font-family:arial;font-weight:bold\" transform=\"translate(10, 20)\" >Evaluation v.1 (evaluation)</text></g></g></svg>";

    @Mock
    KieServerIntegration kieServerIntegration;

    @Mock
    UIServicesClient uiServicesClient;

    @InjectMocks
    RemoteProcessImageServiceImpl service;

    private static void validateHTMLContent(final String html) {
        assertFalse(html.contains("onclick"));
        assertFalse(html.contains("onmouseover"));
        assertEquals(SVG_WITHOUT_ACTIONS,
                     html);
    }

    @Before
    public void setup() {
        final KieServicesClient servicesClient = mock(KieServicesClient.class);
        when(servicesClient.getServicesClient(UIServicesClient.class)).thenReturn(uiServicesClient);
        when(kieServerIntegration.getServerClient("",
                                                  "")).thenReturn(servicesClient);
    }

    @Test
    public void testProcessDiagramHTML() {
        when(uiServicesClient.getProcessImage("",
                                              "")).thenReturn(SVG_WITH_ACTIONS,
                                                              SVG_WITHOUT_ACTIONS);

        validateHTMLContent(service.getProcessDiagram("",
                                                      "",
                                                      ""));

        validateHTMLContent(service.getProcessDiagram("",
                                                      "",
                                                      ""));
    }

    @Test
    public void testProcessInstanceImageHTML() {
        when(uiServicesClient.getProcessInstanceImage("",
                                                      null)).thenReturn(SVG_WITH_ACTIONS,
                                                                        SVG_WITHOUT_ACTIONS);

        validateHTMLContent(service.getProcessInstanceDiagram("",
                                                              "",
                                                              null));

        validateHTMLContent(service.getProcessInstanceDiagram("",
                                                              "",
                                                              null));
    }
}
