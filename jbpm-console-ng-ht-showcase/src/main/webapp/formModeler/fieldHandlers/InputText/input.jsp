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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%try{%>
<mvc:formatter name="RangeInputTextFieldHandlerFormatter">
    <mvc:fragment name="outputForceShowMode">
        <mvc:fragmentValue name="name" id="name">
        <mvc:fragmentValue name="wrong" id="wrong">
        <mvc:fragmentValue name="inputValue" id="inputValue">
        <mvc:fragmentValue name="value" id="value">
        <mvc:fragmentValue name="title" id="title">
        <mvc:fragmentValue name="styleclass" id="styleclass">
        <mvc:fragmentValue name="cssStyle" id="cssStyle">
        <mvc:fragmentValue name="isHTML" id="isHTML">

<input name="<%=name%>"  id="<mvc:fragmentValue name="uid"/>"
    onchange="processFormInputChange(this)" type="hidden"
    value='<%=Boolean.TRUE.equals(wrong)? (StringEscapeUtils.escapeHtml4(StringUtils.defaultString(inputValue==null?"":String.valueOf(inputValue))))
                : (StringEscapeUtils.escapeHtml4(StringUtils.defaultString(value==null?"":String.valueOf(value))))%>'>
<span id="<mvc:fragmentValue name="uid"/>_showContainer" name="<%=name%>_showContainer"
        <%=styleclass!=null && ((String)styleclass).trim().length()>0 ? " class=\""+styleclass+"\"":""%>
        <%=cssStyle!=null ? " style=\""+cssStyle+"\"":""%>
        <%=title!=null?("title=\""+title+"\""):""%>>
    <%= (isHTML != null && ((Boolean)isHTML).booleanValue() && value!=null) ? value : StringEscapeUtils.escapeHtml4( value!=null?value.toString():"" )%>
</span>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="output">
        <mvc:fragmentValue name="name" id="name">
        <mvc:fragmentValue name="title" id="title">
        <mvc:fragmentValue name="styleclass" id="styleclass">
        <mvc:fragmentValue name="size" id="size">
        <mvc:fragmentValue name="maxlength" id="maxlength">
        <mvc:fragmentValue name="value" id="value">
        <mvc:fragmentValue name="alt" id="alt">
        <mvc:fragmentValue name="cssStyle" id="cssStyle">
        <mvc:fragmentValue name="height" id="height">
        <mvc:fragmentValue name="readonly" id="readonly">
        <mvc:fragmentValue name="wrong" id="wrong">
        <mvc:fragmentValue name="inputValue" id="inputValue">
        <mvc:fragmentValue name="hideContent" id="hideContent">
        <mvc:fragmentValue name="onChangeScript" id="onChangeScript">
<input  name="<%=name%>"  id="<mvc:fragmentValue name="uid"/>"
        type="<%=(hideContent!=null && ((Boolean)hideContent).booleanValue()) ? "password" : "text"%>"
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
        class="dynInputStyle <%=StringUtils.defaultString((String) styleclass, "skn-input")%>"
        style="<%=StringUtils.defaultString((String) cssStyle)%>"
        <%=size!=null ? " size=\""+size+"\"":""%>
        <%=maxlength!=null ? " maxlength=\""+maxlength+"\"":""%>
        <%=alt!=null ? " alt=\""+alt+"\"":""%>
        <%=height!=null ? " height=\""+height+"\"":""%>
        <%=readonly!=null && ((Boolean)readonly).booleanValue()? " readonly ":""%>
        value="<%=Boolean.TRUE.equals(wrong) ? (StringEscapeUtils.escapeHtml4(StringUtils.defaultString(inputValue==null?"":String.valueOf(inputValue))))
                    : (StringEscapeUtils.escapeHtml4(StringUtils.defaultString(value==null?"":String.valueOf(value))))%>">
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
    <mvc:fragment name="outputStartRange">
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
    <%=readonly!=null && ((Boolean)readonly).booleanValue()? " onfocus=\"this.blur();\" disabled":""%>
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
    <mvc:fragment name="outputRange">
    <option value="<mvc:fragmentValue name="key"/>"><mvc:fragmentValue name="value"/></option>
    </mvc:fragment>
    <mvc:fragment name="outputSelectedRange">
    <option selected value="<mvc:fragmentValue name="key"/>"><mvc:fragmentValue name="value"/></option>
    </mvc:fragment>
    <mvc:fragment name="outputEndRange">
</select>
    </mvc:fragment>
</mvc:formatter>
<%}catch(Throwable t){System.out.println("Error showing Text input "+t);t.printStackTrace();}%>
