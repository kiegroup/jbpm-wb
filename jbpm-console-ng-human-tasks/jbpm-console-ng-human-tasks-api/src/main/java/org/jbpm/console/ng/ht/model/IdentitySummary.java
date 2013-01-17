package org.jbpm.console.ng.ht.model;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class IdentitySummary implements Serializable {

    private static final long serialVersionUID = -2770686571222400395L;

    private String id;
    private String type;
    
    public IdentitySummary() {
        
    }
    
    public IdentitySummary(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
}
