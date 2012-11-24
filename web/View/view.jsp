<%@page import="SeniorProject.WebApp" %>

<%
String task = request.getParameter("task");

if(task.equals("getData"))
{
    String test_id = request.getParameter("test_id");
    String node_id = request.getParameter("node_id");

    out.print(WebApp.viewData(test_id, node_id));
}
else if(task.equals("getInfo"))
{
    String id = request.getParameter("id");

    out.print(WebApp.viewTestInfo(id));
}
%>