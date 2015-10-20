<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.CreateDynamicObjectFieldFormatter" %>
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
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

<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>


<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>

<%try {%>
<mvc:formatter name="CreateDynamicObjectFieldFormatter">
    <mvc:formatterParam name="<%=CreateDynamicObjectFieldFormatter.PARAM_DISPLAYPAGE%>" value="true"/>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <mvc:fragmentValue name="styleclass" id="styleclass">
        <mvc:fragmentValue name="cssStyle" id="cssStyle">
        <mvc:fragmentValue name="heightDesired" id="heightDesired">
        <div
        <%=styleclass!=null && ((String)styleclass).trim().length()>0 ? " class=\""+styleclass+"\"":""%>
        style="width:100%; <%=cssStyle!=null ? cssStyle:""%>"
            >
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="tableStart">
        <mvc:fragmentValue name="className" id="className">
        <table class="<%=className%>" width="100%" cellspacing="1" cellpadding="1">
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="headerStart">
        <tr class="skn-table_header">
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputColumnName">
        <th>
            <mvc:fragmentValue name="colLabel"/>
        </th>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="headerEnd">
        </tr>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="previewRow">
        <mvc:fragmentValue name="form" id="form">
        <mvc:fragmentValue name="namespace" id="namespace">
        <mvc:fragmentValue name="formValues" id="formValues">
                        <tr>
                            <td valign="top">
                                <table align="left" border="0"  width="100%" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <td>
            <mvc:formatter name="FormRenderingFormatter">
                <mvc:formatterParam name="form" value="<%=form%>"/>
                <mvc:formatterParam name="namespace" value="<%=namespace%>"/>
                <mvc:formatterParam name="renderMode" value="<%=Form.RENDER_MODE_DISPLAY%>"/>
                <mvc:formatterParam name="formValues" value="<%=formValues%>"/>
                <mvc:formatterParam name="reuseStatus" value="false"/>
                <%@ include file="/formModeler/components/WysiwygFormEdit/menu/defaultFormRenderingFormatterOptions.jsp" %>
            </mvc:formatter>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="tableRow">
        <mvc:fragmentValue name="index" id="index">
        <mvc:fragmentValue name="namespace" id="namespace">
        <mvc:fragmentValue name="form" id="form">
        <mvc:fragmentValue name="formValues" id="formValues">
            <mvc:formatter name="FormRenderingFormatter">
                <mvc:formatterParam name="form" value="<%=form%>"/>
                <mvc:formatterParam name="renderMode" value="<%=Form.RENDER_MODE_DISPLAY%>"/>
                <mvc:formatterParam name="displayMode" value="default"/>
                <mvc:formatterParam name="formValues" value="<%=formValues%>"/>
                <mvc:formatterParam name="namespace" value="<%=namespace%>"/>
                <mvc:formatterParam name="reuseStatus" value="false"/>
                <mvc:formatterParam name="labelMode" value="<%=Form.LABEL_MODE_HIDDEN%>"/>
                <mvc:fragment name="outputStart">
                    <tr class="<%=((Integer) index).intValue() % 2 == 1 ? "skn-even_row" : "skn-odd_row"%>">
                </mvc:fragment>
                <mvc:fragment name="beforeField"><td valign="top"></mvc:fragment>
                <mvc:fragment name="afterField"></td></mvc:fragment>
                <mvc:fragment name="outputEnd"></tr></mvc:fragment>
            </mvc:formatter>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="tableEnd">
        </table>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        </div>
    </mvc:fragment>
    <mvc:fragment name="noShowDataForm">

        <span class="skn-error">
            <i18n:message key="noShowForm">
            !!Undefined form to show!
            </i18n:message>
        </span>

    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="separator">
                        <tr align="center">
                            <td colspan="<mvc:fragmentValue name="colspan"/>">
                                 <mvc:fragmentValue name="separator"/>
                            </td>
                        </tr>
    </mvc:fragment>
    <%----------------------------------------------------------------------------------------------------%>
</mvc:formatter>
<%} catch (Throwable t) {
    System.out.println("Error showing CreateDynamicObject " + t);
    t.printStackTrace();
}%>
