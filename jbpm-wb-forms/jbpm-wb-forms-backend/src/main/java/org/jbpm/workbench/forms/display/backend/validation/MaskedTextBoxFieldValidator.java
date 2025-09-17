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

package org.jbpm.workbench.forms.display.backend.validation;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedTextBox.definition.AbstractMaskedTextBoxFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server-side validator for MaskedTextBox fields
 * Validates field values according to the field configuration
 */
@ApplicationScoped
public class MaskedTextBoxFieldValidator {

    private static final Logger logger = LoggerFactory.getLogger(MaskedTextBoxFieldValidator.class);

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;

        public ValidationResult(boolean valid) {
            this.valid = valid;
            this.errors = new ArrayList<>();
        }

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? errors : new ArrayList<>();
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void addError(String error) {
            this.errors.add(error);
            this.valid = false;
        }
    }

    /**
     * Validate a field value against the field definition
     * @param fieldDefinition The field definition
     * @param value The value to validate
     * @param fieldName The field name for error messages
     * @return ValidationResult containing validation status and errors
     */
    public ValidationResult validate(FieldDefinition fieldDefinition, Object value, String fieldName) {
        if (!(fieldDefinition instanceof AbstractMaskedTextBoxFieldDefinition)) {
            return new ValidationResult(true);
        }

        AbstractMaskedTextBoxFieldDefinition maskedField = (AbstractMaskedTextBoxFieldDefinition) fieldDefinition;
        ValidationResult result = new ValidationResult(true);
        
        // Handle null/empty values
        if (value == null) {
            if (Boolean.TRUE.equals(fieldDefinition.getRequired())) {
                result.addError("Field '" + fieldName + "' is required");
            }
            return result;
        }

        String stringValue = value.toString();
        
        // Check if empty string is allowed
        if (stringValue.isEmpty()) {
            if (Boolean.TRUE.equals(fieldDefinition.getRequired())) {
                result.addError("Field '" + fieldName + "' is required");
            }
            return result;
        }

        // Validate minimum length
        Integer minLength = maskedField.getMinLength();
        if (minLength != null && stringValue.length() < minLength) {
            result.addError("Field '" + fieldName + "' must be at least " + minLength + " characters long");
        }

        // Validate maximum length if field supports it
        if (maskedField instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength) {
            Integer maxLength = ((org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength) maskedField).getMaxLength();
            if (maxLength != null && stringValue.length() > maxLength) {
                result.addError("Field '" + fieldName + "' cannot exceed " + maxLength + " characters");
            }
        }

        // Validate masking configuration consistency
        validateMaskingConfiguration(maskedField, fieldName, result);

        if (!result.isValid()) {
            logger.debug("Validation failed for field {}: {}", fieldName, result.getErrors());
        }

        return result;
    }

    /**
     * Validate the masking configuration of the field
     * @param field The field definition
     * @param fieldName The field name
     * @param result The validation result to update
     */
    private void validateMaskingConfiguration(AbstractMaskedTextBoxFieldDefinition field, String fieldName, ValidationResult result) {
        // Validate that if maskingStartIndex is provided, then maskingFromStartLength is also required
        Integer maskingStartIndex = field.getMaskingStartIndex();
        Integer maskingFromStartLength = field.getMaskingFromStartLength();
        
        if (maskingStartIndex != null && maskingFromStartLength == null) {
            result.addError("Field '" + fieldName + "' configuration error: maskingFromStartLength is required when maskingStartIndex is specified");
        }

        // Validate that maskingStartIndex is not negative
        if (maskingStartIndex != null && maskingStartIndex < 0) {
            result.addError("Field '" + fieldName + "' configuration error: maskingStartIndex cannot be negative");
        }

        // Validate that maskingFromStartLength is not negative
        if (maskingFromStartLength != null && maskingFromStartLength < 0) {
            result.addError("Field '" + fieldName + "' configuration error: maskingFromStartLength cannot be negative");
        }

        // Validate that maskingFromEndLength is not negative
        Integer maskingFromEndLength = field.getMaskingFromEndLength();
        if (maskingFromEndLength != null && maskingFromEndLength < 0) {
            result.addError("Field '" + fieldName + "' configuration error: maskingFromEndLength cannot be negative");
        }

        // Validate masking character
        String maskingCharacter = field.getMaskingCharacter();
        if (maskingCharacter != null && maskingCharacter.length() != 1) {
            result.addError("Field '" + fieldName + "' configuration error: maskingCharacter must be exactly one character");
        }

        // Validate against maxLength if applicable
        if (field instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength) {
            Integer maxLength = ((org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength) field).getMaxLength();
            
            if (maxLength != null) {
                // Validate that maskingFromStartLength is not greater than maxLength
                if (maskingFromStartLength != null && maskingFromStartLength > maxLength) {
                    result.addError("Field '" + fieldName + "' configuration error: maskingFromStartLength cannot be greater than maxLength");
                }
                
                // Validate that minLength is not greater than maxLength
                Integer minLength = field.getMinLength();
                if (minLength != null && minLength > maxLength) {
                    result.addError("Field '" + fieldName + "' configuration error: minLength cannot be greater than maxLength");
                }
            }
        }
    }

    /**
     * Check if this validator supports the given field type
     * @param fieldDefinition The field definition
     * @return true if this validator supports the field type
     */
    public boolean supports(FieldDefinition fieldDefinition) {
        return fieldDefinition instanceof AbstractMaskedTextBoxFieldDefinition;
    }
}
