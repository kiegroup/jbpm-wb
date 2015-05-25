package org.jbpm.console.ng.rest;

import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.maven.cli.MavenCli;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.jbpm.console.ng.rest.util.JacksonRestEasyTestConfig;
import org.jbpm.console.ng.rest.util.TestConfig;
import org.jbpm.kie.services.impl.DeployedUnitImpl;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.process.audit.AuditLogService;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.helper.FluentKieModuleDeploymentHelper;
import org.kie.api.builder.helper.KieModuleDeploymentHelper;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JbpmConsoleNgRestBaseIntegrationTest {

    private static Logger logger = LoggerFactory.getLogger(JbpmConsoleNgRestBaseIntegrationTest.class);
    
    protected static TJWSEmbeddedJaxrsServer server;
    protected static int PORT;
    private static GuvnorM2Repository repository = new GuvnorM2Repository();
    static { 
        repository.init();
    }

    public static final String PROCESS_ID = "org.test.process";
    public static final String ERROR_PROCESS_ID = "org.test.error";
    public static final String BPMN2_FILE_NAME = "BPMN2-EvaluationProcess.bpmn2";
    public static final String ERROR_BPMN2_FILE_NAME = "BPMN2-ErrorBoundaryEventInterrupting.bpmn2";
    
    public static String DEPLOYMENT_ID; 
    
    private static final String USER = "user";
    
    @BeforeClass
    public static void setupClass() throws Exception {
        String groupId = "org.jbpm.console.ng.test";
        String artifactId = "bpmn2-svg";
        String version = "1.0-SNAPSHOT";
        DEPLOYMENT_ID = groupId + ":" + artifactId + ":" + version;
        
        if(TestConfig.isLocalServer()) {
            startServer();
        }
        FluentKieModuleDeploymentHelper kjarHelper = KieModuleDeploymentHelper.newFluentInstance();
        
        kjarHelper.setGroupId(groupId)
                  .setArtifactId(artifactId)
                  .setVersion(version)
                  .addResourceFilePath("/kjar/")
                  .setKBaseName("kjar");
                 
        KieBaseModel kbaseModel = kjarHelper.getKieModuleModel().newKieBaseModel("kbase.kjar").addPackage("kjar");
        kbaseModel.newKieSessionModel("kjar.session").setType(KieSessionType.STATEFUL);
        
        KieModule kieModule = kjarHelper.createKieJar();
        
        repository.deployArtifact(
                new ByteArrayInputStream(((InternalKieModule) kieModule).getBytes()), 
                new GAV(groupId, artifactId, version), 
                true);
    }

    @Before
    public void setup() throws Exception {
        // ?
    }

    @AfterClass
    public static void tearDown() {
        if (TestConfig.isLocalServer()) {
            server.stop();
        }
    }

    private static void startServer() throws Exception {
        server = new TJWSEmbeddedJaxrsServer();
        PORT = TestConfig.getAllocatedPort();
        server.setPort(PORT);
        server.start();
        server.getDeployment().getRegistry().addSingletonResource(getProcessImageRESTResource());
        server.getDeployment().setProviderFactory(JacksonRestEasyTestConfig.createRestEasyProviderFactory());
    }

    private static ProcessImageResourceImpl getProcessImageRESTResource() { 
        ProcessImageResourceImpl resource = new ProcessImageResourceImpl();
        resource.setRepository(repository);
      
        RuntimeDataService dataService = createDataService();
        resource.setRuntimeDataService(dataService);
    
        AuditLogService auditLogService = mock(AuditLogService.class);
        // TODO: create mock data (active/completed node instances) that the auditlog service will return
        resource.setAuditLogService(auditLogService);
       
        return resource;
    }

    
    private static RuntimeDataService createDataService() { 
        RuntimeDataServiceImpl dataService = new RuntimeDataServiceImpl();
        dataService.setIdentityProvider(new IdentityProvider() {
           
            @Override
            public boolean hasRole( String role ) {
                return true;
            }
            
            @Override
            public List<String> getRoles() {
                String [] users = { USER };
                return Arrays.asList(users);
            }
            
            @Override
            public String getName() {
                return USER;
            }
        }); 
        

        // create process def into and add it to the dataService
        DeploymentEvent event = createDeploymentEventWithProcessDefinition();
        dataService.onDeploy(event);
        
        return dataService;
    }
   
    private static DeploymentEvent createDeploymentEventWithProcessDefinition() { 
        DeployedUnitImpl deployedUnit = new DeployedUnitImpl(new DeploymentUnit() {
            
            @Override
            public RuntimeStrategy getStrategy() {
                return RuntimeStrategy.SINGLETON;
            }
            
            @Override
            public String getIdentifier() {
                return DEPLOYMENT_ID;
            }
        });
        
        ProcessAssetDesc processAsset = new ProcessAssetDesc();
        processAsset.getRoles().add(USER);
        
        processAsset.setId(PROCESS_ID);
        processAsset.setDeploymentId(DEPLOYMENT_ID);
        processAsset.setOriginalPath(BPMN2_FILE_NAME);
        
        deployedUnit.addAssetLocation("processDef", processAsset); 
      
        processAsset = new ProcessAssetDesc();
        processAsset.getRoles().add(USER);
        
        processAsset.setId(ERROR_PROCESS_ID);
        processAsset.setDeploymentId(DEPLOYMENT_ID);
        processAsset.setOriginalPath(ERROR_BPMN2_FILE_NAME);
        
        deployedUnit.addAssetLocation("errorProcessDef", processAsset); 
        
        DeploymentEvent event = new DeploymentEvent(DEPLOYMENT_ID, deployedUnit);
        return event;
    }
    
    protected static void buildAndDeployMavenProject(String basedir) {
        // need to backup (and later restore) the current class loader, because the Maven/Plexus does some classloader
        // magic which then results in CNFE in RestEasy client
        // run the Maven build which will create the kjar. The kjar is then either installed or deployed to local and
        // remote repo
        logger.debug("Building and deploying Maven project from basedir '{}'.", basedir);
        ClassLoader classLoaderBak = Thread.currentThread().getContextClassLoader();
        MavenCli cli = new MavenCli();
        String[] mvnArgs;
        if (TestConfig.isLocalServer()) {
            // just install into local repository when running the local server. Deploying to remote repo will fail
            // if the repo does not exist.
            mvnArgs = new String[]{"-B", "clean", "install"};
        } else {
            mvnArgs = new String[]{"-B", "clean", "deploy"};
        }
        int mvnRunResult = cli.doMain(mvnArgs, basedir, System.out, System.out);
        if (mvnRunResult != 0) {
            throw new RuntimeException("Error while building Maven project from basedir " + basedir +
                    ". Return code=" + mvnRunResult);
        }
        Thread.currentThread().setContextClassLoader(classLoaderBak);
        logger.debug("Maven project successfully built and deployed!");
    }

}
