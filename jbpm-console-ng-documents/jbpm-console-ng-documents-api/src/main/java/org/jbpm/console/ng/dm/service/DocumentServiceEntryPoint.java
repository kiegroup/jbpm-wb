/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.dm.service;

import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.dm.model.CMSContentSummary;
import org.jbpm.console.ng.dm.model.DocumentSummary;

@Remote
public interface DocumentServiceEntryPoint {

	List<CMSContentSummary> getDocuments(String path);
	

	CMSContentSummary getDocument(String id);

	void removeDocument(String id);

	void addDocument(DocumentSummary doc);
	
	Map<String,String> getConfigurationParameters();
	
	Long setConfigurationParameters(Map<String,String> parameters);
	
	Boolean testConnection();

}