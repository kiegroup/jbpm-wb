<%
   String queryString = request.getQueryString();
    String redirectURL = request.getContextPath()  +"/org.jbpm.console.ng.jBPMShowcase/jBPM.html?"+(queryString==null?"":queryString);
    for(Object key : request.getParameterMap().keySet()){
        System.out.println("KEY -> "+key);
    }
    
    while( request.getAttributeNames().hasMoreElements()){
        System.out.println("Attr -> "+request.getAttributeNames().nextElement());
    }
    if (request.getParameterMap().containsKey("mobile")) {
        redirectURL = request.getContextPath()  +"/org.jbpm.console.ng.jBPMMobileShowcase/jBPM.html?"+(queryString==null?"":queryString);
    }
    response.sendRedirect(redirectURL);
%>