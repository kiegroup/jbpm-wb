/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.model;

import java.util.Date;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
public class DocumentSummary extends GenericSummary {

  private String documentId;
  private Date documentLastModified;
  private Long documentSize;
  private String documentLink;

  public DocumentSummary() {
  }

  public DocumentSummary(String documentId, Date documentLastModified, Long documentSize, String documentLink) {
    this.id = documentId;
    this.name = documentId;
    this.documentId = documentId;
    this.documentLastModified = documentLastModified;
    this.documentSize = documentSize;
    this.documentLink = documentLink;
  }

  public String getDocumentId() {
    return documentId;
  }

  public Date getDocumentLastModified() {
    return documentLastModified;
  }

  public Long getDocumentSize() {
    return documentSize;
  }

  public String getDocumentLink() {
    return documentLink;
  }

}
