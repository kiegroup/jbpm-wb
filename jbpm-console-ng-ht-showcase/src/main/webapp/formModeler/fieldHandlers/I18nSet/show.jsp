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
<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ page import="org.jbpm.formModeler.api.model.wrappers.I18nSet" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.Locale" %>

<%try {%>
<mvc:formatter name="SimpleFieldHandlerFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="name" id="name">
        <mvc:fragmentValue name="value" id="val">
        <mvc:fragmentValue name="title" id="title">
        <mvc:fragmentValue name="localeManager" id="localeManager">
        <mvc:fragmentValue name="styleclass" id="styleclass">
        <mvc:fragmentValue name="cssStyle" id="cssStyle">
        <mvc:fragmentValue name="isHTML" id="isHTML">
            <span <%=styleclass != null && ((String) styleclass).trim().length() > 0 ? " class=\"" + styleclass + "\"" : ""%> <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                  <%=title != null ? ("title=\"" + title + "\"") : ""%>>
                  <%= (isHTML!=null && ((Boolean)isHTML).booleanValue()) ? StringUtils.defaultString((String)((LocaleManager) localeManager).localize(((I18nSet) val).asMap())) :
                  StringEscapeUtils.escapeHtml4((val != null && !"".equals(val)) ? StringUtils.defaultString(((LocaleManager) localeManager).localize(((I18nSet) val).asMap())==null ? "" : ((LocaleManager) localeManager).localize(((I18nSet) val).asMap()).toString()) : "")%>
            </span>
            <mvc:formatter name="org.jbpm.formModeler.service.mvc.formatters.ForFormatter">
                <mvc:formatterParam name="bean" value="org.jbpm.formModeler.service.LocaleManager"/>
                    <mvc:formatterParam name="property" value="platformAvailableLocales"/>
                    <mvc:fragment name="output">
                        <mvc:fragmentValue name="index" id="index">
                        <mvc:fragmentValue name="element" id="locale">
                        <input type="hidden" name="<%=name%>_<%=((Locale) locale).toString()%>"
                            value='<%=StringEscapeUtils.escapeHtml4((val != null && !"".equals(val)) ? StringUtils.defaultString(((I18nSet) val).getValue(((Locale) locale).toString())) : "" )%>'/>
                        </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragment>
            </mvc:formatter>
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
    System.out.println("Error showing I18nSet " + t);
    t.printStackTrace();
}%>
