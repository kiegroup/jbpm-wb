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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.ht.backend.server;


import java.util.Collections;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.cdi.Kjar;
import org.kie.internal.identity.IdentityProvider;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;

@ApplicationScoped
public class TestProducers {
    private final IOService ioService = new IOServiceNio2WrapperImpl();

    @Inject
    @Kjar
    private org.jbpm.services.api.DeploymentService deploymentService;

    private EntityManagerFactory emf;

    @Produces
    public EntityManagerFactory produceEntityManagerFactory() {
        if (this.emf == null) {
            this.emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.domain");
        }

        return this.emf;
    }

    @PreDestroy
    public void doCleanUp() {
        EntityManagerFactoryManager.get().clear();
    }

    @Produces
    @Named("ioStrategy")
    public IOService createIOService() {
        return ioService;
    }

    @Produces
    public DeploymentService produceKjarDeployService() {
        return deploymentService;
    }

    @Produces
    public IdentityProvider produceIdentityProvider() {
        return new IdentityProvider() {
            @Override
            public String getName() {
                return "dummy";
            }

            @Override
            public List<String> getRoles() {
                return Collections.<String>emptyList();
            }

            @Override
            public boolean hasRole(String role) {
                return false;
            }
        } ;
    }

}