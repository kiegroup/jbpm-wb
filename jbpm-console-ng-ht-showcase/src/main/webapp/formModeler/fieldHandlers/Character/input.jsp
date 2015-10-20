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
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<mvc:formatter name="SimpleFieldHandlerFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="title" id="title">
        <mvc:fragmentValue name="name" id="name">
        <mvc:fragmentValue name="styleclass" id="styleclass">
        <mvc:fragmentValue name="value" id="value">
        <mvc:fragmentValue name="cssStyle" id="cssStyle">
        <mvc:fragmentValue name="height" id="height">
        <mvc:fragmentValue name="readonly" id="readonly">
        <mvc:fragmentValue name="onChangeScript" id="onChangeScript">
<input type="text" name="<%=name%>" id="<mvc:fragmentValue name="uid"/>"
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
%>"
        maxlength="1"
        size="1"
        <%=title!=null?("title=\""+title+"\""):""%>
        class="dynInputStyle <%=StringUtils.defaultString((String) styleclass, "skn-input")%>"
        <%=cssStyle!=null ? " style=\""+cssStyle+"\"":""%>
        <%=height!=null ? " height=\""+height+"\"":""%>
        <%=readonly!=null && ((Boolean)readonly).booleanValue()? " readonly ":""%>
        value="<%=StringEscapeUtils.escapeHtml4(StringUtils.defaultString(value==null?"":String.valueOf(value)))%>">
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>
