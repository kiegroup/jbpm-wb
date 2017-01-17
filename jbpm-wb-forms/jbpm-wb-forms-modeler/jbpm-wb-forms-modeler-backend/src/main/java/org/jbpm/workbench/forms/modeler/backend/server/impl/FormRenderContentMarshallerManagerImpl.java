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

package org.jbpm.workbench.forms.modeler.backend.server.impl;

import org.jbpm.formModeler.kie.services.FormRenderContentMarshallerManager;
import org.kie.internal.task.api.ContentMarshallerContext;

import javax.enterprise.context.SessionScoped;
import java.util.concurrent.ConcurrentHashMap;

@SessionScoped
public class FormRenderContentMarshallerManagerImpl implements FormRenderContentMarshallerManager {
    private ConcurrentHashMap<String, ContentMarshallerContext> marhsalContexts = new ConcurrentHashMap<String, ContentMarshallerContext>();

    @Override
    public void addContentMarshaller(String id, ContentMarshallerContext context) {
        marhsalContexts.put(id, context);
    }

    @Override
    public void removeContentMarshaller(String id) {
        marhsalContexts.remove(id);
    }

    @Override
    public ContentMarshallerContext getContentMarshaller(String id) {
        return marhsalContexts.get(id);
    }
}
