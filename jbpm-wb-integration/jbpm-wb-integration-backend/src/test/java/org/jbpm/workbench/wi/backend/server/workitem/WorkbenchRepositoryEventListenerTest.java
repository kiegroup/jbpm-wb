/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.wi.backend.server.workitem;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WorkbenchRepositoryEventListenerTest {

    @Test
    public void testVersionResolveNoRangeOnFinal() {
        
        WorkbenchRepositoryEventListener listener = new WorkbenchRepositoryEventListener();
        
        String version = "7.16.0.Final";
        
        String resolved = listener.resolveVersion(version, false);
        assertEquals(version, resolved);
    }
    
    @Test
    public void testVersionResolveNoRangeOnSnapshot() {
        
        WorkbenchRepositoryEventListener listener = new WorkbenchRepositoryEventListener();
        
        String version = "7.16.0-SNAPSHOT";
        
        String resolved = listener.resolveVersion(version, false);
        assertEquals(version, resolved);
    }
    
    @Test
    public void testVersionResolveRangeOnFinal() {
        
        WorkbenchRepositoryEventListener listener = new WorkbenchRepositoryEventListener();
        
        String version = "7.16.0.Final";
        
        String resolved = listener.resolveVersion(version, true);
        assertEquals("[7.16,)", resolved);
    }
    
    @Test
    public void testVersionResolveRangeOnSnapshot() {
        
        WorkbenchRepositoryEventListener listener = new WorkbenchRepositoryEventListener();
        
        String version = "7.16.0-SNAPSHOT";
        
        String resolved = listener.resolveVersion(version, true);
        assertEquals("[7.16,)", resolved);
    }
    
    @Test
    public void testVersionResolveRangeOnShortFinal() {
        
        WorkbenchRepositoryEventListener listener = new WorkbenchRepositoryEventListener();
        
        String version = "7.Final";
        
        String resolved = listener.resolveVersion(version, true);
        assertEquals("[7,)", resolved);
    }
}
