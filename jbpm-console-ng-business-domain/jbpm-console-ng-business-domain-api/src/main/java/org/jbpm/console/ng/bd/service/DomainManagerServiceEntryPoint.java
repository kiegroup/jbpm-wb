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
package org.jbpm.console.ng.bd.service;

import java.util.List;
import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.bd.model.DomainSummary;
import org.jbpm.console.ng.bd.model.OrganizationSummary;

/**
 *
 * @author salaboy
 */
@Remote
public interface DomainManagerServiceEntryPoint {
    List<OrganizationSummary> getAllOrganizations();
    
    void removeOrganization(long organizationId);

    OrganizationSummary getOrganizationById(long organizationId);

    DomainSummary getDomainById(long domainId);

    List<DomainSummary> getAllDomains();

    List<DomainSummary> getAllDomainsByOrganization(long organizationId);

    DomainSummary getDomainByName(String domainName);

    void removeDomain(long domainId);

    void initOrganization(long organizationId);

    void initDomain(long domainId);
    
    long newOrganization(OrganizationSummary organizationSummary);
}
