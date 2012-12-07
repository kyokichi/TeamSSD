package SeniorProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebApp
{
    //The id can be null; I handle that
    public static String viewTestInfo(String id)
    {
        DatabaseHelper db = new DatabaseHelper();
        String output = null;

        try
        {
            db.connect();
            output = db.displayResultSet(db.getTestInfo(id));
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try {
                db.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return output;
    }

    public static String viewTestNames()
    {
        DatabaseHelper db = new DatabaseHelper();
        String output = null;

        try
        {
            db.connect();
            output = db.displayResultSet(db.getTestNames());
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try {
                db.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return output;
    }

    public static String viewData(String test_id, String node_id)
    {
        DatabaseHelper db = new DatabaseHelper();
        ResultSet data = null;
        String output = null;

        try
        {
            db.connect();

            if(node_id == null)
                data = db.getCombinedTestData(test_id, 100);
            else
                data = db.getTestData(test_id, node_id, 100);

            output = db.displayResultSet(data);
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try {
                db.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return output;
    }

    public static String loadTest(String test_name_id, String client_filename,
            String server_filename)
    {
        DatabaseHelper db = new DatabaseHelper();
        String output = null;

        try
        {
            db.connect();
            db.loadTest(client_filename, server_filename, test_name_id);
            output = "Success.";
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ParseException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            output = ex+"";
        }
        catch (SQLException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            output = ex+"";
        }
        finally
        {
            try {
                db.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ToolsHelper.getJson(true, output);
    }


    public static String deleteTest(String test_id)
    {
        DatabaseHelper db = new DatabaseHelper();
        String output = null;

        try
        {
            db.connect();
            db.deleteTest(test_id);
            output = "Success.";
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            output = ex+"";
        }
        catch (SQLException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            output = ex+"";
        }
        finally
        {
            try {
                db.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return output;
    }

    public static String editTest(String test_id, String notes, String rank)
    {
        DatabaseHelper db = new DatabaseHelper();
        String output = null;

        try
        {
            db.connect();
            db.editTest(rank, notes, test_id);
            output = "Success.";
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            output = ex+"";
        }
        catch (SQLException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            output = ex+"";
        }
        finally
        {
            try {
                db.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ToolsHelper.getJson(true, output);
    }

    public static String editAddTestName(String id, String test_name)
    {
        DatabaseHelper db = new DatabaseHelper();
        String output = null;

        try
        {
            db.connect();

            if(id == null)
                db.addTestName(test_name);
            else
                db.editTestName(test_name, id);

            output = ToolsHelper.getJson(true,"Success.");
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            output = ToolsHelper.getJson(false, ex+"");
        }
        catch (SQLException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            output = ToolsHelper.getJson(false, ex+"");
        }
        finally
        {
            try {
                db.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return output;
    }

    public static String viewCurrentData(int start, String node)
    {
        return "{time:"+start+", power:"+ToolsHelper.getCurrReading(node)+"}";
    }

    public static String viewFiles()
    {
        return ToolsHelper.getJson(ToolsHelper.getFiles());
    }




    /* Stuff for Scheduler */
    public static String scheduleJob(String date, String sec, String test_name, String time)
    {
        String output = "";

        try
        {
            ScheduleTimer.passParam(date, sec, test_name, time);
            output = "Success.";
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            output = ex+"";
        }
        catch (SQLException ex) {
            Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
            output = ex+"";
        }

        return ToolsHelper.getJson(true, output);
    }
    
}
