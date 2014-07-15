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

package org.jbpm.console.ng.ht.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.ga.service.GenericServiceEntryPoint;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.ht.model.TasksPerDaySummary;



/**
 *
 * @author salaboy
 */
@Remote
public interface TaskCalendarService extends GenericServiceEntryPoint<TaskKey, TasksPerDaySummary>{

  
}
