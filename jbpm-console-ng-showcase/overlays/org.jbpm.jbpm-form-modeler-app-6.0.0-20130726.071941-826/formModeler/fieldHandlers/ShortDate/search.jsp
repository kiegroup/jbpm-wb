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
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Date"%>
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.DateFieldHandler" %>
<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%
    try {
%>
<table border="0" cellpadding="0" cellspacing="0" >
    <tr valign="top">
<mvc:formatter name="DateFieldHandlerFormatter">
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
        <mvc:fragmentValue name="disabled" id="disabled">
        <mvc:fragmentValue name="height" id="height">
        <mvc:fragmentValue name="readonly" id="readonly">
        <mvc:fragmentValue name="uid" id="uid">
        <mvc:fragmentValue name="calendarPattern" id="calendarPattern">
        <mvc:fragmentValue name="defaultCalendarPattern" id="defaultCalendarPattern">
        <mvc:fragmentValue name="inputPattern" id="inputPattern">
<%
        SimpleDateFormat sdf = new SimpleDateFormat(StringUtils.defaultString((String) inputPattern, (String) defaultCalendarPattern), LocaleManager.currentLocale());
%>
        <td>
            <input type="hidden" name="<%=name + DateFieldHandler.HAS_CHANGED_PARAM%>" id="<%=uid + DateFieldHandler.HAS_CHANGED_PARAM%>" value="false"/>
            <input type="hidden" name="<%=name + DateFieldHandler.DATE_PATTERN_SUFFIX%>" id="<%=uid + DateFieldHandler.DATE_PATTERN_SUFFIX%>" value="<%=inputPattern%>"/>
            <input  name="<%=name + DateFieldHandler.DATE_FROM_SUFFIX%>"
                    onchange="processFormInputChange(this)"
                    id="<mvc:fragmentValue name="uid"/><%=DateFieldHandler.DATE_FROM_SUFFIX%>"
                <%=title!=null?("title=\""+title+"\""):""%>
                <%=styleclass!=null && ((String)styleclass).trim().length()>0 ? " class=\""+styleclass+"\"":"class=\"skn-input\""%>
                <%=size!=null ? " size=\""+size+"\"":""%>
                <%=maxlength!=null ? " maxlength=\""+maxlength+"\"":""%>
                <%=tabindex!=null ? " tabindex=\""+tabindex+"\"":""%>
                <%=accesskey!=null ? " accesskey=\""+accesskey+"\"":""%>
                <%=alt!=null ? " alt=\""+alt+"\"":""%>
                <%=cssStyle!=null ? " style=\""+cssStyle+"\"":""%>
                <%=height!=null ? " height=\""+height+"\"":""%>
                readonly
                <%=disabled!=null && ((Boolean)disabled).booleanValue()? " disabled ":""%>
                    value='<%= (value!=null) ? ((value instanceof Object[]) ? ((((Object[])value)[0]!=null) ? sdf.format((Date)((Object[])value)[0]):""):"") : ""%>'>
        </td>
        <td>
            <a href="#"
<%
    if( (disabled==null || !((Boolean)disabled).booleanValue()) && (readonly==null || !((Boolean)readonly).booleanValue() )){
%>
                onclick="document.getElementById('<%=uid + DateFieldHandler.HAS_CHANGED_PARAM%>').value = true;
                        NewCal('<mvc:fragmentValue name="uid"/><%=DateFieldHandler.DATE_FROM_SUFFIX%>','<%=calendarPattern%>',false);
                        return false; "
<%
    } else{
%>
                onclick="return false"
<%
    }
%>
            >
                <img src="<static:image relativePath="general/16x16/ico-calendar.png"/>" border="0">
            </a>
        </td>
        <td>
            <a href="#"
               onclick="document.getElementById('<%=uid + DateFieldHandler.HAS_CHANGED_PARAM%>').value = true;
                       document.getElementById('<mvc:fragmentValue name="uid"/><%=DateFieldHandler.DATE_FROM_SUFFIX%>').value='';
                       return false;">
                <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0">
            </a>
        </td>
        <td>
            &nbsp;-&nbsp;
        </td>
        <td>
            <input  name="<%=name + DateFieldHandler.DATE_TO_SUFFIX%>"
                onchange="processFormInputChange(this)"
                id="<mvc:fragmentValue name="uid"/><%=DateFieldHandler.DATE_TO_SUFFIX%>"
            <%=title!=null?("title=\""+title+"\""):""%>
            <%=styleclass!=null && ((String)styleclass).trim().length()>0 ? " class=\""+styleclass+"\"":"class=\"skn-input\""%>
            <%=size!=null ? " size=\""+size+"\"":""%>
            <%=maxlength!=null ? " maxlength=\""+maxlength+"\"":""%>
            <%=tabindex!=null ? " tabindex=\""+tabindex+"\"":""%>
            <%=accesskey!=null ? " accesskey=\""+accesskey+"\"":""%>
            <%=alt!=null ? " alt=\""+alt+"\"":""%>
            <%=cssStyle!=null ? " style=\""+cssStyle+"\"":""%>
            <%=height!=null ? " height=\""+height+"\"":""%>
            readonly
            <%=disabled!=null && ((Boolean)disabled).booleanValue()? " disabled ":""%>
                value="<%= (value!=null) ? ((value instanceof Object[]) ? ((((Object[])value)[1]!=null) ? sdf.format((Date)((Object[])value)[1]):""):"") : ""%>">
        </td>
        <td>
            <a href="#"
<%
    if( (disabled==null || !((Boolean)disabled).booleanValue()) && (readonly==null || !((Boolean)readonly).booleanValue() )){
%>
                onclick="document.getElementById('<%=uid + DateFieldHandler.HAS_CHANGED_PARAM%>').value = true;
                        NewCal('<mvc:fragmentValue name="uid"/><%=DateFieldHandler.DATE_TO_SUFFIX%>','<%=calendarPattern%>',false);
                        return false; "
<%
    } else{
%>
                onclick="return false"
<%
    }
%>
                    >
                <img src="<static:image relativePath="general/16x16/ico-calendar.png"/>" border="0">
            </a>
        </td>
        <td>
             <a href="#" onclick="document.getElementById('<%=uid + DateFieldHandler.HAS_CHANGED_PARAM%>').value = true;
                     document.getElementById('<mvc:fragmentValue name="uid"/><%=DateFieldHandler.DATE_TO_SUFFIX%>').value='';
                     return false;">
                <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0">
            </a>
        </td>
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
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>
    </tr>
</table>
<%}catch(Throwable t){System.out.println("Error showing Date input "+t);t.printStackTrace();}%>
