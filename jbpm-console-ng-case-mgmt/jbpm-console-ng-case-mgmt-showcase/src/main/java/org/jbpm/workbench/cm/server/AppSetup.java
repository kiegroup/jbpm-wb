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

package org.jbpm.workbench.cm.server;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.server.api.KieServerConstants;
import org.kie.server.client.CaseServicesClient;
import org.kie.server.client.KieServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.commons.services.cdi.Startup;

import static org.jbpm.console.ng.ks.utils.KieServerUtils.createKieServicesClient;

@ApplicationScoped
@Startup
public class AppSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppSetup.class);

    @Inject
    private Event<ApplicationStarted> applicationStartedEvent;

    @Inject
    private AuthenticationService authenticationService;

    @PostConstruct
    public void onStartup() {
        applicationStartedEvent.fire(new ApplicationStarted());
    }

    @Produces
    @ApplicationScoped
    public KieServicesClient produceKieServicesClient() {
        LOGGER.info("Creating KieServicesClient...");
        return createKieServicesClient(KieServerConstants.CAPABILITY_CASE);
    }

    @Produces
    @ApplicationScoped
    public CaseServicesClient produceCaseServicesClient(final KieServicesClient kieServicesClient) {
        LOGGER.info("Creating CaseServicesClient...");
        return kieServicesClient.getServicesClient(CaseServicesClient.class);
    }

    @Produces
    @RequestScoped
    public User getIdentity() {
        return authenticationService.getUser();
    }

}