/*
 * Copyright 2012 JBoss by Red Hat.
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

/**
 *
 * @author salaboy
 */
@Portable
public class ProcessSummary {

    private String id;
    private String name;
    private String packageName;
    private String type;
    private String version;
    private String originalPath;
    private String domainId;
    private String encodedProcessSource;

    public ProcessSummary() {
    }

    public ProcessSummary(String id, String name, String domainId, String packageName, String type, String version, String originalpath, String processSource) {
        this.id = id;
        this.name = name;
        this.domainId = domainId;
        this.packageName = packageName;
        this.type = type;
        this.version = version;
        this.originalPath = originalpath;
        this.encodedProcessSource = processSource;
    }

  
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDomainId() {
      return domainId;
    }

    public void setDomainId(String domainId) {
      this.domainId = domainId;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getEncodedProcessSource() {
        return encodedProcessSource;
    }

    public void setEncodedProcessSource(String encodedProcessSource) {
        this.encodedProcessSource = encodedProcessSource;
    }

}
