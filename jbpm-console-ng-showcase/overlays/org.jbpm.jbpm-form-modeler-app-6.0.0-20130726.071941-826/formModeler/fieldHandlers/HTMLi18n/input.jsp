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
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
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
                                        <mvc:fragmentValue name="disabled" id="disabled">
                                            <mvc:fragmentValue name="height" id="height">
                                              <mvc:fragmentValue name="readonly" id="readonly">
                                                 <mvc:fragmentValue name="lang" id="languageForEditor">
                                        <%
                                            readonly= readonly==null ? Boolean.FALSE : readonly;
                                            disabled= disabled==null ? Boolean.FALSE : disabled;
                                        %>
                                        <table border="0" cellpadding="0" cellspacing="0" >
                                            <tr valign="top">
                                                <td>
                                                        <mvc:formatter name="org.jbpm.formModeler.service.mvc.formatters.ForFormatter">
                                                            <mvc:formatterParam name="bean" value="org.jbpm.formModeler.service.LocaleManager"/>
                                                            <mvc:formatterParam name="property" value="platformAvailableLocales"/>
                                                            <mvc:fragment name="outputStart">
                                                                <select name="selectChangeLanguage"   id="<%=uid%>_selectLang"
                                                                    class='dynInputStyle <%=StringUtils.defaultString((String) styleclass, "skn-input")%>'
                                                                    <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                                                                    onchange="
                                                                        for (var index = 0; index < this.options.length; index ++) {
                                                                            if (index == this.selectedIndex) $('#<%=name%>_' + this.options[index].value).show();
                                                                            else $('#<%=name%>_' + this.options[index].value).hide();

                                                                        }">
                                                            </mvc:fragment>
                                                            <mvc:fragment name="output">
                                                                <mvc:fragmentValue name="index" id="index">
                                                                    <mvc:fragmentValue name="element" id="locale">
                                                                        <%
                                                                            String selected = "";
                                                                            if (((Locale) locale).getLanguage().equals(LocaleManager.currentLang())) selected = "selected";
                                                                        %>
                                                                        <option <%=selected%> value="<%=((Locale)locale).toString()%>">
                                                                            <%=StringUtils.capitalize(((Locale)locale).getDisplayName((Locale)locale))%>
                                                                        </option>
                                                                    </mvc:fragmentValue>
                                                                </mvc:fragmentValue>
                                                            </mvc:fragment>
                                                            <mvc:fragment name="outputEnd">
                                                                </select>
                                                            </mvc:fragment>
                                                        </mvc:formatter>

                                                        <mvc:formatter name="org.jbpm.formModeler.service.mvc.formatters.ForFormatter">
                                                            <mvc:formatterParam name="bean" value="org.jbpm.formModeler.service.LocaleManager"/>
                                                            <mvc:formatterParam name="property" value="platformAvailableLocales"/>
                                                            <mvc:fragment name="output">
                                                                <mvc:fragmentValue name="index" id="index">
                                                                    <mvc:fragmentValue name="element" id="locale">
                                                                                <%
                                                                                    boolean selected =  ((Locale) locale).getLanguage().equals(LocaleManager.currentLang());

                                                                                    if ((readonly != null && !((Boolean) readonly).booleanValue()) && ((disabled != null) && !(((Boolean) disabled).booleanValue()))) {
                                                                                %>
                                                                                <textarea id="<%=name%>_<%=((Locale)locale).toString()%>"  name="<%=name%>_<%=((Locale)locale).toString()%>"
                                                                                    rows="<%=height != null ? height : "4"%>"
                                                                                    cols="<%=size != null ? size : "50"%>"
                                                                                    class="skn-input"
                                                                                    onchange="processFormInputChange(this)"
                                                                                    style="<%=StringUtils.defaultString((String)cssStyle)%>"
                                                                                    <%=title != null ? ("title=\"" + title + "\"") : ""%>
                                                                                    <%=maxlength != null ? " maxlength=\"" + maxlength + "\"" : ""%>
                                                                                    <%=tabindex != null ? " tabindex=\"" + tabindex + "\"" : ""%>
                                                                                    <%=accesskey != null ? " accesskey=\"" + accesskey + "\"" : ""%>
                                                                                    <%=altvalue != null ? " alt=\"" + altvalue + "\"" : ""%>
                                                                                    <%=readonly != null && ((Boolean) readonly).booleanValue() ? " readonly " : ""%>
                                                                                    <%=disabled != null && ((Boolean) disabled).booleanValue() ? " disabled " : ""%>><%=StringEscapeUtils.escapeHtml(val == null ? "" : StringUtils.defaultString(((HTMLi18n)val).getValue(((Locale)locale).toString())))%></textarea>

                                                                                <%
                                                                                        if (!selected) {
                                                                                %>
                                                                                <script type="text/javascript" defer="defer">
                                                                                    $('#<%=name%>_<%=((Locale)locale).toString()%>').hide();
                                                                                </script>
                                                                                <%
                                                                                        }
                                                                                    } else {
                                                                                %>
                                                                                <div id="<%=name+HTMLi18nFieldHandler.DIV_INPUT_NAME_PREFFIX%>" style="width:<%=size!=null?size:"250"%>px; height:<%=height!=null?height:"170"%>px"
                                                                                     class="dynInputStyle <%=StringUtils.defaultString((String) styleclass)%>"
                                                                                        <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                                                                                        <%=title != null ? ("title=\"" + title + "\"") : ""%>
                                                                                        >
                                                                                    <%=StringUtils.defaultString(val == null ? "" : ((HTMLi18n)val).getValue(((Locale)locale).toString()))%>
                                                                                </div>
                                                                                <input type="hidden" name="<%=name%>" value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(val == null ? "" : ((HTMLi18n)val).getValue(((Locale)locale).toString())))%>"/>
                                                                                <%
                                                                                    }
                                                                                %>
                                                                        </mvc:fragmentValue>
                                                                </mvc:fragmentValue>
                                                            </mvc:fragment>
                                                        </mvc:formatter>
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
</mvc:fragmentValue>
</mvc:fragment>
</mvc:formatter>
<%}catch(Throwable t){System.out.println("Error showing HTMLi18n input "+t);t.printStackTrace();}%>
