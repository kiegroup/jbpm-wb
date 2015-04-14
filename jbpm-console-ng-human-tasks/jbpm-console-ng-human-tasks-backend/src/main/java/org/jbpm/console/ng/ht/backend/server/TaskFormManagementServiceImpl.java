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
