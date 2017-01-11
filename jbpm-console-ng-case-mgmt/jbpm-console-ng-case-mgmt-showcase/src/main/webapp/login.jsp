<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" class="login-pf">
<head>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/org.jbpm.workbench.cm.jBPMCaseManagement/css/patternfly.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/org.jbpm.workbench.cm.jBPMCaseManagement/css/patternfly-additions.min.css">
    <link rel="shortcut icon" href="favicon.ico"/>
    <title>jBPM Case Management</title>
</head>

<body>
    <span id="badge">
        <img src="<%=request.getContextPath()%>/org.jbpm.workbench.cm.jBPMCaseManagement/img/kie-ide.png" alt="KIE IDE Logo" title="Powered By Drools/jBPM" />
    </span>
    <div class="container">
        <div class="row">
            <div class="col-sm-12">
                <div id="brand">
                    <%--<img src="/assets/img/brand.svg" alt="PatternFly Enterprise Application">--%>
                </div>
            </div>
            <div class="col-sm-7 col-md-6 col-lg-5 login">
                <c:if test="${param.message != null}">
                    <div class="alert alert-danger">
                        <span class="pficon pficon-error-circle-o"></span>
                        <strong>Login failed: Not Authorized</strong>
                    </div>
                </c:if>
                <form class="form-horizontal" action="j_security_check" method="POST" role="form">
                    <div class="form-group">
                        <label for="j_username" class="col-sm-2 col-md-2 control-label">Username</label>
                        <div class="col-sm-10 col-md-10">
                            <input type="text" class="form-control" id="j_username" name="j_username" placeholder="" tabindex="1" autofocus>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="j_password" class="col-sm-2 col-md-2 control-label">Password</label>
                        <div class="col-sm-10 col-md-10">
                            <input type="password" class="form-control" id="j_password" name="j_password" placeholder="" tabindex="2">
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-xs-offset-8 col-xs-4 col-sm-offset-8 col-sm-4 col-md-offset-8 col-md-4 submit">
                            <button type="submit" class="btn btn-primary btn-lg" tabindex="3">Log In</button>
                        </div>
                    </div>
                </form>
            </div>
            <div class="col-sm-5 col-md-6 col-lg-7 details">
                <p><strong>Welcome to jBPM Case Management Showcase app!</strong></p>
            </div>
        </div>
    </div>
</body>
</html>
