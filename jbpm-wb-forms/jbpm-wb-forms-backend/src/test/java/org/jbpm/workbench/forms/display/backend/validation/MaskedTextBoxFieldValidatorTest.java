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

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedTextBox.definition.MaskedTextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;

import static org.junit.Assert.*;

public class MaskedTextBoxFieldValidatorTest {

    private MaskedTextBoxFieldValidator validator;
    private MaskedTextBoxFieldDefinition maskedField;

    @Before
    public void setUp() {
        validator = new MaskedTextBoxFieldValidator();
        maskedField = new MaskedTextBoxFieldDefinition();
    }

    @Test
    public void testSupports() {
        assertTrue("Should support MaskedTextBoxFieldDefinition", 
                  validator.supports(maskedField));
        assertFalse("Should not support other field types", 
                   validator.supports(new TextBoxFieldDefinition()));
    }

    @Test
    public void testValidateRequiredFieldWithValue() {
        maskedField.setRequired(true);
        
        MaskedTextBoxFieldValidator.ValidationResult result = validator.validate(maskedField, "test value", "testField");
        
        assertTrue("Should be valid when required field has value", result.isValid());
        assertTrue("Should have no errors", result.getErrors().isEmpty());
    }

    @Test
    public void testValidateRequiredFieldWithoutValue() {
        maskedField.setRequired(true);
        
        MaskedTextBoxFieldValidator.ValidationResult result = validator.validate(maskedField, null, "testField");
        
        assertFalse("Should be invalid when required field is null", result.isValid());
        assertEquals("Should have one error", 1, result.getErrors().size());
        assertTrue("Error should mention field is required", 
                  result.getErrors().get(0).contains("required"));
    }

    @Test
    public void testValidateRequiredFieldWithEmptyString() {
        maskedField.setRequired(true);
        
        MaskedTextBoxFieldValidator.ValidationResult result = validator.validate(maskedField, "", "testField");
        
        assertFalse("Should be invalid when required field is empty", result.isValid());
        assertEquals("Should have one error", 1, result.getErrors().size());
        assertTrue("Error should mention field is required", 
                  result.getErrors().get(0).contains("required"));
    }

    @Test
    public void testValidateMinLength() {
        maskedField.setMinLength(5);
        
        // Test valid length
        MaskedTextBoxFieldValidator.ValidationResult result1 = validator.validate(maskedField, "12345", "testField");
        assertTrue("Should be valid when value meets min length", result1.isValid());
        
        // Test invalid length
        MaskedTextBoxFieldValidator.ValidationResult result2 = validator.validate(maskedField, "123", "testField");
        assertFalse("Should be invalid when value is too short", result2.isValid());
        assertTrue("Error should mention minimum length", 
                  result2.getErrors().get(0).contains("at least"));
    }

    @Test
    public void testValidateMaxLength() {
        maskedField.setMaxLength(10);
        
        // Test valid length
        MaskedTextBoxFieldValidator.ValidationResult result1 = validator.validate(maskedField, "1234567890", "testField");
        assertTrue("Should be valid when value meets max length", result1.isValid());
        
        // Test invalid length
        MaskedTextBoxFieldValidator.ValidationResult result2 = validator.validate(maskedField, "12345678901", "testField");
        assertFalse("Should be invalid when value is too long", result2.isValid());
        assertTrue("Error should mention maximum length", 
                  result2.getErrors().get(0).contains("exceed"));
    }

    @Test
    public void testValidateMaskingConfiguration() {
        // Test valid configuration
        maskedField.setMaskingStartIndex(2);
        maskedField.setMaskingFromStartLength(3);
        maskedField.setMaskingFromEndLength(2);
        maskedField.setMaskingCharacter("*");
        
        MaskedTextBoxFieldValidator.ValidationResult result1 = validator.validate(maskedField, "test123", "testField");
        assertTrue("Should be valid with proper masking configuration", result1.isValid());
        
        // Test invalid configuration: missing maskingFromStartLength
        maskedField.setMaskingStartIndex(2);
        maskedField.setMaskingFromStartLength(null);
        
        MaskedTextBoxFieldValidator.ValidationResult result2 = validator.validate(maskedField, "test123", "testField");
        assertFalse("Should be invalid when maskingFromStartLength is missing", result2.isValid());
        assertTrue("Error should mention required maskingFromStartLength", 
                  result2.getErrors().get(0).contains("maskingFromStartLength is required"));
    }

    @Test
    public void testValidateNegativeValues() {
        // Test negative maskingStartIndex
        maskedField.setMaskingStartIndex(-1);
        maskedField.setMaskingFromStartLength(2);
        
        MaskedTextBoxFieldValidator.ValidationResult result1 = validator.validate(maskedField, "test", "testField");
        assertFalse("Should be invalid with negative maskingStartIndex", result1.isValid());
        assertTrue("Error should mention negative value", 
                  result1.getErrors().get(0).contains("cannot be negative"));
        
        // Test negative maskingFromStartLength
        maskedField.setMaskingStartIndex(0);
        maskedField.setMaskingFromStartLength(-1);
        
        MaskedTextBoxFieldValidator.ValidationResult result2 = validator.validate(maskedField, "test", "testField");
        assertFalse("Should be invalid with negative maskingFromStartLength", result2.isValid());
        assertTrue("Error should mention negative value", 
                  result2.getErrors().get(0).contains("cannot be negative"));
        
        // Test negative maskingFromEndLength
        maskedField.setMaskingFromStartLength(2);
        maskedField.setMaskingFromEndLength(-1);
        
        MaskedTextBoxFieldValidator.ValidationResult result3 = validator.validate(maskedField, "test", "testField");
        assertFalse("Should be invalid with negative maskingFromEndLength", result3.isValid());
        assertTrue("Error should mention negative value", 
                  result3.getErrors().get(0).contains("cannot be negative"));
    }

    @Test
    public void testValidateMaskingCharacter() {
        // Test valid masking character
        maskedField.setMaskingCharacter("*");
        
        MaskedTextBoxFieldValidator.ValidationResult result1 = validator.validate(maskedField, "test", "testField");
        assertTrue("Should be valid with single character", result1.isValid());
        
        // Test invalid masking character (multiple characters)
        maskedField.setMaskingCharacter("**");
        
        MaskedTextBoxFieldValidator.ValidationResult result2 = validator.validate(maskedField, "test", "testField");
        assertFalse("Should be invalid with multiple characters", result2.isValid());
        assertTrue("Error should mention single character requirement", 
                  result2.getErrors().get(0).contains("exactly one character"));
    }

    @Test
    public void testValidateAgainstMaxLength() {
        maskedField.setMaxLength(10);
        maskedField.setMinLength(15); // Invalid: minLength > maxLength
        
        MaskedTextBoxFieldValidator.ValidationResult result1 = validator.validate(maskedField, "test", "testField");
        assertFalse("Should be invalid when minLength > maxLength", result1.isValid());
        assertTrue("Error should mention minLength vs maxLength", 
                  result1.getErrors().stream().anyMatch(error -> error.contains("cannot be greater than maxLength")));
        
        // Test maskingFromStartLength vs maxLength
        maskedField.setMinLength(5);
        maskedField.setMaskingStartIndex(0);
        maskedField.setMaskingFromStartLength(15); // Invalid: > maxLength
        
        MaskedTextBoxFieldValidator.ValidationResult result2 = validator.validate(maskedField, "test", "testField");
        assertFalse("Should be invalid when maskingFromStartLength > maxLength", result2.isValid());
        assertTrue("Error should mention maskingFromStartLength vs maxLength", 
                  result2.getErrors().stream().anyMatch(error -> error.contains("maskingFromStartLength cannot be greater than maxLength")));
    }

    @Test
    public void testValidateMultipleErrors() {
        maskedField.setRequired(true);
        maskedField.setMinLength(10);
        maskedField.setMaskingStartIndex(-1);
        maskedField.setMaskingCharacter("**");
        
        MaskedTextBoxFieldValidator.ValidationResult result = validator.validate(maskedField, "test", "testField");
        
        assertFalse("Should be invalid with multiple errors", result.isValid());
        assertTrue("Should have multiple errors", result.getErrors().size() > 1);
    }

    @Test
    public void testValidateNonMaskedField() {
        TextBoxFieldDefinition textField = new TextBoxFieldDefinition();
        
        MaskedTextBoxFieldValidator.ValidationResult result = validator.validate(textField, "test", "testField");
        
        assertTrue("Should be valid for non-masked fields", result.isValid());
        assertTrue("Should have no errors", result.getErrors().isEmpty());
    }
}
