/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.df.client.events;

import org.jbpm.workbench.df.client.filter.SavedFilter;

public class SavedFilterAddedEvent {

    private SavedFilter filter;

    public SavedFilterAddedEvent(SavedFilter filter) {
        this.filter = filter;
    }

    public SavedFilter getFilter() {
        return filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SavedFilterAddedEvent)) {
            return false;
        }

        SavedFilterAddedEvent that = (SavedFilterAddedEvent) o;

        return getFilter().equals(that.getFilter());
    }

    @Override
    public int hashCode() {
        return getFilter().hashCode();
    }

    @Override
    public String toString() {
        return "SavedFilterAddedEvent{" +
                "filter=" + filter +
                '}';
    }
}
