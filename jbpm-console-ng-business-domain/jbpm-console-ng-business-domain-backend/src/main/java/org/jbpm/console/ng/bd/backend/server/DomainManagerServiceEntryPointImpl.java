/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.bd.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.droolsjbpm.services.api.DomainManagerService;
import org.droolsjbpm.services.domain.entities.Domain;
import org.droolsjbpm.services.domain.entities.Organization;
import org.droolsjbpm.services.domain.entities.RuntimeId;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.console.ng.bd.model.DomainSummary;
import org.jbpm.console.ng.bd.model.OrganizationSummary;
import org.jbpm.console.ng.bd.model.RuntimeSummary;
import org.jbpm.console.ng.bd.service.DomainManagerServiceEntryPoint;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
@Transactional
public class DomainManagerServiceEntryPointImpl implements DomainManagerServiceEntryPoint{
    @Inject
    private DomainManagerService domainManager;

    public List<OrganizationSummary> getAllOrganizations() {
        return DomainEntitiesHelper.adaptOrganizationList(domainManager.getAllOrganizations());
    }

    public void removeOrganization(long organizationId) {
        domainManager.removeOrganization(organizationId);
    }

    public OrganizationSummary getOrganizationById(long organizationId) {
        return DomainEntitiesHelper.adaptOrganization(domainManager.getOrganizationById(organizationId));
    }

    public DomainSummary getDomainById(long domainId) {
        return DomainEntitiesHelper.adaptDomain(domainManager.getDomainById(domainId));
    }

    public List<DomainSummary> getAllDomains() {
        return DomainEntitiesHelper.adaptDomainList(domainManager.getAllDomains());
    }

    public List<DomainSummary> getAllDomainsByOrganization(long organizationId) {
        return DomainEntitiesHelper.adaptDomainList(domainManager.getAllDomainsByOrganization(organizationId));
    }

    public DomainSummary getDomainByName(String domainName) {
        return DomainEntitiesHelper.adaptDomain(domainManager.getDomainByName(domainName));
    }

    public void removeDomain(long domainId) {
        domainManager.removeDomain(domainId);
    }

    public void initOrganization(long organizationId) {
        domainManager.initOrganization(organizationId);
    }

    public void initDomain(long domainId) {
        domainManager.initDomain(domainId);
    }

    public long newOrganization(OrganizationSummary organizationSummary) {
        Organization org = new Organization();
        org.setName(organizationSummary.getName());  
        List<Domain> domains = new ArrayList<Domain>(organizationSummary.getDomains().size());
        for(DomainSummary d : organizationSummary.getDomains()){
            Domain domain = new Domain();
            domain.setName(d.getName());
            domain.setOrganization(org);
            List<RuntimeId> runtimes = new ArrayList<RuntimeId>(d.getRuntimes().size());
            for(RuntimeSummary r: d.getRuntimes()){
                RuntimeId runtimeId = new RuntimeId();
                runtimeId.setName(r.getName());
                runtimeId.setReference(r.getReference());
                runtimeId.setType(r.getType());
                runtimeId.setDomain(domain);
                runtimes.add(runtimeId);
            }
            domain.setRuntimes(runtimes);
            domains.add(domain);
        }
        org.setDomains(domains);
        
        return domainManager.storeOrganization(org);
    }

    

   
    
    
}
