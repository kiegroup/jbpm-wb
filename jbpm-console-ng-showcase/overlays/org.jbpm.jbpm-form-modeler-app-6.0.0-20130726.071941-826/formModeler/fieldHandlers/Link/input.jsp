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
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.jbpm.formModeler.core.wrappers.Link"%>
<%@ page import="org.jbpm.formModeler.service.LocaleManager"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jbpm.formModeler.core.processing.fieldHandlers.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>
<%try{%>
<mvc:formatter name="LinkFieldHandlerFormatter">
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
                                            <table border="0" cellpadding="0" cellspacing="0"
                                                   class="dynInputStyle <%=StringUtils.defaultString((String) styleclass)%>"
                                                   style="<%=StringUtils.defaultString((String) cssStyle)%>"
                                                    >
                                                <tr valign="top">
                                                    <td id="<mvc:fragmentValue name="uid"/>_nameTD">
                                                        <label for="<mvc:fragmentValue name="uid"/>_name">
                                                        <i18n:message key="link.name">!!!Nombre</i18n:message>:
                                                        </label>
                                                        &nbsp;
                                                    </td>
                                                    <td>
                                                        <input  name="<%=name%>_name"  id="<mvc:fragmentValue name="uid"/>_name"
                                                            onchange="processFormInputChange(this)"
                                                            <%=title!=null?("title=\""+title+"\""):""%>
                                                            class="skn-input"
                                                            <%=size!=null ? " size=\""+size+"\"":""%>
                                                            <%=maxlength!=null ? " maxlength=\""+maxlength+"\"":""%>
                                                            <%=tabindex!=null ? " tabindex=\""+tabindex+"\"":""%>
                                                            <%=accesskey!=null ? " accesskey=\""+accesskey+"\"":""%>
                                                            <%=alt!=null ? " alt=\""+alt+"\"":""%>
                                                            <%=height!=null ? " height=\""+height+"\"":""%>
                                                            <%=readonly!=null && ((Boolean)readonly).booleanValue()? " readonly ":""%>
                                                            <%=disabled!=null && ((Boolean)disabled).booleanValue()? " disabled ":""%>
                                                                value='<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(value==null?"":String.valueOf(((Link)value).getName())))%>'>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td id="<mvc:fragmentValue name="uid"/>_linkTD">
                                                        <label for="<mvc:fragmentValue name="uid"/>_link">
                                                        <i18n:message key="link.link">!!!Link</i18n:message>:
                                                        </label>
                                                        &nbsp;
                                                    </td>
                                                    <td>
                                                        <input  name="<%=name%>_link"  id="<mvc:fragmentValue name="uid"/>_link"
                                                            onchange="processFormInputChange(this)"
                                                            <%=title!=null?("title=\""+title+"\""):""%>
                                                            class="skn-input"
                                                            <%=size!=null ? " size=\""+size+"\"":""%>
                                                            <%=maxlength!=null ? " maxlength=\""+maxlength+"\"":""%>
                                                            <%=tabindex!=null ? " tabindex=\""+tabindex+"\"":""%>
                                                            <%=accesskey!=null ? " accesskey=\""+accesskey+"\"":""%>
                                                            <%=alt!=null ? " alt=\""+alt+"\"":""%>
                                                            <%=height!=null ? " height=\""+height+"\"":""%>
                                                            <%=readonly!=null && ((Boolean)readonly).booleanValue()? " readonly ":""%>
                                                            <%=disabled!=null && ((Boolean)disabled).booleanValue()? " disabled ":""%>
                                                                value='<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(value==null?"":String.valueOf(((Link)value).getLink())))%>'>

                                                    </td>

                                                </tr>
                                            </table>
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
<%}catch(Throwable t){System.out.println("Error showing Text input "+t);t.printStackTrace();}%>
