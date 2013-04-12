<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.jbpm.console.ng.jBPMShowcase/jBPM.html?message=Login failed: Not Authorized";
  response.sendRedirect(redirectURL);
%>