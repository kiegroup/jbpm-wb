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

package org.jbpm.console.ng.bd.security;

import java.lang.reflect.Method;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

import org.kie.server.client.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.server.SecurityIntegrationFilter;

public class KeyCloakTokenCredentialsProvider implements CredentialsProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(KeyCloakTokenCredentialsProvider.class);

    private Class<?> keycloakPrincipal;
    private Class<?> keycloakSecurityContext;
    private Method tokenMethod;
    private Method securityContextMethod;

    public KeyCloakTokenCredentialsProvider() {
        try {
            keycloakPrincipal = Class.forName("org.keycloak.KeycloakPrincipal");
            keycloakSecurityContext = Class.forName("org.keycloak.KeycloakSecurityContext");
            tokenMethod = keycloakSecurityContext.getMethod("getTokenString", new Class[0]);
            securityContextMethod = keycloakPrincipal.getMethod("getKeycloakSecurityContext", new Class[0]);
        } catch (Exception e) {
            logger.debug("KeyCloak not on classpath due to {}", e.toString());
            throw new UnsupportedOperationException("KeyCloak not on classpath");
        }
    }

    @Override
    public String getHeaderName() {
        return "Authorization";
    }

    @Override
    public String getAuthorization() {
        HttpServletRequest request = SecurityIntegrationFilter.getRequest();

        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            try {
                Object securityContext = securityContextMethod.invoke(principal, new Object[0]);

                return CredentialsProvider.TOKEN_AUTH_PREFIX + tokenMethod.invoke(securityContext, new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
