package SeniorProject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseHelper
{
    public static void main(String[] args)
    {
        try {
            DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            Date indate = inputFormat.parse("Fri Oct 26 18:06:27 PDT 2012");
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(outputFormat.format(indate));
        }
        catch (ParseException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private String user = "root", pwd = "sherman1";
    private String db_url = "jdbc:mysql://localhost/senior_project";
    private String dir = "/home/alexis/";

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

            String query = "UPDATE test_info SET rank=?, notes=? WHERE id=?";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, rank);
            stmt.setString(2, notes);
            stmt.setString(3, id);
            num = stmt.executeUpdate();

            System.out.println(stmt.toString());

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

    public int loadTest(String client_filename, String server_filename, String test_name_id) 
            throws SQLException, FileNotFoundException, IOException, ParseException
    {
        int num;
        ResultSet rs;
        BufferedReader br = null;

        try
        {
            // First we get the dates from the file
            String start = "", end = "", current;
            int line = 0;

            br = new BufferedReader(new FileReader(client_filename));

            while((current = br.readLine()) != null)
            {
                line++;

                if(line == 1) //skip the first line
                    continue;

                if(line > 3) //exit if the line is past the 3rd
                    break;

                DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                Date date = inputFormat.parse(current);
                DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println(outputFormat.format(date));

                if(line == 2)
                    start = outputFormat.format(date);
                if(line == 3)
                    end = outputFormat.format(date);
            }

            con.setAutoCommit(false);
            String query = "insert into test_info (test_name_id, start, end, client_filename, server_filename)" +
                            " values (?, ?, ?, ?, ?)";

            PreparedStatement st = con.prepareStatement(query);
            st.setString(1, test_name_id);
            st.setString(2, start);
            st.setString(3, end);
            st.setString(4, client_filename);
            st.setString(5, server_filename);
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
            stmt.setString(1, client_filename);
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
            stmt2.setString(1, server_filename);
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
        finally
        {
            br.close();
        }

        return num;
    }

    public ResultSet getTestNames() throws SQLException
    {
        ResultSet rs = null;
        PreparedStatement ps;

        String query = "SELECT id, test_name"
                + " FROM test_name";
        ps = con.prepareStatement(query);

        rs = ps.executeQuery();
     //   ps.close();

        return rs;
    }

    public int editTestName(String newName, String id) throws SQLException
    {
            int num;

            String query = "UPDATE test_name SET test_name=? WHERE id=?";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, newName);
            stmt.setString(2, id);
            num = stmt.executeUpdate();

            stmt.close();

            return num;
    }

    public int addTestName(String name) throws SQLException
    {
            int num;

            String query = "INSERT INTO test_name (test_name) VALUES (?)";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, name);
            num = stmt.executeUpdate();

            stmt.close();

            return num;
    }

    public int deleteTestName(String name) throws SQLException
    {
            int num;

            String query = "DELETE FROM test_name WHERE test_name=?";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, name);
            num = stmt.executeUpdate();

            stmt.close();

            return num;
    }

}
