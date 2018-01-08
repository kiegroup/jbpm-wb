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
package org.jbpm.workbench.pr.backend.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.instance.DocumentInstance;
import org.kie.server.client.DocumentServicesClient;
import org.kie.server.client.KieServicesClient;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessDocumentServletTest {

    @Mock
    KieServerIntegration kieServerIntegration;

    @Mock
    KieServicesClient kieServicesClient;

    @Mock
    DocumentServicesClient documentServicesClient;

    @Mock
    DocumentInstance documentInstance;

    Map<String, String> params;

    @Mock
    private Appender loggingAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> loggingEventArgumentCaptor;

    private ProcessDocumentServlet processDocServlet;

    @Before
    public void setup() {
        params = new HashMap();
        processDocServlet = new ProcessDocumentServlet();

        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(loggingAppender);
        logger.setLevel(Level.INFO);
    }

    @After
    public void teardown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(loggingAppender);
    }

    @Test
    public void testInvalidRequestParamets() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("templateid")).thenReturn(null);
        when(request.getParameter("docid")).thenReturn(null);

        ProcessDocumentServlet processDocServlet = new ProcessDocumentServlet();
        processDocServlet.doGet(request,
                                response);

        verify(loggingAppender).doAppend(loggingEventArgumentCaptor.capture());
        final LoggingEvent loggingEvent = loggingEventArgumentCaptor.getValue();
        assertEquals(Level.ERROR,
                     loggingEvent.getLevel());
        assertEquals(MessageFormat.format(ProcessDocumentServlet.INVALID_PARAMS,
                                          "null",
                                          "null"),
                     loggingEvent.getFormattedMessage());
    }

    @Test
    public void testInvalidServerTemplate() throws Exception {
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(null);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("templateid")).thenReturn("invalidTemplateId");
        when(request.getParameter("docid")).thenReturn("invalidDocId");

        ProcessDocumentServlet processDocServlet = new ProcessDocumentServlet();
        processDocServlet.setKieServerIntegration(kieServerIntegration);
        processDocServlet.doGet(request,
                                response);

        verify(loggingAppender).doAppend(loggingEventArgumentCaptor.capture());
        final LoggingEvent loggingEvent = loggingEventArgumentCaptor.getValue();
        assertEquals(Level.ERROR,
                     loggingEvent.getLevel());
        assertEquals(MessageFormat.format(ProcessDocumentServlet.INVALID_SERVICES_CLIENT,
                                          "invalidTemplateId"),
                     loggingEvent.getFormattedMessage());
    }

    @Test
    public void testInvalidDocumentInstance() throws Exception {
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(any())).thenReturn(documentServicesClient);
        when(documentServicesClient.getDocument(anyString())).thenReturn(null);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("templateid")).thenReturn("kie-server-id");
        when(request.getParameter("docid")).thenReturn("invalidDocId");

        ProcessDocumentServlet processDocServlet = new ProcessDocumentServlet();
        processDocServlet.setKieServerIntegration(kieServerIntegration);
        processDocServlet.doGet(request,
                                response);

        verify(loggingAppender).doAppend(loggingEventArgumentCaptor.capture());
        final LoggingEvent loggingEvent = loggingEventArgumentCaptor.getValue();
        assertEquals(Level.ERROR,
                     loggingEvent.getLevel());
        assertEquals(MessageFormat.format(ProcessDocumentServlet.INVALID_DOCUMENT,
                                          "invalidDocId"),
                     loggingEvent.getFormattedMessage());
    }

    @Test
    public void testRetrieveValidDocument() throws Exception {
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(any())).thenReturn(documentServicesClient);
        when(documentServicesClient.getDocument(anyString())).thenReturn(documentInstance);
        when(documentInstance.getName()).thenReturn("testdoc.properties");
        when(documentInstance.getContent()).thenReturn("sampleContent".getBytes());

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("templateid")).thenReturn("kie-server-id");
        when(request.getParameter("docid")).thenReturn("AAAA-BBBB-CCCC-DDDD");
        ProcessDocumentServletTest.StubServletOutputStream stubServletOutputStream = new ProcessDocumentServletTest.StubServletOutputStream();
        when(response.getOutputStream()).thenReturn(stubServletOutputStream);

        ProcessDocumentServlet processDocServlet = new ProcessDocumentServlet();
        processDocServlet.setKieServerIntegration(kieServerIntegration);
        processDocServlet.doGet(request,
                                response);

        assertNotNull(response.getOutputStream());

        assertEquals("sampleContent",
                     new String(((ProcessDocumentServletTest.StubServletOutputStream) response.getOutputStream()).getContent()));
    }

    protected class StubServletOutputStream extends ServletOutputStream {

        public ByteArrayOutputStream baos = new ByteArrayOutputStream();

        public byte[] getContent() {
            return baos.toByteArray();
        }

        @Override
        public void write(int i) throws IOException {
            baos.write(i);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }
}
