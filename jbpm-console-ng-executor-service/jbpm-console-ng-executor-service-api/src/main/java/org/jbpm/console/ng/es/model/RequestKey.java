package org.jbpm.console.ng.es.model;

import org.jbpm.console.ng.ga.service.ItemKey;

public class RequestKey implements ItemKey {
    private Long id;
    public RequestKey() {
    }

    public RequestKey(Long id) {
      this.id = id;
    }

    public Long getId() {
      return id;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
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
      final RequestKey other = (RequestKey) obj;
      if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "RequestKey{" + "RequestId=" + id + '}';
    }
}
