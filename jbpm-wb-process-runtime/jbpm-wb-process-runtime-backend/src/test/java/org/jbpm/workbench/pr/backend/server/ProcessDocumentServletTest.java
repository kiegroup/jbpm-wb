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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;

import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.jbpm.workbench.pr.backend.server.org.jbpm.workbench.pr.backend.server.helper.TestHttpServletRequest;
import org.jbpm.workbench.pr.backend.server.org.jbpm.workbench.pr.backend.server.helper.TestHttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.instance.DocumentInstance;
import org.kie.server.client.DocumentServicesClient;
import org.kie.server.client.KieServicesClient;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessDocumentServletTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDocumentServletTest.class);

    @Mock
    KieServerIntegration kieServerIntegration;

    @Mock
    KieServicesClient kieServicesClient;

    @Mock
    DocumentServicesClient documentServicesClient;

    @Mock
    DocumentInstance documentInstance;

    @Captor
    protected ArgumentCaptor<NotificationEvent> notificationCaptor;

    protected Event<NotificationEvent> notification = mock(EventSourceMock.class);

    protected final List<Object> receivedNotificationEvents = new ArrayList<Object>();

    protected Event<NotificationEvent> notificationEvents = new EventSourceMock<NotificationEvent>() {
        @Override
        public void fire(NotificationEvent event) {
            receivedNotificationEvents.add(event);
        }
    };

    Map<String, String> params;

    @Before
    public void setup() {
        params = new HashMap();
    }

    @Test
    public void testInvalidRequestParamets() throws Exception {
        TestHttpServletRequest request = new TestHttpServletRequest(params);
        TestHttpServletResponse response = new TestHttpServletResponse();

        ProcessDocumentServlet processDocServlet = new ProcessDocumentServlet();
        processDocServlet.setNotification(notificationEvents);
        processDocServlet.doGet(request,
                                response);

        assertEquals(1,
                     receivedNotificationEvents.size());
        Object event = receivedNotificationEvents.get(0);
        assertTrue(event instanceof NotificationEvent);
        NotificationEvent eventReceived = (NotificationEvent) event;
        assertEquals(MessageFormat.format(ProcessDocumentServlet.INVALID_PARAMS,
                                          "null",
                                          "null"),
                     eventReceived.getNotification());
    }

    @Test
    public void testInvalidServerTemplate() throws Exception {
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(null);

        params.put("templateid",
                   "invalidTemplateId");
        params.put("docid",
                   "invalidDocId");

        TestHttpServletRequest request = new TestHttpServletRequest(params);
        TestHttpServletResponse response = new TestHttpServletResponse();

        ProcessDocumentServlet processDocServlet = new ProcessDocumentServlet();
        processDocServlet.setNotification(notificationEvents);
        processDocServlet.setKieServerIntegration(kieServerIntegration);
        processDocServlet.doGet(request,
                                response);

        assertEquals(1,
                     receivedNotificationEvents.size());
        Object event = receivedNotificationEvents.get(0);
        assertTrue(event instanceof NotificationEvent);
        NotificationEvent eventReceived = (NotificationEvent) event;
        assertEquals(MessageFormat.format(ProcessDocumentServlet.INVALID_SERVICES_CLIENT,
                                          "invalidTemplateId"),
                     eventReceived.getNotification());
    }

    @Test
    public void testInvalidDocumentInstance() throws Exception {
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(any())).thenReturn(documentServicesClient);
        when(documentServicesClient.getDocument(anyString())).thenReturn(null);

        params.put("templateid",
                   "kie-server-id");
        params.put("docid",
                   "invalidDocId");

        TestHttpServletRequest request = new TestHttpServletRequest(params);
        TestHttpServletResponse response = new TestHttpServletResponse();

        ProcessDocumentServlet processDocServlet = new ProcessDocumentServlet();
        processDocServlet.setNotification(notificationEvents);
        processDocServlet.setKieServerIntegration(kieServerIntegration);
        processDocServlet.doGet(request,
                                response);

        assertEquals(1,
                     receivedNotificationEvents.size());
        Object event = receivedNotificationEvents.get(0);
        assertTrue(event instanceof NotificationEvent);
        NotificationEvent eventReceived = (NotificationEvent) event;
        assertEquals(MessageFormat.format(ProcessDocumentServlet.INVALID_DOCUMENT,
                                          "invalidDocId"),
                     eventReceived.getNotification());
    }

    @Test
    public void testRetrieveValidDocument() throws Exception {
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(any())).thenReturn(documentServicesClient);
        when(documentServicesClient.getDocument(anyString())).thenReturn(documentInstance);
        when(documentInstance.getName()).thenReturn("testdoc.properties");
        when(documentInstance.getContent()).thenReturn("sampleContent".getBytes());

        params.put("templateid",
                   "kie-server-id");
        params.put("docid",
                   "AAAA-BBBB-CCCC-DDDD");

        TestHttpServletRequest request = new TestHttpServletRequest(params);
        TestHttpServletResponse response = new TestHttpServletResponse();

        ProcessDocumentServlet processDocServlet = new ProcessDocumentServlet();
        processDocServlet.setNotification(notificationEvents);
        processDocServlet.setKieServerIntegration(kieServerIntegration);
        processDocServlet.doGet(request,
                                response);

        assertEquals(0,
                     receivedNotificationEvents.size());

        assertEquals("application/octet-stream",
                     response.getContentType());

        String docName = "";
        String dispositionHeader = response.getHeader("Content-Disposition");
        assertNotNull(dispositionHeader);
        int index = dispositionHeader.indexOf("filename=");
        if (index > 0) {
            docName = dispositionHeader.substring(index + 10,
                                                  dispositionHeader.length() - 1);
        }
        assertNotNull(docName);
        assertEquals("testdoc.properties",
                     docName);
        assertNotNull(response.getOutputStream());
        assertEquals("sampleContent",
                     new String(response.getContent()));
    }
}
