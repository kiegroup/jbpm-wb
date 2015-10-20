<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.plugable.PlugableFieldHandlerFormatter" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<mvc:formatter name="PlugableFieldHandlerFormatter">
    <mvc:formatterParam name="<%=PlugableFieldHandlerFormatter.PARAM_MODE%>" value="<%=PlugableFieldHandlerFormatter.MODE_INPUT%>"/>
    <mvc:fragment name="output">
<div>
    <mvc:fragmentValue name="htmlCode"/>
</div>
    </mvc:fragment>
</mvc:formatter>