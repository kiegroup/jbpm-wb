/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.forms.client.modeler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.forms.editor.client.editor.EditorFieldTypesProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedTextBox.type.MaskedTextBoxFieldType;
import org.kie.workbench.common.forms.model.FieldType;

/**
 * Field type provider for MaskedTextBox in the jBPM form editor
 * Adds MaskedTextBox to the form designer palette
 */
@ApplicationScoped
public class MaskedTextBoxEditorFieldTypeProviderImpl implements EditorFieldTypesProvider {

    private List<FieldType> paletteFieldTypes = new ArrayList<>();
    private List<FieldType> fieldPropertiesFieldTypes = new ArrayList<>();

    @PostConstruct
    public void init() {
        MaskedTextBoxFieldType maskedTextBoxType = new MaskedTextBoxFieldType();
        fieldPropertiesFieldTypes.add(maskedTextBoxType);
    }

    @Override
    public int getPriority() {
        return 1; 
    }

    @Override
    public Collection<FieldType> getPaletteFieldTypes() {
        return paletteFieldTypes;
    }

    @Override
    public Collection<FieldType> getFieldPropertiesFieldTypes() {
        return fieldPropertiesFieldTypes;
    }
}
