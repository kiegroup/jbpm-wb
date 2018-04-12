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

package org.jbpm.dashboard.dataset.editor;

import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.jbpm.workbench.ks.integration.RemoteDataSetDef;

import com.google.gwt.editor.client.ValueAwareEditor;

/**
 * <p>The GWT editor contract for the specific attributes of type <code>org.jbpm.workbench.ks.integration.RemoteDataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>queryTarget</li>
 *     <li>dataSource</li>
 *     <li>dbSQL</li>
 * </ul>
 */
public interface RemoteDataSetDefAttributesEditor extends ValueAwareEditor<RemoteDataSetDef> {

    LeafAttributeEditor<String> queryTarget();
    LeafAttributeEditor<String> serverTemplateId();
    LeafAttributeEditor<String> dataSource();
    LeafAttributeEditor<String> dbSQL();
   
    
}
