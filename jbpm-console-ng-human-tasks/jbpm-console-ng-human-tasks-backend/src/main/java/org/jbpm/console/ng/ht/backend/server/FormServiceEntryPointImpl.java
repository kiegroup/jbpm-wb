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

package org.jbpm.console.ng.ht.backend.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.kie.services.api.FormProviderService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.console.ng.ht.model.fb.events.PaletteItemAddedEvent;
import org.jbpm.console.ng.ht.service.FormServiceEntryPoint;
import org.jbpm.form.builder.services.api.FormBuilderService;
import org.jbpm.form.builder.services.api.FormBuilderServiceException;
import org.jbpm.form.builder.services.api.MenuService;
import org.jbpm.form.builder.services.api.MenuServiceException;
import org.jbpm.form.builder.services.encoders.FormRepresentationDecoderImpl;
import org.jbpm.form.builder.services.encoders.FormRepresentationEncoderImpl;
import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.FormRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;
import org.jbpm.form.builder.services.model.forms.FormEncodingFactory;
import org.jbpm.form.builder.services.model.menu.MenuItemDescription;
import org.jbpm.form.builder.services.model.menu.MenuOptionDescription;
import org.jbpm.shared.services.api.FileService;

@Service
@ApplicationScoped
@Transactional
public class FormServiceEntryPointImpl implements FormServiceEntryPoint {

    @Inject
    private MenuService menuService;

    @Inject
    private FileService fileService;

    @Inject
    private FormProviderService displayService;

    @Inject
    private FormBuilderService formService;

    @Inject
    Event<PaletteItemAddedEvent> itemAddedEvents;

    @PostConstruct
    public void init() {
        FormEncodingFactory.register(new FormRepresentationEncoderImpl(), new FormRepresentationDecoderImpl());

    }

    public List<Map<String, Object>> listOptions() {
        try {
            List<MenuOptionDescription> options = menuService.listOptions();
            List<Map<String, Object>> retval = new ArrayList<Map<String, Object>>();
            for (MenuOptionDescription option : options) {
                retval.add(option.getDataMap());
            }
            return retval;
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(FormServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void listMenuItems() {
        try {
            Map<String, List<MenuItemDescription>> listMenuItems = menuService.listMenuItems();
            for (String groupName : listMenuItems.keySet()) {
                for (MenuItemDescription itemDesc : listMenuItems.get(groupName)) {
                    Map<String, Object> itemDescMap = itemDesc.getDataMap();
                    itemAddedEvents.fire(new PaletteItemAddedEvent(itemDescMap, groupName));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(FormServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, String> getFormBuilderProperties() {
        try {
            return menuService.getFormBuilderProperties();
        } catch (MenuServiceException ex) {
            ex.printStackTrace();
            Logger.getLogger(FormServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getFormDisplayTask(long taskId) {
        return displayService.getFormDisplayTask(taskId);
    }

    public String saveForm(Map<String, Object> form) {
        try {
            Object obj = FormEncodingFactory.getDecoder().decode(form);
            if (obj != null && obj instanceof FormRepresentation) {
                return formService.saveForm((FormRepresentation) form);
            }
        } catch (FormBuilderServiceException ex) {
            Logger.getLogger(FormServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FormEncodingException ex) {
            Logger.getLogger(FormServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void saveFormItem(Map<String, Object> formItem, String formItemName) {
        try {
            Object obj = FormEncodingFactory.getDecoder().decode(formItem);
            if (obj != null && obj instanceof FormItemRepresentation) {
                formService.saveFormItem((FormItemRepresentation) obj, formItemName);
            }
        } catch (FormBuilderServiceException ex) {
            Logger.getLogger(FormServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FormEncodingException ex) {
            Logger.getLogger(FormServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, Object> loadForm(String json) {
        FormRepresentation form = formService.loadForm(json);
        Map<String, Object> formMap = form == null ? null : form.getDataMap();
        return formMap;
    }

    @Override
    public String getFormDisplayProcess(String domainId, String processId) {
        return displayService.getFormDisplayProcess(domainId, processId);
    }

}
