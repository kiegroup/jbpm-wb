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


import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jbpm.console.ng.bd.backend.server.AdministrationService;
import org.kie.commons.services.cdi.Startup;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

@ApplicationScoped
@Startup
public class AppSetup {

     // default repository section - start
    private static final String JBPM_WB_PLAYGROUND_ALIAS = "jbpm-playground";
    private static final String JBPM_WB_PLAYGROUND_ORIGIN = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground-kjar.git";
    private static final String JBPM_WB_PLAYGROUND_UID = "guvnorngtestuser1";
    private static final String JBPM_WB_PLAYGROUND_PWD = "test1234";

    @Inject
    private AdministrationService administrationService;

    private Repository repository;
    
    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @PostConstruct
    public void onStartup() {
        // TODO Setup default repository for jBPM-Workbench
        administrationService.bootstrapRepository( JBPM_WB_PLAYGROUND_ALIAS, JBPM_WB_PLAYGROUND_ORIGIN,
                                                   JBPM_WB_PLAYGROUND_UID, JBPM_WB_PLAYGROUND_PWD );
       
        
        administrationService.bootstrapConfig();

        administrationService.bootstrapDeployments();
    }


}