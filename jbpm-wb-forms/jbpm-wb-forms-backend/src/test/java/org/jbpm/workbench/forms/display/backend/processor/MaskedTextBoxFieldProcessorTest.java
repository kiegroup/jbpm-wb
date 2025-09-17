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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedTextBox.definition.MaskedTextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;

import static org.junit.Assert.*;

public class MaskedTextBoxFieldProcessorTest {

    private MaskedTextBoxFieldProcessor processor;
    private MaskedTextBoxFieldDefinition maskedField;
    private Map<String, Object> formData;

    @Before
    public void setUp() {
        processor = new MaskedTextBoxFieldProcessor();
        maskedField = new MaskedTextBoxFieldDefinition();
        formData = new HashMap<>();
    }

    @Test
    public void testSupports() {
        assertTrue("Should support MaskedTextBoxFieldDefinition", 
                  processor.supports(maskedField));
        assertFalse("Should not support other field types", 
                   processor.supports(new TextBoxFieldDefinition()));
    }

    @Test
    public void testProcessFieldValueWithoutMasking() {
        maskedField.setIsMaskedInDB(false);
        formData.put("testField", "originalValue");
        
        Object result = processor.processFieldValue(maskedField, formData, "testField");
        
        assertEquals("Should return original value when not masked in DB", "originalValue", result);
    }

    @Test
    public void testProcessFieldValueWithDatabaseMasking() {
        maskedField.setIsMaskedInDB(true);
        maskedField.setMaskingCharacter("*");
        maskedField.setMaskingStartIndex(2);
        maskedField.setMaskingFromStartLength(4);
        formData.put("testField", "password123");
        
        Object result = processor.processFieldValue(maskedField, formData, "testField");
        
        assertEquals("Should return masked value", "pa****rd123", result);
    }

    @Test
    public void testProcessFieldValueWithEndMasking() {
        maskedField.setIsMaskedInDB(true);
        maskedField.setMaskingCharacter("#");
        maskedField.setMaskingStartIndex(null);
        maskedField.setMaskingFromStartLength(null);
        maskedField.setMaskingFromEndLength(3);
        formData.put("testField", "sensitive");
        
        Object result = processor.processFieldValue(maskedField, formData, "testField");
        
        assertEquals("Should mask from end", "sensit###", result);
    }

    @Test
    public void testProcessFieldValueWithBothMaskingTypes() {
        maskedField.setIsMaskedInDB(true);
        maskedField.setMaskingCharacter("@");
        maskedField.setMaskingStartIndex(1);
        maskedField.setMaskingFromStartLength(2);
        maskedField.setMaskingFromEndLength(2);
        formData.put("testField", "12345678");
        
        Object result = processor.processFieldValue(maskedField, formData, "testField");
        
        // First apply start masking: 1@@45678, then end masking: 1@@456@@
        assertEquals("Should apply both masking types", "1@@456@@", result);
    }

    @Test
    public void testProcessFieldValueWithNullValue() {
        maskedField.setIsMaskedInDB(true);
        formData.put("testField", null);
        
        Object result = processor.processFieldValue(maskedField, formData, "testField");
        
        assertNull("Should handle null values", result);
    }

    @Test
    public void testProcessFieldValueWithEmptyValue() {
        maskedField.setIsMaskedInDB(true);
        formData.put("testField", "");
        
        Object result = processor.processFieldValue(maskedField, formData, "testField");
        
        assertEquals("Should handle empty values", "", result);
    }

    @Test
    public void testProcessFieldValueWithDefaultMaskingCharacter() {
        maskedField.setIsMaskedInDB(true);
        maskedField.setMaskingCharacter(null); // Should default to "*"
        maskedField.setMaskingStartIndex(0);
        maskedField.setMaskingFromStartLength(3);
        formData.put("testField", "test123");
        
        Object result = processor.processFieldValue(maskedField, formData, "testField");
        
        assertEquals("Should use default masking character '*'", "***t123", result);
    }

    @Test
    public void testProcessFieldValueWithBoundaryConditions() {
        maskedField.setIsMaskedInDB(true);
        maskedField.setMaskingCharacter("X");
        maskedField.setMaskingStartIndex(10); // Beyond string length
        maskedField.setMaskingFromStartLength(5);
        formData.put("testField", "short");
        
        Object result = processor.processFieldValue(maskedField, formData, "testField");
        
        assertEquals("Should handle index beyond string length", "short", result);
    }

    @Test
    public void testProcessFieldValueWithZeroLengthMasking() {
        maskedField.setIsMaskedInDB(true);
        maskedField.setMaskingCharacter("*");
        maskedField.setMaskingStartIndex(2);
        maskedField.setMaskingFromStartLength(0);
        formData.put("testField", "test123");
        
        Object result = processor.processFieldValue(maskedField, formData, "testField");
        
        assertEquals("Should handle zero length masking", "test123", result);
    }

    @Test
    public void testGetOriginalValue() {
        String maskedValue = "pa****rd123";
        String result = processor.getOriginalValue(maskedValue, maskedField);
        
        // Current implementation returns the masked value as a placeholder
        assertEquals("Should return masked value (placeholder implementation)", maskedValue, result);
    }
}
