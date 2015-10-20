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
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.select.SelectBoxFieldHandlerFormatter" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%try{%>
<mvc:formatter name="SelectBoxFieldHandlerFormatter">
    <mvc:formatterParam name="<%=SelectBoxFieldHandlerFormatter.PARAM_MODE%>" value="<%=SelectBoxFieldHandlerFormatter.MODE_INPUT%>"/>
    <mvc:fragment name="outputStart">
        <mvc:fragmentValue name="name" id="name">
        <mvc:fragmentValue name="title" id="title">
        <mvc:fragmentValue name="styleclass" id="styleclass">
        <mvc:fragmentValue name="size" id="size">
        <mvc:fragmentValue name="maxlength" id="maxlength">
        <mvc:fragmentValue name="tabindex" id="tabindex">
        <mvc:fragmentValue name="accesskey" id="accesskey">
        <mvc:fragmentValue name="alt" id="alt">
        <mvc:fragmentValue name="cssStyle" id="cssStyle">
        <mvc:fragmentValue name="height" id="height">
        <mvc:fragmentValue name="readonly" id="readonly">
        <mvc:fragmentValue name="onChangeScript" id="onChangeScript">
<select name="<%=name%>"  id='<mvc:fragmentValue name="uid"/>'
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
    <%=title!=null?("title=\""+title+"\""):""%>
    class='dynInputStyle <%=StringUtils.defaultString((String) styleclass, "skn-input")%>'
    style="<%=StringUtils.defaultString((String) cssStyle)%>"
    <%=size!=null ? " size=\""+size+"\"":""%>
    <%=maxlength!=null ? " maxlength=\""+maxlength+"\"":""%>
    <%=tabindex!=null ? " tabindex=\""+tabindex+"\"":""%>
    <%=accesskey!=null ? " accesskey=\""+accesskey+"\"":""%>
    <%=alt!=null ? " alt=\""+alt+"\"":""%>
    <%=height!=null ? " height=\""+height+"\"":""%>
    <%=readonly!=null && ((Boolean)readonly).booleanValue()? " onfocus=\"this.blur();\" disabled":""%>>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputOption">
    <option value="<mvc:fragmentValue name="key"/>"><mvc:fragmentValue name="value"/></option>
    </mvc:fragment>
    <mvc:fragment name="outputSelectedOption">
    <option selected value="<mvc:fragmentValue name="key"/>"><mvc:fragmentValue name="value"/></option>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
</select>
    </mvc:fragment>
</mvc:formatter>
<%}catch(Throwable t){System.out.println("Error showing Text input "+t);t.printStackTrace();}%>
