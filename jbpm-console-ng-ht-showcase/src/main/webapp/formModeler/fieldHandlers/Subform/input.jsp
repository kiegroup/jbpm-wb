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
<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="i18n" uri="http://jakarta.apache.org/taglibs/i18n-1.0" %>


<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.core.processing.fieldHandlers.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<%try {%>
<mvc:formatter name="SubformFormatter">
    <mvc:formatterParam name="formMode" value='<%=request.getAttribute("formMode")%>'/>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <mvc:fragmentValue name="styleclass" id="styleclass">
            <mvc:fragmentValue name="cssStyle" id="cssStyle">
        <table  cellspacing="0" cellpadding="0"
                class="dynInputStyle <%=StringUtils.defaultString((String) styleclass)%>"
                style="width:100%;<%=StringUtils.defaultString((String) cssStyle)%>">
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputForm">
        <tr><td>
        <mvc:fragmentValue name="form" id="form">
        <mvc:fragmentValue name="namespace" id="namespace">
        <mvc:fragmentValue name="uid" id="uid">
        <mvc:fragmentValue name="name" id="name">
        <mvc:fragmentValue name="formValues" id="formValues">
        <mvc:fragmentValue name="renderMode" id="renderMode">
        <mvc:fragmentValue name="readonly" id="readonly">
                                    <mvc:formatter name="FormRenderingFormatter">
                                        <mvc:formatterParam name="form" value="<%=form%>"/>
                                        <mvc:formatterParam name="namespace" value="<%=namespace%>"/>
                                        <mvc:formatterParam name="renderMode" value="<%=renderMode%>"/>
                                        <mvc:formatterParam name="formValues" value="<%=formValues%>"/>
                                        <mvc:formatterParam name="isReadonly" value="<%=readonly%>"/>
                                        <mvc:formatterParam name="isMultipleSubForm" value="false"/>
                                        <mvc:formatterParam name="isSubForm" value="true"/>
                                        <%@ include file="/formModeler/components/WysiwygFormEdit/menu/defaultFormRenderingFormatterOptions.jsp" %>
                                    </mvc:formatter>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </td></tr>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="renderError">
        <mvc:fragmentValue name="error" id="error">
            <tr>
                <td>
                <span class="skn-error">
                    <i18n:message key="<%=(String)error%>">!!!<%=error%></i18n:message>
                </span>
                </td>
            </tr>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="noFormError">
        <mvc:fragmentValue name="errorMsg" id="errorMsg">
        <tr><td>
        <span class="skn-error">
            <i18n:message key="<%=(String)errorMsg%>">
            !!!Undefined form <%=errorMsg%>
            </i18n:message>
        </span>
        </td></tr>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        </table>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
</mvc:formatter>
<%} catch (Throwable t) {
    System.out.println("Error showing Subform input " + t);
    t.printStackTrace();
}%>

