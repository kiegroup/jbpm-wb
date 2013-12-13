/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.ht.backend.server;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;

public class TestProducers {
    private final IOService ioService = new IOServiceNio2WrapperImpl();


    private EntityManagerFactory emf;

    @Produces
    @ApplicationScoped
    @PersistenceUnit(unitName = "org.jbpm.servies.task")
    public EntityManagerFactory getEntityManagerFactory() {
        if (this.emf == null) {
            this.emf = Persistence.createEntityManagerFactory("org.jbpm.servies.task");

        }
        return this.emf;
    }


    @Produces
    @Named("ioStrategy")
    public IOService createIOService() {
        return ioService;
    }

}