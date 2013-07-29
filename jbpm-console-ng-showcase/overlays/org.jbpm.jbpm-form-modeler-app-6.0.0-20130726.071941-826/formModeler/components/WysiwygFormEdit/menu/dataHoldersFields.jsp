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
<%@ page import="org.jbpm.formModeler.components.editor.WysiwygFormEditor" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="DataHoldersFormFormatter">
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <script language="javascript">
            $(document).ready(function(){
                $("#red").treeview({
                    animated: "fast",
                    collapsed: true,
                    control: "#treecontrol",
                    unique: true
                });
            });
        </script>
        <div class="LeftColumnProperties">
        <table cellpadding="0" cellspacing="0" border="0"  width="100%"><tr><td>
        <ul id="red" class="treeview-red">

    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStartBindings">
    </mvc:fragment>

    <mvc:fragment name="outputBinding">
        <mvc:fragmentValue name="id" id="id">
            <mvc:fragmentValue name="type" id="type">
                <mvc:fragmentValue name="renderColor" id="rColor">
                    <mvc:fragmentValue name="open" id="open">
                        <mvc:fragmentValue name="showHolderName" id="showHolderName">
                            <mvc:fragmentValue name="noConfirm" id="noConfirm">
                            <li <%= (open!=null && (Boolean.TRUE.equals((Boolean)open)) ? "class=\"open\"":"" )%> ><span title="<%=showHolderName%>"><div style="margin-top: -3px; padding: 2px 1px 2px 5px;background-color: <%=rColor%>"><b><%=StringEscapeUtils.escapeHtml((String) showHolderName) %></b><a
                            href="<factory:url  action="formDataHolders"><factory:param name="<%=WysiwygFormEditor.PARAMETER_HOLDER_ID%>" value="<%=id%>"/><factory:param name="<%=WysiwygFormEditor.ACTION_TO_DO%>" value="<%=WysiwygFormEditor.ACTION_ADD_DATA_HOLDER_FIELDS%>"/></factory:url>"
                            <%if ( noConfirm!=null && (Boolean.FALSE.equals((Boolean)noConfirm))) { %> title='<i18n:message key="dataHolder_addAllFields">!!!dataHolder_addAllFields</i18n:message>' onclick="return confirm('<i18n:message key="dataHolder_addAll_comfirm">!!!dataHolder_addAll_comfirm</i18n:message>');" <%} else {%> title='<i18n:message key="dataHolder_addField">!!!add as new form field</i18n:message>' <%}  %> >
                            <img style="float: right; position: relative; top: 0px;" src='<static:image relativePath="actions/triang_right.png"/>'>
                            </a></div></span>
                            <ul>
                            </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="firstField">
    </mvc:fragment>
    <mvc:fragment name="outputField">
        <mvc:fragmentValue name="bindingId" id="bindingId">
            <mvc:fragmentValue name="iconUri" id="iconUri">
                <mvc:fragmentValue name="typeName" id="typeName">
                    <mvc:fragmentValue name="fieldName" id="fieldName">
                        <mvc:fragmentValue name="showFieldName" id="showFieldName">
                            <mvc:fragmentValue name="className" id="className">
                            <li><span title="<%=fieldName%>" style="vertical-align: top"><img src="<static:image relativePath="<%=(String)iconUri%>"/>"
                                                                                              align="absmiddle">
                                        <%=StringEscapeUtils.escapeHtml((String) showFieldName)%>
                                            <a href="<factory:url  action="addFieldFromDataHolder">
                                                         <factory:param name="<%=WysiwygFormEditor.PARAMETER_HOLDER_ID%>" value="<%=bindingId%>"/>
                                                         <factory:param name="<%=WysiwygFormEditor.PARAMETER_FIELD_NAME%>" value="<%=fieldName%>"/>
                                                         <factory:param name="<%=WysiwygFormEditor.PARAMETER_FIELD_CLASS%>" value="<%=className%>"/>
                                                         </factory:url>"><img style="float: right; position: relative; top: 0px;" src='<static:image relativePath="actions/triang_right.png"/>'></a>
                            </span></li>
                            </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="lastField">
    </mvc:fragment>

    <mvc:fragment name="outputEndBinding">
        </ul> </li>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        </ul>
        </td></tr>
        </table>
        </div>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
</mvc:formatter>