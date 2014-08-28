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
package org.jbpm.console.ng.bd.backend.server.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.console.ng.bd.service.DeploymentUnitProvider;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.cdi.Kjar;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows to define KModuleDeploymentUnits based on either system property
 * or read from a file.<br/>
 * Structure of the deployment unit must conform to one of the format: <br/>
 * 1. groupId:artifactId:version<br/>
 * 2. groupId:artifactId:version:kbase-name:ksession-name<br/>
 *
 * <ul>
 *     <li>
 *         Provide a list of semicolon separated GAV deployments in above format as system parameter called:
 *         jbpm.deployment.units
 *     </li>
 *     <li>
 *         Provide text file where each line defines single deployment in above format and file location should be
 *         given as absolute path via system parameter called: jbpm.deployment.units.file
 *     </li>
 * </ul>
 */
@ApplicationScoped
@Kjar
public class UserDefinedDeploymentUnitProvider implements DeploymentUnitProvider<DeploymentUnit> {

    private static final Logger logger = LoggerFactory.getLogger(UserDefinedDeploymentUnitProvider.class);

    @Override
    public Set<DeploymentUnit> getDeploymentUnits() {
        Set<DeploymentUnit> deploymentUnits = new HashSet<DeploymentUnit>();
        // read in deployment units from system property
        String propertyDeployments = System.getProperty("jbpm.deployment.units");
        if (propertyDeployments != null) {
            // first split based on ; to find all GAVs
            String[] allGAVs = propertyDeployments.split(";");


            for (String gav : allGAVs) {
                DeploymentUnit unit = buildDeploymentUnit(gav);
                if (unit != null) {
                    deploymentUnits.add(unit);
                }
            }
        }

        // read in deployment units from a file
        String deploymentUnitsFile = System.getProperty("jbpm.deployment.units.file");
        if (deploymentUnitsFile != null) {
            File duFile = new File(deploymentUnitsFile);
            if (duFile.exists()) {
                try {
                    Scanner duFileScanner = new Scanner(duFile);
                    while (duFileScanner.hasNextLine()) {
                        String gav = duFileScanner.nextLine();
                        DeploymentUnit unit = buildDeploymentUnit(gav);
                        if (unit != null) {
                            deploymentUnits.add(unit);
                        }
                    }
                } catch (FileNotFoundException e) {

                }
            }
        }
        return deploymentUnits;
    }

    protected DeploymentUnit buildDeploymentUnit(String gav) {

        String[] elems = gav.split(":");
        if (elems.length == 3) {
            return new KModuleDeploymentUnit(elems[0],elems[1],elems[2]);
        } else if (elems.length == 5) {
            return new KModuleDeploymentUnit(elems[0],elems[1],elems[2], elems[3], elems[4]);
        } else {
            logger.warn("Unknown deployment unit {}", gav);
        }
        return null;
    }
}
