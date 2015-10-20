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
<%@ page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.radio.RadioGroupFieldHandlerFormatter" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%try{%>
<mvc:formatter name="RadioGroupFieldHandlerFormatter">
    <mvc:formatterParam name="<%=RadioGroupFieldHandlerFormatter.PARAM_MODE%>" value="<%=RadioGroupFieldHandlerFormatter.MODE_INPUT%>"/>
    <mvc:fragment name="outputStart">
        <mvc:fragmentValue name="readonly" id="readonly">
<div>
<%
    if (readonly!=null && ((Boolean)readonly).booleanValue()) {
%>
            <input type="hidden" name="<mvc:fragmentValue name="name"/>" id="<mvc:fragmentValue name="uid"/>" value="<mvc:fragmentValue name="value"/>">
<%
    }
%>
    <div style="display: table;">
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="startRow">
        <div style="display: table-row;">
    </mvc:fragment>
    <mvc:fragment name="startCell">
            <div style="display: table-cell; white-space: nowrap; padding: 5px;">
    </mvc:fragment>
    <mvc:fragment name="outputRadio">
        <mvc:fragmentValue name="cssStyle" id="cssStyle">
        <mvc:fragmentValue name="styleclass" id="styleclass">
        <mvc:fragmentValue name="uid" id="uid">
        <mvc:fragmentValue name="readonly" id="readonly">
        <mvc:fragmentValue name="checked" id="checked">
        <mvc:fragmentValue name="onChangeScript" id="onChangeScript">
                <input type="radio"
                       name="<mvc:fragmentValue name="name"/>"
                       id="<%=uid%>"
                       class='dynInputStyle <%=StringUtils.defaultString((String) styleclass, "skn-input")%>'
                       style="<%=StringUtils.defaultString((String) cssStyle)%>"
                        <%=readonly!=null && ((Boolean)readonly).booleanValue()? " disabled= \"disabled\"":""%>
                        <%=checked!=null && ((Boolean)checked).booleanValue()? " checked=\"checked\"'":""%>
                       value="<mvc:fragmentValue name="key"/>"
                       onchange="processFormInputChange(this);
<%
    if (onChangeScript != null) {
%>
                                   try {
                                       eval('<%=StringEscapeUtils.escapeEcmaScript(StringEscapeUtils.escapeHtml4((String)onChangeScript))%>');
                                   } catch (err) {
                                       alert('Error executing inline js: ' + scriptCode);
                                   }
<%
    }
%>
                               ">
                <label for="<%=uid%>"><mvc:fragmentValue name="value"/></label>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="endCell">
            </div>
    </mvc:fragment>
    <mvc:fragment name="endRow">
        </div>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
    </div>
</div>
    </mvc:fragment>
</mvc:formatter>
<%}catch(Throwable t){System.out.println("Error showing RadioGroup: " + t);t.printStackTrace();}%>
