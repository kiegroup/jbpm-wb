<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<mvc:formatter name="FormRenderingComponentFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="ctxUID" id="ctxUID">
        <mvc:fragmentValue name="form" id="form">
        <mvc:fragmentValue name="readonly" id="readonly">
<div style="display: inline-block;">
  <form action="<factory:formUrl/>" method="post" id="formRendering<%=ctxUID%>" style="margin:0px; padding:0px;" enctype="multipart/form-data">
      <factory:handler action="submitForm"/>
      <input type="hidden" name="ctxUID" id="ctxUID" value="<%=ctxUID%>"/>
      <input type="hidden" id="persist_<%=ctxUID%>" name="persistForm" value="false"/>
      <mvc:formatter name="FormRenderingFormatter">
          <mvc:formatterParam name="form" value="<%=form%>"/>
          <mvc:formatterParam name="renderMode" value="<%=Form.RENDER_MODE_FORM%>"/>
          <mvc:formatterParam name="namespace" value="<%=ctxUID%>"/>
          <mvc:formatterParam name="isReadonly" value="<%=readonly%>"/>
          <mvc:formatterParam name="reuseStatus" value="true"/>
          <%@ include file="/formModeler/defaultFormRenderingFormatterOptions.jsp" %>
      </mvc:formatter>
  </form>

<script type="text/javascript" defer="defer">

    function resizeParent() {
      var width = ($("#formRendering<%=ctxUID%>").parent().width() + 20);
      var height = ($("#formRendering<%=ctxUID%>").parent().height() + 20);

      var request = $.ajax({
        url: "Controller",
        type: "POST",
        data: {
          _fb: "frc",
          _fp: "DoResize",
          ctxUID: "<%=ctxUID%>",
          width : width,
          height: height},
        dataType: "html"
      });
    }

    $(document).ready(function() {
        setTimeout("resizeParent()", 100);
    });

    setAjax("formRendering<%=ctxUID%>");
  </script>
</div>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>