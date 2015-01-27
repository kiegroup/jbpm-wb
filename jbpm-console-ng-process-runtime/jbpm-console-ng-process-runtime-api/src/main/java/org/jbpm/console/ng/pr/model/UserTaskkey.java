package org.jbpm.console.ng.pr.model;

import org.jbpm.console.ng.ga.service.ItemKey;

public class UserTaskkey implements ItemKey {
	 
	public UserTaskkey() {
	}

	private Long taskId;

    public Long getUserTaskId() {
		return taskId;
	}

	public void setUserTaskId(Long userTaskId) {
		this.taskId = userTaskId;
	}
	
	@Override
	 public int hashCode() {
	   int hash = 7;
	   hash = 13 * hash + (this.taskId != null ? this.taskId.hashCode() : 0);
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
	   final UserTaskkey other = (UserTaskkey) obj;
	   if (this.taskId != other.taskId && (this.taskId == null || !this.taskId.equals(other.taskId))) {
	     return false;
	   }
	   return true;
	 }

	 @Override
	 public String toString() {
	   return "UserTaskkey{" + "userTaskId=" + taskId + '}';
	 }
}
