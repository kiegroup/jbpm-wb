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

package org.jbpm.workbench.cm.backend.server;

import javax.interceptor.InvocationContext;

import org.jbpm.workbench.cm.util.KieServerException;
import org.junit.Test;
import org.kie.server.client.KieServicesHttpException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KieServerExceptionInterceptorTest {

    private KieServerExceptionInterceptor interceptor = new KieServerExceptionInterceptor();

    @Test
    public void testKieServerException() throws Exception {
        InvocationContext ctx = mock(InvocationContext.class);
        final String message = "message";
        final String responseBody = "responseBody";
        final Integer httpCode = 400;
        final String url = "/url";
        doThrow(new KieServicesHttpException(message,
                                             httpCode,
                                             url,
                                             responseBody)).when(ctx).proceed();

        try {
            interceptor.handleKieServerException(ctx);
        } catch (KieServerException ex) {
            assertEquals(responseBody,
                         ex.getMessage());
            assertEquals(url,
                         ex.getUrl());
            assertEquals(httpCode,
                         ex.getHttpCode());
        }
        verify(ctx).proceed();
    }

    @Test
    public void testRuntimeException() throws Exception {
        InvocationContext ctx = mock(InvocationContext.class);
        final String message = "message";
        doThrow(new RuntimeException(message)).when(ctx).proceed();

        try {
            interceptor.handleKieServerException(ctx);
        } catch (Exception ex) {
            assertFalse(ex instanceof KieServerException);
            assertEquals(message,
                         ex.getMessage());
        }
        verify(ctx).proceed();
    }
}
