package org.jbpm.console.ng.bd.integration.security;

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
            logger.warn("KeyCloak not on classpath due to {}", e.toString());
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
