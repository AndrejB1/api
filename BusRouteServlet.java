package com.andrej;

import net.minidev.json.JSONObject;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Properties;


@WebServlet(name = "CheckRoutes", urlPatterns = {"/direct"})
public class BusRouteServlet extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{

        //Take the two parameters from the URL and use them as bus stations to be checked.
        int station1 = Integer.parseInt(req.getParameter("dep_sid"));
        int station2 = Integer.parseInt(req.getParameter("arr_sid"));//System.getProperty("filename")

        Properties props = new Properties();
        try(Reader reader = Files.newBufferedReader(Paths.get("filename.properties"))){
            props.load(reader);
        }

        LinkedHashMap map = new LinkedHashMap<String, Object>();
            map.put("dep_sid", station1);
            map.put("arr_sid", station2);
            map.put("direct_route", checkForConnection(props.getProperty("filepath"), station1, station2));

        res.getWriter().write(JSONObject.toJSONString(map));

}

    /*
     * This is the method that reads the 'example.txt' file, and line-by-line compares
     * the listed bus stations to the two stations which were queried by the user.
     */
    private boolean checkForConnection(String filename, int dep_sid, int arr_sid) throws IOException {

        try(FileInputStream is = new FileInputStream(filename)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String text;

            boolean station1 = false;
            boolean station2 = false;

            // Read each line of the file to find a connection between the bus stations.
            while ((text = reader.readLine()) != null) {
                String[] values = text.split(" ");

                // Since the first number in every line is actually the route number, skip it by starting at value 1 instead of 0.
                for (int i = 1; i < values.length; i++) {
                    // Check if the currently selected value is equal to either the 'dep_sid' or 'arr_sid'
                    if (Integer.parseInt(values[i]) == dep_sid) {
                        station1 = true;
                    }
                    if (Integer.parseInt(values[i]) == arr_sid) {
                        station2 = true;
                    }
                    // If both booleans have been made positive by this point, end the method and return 'true'
                    if (station1 && station2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
     * A main method is usually not necessary with a web application, but in this
     * case, we need to be able to pass a parameter from the command line.
     *
     * This parameter will be stored in a properties file and reloaded later
     * inside the doGet method. Tomcat will be started after this file is written.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 1) {
            System.out.println("Please enter a filepath");
        } else {
            Properties props = new Properties();

            props.setProperty("filepath", args[0]);
            try (Writer writer = Files.newBufferedWriter(Paths.get("filename.properties"))) {
                props.store(writer, null);
            }


            // Once the filename has been recorded, start catalina
            Runtime rt = Runtime.getRuntime();
            Process rr = rt.exec("cmd.exe /c catalina start");
            rr.waitFor();
            rr = rt.exec("cmd.exe /c mvn tomcat7:redeploy");
            rr.waitFor();

            String dataDirectory = Paths.get(args[0]).toAbsolutePath().toString();
            System.out.println(dataDirectory);

        }
    }
}

