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
<%@ page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@ page import="org.jbpm.formModeler.core.wrappers.HTMLi18n"%>
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.HTMLi18nFieldHandler"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.jbpm.formModeler.service.LocaleManager"%>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%try{%>
<mvc:formatter name="SimpleFieldHandlerFormatter">
<mvc:fragment name="output">
    <mvc:fragmentValue name="name" id="name">
    <mvc:fragmentValue name="title" id="title">
    <mvc:fragmentValue name="uid" id="uid">
        <mvc:fragmentValue name="styleclass" id="styleclass">
            <mvc:fragmentValue name="size" id="size">
                <mvc:fragmentValue name="maxlength" id="maxlength">
                    <mvc:fragmentValue name="tabindex" id="tabindex">
                        <mvc:fragmentValue name="value" id="val">
                            <mvc:fragmentValue name="accesskey" id="accesskey">
                                <mvc:fragmentValue name="alt" id="altvalue">
                                    <mvc:fragmentValue name="cssStyle" id="cssStyle">
                                        <mvc:fragmentValue name="height" id="height">
                                          <mvc:fragmentValue name="readonly" id="readonly">
                                             <mvc:fragmentValue name="lang" id="languageForEditor">
                                        <table border="0" cellpadding="0" cellspacing="0" >
                                            <tr valign="top">
                                                <td>
                                                    <%
                                                        String value = StringEscapeUtils.escapeHtml4(StringUtils.defaultString((val == null || "".equals(val)) ? "" : ((HTMLi18n) val).getValue(LocaleManager.currentLang())));
                                                        if (!Boolean.TRUE.equals(readonly)) {
                                                    %>
                                                    <table border="0" cellpadding="0" cellspacing="0" >
                                                        <tr valign="top">
                                                            <td style="padding-bottom: 250px;">
                                                                <div style=" width:<%=size!=null?size:"250"%>px; height:<%=height!=null?height:"170"%>px;<%=StringUtils.defaultString((String) cssStyle)%>"
                                                                     id="<%=uid%>_divcontainer" class="dynInputStyle">
                                                                    <textarea id="<%=uid%>_<%=LocaleManager.currentLang()%>" name="<%=name%>_<%=LocaleManager.currentLang()%>"
                                                                              onchange="processFormInputChange(this)"
                                                                              onkeyup="return ismaxlength(this)"
                                                                              rows="4" cols="50"
                                                                            <%=title != null ? ("title=\"" + title + "\"") : ""%>
                                                                              class="skn-input"
                                                                            <%=maxlength != null ? " maxlength=\"" + maxlength + "\"" : ""%>
                                                                            <%=tabindex != null ? " tabindex=\"" + tabindex + "\"" : ""%>
                                                                            <%=accesskey != null ? " accesskey=\"" + accesskey + "\"" : ""%>
                                                                            <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                                                                            <%=Boolean.TRUE.equals(readonly) ? " readonly disabled " : ""%>><%=value%></textarea>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                    <input type="hidden" name="<%=name%>" id="<%=name%>" value="<%=value%>"/>
                                                    <script>
                                                        CKEditorHandler.create('<%=uid%>_<%=LocaleManager.currentLang()%>', '<%=name%>', '<%=title%>',<%=readonly%>,<%=tabindex%>,<%=100%>,<%=300%>,'<%=LocaleManager.currentLang()%>', '<%=maxlength%>');
                                                    </script>
                                                    <%
                                                    } else {
                                                    %>
                                                    <div id="<%=name+HTMLi18nFieldHandler.DIV_INPUT_NAME_PREFFIX%>" style="width:<%=size!=null?size:"250"%>px; height:<%=height!=null?height:"170"%>px"
                                                         class="dynInputStyle <%=StringUtils.defaultString((String) styleclass)%>"
                                                            <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                                                            <%=title != null ? ("title=\"" + title + "\"") : ""%>
                                                            >
                                                        <%=StringUtils.defaultString(val == null ? "" : ((HTMLi18n)val).getValue(LocaleManager.currentLang()).toString())%>
                                                    </div>
                                                    <input type="hidden" name="<%=name%>" value="<%=value%>"/>
                                                    <%
                                                        }
                                                    %>

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
</mvc:fragmentValue>
</mvc:fragment>
</mvc:formatter>
<%}catch(Throwable t){System.out.println("Error showing HTMLi18n input "+t);t.printStackTrace();}%>
