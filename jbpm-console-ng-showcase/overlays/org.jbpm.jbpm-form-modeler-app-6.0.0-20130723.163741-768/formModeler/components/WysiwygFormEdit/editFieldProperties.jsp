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
<%@ page import="org.jbpm.formModeler.service.LocaleManager"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="org.jbpm.formModeler.components.editor.WysiwygFormEditor" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="factory.tld" prefix="factory" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>

<%
    String editionNamespace = (String)request.getAttribute("editionNamespace");
%>
<mvc:formatter name="FieldEditionFormatter">

    <mvc:formatterParam name="namespace" value="<%=editionNamespace%>"/>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <mvc:fragmentValue name="fieldName" id="fieldName">
            <div id="<factory:encode name="fieldProperties"/>">
            <form action="<factory:formUrl/>" id="<factory:encode name="updateFormField"/>" method="POST" enctype="multipart/form-data">
            <factory:handler bean="org.jbpm.formModeler.components.editor.WysiwygFormEditor" action="saveFieldProperties"/>
            <input type="hidden" name="<%=WysiwygFormEditor.ACTION_TO_DO%>" id="<factory:encode name="actionToDo"/>" value="<%=WysiwygFormEditor.ACTION_SAVE_FIELD_PROPERTIES%>"/>

            <table border="0" class="EditFieldProperties">
            <tr>
                <td align="left" colspan="3">
                    <div class="headerEditFP">
                        <input type="image" onclick="$('#<factory:encode name="actionToDo"/>').val('<%=WysiwygFormEditor.ACTION_CANCEL_FIELD_EDITION%>');this.onclick=function(){return false;}" style="cursor:hand; float: left; margin-right: 10px; margin-left: 5px;" src="<static:image relativePath="actions/close.png"/>"><i18n:message key="properties">Properties</i18n:message> (<%=StringEscapeUtils.escapeHtml((String) fieldName)%>)
                    </div>
                </td>
            </tr>
            <tr>
            <td colspan="3">
            <table class="FormFieldProperties">
            <tr>
                <td><i18n:message key="fieldType">!!!Tipo de campo</i18n:message></td>
            </tr>
            <tr>
                <td colspan="3">
                    <mvc:formatter name="FieldAvailableTypesFormatter">
                        <mvc:fragment name="outputStart">
                            <select name="fieldType" class="skn-input" style="width:200px" onchange="$('#<factory:encode name="actionToDo"/>').val('<%=WysiwygFormEditor.ACTION_CHANGE_FIELD_TYPE%>'); submitAjaxForm(this.form);">
                        </mvc:fragment>
                        <mvc:fragment name="output">
                            <mvc:fragmentValue name="id" id="id">
                                <option value="<%=id%>"><i18n:message key='<%="fieldType." + id%>'/></option>
                            </mvc:fragmentValue>
                        </mvc:fragment>
                        <mvc:fragment name="outputSelected">
                            <mvc:fragmentValue name="id" id="id">
                                <option value="<%=id%>" selected><i18n:message key='<%="fieldType." + id%>'/></option>
                            </mvc:fragmentValue>
                        </mvc:fragment>
                        <mvc:fragment name="outputEnd">
                            </select>
                        </mvc:fragment>
                        <mvc:fragment name="empty">
                            <input type="hidden" name="fieldType" value="<mvc:fragmentValue name="id"/>">
                        </mvc:fragment>
                    </mvc:formatter>
                </td>
                <td></td>
            </tr>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="fieldCustomForm">
        <mvc:fragmentValue name="formId" id="formId">
            <mvc:fragmentValue name="namespace" id="formNamespace">
                <mvc:fragmentValue name="editClass" id="editClass">
                    <mvc:fragmentValue name="editId" id="editId">
                        <mvc:fragmentValue name="fieldType" id="fieldType">

                                <mvc:formatter name="FormRenderingFormatter">
                                    <mvc:formatterParam name="formId" value="<%=formId%>"/>
                                    <mvc:formatterParam name="namespace" value="<%=formNamespace%>"/>
                                    <mvc:formatterParam name="editId" value="<%=editId%>"/>
                                    <mvc:formatterParam name="editClass" value="<%=editClass%>"/>
                                    <mvc:fragment name="outputStart">
                                    </mvc:fragment>
                                    <mvc:fragment name="groupStart">
                                        <tr>
                                        <td colspan="3">
                                        <table border="0" cellpadding="0" cellspacing="0">
                                        <tr>
                                    </mvc:fragment>
                                    <mvc:fragment name="groupEnd">
                                        </tr>
                                        </table >
                                        </td>
                                        </tr>
                                    </mvc:fragment>
                                    <mvc:fragment name="beforeInputElement">
                                        <td>
                                        <table border="0" cellpadding="0" cellspacing="0" >
                                        <tr>
                                    </mvc:fragment>

                                    <mvc:fragment name="beforeLabel"><td valign="top" colspan="2"></mvc:fragment>
                                    <mvc:fragment name="afterLabel"></td></mvc:fragment>
                                    <mvc:fragment name="lineBetweenLabelAndField">
                                        </tr>
                                        <tr>
                                    </mvc:fragment>
                                    <mvc:fragment name="beforeField">
                                        <mvc:fragmentValue name="field" id="field">
                                            <td>
                                        </mvc:fragmentValue>
                                    </mvc:fragment>
                                    <mvc:fragment name="afterField">
                                        <mvc:fragmentValue name="field" id="field">
                                            <mvc:fragmentValue id="fieldPosition" name="field/position">
                                                </td>
                                                <td>
                                                    <mvc:formatter name="FieldPropertyTooltipFormatter">
                                                        <mvc:formatterParam name="field" value="<%=field%>"/>
                                                        <mvc:fragment name="output">
                                                            <mvc:fragmentValue name="help" id="help">
                                                                <img src="<static:image relativePath="general/16x16/ico-info.png"/>"
                                                                     id='<%="tooltip_" + fieldPosition%>' border="0" title="<%=StringEscapeUtils.unescapeHtml((String)help)%>"/>
                                                                <script type="text/javascript" defer="defer">
                                                                    $(function() {
                                                                        $('#<%="tooltip_" + fieldPosition%>').tooltip();
                                                                    });
                                                                </script>
                                                            </mvc:fragmentValue>
                                                        </mvc:fragment>
                                                    </mvc:formatter>
                                                </td>
                                            </mvc:fragmentValue>
                                        </mvc:fragmentValue>
                                    </mvc:fragment>
                                    <mvc:fragment name="afterInputElement">
                                        </tr>
                                        </table></td>
                                    </mvc:fragment>
                                    <mvc:fragment name="outputEnd">
                                    </mvc:fragment>
                                </mvc:formatter>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputName">
        <mvc:fragmentValue name="index" id="index">
            <mvc:fragmentValue name="name" id="name">
                <tr class="<%=((Integer) index).intValue() % 2 == 0 ? "skn-even_row" : "skn-odd_row"%>">
                <td>
                    <i18n:message key='<%="field."+name%>'><%=name%></i18n:message>
                </td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="beforeDefaultValue">
        <td>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="afterDefaultValue">
        </td>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="beforeInput">
        <td>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="afterInput">
        </td>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="cantShowInput">
        <td>-</td>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="errorShowingInput">
        <td colspan="2"></td>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputNameEnd">
        </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        <mvc:fragmentValue name="fieldName" id="fieldName">
            <tr>
                <td align="center" colspan="3">
                    <table>
                        <tr>
                            <td><input type="submit" value="<i18n:message key="save"> !!!Save </i18n:message>" class="skn-button"
                                       onclick="$('#<factory:encode name="actionToDo"/>').val('<%=WysiwygFormEditor.ACTION_SAVE_FIELD_PROPERTIES%>');"></td>
                            <td><input type="submit" value="<i18n:message key="cancel"> !!!Cancel </i18n:message>" class="skn-button_alt"
                                       onclick="$('#<factory:encode name="actionToDo"/>').val('<%=WysiwygFormEditor.ACTION_CANCEL_FIELD_EDITION%>');"></td>
                        </tr>
                    </table>
                </td>
            </tr>
            </table>
            </td>
            </tr>
            </table>

            </form>
            </div>
            <script type="text/javascript" defer="defer">
                setAjax("<factory:encode name="updateFormField"/>");
            </script>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>
