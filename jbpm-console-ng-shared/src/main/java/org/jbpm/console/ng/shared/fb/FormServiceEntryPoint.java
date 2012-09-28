/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.console.ng.shared.fb;

import java.util.List;
import java.util.Map;
import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.form.builder.ng.model.client.FormBuilderException;
import org.jbpm.form.builder.ng.model.shared.api.FormItemRepresentation;
import org.jbpm.form.builder.ng.model.shared.api.FormRepresentation;
import org.jbpm.form.builder.ng.model.shared.menu.MenuOptionDescription;
import org.jbpm.form.builder.services.api.FileException;
import org.jbpm.form.builder.services.api.MenuServiceException;

/**
 *
 * @author salaboy
 */
@Remote
public interface FormServiceEntryPoint {

    Map<String, String> getFormBuilderProperties() throws MenuServiceException;

    void listMenuItems() throws MenuServiceException;

    List<MenuOptionDescription> listOptions() throws MenuServiceException;

    public String storeFile(String packageName, String fileName, byte[] content) throws FileException;

    public void deleteFile(String packageName, String fileName) throws FileException;

    public List<String> loadFilesByType(String packageName, String fileType) throws FileException;

    public byte[] loadFile(String packageName, String fileName) throws FileException;

    public String getFormDisplay(long taskId);

    public void completeForm(long id, String userId, Map<String, String> params);

    public String saveForm(FormRepresentation form);
    
    public FormRepresentation loadForm(String json) ;

    public void saveFormItem(FormItemRepresentation formItem, String formItemName) ;
}
