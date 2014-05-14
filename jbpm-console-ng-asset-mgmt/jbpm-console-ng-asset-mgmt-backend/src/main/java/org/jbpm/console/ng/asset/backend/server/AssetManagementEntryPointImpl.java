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
package org.jbpm.console.ng.asset.backend.server;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.asset.service.AssetManagementEntryPoint;

import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;


@Service
@ApplicationScoped
public class AssetManagementEntryPointImpl implements AssetManagementEntryPoint {

    @Inject
    private KieSessionEntryPoint sessionServices;

    public AssetManagementEntryPointImpl() {
    }
    
    @PostConstruct
    public void init(){
    }

    @Override
    public void configureRepository(String repository, String devBranch, String releaseBranch){
        Map<String, String> params = new HashMap<String, String>();
        params.put("RepositoryName", repository);
        params.put("DevBranchName", devBranch);
        params.put("RelBranchName", releaseBranch);
        sessionServices.startProcess("org.kie.management:asset-management-kmodule:1.0.0-SNAPSHOT","asset-management-kmodule.ConfigureRepository", params);
    }
    
    
    

   

}
