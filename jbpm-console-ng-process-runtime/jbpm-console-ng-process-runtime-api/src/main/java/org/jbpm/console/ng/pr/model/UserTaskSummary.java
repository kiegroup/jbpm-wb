package org.jbpm.console.ng.pr.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class UserTaskSummary {

    private Long id;
    private String name;
    private String owner;
    private String status;

    public UserTaskSummary() {

    }

    public UserTaskSummary(Long id, String name, String owner, String status) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.status = status;

        if (owner == null || owner.isEmpty()) {
            this.owner = "---";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
