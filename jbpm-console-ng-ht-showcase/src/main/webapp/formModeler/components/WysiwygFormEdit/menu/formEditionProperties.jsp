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
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="WysiwygEditionPropertiesFormatter">
    <mvc:fragment name="outputStart">
        <div style="width: 100%;">
    </mvc:fragment>
    <mvc:fragment name="outputSwitchRenderMode">
        <mvc:fragmentValue name="renderMode" id="renderMode">
        <div>
            <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="switchRenderMode"/>">
                <factory:handler action="switchRenderMode" />
                <div style="margin:3px;"><i18n:message key="wysiwyg.renderMode">!!!Modo</i18n:message></div>
                <select name="renderMode" onchange="submitAjaxForm(this.form);return false;" class="skn-input">
                    <option value="<%=Form.RENDER_MODE_WYSIWYG_FORM%>" <%=Form.RENDER_MODE_WYSIWYG_FORM.equals(renderMode) ? "selected" : ""%>><i18n:message key="wysiwyg.renderModeForm"/></option>
                    <option value="<%=Form.RENDER_MODE_WYSIWYG_DISPLAY%>" <%=Form.RENDER_MODE_WYSIWYG_DISPLAY.equals(renderMode) ? "selected" : ""%>><i18n:message key="wysiwyg.renderModeDisplay"/></option>
                </select>
            </form>
            <script type="text/javascript" defer="defer">
                setAjax("<factory:encode name="switchRenderMode"/>");
            </script>
        </div>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </div>
    </mvc:fragment>
</mvc:formatter>