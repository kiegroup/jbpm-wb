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

package org.jbpm.console.ng.pr.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.service.ItemKey;

/**
 *
 * @author salaboy
 */
@Portable
public class ProcessDefinitionKey implements ItemKey {
  private String deploymentId;
  private String processId;

  public ProcessDefinitionKey(String deploymentId, String processId) {
    this.deploymentId = deploymentId;
    this.processId = processId;
  }

  public ProcessDefinitionKey() {
  }

  public String getDeploymentId() {
    return deploymentId;
  }

  public String getProcessId() {
    return processId;
  }

  @Override
  public String toString() {
    return "ProcessDefinitionKey{" + "deploymentId=" + deploymentId + ", processId=" + processId + '}';
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (this.deploymentId != null ? this.deploymentId.hashCode() : 0);
    hash = 97 * hash + (this.processId != null ? this.processId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ProcessDefinitionKey other = (ProcessDefinitionKey) obj;
    if ((this.deploymentId == null) ? (other.deploymentId != null) : !this.deploymentId.equals(other.deploymentId)) {
      return false;
    }
    if ((this.processId == null) ? (other.processId != null) : !this.processId.equals(other.processId)) {
      return false;
    }
    return true;
  }
  
  
}
