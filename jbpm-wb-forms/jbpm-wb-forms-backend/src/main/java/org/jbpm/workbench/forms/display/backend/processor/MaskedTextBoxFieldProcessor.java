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

package org.jbpm.workbench.forms.display.backend.processor;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedTextBox.definition.AbstractMaskedTextBoxFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backend processor for MaskedTextBox fields
 * Handles server-side value processing including database masking
 */
@ApplicationScoped
public class MaskedTextBoxFieldProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MaskedTextBoxFieldProcessor.class);

    /**
     * Process form values for MaskedTextBox fields
     * @param fieldDefinition The field definition
     * @param formData The form data map
     * @param fieldName The field name
     * @return The processed value
     */
    public Object processFieldValue(FieldDefinition fieldDefinition, Map<String, Object> formData, String fieldName) {
        if (!(fieldDefinition instanceof AbstractMaskedTextBoxFieldDefinition)) {
            return formData.get(fieldName);
        }

        AbstractMaskedTextBoxFieldDefinition maskedField = (AbstractMaskedTextBoxFieldDefinition) fieldDefinition;
        Object rawValue = formData.get(fieldName);
        
        if (rawValue == null) {
            return null;
        }

        String value = rawValue.toString();
        
        // If field is configured to be masked in database, apply masking
        if (Boolean.TRUE.equals(maskedField.getIsMaskedInDB())) {
            String maskedValue = applyServerSideMasking(value, maskedField);
            logger.debug("Applied database masking for field {}: {} -> {}", fieldName, value, maskedValue);
            return maskedValue;
        }
        
        // Otherwise return the original value
        return value;
    }

    /**
     * Apply server-side masking logic
     * @param value The original value
     * @param field The field definition
     * @return The masked value
     */
    private String applyServerSideMasking(String value, AbstractMaskedTextBoxFieldDefinition field) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String maskingCharacter = field.getMaskingCharacter();
        if (maskingCharacter == null || maskingCharacter.isEmpty()) {
            maskingCharacter = "*";
        }

        String maskedValue = value;
        
        // Apply masking from start index
        Integer maskingStartIndex = field.getMaskingStartIndex();
        Integer maskingFromStartLength = field.getMaskingFromStartLength();
        
        if (maskingStartIndex != null && maskingFromStartLength != null) {
            int startIndex = Math.min(maskingStartIndex, value.length());
            int endIndex = Math.min(startIndex + maskingFromStartLength, value.length());
            
            if (startIndex < endIndex) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.substring(0, startIndex));
            for (int i = 0; i < (endIndex - startIndex); i++) {
                sb.append(maskingCharacter);
            }
            sb.append(value.substring(endIndex));
            maskedValue = sb.toString();
            }
        }
        
        // Apply masking from end
        Integer maskingFromEndLength = field.getMaskingFromEndLength();
        if (maskingFromEndLength != null && maskingFromEndLength > 0) {
            int startIndex = Math.max(0, maskedValue.length() - maskingFromEndLength);
            StringBuilder sb = new StringBuilder();
            sb.append(maskedValue.substring(0, startIndex));
            for (int i = 0; i < maskingFromEndLength; i++) {
                sb.append(maskingCharacter);
            }
            maskedValue = sb.toString();
        }
        
        return maskedValue;
    }

    /**
     * Check if a field definition is a MaskedTextBox field
     * @param fieldDefinition The field definition
     * @return true if it's a MaskedTextBox field
     */
    public boolean supports(FieldDefinition fieldDefinition) {
        return fieldDefinition instanceof AbstractMaskedTextBoxFieldDefinition;
    }

    /**
     * Get the original unmasked value for editing
     * This method would typically retrieve the original value from a secure store
     * @param maskedValue The masked value from database
     * @param fieldDefinition The field definition
     * @return The original unmasked value (implementation dependent)
     */
    public String getOriginalValue(String maskedValue, AbstractMaskedTextBoxFieldDefinition fieldDefinition) {
        // In a real implementation, this would retrieve the original value from a secure location
        // For now, we return the masked value as a placeholder
        logger.warn("getOriginalValue called but secure value storage not implemented. Returning masked value.");
        return maskedValue;
    }
}
