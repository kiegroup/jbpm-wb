/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.server.editors.jbpm.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.internal.identity.IdentityProvider;

@SessionScoped
public class UberFireIdentityProvider implements IdentityProvider, Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private User identity;

    @Inject
    private HttpServletRequest request;

    @Override
    public String getName() {
        try {
            return identity.getIdentifier();
        } catch (Exception e) {
            if (request != null && request.getUserPrincipal() != null) {
                return request.getUserPrincipal().getName();
            }
            return null;
        }
    }

    @Override
    public List<String> getRoles() {
        List<String> roles = new ArrayList<String>();

        Collection<Role> ufRoles = identity.getRoles();
        for (Role role : ufRoles) {
            roles.add(role.getName());
        }

        return roles;
    }

    @Override
    public boolean hasRole(String role) {
        if (request != null) {
            return request.isUserInRole(role);
        }
        return false;
    }
}