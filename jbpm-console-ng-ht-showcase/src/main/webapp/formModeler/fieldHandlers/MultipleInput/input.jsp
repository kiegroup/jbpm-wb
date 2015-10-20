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
<%@ page import="org.jbpm.formModeler.core.processing.FormProcessor" %>
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.multiple.MultipleInputFieldHandlerFormatter" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.core.processing.fieldHandlers.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="MultipleInputFieldHandlerFormatter">
    <mvc:formatterParam name="<%=MultipleInputFieldHandlerFormatter.PARAM_MODE%>" value="<%=MultipleInputFieldHandlerFormatter.MODE_INPUT%>"/>
    <mvc:fragment name="outputStart">
        <mvc:fragmentValue name="uid" id="uid">
        <mvc:fragmentValue name="namespace" id="namespace">
        <mvc:fragmentValue name="fieldName" id="fieldName">
        <mvc:fragmentValue name="formId" id="formId">
    <div>
        <input type="hidden" id="<%=uid%>_addItem" name="<%=fieldName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "addItem"%>" value="leaveItAlone">
        <input type="hidden" id="<%=uid%>_deleteItem" name="<%=fieldName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "deleteItem"%>" value="-1">
        <input type="hidden" id="<%=uid%>_namespace" name="<%=fieldName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "namespace"%>" value="<%=namespace%>">
        <input type="hidden" id="<%=uid%>_formId" name="<%=fieldName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "formId"%>" value="<%=formId%>">
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="tableStart">
        <table class="skn-table_border" width="100%" cellspacing="1" cellpadding="1">
    </mvc:fragment>
    <mvc:fragment name="startHeader">
            <tr class="skn-table_header">
    </mvc:fragment>
    <mvc:fragment name="actionsColumn">
                <td width="1px"></td>
    </mvc:fragment>
    <mvc:fragment name="itemsColumn">
                <td><i18n:message key="items">!!!Items</i18n:message></td>
    </mvc:fragment>
    <mvc:fragment name="endHeader">
            </tr>
    </mvc:fragment>
    <mvc:fragment name="startRow">
            <tr>
    </mvc:fragment>
    <mvc:fragment name="rowAction">
        <mvc:fragmentValue name="uid" id="uid">
        <mvc:fragmentValue name="fieldName" id="fieldName">
        <mvc:fragmentValue name="index" id="index">
                <td style="width:1px; text-align: center">
                    <a title='<i18n:message key="delete">!!!Delete</i18n:message>'
                       href="#"
                       onclick="if (confirm('<i18n:message key="delete.confirm">Sure?</i18n:message>')) {
                               document.getElementById('<%=uid%>_deleteItem').value = <%=index%>;
                               clearChangeDDMTrigger();
                               sendFormToHandler(document.getElementById('<%=uid%>_deleteItem').form, 'org.jbpm.formModeler.core.processing.fieldHandlers.multiple.MultipleInputHandler', 'deleteItem');
                               }
                               return false;"
                       id="<%=uid%>_delete_<%=index%>">
                        <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0">
                    </a>
                </td>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="inputRow">
                <td>
    </mvc:fragment>
    <mvc:fragment name="endRow">
                </td>
            </tr>
    </mvc:fragment>
    <mvc:fragment name="tableEnd">
        </table>
    </mvc:fragment>
    <mvc:fragment name="startAdd">
        <div style="padding-top:5px; width:100%;">
            <i18n:message key="enterNewItem">!!!Please enter a new item</i18n:message>
            <div>
    </mvc:fragment>
    <mvc:fragment name="endAdd">
        <mvc:fragmentValue name="uid" id="uid">
        <mvc:fragmentValue name="fieldName" id="fieldName">
            </div>
        </div>
        <div style="padding-top:5px; width:100%;">
            <input type="button" class="skn-button" value="<mvc:fragmentValue name="addItemButtonText"/>"
                   onclick="document.getElementById('<%=uid%>_addItem').value = true;
                           this.disabled=true;
                           clearChangeDDMTrigger();
                           sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.multiple.MultipleInputHandler', 'addItem');"/>
        </div>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
    </div>
    </mvc:fragment>
</mvc:formatter>