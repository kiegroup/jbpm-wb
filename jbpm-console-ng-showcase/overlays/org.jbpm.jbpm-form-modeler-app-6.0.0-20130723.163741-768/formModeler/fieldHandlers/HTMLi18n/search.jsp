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
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.jbpm.formModeler.core.wrappers.HTMLi18n"%>
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.HTMLi18nFieldHandler"%>
<%@ page import="org.jbpm.formModeler.service.LocaleManager"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%try{%>
<table border="0" cellpadding="0" cellspacing="0" >
    <tr>
        <td>
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
                    <input type="text" name="<%=name%>_<%=LocaleManager.currentLang()%>" id="<%=name%>_<%=LocaleManager.currentLang()%>"
                            <%=title!=null?("title=\""+title+"\""):""%>
                            <%=styleclass!=null && ((String)styleclass).trim().length()>0 ? " class=\""+styleclass+"\"":"class=\"skn-input\""%>
                            <%=size!=null ? " size=\""+(int)(Integer.valueOf((String)size).intValue()/10)+"\"":""%>
                            <%=maxlength!=null ? " maxlength=\""+maxlength+"\"":""%>
                            <%=tabindex!=null ? " tabindex=\""+tabindex+"\"":""%>
                            <%=accesskey!=null ? " accesskey=\""+accesskey+"\"":""%>
                            <%=alt!=null ? " alt=\""+alt+"\"":""%>
                            <%=cssStyle!=null ? " style=\""+cssStyle+"\"":""%>
                            <%=height!=null ? " height=\""+height+"\"":""%>
                            <%=readonly!=null && ((Boolean)readonly).booleanValue()? " readonly ":""%>
                            <%=disabled!=null && ((Boolean)disabled).booleanValue()? " disabled ":""%>
                            value="<%=StringEscapeUtils.escapeHtml((value != null && !"".equals(value)) ? StringUtils.defaultString(((HTMLi18n) value).getValue(LocaleManager.currentLang())) : "") %>"
                            onchange="document.getElementById('<%=name%>').value=this.value; processFormInputChange(this);">
                    <input type="hidden" name="<%=name%>_<%=HTMLi18nFieldHandler.LANGUAGE_INPUT_NAME%>" value="<%=LocaleManager.currentLang()%>">
                    <input type="hidden" name="<%=name%>" id="<%=name%>" value="<%=StringEscapeUtils.escapeHtml((value != null && !"".equals(value)) ? StringUtils.defaultString(((HTMLi18n) value).getValue(LocaleManager.currentLang())) : "") %>">
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
        </td>
    </tr>
</table>
<%}catch(Throwable t){System.out.println("Error showing HTMLi18n "+t);t.printStackTrace();}%>
