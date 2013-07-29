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
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.CheckBoxFieldHandler"%>
<%@ page import="org.jbpm.formModeler.service.LocaleManager"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.core.processing.fieldHandlers.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<table border="0" cellpadding="0" cellspacing="0" >
    <tr valign="top">
        <td>
<mvc:formatter name="SimpleFieldHandlerFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="title" id="title">
        <mvc:fragmentValue name="name" id="name">
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
                                                        <select name="<%=name%>Value"
                                                            id='<mvc:fragmentValue name="uid"/>'
                                                            <%=title!=null?("title=\""+title+"\""):""%>
                                                            <%=styleclass!=null && ((String)styleclass).trim().length()>0 ? " class=\""+styleclass+"\"":"class=\"skn-input\""%>
                                                            <%=tabindex!=null ? " tabindex=\""+tabindex+"\"":""%>
                                                            <%=accesskey!=null ? " accesskey=\""+accesskey+"\"":""%>
                                                            <%=alt!=null ? " alt=\""+alt+"\"":""%>
                                                            <%=cssStyle!=null ? " style=\""+cssStyle+"\"":""%>
                                                            <%=height!=null ? " height=\""+height+"\"":""%>
                                                            <%=readonly!=null && ((Boolean)readonly).booleanValue()? " readonly ":""%>
                                                            <%=disabled!=null && ((Boolean)disabled).booleanValue()? " disabled ":""%>
                                                            onchange="processFormInputChange(this);">
                                                            <option value="<%=CheckBoxFieldHandler.NULL_VALUE%>" <%=(value==null)? " selected " : ""%>></option>
                                                            <option value="true" <%=(value!=null && (new Boolean(String.valueOf(value))).booleanValue())?" selected ":" "%>>
                                                                <i18n:message key="checkbox.true">!!!!true</i18n:message>
                                                            </option>
                                                            <option value="false" <%=(value!=null && !(new Boolean(String.valueOf(value))).booleanValue())?" selected ":" "%>>
                                                                <i18n:message key="checkbox.false">!!!!false</i18n:message>
                                                            </option>
                                                        </select>
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
