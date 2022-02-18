/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.forms.display.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.impl.KieServicesClientImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormRendererProxyServletTest {

    @Mock
    KieServerIntegration kieServerIntegration;

    @InjectMocks
    FormRendererProxyServlet servlet;

    @Test
    public void testStartProcessWithKieServerFormRenderer() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String correlationKey = "test-correlationKey";
        when(request.getParameter(eq("templateid"))).thenReturn("test-templateid");
        when(request.getParameter(eq("correlationKey"))).thenReturn(correlationKey);
        when(request.getPathInfo()).thenReturn("/containers/1/processes/2");

        BufferedReader reader = mock(BufferedReader.class);
        Stream<String> stream = mock(Stream.class);
        when(request.getReader()).thenReturn(reader);
        when(reader.lines()).thenReturn(stream);
        when(stream.collect(any())).thenReturn("{\"name\" : \"value\"}");

        KieServicesClientImpl client = mock(KieServicesClientImpl.class);
        when(kieServerIntegration.getServerClient(any())).thenReturn(client);
        ProcessServicesClient processServicesClient = mock(ProcessServicesClient.class);
        when(client.getServicesClient(any())).thenReturn(processServicesClient);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        doNothing().when(servletOutputStream).write(any());

        servlet.doPost(request, response);
        verify(processServicesClient).startProcess(eq("1"), eq("2"), any(), any());

        when(request.getParameter(eq("correlationKey"))).thenReturn(null);

        servlet.doPost(request, response);
        verify(processServicesClient).startProcess(eq("1"), eq("2"), anyMap());
    }
}
