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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskDataConverterUtil {

    private static final Logger logger = LoggerFactory.getLogger(TaskDataConverterUtil.class);

    private static final Collection<Class> bypassedTypes = Arrays.asList(Object.class, String.class);
    private static final Map<Class, Function<String, Object>> converters = new HashMap<>();

    static {
        converters.put(Double.class, Double::parseDouble);
        converters.put(Float.class, Float::parseFloat);
        converters.put(Integer.class, Integer::parseInt);
        converters.put(Boolean.class, Boolean::parseBoolean);
    }

    public static void convert(Map<String, String> typeDefinitions, Map<String, Object> data) {
        typeDefinitions.entrySet().stream()
                .filter(entry -> !isBypassType(entry.getValue()) && mightBeConstant(data.get(entry.getKey())))
                .forEach(entry -> process(entry.getKey(), entry.getValue(), data.get(entry.getKey()), newValue -> data.put(entry.getKey(), newValue)));
    }

    private static void process(String variable, String type, Object value, Consumer<Object> consumer) {

        Optional<Function<String, Object>> optional = converters.entrySet().stream()
                .filter(entry -> matches(entry.getKey(), type))
                .map(Map.Entry::getValue)
                .findAny();

        if (optional.isPresent()) {
            Function<String, Object> function = optional.get();
            try {
                consumer.accept(function.apply(value.toString()));
            } catch (Exception ex) {
                logger.warn("Couldn't parse constant value '{}' for variable '{}': {}", value, variable, ex);
                logger.warn("Setting variable '{}' to null.", variable);
                consumer.accept(null);
            }
        } else {
            logger.warn("Variable '{}' has a constant value '{}' that doesn't match the variable type ('{}'). This might " +
                                "cause problems during the form lifecycle.", variable, value, type);
            logger.warn("Setting variable '{}' to null.", variable);
            consumer.accept(null);
        }
    }

    private static boolean isBypassType(String type) {
        return bypassedTypes.stream()
                .anyMatch(clazz -> matches(clazz, type));
    }

    private static boolean matches(Class clazz, String type) {
        return clazz.getName().equals(type) || clazz.getSimpleName().equals(type);
    }

    private static boolean mightBeConstant(Object value) {
        return value instanceof String;
    }
}
