/*
 * Copyright 2012 JBoss by Red Hat.
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
import org.droolsjbpm.services.domain.entities.Domain;
import org.droolsjbpm.services.domain.entities.Organization;
import org.droolsjbpm.services.domain.entities.RuntimeId;
import org.jbpm.console.ng.bd.model.DomainSummary;
import org.jbpm.console.ng.bd.model.OrganizationSummary;
import org.jbpm.console.ng.bd.model.RuntimeSummary;

/**
 *
 * @author salaboy
 */
public class DomainEntitiesHelper {
    
    public static List<DomainSummary> adaptDomainList(List<Domain> domains){
        List<DomainSummary> domainSummaries = new ArrayList<DomainSummary>(domains.size());
        for(Domain domain : domains){
            domainSummaries.add(adaptDomain(domain));
        }
        return domainSummaries;
    }
    
    public static DomainSummary adaptDomain(Domain domain){
        return new DomainSummary(domain.getId(), domain.getName(), domain.getOrganization().getName(), adaptRuntimeList(domain.getRuntimes()));
    }
    
    public static List<RuntimeSummary> adaptRuntimeList(List<RuntimeId> runtimes){
        List<RuntimeSummary> runtimeSummaries = new ArrayList<RuntimeSummary>(runtimes.size());
        for(RuntimeId runtime: runtimes){
            runtimeSummaries.add(adaptRuntime(runtime));
        }
        return runtimeSummaries;
    }
    
    public static RuntimeSummary adaptRuntime(RuntimeId runtime){
        return new RuntimeSummary(runtime.getId(), runtime.getName(), runtime.getReference(), runtime.getType());
    }

    public static List<OrganizationSummary> adaptOrganizationList(List<Organization> organizations) {
        List<OrganizationSummary> organizationSummaries = new ArrayList<OrganizationSummary>(organizations.size());
        for(Organization org : organizations){
            organizationSummaries.add(adaptOrganization(org));
        }
        return organizationSummaries;
    }
    
    public static OrganizationSummary adaptOrganization(Organization organization){
        return new OrganizationSummary(organization.getId(), organization.getName(), adaptDomainList(organization.getDomains()));
    }
    
}
