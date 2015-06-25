/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.ht.backend.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ht.service.TaskFormManagementService;
import org.jbpm.kie.services.impl.FormManagerService;

/**
 * Created by pefernan
 */
@Service
@ApplicationScoped
public class TaskFormManagementServiceImpl implements TaskFormManagementService {

    @Inject
    FormManagerService formManagerService;

    @Override
    public List<String> getAvailableDeployments() {
        Set<String> deployments = formManagerService.getAllDeployments();
        if (deployments != null) {
            ArrayList result = new ArrayList<String>( deployments );
            Collections.sort( result );
            return result;
        }
        return null;
    }

    @Override
    public List<String> getFormsByDeployment( String deploymentId ) {
        Map<String, String> forms = formManagerService.getAllFormsByDeployment( deploymentId );
        if (forms != null) {
            ArrayList result = new ArrayList<String>( forms.keySet() );
            Collections.sort( result );
            return result;
        }

        return null;
    }
}
