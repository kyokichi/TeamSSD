package SeniorProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToolsHelper
{
    static String dir = "/home/alexis/";

    public static String getCurrTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static double getCurrReading(String node)
    {
        File file = null;
        Scanner scan1 = null;

        try 
        {
            if (node.equals("1"))
                file = new File(dir + "Portal/tout_c2n3.txt");
            else if (node.equals("2"))
                file = new File(dir + "Portal/tout.txt");
            else
                return 0.0;
 

            scan1 = new Scanner(file);

            while (scan1.hasNext())
            {
                String line = scan1.nextLine();
                return Double.parseDouble(line);
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(ToolsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* This will catch a problem if the watts up code produced a non-number (or error) */
        catch (NumberFormatException ex) {
            Logger.getLogger(ToolsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (scan1 != null)
                scan1.close();
        }

        return 0.0;
    }


    public static String getJson(boolean status, String msg)
    {
        return "{ \"success\":"+status+", \"msg\":\""+msg+"\" }";
    }

    public static String getJson(ArrayList<String> tokens)
    {
        StringBuilder str = new StringBuilder();

        for(int i = 0; i < tokens.size(); i++)
        {
            str.append("{ \"name\" : \"").append(tokens.get(i))
                    .append("\" }");

            if(i < tokens.size()-1)
                str.append(",");
        }

        return "{ \"total\":"+tokens.size()+", \"data\":["+str.toString()+"]}";
    }

    public static ArrayList<String> getFiles()
    {
        return filePaths(new File(dir+"Portal/"));
    }

    public static ArrayList<String> filePaths(File file)
    {
        ArrayList<String> tokens = new ArrayList<String>();

        if(file.isDirectory())
        {
            File [] subFile = file.listFiles();
            for(File f : subFile)
            {
                tokens.addAll(filePaths(f));
            }
        }
        else
        {
            tokens.add(file.getAbsolutePath());
        }

        return tokens;
    }

}
