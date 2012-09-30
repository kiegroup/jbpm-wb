<%
	String queryString = request.getQueryString();
    String redirectURL = "org.jbpm.console.ng.jBPMShowcase/jBPM.html?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>