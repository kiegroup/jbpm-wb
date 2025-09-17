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

/**
 * MaskedTextBox field handler
 * Provides client-side masking functionality for MaskedTextBox fields
 */
(function() {
    'use strict';

    // Initialize masked input fields when the page loads
    document.addEventListener('DOMContentLoaded', function() {
        try {
            renamePaletteItems();
        } catch (e) {}
        initializeMaskedInputFields();
        
        // Initialize MaskedTextBox dropdowns
        setTimeout(function() {
            try {
                forceAddMaskedTextBoxToDropdowns();
            } catch (e) {}
        }, 2000);
    });

    // Also initialize when new content is added dynamically
    if (typeof MutationObserver !== 'undefined') {
        var observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                if (mutation.type === 'childList') {
                    mutation.addedNodes.forEach(function(node) {
                        if (node.nodeType === 1) { // Element node
                            try { renamePaletteItems(node); } catch (e) {}
                            initializeMaskedInputFields(node);
                            
                            // Special handling for modal dialogs (field properties)
                            if (node.classList && (node.classList.contains('modal') || node.querySelector('.modal'))) {
                                setTimeout(function() {
                                    try {
                                        handleFieldTypeDropdowns(node);
                                        forceAddMaskedTextBoxToDropdowns();
                                    } catch (e) {}
                                }, 100);
                            }
                        }
                    });
                }
            });
        });
        
        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    }

    function renamePaletteItems(container) {
        container = container || document;
        var targets = [
            { find: 'MaskedInputText', replace: 'MaskedTextBox' },
            { find: 'maskedinputtext', replace: 'MaskedTextBox' },
            { find: 'MASKEDINPUTTEXT', replace: 'MaskedTextBox' }
        ];
        
        // Check all text nodes, not just specific elements
        var nodes = container.querySelectorAll('*');
        for (var i = 0; i < nodes.length; i++) {
            var el = nodes[i];
            if (el.getAttribute && el.getAttribute('data-renamed') === 'true') continue;
            
            var txt = (el.textContent || '').trim();
            for (var t = 0; t < targets.length; t++) {
                if (txt === targets[t].find) {
                    el.textContent = targets[t].replace;
                    el.setAttribute('data-renamed', 'true');
                    break;
                }
            }
            
            // Also check option elements in select dropdowns
            if (el.tagName === 'OPTION') {
                for (var t = 0; t < targets.length; t++) {
                    if (txt === targets[t].find) {
                        el.textContent = targets[t].replace;
                        break;
                    }
                }
            }
        }
        
        // Special handling for field type dropdowns
        handleFieldTypeDropdowns(container);
        
        // Run again after a delay to catch dynamically loaded content
        setTimeout(function() {
            renamePaletteItemsDelayed(container);
        }, 500);
    }
    
    function renamePaletteItemsDelayed(container) {
        container = container || document;
        var targets = [
            { find: 'MaskedInputText', replace: 'MaskedTextBox' },
            { find: 'maskedinputtext', replace: 'MaskedTextBox' },
            { find: 'MASKEDINPUTTEXT', replace: 'MaskedTextBox' }
        ];
        
        var nodes = container.querySelectorAll('*');
        for (var i = 0; i < nodes.length; i++) {
            var el = nodes[i];
            if (el.getAttribute && el.getAttribute('data-renamed') === 'true') continue;
            
            var txt = (el.textContent || '').trim();
            for (var t = 0; t < targets.length; t++) {
                if (txt === targets[t].find) {
                    el.textContent = targets[t].replace;
                    el.setAttribute('data-renamed', 'true');
                    break;
                }
            }
        }
    }

    function handleFieldTypeDropdowns(container) {
        container = container || document;
        
        // Find field type dropdowns (commonly have id="fieldType" or similar)
        var fieldTypeSelects = container.querySelectorAll('select[id*="fieldType"], select[id*="FieldType"], .field-type-select');
        
        fieldTypeSelects.forEach(function(select) {
            // Check if this dropdown has MaskedTextBox option
            var options = select.querySelectorAll('option');
            var hasMaskedTextBox = false;
            var maskedTextBoxOption = null;
            
            for (var i = 0; i < options.length; i++) {
                var option = options[i];
                var value = option.value || '';
                var text = (option.textContent || '').trim();
                
                if (value === 'MaskedTextBox' || text === 'MaskedTextBox') {
                    hasMaskedTextBox = true;
                    maskedTextBoxOption = option;
                    break;
                }
            }
            
            // If the select currently has MaskedTextBox selected but it's not showing as selected,
            // try to fix the selection
            if (hasMaskedTextBox && maskedTextBoxOption) {
                var currentValue = select.value;
                if (currentValue === 'MaskedTextBox' && select.selectedIndex !== maskedTextBoxOption.index) {
                    select.selectedIndex = maskedTextBoxOption.index;
                }
            }
        });
    }

    function initializeMaskedInputFields(container) {
        container = container || document;
        
        // Find all input fields that should be masked
        var inputs = container.querySelectorAll('input[data-field-type="MaskedTextBox"], input.masked-input-text-field');
        
        inputs.forEach(function(input) {
            if (!input.hasAttribute('data-masked-initialized')) {
                setupMaskedInput(input);
                input.setAttribute('data-masked-initialized', 'true');
            }
        });
    }

    function setupMaskedInput(input) {
        var originalValue = input.value || '';
        var isMasked = false;
        
        // Get masking configuration from data attributes
        var maskingCharacter = input.getAttribute('data-masking-character') || '*';
        var maskingStartIndexAttr = input.getAttribute('data-masking-start-index');
        var maskingStartIndex = maskingStartIndexAttr !== null ? parseInt(maskingStartIndexAttr) : null;
        var maskingFromStartLengthAttr = input.getAttribute('data-masking-from-start-length');
        var maskingFromStartLength = maskingFromStartLengthAttr !== null ? parseInt(maskingFromStartLengthAttr) : null;
        var maskingFromEndLengthAttr = input.getAttribute('data-masking-from-end-length');
        var maskingFromEndLength = maskingFromEndLengthAttr !== null ? parseInt(maskingFromEndLengthAttr) : null;
        var isMaskedInDB = input.getAttribute('data-is-masked-in-db') === 'true';
        
        // Apply initial masking if configured
        if (originalValue && (isMaskedInDB || input.readOnly)) {
            input.value = applyMasking(originalValue, maskingCharacter, maskingStartIndex, maskingFromStartLength, maskingFromEndLength);
            isMasked = true;
            input.classList.add('masked');
        }
        
        // Store original value
        input.setAttribute('data-original-value', originalValue);
        
        // Add event listeners
        input.addEventListener('focus', function() {
            if (!input.readOnly) {
                showOriginalValue();
            }
        });
        
        input.addEventListener('blur', function() {
            if (!input.readOnly) {
                applyMaskingToInput();
            }
        });
        
        input.addEventListener('input', function() {
            if (!input.readOnly) {
                // Update stored original value
                input.setAttribute('data-original-value', input.value);
            }
        });
        
        function showOriginalValue() {
            var original = input.getAttribute('data-original-value');
            if (original) {
                input.value = original;
                isMasked = false;
                input.classList.remove('masked');
                input.classList.add('unmasked');
            }
        }
        
        function applyMaskingToInput() {
            var original = input.getAttribute('data-original-value');
            if (original) {
                input.value = applyMasking(original, maskingCharacter, maskingStartIndex, maskingFromStartLength, maskingFromEndLength);
                isMasked = true;
                input.classList.add('masked');
                input.classList.remove('unmasked');
            }
        }
    }

    function applyMasking(value, maskingCharacter, maskingStartIndex, maskingFromStartLength, maskingFromEndLength) {
        if (!value) {
            return value;
        }
        
        var maskedValue = value;
        
        // Apply masking from start index
        if (maskingStartIndex !== null && maskingStartIndex !== undefined && maskingFromStartLength !== null && maskingFromStartLength !== undefined) {
            var startIndex = Math.min(maskingStartIndex, value.length);
            var endIndex = Math.min(startIndex + maskingFromStartLength, value.length);
            
            maskedValue = value.substring(0, startIndex) + 
                         maskingCharacter.repeat(endIndex - startIndex) + 
                         value.substring(endIndex);
        }
        
        // Apply masking from end
        if (maskingFromEndLength !== null && maskingFromEndLength !== undefined && maskingFromEndLength > 0) {
            var startIndex = Math.max(0, maskedValue.length - maskingFromEndLength);
            maskedValue = maskedValue.substring(0, startIndex) + 
                         maskingCharacter.repeat(maskedValue.length - startIndex);
        }
        
        return maskedValue;
    }


    // Expose functions globally for manual initialization
    window.initializeMaskedInputFields = initializeMaskedInputFields;
    window.handleFieldTypeDropdowns = handleFieldTypeDropdowns;
    
    // Add a global function to force fix dropdown selection
    // Function to force-add MaskedTextBox to field type dropdowns if missing
    function forceAddMaskedTextBoxToDropdowns() {
        var dropdowns = document.querySelectorAll('select[id*="fieldType"], select[id*="FieldType"], .field-type-select');
        
        dropdowns.forEach(function(dropdown) {
            var options = dropdown.querySelectorAll('option');
            var hasMaskedTextBox = false;
            var hasTextBox = false;
            var textBoxOption = null;
            
            // Check what options are available
            for (var i = 0; i < options.length; i++) {
                var option = options[i];
                var value = option.value || '';
                var text = (option.textContent || '').trim();
                
                if (value === 'MaskedTextBox' || text === 'MaskedTextBox') {
                    hasMaskedTextBox = true;
                } else if (value === 'TextBox' || text === 'TextBox') {
                    hasTextBox = true;
                    textBoxOption = option;
                }
            }
            
            // If MaskedTextBox is missing but TextBox exists, add MaskedTextBox
            if (!hasMaskedTextBox && hasTextBox && textBoxOption) {
                var maskedOption = document.createElement('option');
                maskedOption.value = 'MaskedTextBox';
                maskedOption.textContent = 'MaskedTextBox';
                
                // Insert after TextBox option
                textBoxOption.parentNode.insertBefore(maskedOption, textBoxOption.nextSibling);
            }
        });
    }

    window.fixMaskedTextBoxDropdown = function() {
        handleFieldTypeDropdowns();
        forceAddMaskedTextBoxToDropdowns();
    };

})();
