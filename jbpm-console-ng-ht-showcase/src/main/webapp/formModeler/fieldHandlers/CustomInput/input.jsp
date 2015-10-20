<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.customInput.CustomInputFieldHandlerFormatter" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<mvc:formatter name="CustomInputFieldHandlerFormatter">
    <mvc:formatterParam name="<%=CustomInputFieldHandlerFormatter.PARAM_MODE%>" value="<%=CustomInputFieldHandlerFormatter.MODE_INPUT%>"/>
    <mvc:fragment name="output">
<div>
    <mvc:fragmentValue name="htmlCode"/>
</div>
    </mvc:fragment>
</mvc:formatter>