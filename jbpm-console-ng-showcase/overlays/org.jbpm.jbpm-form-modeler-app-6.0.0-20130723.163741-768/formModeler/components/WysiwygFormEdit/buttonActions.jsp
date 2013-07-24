<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
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
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>
<table cellspacing="0" cellpadding="1" align="left" border="0" width="1px">
    <tr>
        <mvc:formatter name="FieldButtonsFormatter">
            <mvc:formatterParam name="hideMotionButtons" value="<%=hideMotionButtons%>"/>
            <mvc:formatterParam name="field" value="<%=field%>"/>

            <mvc:fragment name="outputDelete">
                <mvc:fragmentValue name="position" id="position">
                    <mvc:fragmentValue name="icon" id="icon">
                        <td width="1px">
                            <a title="<i18n:message key="delete">!!!Borrar</i18n:message>"
                               href="<factory:url  action="delete"><factory:param name="position" value="<%=position%>"/></factory:url>"
                               id="<factory:encode name='<%="deleteBtn"+position%>'/>"
                               onclick="return confirm('<i18n:message key="delete.field.confirm">Sure?</i18n:message>');">
                                <img src='<static:image relativePath="<%=(String)icon%>" />' border="0" alt='<i18n:message key="delete">!!!Borrar</i18n:message>' align="absmiddle">
                            </a>
                            <script defer="true">
                                setAjax("<factory:encode name='<%="deleteBtn"+position%>'/>");
                            </script>
                        </td>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragment>
            <mvc:fragment name="outputEdit">
                <mvc:fragmentValue name="position" id="position">
                    <mvc:fragmentValue name="icon" id="icon">
                        <mvc:fragmentValue name="buttonId" id="buttonId">
                            <td width="1px">
                                    <%--Edit button--%>
                                <a title="<i18n:message key="edit">!!!Editar</i18n:message>"
                                   id="<factory:encode name='<%=(String)buttonId%>'/>"
                                   href="<factory:url action="startEdit"><factory:param name="position" value="<%=position%>"/></factory:url>">
                                    <img src='<static:image relativePath="<%=(String)icon%>"/>' border="0" alt='<i18n:message key="edit">!!!Editar</i18n:message>' align="absmiddle">
                                </a>
                                <script defer="true">
                                    setAjax("<factory:encode name='<%=(String)buttonId%>'/>");
                                </script>
                            </td>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragment>
            <mvc:fragment name="outputMoveField">
                <mvc:fragmentValue name="position" id="position">
                    <mvc:fragmentValue name="icon" id="icon">
                        <mvc:fragmentValue name="actionUrl" id="actionUrl">
                            <mvc:fragmentValue name="buttonId" id="buttonId">
                                <mvc:fragmentValue name="msgId" id="msgId">
                                    <td width="1px">
                                        <a title="<i18n:message key="<%=(String)msgId%>">!!!<%=(String)msgId%></i18n:message>"
                                           href="<%=actionUrl%>"  onclick="this.onclick=function(){return false;};"
                                           id="<factory:encode name='<%=(String)buttonId%>'/>">
                                            <img src='<static:image relativePath="<%=(String)icon%>" />' border="0" alt='<i18n:message key="<%=(String)msgId%>">!!!<%=(String)msgId%></i18n:message>' align="absmiddle">
                                        </a>
                                        <script defer="true">
                                            setAjax("<factory:encode name='<%=(String)buttonId%>'/>");
                                        </script>
                                    </td>
                                </mvc:fragmentValue>
                            </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragment>
            <mvc:fragment name="outputSelectField">
                <mvc:fragmentValue name="position" id="position">
                    <mvc:fragmentValue name="grouped" id="grouped">
                        <mvc:fragmentValue name="icon" id="icon">
                            <td width="1px">
                                <a href="#" id="<factory:encode name='selectField'/>"
                                   onclick="selectField(<%=position%>, '<factory:encode name="formMenuDiv"/>', <%=grouped%>);return false;"
                                   title="<i18n:message key="move">!!!Mover Campo</i18n:message>">
                                    <img src='<static:image relativePath="<%=(String)icon%>" />' border="0" alt='<i18n:message key="move">!!!Mover Campo</i18n:message>' align="absmiddle">
                                </a>
                            </td>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragment>
        </mvc:formatter>
    </tr>
</table>
