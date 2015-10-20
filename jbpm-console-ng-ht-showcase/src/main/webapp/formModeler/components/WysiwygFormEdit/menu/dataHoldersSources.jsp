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
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ page import="org.jbpm.formModeler.components.editor.WysiwygFormEditor" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="DataHoldersFormFormatter">
<mvc:fragment name="outputStartHeader">
    <script type="text/javascript">
        var holderSources = {};
</mvc:fragment>

<mvc:fragment name="notifyHolderBuilder">
    holderSources['<mvc:fragmentValue name="id"/>'] = null;
</mvc:fragment>
<mvc:fragment name="notifyComboHolderBuilder">
        holderSources['<mvc:fragmentValue name="id"/>'] = <mvc:fragmentValue name="comboValues"/>;
</mvc:fragment>
<mvc:fragment name="outputEndHeader">
        function show_dataholderInfo(holderType) {

            var combo = $("#<%=WysiwygFormEditor.PARAMETER_HOLDER_COMBO_VALUE%>");
            var input = $("#<%=WysiwygFormEditor.PARAMETER_HOLDER_INPUT_VALUE%>");

            combo.val("");
            combo.hide();
            combo.empty();
            input.val("");
            input.hide();

            var options = holderSources[holderType];

            if (options) {
                jQuery.each(options, function(index, value) {
                    combo.append($('<option></option>').attr("value", value.key).text(value.value));
                });
                combo.show();
            } else {
                input.show();
            }
        }
    </script>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputStart">
    <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="formDataHolders"/>">
    <factory:handler action="formDataHolders"/>
    <input type="hidden" name="<%=WysiwygFormEditor.ACTION_TO_DO%>" id="<factory:encode name="actionToDo"/>"
           value="<%=WysiwygFormEditor.ACTION_ADD_DATA_HOLDER%>"/>
    <table width="100%" >
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputFormAddHolderStart">
    <mvc:fragmentValue name="existingInputIds" id="existingInputIds">
        <mvc:fragmentValue name="existingOutputIds" id="existingOutputIds">
            <mvc:fragmentValue name="existingIds" id="existingIds">
        <tr>
            <td class="LeftColumnProperties" align="center">
                <script type="text/javascript">

                    function confirmAdd(){
                        var existingInputIds = [<%=existingInputIds%>];
                        var existingOutputIds = [<%=existingOutputIds%>];
                        var existingIds = [<%=existingIds%>];

                        var inVal = $("#<%=WysiwygFormEditor.PARAMETER_HOLDER_INPUT_ID%>").val();
                        var idVal = $("#<%=WysiwygFormEditor.PARAMETER_HOLDER_ID%>").val();
                        var outVal = $("#<%=WysiwygFormEditor.PARAMETER_HOLDER_OUTPUT_ID%>").val();
                        if ((idVal && jQuery.inArray(idVal, existingIds)!=-1) ){
                            alert("<i18n:message key="dataHolder_existingId_Message">Sure?</i18n:message>")
                            return false;
                        }
                        if ((inVal && jQuery.inArray(inVal, existingInputIds)!=-1) ||
                                (outVal && jQuery.inArray(outVal, existingOutputIds)!=-1)){
                            alert("<i18n:message key="dataHolder_add_confirm">Sure?</i18n:message>")
                            return false;
                        }
                        if(!idVal){
                            alert("<i18n:message key="dataHolder_requiredId">required id!</i18n:message>")
                            return false;
                        }
                        if (!(inVal || outVal)){
                            alert("<i18n:message key="dataHolder_requiredInOut">required input output!</i18n:message>")
                            return false;
                        }

                    }
            </script>
            <table>
                <tr>
                    <td><b><i18n:message key="dataHolder_Id">!!!dataHolder_id</i18n:message>:</b></td>
                </tr>
                <tr>
                    <td><input name="<%=WysiwygFormEditor.PARAMETER_HOLDER_ID%>" type="text" class="skn-input" value=""
                               size="20" maxlength="64" id="<%=WysiwygFormEditor.PARAMETER_HOLDER_ID%>"></td>
                </tr>
                <tr>
                    <td><b><i18n:message key="dataHolder_inputId">!!!dataHolder_input</i18n:message>:</b></td>
                </tr>
                <tr>
                    <td><input name="<%=WysiwygFormEditor.PARAMETER_HOLDER_INPUT_ID%>" type="text" class="skn-input" value=""
                               size="20" maxlength="64" id="<%=WysiwygFormEditor.PARAMETER_HOLDER_INPUT_ID%>"></td>
                </tr>
                <tr>
                    <td><b><i18n:message key="dataHolder_outputId">!!!dataHolder_outputid</i18n:message>:</b></td>
                </tr>
                <tr>
                    <td><input name="<%=WysiwygFormEditor.PARAMETER_HOLDER_OUTPUT_ID%>" type="text" class="skn-input" value=""
                               size="20" maxlength="64" id="<%=WysiwygFormEditor.PARAMETER_HOLDER_OUTPUT_ID%>"></td>
                </tr>
                <tr>
                    <td><b><i18n:message key="dataHolder_renderColor">!!!dataHolder_renderColor</i18n:message>:</b></td>
                </tr>
                <tr>
                    <td>
                        <select class="skn-input" name="<%=WysiwygFormEditor.PARAMETER_HOLDER_RENDERCOLOR%>">
            </mvc:fragmentValue>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="color">
    <mvc:fragmentValue name="color" id="color">
    <mvc:fragmentValue name="name" id="name">
                            <option value="<%=color%>"><i18n:message key="<%=(String)name%>"><%=color%></i18n:message></option>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputFormHolderTypes">
                        </select>
                    </td>
                </tr>
                <tr>
                    <td><b><i18n:message key="dataHolder_type">!!!dataHolder_type</i18n:message>:</b></td>
                </tr>
                <tr>
                    <td>
</mvc:fragment>
<mvc:fragment name="outputHolderType">
    <mvc:fragmentValue name="holderType" id="holderType">
                        <input type="radio"
                               name="<%=WysiwygFormEditor.PARAMETER_HOLDER_TYPE%>"
                               id="<%="dataHolder_" + holderType%>"
                               value="<%=holderType%>"
                               onclick="show_dataholderInfo('<%=holderType%>')">&nbsp;<label for="<%="dataHolder_" + holderType%>"><mvc:fragmentValue name="holderName"/></label><br>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputEndHolderTypes">
                    </td>
                </tr>
                <tr>
                    <td><b><i18n:message key="dataHolder_info">!!!dataHolder_info</i18n:message>:</b></td>
                </tr>
                <tr>
                    <td valign="top">
                        <table cellpadding="0" cellspacing="0" border="0" width="100%" >
                            <tr>
                                <td>
                                    <select class="skn-input" id="<%=WysiwygFormEditor.PARAMETER_HOLDER_COMBO_VALUE%>" name="<%=WysiwygFormEditor.PARAMETER_HOLDER_COMBO_VALUE%>" style="display: none"></select>
                                    <input type="text" id="<%=WysiwygFormEditor.PARAMETER_HOLDER_INPUT_VALUE%>" name="<%=WysiwygFormEditor.PARAMETER_HOLDER_INPUT_VALUE%>"  class="skn-input" value="" size="20" maxlength="64" style="display: none">
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>

            <br>

            <div style="text-align: center;">
                <input type="submit"
                       value="<i18n:message key="dataHolder_addDataHolder">!!! dataHolder_addDataHolder</i18n:message>"
                       class="skn-button" onclick="return confirmAdd()">
            </div>
        </td>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputStartBindings">

    <td class="RightMainColumn">

    <h1><i18n:message key="dataHolder_sources_title_list">Manage form data origins</i18n:message></h1>

    <p><i18n:message key="dataHolder_sources_subtitle_list">!!!List of data sources that will be bound to form fields.</i18n:message></p>
    <br><br>
    <table width="100%" class="skn-table_border">
    <tr class="skn-table_header">
        <td style="width:16px;">&nbsp;</td>
        <td><i18n:message key="dataHolder_Id">!!!dataHolder_id</i18n:message></td>
        <td><i18n:message key="dataHolder_inputId">!!!dataHolder_InputId</i18n:message></td>
        <td><i18n:message key="dataHolder_outputId">!!!dataHolder_outid</i18n:message></td>
        <td><i18n:message key="dataHolder_type">!!!dataHolder_type</i18n:message></td>
        <td><i18n:message key="dataHolder_info">!!!!!!dataHolder_info</i18n:message></td>
        <td style="width: 100px;"><i18n:message key="dataHolder_renderColor">!!!!!!dataHolder_renderColor</i18n:message></td>

    </tr>
</mvc:fragment>

<mvc:fragment name="outputBindings">
    <mvc:fragmentValue name="id" id="id">
    <mvc:fragmentValue name="input_id" id="input_id">
        <mvc:fragmentValue name="outId" id="outId">
            <mvc:fragmentValue name="deleteId" id="deleteId">
                <mvc:fragmentValue name="type" id="type">
                    <mvc:fragmentValue name="value" id="value">
                        <mvc:fragmentValue name="renderColor" id="renderColor">
                            <mvc:fragmentValue name="rowStyle" id="rowStyle">
                                <tr class="<%=rowStyle%>">
                                    <td align="center" style="width:16px;"><a
                                            title="<i18n:message key="delete">!!!Borrar</i18n:message>"
                                            href="<factory:url  action="formDataHolders">
                                         <factory:param name="<%=WysiwygFormEditor.PARAMETER_HOLDER_ID%>" value="<%=deleteId%>"/>
                                         <factory:param name="<%=WysiwygFormEditor.ACTION_TO_DO%>" value="<%=WysiwygFormEditor.ACTION_REMOVE_DATA_HOLDER%>"/>
                                      </factory:url>"
                                            onclick="return confirm('<i18n:message
                                            key="dataHolder_delete.confirm">Sure?</i18n:message>');">
                                        <img src="<static:image relativePath="actions/delete.png"/>" border="0"
                                             title="<i18n:message key="delete">!!!Clear</i18n:message>"/>
                                    </a></td>
                                    <td>
                                        <%=StringEscapeUtils.escapeHtml4((String)id) %>
                                    </td>
                                    <td>
                                        <%=StringEscapeUtils.escapeHtml4((String)input_id) %>
                                    </td>
                                    <td>
                                        <%=StringEscapeUtils.escapeHtml4((String)outId) %>
                                    </td>
                                    <td>
                                        <%=type%>
                                    </td>
                                    <td>
                                        <%=value%>
                                    </td>
                                    <td style="width: 100px;">
                                        <div style="background-color: <%=renderColor%> ">&nbsp;</div>
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
</mvc:fragment>

<mvc:fragment name="outputEndBindings">
    </table>
    </td>
    </tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputEnd">
    </table>

    </form>
    <script type="text/javascript" defer="defer">
        setAjax("<factory:encode name="formDataHolders"/>");
    </script>


</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
</mvc:formatter>
