package org.jbpm.console.ng.asset.backend.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jbpm.console.ng.bd.service.DeploymentUnitProvider;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.cdi.Kjar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Kjar
public class AssetMgmtDeploymentUnitProvider implements DeploymentUnitProvider<DeploymentUnit> {

    private static final String ASSET_MGMT_PROPS = "/guvnor-asset-mgmt.properties";

    private static final Logger logger = LoggerFactory.getLogger(AssetMgmtDeploymentUnitProvider.class);

    @Inject
    private GuvnorM2Repository m2repository;

    public void setM2Repository(GuvnorM2Repository m2Repository) {
        this.m2repository = m2Repository;
    }

    @Override
    public Set<DeploymentUnit> getDeploymentUnits() {
        Set<DeploymentUnit> units = new HashSet<DeploymentUnit>();
        URL assetMgmtPropsLocation = this.getClass().getResource(ASSET_MGMT_PROPS);
        if (assetMgmtPropsLocation != null) {
            String file = assetMgmtPropsLocation.toExternalForm();
            InputStream in = this.getClass().getResourceAsStream(ASSET_MGMT_PROPS);
            if (in != null) {
                try {
                    Properties assetmgmtProps = new Properties();
                    assetmgmtProps.load(in);

                    if (assetmgmtProps.containsKey("groupId") && assetmgmtProps.containsKey("artifactId") && assetmgmtProps.containsKey("version")) {

                        deployToLocalMavenIfNeeded(file, assetmgmtProps);

                        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(assetmgmtProps.getProperty("groupId"),
                                assetmgmtProps.getProperty("artifactId"), assetmgmtProps.getProperty("version"));
                        unit.addAttribute("sync", "false");
                        units.add(unit);
                        logger.info("Found guvnor asset management deployment unit {} attempting to deploy it", unit);
                    }
                } catch (IOException e) {
                    logger.warn("Unable to read guvnor asset mgmt deployment unit properties due to {}", e.getMessage());
                } finally {
                    try {
                        in.close();
                    } catch (IOException ignore) {
                        // Nothing to do
                    }
                }
            }
        }
        return units;
    }

    private void deployToLocalMavenIfNeeded(String artifactLocation, Properties properties) {
        InputStream inputStream = null;
        try {
            if ( artifactLocation.contains( "!" ) ) {
                artifactLocation = artifactLocation.substring(0, artifactLocation.indexOf("!"));

                if (artifactLocation.startsWith("jar:")) {
                    // remove jar: prefix from it
                    artifactLocation = artifactLocation.substring(4);
                }

                inputStream = new URL(artifactLocation).openStream();
            } else if (artifactLocation.startsWith("vfs")){
                artifactLocation = artifactLocation.replaceFirst(ASSET_MGMT_PROPS, "");
                URL vfsUrl = new URL(artifactLocation);
                vfsUrl.openConnection();
                Object vfsFile = vfsUrl.getContent();

                Method m1 = vfsFile.getClass().getMethod("getPhysicalFile", new Class[]{});
                File contentsFile = (File) m1.invoke(vfsFile, new Object[]{});

                File physicalFile = new File(contentsFile.getParentFile(), properties.getProperty("artifactId") + "-" + properties.getProperty("version") + ".jar");


                inputStream = new FileInputStream(physicalFile);
            }

            final GAV gav = new GAV(properties.getProperty("groupId"), properties.getProperty("artifactId"), properties.getProperty("version"));

            final File artifactInRepo = m2repository.getArtifactFileFromRepository(gav);

            if (artifactInRepo == null && inputStream != null) {
                m2repository.deployArtifact(inputStream, gav, false);
            }
        } catch (Exception e) {
            logger.warn("Unable to deploy asset mgmt kjar into maven repo", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                    // Nothing to do
                }
            }
        }
    }
}
