<%@page import="SeniorProject.WebApp" %>

<%
    String date = request.getParameter("date");
    String sec = request.getParameter("seconds");
    String test_name = request.getParameter("test_name");
    String time = request.getParameter("time");

    out.print(WebApp.scheduleJob(date, sec, test_name, time));
%>