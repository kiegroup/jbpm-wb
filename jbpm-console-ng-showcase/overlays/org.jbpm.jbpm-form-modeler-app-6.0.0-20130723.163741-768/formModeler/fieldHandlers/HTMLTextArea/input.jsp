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
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.jbpm.formModeler.core.wrappers.HTMLString" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%try {%>
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
                                                <mvc:fragmentValue name="disabled" id="disabled">
                                                    <mvc:fragmentValue name="height" id="height">
                                                        <mvc:fragmentValue name="readonly" id="readonly">
                                                            <mvc:fragmentValue name="uid" id="uid">
                                                                    <%
                                                                        readonly= readonly==null ? Boolean.FALSE : readonly;
                                                                        disabled= disabled==null ? Boolean.FALSE : disabled;
                                                                    %>
                                                                    <%if ((readonly != null && !((Boolean) readonly).booleanValue()) && ((disabled != null) && !(((Boolean) disabled).booleanValue()))) { %>
                                                            <table border="0" cellpadding="0" cellspacing="0" >
                                                                <tr valign="top">
                                                                    <td>
                                                                        <div style=" width:<%=size!=null?size:"250"%>px; height:<%=height!=null?height:"170"%>px;<%=StringUtils.defaultString((String) cssStyle)%>"
                                                                             id="<%=uid%>_divcontainer" class="dynInputStyle">
                                                                            <textarea name="<%=name%>" rows="4" cols="50"
                                                                                      id="_<%=uid%>"
                                                                                    <%=title != null ? ("title=\"" + title + "\"") : ""%>
                                                                                    class="skn-input"
                                                                                    <%=maxlength != null ? " maxlength=\"" + maxlength + "\"" : ""%>
                                                                                    <%=tabindex != null ? " tabindex=\"" + tabindex + "\"" : ""%>
                                                                                    <%=accesskey != null ? " accesskey=\"" + accesskey + "\"" : ""%>
                                                                                    <%=alt != null ? " alt=\"" + alt + "\"" : ""%>
                                                                                    <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                                                                                    <%=readonly != null && ((Boolean) readonly).booleanValue() ? " readonly " : ""%>
                                                                                    <%=disabled != null && ((Boolean) disabled).booleanValue() ? " disabled " : ""%>><%=StringEscapeUtils.escapeHtml(value == null ? "" : ((HTMLString) value).getValue())%></textarea>
                                                                        </div>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                                    <% } else {%>
                                                            <table border="0" cellpadding="0" cellspacing="0" >
                                                                <tr valign="top">
                                                                    <td>
                                                                        <div style="width:<%=size!=null?size:"250"%>px; height:<%=height!=null?height:"170"%>px"
                                                                            class="dynInputStyle <%=StringUtils.defaultString((String) styleclass)%>"
                                                                            <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                                                                            <%=title != null ? ("title=\"" + title + "\"") : ""%>
                                                                            >
                                                                                <%=StringUtils.defaultString(value == null ? "" : ((HTMLString) value).getValue())%>
                                                                        </div>
                                                                        <input type="hidden" name="<%=name%>" value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(value==null?"":((HTMLString)value).getValue()))%>"/>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                                    <% } %>

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
<%} catch (Throwable t) {
    System.out.println("Error showing TextArea input " + t);
    t.printStackTrace();
}%>
