<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<mvc:formatter name="FormRenderingComponentFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="ctxUID" id="ctxUID">
        <mvc:fragmentValue name="form" id="form">
        <mvc:fragmentValue name="disabled" id="disabled">
<form action="<factory:formUrl/>" method="post" id="formRendering<%=ctxUID%>" style="margin:0px; padding:0px;">
    <factory:handler action="submitForm"/>
    <input type="hidden" name="ctxUID" id="ctxUID" value="<%=ctxUID%>"/>
    <input type="hidden" id="persist_<%=ctxUID%>" name="persistForm" value="false"/>
    <mvc:formatter name="FormRenderingFormatter">
        <mvc:formatterParam name="form" value="<%=form%>"/>
        <mvc:formatterParam name="renderMode" value="<%=Form.RENDER_MODE_FORM%>"/>
        <mvc:formatterParam name="namespace" value="<%=ctxUID%>"/>
        <mvc:formatterParam name="isDisabled" value="<%=disabled%>"/>
        <mvc:formatterParam name="reuseStatus" value="true"/>
        <%@ include file="/formModeler/defaultFormRenderingFormatterOptions.jsp" %>
    </mvc:formatter>
</form>
<script type="text/javascript"defer="defer">
    var width = ($(document).width() + 20) + "px";
    var pDiv = $("#formRendering<%=ctxUID%>").parent();
    var height = (pDiv.height() + 20) + "px";

    window.parent.resizeRendererWidget(width, height);

    setAjax("formRendering<%=ctxUID%>");
</script>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>