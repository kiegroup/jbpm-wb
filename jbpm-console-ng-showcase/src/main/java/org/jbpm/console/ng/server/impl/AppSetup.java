/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jbpm.console.ng.bd.service.AdministrationService;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.screens.workbench.backend.BaseAppSetup;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.io.IOService;

@ApplicationScoped
@Startup
public class AppSetup extends BaseAppSetup {

    // default repository section - start
    private static final String JBPM_WB_PLAYGROUND_ALIAS = "jbpm-playground";
    private static final String JBPM_WB_PLAYGROUND_ORIGIN = "https://github.com/droolsjbpm/jbpm-playground.git";

    private AdministrationService administrationService;

    private Event<ApplicationStarted> applicationStartedEvent;

    protected AppSetup() {
    }

    @Inject
    public AppSetup( @Named("ioStrategy") final IOService ioService,
                     final RepositoryService repositoryService,
                     final OrganizationalUnitService organizationalUnitService,
                     final KieProjectService projectService,
                     final ConfigurationService configurationService,
                     final ConfigurationFactory configurationFactory,
                     final AdministrationService administrationService,
                     final Event<ApplicationStarted> applicationStartedEvent ) {
        super( ioService, repositoryService, organizationalUnitService, projectService, configurationService, configurationFactory );
        this.administrationService = administrationService;
        this.applicationStartedEvent = applicationStartedEvent;
    }

    @PostConstruct
    public void onStartup() {
        if ( !"false".equalsIgnoreCase( System.getProperty( "org.kie.demo" ) ) ) {
            administrationService.bootstrapRepository( "demo", JBPM_WB_PLAYGROUND_ALIAS, JBPM_WB_PLAYGROUND_ORIGIN,
                                                       "", "" );
        } else if ( "true".equalsIgnoreCase( System.getProperty( "org.kie.example" ) ) ) {
            administrationService.bootstrapRepository( "example", "repository1", null, "", "" );
            administrationService.bootstrapProject( "repository1", "org.kie.example", "project1", "1.0.0-SNAPSHOT" );
        }
        
        configurationService.addConfiguration( getGlobalConfiguration() );

        // notify cluster service that bootstrap is completed to start synchronization
        applicationStartedEvent.fire( new ApplicationStarted() );
    }

    private ConfigGroup getGlobalConfiguration() {
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                                                                       GLOBAL_SETTINGS,
                                                                       "" );

        group.addConfigItem( configurationFactory.newConfigItem( "build.enable-incremental",
                                                                 "true" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "support.runtime.deploy",
                                                                 "true" ) );

        return group;
    }
}
