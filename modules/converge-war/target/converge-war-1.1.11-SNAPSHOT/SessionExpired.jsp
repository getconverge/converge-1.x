<%
    response.setContentType("text/xml;charset=UTF-8");
    response.setStatus(200);
    response.sendRedirect(request.getContextPath() + "/Dashboard.xhtml");
%>