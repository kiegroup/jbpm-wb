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
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%try{%>
<mvc:formatter name="SimpleFieldHandlerFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="name" id="name">
        <mvc:fragmentValue name="title" id="title">
        <mvc:fragmentValue name="styleclass" id="styleclass">
        <mvc:fragmentValue name="size" id="size">
        <mvc:fragmentValue name="maxlength" id="maxlength">
        <mvc:fragmentValue name="tabindex" id="tabindex">
        <mvc:fragmentValue name="value" id="value">
        <mvc:fragmentValue name="accesskey" id="accesskey">
        <mvc:fragmentValue name="alt" id="alt">
        <mvc:fragmentValue name="cssStyle" id="cssStyle">
        <mvc:fragmentValue name="height" id="height">
        <mvc:fragmentValue name="readonly" id="readonly">
        <mvc:fragmentValue name="isEditMode" id="isEditMode">
        <mvc:fragmentValue name="onChangeScript" id="onChangeScript">
<textarea  name="<%=name%>" id="<mvc:fragmentValue name="uid"/>"
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
           onkeyup="return ismaxlength(this)"
        <%=title!=null?("title=\""+title+"\""):""%>
           class='dynInputStyle <%=StringUtils.defaultString((String) styleclass, "skn-input")%>'
           style="<%=Boolean.TRUE.equals(isEditMode) ? "resize: none;" : ""%><%=StringUtils.defaultString((String) cssStyle)%>"
        <%=size!=null ? " cols=\""+size+"\"":""%>
        <%=maxlength!=null ? " maxlength=\""+maxlength+"\"":""%>
        <%=tabindex!=null ? " tabindex=\""+tabindex+"\"":""%>
        <%=accesskey!=null ? " accesskey=\""+accesskey+"\"":""%>
        <%=alt!=null ? " alt=\""+alt+"\"":""%>
        <%=height!=null ? " rows=\""+height+"\"":""%>
        <%=readonly!=null && ((Boolean)readonly).booleanValue()? " readonly ":""%>><%=StringEscapeUtils.escapeHtml4(StringUtils.defaultString((String)value))%></textarea>
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
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>
<%}catch(Throwable t){System.out.println("Error showing TextArea input "+t);t.printStackTrace();}%>
