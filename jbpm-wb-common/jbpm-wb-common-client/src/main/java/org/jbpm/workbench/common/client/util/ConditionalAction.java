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

package org.jbpm.workbench.common.client.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jbpm.workbench.common.model.GenericSummary;

public class ConditionalAction<T extends GenericSummary> {

    private Predicate<T> predicate;
    private String text;
    private Consumer<T> callback;
    private Boolean isNavigation;

    public ConditionalAction(final String text,
                             final Consumer<T> callback,
                             final Predicate<T> predicate,
                             final Boolean isNavigation) {
        this.predicate = predicate;
        this.text = text;
        this.callback = callback;
        this.isNavigation = isNavigation;
    }

    public Consumer<T> getCallback() {
        return callback;
    }

    public Predicate<T> getPredicate() {
        return predicate;
    }

    public String getText() {
        return text;
    }

    public Boolean isNavigation() {
        return isNavigation;
    }
}
