<%
    String queryString = request.getQueryString();
    String redirectURL = request.getContextPath()  +"/org.jbpm.console.ng.jBPMShowcase/jBPM.html?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>