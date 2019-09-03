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

package org.jbpm.workbench.ks.utils;

import java.security.Principal;
import java.util.Arrays;

import org.jbpm.workbench.ks.security.KeyCloakTokenCredentialsProvider;
import org.keycloak.KeycloakPrincipal;
import org.kie.server.api.KieServerConstants;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.CredentialsProvider;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.balancer.LoadBalancer;
import org.kie.server.client.credentials.EnteredCredentialsProvider;
import org.kie.server.client.credentials.EnteredTokenCredentialsProvider;
import org.kie.server.client.credentials.SubjectCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.server.SecurityIntegrationFilter;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.kie.server.common.KeyStoreHelperUtil.loadServerPassword;

public class KieServerUtils {


    private static final Logger LOGGER = LoggerFactory.getLogger(KieServerUtils.class);

    private static boolean KIE_SERVER_FORM_RENDERER = Boolean.parseBoolean(System.getProperty("org.jbpm.wb.forms.renderer.ext", "false"));
    
    public static KieServicesClient createKieServicesClient(final String... capabilities) {
        final String kieServerEndpoint = System.getProperty(KieServerConstants.KIE_SERVER_LOCATION);
        checkNotNull(kieServerEndpoint,
                     "Missing Kie Server system property " + KieServerConstants.KIE_SERVER_LOCATION);
        return createKieServicesClient(kieServerEndpoint,
                                       null,
                                       getCredentialsProvider(),
                                       capabilities);
    }

    public static KieServicesClient createAdminKieServicesClient(final String... capabilities) {
        final String kieServerEndpoint = System.getProperty(KieServerConstants.KIE_SERVER_LOCATION);
        checkNotNull(kieServerEndpoint,
                     "Missing Kie Server system property " + KieServerConstants.KIE_SERVER_LOCATION);
        return createKieServicesClient(kieServerEndpoint,
                                       null,
                                       getAdminCredentialsProvider(),
                                       capabilities);
    }

    public static KieServicesClient createKieServicesClient(final String endpoint,
                                                            final ClassLoader classLoader,
                                                            final String login,
                                                            final String password,
                                                            final String... capabilities) {
        final KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration(endpoint,
                                                                                               login,
                                                                                               password);
        return createKieServicesClient(endpoint,
                                       classLoader,
                                       configuration,
                                       capabilities);
    }

    public static KieServicesClient createKieServicesClient(final String endpoint,
                                                            final ClassLoader classLoader,
                                                            final CredentialsProvider credentialsProvider,
                                                            final String... capabilities) {
        final KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration(endpoint,
                                                                                               credentialsProvider);
        return createKieServicesClient(endpoint,
                                       classLoader,
                                       configuration,
                                       capabilities);
    }

    public static KieServicesClient createKieServicesClient(final String endpoint,
                                                            final ClassLoader classLoader,
                                                            final KieServicesConfiguration configuration,
                                                            final String... capabilities) {
        LOGGER.debug("Creating client that will use following endpoint {}",
                     endpoint);
        configuration.setTimeout(60000);
        if (capabilities != null) {
            configuration.setCapabilities(Arrays.asList(capabilities));
        }
        configuration.setMarshallingFormat(isKieServerRendererEnabled() ? MarshallingFormat.JSON : MarshallingFormat.XSTREAM);
        configuration.setLoadBalancer(LoadBalancer.getDefault(endpoint));

        KieServicesClient kieServicesClient;

        if (classLoader == null) {
            kieServicesClient = KieServicesFactory.newKieServicesClient(configuration);
        } else {
            kieServicesClient = KieServicesFactory.newKieServicesClient(configuration,
                                                                        classLoader);
        }
        LOGGER.debug("KieServerClient created successfully for endpoint {}",
                     endpoint);
        return kieServicesClient;
    }

    public static CredentialsProvider getCredentialsProvider() {
        return new CredentialsProvider() {

            KeyCloakTokenCredentialsProvider keyCloakProvider = new KeyCloakTokenCredentialsProvider();
            SubjectCredentialsProvider subjectProvider = new SubjectCredentialsProvider();
            
            @Override
            public String getHeaderName() {
                return javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
            }

            @Override
            public String getAuthorization() {
                Principal principal = SecurityIntegrationFilter.getRequest().getUserPrincipal();
                if (principal instanceof KeycloakPrincipal) {
                    return keyCloakProvider.getAuthorization();
                } else {
                    return subjectProvider.getAuthorization();
                }
            }
        };
    }

    public static CredentialsProvider getAdminCredentialsProvider() {
        if (System.getProperty(KieServerConstants.CFG_KIE_TOKEN) != null) {
            return new EnteredTokenCredentialsProvider(System.getProperty(KieServerConstants.CFG_KIE_TOKEN));
        } else {
            return new EnteredCredentialsProvider(System.getProperty(KieServerConstants.CFG_KIE_USER,
                                                                     "kieserver"),
                                                  loadServerPassword());
        }
    }

    public static boolean isKieServerRendererEnabled() {
        return KIE_SERVER_FORM_RENDERER;
    }
}
