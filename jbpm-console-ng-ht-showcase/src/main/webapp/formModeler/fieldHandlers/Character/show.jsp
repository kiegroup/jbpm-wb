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
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%try{%>
<mvc:formatter name="SimpleFieldHandlerFormatter">
	<mvc:fragment name="output">
		<mvc:fragmentValue name="value" id="value">
			<mvc:fragmentValue name="title" id="title">
				<mvc:fragmentValue name="styleclass" id="styleclass">
					<mvc:fragmentValue name="cssStyle" id="cssStyle">
                    <span
						<%=styleclass!=null && ((String)styleclass).trim().length()>0 ? " class=\""+styleclass+"\"":""%>
						<%=cssStyle!=null ? " style=\""+cssStyle+"\"":""%>
						<%=title!=null?("title=\""+title+"\""):""%>
						><%=StringEscapeUtils.escapeHtml4( value!=null?value.toString():"")%></span>
					</mvc:fragmentValue>
				</mvc:fragmentValue>
			</mvc:fragmentValue>
		</mvc:fragmentValue>
	</mvc:fragment>
</mvc:formatter>
<%}catch(Throwable t){System.out.println("Error showing Text "+t);t.printStackTrace();}%>
