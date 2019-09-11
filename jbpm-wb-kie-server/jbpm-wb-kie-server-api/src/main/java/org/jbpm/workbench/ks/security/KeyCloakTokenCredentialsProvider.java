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

package org.jbpm.workbench.ks.security;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakPrincipal;
import org.kie.server.client.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.server.SecurityIntegrationFilter;

public class KeyCloakTokenCredentialsProvider implements CredentialsProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyCloakTokenCredentialsProvider.class);

    @Override
    public String getHeaderName() {
        return "Authorization";
    }

    @Override
    public String getAuthorization() {
        LOGGER.debug("Get user authorization using KeyCloakTokenCredentialsProvider");
        HttpServletRequest request = SecurityIntegrationFilter.getRequest();

        Principal principal = request.getUserPrincipal();
        if (principal != null && principal instanceof KeycloakPrincipal) {
            try {
                KeycloakPrincipal kc = (KeycloakPrincipal) principal;
                return CredentialsProvider.TOKEN_AUTH_PREFIX + kc.getKeycloakSecurityContext().getTokenString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
