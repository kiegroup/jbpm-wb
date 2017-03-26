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

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jbpm.workbench.cm.util.KieServerException;
import org.kie.server.client.KieServicesHttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Strings.isNullOrEmpty;

@KieServerExceptionHandler
@Priority(Interceptor.Priority.APPLICATION)
@Interceptor
public class KieServerExceptionInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieServerExceptionInterceptor.class);

    private static String getMessage(KieServicesHttpException response) {
        return isNullOrEmpty(response.getResponseBody()) ? response.getMessage() : response.getResponseBody();
    }

    @AroundInvoke
    public Object handleKieServerException(final InvocationContext ctx) throws Exception {
        try {
            return ctx.proceed();
        } catch (KieServicesHttpException ktex) {
            LOGGER.error(ktex.getMessage(),
                         ktex);
            throw new KieServerException(getMessage(ktex),
                                         ktex.getHttpCode(),
                                         ktex.getUrl());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(),
                         ex);
            throw ex;
        }
    }
}
