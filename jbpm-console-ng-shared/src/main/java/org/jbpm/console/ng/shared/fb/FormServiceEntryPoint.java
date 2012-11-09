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

/**
 *
 * @author salaboy
 */
@Remote
public interface FormServiceEntryPoint {

    Map<String, String> getFormBuilderProperties();

    void listMenuItems();

    List<Map<String, Object>> listOptions();

    public String storeFile(String packageName, String fileName, byte[] content);

    public void deleteFile(String packageName, String fileName);

    public List<String> loadFilesByType(String packageName, String fileType);

    public byte[] loadFile(String packageName, String fileName);

    public String getFormDisplay(long taskId);

    public String saveForm(Map<String, Object> form);
    
    public Map<String, Object> loadForm(String json) ;

    public void saveFormItem(Map<String, Object> formItem, String formItemName) ;
}
