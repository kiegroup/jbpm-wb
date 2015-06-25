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

package org.jbpm.console.ng.pr.backend.server.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.kie.services.impl.IdentityProviderAwareProcessListener;
import org.jbpm.runtime.manager.api.qualifiers.Process;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.manager.EventListenerProducer;
@Process
public class IdentityProviderAwareProcessListenerProducer implements EventListenerProducer<ProcessEventListener> {

    @Override
    public List<ProcessEventListener> getEventListeners(String identifier,
                                                                        Map<String, Object> params) {
        
        List<ProcessEventListener> identityProviderAwareProcessListenerList =  new ArrayList<ProcessEventListener>();
        InitiatorProviderProcessListener initiatorProviderProcessListener = new InitiatorProviderProcessListener( (KieSession)params.get( "ksession" ) );
        identityProviderAwareProcessListenerList.add( initiatorProviderProcessListener );
        return identityProviderAwareProcessListenerList;
    }

}
