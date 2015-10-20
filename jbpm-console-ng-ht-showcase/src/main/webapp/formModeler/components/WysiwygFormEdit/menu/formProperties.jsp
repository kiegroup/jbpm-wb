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
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="EditFormFormatter">
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="saveForm"/>">
        <factory:handler action="saveCurrentForm"/>
        <div class="LeftColumnProperties">
        <table>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputNameInput">
        <tr>
            <td>
                <b><i18n:message key="name">!!!Nombre</i18n:message>:</b><br>
                <input name="name" type="text" class="skn-input"
                       value="<mvc:fragmentValue name="formName"/>"
                       size="20" maxlength="64">
            </td>
        </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputDisplayModeStart">

        <tr>
            <td>
                <br><b><i18n:message key="displayMode">
                !!! displayMode mode
            </i18n:message>:</b><br><br>
                <script type="text/javascript">

                    function show_Label_locationSelect(show) {
                        if (show) $('#labelModeDiv').show();
                        else $('#labelModeDiv').hide();
                    }

                </script>
            </td>
        </tr>
        <tr>
            <td>
                <input
                    <mvc:fragmentValue name="checked"/> type="radio" id='<factory:encode name="editTemplateCheckbox"/>'
                                                        name="displayMode" value="<%=Form.DISPLAY_MODE_ALIGNED%>"
                                                        onclick="show_Label_locationSelect(true)">
                <i18n:message key="displayMode.predefined">!!!Predefined</i18n:message>
            </td>
        </tr>
        <tr><td><div id="labelModeDiv" style="padding-left: 15">
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputAlignedDisplayMode">
        <mvc:fragmentValue name="showLabel" id="showLabel">
            <input
                <mvc:fragmentValue name="checked"/> type="checkbox"
                                                    name="displayModeAligned" value="aligned">
            <i18n:message key="displayMode.aligned">!!!Aligned </i18n:message>
        </mvc:fragmentValue>

    </mvc:fragment>

    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputLabelModeStart">
        <br>
        <i18n:message key="labelMode">
            !!! LabelMode:
        </i18n:message>:
        <br>
        <select class="skn-input" name="labelMode" id="labelMode">
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputLabelMode">
        <mvc:fragmentValue name="labelMode" id="labelMode">
            <option value="<%=labelMode%>">
                <i18n:message key='<%="labelMode."+labelMode%>'><%=labelMode%>
                </i18n:message>
            </option>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputLabelModeSelected">
        <mvc:fragmentValue name="labelMode" id="labelMode">
            <option selected class="skn-important" value="<%=labelMode%>">
                <i18n:message key='<%="labelMode."+labelMode%>'><%=labelMode%>
                </i18n:message>
            </option>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputLabelModeEnd">
        <mvc:fragmentValue name="showLabel" id="showLabel">

            </select>
            <script type="text/javascript">
                show_Label_locationSelect(<%=showLabel%>);
            </script>

        </mvc:fragmentValue>

    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputDisplayModeEnd">
        </div>
        </td>
        </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputTemplateDisplayMode">
        <mvc:fragmentValue name="showLabel" id="showLabel">
            <tr>
                <td>
                    <input
                        <mvc:fragmentValue name="checked"/> type="radio"
                                                            id='<factory:encode name="editTemplateCheckbox"/>'
                                                            name="displayMode" value="template"
                                                            onclick="show_Label_locationSelect(false)">
                    <i18n:message key="displayMode.template">!!!Template</i18n:message>

                    <input type="hidden" name="editTemplate" value="false">
                    <input type="image"
                           onclick="var chk = document.getElementById('<factory:encode name="editTemplateCheckbox"/>');
                                   chk.checked=true; chk.form.editTemplate.value='true';"
                           src="<static:image relativePath="general/16x16/ico-actions_edit.png"/>"
                           title="<i18n:message key="edit">!!!Edit</i18n:message>"
                           alt="<i18n:message key="edit">!!!Edit</i18n:message>"
                           border="0"
                            />
                </td>
            </tr>
        </mvc:fragmentValue>
    </mvc:fragment>

    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        <tr>
            <td>
                <table cellpadding="1" cellspacing="0" border="0" width="100%">
                    <tr>
                        <td align="center" style="height:30px" nowrap><br>
                            <input id="<factory:encode name="saveFormSubmit"/>" type="submit"
                                   class="skn-button" value="<i18n:message key="save">!!!Save</i18n:message>"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        </table>
        </div>
        </form>

    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
</mvc:formatter>
<script defer>
    setAjax("<factory:encode name="saveForm"/>");
</script>