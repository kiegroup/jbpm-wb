/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.forms.display.backend.provider.util;

import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

public class FormContentReader {

    public static final String getStartProcessForms() {
        return getFormContent("/forms/invoices-taskform.frm");
    }

    public static final String getTaskForms() {
        return getFormContent("/forms/modify-taskform.frm");
    }

    private static String getFormContent(String formPath) {

        try {
            JsonArray formsArray = new JsonArray();

            JsonParser parser = new JsonParser();

            String content = IOUtils.toString(new InputStreamReader(FormContentReader.class.getResourceAsStream("/forms/Client.frm")));

            formsArray.add(parser.parse(content));

            content = IOUtils.toString(new InputStreamReader(FormContentReader.class.getResourceAsStream("/forms/InvoiceLine.frm")));

            formsArray.add(parser.parse(content));

            content = IOUtils.toString(new InputStreamReader(FormContentReader.class.getResourceAsStream("/forms/Invoice.frm")));

            formsArray.add(parser.parse(content));

            content = IOUtils.toString(new InputStreamReader(FormContentReader.class.getResourceAsStream(formPath)));

            formsArray.add(parser.parse(content));

            Gson gson = new Gson();

            return gson.toJson(formsArray);
        } catch (IOException e) {
        }

        return null;
    }
}
