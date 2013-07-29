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
                            <mvc:fragmentValue name="value" id="value">
                                <mvc:fragmentValue name="accesskey" id="accesskey">
                                    <mvc:fragmentValue name="alt" id="alt">
                                        <mvc:fragmentValue name="cssStyle" id="cssStyle">
                                            <mvc:fragmentValue name="disabled" id="disabled">
                                                <mvc:fragmentValue name="height" id="height">
                                                    <mvc:fragmentValue name="readonly" id="readonly">
                                                        <mvc:fragmentValue name="wrong" id="wrong">
                                                            <mvc:fragmentValue name="inputValue" id="inputValue">
                                            <table border="0" cellpadding="0" cellspacing="0" >
                                                <tr valign="top">
                                                    <td>
                                                        <input  name="<%=name%>"  id='<mvc:fragmentValue name="uid"/>'
                                                            onchange="processFormInputChange(this)"
                                                            <%=title!=null?("title=\""+title+"\""):""%>
                                                            class="dynInputStyle <%=StringUtils.defaultString((String) styleclass, "skn-input")%>"
                                                            <%=size!=null ? " size=\""+size+"\"":""%>
                                                            <%=maxlength!=null ? " maxlength=\""+maxlength+"\"":""%>
                                                            <%=tabindex!=null ? " tabindex=\""+tabindex+"\"":""%>
                                                            <%=accesskey!=null ? " accesskey=\""+accesskey+"\"":""%>
                                                            <%=alt!=null ? " alt=\""+alt+"\"":""%>
                                                            <%=cssStyle!=null ? " style=\""+cssStyle+"\"":""%>
                                                            <%=height!=null ? " height=\""+height+"\"":""%>
                                                            <%=readonly!=null && ((Boolean)readonly).booleanValue()? " readonly ":""%>
                                                            <%=disabled!=null && ((Boolean)disabled).booleanValue()? " disabled ":""%>
                                                                value='<%=
                                                                Boolean.TRUE.equals(wrong)?
                                                                (StringEscapeUtils.escapeHtml(StringUtils.defaultString(inputValue==null?"":String.valueOf(inputValue))))
                                                                :
                                                                (StringEscapeUtils.escapeHtml(StringUtils.defaultString(value==null?"":String.valueOf(value))))
                                                                %>'>
                                                    </td>
                                                    <td align="left">
                                                        &nbsp;<b>&#8364;</b>
                                                    </td>
                                                </tr>
                                            </table>                   
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
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>
<%}catch(Throwable t){System.out.println("Error showing Text input "+t);t.printStackTrace();}%>
