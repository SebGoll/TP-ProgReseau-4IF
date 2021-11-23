///A Simple Web Server (WebServer.java)

package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

    protected PrintWriter out;


    protected void methodGET(List<String> requestHeader,Socket s) throws IOException {
        System.out.println("Method GET : " + requestHeader.get(0));
        String[] temp = requestHeader.get(0).split(" ");
        String ressource = temp[1];
        String ressourceType = ressource.split("\\.")[1];
        switch (ressourceType){
            case "html":
                showPage(ressource);
                break;
            case "png":

                File image = new File("pageHTML"+ressource);


                if(image.isFile()){
                    System.out.println(image.length());
                    returnHeader(200,"image/png",image.length());
                    Scanner sc = new Scanner(image,StandardCharsets.UTF_8);
                    while(sc.hasNextByte()){
                        Byte b = sc.nextByte();
                        out.println(b);
                        System.out.println(b);
                    }
                    out.println();

//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(
//                                    new FileInputStream(image),
//                                    StandardCharsets.UTF_8));
//
//                    int c;
//                    while((c = reader.read()) != -1) {
//                        out.print(c);
//                        // Do something with your character
//                    }


//                    Files.copy(image.toPath(), s.getOutputStream());
                }

                break;

        }

    }

    protected void methodPOST(List<String> requestHeader, BufferedReader in) throws IOException {
        Integer contentLength = Integer.parseInt(requestHeader.get(3).split(": ")[1]);
        System.out.println("Method POST : " + requestHeader.get(0));
        char[] postArguments = new char[contentLength];
        in.read(postArguments, 0, contentLength);
        String[] postArgumentsList = String.valueOf(postArguments).split("&");
        HashMap<String, String> postArgumentsHashTable = new HashMap<>();
        for (String argument : postArgumentsList) {
            postArgumentsHashTable.put(argument.split("=")[0], argument.split("=")[1]);
        }

        returnHeader(200, "text/plain",(long)0);

        out.println("Nom = " + postArgumentsHashTable.get("lname"));
        out.println("Prenom = " + postArgumentsHashTable.get("fname"));
    }

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
                out = new PrintWriter(remote.getOutputStream());

                // read the data sent. We basically ignore it,
                // stop reading once a blank line is hit. This
                // blank line signals the end of the client HTTP
                // headers.
                String str = ".";
                List<String> httpRequest = new ArrayList<>();
                while (str != null && !str.equals("")) {
                    str = in.readLine();
                    httpRequest.add(str);
                }
                System.out.println(httpRequest);

                switch (httpRequest.get(0).split(" ")[0]) {
                    case "GET":
                        methodGET(httpRequest,remote);
                        break;

                    case "POST":
                        methodPOST(httpRequest, in);
                        break;

                    case "DELETE":
                        break;
                    case "HEAD":
                        returnHeader(200, "text/html",(long)0);
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
                e.printStackTrace();
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

    protected void showPage(String page) {

        System.out.println(page);

        File f = new File("pageHTML" + page);
        if (f.exists() && !f.isDirectory()) {
            // Send the response
            // Send the headers
            returnHeader(200, "text/html",(long)0);
            // Send the HTML page
            try {
                Scanner scanner = new Scanner(f,StandardCharsets.UTF_8);
                while (scanner.hasNextLine()) {
                    out.println(scanner.nextLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void returnHeader(Integer code, String type,Long Size) {
        out.println("HTTP/1.0 " + code + " OK");
        out.println("Content-Type: " + type);
        if(Objects.equals(type, "image/png")){
//            out.println("Content-Length:"+Size);
        }
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
    }
}
