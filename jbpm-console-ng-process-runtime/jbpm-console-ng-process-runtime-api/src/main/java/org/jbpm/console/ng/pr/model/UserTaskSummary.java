package org.jbpm.console.ng.pr.model;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;

@Portable
public class UserTaskSummary extends GenericSummary {

	private Long taskId;
    private String taskName;
    private String owner;
    private String status;
    private String description;
    private String createdBy;
    private Date createdOn;

	private Date activationTime;
    private Date expirationTime;
    public UserTaskSummary() {

    }

    public UserTaskSummary(Long id, String name, String owner, String status) {
    	this.id = id;
		this.name = taskName;
    	this.taskId = id;
        this.taskName = name;
        this.owner = owner;
        this.status = status;

        if (owner == null || owner.isEmpty()) {
            this.owner = "---";
        }
    }
    
    

    public UserTaskSummary(Long id, String taskName, String owner, String status,
			String description, String createdBy,
			Date createdOn, Date activationTime, Date expirationTime) {
		super();
    	this.taskId = id;
		this.id = id;
		this.name = taskName;
		this.taskName = taskName;
		this.owner = owner;
		this.status = status;
		this.description = description;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
		this.activationTime = activationTime;
		this.expirationTime = expirationTime;
	}

    public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public Long getId() {
        return taskId;
    }

    public void setId(Long id) {
        this.taskId = id;
    }

    public String getName() {
        return taskName;
    }

    public void setName(String name) {
        this.taskName = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getActivationTime() {
		return activationTime;
	}

	public void setActivationTime(Date activationTime) {
		this.activationTime = activationTime;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

}
