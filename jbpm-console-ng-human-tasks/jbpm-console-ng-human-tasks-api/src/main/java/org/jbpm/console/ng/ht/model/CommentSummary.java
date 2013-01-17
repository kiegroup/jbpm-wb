/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.ht.model;

import java.io.Serializable;
import java.util.Date;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 *
 * @author salaboy
 */
@Portable
public class CommentSummary implements Serializable{
    
    
    private long id;

    
    private String text;
    
    
    private String addedBy;
    
    private Date addedAt;  

    public CommentSummary(long id, String text, String addedBy, Date addedAt) {
        this.id = id;
        this.text = text;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
    }

    public CommentSummary(String text, String addedBy, Date addedAt) {
        this.text = text;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
    }
    
    

    public CommentSummary() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "CommentSummary{" + "id=" + id + ", text=" + text + ", addedBy=" + addedBy + ", addedAt=" + addedAt + '}';
    }
    
    
}
