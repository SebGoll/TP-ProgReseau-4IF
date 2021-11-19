///A Simple Web Server (WebServer.java)

package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * <p>
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 *
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

    /**
     * WebServer constructor.
     */
    protected void start() {
        ServerSocket s;

        System.out.println("Webserver starting up on port 3000");
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(3000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (; ; ) {
            try {
                // wait for a connection
                Socket remote = s.accept();
                // remote is now the connected socket
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        remote.getInputStream()));
                PrintWriter out = new PrintWriter(remote.getOutputStream());

                // read the data sent. We basically ignore it,
                // stop reading once a blank line is hit. This
                // blank line signals the end of the client HTTP
                // headers.
                String str = ".";
                List<String> usefulData = new ArrayList<>();
                while (str != null && !str.equals("")) {
                    System.out.println(str);
                    if (str.startsWith("GET")) {

                        String[] method  = str.split(" ");
                        usefulData.add(method[0]);
                        usefulData.add(method[1]);


                    }
                    if (str.startsWith("POST")) {

                        String[] method  = str.split(" ");
                        usefulData.add(method[0]);
                        usefulData.add(method[1]);

                    }
                    str = in.readLine();

                }
                System.out.println(usefulData);
                showPage(usefulData.get(1),out);
                switch (usefulData.get(0)){
                    case "GET":
                        break;
                    case "POST":
                        str=".";
                        for(int i=0;i<10;i++){
                            System.out.println("i "+in.readLine());
                        }
//                        while (str != null && !str.equals("")) {
//                            System.out.println(str);
//                            str = in.readLine();
//                        }
                        break;
                    case "DELETE":
                        break;
                    case "HEA":
                        break;
                    case "PUT":
                        break;
                }


//                // Send the response
//                // Send the headers
//                out.println("HTTP/1.0 200 OK");
//                out.println("Content-Type: text/html");
//                out.println("Server: Bot");
//                // this blank line signals the end of the headers
//                out.println("");
//                // Send the HTML page
//                out.println("<H1>Welcome to the Ultra Mini-WebServer</H1>");
                out.flush();
                remote.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    /**
     * Start the application.
     *
     * @param args Command line parameters are not used.
     */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }

    protected void showPage(String page, PrintWriter out) {

        System.out.println(page);

        File f = new File("pageHTML" + page);
        if (f.exists() && !f.isDirectory()) {
            // Send the response
            // Send the headers
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: text/html");
            out.println("Server: Bot");
            // this blank line signals the end of the headers
            out.println("");

            // Send the HTML page
            try {
                Scanner scanner = new Scanner(f);
                while (scanner.hasNextLine()) {
                    out.println(scanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
