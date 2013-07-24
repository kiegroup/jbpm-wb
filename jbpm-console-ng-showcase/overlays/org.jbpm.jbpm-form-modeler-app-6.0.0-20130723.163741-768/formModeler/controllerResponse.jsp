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
<%@ taglib prefix="static" uri="static-resources.tld" %>

<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/formModeler/css/styles.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/formModeler/css/ui-lightness/jquery-ui-1.10.2.custom.min.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/formModeler/css/jquery-ui-timepicker-addon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/formModeler/css/jquery.treeview.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap.min.css" media="screen">
</head>
<script type="text/javascript">
    <%@ include file="/formModeler/js/ajax.jsp" %>
    <%@ include file="/formModeler/js/ajaxMonitor.jsp" %>
</script>
<script type="text/javascript" src="<%=request.getContextPath()%>/formModeler/js/jquery-1.9.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/formModeler/js/jquery-ui-1.10.2.custom.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/formModeler/js/jquery-ui-sliderAccess.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/formModeler/js/jquery-ui-timepicker-addon.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/formModeler/js/jquery.treeview.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/formModeler/js/jquery.treeview.min.js"></script>
<body>
<%@ include file="index.jsp" %>
<div id="ajaxLoadingDiv" style="position:absolute;position: absolute; left: 50%; top: 50%; z-index: 6000; opacity: 0.6; display: none;">
    <img  src="<static:image relativePath="general/loading.gif"/>">
</div>
</body>
</html>