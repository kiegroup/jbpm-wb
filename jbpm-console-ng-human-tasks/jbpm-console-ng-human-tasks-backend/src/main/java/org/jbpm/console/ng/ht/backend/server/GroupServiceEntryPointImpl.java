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

package org.jbpm.console.ng.ht.backend.server;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.console.ng.ht.model.Group;
import org.jbpm.console.ng.ht.service.GroupServiceEntryPoint;
import org.jbpm.console.ng.ht.service.IdentityServiceMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@ApplicationScoped
@Transactional
public class GroupServiceEntryPointImpl implements GroupServiceEntryPoint {
    
    private static final Logger log = LoggerFactory.getLogger(GroupServiceEntryPointImpl.class);

    @Inject
    private IdentityServiceMock identityServiceMock;

    @Override
    public void save(Group identity) {
        // TODO log
        log.info("** Save Group ** ");
        log.info("Group id: " + identity.getId());
        if(identity.getParent()!=null){
            log.info("Group parent: " + identity.getParent().getName());
        }

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
    public List<Group> getAll() {
        // TODO invoke mock
        return identityServiceMock.getAllGroups();
    }

    @Override
    public Group getById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Group> getGroupsByParent(String idParent) {
        // TODO Auto-generated method stub
        return null;
    }

}