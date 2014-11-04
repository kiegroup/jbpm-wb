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
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jbpm.console.ng.bd.service.AdministrationService;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.io.IOService;

@ApplicationScoped
@Startup
public class AppSetup {

     // default repository section - start
    private static final String JBPM_WB_PLAYGROUND_ALIAS = "jbpm-playground";
    private static final String JBPM_WB_PLAYGROUND_ORIGIN = "https://github.com/droolsjbpm/jbpm-playground.git";
   

    private static final String GLOBAL_SETTINGS = "settings";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private AdministrationService administrationService;

    private Repository repository;
    
    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private Event<ApplicationStarted> applicationStartedEvent;

    @PostConstruct
    public void onStartup() {
        if (!"false".equalsIgnoreCase(System.getProperty("org.kie.demo"))) {
            administrationService.bootstrapRepository( "demo", JBPM_WB_PLAYGROUND_ALIAS, JBPM_WB_PLAYGROUND_ORIGIN,
                                                       "", "" );
        } else if ("true".equalsIgnoreCase(System.getProperty("org.kie.example"))) {
            administrationService.bootstrapRepository( "example", "repository1", null, "", "" );
            administrationService.bootstrapProject("repository1", "org.kie.example", "project1", "1.0.0-SNAPSHOT");
        }
        
        administrationService.bootstrapConfig();

        administrationService.bootstrapDeployments();

        configurationService.addConfiguration( getGlobalConfiguration() );

        // notify cluster service that bootstrap is completed to start synchronization
        applicationStartedEvent.fire(new ApplicationStarted());
    }


    private ConfigGroup getGlobalConfiguration() {
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                GLOBAL_SETTINGS,
                "" );

        /*
        group.addConfigItem( configurationFactory.newConfigItem( "drools.dateformat",
                "dd-MMM-yyyy" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.datetimeformat",
                "dd-MMM-yyyy hh:mm:ss" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultlanguage",
                "en" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultcountry",
                "US" ) );
        */

        group.addConfigItem( configurationFactory.newConfigItem( "build.enable-incremental",
                "true" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "support.runtime.deploy",
                "true" ) );

        /*
        group.addConfigItem( configurationFactory.newConfigItem( "rule-modeller-onlyShowDSLStatements",
                "false" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "designer.url",
                "http://localhost:8080" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "designer.context",
                "designer" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "designer.profile",
                "jbpm" ) );
        */

        return group;
    }
}
