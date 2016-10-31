/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.dm.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DocumentSummary extends CMSContentSummary /**implements Document */ {

    /**
     *
     */
    private static final long serialVersionUID = 1946063131992320204L;

    private byte[] content;

    public DocumentSummary(String name, String id, String path) {
        super(name, id, path);
    }

    public DocumentSummary() {
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.DOCUMENT;
    }

}
