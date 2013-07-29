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
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.jbpm.formModeler.api.model.wrappers.I18nSet"%>
<%@ page import="org.jbpm.formModeler.service.LocaleManager"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%try{%>
<mvc:formatter name="SimpleFieldHandlerFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="name" id="name">
        <mvc:fragmentValue name="title" id="title">
            <mvc:fragmentValue name="styleclass" id="styleclass">
                <mvc:fragmentValue name="size" id="size">
                    <mvc:fragmentValue name="maxlength" id="maxlength">
                        <mvc:fragmentValue name="tabindex" id="tabindex">
                            <mvc:fragmentValue name="value" id="val">
                                <mvc:fragmentValue name="accesskey" id="accesskey">
                                    <mvc:fragmentValue name="alt" id="altvalue">
                                        <mvc:fragmentValue name="cssStyle" id="cssStyle">
                                            <mvc:fragmentValue name="disabled" id="disabled">
                                                <mvc:fragmentValue name="height" id="height">
                                                    <mvc:fragmentValue name="readonly" id="readonly">
                                                        <input  name="<%=name%>_<%=LocaleManager.currentLang()%>"  id="<mvc:fragmentValue name="uid"/>"
                                                            onchange="processFormInputChange(this)"
                                                            <%=title!=null?("title=\""+title+"\""):""%>
                                                            <%=styleclass!=null && ((String)styleclass).trim().length()>0 ? " class=\""+styleclass+"\"":"class=\"skn-input\""%>
                                                            <%=size!=null ? " size=\""+size+"\"":""%>
                                                            <%=maxlength!=null ? " maxlength=\""+maxlength+"\"":""%>
                                                            <%=tabindex!=null ? " tabindex=\""+tabindex+"\"":""%>
                                                            <%=accesskey!=null ? " accesskey=\""+accesskey+"\"":""%>
                                                            <%=altvalue!=null ? " alt=\""+altvalue+"\"":""%>
                                                            <%=cssStyle!=null ? " style=\""+cssStyle+"\"":""%>
                                                            <%=height!=null ? " height=\""+height+"\"":""%>
                                                            <%=readonly!=null && ((Boolean)readonly).booleanValue()? " readonly ":""%>
                                                            <%=disabled!=null && ((Boolean)disabled).booleanValue()? " disabled ":""%>
                                                                value='<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(StringUtils.defaultString(!(val==null || "".equals(val))?((I18nSet)val).getValue(LocaleManager.currentLang()):"")))%>'>
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
<%}catch(Throwable t){System.out.println("Error showing I18nSet input "+t);t.printStackTrace();}%>
