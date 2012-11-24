<%@page import="SeniorProject.WebApp" %>

<%
String first_time = request.getParameter("first");

if(first_time != null)
    out.print("{total:0, data:[]}");
else
{
    String seconds = request.getParameter("seconds");
    String node = request.getParameter("node");

    out.print(WebApp.viewCurrentData(Integer.parseInt(seconds), node));
}

%>