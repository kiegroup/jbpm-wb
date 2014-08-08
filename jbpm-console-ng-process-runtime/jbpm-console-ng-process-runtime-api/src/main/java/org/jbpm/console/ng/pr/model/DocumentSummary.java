/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.pr.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;

/**
 *
 * @author salaboy
 */
@Portable
public class DocumentSummary extends GenericSummary {
  private String documentId;
  private String documentName;
  private String documentPath;
  private String documentLink;

  public DocumentSummary() {
  }

  
  public DocumentSummary(String documentId, String documentName, String documentPath, String documentLink) {
    this.id = documentId;
    this.name = documentName;
    this.documentId = documentId;
    this.documentName = documentName;
    this.documentPath = documentPath;
    this.documentLink = documentLink;
  }

  public String getDocumentId() {
    return documentId;
  }
  
  public String getDocumentName() {
    return documentName;
  }

  public String getDocumentPath() {
    return documentPath;
  }

  public String getDocumentLink() {
    return documentLink;
  }

}
