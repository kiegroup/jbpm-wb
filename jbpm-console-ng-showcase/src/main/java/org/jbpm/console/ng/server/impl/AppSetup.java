/*
 * Copyright 2012 JBoss Inc
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

package org.jbpm.console.ng.server.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jbpm.console.ng.bd.backend.server.AdministrationService;
import org.kie.commons.services.cdi.Startup;
import org.uberfire.backend.repositories.Repository;

@ApplicationScoped
@Startup
public class AppSetup {

    // demo repository settings
    private static final String REPO_PLAYGROUND = "jbpm-playground";
    private static final String ORIGIN_URL = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground-kjar.git";
    private static final String USER_NAME = "guvnorngtestuser1";
    private static final String PASSWORD = "test1234";

    @Inject
    private AdministrationService administrationService;

    private Repository repository;


    @PostConstruct
    public void onStartup() {
        administrationService.bootstrapRepository(REPO_PLAYGROUND, ORIGIN_URL, USER_NAME, PASSWORD);

        administrationService.bootstrapConfig();

        administrationService.bootstrapDeployments();
    }




}