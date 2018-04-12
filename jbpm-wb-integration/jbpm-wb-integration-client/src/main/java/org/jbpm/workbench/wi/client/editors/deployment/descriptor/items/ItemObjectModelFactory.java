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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.items;

import java.util.ArrayList;

import javax.enterprise.context.Dependent;

import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.Resolver;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;

@Dependent
public class ItemObjectModelFactory {

    public ItemObjectModel newItemObjectModel(final String name, final String value) {
        final ItemObjectModel model = new ItemObjectModel();
        model.setName(name);
        model.setValue(value);
        model.setResolver(Resolver.MVEL.name().toLowerCase());
        model.setParameters(new ArrayList<>());
        return model;
    }

    public ItemObjectModel newItemObjectModel(final String name) {
        final ItemObjectModel model = new ItemObjectModel();
        model.setValue(name);
        model.setResolver(Resolver.MVEL.name().toLowerCase());
        model.setParameters(new ArrayList<>());
        return model;
    }
}
