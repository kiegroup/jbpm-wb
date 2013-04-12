<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.jbpm.console.ng.jBPMShowcase/jBPM.html?message=Login failed: Invalid UserName or Password";
  response.sendRedirect(redirectURL);
%>