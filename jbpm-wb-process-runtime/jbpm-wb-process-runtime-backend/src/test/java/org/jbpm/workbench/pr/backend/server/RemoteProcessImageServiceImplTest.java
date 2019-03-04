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
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.UIServicesClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteProcessImageServiceImplTest {

    private static final String SVG_WITH_ACTIONS = "<svg><g><text font-size=\"12\" id=\"_5D35D92A-455D-408D-9BF3-4D6C25D6B64D\" style=\"stroke-width:1;fill:rgb(177,194,214);font-family:arial;font-weight:bold\" transform=\"translate(10, 20)\" onmouseover=\"ORYX.Plugins.CanvasTitle.addToolTip('_5D35D92A-455D-408D-9BF3-4D6C25D6B64D')\" onclick=\"ORYX.Plugins.CanvasTitle.openTextualAnalysis()\">Evaluation v.1 (evaluation)</text></g></svg>";
    private static final String SVG_WITHOUT_ACTIONS = "<svg><g><text font-size=\"12\" id=\"_5D35D92A-455D-408D-9BF3-4D6C25D6B64D\" style=\"stroke-width:1;fill:rgb(177,194,214);font-family:arial;font-weight:bold\" transform=\"translate(10, 20)\"  >Evaluation v.1 (evaluation)</text></g></svg>";
    private static final String SVG_WITH_LINK = "<svg><a onclick=\"parent.designeropenintab(&quot;place-order.bpmn2&quot;,&quot;default://master@myrepo/itorders/src/main/resources/org/jbpm/demo/itorders/place-order.bpmn2&quot;);\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"\" xlink:type=\"simple\" xlink:actuate=\"onRequest\" id=\"_646D12C5-DE96-4300-B822-EC8C77253514pimg\" xlink:show=\"replace\"></svg>";
    private static final String SVG_WITHOUT_LINK = "<svg><a  xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"\" xlink:type=\"simple\" xlink:actuate=\"onRequest\" id=\"_646D12C5-DE96-4300-B822-EC8C77253514pimg\" xlink:show=\"replace\"></svg>";

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
        when(uiServicesClient.getProcessInstanceImageCustomColor("", null, "", "", ""))
                .thenReturn(SVG_WITH_ACTIONS, SVG_WITHOUT_ACTIONS);

        validateHTMLContent(service.getProcessInstanceDiagram("",
                                                              "",
                                                              null,
                                                              "",
                                                              "",
                                                              ""));

        validateHTMLContent(service.getProcessInstanceDiagram("",
                                                              "",
                                                              null,
                                                              "",
                                                              "",
                                                              ""));
    }

    @Test
    public void testImageWithLink() {
        when(uiServicesClient.getProcessInstanceImageCustomColor("",
                                                                 null,
                                                                 "",
                                                                 "",
                                                                 "")).thenReturn(SVG_WITH_LINK);
        final String diagram = service.getProcessInstanceDiagram("",
                                                                 "",
                                                                 null,
                                                                 "",
                                                                 "",
                                                                 "");
        assertFalse(diagram.contains("onclick"));
        assertEquals(SVG_WITHOUT_LINK,
                     diagram);
    }

    @Test
    public void testProcessInstanceImageNotFound() {
        final Integer okCode = 400;
        when(uiServicesClient.getProcessInstanceImageCustomColor("", null, "", "", ""))
                .thenThrow(new KieServicesHttpException(null, 404, null, null), new KieServicesHttpException(null, okCode, null, null));

        assertNull(service.getProcessInstanceDiagram("",
                                                     "",
                                                     null,
                                                     "",
                                                     "",
                                                     ""));

        try {
            service.getProcessInstanceDiagram("",
                                              "",
                                              null,
                                              "",
                                              "",
                                              "");
            fail("Method should throw exception");
        } catch (KieServicesHttpException ex) {
            assertEquals(okCode,
                         ex.getHttpCode());
        }
    }

    @Test
    public void testProcessImageNotFound() {
        final Integer okCode = 400;
        when(uiServicesClient.getProcessImage("",
                                              null)).thenThrow(new KieServicesHttpException(null,
                                                                                            404,
                                                                                            null,
                                                                                            null),
                                                               new KieServicesHttpException(null,
                                                                                            okCode,
                                                                                            null,
                                                                                            null));

        assertNull(service.getProcessDiagram("",
                                             "",
                                             null));

        try {
            service.getProcessDiagram("",
                                      "",
                                      null);
            fail("Method should throw exception");
        } catch (KieServicesHttpException ex) {
            assertEquals(okCode,
                         ex.getHttpCode());
        }
    }

    @Test
    public void testProcessInstanceImageCustomColors() {
        String completeNodeColor = "#888888";
        String completeNodeBorderColor = "#888887";
        String activeNodeBorderColor = "#888886";

        service.getProcessInstanceDiagram("", "", null,
                                          completeNodeColor, completeNodeBorderColor, activeNodeBorderColor);

        verify(uiServicesClient).getProcessInstanceImageCustomColor("", null,
                                                                    completeNodeColor, completeNodeBorderColor,
                                                                    activeNodeBorderColor);
    }
}