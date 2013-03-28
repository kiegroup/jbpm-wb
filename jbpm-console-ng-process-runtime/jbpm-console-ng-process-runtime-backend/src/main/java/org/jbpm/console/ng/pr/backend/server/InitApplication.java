package org.jbpm.console.ng.pr.backend.server;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.droolsjbpm.services.api.DomainManagerService;
import org.droolsjbpm.services.domain.entities.Domain;
import org.droolsjbpm.services.domain.entities.Organization;
import org.droolsjbpm.services.domain.entities.RuntimeId;
import org.jbpm.shared.services.cdi.Startup;

@ApplicationScoped
@Startup
public class InitApplication {
    
    @Inject
    private DomainManagerService domainManagerService;

    @PostConstruct
    public void newInitDomain() throws Exception {
        UserTransaction ut = null;
        try {
            ut = InitialContext.doLookup("java:comp/UserTransaction");
        } catch (Exception ex) {
            try {
                ut = InitialContext.doLookup(System.getProperty("jbpm.ut.jndi.lookup", "java:jboss/UserTransaction"));
                
            } catch (Exception e1) {
                throw new RuntimeException("Cannot find UserTransaction", e1);
            }
        }
        ut.begin();
        try {
            List<Organization> organizations = domainManagerService.getAllOrganizations();
            if (organizations == null || organizations.isEmpty()) {
                // remove stored session ids since there is not organizations available
                cleanup();
                organizations = new ArrayList<Organization>();
                Domain domainRelease = new Domain();
                domainRelease.setName("Release Domain");
                List<RuntimeId> runtimesRelease = new ArrayList<RuntimeId>();
                RuntimeId releaseRuntime = new RuntimeId();
                releaseRuntime.setName("Release Runtime");
                releaseRuntime.setReference("processes/release/");
                releaseRuntime.setType("Folder/Runtime Manager(Singleton)");
                
                runtimesRelease.add(releaseRuntime);
                
                domainRelease.setRuntimes(runtimesRelease);
                
                Domain domainGeneral = new Domain();
                domainGeneral.setName("General Domain");
                
                List<RuntimeId> runtimesGeneral = new ArrayList<RuntimeId>();
                RuntimeId generalRuntime = new RuntimeId();
                generalRuntime.setName("General Runtime");
                generalRuntime.setReference("processes/general/");
                generalRuntime.setType("Folder/Runtime Manager(Singleton)");
                
                runtimesGeneral.add(generalRuntime);
                
                
                domainGeneral.setRuntimes(runtimesGeneral);
                
                List<Domain> domains = new ArrayList<Domain>();
                domains.add(domainRelease);
                domains.add(domainGeneral);
                
                
                
                Organization organizationSummary = new Organization();
                organizationSummary.setDomains(domains);
                organizationSummary.setName("jBPM Console NG");
                
                domainManagerService.storeOrganization( organizationSummary );
                organizations.add(organizationSummary);
            }
            for (Organization org : organizations) {
                domainManagerService.initOrganization(org.getId());
            }
            
            ut.commit();
        } catch (Exception e) {
            e.printStackTrace();
            ut.rollback();
        }
        
    }
    
    protected void cleanup() {
        String location = System.getProperty("jbpm.data.dir", System.getProperty("jboss.server.data.dir"));
        if (location == null) {
            location = System.getProperty("java.io.tmpdir");
        }
        File dataDir = new File(location);
        if (dataDir.exists()) {
            
            String[] jbpmSerFiles = dataDir.list(new FilenameFilter() {
                
                @Override
                public boolean accept(File dir, String name) {
                    
                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                new File(dataDir, file).delete();
            }
        }
    }
}
