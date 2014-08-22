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


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.guvnor.asset.management.model.BuildProjectStructureEvent;
import org.guvnor.asset.management.model.ConfigureRepositoryEvent;
import org.guvnor.asset.management.model.PromoteChangesEvent;

import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;

@ApplicationScoped
public class AdvancedAssetManagementImpl {

  @Inject
  private KieSessionEntryPoint sessionServices;

  public AdvancedAssetManagementImpl() {
  }

  public void configureRepository(@Observes ConfigureRepositoryEvent event) {
    sessionServices.startProcess("org.kie.management:asset-management-kmodule:1.0.0-SNAPSHOT", "asset-management-kmodule.ConfigureRepository", event.getParams());
  }

  public void buildProject(@Observes BuildProjectStructureEvent event) {
    sessionServices.startProcess("org.kie.management:asset-management-kmodule:1.0.0-SNAPSHOT", "asset-management-kmodule.BuildProject", event.getParams());
  }

  public void promoteChanges(@Observes PromoteChangesEvent event) {
    sessionServices.startProcess("org.kie.management:asset-management-kmodule:1.0.0-SNAPSHOT", "asset-management-kmodule.PromoteAssets", event.getParams());
  }

}
