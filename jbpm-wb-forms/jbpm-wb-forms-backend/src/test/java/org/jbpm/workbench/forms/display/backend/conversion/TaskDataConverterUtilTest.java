/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.forms.display.backend.conversion;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TaskDataConverterUtilTest {

    private static final String STRING = "string";
    private static final String OBJECT = "object";
    private static final String DOUBLE = "double";
    private static final String FLOAT = "float";
    private static final String INTEGER = "integer";
    private static final String BOOLEAN = "boolean";
    private static final String DATE = "date";
    private static final String LIST = "list";

    @Test
    public void testSupportedConstantValues() {
        testSupportedConstantValues(false);
    }

    @Test
    public void testSupportedConstantValuesWithTypeAlias() {
        testSupportedConstantValues(true);
    }

    @Test
    public void testSupportedconstantValuesWithParsingErrors() {
        Map<String, String> definitions = new HashMap<>();
        definitions.put(DOUBLE, Double.class.getName());
        definitions.put(FLOAT, Float.class.getName());
        definitions.put(INTEGER, Integer.class.getName());
        definitions.put(BOOLEAN, Boolean.class.getName());

        Map<String, Object> values = new HashMap<>();
        values.put(DOUBLE, "a wrong double constant");
        values.put(FLOAT, "a wrong float constant");
        values.put(INTEGER, "a wrong integer constant");
        values.put(BOOLEAN, "a wrong boolean constant");

        TaskDataConverterUtil.convert(definitions, values);

        Assertions.assertThat(values)
                .containsEntry(DOUBLE, null)
                .containsEntry(FLOAT, null)
                .containsEntry(INTEGER, null)
                .containsEntry(BOOLEAN, false);
    }

    @Test
    public void testWithoutConstantValues() {
        testWithoutConstantValues(false);
    }

    @Test
    public void testWithoutConstantValuesWithTypeAlias() {
        testWithoutConstantValues(true);
    }

    @Test
    public void testUnsupportedConstants() {
        Map<String, String> definitions = new HashMap<>();
        definitions.put(DATE, Date.class.getName());
        definitions.put(LIST, List.class.getName());

        Map<String, Object> values = new HashMap<>();
        values.put(DATE, "a date constant");
        values.put(LIST, "a list constant");

        TaskDataConverterUtil.convert(definitions, values);

        Assertions.assertThat(values)
                .containsEntry(DATE, null)
                .containsEntry(LIST, null);
    }

    private void testWithoutConstantValues(boolean useTypeAlias) {
        Map<String, String> definitions = new HashMap<>();
        definitions.put(STRING, resolveType(String.class, useTypeAlias));
        definitions.put(OBJECT, resolveType(Object.class, useTypeAlias));
        definitions.put(DOUBLE, resolveType(Double.class, useTypeAlias));
        definitions.put(FLOAT, resolveType(Float.class, useTypeAlias));
        definitions.put(INTEGER, resolveType(Integer.class, useTypeAlias));
        definitions.put(BOOLEAN, resolveType(Boolean.class, useTypeAlias));

        Map<String, Object> values = new HashMap<>();
        values.put(STRING, STRING);
        values.put(OBJECT, OBJECT);
        values.put(DOUBLE, 1.5);
        values.put(FLOAT, 1.5f);
        values.put(INTEGER, 1000);
        values.put(BOOLEAN, true);

        TaskDataConverterUtil.convert(definitions, values);

        Assertions.assertThat(values)
                .containsEntry(STRING, STRING)
                .containsEntry(OBJECT, OBJECT)
                .containsEntry(DOUBLE, 1.5)
                .containsEntry(FLOAT, 1.5f)
                .containsEntry(INTEGER, 1000)
                .containsEntry(BOOLEAN, true);
    }

    private void testSupportedConstantValues(boolean useTypeAlias) {
        Map<String, String> definitions = new HashMap<>();
        definitions.put(STRING, resolveType(String.class, useTypeAlias));
        definitions.put(OBJECT, resolveType(Object.class, useTypeAlias));
        definitions.put(DOUBLE, resolveType(Double.class, useTypeAlias));
        definitions.put(FLOAT, resolveType(Float.class, useTypeAlias));
        definitions.put(INTEGER, resolveType(Integer.class, useTypeAlias));
        definitions.put(BOOLEAN, resolveType(Boolean.class, useTypeAlias));

        Map<String, Object> values = new HashMap<>();
        values.put(STRING, STRING);
        values.put(OBJECT, OBJECT);
        values.put(DOUBLE, "1.5");
        values.put(FLOAT, "1.5");
        values.put(INTEGER, "1000");
        values.put(BOOLEAN, "false");

        TaskDataConverterUtil.convert(definitions, values);

        Assertions.assertThat(values)
                .containsEntry(STRING, STRING)
                .containsEntry(OBJECT, OBJECT)
                .containsEntry(DOUBLE, 1.5)
                .containsEntry(FLOAT, 1.5f)
                .containsEntry(INTEGER, 1000)
                .containsEntry(BOOLEAN, false);
    }

    private String resolveType(Class type, boolean useTypeAlias) {
        return useTypeAlias ? type.getSimpleName() : type.getName();
    }
}
