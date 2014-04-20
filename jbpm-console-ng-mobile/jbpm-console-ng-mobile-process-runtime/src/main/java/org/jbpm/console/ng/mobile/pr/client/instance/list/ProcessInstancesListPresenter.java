/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.mobile.pr.client.instance.list;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;

/**
 *
 * @author livthomas
 */
public class ProcessInstancesListPresenter {
    
    public interface ProcessInstancesListView extends MGWTUberView<ProcessInstancesListPresenter> {
        
        void render(List<ProcessInstanceSummary> instances);
        
    }

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;
    
    @Inject
    private ProcessInstancesListView view;

    public ProcessInstancesListView getView() {
        return view;
    }
    
    public void refresh() {
        dataServices.call(new RemoteCallback<Collection<ProcessInstanceSummary>>() {
            @Override
            public void callback(Collection<ProcessInstanceSummary> instances) {
                view.render(new ArrayList(instances));
            }
        }).getProcessInstances();
    }
    
    public void refresh(String definitionId) {
        dataServices.call(new RemoteCallback<Collection<ProcessInstanceSummary>>() {
            @Override
            public void callback(Collection<ProcessInstanceSummary> instances) {
                view.render(new ArrayList(instances));
            }
        }).getProcessInstancesByProcessDefinition(definitionId);
    }

}
