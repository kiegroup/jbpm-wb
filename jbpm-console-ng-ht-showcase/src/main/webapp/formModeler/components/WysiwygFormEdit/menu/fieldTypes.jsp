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
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>


<table cellspacing="1" cellpadding="3" border="0" class="oldfieldTypes">
    <mvc:formatter name="WysiwygFieldsFormatter">
        <mvc:fragment name="separator">

        </mvc:fragment>

        <%------------------ Primitive types -----------------%>
        <mvc:fragment name="outputDisabledType">
            <tr>
                <td nowrap="nowrap" style="vertical-align:middle;" width="10px">
                    <mvc:fragmentValue name="iconUri" id="iconUri">
                        <img src="<static:image relativePath="<%=(String)iconUri%>"/>" align="absmiddle">
                    </mvc:fragmentValue>
                    <mvc:fragmentValue name="typeName"/>
                </td>
            </tr>
        </mvc:fragment>
        <mvc:fragment name="outputType">
            <mvc:fragmentValue name="uid" id="uid">
                <mvc:fragmentValue name="iconUri" id="iconUri">
                    <mvc:fragmentValue name="typeName" id="typeName">
                        <tr onclick="className='skn-even_row_alt'" onmouseout="className=''">
                            <td nowrap="nowrap"  class="fieldTypes">
                                <table cellspacing="0" cellpadding="0" width="100%">
                                    <tr onmouseover="className='skn-even_row_alt'"
                                        onmouseout="className=''">
                                        <td style="width:10px; padding-right: 5px;">
                                            <img src="<static:image relativePath="<%=(String)iconUri%>"/>"
                                                 align="absmiddle">
                                        </td>
                                        <td>
                                            <mvc:fragmentValue name="label"/>
                                        </td>
                                        <td style="text-align:right; padding-right:5px; padding-bottom:1px;">
                                            <input type="image"
                                                   title="<mvc:fragmentValue name="prop"/>"
                                                   name="<mvc:fragmentValue name="typeName"/>"
                                                   style="cursor:hand"
                                                   src="<static:image relativePath="actions/triang_right.png"/>"
                                                   onclick="setFormInputValue(document.getElementById('<factory:encode name="addField"/>'),'action','addFieldToForm');
                                                       setFormInputValue(document.getElementById('<factory:encode name="addField"/>'),'fieldType','<mvc:fragmentValue name="typeId"/>');
                                                       submitAjaxForm(document.getElementById('<factory:encode name="addField"/>')); return false;"
                                                >
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragment>

        <%------------------ Complex types -----------------%>
        <mvc:fragment name="complexStart"></mvc:fragment>
        <mvc:fragment name="outputComplex">
            <mvc:fragmentValue name="complexId" id="complexId">
                <mvc:fragmentValue name="position" id="position">
                    <tr>
                        <td nowrap="nowrap" width="10px">
                            <table cellspacing="0" cellpadding="0" width="100%">
                                <tr>
                                    <td style="width:10px; padding-right: 5px;">
                                        <mvc:fragmentValue name="iconUri" id="iconUri">
                                            <img src="<static:image relativePath="<%=(String)iconUri%>"/>"
                                                 align="absmiddle">
                                        </mvc:fragmentValue>
                                    </td>
                                    <td>
                                        <mvc:fragmentValue name="label"/>
                                    </td>
                                    <td style="text-align:right; padding-right:5px; padding-bottom:1px;">
                                        <input type="image" style="cursor:hand"
                                               title="<mvc:fragmentValue name="complexName"/>"
                                               src="<static:image relativePath="actions/triang_right.png"/>"
                                               onclick="setFormInputValue(document.getElementById('<factory:encode name="addField"/>'),'action','addFieldToForm');
                                                   setFormInputValue(document.getElementById('<factory:encode name="addField"/>'),'fieldType','<%=complexId%>');
                                                   submitAjaxForm(document.getElementById('<factory:encode name="addField"/>')); return false;"
                                            >
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragment>
        <mvc:fragment name="complexEnd"></mvc:fragment>
        <%------------------ Decorators -----------------%>
        <mvc:fragment name="decoratorsStart"></mvc:fragment>
        <mvc:fragment name="outputDecorator">
            <mvc:fragmentValue name="decoratorId" id="decoratorId">
                <mvc:fragmentValue name="position" id="position">
                    <tr>
                        <td nowrap="nowrap" width="10px">
                            <table cellspacing="0" cellpadding="0" width="100%">
                                <tr>
                                    <td style="width:10px; padding-right: 5px;">
                                        <mvc:fragmentValue name="iconUri" id="iconUri">
                                            <img src="<static:image relativePath="<%=(String)iconUri%>"/>"
                                                 align="absmiddle">
                                        </mvc:fragmentValue>
                                    </td>
                                    <td>
                                        <mvc:fragmentValue name="label"/>
                                    </td>
                                    <td style="text-align:right; padding-right:5px; padding-bottom:1px;">
                                        <input type="image" style="cursor:hand"
                                               title="<mvc:fragmentValue name="decoratorName"/>"
                                               src="<static:image relativePath="actions/triang_right.png"/>"
                                               onclick="setFormInputValue(document.getElementById('<factory:encode name="addField"/>'),'action','addDecoratorToForm');
                                                   setFormInputValue(document.getElementById('<factory:encode name="addField"/>'),'fieldType','<%=decoratorId%>');
                                                   submitAjaxForm(document.getElementById('<factory:encode name="addField"/>')); return false;">
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragment>
        <mvc:fragment name="decoratorsEnd"></mvc:fragment>
    </mvc:formatter>
</table>