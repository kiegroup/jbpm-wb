/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.ht.backend.server;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.jbpm.shared.services.cdi.Selectable;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceNio2WrapperImpl;
import org.kie.internal.task.api.UserGroupCallback;

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
    @ApplicationScoped
    public EntityManager getEntityManager() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        return em;
    }

    @ApplicationScoped
    public void commitAndClose(@Disposes EntityManager em) {
        try {
            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {

        }
    }


    @Inject
    @Selectable
    private UserGroupCallback userGroupCallback;

    @Produces
    public UserGroupCallback produceSelectedUserGroupCalback() {
        return userGroupCallback;
    }

    @Produces
    @Named("ioStrategy")
    public IOService createIOService() {
        return ioService;
    }

}