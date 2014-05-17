<%@page contentType="text/html" pageEncoding="utf-8"%><%
request.getSession().invalidate();
(new com.sun.appserv.security.ProgrammaticLogin()).logout(request, response, true);
response.sendRedirect(request.getContextPath());
%>