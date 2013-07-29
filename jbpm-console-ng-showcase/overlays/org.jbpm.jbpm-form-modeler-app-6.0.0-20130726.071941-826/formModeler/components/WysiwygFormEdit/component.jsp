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
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ page import="org.jbpm.formModeler.components.editor.WysiwygFormEditor" %>
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>
<%
    String editorBgColor = "#eaeaea";
%>

<mvc:formatter name="WysiwygMenuFormatter">
    <mvc:fragment name="outputStart">
        <table style="width: 100%; border-collapse: collapse;">
    </mvc:fragment>
    <mvc:fragment name="outputHeader">
        <tr>
        <td class="headerComponent">
        <table width="100%">
        <tr>
    </mvc:fragment>
    <mvc:fragment name="beforeOptions">
        <td valign="top">
    </mvc:fragment>

    <mvc:fragment name="optionsOutputStart">

        <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="changeMainOption"/>">
        <factory:handler action="changeMainOption"/>
        <input type="hidden" name="newMainOption">
        <table class="HorMenu"><tr>
    </mvc:fragment>
    <mvc:fragment name="outputOption">
        <mvc:fragmentValue name="optionName" id="optionName">
            <mvc:fragmentValue name="optionImage" id="optionImage">
                <td class="HorMenuOff">
                    <input type="image"
                           onclick="setFormInputValue(this.form,'newMainOption','<%=optionName%>');"
                           title="<i18n:message key="<%=(String)optionName%>">!!!optionName</i18n:message>"
                           src="<static:image relativePath="<%=(String)optionImage%>"/>">&nbsp;<a href="#" onclick="setFormInputValue(document.getElementById('<factory:encode name="changeMainOption"/>'),'newMainOption','<%=optionName%>');submitAjaxForm(document.getElementById('<factory:encode name="changeMainOption"/>'));"><i18n:message key="<%=(String)optionName%>">!!!optionName</i18n:message></a>
                </td>

            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputSelectedOption">
        <mvc:fragmentValue name="optionName" id="optionName">
            <mvc:fragmentValue name="optionImage" id="optionImage">
                <td class="HorMenuOn">
                    <input type="image"
                           onclick="setFormInputValue(this.form,'newMainOption','<%=optionName%>');"
                           title="<i18n:message key="<%=(String)optionName%>">!!!optionName</i18n:message>"
                           src="<static:image relativePath="<%=(String)optionImage%>"/>">&nbsp;<a href="#" onclick="setFormInputValue(document.getElementById('<factory:encode name="changeMainOption"/>'),'newMainOption','<%=optionName%>');submitForm(document.getElementById('<factory:encode name="changeMainOption"/>'));"><i18n:message key="<%=(String)optionName%>">!!!optionName</i18n:message></a>
                </td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="optionsOutputEnd">
        <mvc:fragmentValue name="renderMode" id="renderMode">
            <mvc:fragmentValue name="displayBindings" id="displayBindings">
                <mvc:fragmentValue name="displayCheckbox" id="displayCheckbox">
                    <mvc:fragmentValue name="displayGrid" id="displayGrid">
                        <td class="HorMenuOff">
                            <input type="image"
                                   onclick="setFormInputValue(this.form,'newMainOption','<%=WysiwygFormEditor.EDITION_OPTION_SAVE%>');"
                                   title="<i18n:message key="save">!!!Save</i18n:message>"
                                   src="<static:image relativePath="general/Save.png"/>">&nbsp;<a href="#" onclick="setFormInputValue(document.getElementById('<factory:encode name="changeMainOption"/>'),'newMainOption','<%=WysiwygFormEditor.EDITION_OPTION_SAVE%>');submitForm(document.getElementById('<factory:encode name="changeMainOption"/>'));"><i18n:message key="save">!!!Save</i18n:message></a>
                        </td>
                        </tr>
                        </table>
                        </form>
                        <script defer>
                            setAjax("<factory:encode name="changeMainOption"/>");
                        </script>

                        </td>
                        <td valign="top" style="padding:6px 0px; white-space: nowrap;" width="99%;">

                        </td>
                        <td style="white-space: nowrap;">
                            <% if(displayCheckbox!=null && ((Boolean)displayCheckbox).booleanValue()) { %>
                            <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="switchRenderMode"/>">
                                <factory:handler action="switchRenderMode"/>

                                <input type="hidden" name="renderMode" value="<%=renderMode%>">
                                <input type="hidden" name="displayBindings" value=<%=displayBindings%>>
                                <input type="hidden" name="displayGrid" value="<%=displayGrid%>">

                                <input type="checkbox"  <%if (Form.RENDER_MODE_WYSIWYG_DISPLAY.equals(renderMode)){ %>checked <% }%>
                                       onclick="setFormInputValue(this.form,'renderMode','<%=(Form.RENDER_MODE_WYSIWYG_FORM.equals(renderMode) ? Form.RENDER_MODE_WYSIWYG_DISPLAY : Form.RENDER_MODE_WYSIWYG_FORM)%>');submitAjaxForm(form);"> <i18n:message key="header_chk_show">Show mode</i18n:message>
                                <input type="checkbox"  <%= ((displayBindings!=null && !((Boolean) displayBindings).booleanValue()) ? "": "checked")%>
                                       onclick="setFormInputValue(this.form,'displayBindings','<%=(displayBindings!=null ? Boolean.toString(!((Boolean)displayBindings).booleanValue()): Boolean.TRUE.toString()) %>');submitAjaxForm(form);"> <i18n:message key="header_chk_bindings">Bindings</i18n:message>
                                <input type="checkbox" <%= ((displayGrid!=null && ((Boolean) displayGrid).booleanValue()) ? "checked": "")%> value="rule" onclick="setFormInputValue(this.form,'displayGrid','<%=(displayGrid!=null ? Boolean.toString(!((Boolean)displayGrid).booleanValue()): Boolean.TRUE.toString()) %>');submitAjaxForm(form);"> <i18n:message key="header_chk_ruler">Grid & ruler</i18n:message>
                            </form>
                            <script type="text/javascript" defer="defer">
                                setAjax("<factory:encode name="switchRenderMode"/>");
                            </script>
                            <% } %>

                        </td>
                        </tr>
                        </table>
                        </td>
                        </tr>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputWithFormEditionPage">
        <mvc:fragmentValue name="editionPage" id="editionPage">
            <mvc:fragmentValue name="displayGrid" id="displayGrid">
                <mvc:fragmentValue name="editionNamespace" id="editionNamespace">
                <tr>
                    <td>

                        <table border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
                            <tr>
                                <td class="CompLeftColumn">
                                    <jsp:include page="<%=(String)editionPage%>" flush="true"/>
                                </td>

                                <td class="CompCenterColumn  <% if (displayGrid!=null && ((Boolean) displayGrid).booleanValue()) {%>bgGuides<%}%>" id="preview">
                                    <jsp:include page="formPreview.jsp"/>
                                </td>
                                <td class="CompRightColumn">
                                    <%
                                        request.setAttribute("editionNamespace", editionNamespace);
                                    %>
                                    <jsp:include page="editFieldProperties.jsp"/>
                                    <%
                                        request.removeAttribute("editionNamespace");
                                    %>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputWithEditionZone">
        <mvc:fragmentValue name="editionPage" id="editionPage">
            <mvc:fragmentValue name="editionZone" id="editionZone">
                <tr>
                    <td>
                        <table border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
                            <tr>
                                <td style="vertical-align: top;height: 600px;" width="220px">
                                    <jsp:include page="<%=(String)editionPage%>" flush="true"/>
                                </td>
                                <td style="vertical-align: top;"><!-- component: <%=(String)editionZone%>-->
                                    <jsp:include page="<%=(String)editionZone%>" flush="true"/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputWithoutFormEditionPage">
        <mvc:fragmentValue name="editionPage" id="editionPage">
            <tr>
                <td>
                    <jsp:include page="<%=(String)editionPage%>" flush="true"/>
                </td>
            </tr>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputEnd">
        </table>
    </mvc:fragment>
</mvc:formatter>
