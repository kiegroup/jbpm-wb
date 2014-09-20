package org.jbpm.console.ng.ga.model;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.internal.query.QueryFilter;

@Portable
public class PortableQueryFilter extends QueryFilter {

    public PortableQueryFilter() { 
        super();
    }
    
    public PortableQueryFilter(int offset, int count, boolean isSingleResult, String language, String orderBy, boolean isAscending) {
        this.offset = offset;
        this.count = count;
        this.orderBy = orderBy;
        setSingleResult(isSingleResult);
        setLanguage(language);
        setAscending(isAscending);
    }
    
    public PortableQueryFilter(int offset, int count, boolean isSingleResult, String language, String orderBy, boolean isAscending, String filterParams, Map<String, Object> params) {
        this.offset = offset;
        this.count = count;
        this.orderBy = orderBy;
        setSingleResult(isSingleResult);
        setLanguage(language);
        setAscending(isAscending);
        setFilterParams(filterParams);
        setParams(params);
    }
}
