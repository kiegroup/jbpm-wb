<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
  final Logger logger = LoggerFactory.getLogger("logout.jsp");
  try {
    request.logout();
    javax.servlet.http.HttpSession httpSession = request.getSession(false);
    if (httpSession != null) {
      httpSession.invalidate();
    }
  } catch (SecurityException e) {
    //The only case we know that this  happens is when java security manager is enabled on EAP.
    logger.debug("Security exception happened, without consequences, during logout.",
                 e);
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <link rel="shortcut icon" href="favicon.ico"/>
  <title>jBPM Workbench</title>

  <style type="text/css">
    * {
      font-family: Helvetica, Arial, sans-serif;
    }

    body {
      margin: 0;
      pading: 0;
      color: #fff;
      background: url('images/bg-login.png') repeat #1b1b1b;
      font-size: 14px;
      text-shadow: #050505 0 -1px 0;
      font-weight: bold;
    }

    #dummy {
      position: absolute;
      top: 0;
      left: 0;
      border-bottom: solid 3px #777973;
      height: 250px;
      width: 100%;
      background: url('images/bg-login-top.png') repeat #fff;
      z-index: 1;
    }

    #dummy2 {
      position: absolute;
      top: 0;
      left: 0;
      border-bottom: solid 2px #545551;
      height: 252px;
      width: 100%;
      background: transparent;
      z-index: 2;
    }

    #login-wrapper {
      margin: 0 0 0 -160px;
      width: 320px;
      text-align: center;
      z-index: 99;
      position: absolute;
      top: 0;
      left: 50%;
    }

    #login-top {
      height: 120px;
      width: 401px;
      padding-top: 20px;
      text-align: center;
    }

    #login-content {
      margin-top: 120px;
    }

    input.button {
      padding: 6px 10px;
      color: #fff;
      font-size: 14px;
      background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#a4d04a), to(#459300));
      text-shadow: #050505 0 -1px 0;
      background-color: #459300;
      -moz-border-radius: 4px;
      -webkit-border-radius: 4px;
      border-radius: 4px;
      border: solid 1px transparent;
      font-weight: bold;
      cursor: pointer;
      letter-spacing: 1px;
    }

    input.button:hover {
      background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#a4d04a), to(#a4d04a), color-stop(80%, #76b226));
      text-shadow: #050505 0 -1px 2px;
      background-color: #a4d04a;
      color: #fff;
    }

  </style>
</head>

<body id="login">

<div id="login-wrapper" class="png_bg">
  <div id="login-top">
    <img src="images/jbpm-console-ng.png" alt="jBPM Workbench Logo" title="Powered By jBPM"/>
  </div>
  <div id="login-content">
    <form action="<%= request.getContextPath() %>/" method="POST">
      <p>
        <% if (request.getParameter("gwt.codesvr") != null) { %>
        <input type="hidden" name="gwt.codesvr" value="<%= org.owasp.encoder.Encode.forHtmlAttribute(request.getParameter("gwt.codesvr")) %>"/>
        <% } %>
        <span>Logout successful</span>
      </p>
      <br style="clear: both;"/>
      <p>
        <input class="button" type="submit" value='Login again'/>
      </p>
    </form>
  </div>
</div>
<div id="dummy"></div>
<div id="dummy2"></div>
</body>
</html>