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

package org.jbpm.workbench.common.client.filters.active;

public class ActiveFilterItemAddedEvent {

    private ActiveFilterItem activeFilterItem;

    public ActiveFilterItemAddedEvent(final ActiveFilterItem activeFilterItem) {
        this.activeFilterItem = activeFilterItem;
    }

    public ActiveFilterItem getActiveFilterItem() {
        return activeFilterItem;
    }

    @Override
    public String toString() {
        return "ActiveFilterItemAddedEvent{" +
                "activeFilterItem=" + activeFilterItem +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActiveFilterItemAddedEvent)) {
            return false;
        }

        ActiveFilterItemAddedEvent that = (ActiveFilterItemAddedEvent) o;

        return getActiveFilterItem().equals(that.getActiveFilterItem());
    }

    @Override
    public int hashCode() {
        return getActiveFilterItem().hashCode();
    }
}