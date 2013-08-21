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

import org.jbpm.console.ng.ht.model.Group;
import org.jbpm.console.ng.ht.model.TypeRole;
import org.jbpm.console.ng.ht.model.User;
import org.jbpm.console.ng.ht.service.IdentityServiceMock;

import com.google.common.collect.Lists;

public class IdentityServiceMockImpl implements IdentityServiceMock {

    @Override
    public List<Group> getAllGroups() {
        List<Group> groups = Lists.newArrayList();
        groups.add(new Group("Manager"));
        groups.add(new Group("Director"));
        groups.add(new Group("Secretary"));
        groups.add(new Group("Development"));

        return groups;
    }

    @Override
    public List<User> getAllUser() {
        List<User> users = Lists.newArrayList();
        
        User salaboy = new User("salaboy"); 
        List<TypeRole> typesRole = Lists.newArrayList();
        typesRole.add(new TypeRole("Writer"));
        typesRole.add(new TypeRole("Release"));
        typesRole.add(new TypeRole("Manager"));
        typesRole.add(new TypeRole("IT"));
        List<Group> groups = Lists.newArrayList();
        groups.add(new Group("Manager"));
        salaboy.setTypesRole(typesRole);
        salaboy.setGroups(groups);
        users.add(salaboy);
        
        User maciej = new User("maciej"); 
        typesRole = Lists.newArrayList();
        typesRole.add(new TypeRole("Translator"));
        groups = Lists.newArrayList();
        groups.add(new Group("Secretary"));
        maciej.setTypesRole(typesRole);
        maciej.setGroups(groups);
        users.add(maciej);
        
        User kris = new User("kris"); 
        typesRole = Lists.newArrayList();
        typesRole.add(new TypeRole("Reviewer"));
        groups = Lists.newArrayList();
        groups.add(new Group("Secretary"));
        kris.setTypesRole(typesRole);
        kris.setGroups(groups);
        users.add(kris);
        
        User tiho = new User("tiho"); 
        typesRole = Lists.newArrayList();
        typesRole.add(new TypeRole("Writer"));
        groups = Lists.newArrayList();
        groups.add(new Group("Development"));
        tiho.setTypesRole(typesRole);
        tiho.setGroups(groups);
        users.add(tiho);
        
        User marco = new User("marco"); 
        typesRole = Lists.newArrayList();
        typesRole.add(new TypeRole("Reviewer"));
        groups = Lists.newArrayList();
        groups.add(new Group("Secretary"));
        marco.setTypesRole(typesRole);
        marco.setGroups(groups);
        users.add(marco);
        
        User katy = new User("katy"); 
        typesRole = Lists.newArrayList();
        typesRole.add(new TypeRole("HR"));
        groups = Lists.newArrayList();
        groups.add(new Group("Secretary"));
        katy.setTypesRole(typesRole);
        katy.setGroups(groups);
        users.add(katy);
        
        User jack = new User("jack"); 
        typesRole = Lists.newArrayList();
        typesRole.add(new TypeRole("IT"));
        groups = Lists.newArrayList();
        groups.add(new Group("Development"));
        jack.setTypesRole(typesRole);
        jack.setGroups(groups);
        users.add(jack);
        
        return users;
    }

    @Override
    public List<TypeRole> getAllTypeRole() {
        List<TypeRole> typesRole = Lists.newArrayList();
        typesRole.add(new TypeRole("Translator"));
        typesRole.add(new TypeRole("Reviewer"));
        typesRole.add(new TypeRole("Writer"));
        typesRole.add(new TypeRole("HR"));
        typesRole.add(new TypeRole("Accounting"));
        typesRole.add(new TypeRole("IT"));
        typesRole.add(new TypeRole("Release"));
        typesRole.add(new TypeRole("Manager"));
        
        return typesRole;
    }

}
