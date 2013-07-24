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
<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.CreateDynamicObjectFieldFormatter" %>
<%@ page import="org.jbpm.formModeler.core.processing.FormProcessor" %>
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>


<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.core.processing.fieldHandlers.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%try {%>

<mvc:formatter name="CreateDynamicObjectFieldFormatter">
    <mvc:formatterParam name="<%=CreateDynamicObjectFieldFormatter.PARAM_DISPLAYPAGE%>" value="false"/>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <mvc:fragmentValue name="styleclass" id="styleclass">
            <mvc:fragmentValue name="cssStyle" id="cssStyle">
                <mvc:fragmentValue name="uid" id="uid">
                    <mvc:fragmentValue name="count" id="count">
                        <mvc:fragmentValue name="tableEnterMode" id="tableEnterMode">
                            <mvc:fragmentValue name="name" id="name">
        <input type="hidden" id='<%=uid + "_index"%>' name='<%=uid + "_index"%>' value="">
        <input type="hidden" id='<%=uid + "_child_uid_value"%>' name="child_uid_value" value="">
        <input type="hidden" id='<%=uid + "_parentFormId"%>' name="<%=uid + "_parentFormId"%>" value="">
        <input type="hidden" id='<%=uid + "_parentNamespace"%>' name="<%=uid + "_parentNamespace"%>" value="">
        <input type="hidden" id='<%=uid + "_field"%>' name='<%=uid + "_field"%>' value="">

        <input type="hidden" id="<%=uid%>_tableEnterMode" name='<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "tableEnterMode"%>' value="<%=tableEnterMode%>">
        <input type="hidden" id="<%=uid%>_count" name='<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "count"%>' value="<%=count%>">

        <table cellpadding="0" cellspacing="0" class="dynInputStyle <%=StringUtils.defaultString((String) styleclass)%>" style='width:100%; <%=cssStyle!=null ? cssStyle:""%>'>
                                </mvc:fragmentValue>
                            </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="previewItem">
    <mvc:fragmentValue name="form" id="form">
        <mvc:fragmentValue name="valueToPreview" id="valueToPreview">
            <mvc:fragmentValue name="index" id="index">
                <mvc:fragmentValue name="parentFormId" id="parentFormId">
                    <mvc:fragmentValue name="namespace" id="namespace">
                        <mvc:fragmentValue name="parentNamespace" id="parentNamespace">
                            <mvc:fragmentValue name="field" id="field">
                                <mvc:fragmentValue name="uid" id="uid">
                                    <mvc:fragmentValue name="disabled" id="disabled">
                                        <mvc:fragmentValue name="readonly" id="readonly">
                                            <tr>
                                                <td>
                                                    <table  width="100%" cellspacing="1" cellpadding="1">
                                                        <tr>
                                                            <td>
                                                                <mvc:formatter name="FormRenderingFormatter">
                                                                    <mvc:formatterParam name="form" value="<%=form%>"/>
                                                                    <mvc:formatterParam name="renderMode" value="<%=Form.RENDER_MODE_DISPLAY%>"/>
                                                                    <mvc:formatterParam name="reuseStatus" value="false"/>
                                                                    <mvc:formatterParam name="namespace" value="showItemPreview"/>
                                                                    <mvc:formatterParam name="formValues" value="<%=valueToPreview%>"/>
                                                                    <mvc:formatterParam name="isDisabled" value="<%=disabled%>"/>
                                                                    <mvc:formatterParam name="isReadonly" value="<%=readonly%>"/>
                                                                    <%@ include file="/formModeler/components/WysiwygFormEdit/menu/defaultFormRenderingFormatterOptions.jsp" %>
                                                                </mvc:formatter>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td align="center" style="padding-top:10px">
                                                                <input type="button" class="skn-button_alt"
                                                                       value="<i18n:message key="return">!!!Return</i18n:message>"
                                                                       onclick="
                                                                               document.getElementById('<%=uid + "_child_uid_value"%>').value='<%=uid%>';
                                                                               document.getElementById('<%=uid + "_index"%>').value='<%=index%>';
                                                                               document.getElementById('<%=uid + "_parentFormId"%>').value='<%=parentFormId%>';
                                                                               document.getElementById('<%=uid + "_parentNamespace"%>').value='<%=parentNamespace%>';
                                                                               document.getElementById('<%=uid + "_field"%>').value='<%=field%>';
                                                                               clearChangeDDMTrigger();
                                                                               sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.SubFormSendHandler', 'cancelPreviewItem');">
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>

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
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="editItem">
    <mvc:fragmentValue name="form" id="form">
        <mvc:fragmentValue name="valueToEdit" id="valueToEdit">
            <mvc:fragmentValue name="index" id="index">
                <mvc:fragmentValue name="parentFormId" id="parentFormId">
                    <mvc:fragmentValue name="parentNamespace" id="parentNamespace">
                        <mvc:fragmentValue name="namespace" id="namespace">
                            <mvc:fragmentValue name="name" id="name">
                                <mvc:fragmentValue name="field" id="field">
                                    <mvc:fragmentValue name="uid" id="uid">
                                        <mvc:fragmentValue name="disabled" id="disabled">
                                            <mvc:fragmentValue name="readonly" id="readonly">

            <tr>
                <td>
                    <table width="100%" cellspacing="1" cellpadding="1">
                        <tr>
                            <td>
                                <mvc:formatter name="FormRenderingFormatter">
                                    <mvc:formatterParam name="form" value="<%=form%>"/>
                                    <mvc:formatterParam name="formValues" value="<%=valueToEdit%>"/>
                                    <mvc:formatterParam name="namespace" value="<%=namespace%>"/>
                                    <mvc:formatterParam name="isMultipleSubForm" value="true"/>
                                    <mvc:formatterParam name="isSubForm" value="true"/>
                                    <mvc:formatterParam name="isMultiple" value="true"/>
                                    <mvc:formatterParam name="renderMode" value="<%=Form.RENDER_MODE_FORM%>"/>
                                    <mvc:formatterParam name="isDisabled" value="<%=disabled%>"/>
                                    <mvc:formatterParam name="isReadonly" value="<%=readonly%>"/>
                                    <%@ include file="/formModeler/components/WysiwygFormEdit/menu/defaultFormRenderingFormatterOptions.jsp" %>
                                </mvc:formatter>
                            </td>
                        </tr>
                        <%
                            if(!Boolean.TRUE.equals(readonly) && !Boolean.TRUE.equals(disabled)) {
                        %>

                        <tr>
                            <td align="center" style="padding-top:10px">
                                <input type="hidden" name="<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "saveEdited"%>" value="false">
                                <input type="button" class="skn-button" value="<i18n:message key="save">!!!Save</i18n:message>"
                                       onclick="this.form.elements['<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "saveEdited"%>'].value=true;clearChangeDDMTrigger();sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.SubFormSendHandler', 'saveEditedItem');"
                                        >
                                <input type="button" class="skn-button_alt" value='<i18n:message key="return">!!!Return</i18n:message>'
                                       onclick="
                                               document.getElementById('<%=uid + "_child_uid_value"%>').value='<%=uid%>';
                                               document.getElementById('<%=uid + "_index"%>').value='<%=index%>';
                                               document.getElementById('<%=uid + "_parentFormId"%>').value='<%=parentFormId%>';
                                               document.getElementById('<%=uid + "_parentNamespace"%>').value='<%=parentNamespace%>';
                                               document.getElementById('<%=uid + "_field"%>').value='<%=field%>';
                                               clearChangeDDMTrigger();
                                               sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.SubFormSendHandler', 'cancelEditItem');">
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                </td>
            </tr>

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
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="tableStart">
    <mvc:fragmentValue name="className" id="className">
        <mvc:fragmentValue name="uid" id="uid">
            <tr>
            <td>
            <table class="<%=className%>" width="100%" cellspacing="1" cellpadding="1">
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="headerStart">
    <mvc:fragmentValue name="colspan" id="colspan">
        <tr class="skn-table_header">
        <%
            if(colspan!=null && ((Integer)colspan).intValue()>0) {
        %>
        <td colspan="<%=colspan%>" width="1px">
            <i18n:message key="actions">Actions!!!!!</i18n:message>
        </td>
        <%
            }
        %>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputColumnName">
    <td style="white-space: nowrap">
        <mvc:fragmentValue name="colLabel"/>
    </td>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="headerEnd">
    </tr>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputSubformActions">
    <mvc:fragmentValue name="modificable" id="modificable">
        <mvc:fragmentValue name="visualizable" id="visualizable">
            <mvc:fragmentValue name="deleteable" id="deleteable">
                <mvc:fragmentValue name="uid" id="uid">
                    <mvc:fragmentValue name="index" id="index">
                        <mvc:fragmentValue name="parentFormId" id="parentFormId">
                            <mvc:fragmentValue name="parentNamespace" id="parentNamespace">
                                <mvc:fragmentValue name="field" id="field">

                        <tr valign="top" class='<%=((Integer) index).intValue() % 2 == 1 ? "skn-even_row" : "skn-odd_row"%>'>
<%
    if (Boolean.TRUE.equals(deleteable)) {
%>
                            <td align="center" style="width:13px">
                                <a title='<i18n:message key="delete">!!!Delete</i18n:message>'
                                   href="#"
                                   onclick="
                                       if (confirm('<i18n:message key="delete.confirm">Sure?</i18n:message>')) {
                                           document.getElementById('<%=uid + "_child_uid_value"%>').value='<%=uid%>';
                                           document.getElementById('<%=uid + "_index"%>').value='<%=index%>';
                                           document.getElementById('<%=uid + "_parentFormId"%>').value='<%=parentFormId%>';
                                           document.getElementById('<%=uid + "_parentNamespace"%>').value='<%=parentNamespace%>';
                                           document.getElementById('<%=uid + "_field"%>').value='<%=field%>';
                                           clearChangeDDMTrigger();
                                           sendFormToHandler(document.getElementById('<%=uid + "_child_uid_value"%>').form, 'org.jbpm.formModeler.core.processing.fieldHandlers.SubFormSendHandler', 'deleteItem');
                                       }
                                       return false;"
                                   id="<%=uid%>_delete_<%=index%>">
                                    <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0">
                                </a>

                            </td>
<%
    }
    if (Boolean.TRUE.equals(visualizable)) {
%>
                            <td align="center" style="width:13px">
                                <a title='<i18n:message key="preview">!!!Preview</i18n:message>'
                                   href="#"
                                   onclick="
                                       document.getElementById('<%=uid + "_child_uid_value"%>').value='<%=uid%>';
                                       document.getElementById('<%=uid + "_index"%>').value='<%=index%>';
                                       document.getElementById('<%=uid + "_parentFormId"%>').value='<%=parentFormId%>';
                                       document.getElementById('<%=uid + "_parentNamespace"%>').value='<%=parentNamespace%>';
                                       document.getElementById('<%=uid + "_field"%>').value='<%=field%>';
                                       clearChangeDDMTrigger();
                                       sendFormToHandler(document.getElementById('<%=uid + "_child_uid_value"%>').form, 'org.jbpm.formModeler.core.processing.fieldHandlers.SubFormSendHandler', 'previewItem');
                                       return false;"
                                   id="<%=uid%>_preview_<%=index%>">
                                    <img src="<static:image relativePath="general/16x16/preview.png"/>" border="0">
                                </a>
                            </td>
<%
    }
    if (Boolean.TRUE.equals(modificable)) {
%>
                            <td align="center" style="width:13px">
                                <a title="<i18n:message key="edit">!!!Edit</i18n:message>"
                                   href="#"
                                   onclick="
                                       document.getElementById('<%=uid + "_child_uid_value"%>').value='<%=uid%>';
                                       document.getElementById('<%=uid + "_index"%>').value='<%=index%>';
                                       document.getElementById('<%=uid + "_parentFormId"%>').value='<%=parentFormId%>';
                                       document.getElementById('<%=uid + "_parentNamespace"%>').value='<%=parentNamespace%>';
                                       document.getElementById('<%=uid + "_field"%>').value='<%=field%>';
                                       clearChangeDDMTrigger();
                                       sendFormToHandler(document.getElementById('<%=uid + "_child_uid_value"%>').form, 'org.jbpm.formModeler.core.processing.fieldHandlers.SubFormSendHandler', 'editItem');
                                       return false;"
                                   id="<%=uid%>_edit_<%=index%>">
                                    <img src="<static:image relativePath="general/16x16/ico-edit.png"/>" border="0">
                                </a>
                            </td>
<%
    }
%>        </mvc:fragmentValue>
                            </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="tableRow">
    <mvc:fragmentValue name="namespace" id="namespace">
        <mvc:fragmentValue name="form" id="form">
            <mvc:fragmentValue name="formValues" id="formValues">
                <mvc:fragmentValue name="disabled" id="disabled">
                    <mvc:fragmentValue name="readonly" id="readonly">
                        <mvc:fragmentValue name="renderMode" id="renderMode">
                            <mvc:fragmentValue name="labelMode" id="labelMode">
                                <mvc:formatter
                                        name="FormRenderingFormatter">
                                    <%-- Formatter for table row, cannot use default rendering options --%>
                                    <mvc:formatterParam name="form" value="<%=form%>"/>
                                    <mvc:formatterParam name="renderMode" value="<%=renderMode%>"/>
                                    <mvc:formatterParam name="displayMode" value="default"/>
                                    <mvc:formatterParam name="formValues" value="<%=formValues%>"/>
                                    <mvc:formatterParam name="namespace" value="<%=namespace%>"/>
                                    <mvc:formatterParam name="isDisabled" value="<%=disabled%>"/>
                                    <mvc:formatterParam name="isReadonly" value="<%=readonly%>"/>
                                    <mvc:formatterParam name="labelMode" value="<%=labelMode%>"/>
                                    <mvc:fragment name="outputStart"></mvc:fragment>
                                    <mvc:fragment name="beforeField"><td valign="top"></mvc:fragment>
                                    <mvc:fragment name="afterField"></td></mvc:fragment>
                                    <mvc:fragment name="outputEnd"></tr></mvc:fragment>
                                </mvc:formatter>
                            </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
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
<mvc:fragment name="tableEnd">
    </table><br>
    </td>
    </tr>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputEnterDataForm">
    <mvc:fragmentValue name="form" id="form">
        <mvc:fragmentValue name="namespace" id="namespace">
            <mvc:fragmentValue name="uid" id="uid">
                <mvc:fragmentValue name="name" id="name">
                    <mvc:fragmentValue name="fieldName" id="fieldName">
                        <mvc:fragmentValue name="entityName" id="entityName">
                            <mvc:fragmentValue name="expanded" id="expanded">
                                <mvc:fragmentValue name="noCancelButton" id="noCancelButton">
                                    <mvc:fragmentValue name="disabled" id="disabled">
                                        <mvc:fragmentValue name="readonly" id="readonly">
                                            <mvc:fragmentValue name="renderMode" id="renderMode">
                                                <tr>
                                                    <td>
                                                        <input type="hidden" id="<%=uid%>_expand" name="<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "expand"%>" value="leaveItAlone">
                                                        <input type="hidden" id="<%=uid%>_create" name="<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create"%>" value="leaveItAlone">
                                                        <%
                                                            if (Boolean.TRUE.equals(expanded)) {
                                                        %>
                                                        <table align="left" border="0"  width="100%" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <td>
                                                                    <mvc:formatter name="FormRenderingFormatter">
                                                                        <mvc:formatterParam name="form" value="<%=form%>"/>
                                                                        <mvc:formatterParam name="namespace" value="<%=namespace%>"/>
                                                                        <mvc:formatterParam name="isMultiple" value="true"/>
                                                                        <mvc:formatterParam name="isSubForm" value="true"/>
                                                                        <mvc:formatterParam name="isDisabled" value="<%=disabled%>"/>
                                                                        <mvc:formatterParam name="isReadonly" value="<%=readonly%>"/>
                                                                        <mvc:formatterParam name="renderMode" value="<%=renderMode%>"/>
                                                                        <%@ include file="/formModeler/components/WysiwygFormEdit/menu/defaultFormRenderingFormatterOptions.jsp" %>
                                                                    </mvc:formatter>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td>
                                                                    <table align="left" border="0"  width="100%">
                                                                        <tr>
                                                                            <td align="center" nowrap="nowrap" style="padding-top:10px">
                                                                                <input type="button" class="skn-button"
                                                                                       value="<mvc:fragmentValue name="newItemButtonText"/>"
                                                                                       onclick="this.disabled=true; document.getElementById('<%=uid%>_create').value=true;clearChangeDDMTrigger();sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.SubFormSendHandler', 'addItem');">
                                                                                <% if (!Boolean.TRUE.equals(noCancelButton)) { %>
                                                                                <input type="button" class="skn-button_alt"
                                                                                       value="<mvc:fragmentValue name="cancelButtonText"/>"
                                                                                       onclick="document.getElementById('<%=uid%>_create').value=false;document.getElementById('<%=uid%>_expand').value=false;clearChangeDDMTrigger();sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.SubFormSendHandler', 'expandSubform');"/>
                                                                                <% } %>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>

                                                        <%
                                                            }
                                                            if (!Boolean.TRUE.equals(expanded) && !Boolean.TRUE.equals(readonly) && !Boolean.TRUE.equals(disabled)) {
                                                        %>

                                                        <div style="text-align:center; padding-top:0px; width:100%;">
                                                            <input type="button" class="skn-button" value="<mvc:fragmentValue name="addItemButtonText"/>"
                                                                   onclick="this.disabled=true; document.getElementById('<%=uid%>_expand').value=true;clearChangeDDMTrigger();sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.SubFormSendHandler', 'expandSubform');"/>
                                                        </div>
                                                        <%
                                                            }
                                                        %>
                                                    </td>
                                                </tr>
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
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputEnd">
    </table>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
</mvc:formatter>
<%} catch (Throwable t) {
    System.out.println("Error showing CreateDynamicObject input " + t);
    t.printStackTrace();
}%>
