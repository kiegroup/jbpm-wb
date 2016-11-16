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

package org.jbpm.console.ng.ks.utils;

import java.util.Arrays;

import org.jbpm.console.ng.ks.security.KeyCloakTokenCredentialsProvider;
import org.kie.server.api.KieServerConstants;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.CredentialsProvider;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.balancer.LoadBalancer;
import org.kie.server.client.credentials.SubjectCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

public class KieServerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieServerUtils.class);

    public static KieServicesClient createKieServicesClient(final String... capabilities) {
        final String kieServerEndpoint = System.getProperty(KieServerConstants.KIE_SERVER_LOCATION);
        checkNotNull(kieServerEndpoint, "Missing Kie Server system property " + KieServerConstants.KIE_SERVER_LOCATION);
        final String userName = System.getProperty(KieServerConstants.CFG_KIE_USER);
        final String password = System.getProperty(KieServerConstants.CFG_KIE_PASSWORD);

        if (isNullOrEmpty(userName)) {
            return createKieServicesClient(kieServerEndpoint, null, getCredentialsProvider(), capabilities);
        } else {
            return createKieServicesClient(kieServerEndpoint, null, userName, password, capabilities);
        }
    }

    public static KieServicesClient createKieServicesClient(final String endpoint, final ClassLoader classLoader, final String login, final String password, final String... capabilities) {
        final KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration(endpoint, login, password);
        return createKieServicesClient(endpoint, classLoader, configuration, capabilities);
    }

    public static KieServicesClient createKieServicesClient(final String endpoint, final ClassLoader classLoader, final CredentialsProvider credentialsProvider, final String... capabilities) {
        final KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration(endpoint, credentialsProvider);
        return createKieServicesClient(endpoint, classLoader, configuration, capabilities);
    }

    public static KieServicesClient createKieServicesClient(final String endpoint, final ClassLoader classLoader, final KieServicesConfiguration configuration, final String... capabilities) {
        LOGGER.debug("Creating client that will use following endpoint {}", endpoint);
        configuration.setTimeout(60000);
        configuration.setCapabilities(Arrays.asList(capabilities));
        configuration.setMarshallingFormat(MarshallingFormat.XSTREAM);
        configuration.setLoadBalancer(LoadBalancer.getDefault(endpoint));

        KieServicesClient kieServicesClient;

        if (classLoader == null) {
            kieServicesClient = KieServicesFactory.newKieServicesClient(configuration);
        } else {
            kieServicesClient = KieServicesFactory.newKieServicesClient(configuration, classLoader);
        }
        LOGGER.debug("KieServerClient created successfully for endpoint {}", endpoint);
        return kieServicesClient;
    }

    public static CredentialsProvider getCredentialsProvider() {
        CredentialsProvider credentialsProvider;
        try {
            credentialsProvider = new KeyCloakTokenCredentialsProvider();
        } catch (UnsupportedOperationException e) {
            credentialsProvider = new SubjectCredentialsProvider();
        }
        LOGGER.debug("{} initialized for the client.", credentialsProvider.getClass().getName());
        return credentialsProvider;
    }

}
