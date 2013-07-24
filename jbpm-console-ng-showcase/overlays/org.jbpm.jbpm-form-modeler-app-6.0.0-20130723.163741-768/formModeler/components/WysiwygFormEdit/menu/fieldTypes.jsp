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
                        <form method="POST" style="margin:0px;" action="<factory:formUrl/>"
                              id="<%="addPrimitiveFieldForm"+uid%>">
                            <factory:handler action="addFieldToForm"/>
                            <table cellspacing="0" cellpadding="0" width="100%">
                                <tr onmouseover="className='skn-even_row_alt'"
                                    onmouseout="className=''">
                                    <td style="width:10px; padding-right: 5px;">
                                        <img src="<static:image relativePath="<%=(String)iconUri%>"/>"
                                             align="absmiddle">
                                    </td>
                                    <td>
                                        <i18n:message key='<%="fieldType." + typeName%>'/>

                                    </td>
                                    <td style="text-align:right; padding-right:5px; padding-bottom:1px;">
                                        <input type="image" onclick="this.onclick=function(){return false;}"
                                               title="<mvc:fragmentValue name="prop"/>"
                                               name="<mvc:fragmentValue name="typeName"/>"
                                               style="cursor:hand"
                                               src="<static:image relativePath="actions/triang_right.png"/>">
                                    </td>
                                </tr>
                            </table>
                            <input type="hidden" name="fieldType" value="<mvc:fragmentValue name="typeId"/>">
                        </form>
                        <script defer>
                            setAjax('<%="addPrimitiveFieldForm"+uid%>');
                        </script>
                    </td>
                </tr>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputFieldNameToAddStart">
    <tr><td nowrap="nowrap">
    <mvc:fragmentValue name="uid" id="uid">
        <div style="display:none; margin-left: 6px;" id='<%="" + uid%>'>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputFieldNameToAdd">
    <mvc:fragmentValue name="uid" id="uid">
        <form method="POST" style="margin:0px;" action="<factory:formUrl/>"
              id='<%="addPrimitiveFieldForm"+uid%>'>
            <factory:handler action="addFieldToForm"/>
            <table cellspacing="0" cellpadding="0" width="100%">
                <tr onmouseover="className='skn-even_row_alt'" onmouseout="className=''">
                    <td>
                        <b style="cursor:text;"
                           onclick="this.innerHTML='<input name=\'label\' style=\'width:150px\'  maxlength=\'200\' class=\'skn-input\' value=\'
                               <mvc:fragmentValue name="prop"/>\'>'; this.onclick=''; ">
                            <mvc:fragmentValue name="prop"/>
                        </b>
                    </td>
                    <td style="text-align:right; padding-right:5px; padding-bottom:1px;">
                        <input type="image" onclick="this.onclick=function(){return false;}"
                               title="<mvc:fragmentValue name="prop"/>"
                               name="<mvc:fragmentValue name="typeName"/>"
                               style="cursor:hand"
                               src="<static:image relativePath="actions/triang_right.png"/>">
                    </td>
                </tr>
            </table>

            <input type="hidden" name="name" value="<mvc:fragmentValue name="prop"/>">
            <input type="hidden" name="fieldType" value="<mvc:fragmentValue name="typeId"/>">
        </form>
        <script defer>
            setAjax('<%="addPrimitiveFieldForm"+uid%>');
        </script>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputFieldNameToAddEnd">
    </div>
    </td></tr>
</mvc:fragment>


<%------------------ Complex types -----------------%>
<mvc:fragment name="disabledComplexTypeStart">
    <tr class="fieldTypes">
        <td nowrap="nowrap" width="10px">
            <mvc:fragmentValue name="iconUri" id="iconUri">
                <img src="<static:image relativePath="<%=(String)iconUri%>"/>" align="absmiddle">
            </mvc:fragmentValue>
            <span class="skn-disabled"><mvc:fragmentValue name="managerName"/></span>
        </td>
    </tr>
</mvc:fragment>
<mvc:fragment name="complexTypeStart">
    <tr onclick="className='skn-even_row_alt'" onmouseout="className=''">
    <td nowrap="nowrap" width="10px">
    <mvc:fragmentValue name="position" id="position">
        <mvc:fragmentValue name="type" id="type">
            <mvc:fragmentValue name="iconUri" id="iconUri">
                <img src="<static:image relativePath="<%=(String)iconUri%>"/>" align="absmiddle">
            </mvc:fragmentValue>
            <a href="fieldTypes.jsp#"
               onclick="var divElement = document.getElementById('<%="type_"+type+position%>');
                       divElement.style.display = divElement.style.display == 'block' ? 'none' : 'block';
                       return false;">
                <mvc:fragmentValue name="managerName"/>
            </a>
            </td>
            </tr>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputComplexFieldNameToAddStart">
    <mvc:fragmentValue name="position" id="position">
        <mvc:fragmentValue name="type" id="type">
            <tr><td>
            <div id='<%="type_" + type + position%>' style="display:none; margin-left: 6px;">
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputComplexFieldNameToAdd">
    <mvc:fragmentValue name="uid" id="uid">
        <script defer="true">
            setAjax('<%="addComplexForm"+uid%>');
        </script>
        <form method="POST" style="margin:0px;" action="<factory:formUrl/>" id='<%="addComplexForm"+uid%>'>
            <factory:handler action="addComplexFieldToForm"/>
            <table cellspacing="0" cellpadding="0" width="100%">
                <tr onmouseover="className='skn-even_row_alt'" onmouseout="className=''">
                    <td>
                        <b style="cursor:text;"
                           onclick="this.innerHTML='<input name=\'label\' style=\'width:150px\'  maxlength=\'200\' class=\'skn-input\' value=\'
                               <mvc:fragmentValue name="typeName"/>\'>'; this.onclick=''; "><mvc:fragmentValue
                                name="typeName"/></b>
                    </td>
                    <td style="text-align:right; padding-right:5px; padding-bottom:1px;">
                        <input type="image" onclick="this.onclick=function(){return false;}"
                               title="<mvc:fragmentValue name="typeName"/>"
                               name="<mvc:fragmentValue name="managerClass"/>"
                               style="cursor:hand"
                               src="<static:image relativePath="actions/triang_right.png"/>">
                    </td>
                </tr>
            </table>
            <input name="fieldClass" type="hidden" value="<mvc:fragmentValue name="managerClass"/>">
            <input type="hidden" name="name" value="<mvc:fragmentValue name="typeName"/>">
        </form>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputComplexFieldNameToAddEnd">
    </div>
    </td></tr>
</mvc:fragment>

<%------------------ Decorators -----------------%>
<mvc:fragment name="decoratorsStart"></mvc:fragment>
<mvc:fragment name="outputDecorator">
    <mvc:fragmentValue name="decoratorId" id="decoratorId">
        <mvc:fragmentValue name="position" id="position">
            <tr>
                <td nowrap="nowrap" width="10px">
                    <form method="POST" style="margin:0px;" action="<factory:formUrl/>"
                          id='<%="addDecForm"+position%>' >
                        <factory:handler action="addDecoratorToForm"/>

                        <input type="hidden" name="fieldType" value="<%=decoratorId%>">
                        <table cellspacing="0" cellpadding="0" width="100%">
                            <tr>
                                <td style="width:10px; padding-right: 5px;">
                                    <mvc:fragmentValue name="iconUri" id="iconUri">
                                        <img src="<static:image relativePath="<%=(String)iconUri%>"/>"
                                             align="absmiddle">
                                    </mvc:fragmentValue>
                                </td>
                                <td>
                                    <i18n:message key='<%="fieldType." + decoratorId%>'/>
                                </td>
                                <td style="text-align:right; padding-right:5px; padding-bottom:1px;">
                                    <input type="image" style="cursor:hand"
                                           title="<mvc:fragmentValue name="decoratorName"/>"
                                           src="<static:image relativePath="actions/triang_right.png"/>">
                                </td>
                            </tr>
                        </table>
                    </form>
                    <script defer>
                        setAjax('<%="addDecForm"+position%>');
                    </script>
                </td>
            </tr>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="decoratorsEnd"></mvc:fragment>
</mvc:formatter>
</table>