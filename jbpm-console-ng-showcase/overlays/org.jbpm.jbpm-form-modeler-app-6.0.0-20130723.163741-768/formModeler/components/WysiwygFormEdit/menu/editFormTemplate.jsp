<%--

    Copyright (C) 2012 JBoss Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>

<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ page import="org.jbpm.formModeler.components.editor.WysiwygFormEditor" %>
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="WysiwygEditFormTemplateFormatter">
<mvc:fragment name="output">

<mvc:fragmentValue name="templateContent" id="templateContent">
<mvc:fragmentValue name="templateToLoad" id="templateToLoad">
<mvc:fragmentValue name="formId" id="formId">
<mvc:fragmentValue name="loadTemplate" id="loadTemplate">
<mvc:fragmentValue name="genMode" id="genMode">

<script defer>
    window.formTemplateEditorHandler = new Object();
    window.formTemplateEditorHandler.evaluatingFlag = false;
    window.formTemplateEditorHandler.labelsSynchronized = false;
    window.formTemplateEditorHandler.fieldsSynchronized = false;


    window.formTemplateEditorHandler.evalAvailableFields = function (fieldsArray, selectElement) {
        if (!window.formTemplateEditorHandler.evaluatingFlag) {
            var synchronized = false;
            if (selectElement.id == "<factory:encode name="fieldsSelect"/>") {
                synchronized = window.formTemplateEditorHandler.fieldsSynchronized;
            } else {
                synchronized = window.formTemplateEditorHandler.labelsSynchronized;
            }
            if (synchronized) return;
            // var editor = window.formTemplateEditorHandler.getEditor();
            // if (!editor) return;


            window.formTemplateEditorHandler.evaluatingFlag = true;
            //alert("evalAvailableFields fieldsArray="+fieldsArray+" selectElement="+selectElement);
            //while (selectElement.options.length > 0) {if(selectElement.options) selectElement.options[0] = null;}
            selectElement.options.length = 0;
            //var editorContent = editor.GetHTML();


            var editorContent  = document.getElementById("<factory:encode name="templateTextArea"/>").value;

            selectElement.options[0] = new Option("-- <i18n:message key="chooseField">!!!Escoger campo</i18n:message> --", "");
            selectElement.options[0].selected = true;
            //alert("Iterating fieldsArray to create "+fieldsArray.length+" select elements");
            if (fieldsArray)
                for (i = 0; i < fieldsArray.length; i++) {
                    var opt = fieldsArray[i];
                    var key = opt[0];
                    var val = opt[1];
                    if (editorContent.indexOf(key) == -1) {
                        var option = new Option(val, key);
                        selectElement.options[selectElement.options.length] = option;
                    }
                }
            //alert("Finishing available fields evaluation");
            if (selectElement.id == "<factory:encode name="fieldsSelect"/>") {
                window.formTemplateEditorHandler.fieldsSynchronized = true;
            } else {
                window.formTemplateEditorHandler.labelsSynchronized = true;
            }
            window.formTemplateEditorHandler.evaluatingFlag = false;
        }
    }

    window.formTemplateEditorHandler.insertAtCaret = function (obj, text) {
        if (document.selection) { // Go the IE way
            obj.focus();

            var rng = document.selection.createRange();
            rng.text = text;
            rng.select();

        } else if (obj.selectionStart) { //Gecko
            var start = obj.selectionStart;
            var end = obj.selectionEnd;
            obj.value = obj.value.substr(0, start) + text + obj.value.substr(end, obj.value.length);
            obj.focus();
            obj.setSelectionRange(end + text.length, end + text.length);
        } else { // Fallback for any other browser
            obj.value += text;
        }
    }
    window.formTemplateEditorHandler.processSelectChange = function (selectElement, options) {
        if (!window.formTemplateEditorHandler.evaluatingFlag) {
            //alert("processSelectChange selectElement="+selectElement+" options="+options);
            //var editor = window.formTemplateEditorHandler.getEditor();
            //if (!editor) return;
            //var isTextArea = (editor == null) || (editor.EditorDocument == null) || (editor.EditMode != FCK_EDITMODE_WYSIWYG);
            //if (isTextArea) {
            var editorFrame = document.getElementById("<factory:encode name="templateTextArea"/>").value;
            //var editorFrame  = editorFrame.contentWindow.document.getElementsByTagName("textarea")[0];
            var eSourceField = document.getElementsByTagName("textarea")[0]
            window.formTemplateEditorHandler.insertAtCaret(eSourceField, selectElement.options[selectElement.selectedIndex].value);
            //}
            //else {
            //    editor.InsertHtml(selectElement.options[selectElement.selectedIndex].value);
            //}
            window.formTemplateEditorHandler.fieldsSynchronized = false;
            window.formTemplateEditorHandler.labelsSynchronized = false;
            //alert("Element inserted: "+selectElement.options[selectElement.selectedIndex].value);
            window.formTemplateEditorHandler.evalAvailableFields(options, selectElement);
        }
    }
</script>
<mvc:formatter name="AvailableTemplateElementsFormatter">
    <mvc:formatterParam name="type" value="Field"/>
    <mvc:fragment name="outputStartItemsToAdd">
        <script defer>
        window.formTemplateEditorHandler.fieldOptionsArray = [ ["",""]
    </mvc:fragment>
    <mvc:fragment name="outputItemToAdd">
        <mvc:fragmentValue name="val" id="val">
            ,["<mvc:fragmentValue name="key"/>","<%=StringEscapeUtils.escapeJava((String) val) %>"  ]
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEndItemsToAdd">
        ];
        </script>
    </mvc:fragment>
</mvc:formatter>
<mvc:formatter name="AvailableTemplateElementsFormatter">
    <mvc:formatterParam name="type" value="Label"/>
    <mvc:fragment name="outputStartItemsToAdd">
        <script defer>
        window.formTemplateEditorHandler.labelOptionsArray = [ ["",""]
    </mvc:fragment>
    <mvc:fragment name="outputItemToAdd">
        <mvc:fragmentValue name="val" id="val">
            ,["<mvc:fragmentValue name="key"/>","<%=StringEscapeUtils.escapeJava((String) val) %>"]
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEndItemsToAdd">
        ];
        </script>
    </mvc:fragment>
</mvc:formatter>

<table width="100%" class="skn-table_border" bgcolor="#FFFFFF" cellpadding="4" cellspacing="1" border="0">
        <%--  Edit form template --%>
    <tr>
        <td>
            <fieldset style="margin:10px">
                <legend class="skn-title3">
                    <i18n:message key="editingFormTemplate">!!!Form template</i18n:message>&nbsp;
                </legend>

                <form action="<factory:formUrl/>" id="<factory:encode name="editTemplateForm"/>" method="POST">
                    <table id="<factory:encode name="editorTable"/>" cellpadding="4" cellspacing="0" border="0"  class="skn-table_border" >
                        <tr>
                            <td colspan="2">
                                <factory:handler action="saveTemplate"/>
                                <textarea id="<factory:encode name="templateTextArea"/>"
                                          name="templateContent"
                                          rows="20"
                                          cols="100%"><% if (Boolean.TRUE.equals(loadTemplate)) {%>
                                    <mvc:formatter name="FormRenderingFormatter">
                                        <mvc:formatterParam name="formId" value="<%=formId%>"/>
                                        <mvc:formatterParam name="renderMode"
                                                            value="<%=Form.RENDER_MODE_TEMPLATE_EDIT%>"/>
                                        <mvc:formatterParam name="displayMode"
                                                            value="<%=genMode%>"/>
                                        <mvc:formatterParam name="namespace"
                                                            value='<%="template_" + templateToLoad%>'/>
                                        <%@ include
                                                file="defaultFormRenderingFormatterOptions.jsp" %>
                                    </mvc:formatter><% } else {%><%= (templateContent!= null ? templateContent:"") %><%}
                                    %></textarea>
                                <script language="Javascript" defer="true">
                                    var fieldTR='<factory:encode name="insertFieldTR"/>';
                                    var labelTR='<factory:encode name="insertLabelTR"/>';
                                    setTimeout('document.getElementById(fieldTR).style.display="";', 1500);
                                    setTimeout('document.getElementById(labelTR).style.display="";', 1500);
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" class="skn-table_header">
                                <i18n:message
                                        key="insertFormElements">!!!Insert form elements</i18n:message>
                            </td>
                        </tr>
                        <tr style="display:true" id="<factory:encode name="insertFieldTR"/>">
                            <td nowrap="nowrap" class="skn-even_row">

                                <i18n:message key="field">!!!Campo:</i18n:message>
                            </td>
                            <td>
                                <select style="width: 100%" class="skn-input"
                                        id="<factory:encode name="fieldsSelect"/>"
                                        onchange="if(window.formTemplateEditorHandler)window.formTemplateEditorHandler.processSelectChange( this , window.formTemplateEditorHandler.fieldOptionsArray );"
                                        onmouseover="if(window.formTemplateEditorHandler && this.length == 1)window.formTemplateEditorHandler.evalAvailableFields(window.formTemplateEditorHandler.fieldOptionsArray, this );"
                                        onmouseout="if(window.formTemplateEditorHandler)window.formTemplateEditorHandler.fieldsSynchronized=false"
                                        >
                                    <option>-- <i18n:message key="chooseField">!!!Escoger campo</i18n:message> --
                                    </option>
                                </select>
                            </td>
                        </tr>
                        <tr style="display:true" id="<factory:encode name="insertLabelTR"/>">
                            <td nowrap="nowrap" class="skn-even_row">
                                <i18n:message key="fieldLabel">!!!Field Label:</i18n:message>
                            </td>
                            <td>
                                <select style="width: 100%" class="skn-input"
                                        id="<factory:encode name="labelsSelect"/>"
                                        onchange="if(window.formTemplateEditorHandler)window.formTemplateEditorHandler.processSelectChange( this , window.formTemplateEditorHandler.labelOptionsArray );"
                                        onmouseover="if(window.formTemplateEditorHandler && this.length == 1)window.formTemplateEditorHandler.evalAvailableFields(window.formTemplateEditorHandler.labelOptionsArray, this);"
                                        onmouseout="if(window.formTemplateEditorHandler)window.formTemplateEditorHandler.labelsSynchronized=false"
                                        >
                                    <option>-- <i18n:message key="chooseField">!!!Escoger campo</i18n:message> --
                                    </option>
                                </select>

                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" height="1px">
                                <hr>
                            </td>
                        </tr>

                        <tr >
                            <td nowrap="nowrap" class="skn-even_row">
                                <i18n:message key="loadTemplate">!!!Load template:</i18n:message>
                            </td>
                            <td width="65%">
                                <select class="skn-input"
                                        name="genModeTemplate">
                                    <option value="<%=Form.DISPLAY_MODE_DEFAULT%>">
                                        <i18n:message key="displayMode.default">!!!Default</i18n:message>
                                    </option>
                                    <option value="<%=Form.DISPLAY_MODE_ALIGNED%>">
                                        <i18n:message key="displayMode.aligned">!!!Aligned fields</i18n:message>
                                    </option>
                                    <option value="<%=Form.DISPLAY_MODE_NONE%>">
                                        <i18n:message key="displayMode.none">!!!Without alignment</i18n:message>
                                    </option>
                                </select>
                                <input type="button" class="skn-button"
                                       onclick="
                                               if ( confirm('<i18n:message key="loadTemplateWarning"/>') ) {
                                               document.getElementById('<factory:encode name="loadTemplateHidden"/>').value='true' ;this.form.submit();
                                               }"
                                       value="<i18n:message key="ok"/>">
                                <input type="hidden"
                                       id="<factory:encode name="cancelHidden"/>"
                                       value="false">
                                <input type="hidden" name="loadTemplate"
                                       id="<factory:encode name="loadTemplateHidden"/>"
                                       value="false">
                                <input type="hidden"
                                       id="<factory:encode name="persistHidden"/>"
                                       value="false">
                            </td>
                        </tr >

                        <tr>
                            <td colspan="2">
                                <div style="width:100%; text-align:center; padding:5px;">
                                    <input type="hidden"
                                           id="<factory:encode name="cancelHidden"/>"
                                           value="false">
                                    <input type="hidden" name="loadTemplate"
                                           id="<factory:encode name="loadTemplateHidden"/>"
                                           value="false">
                                    <input type="hidden"
                                           id="<factory:encode name="persistHidden"/>"
                                           value="false">
                                    <input type="button" class="skn-button"
                                           onclick="document.getElementById('<factory:encode name="persistHidden"/>').value='true' ;this.form.submit();"
                                           value="<i18n:message key="save"/>">
                                    &nbsp;&nbsp;<input type="button" class="skn-button_alt"
                                                       onclick="document.getElementById('<factory:encode name="cancelHidden"/>').value='true' ;this.form.submit();"
                                                       value="<i18n:message key="cancel"/>">
                                </div>
                            </td>
                        </tr>
                    </table>
                </form>


            </fieldset>
        </td>
    </tr>
</table>
</mvc:fragmentValue>
</mvc:fragmentValue>
</mvc:fragmentValue>
</mvc:fragmentValue>
</mvc:fragmentValue>

</mvc:fragment>
</mvc:formatter>