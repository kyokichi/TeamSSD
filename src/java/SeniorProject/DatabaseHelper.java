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

    public int editTest(String rank, String notes, String id) throws SQLException
    {
            int num;

            String query = "UPDATE test_info SET rank=?, notes=? WHERE test_name_id=?";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, rank);
            stmt.setString(2, notes);
            stmt.setString(3, id);
            num = stmt.executeUpdate();

            stmt.close();

            if(!con.isClosed())
                con.close();

            return num;

    }

    public int deleteTest(String id) throws SQLException
    {
        int num;

        try
        {
            con.setAutoCommit(false);

            String query = "DELETE FROM test_info WHERE id=?";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, id);
            num = stmt.executeUpdate();
            stmt.close();

            query = "DELETE FROM tests WHERE info_id=?";

            PreparedStatement stmt2 = con.prepareStatement(query);
            stmt2.setString(1, id);
            num += stmt2.executeUpdate();
            stmt2.close();

            con.commit();
            System.out.println("Time: Transaction committed.");
            con.setAutoCommit(true);
        }
        catch (SQLException ex)
        {
            if(con != null)
            {
                con.rollback();
                System.out.println("Time: Transaction rolled back.");
            }

            throw ex;
        }

        return num;
    }

    public int loadTest(String client_filename, String server_filename, String id) throws SQLException
    {
        int num;
        ResultSet rs;

        try
        {
            con.setAutoCommit(false);
            String query = "insert into test_info (test_name_id, client_filename, server_filename)" +
                            " values (?, ?, ?)";

            PreparedStatement st = con.prepareStatement(query);
            st.setString(1, id);
            st.setString(2, client_filename);
            st.setString(3, server_filename);
            st.executeUpdate();
            st.close();

            String info_id;
            query = "select max(id) from test_info";
            st = con.prepareStatement(query);
            rs = st.executeQuery();

            if(rs.next())
                info_id = rs.getString(1);
            else
                info_id = "something bad happened";
            st.close();
            rs.close();

            query = "load data local infile ? " +
            "into table tests " +
            "fields terminated by ' ' " +
            "lines terminated by '\n' " +
            "ignore 5 lines " +
            "(@date, watts) " +
            "set info_id=?, timestamp=str_to_date(@date, '[%H:%i:%S]'), node=1";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, "/etc/Portal/demo/" + client_filename);
            stmt.setString(2, info_id);
            num = stmt.executeUpdate();
            stmt.close();

            query = "load data local infile ? " +
            "into table tests " +
            "fields terminated by ' ' " +
            "lines terminated by '\n' " +
            "ignore 5 lines " +
            "(@date, watts) " +
            "set info_id=?, timestamp=str_to_date(@date, '[%H:%i:%S]'), node=2";
            PreparedStatement stmt2 = con.prepareStatement(query);
            stmt2.setString(1, "/etc/Portal/demo/" + server_filename);
            stmt2.setString(2, info_id);
            num += stmt2.executeUpdate();
            stmt2.close();

            con.commit();
            System.out.println("Time: Transaction committed.");
            con.setAutoCommit(true);
        }
        catch (SQLException ex)
        {
            if(con != null)
            {
                con.rollback();
                System.out.println("Time: Transaction rolled back.");
            }

            throw ex;
        }

        return num;
    }

    public ResultSet getTestNames() throws SQLException
    {
        ResultSet rs = null;
        PreparedStatement ps;

        String query = "SELECT *"
                + " FROM test_name";
        ps = con.prepareStatement(query);

        rs = ps.executeQuery();
     //   ps.close();

        return rs;
    }

}
