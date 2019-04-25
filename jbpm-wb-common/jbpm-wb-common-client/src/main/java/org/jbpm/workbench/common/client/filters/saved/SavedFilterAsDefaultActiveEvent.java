/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.common.client.filters.saved;

import org.jbpm.workbench.df.client.filter.SavedFilter;

public class SavedFilterAsDefaultActiveEvent {

    private SavedFilter savedFilter;

    public SavedFilterAsDefaultActiveEvent(final SavedFilter savedFilter) {
        this.savedFilter = savedFilter;
    }

    public SavedFilter getSavedFilter() {
        return savedFilter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SavedFilterAsDefaultActiveEvent)) {
            return false;
        }

        SavedFilterAsDefaultActiveEvent that = (SavedFilterAsDefaultActiveEvent) o;

        return getSavedFilter().equals(that.getSavedFilter());
    }

    @Override
    public int hashCode() {
        return getSavedFilter().hashCode();
    }

    @Override
    public String toString() {
        return "SaveDefaultActiveFilterEvent{" +
                "savedFilter=" + savedFilter +
                '}';
    }
}
