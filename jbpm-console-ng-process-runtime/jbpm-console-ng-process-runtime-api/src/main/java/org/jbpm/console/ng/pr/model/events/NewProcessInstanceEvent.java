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
package org.jbpm.console.ng.pr.model.events;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NewProcessInstanceEvent implements Serializable {

  private Long newProcessInstanceId;
  private String serverTemplateId;
  private String newProcessDefId;
  private String deploymentId;
  private Integer newProcessInstanceStatus;
  private String processDefName;

  public NewProcessInstanceEvent() {
  }

  public NewProcessInstanceEvent(String serverTemplateId, String deploymentId, Long newProcessInstanceId, String newProcessDefId, String processDefName,  Integer newProcessInstanceStatus) {
    this.serverTemplateId = serverTemplateId;
    this.newProcessInstanceId = newProcessInstanceId;
    this.newProcessDefId = newProcessDefId;
    this.deploymentId = deploymentId;
    this.newProcessInstanceStatus = newProcessInstanceStatus;
    this.processDefName = processDefName;
  }

  public Long getNewProcessInstanceId() {
    return newProcessInstanceId;
  }

  public String getNewProcessDefId() {
    return newProcessDefId;
  }

  public String getDeploymentId() {
    return deploymentId;
  }

  public Integer getNewProcessInstanceStatus() {
    return newProcessInstanceStatus;
  }

  public String getProcessDefName() {
    return processDefName;
  }

  public String getServerTemplateId() {
    return serverTemplateId;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + (this.serverTemplateId != null ? this.serverTemplateId.hashCode() : 0);
    hash = 31 * hash + (this.newProcessInstanceId != null ? this.newProcessInstanceId.hashCode() : 0);
    hash = 31 * hash + (this.newProcessDefId != null ? this.newProcessDefId.hashCode() : 0);
    hash = 31 * hash + (this.deploymentId != null ? this.deploymentId.hashCode() : 0);
    hash = 31 * hash + (this.newProcessInstanceStatus != null ? this.newProcessInstanceStatus.hashCode() : 0);
    hash = 31 * hash + (this.processDefName != null ? this.processDefName.hashCode() : 0);
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
    final NewProcessInstanceEvent other = (NewProcessInstanceEvent) obj;
    if (this.newProcessInstanceId != other.newProcessInstanceId && (this.newProcessInstanceId == null || !this.newProcessInstanceId.equals(other.newProcessInstanceId))) {
      return false;
    }
    if ((this.newProcessDefId == null) ? (other.newProcessDefId != null) : !this.newProcessDefId.equals(other.newProcessDefId)) {
      return false;
    }
    if ((this.deploymentId == null) ? (other.deploymentId != null) : !this.deploymentId.equals(other.deploymentId)) {
      return false;
    }
    if (this.newProcessInstanceStatus != other.newProcessInstanceStatus && (this.newProcessInstanceStatus == null || !this.newProcessInstanceStatus.equals(other.newProcessInstanceStatus))) {
      return false;
    }
    if ((this.processDefName == null) ? (other.processDefName != null) : !this.processDefName.equals(other.processDefName)) {
      return false;
    }
    if ((this.serverTemplateId == null) ? (other.serverTemplateId != null) : !this.serverTemplateId.equals(other.serverTemplateId)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "NewProcessInstanceEvent{serverTemplateId=" +serverTemplateId + ", " + "newProcessInstanceId=" + newProcessInstanceId + ", newProcessDefId=" + newProcessDefId + ", deploymentId=" + deploymentId + ", newProcessInstanceStatus=" + newProcessInstanceStatus + ", processDefName=" + processDefName + '}';
  }

  
}
