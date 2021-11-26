/**
 * WebServer
 *
 * @author Goll Sebastien & Hasenfratz Louis
 */

package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
 * <p>
 * The server supports GET, POST, DELETE and HEAD methods
 * The server supports html,png,mp3,mp4,gif
 * Codes 200, 201, 400, 404, 405, 500 are implemented
 *
 * @author Jeff Heaton, completed by Sebastien Goll and Louis Hasenfratz
 * @version 1.0
 */
public class WebServer {

    protected PrintWriter out;
    protected Socket remote;

    /**
     * Start the application.
     *
     * @param args Command line parameters are not used.
     */
    public static void main(String[] args) {
        WebServer ws = new WebServer();
        ws.start();
    }

    /**
     * WebServer constructor.
     * Start the server on port 3000
     * The server then wait for connections, get the incoming request and handle it.
     */
    protected void start() {

        /*Start the server on port 3000*/
        ServerSocket serverSocket;
        System.out.println("Webserver starting up on port 3000");
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            serverSocket = new ServerSocket(3000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
            return;
        }

        /*Wait for connections*/
        System.out.println("Waiting for connection");
        for (; ; ) {
            try {
                // wait for a connection
                remote = serverSocket.accept();

                // remote is now the connected socket
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
                out = new PrintWriter(remote.getOutputStream());

                // read the data sent. We basically ignore it,
                // stop reading once a blank line is hit. This
                // blank line signals the end of the client HTTP
                String str;
                List<String> httpRequest = new ArrayList<>();
                do {
                    str = in.readLine();
                    httpRequest.add(str);
                } while (str != null && !str.equals(""));

                /*Depending on the HTTP method type, handle the request differently*/
                switch (httpRequest.get(0).split(" ")[0]) {
                    case "GET":
                        methodGET(httpRequest);
                        break;

                    case "POST":
                        methodPOST(httpRequest, in);
                        break;

                    case "DELETE":
                        methodDELETE(httpRequest);
                        break;
                    case "HEAD":
                        methodHEAD(httpRequest);
                        break;
                    case "PUT":
                        break;
                    default:
                        error400();
                        break;
                }

                /*Close the BufferReader and Socket*/
                out.flush();
                remote.close();
            } catch (Exception e) {
                error500();
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle the GET request by identifying the resource needed and displaying it
     *
     * @param requestHeader A list of strings corresponding to the request received by the server
     */
    protected void methodGET(List<String> requestHeader) {
        String[] temp = requestHeader.get(0).split(" ");

        String resource = "resources" + temp[1].split("\\?")[0];
        if (Objects.equals(temp[1], "/")) {
            resource = "resources/index.html";

        }
        String resourceType = resource.split("\\.")[1];
        displayResource(resource, resourceType);
    }

    /**
     * Fetch the information given in the POST request and create a new HTML page with them
     *
     * @param requestHeader A list of strings corresponding to the request received by the server
     * @param in            The BufferedReader needed to read the information given after the POST method
     */
    protected void methodPOST(List<String> requestHeader, BufferedReader in) {
        int contentLength = Integer.parseInt(requestHeader.get(3).split(": ")[1]);

        /*Read the value given by the user*/
        char[] postArguments = new char[contentLength];
        try {
            in.read(postArguments, 0, contentLength);
        } catch (IOException e) {
            error500();
            e.printStackTrace();
        }
        String[] postArgumentsList = String.valueOf(postArguments).split("&");
        HashMap<String, String> postArgumentsHashTable = new HashMap<>();
        for (String argument : postArgumentsList) {
            postArgumentsHashTable.put(argument.split("=")[0], argument.split("=")[1]);
        }

        /*Create a new page HTML and displays it*/
        String path = "resources/" + postArgumentsHashTable.get("fname") + postArgumentsHashTable.get("lname") + ".html";
        File personFile = new File(path);
        try {
            if (personFile.createNewFile()) {

                String content = Files.readString(Path.of("resources/template.html"));
                String title = "Page de " + postArgumentsHashTable.get("lname");
                String body = "<h1>Nom = " + postArgumentsHashTable.get("lname") + "</h1>\n" +
                        "<h1>Prenom = " + postArgumentsHashTable.get("fname") + "</h1>\n";
                String pageName = postArgumentsHashTable.get("fname") + postArgumentsHashTable.get("lname") + ".html";

                content = content.replace("$title", title);
                content = content.replace("$body", body);
                content = content.replace("$pagename", pageName);


                FileWriter fw = new FileWriter(personFile);
                fw.write(content);
                fw.close();
                showPage(path, 201);
            } else {
                showPage(path, 200);
            }
        } catch (IOException e) {
            error500();
            e.printStackTrace();
        }
    }

    /**
     * Same as methodGET but return only the header
     *
     * @param requestHeader A list of strings corresponding to the request received by the server
     */
    protected void methodHEAD(List<String> requestHeader) {
        String[] temp = requestHeader.get(0).split(" ");
        String resource = "resources" + temp[1];
        if (Objects.equals(temp[1], "/")) {
            resource = "resources/index.html";

        }
        String resourceType = resource.split("\\.")[1];

        switch (resourceType) {
            case "html" -> resourceType = "text/html";
            case "png" -> resourceType = "image/png";
            case "gif" -> resourceType = "image/gif";
            case "mp4" -> resourceType = "video/mp4";

            case "ico" -> resourceType = "image/x-icon";
            case "mp3" -> resourceType = "audio/mpeg";
            default -> resourceType = "text/plain";
        }
        File file = new File(resource);

        if (file.isFile()) {
            returnHeader(200, resourceType);
        } else {
            try {
                error404();
            } catch (Exception e) {
                error500();
            }
        }

    }

    /**
     * Delete the resource given in the method
     *
     * @param requestHeader A list of strings corresponding to the request received by the server
     */
    private void methodDELETE(List<String> requestHeader) {
        String[] temp = requestHeader.get(0).split(" ");
        String resource = "resources" + temp[1];
        if (Objects.equals(temp[1], "/")) {
            try {
                error405();
            } catch (Exception e) {
                error500();
            }
            return;
        }
        File fileToDelete = new File(resource);
        fileToDelete.delete();
        returnHeader(200, "text/plain");

    }

    /**
     * Given a resource, displays it to the user
     *
     * @param resource     The resource path the user want to access
     * @param resourceType The extension of the resource (eg. html, png, mp3, gif...)
     */
    protected void displayResource(String resource, String resourceType) {
        switch (resourceType) {
            case "html" -> resourceType = "text/html";
            case "png" -> resourceType = "image/png";
            case "gif" -> resourceType = "image/gif";
            case "mp4" -> resourceType = "video/mp4";
            case "ico" -> resourceType = "image/x-icon";
            case "mp3" -> resourceType = "audio/mpeg";
            default -> resourceType = "text/plain";
        }
        File file = new File(resource);
        try {
            if (file.isFile()) {

                returnHeader(200, resourceType);

                Files.copy(file.toPath(), remote.getOutputStream());

            } else {
                error404();
            }
        } catch (Exception e) {
            error500();
            e.printStackTrace();
        }
    }

    /**
     * Display a page html and return a header with the code
     *
     * @param page The path of the html page to display
     * @param code The code to put in the header
     */
    protected void showPage(String page, Integer code) {


        File f = new File(page);
        if (f.exists() && !f.isDirectory()) {
            // Send the response
            // Send the headers
            returnHeader(code, "text/html");
            // Send the HTML page
            try {
                Scanner scanner = new Scanner(f, StandardCharsets.UTF_8);
                while (scanner.hasNextLine()) {
                    out.println(scanner.nextLine());
                }
                scanner.close();
            } catch (IOException e) {
                error500();
                e.printStackTrace();
            }
        }
    }

    /**
     * Write the header of the response
     *
     * @param code The HTTP response code of the header
     * @param type The type of resource (MIME)
     */
    protected void returnHeader(Integer code, String type) {
        out.print("HTTP/1.0 ");
        switch (code) {
            case 200 -> out.println("200 OK");
            case 201 -> out.println("201 Created");
            case 400 -> out.println("400 Bad Request");
            case 404 -> out.println("404 Not Found");
            case 405 -> out.println("405 Method Not Allowed");
        }

        out.println("Content-Type: " + type);
        out.println("Server: Bot");

        out.println("");
        out.flush();
    }

    /**
     * Give a error 404
     *
     * @throws IOException If an I/O error occurs when creating the output stream or if the socket is not connected
     */
    protected void error404() throws IOException {
        returnHeader(404, "text/html");
        out.flush();

        System.out.println("Error 404");
        Files.copy(Path.of("resources/error404.html"), remote.getOutputStream());

    }

    /**
     * Give a error 400
     *
     * @throws IOException If an I/O error occurs when creating the output stream or if the socket is not connected
     */
    protected void error400() throws IOException {
        returnHeader(400, "text/html");
        out.flush();
        System.out.println("Error 400");
        Files.copy(Path.of("resources/error400.html"), remote.getOutputStream());
    }

    /**
     * Give a error 405
     */
    protected void error405() throws IOException {
        returnHeader(405, "text/html");
        out.flush();
        System.out.println("Error 405");
        Files.copy(Path.of("resources/error405.html"), remote.getOutputStream());
    }

    /**
     * Give a error 500
     */
    protected void error500() {
        returnHeader(500, "text/html");
        out.flush();
        System.out.println("Error 500");
        try {
            Files.copy(Path.of("resources/error500.html"), remote.getOutputStream());
        } catch (Exception e) {
            System.out.println("Erreur de connection");
            e.printStackTrace();
        }
    }


}
