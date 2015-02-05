package org.jbpm.console.ng.pr.model;

import org.jbpm.console.ng.ga.service.ItemKey;

public class UserTaskkey implements ItemKey {
	 
	public UserTaskkey() {
	}

	private Long userTaskId;

    public Long getUserTaskId() {
		return userTaskId;
	}

	public void setUserTaskId(Long userTaskId) {
		this.userTaskId = userTaskId;
	}
	
	@Override
	 public int hashCode() {
	   int hash = 7;
	   hash = 13 * hash + (this.userTaskId != null ? this.userTaskId.hashCode() : 0);
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
	   if (this.userTaskId != other.userTaskId && (this.userTaskId == null || !this.userTaskId.equals(other.userTaskId))) {
	     return false;
	   }
	   return true;
	 }

	 @Override
	 public String toString() {
	   return "UserTaskkey{" + "userTaskId=" + userTaskId + '}';
	 }
}
