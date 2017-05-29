/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.common.client.list;

import java.util.function.Consumer;

import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
public class ActiveFilterItem<T extends Object> {

    private String labelKey;

    private String labelValue;

    private T value;

    private Consumer<T> callback;

    public ActiveFilterItem() {
    }

    public ActiveFilterItem(final String labelKey,
                            final String labelValue,
                            final T value,
                            final Consumer<T> callback) {
        this.labelKey = labelKey;
        this.labelValue = labelValue;
        this.value = value;
        this.callback = callback;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getLabelValue() {
        return labelValue;
    }

    public void setLabelValue(String labelValue) {
        this.labelValue = labelValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Consumer<T> getCallback() {
        return callback;
    }

    public void setCallback(Consumer<T> callback) {
        this.callback = callback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActiveFilterItem)) {
            return false;
        }

        ActiveFilterItem that = (ActiveFilterItem) o;

        if (!getLabelKey().equals(that.getLabelKey())) {
            return false;
        }
        if (!getLabelValue().equals(that.getLabelValue())) {
            return false;
        }
        return getValue().equals(that.getValue());
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        int result = getLabelKey().hashCode();
        result = ~~result;
        result = 31 * result + getLabelValue().hashCode();
        result = ~~result;
        result = 31 * result + getValue().hashCode();
        result = ~~result;
        return result;
    }
}