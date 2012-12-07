package SeniorProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 *
 * @author tiffany
 */
public class ScheduleJob {
        
    private String user = "root", pwd = "sherman1";
    private String db_url = "jdbc:mysql://localhost/senior_project";

    private Connection con = null;

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

    public String addTestInfoRecord (String testName) throws ClassNotFoundException,
    SQLException {
        connect();
        PreparedStatement st;
        ResultSet rs;

        String query = "insert into test_name (test_name) values (?)";

        st = con.prepareStatement(query);
        st.setString(1, testName);
        st.executeUpdate();
        st.close();

        String info_id;
        query = "select max(id) from test_name";
        st = con.prepareStatement(query);
        rs = st.executeQuery();

        if(rs.next())
            info_id = rs.getString(1);
        else
        info_id = "something bad happened";
        st.close();
        rs.close();
        
        query = "insert into test_info (test_name_id, client_filename, server_filename)" +
                            " values (?, ?, ?)";

        st = con.prepareStatement(query);
        st.setString(1, info_id);
        st.setString(2, "client_" + testName);
        st.setString(3, "server_" + testName);
        st.executeUpdate();
        st.close();

        query = "select max(id) from test_info";
        st = con.prepareStatement(query);
        rs = st.executeQuery();

        String info_id2;
        if(rs.next())
            info_id2 = rs.getString(1);
        else
            info_id2 = "something bad happened";
        st.close();
        rs.close();

        return info_id2;
    }

    public void addRecords(String id, String info_id) throws SQLException,
    ClassNotFoundException
    {
        connect();
        int num;
        PreparedStatement st, stmt;
        ResultSet rs;

      /*  String query = "insert into test_info (test_name_id)" +
                            " values (?)";

        st = con.prepareStatement(query);
        st.setString(1, id);
        st.executeUpdate();
        st.close();

        query = "select max(id) from test_info";
        st = con.prepareStatement(query);
        rs = st.executeQuery();

        String info_id;
        if(rs.next())
            info_id = rs.getString(1);
        else
            info_id = "something bad happened";
        st.close();
        rs.close();*/

        String query = "load data local infile ? " +
            "into table tests " +
            "lines terminated by '\n' " +
            "(watts) " +
            "set info_id=?, timestamp=str_to_date(@date, '[%H:%i:%S]'), node=1";
        stmt = con.prepareStatement(query);
        stmt.setString(1, "/etc/Portal/tout_c2n3.txt");
        stmt.setString(2, info_id);
        num = stmt.executeUpdate();
        stmt.close();

        query = "load data local infile ? " +
            "into table tests " +
            "lines terminated by '\n' " +
            "(watts) " +
            "set info_id=?, timestamp=str_to_date(@date, '[%H:%i:%S]'), node=2";
        PreparedStatement stmt2 = con.prepareStatement(query);
        stmt2.setString(1, "/etc/Portal/tout.txt");
        stmt2.setString(2, info_id);
        num = stmt2.executeUpdate();
        stmt2.close();

        query = "update test_info set end=now() where id=?";
        stmt2 = con.prepareStatement(query);
        stmt2.setString(1, info_id);
        num = stmt2.executeUpdate();
        stmt2.close();

        //return num;
    }

    public long futureTime(int year, int month, int day, int hour, int minute)
    {
        Calendar future = Calendar.getInstance(); //future time
        future.set(Calendar.YEAR, year);
        future.set(Calendar.MONTH, month);
        future.set(Calendar.DATE, day);
        future.set(Calendar.HOUR_OF_DAY, hour);
        future.set(Calendar.MINUTE, minute);
        //get current time
        Calendar now = Calendar.getInstance();
        //time difference between now and future in seconds
        long secondsDiff = (future.getTimeInMillis() -
                            now.getTimeInMillis())/(1000);

        return secondsDiff;
    }

    public static void main(String [] args)
    {
        ScheduleJob no = new ScheduleJob();
        System.out.println(no.futureTime(2012, 11, 6, 18, 17));

    }
}
