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

package org.jbpm.workbench.forms.client.rendering;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.HTML;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ValueConvertersFactory;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedTextBox.definition.AbstractMaskedTextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedTextBox.type.MaskedTextBoxFieldType;

/**
 * jBPM-specific renderer for MaskedTextBox field
 * Extends the base functionality with jBPM-specific features
 */
@Dependent
@Renderer(type = MaskedTextBoxFieldType.class)
public class MaskedTextBoxFieldRenderer extends FieldRenderer<AbstractMaskedTextBoxFieldDefinition, DefaultFormGroup> implements RequiresValueConverter {

    @Inject
    protected TextBox textBox;

    @Override
    public String getName() {
        return "MaskedTextBox";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            HTML html = new HTML();
            formGroup.render(html, field);
        } else {
            String inputId = generateUniqueId();
            textBox.setName(fieldNS);
            textBox.setId(inputId);
            textBox.setPlaceholder(field.getPlaceHolder());
            
            // Set maxLength if the field supports it
            if (field instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength) {
                textBox.setMaxLength(((org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength) field).getMaxLength());
            }
            textBox.setEnabled(!field.getReadOnly());
            
            // Add jBPM-specific masking functionality
            addJBPMMaskingBehavior();
            
            registerFieldRendererPart(textBox);
            formGroup.render(inputId, textBox, field);
        }

        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        textBox.setEnabled(!readOnly);
    }

    @Override
    public Converter<?, ?> getConverter() {
        return ValueConvertersFactory.getConverterForType(field.getStandaloneClassName());
    }

    /**
     * Add jBPM-specific masking behavior with enhanced features
     */
    private void addJBPMMaskingBehavior() {
        // Attach data attributes consumed by masked-input.js and add identifying class
        textBox.addStyleName("masked-input-text-field");
        textBox.addStyleName("jbpm-masked-field"); // jBPM-specific class
        textBox.getElement().setAttribute("data-field-type", "MaskedTextBox");
        textBox.getElement().setAttribute("data-jbpm-field", "true");

        if (field.getMaskingCharacter() != null && !field.getMaskingCharacter().isEmpty()) {
            textBox.getElement().setAttribute("data-masking-character", field.getMaskingCharacter());
        }
        if (field.getMaskingStartIndex() != null) {
            textBox.getElement().setAttribute("data-masking-start-index", String.valueOf(field.getMaskingStartIndex()));
        }
        if (field.getMaskingFromStartLength() != null) {
            textBox.getElement().setAttribute("data-masking-from-start-length", String.valueOf(field.getMaskingFromStartLength()));
        }
        if (field.getMaskingFromEndLength() != null) {
            textBox.getElement().setAttribute("data-masking-from-end-length", String.valueOf(field.getMaskingFromEndLength()));
        }
        if (field.getIsMaskedInDB() != null) {
            textBox.getElement().setAttribute("data-is-masked-in-db", String.valueOf(field.getIsMaskedInDB()));
        }
        
        // Add minimum length validation
        if (field.getMinLength() != null) {
            textBox.getElement().setAttribute("data-min-length", String.valueOf(field.getMinLength()));
        }
    }
}
