/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.pr.backend.server;

import java.lang.reflect.Method;
import java.util.Collection;

import org.jbpm.console.ng.pr.service.ProcessRuntimeDataService;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class RemoteProcessRuntimeDataServiceImplTest {

    RemoteProcessRuntimeDataServiceImpl service = new RemoteProcessRuntimeDataServiceImpl();

    @Test
    public void testInvalidServerTemplate() throws Exception {
        final Method[] methods = ProcessRuntimeDataService.class.getMethods();
        for (Method method : methods) {
            final Class<?> returnType = method.getReturnType();
            final Object[] args = new Object[method.getParameterCount()];
            Object result = method.invoke(service, args);

            assertMethodResult(method, returnType, result);

            args[0] = "";
            result = method.invoke(service, args);
            assertMethodResult(method, returnType, result);
        }
    }

    private void assertMethodResult(final Method method, final Class<?> returnType, final Object result) {
        if (Collection.class.isAssignableFrom(returnType)) {
            assertNotNull(format("Returned collection for method %s should not be null", method.getName()), result);
            assertTrue(format("Returned collection for method %s should be empty", method.getName()), ((Collection) result).isEmpty());
        } else {
            assertNull(format("Returned object for method %s should be null", method.getName()), result);
        }
    }

}