package SeniorProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseHelper
{
    private String user = "root", pwd = "sherman1";
    private String db_url = "jdbc:mysql://localhost/senior_project";

    private Connection con;

    public DatabaseHelper()
    {
    }

    public void connect() throws ClassNotFoundException, SQLException
    {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(db_url, user, pwd);
    }

    public void disconnect() throws SQLException
    {
        if(!con.isClosed())
            con.close();
    }

    public String displayResultSet(ResultSet rs) throws SQLException
    {
        int count = 0;
        ResultSetMetaData md = rs.getMetaData();

        StringBuilder str = new StringBuilder();

        while(rs.next())
        {
            count++;
            str.append("{");

            for(int i = 1; i <= md.getColumnCount(); i++)
            {
                str.append("\"").append(md.getColumnLabel(i)).append("\":\"")
                        .append(rs.getString(i)).append("\"");

                if(i < md.getColumnCount())
                    str.append(",");
            }

            str.append("}");

            if(!rs.isLast())
                str.append(",");
        }

        rs.close();

        return "{ \"total\":"+count+", \"data\":["+str.toString()+"]}";
    }

    public ResultSet getAllTestInfo() throws SQLException
    {
        return getTestInfo(null);
    }

    public ResultSet getTestInfo(String test_id) throws SQLException
    {
        // Just getting a test run from a database
        ResultSet rs = null;
        PreparedStatement ps;
        String where_clause = "";

        if(test_id != null)
            where_clause = " where TI.id=?";

        String infoQuery = "select TI.id, TN.test_name, TI.start, TI.end, TI.notes,"
                + " TI.rank, TI.client_filename, TI.server_filename, TI.user"
                + " from test_info TI"
                + " join test_name TN on TN.id=TI.test_name_id"
                + where_clause;
        ps = con.prepareStatement(infoQuery);

        if(test_id != null)
            ps.setString(1, test_id);

        rs = ps.executeQuery();

        return rs;
    }

    public ResultSet getTestData(String test_id, String node_id, int limit) throws SQLException
    {
        ResultSet rs = null;

        String min_time = "";
        String query = "select min(timestamp) from tests" +
                " where info_id=? and node=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, test_id);
        ps.setString(2, node_id);
        System.out.println(ps.toString());
        
        rs = ps.executeQuery();
        if(rs.next())
            min_time = rs.getString(1);
        ps.close();
        rs.close();

        String clientServerQuery = "select time_to_sec(timediff(timestamp, '"+min_time+"')) as time, watts as power"
                + " from tests"
                + " where info_id=? and node=?"
                + " limit "+limit;
        PreparedStatement dataPs = con.prepareStatement(clientServerQuery);
        dataPs.setString(1, test_id);
        dataPs.setString(2, node_id);

        System.out.println(dataPs.toString());
        rs = dataPs.executeQuery();

        return rs;
    }

    public ResultSet getCombinedTestData(String test_id, int limit) throws SQLException
    {
        ResultSet rs = null;

        String min_time = "";
        String query = "select min(timestamp) from tests" +
                " where info_id=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, test_id);
        System.out.println(ps.toString());

        rs = ps.executeQuery();
        if(rs.next())
            min_time = rs.getString(1);
        ps.close();
        rs.close();

        String clientServerQuery = "select time_to_sec(timediff(timestamp, '"+min_time+"')) as time,"
                + " sum(watts) as power"
                + " from tests"
                + " where info_id=?"
                + " group by time"
                + " limit "+limit;
        PreparedStatement dataPs = con.prepareStatement(clientServerQuery);
        dataPs.setString(1, test_id);

        System.out.println(dataPs.toString());
        rs = dataPs.executeQuery();

        return rs;
    }

}
