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
    <mvc:formatterParam name="<%=MultipleInputFieldHandlerFormatter.PARAM_MODE%>" value="<%=MultipleInputFieldHandlerFormatter.MODE_SHOW%>"/>
    <mvc:fragment name="outputStart">
        <mvc:fragmentValue name="uid" id="uid">
        <mvc:fragmentValue name="namespace" id="namespace">
        <mvc:fragmentValue name="fieldName" id="fieldName">
        <mvc:fragmentValue name="formId" id="formId">
<div>
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
    <mvc:fragment name="outputEnd">
</div>
    </mvc:fragment>
</mvc:formatter>