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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="factory.tld" prefix="factory" %>

<factory:useComponent bean="org.jbpm.formModeler.service.bb.mvc.components.handling.MessagesComponentHandler"/>

<%--

<i18n:bundle baseName="org.jbpm.formModeler.core.processing.formRendering.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<mvc:formatter name="org.jbpm.formModeler.core.processing.formRendering.FormErrorsFormatter">
    <mvc:fragment name="outputStart">
        <div style="background-color: #f5f5dc; margin: 0px; width:100%;">
            <table border="0">
                <tr>
                    <td valign="top">
                        <img src="<mvc:context uri="/formModeler/images/32x32/messages/warning.gif"/>" border="0"/>
                    </td>
                    <td>
    </mvc:fragment>
    <mvc:fragment name="outputErrorsStart">
                        <table>
    </mvc:fragment>
    <mvc:fragment name="outputError">
        <mvc:fragmentValue name="namespace" id="namespace">
        <mvc:fragmentValue name="index" id="index">
                            <tr id="<%= "tr_" + namespace + "_" + index%>"
                                style="display:<mvc:fragmentValue name="display"/>">
                                <td class="skn-error">
                                    <mvc:fragmentValue name="errorMsg"/>
                                </td>
                            </tr>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputErrorsEnd">
                        </table>
                    </td>
    </mvc:fragment>
    <mvc:fragment name="outputDisplayLinks">
        <mvc:fragmentValue name="namespace" id="namespace">
        <mvc:fragmentValue name="min" id="min">
        <mvc:fragmentValue name="max" id="max">
                    <td valign="bottom">
                        <a href="#" id="<%="link_"+namespace+"_show"%>" onclick="showErrorMessages('<%=namespace%>', true, <%=min%>, <%=max%>); return false">
                            <i18n:message key="show">!!! ver mas</i18n:message>
                        </a>
                        <a href="#" style="display:none;" id="<%="link_"+namespace+"_hide"%>" onclick="showErrorMessages('<%=namespace%>', false, <%=min%>, <%=max%>); return false">
                            <i18n:message key="hide">!!! esconder</i18n:message>
                        </a>
                    </td>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
                </tr>
            </table>
        </div>
    </mvc:fragment>
</mvc:formatter>
--%>