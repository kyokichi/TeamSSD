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
else if(task.equals("getTestNames"))
{
    out.print(WebApp.viewTestNames());
}
else if(task.equals("getFiles"))
{
    out.print(WebApp.viewFiles());
}


else if(task.equals("deleteTest"))
{
    String id = request.getParameter("id");

    out.print(WebApp.deleteTest(id));
}
else if(task.equals("editTest"))
{
    String id = request.getParameter("id");
    String notes = request.getParameter("notes");
    String rank = request.getParameter("rank");

    out.print(WebApp.editTest(id, notes, rank));
}

else if(task.equals("editTestName"))
{
    String id = request.getParameter("id");
    String name = request.getParameter("name");

    out.print(WebApp.editAddTestName(id, name));
}
else if(task.equals("addTestName"))
{
    String name = request.getParameter("name");

    out.print(WebApp.editAddTestName(null, name));
}

else if(task.equals("loadTest"))
{
    String test_name_id = request.getParameter("test_name");
    String client = request.getParameter("client_file");
    String server = request.getParameter("server_file");

    out.print(WebApp.loadTest(test_name_id, client, server));
}
%>