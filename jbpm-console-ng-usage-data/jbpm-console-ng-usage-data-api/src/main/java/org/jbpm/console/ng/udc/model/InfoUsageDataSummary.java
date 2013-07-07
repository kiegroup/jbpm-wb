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

package org.jbpm.console.ng.udc.model;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class InfoUsageDataSummary implements Serializable {

    private static final long serialVersionUID = 560071525389900074L;

    private String module;

    private String components;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((components == null) ? 0 : components.hashCode());
        result = prime * result + ((module == null) ? 0 : module.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InfoUsageDataSummary other = (InfoUsageDataSummary) obj;
        if (components == null) {
            if (other.components != null)
                return false;
        } else if (!components.equals(other.components))
            return false;
        if (module == null) {
            if (other.module != null)
                return false;
        } else if (!module.equals(other.module))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "InfoUsageDataSummary [module=" + module + ", components=" + components + "]";
    }

}
