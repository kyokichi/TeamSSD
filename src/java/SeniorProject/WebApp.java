package SeniorProject;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static String viewCurrentData(int start, String node)
    {
        return "{time:"+start+", power:"+ToolsHelper.getCurrReading(node)+"}";
    }
    
}
