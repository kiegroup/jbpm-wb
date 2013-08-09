package org.jbpm.console.ng.ht.backend.server;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.console.ng.ht.model.IdentitySummary;
import org.jbpm.console.ng.ht.service.GroupServiceEntryPoint;

@Service
@ApplicationScoped
@Transactional
public class GroupServiceEntryPointImpl implements GroupServiceEntryPoint {

    @Override
    public void save(IdentitySummary identity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<IdentitySummary> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IdentitySummary getById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IdentitySummary getByType(String id) {
        // TODO Auto-generated method stub
        return null;
    }

}
