package com.andrej;

import net.minidev.json.JSONObject;

import javax.servlet.*;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.LinkedHashMap;
                                                        // This WebInitParameter is the default file path.
@WebServlet(name = "CheckRoutes", urlPatterns = {"/direct"}, initParams = {@WebInitParam(name = "filename", value = "api/example.txt")})
public class BusRouteServlet extends HttpServlet{

    private static String filename;

    public void init() throws ServletException{
        filename = getInitParameter("filename");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{

        //Take the two parameters from the URL and use them as bus stations to be checked.
        int station1 = Integer.parseInt(req.getParameter("dep_sid"));
        int station2 = Integer.parseInt(req.getParameter("arr_sid"));

    LinkedHashMap map = new LinkedHashMap<String, Object>();
        map.put("dep_sid", station1);
        map.put("arr_sid", station2);
        map.put("direct_route", checkForConnection(filename, station1, station2));

        res.getWriter().write(JSONObject.toJSONString(map));

}

    /*
     * This is the method that reads the 'example.txt' file, and line-by-line compares
     * the listed bus stations to the two stations which were queried by the user.
     */
    private boolean checkForConnection(String filename, int dep_sid, int arr_sid) throws IOException {

        // Declare both the stream and reader outside of the catch block to enable closing at the end.
        FileInputStream is = null;
        BufferedReader reader = null;

        try {
            is = new FileInputStream(filename);
            reader = new BufferedReader(new InputStreamReader(is));
            String text;

            boolean station1 = false;
            boolean station2 = false;

            // Read each line of the file to find a connection between the bus stations.
            while ((text = reader.readLine()) != null) {
                String[] values = text.split(" ");

                // Since the first number in every line is actually the route number, skip it by starting at value 1 instead of 0.
                for(int i = 1; i<values.length; i++)
                {
                    // Check if the currently selected value is equal to either the 'dep_sid' or 'arr_sid'
                    if (Integer.parseInt(values[i])==dep_sid){
                        station1 = true;
                    }
                    if (Integer.parseInt(values[i])==arr_sid){
                        station2 = true;
                    }
                    // If both booleans have been made positive by this point, end the method and return 'true'
                    if(station1 && station2){
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("There was an error while reading from the 'bus routes' file.");
            e.printStackTrace();
        }finally{
            if(is!=null)
                is.close();
            if(reader!=null)
                reader.close();
        }
        return false;
    }

    public static void main(java.lang.String[] args){
        if(args.length<1)
            System.out.println("Please provide a file name");
        else
            System.out.println(args[0]);
    }
}

