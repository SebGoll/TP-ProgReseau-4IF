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
    protected Socket remote;

    protected void error404() throws IOException {
        returnHeader(404, "text/html");
        out.flush();


        Files.copy(Path.of("ressources/error404.html"), remote.getOutputStream());

    }

    protected void error400() throws IOException {
        returnHeader(400, "text/html");
        out.flush();

        Files.copy(Path.of("ressources/error400.html"), remote.getOutputStream());
    }

    protected void error405() {
        returnHeader(405, "text/html");
        out.flush();
    }

    protected void error500() {
        returnHeader(500, "text/html");
        out.flush();

        try {
            Files.copy(Path.of("ressources/error500.html"), remote.getOutputStream());
        } catch (Exception e) {
            System.out.println("Erreur de connection");
            e.printStackTrace();
        }
    }

    protected void displayRessource(String ressource, String ressourceType) {
        switch (ressourceType) {
            case "html" -> ressourceType = "text/html";
            case "png" -> ressourceType = "image/png";
            case "gif" -> ressourceType = "image/gif";
            case "mp4" -> ressourceType = "video/mp4";

            case "ico" -> ressourceType = "image/x-icon";
            case "mp3" -> ressourceType = "audio/mpeg";
            default -> ressourceType = "text/plain";
        }
        File file = new File(ressource);
        System.out.println(file);
        try {
            if (file.isFile()) {

                returnHeader(200, ressourceType);

                Files.copy(file.toPath(), remote.getOutputStream());

            } else {
                error404();
            }
        } catch (Exception e) {
            error500();
            e.printStackTrace();
        }
    }


    protected void methodGET(List<String> requestHeader) {
        System.out.println("Method GET : " + requestHeader.get(0));
        String[] temp = requestHeader.get(0).split(" ");
        String ressource = "ressources" + temp[1];
        if (Objects.equals(temp[1], "/")) {
            ressource = "ressources/index.html";

        }
        String ressourceType = ressource.split("\\.")[1];
        displayRessource(ressource, ressourceType);

    }

    protected void methodHEAD(List<String> requestHeader) {
        System.out.println("Method GET : " + requestHeader.get(0));
        String[] temp = requestHeader.get(0).split(" ");
        String ressource = "ressources" + temp[1];
        if (Objects.equals(temp[1], "/")) {
            ressource = "ressources/index.html";

        }
        String ressourceType = ressource.split("\\.")[1];

        switch (ressourceType) {
            case "html" -> ressourceType = "text/html";
            case "png" -> ressourceType = "image/png";
            case "gif" -> ressourceType = "image/gif";
            case "mp4" -> ressourceType = "video/mp4";

            case "ico" -> ressourceType = "image/x-icon";
            case "mp3" -> ressourceType = "audio/mpeg";
            default -> ressourceType = "text/plain";
        }
        File file = new File(ressource);
        System.out.println(file);

        if (file.isFile()) {
            returnHeader(200, ressourceType);
        } else {
            try {
                error404();
            } catch (Exception e) {
                error500();
            }
        }

    }

    private void methodDELETE(List<String> requestHeader) {
        String[] temp = requestHeader.get(0).split(" ");
        String ressource = "ressources" + temp[1];
        if (Objects.equals(temp[1], "/")) {
            try {
                error405();
            } catch (Exception e) {
                error500();
            }
            return;
        }
        File fileToDelete = new File(ressource);
        fileToDelete.delete();
        returnHeader(200, "text/plain");

    }

    protected void methodPOST(List<String> requestHeader, BufferedReader in){
        Integer contentLength = Integer.parseInt(requestHeader.get(3).split(": ")[1]);
        System.out.println("Method POST : " + requestHeader.get(0));
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

        String path = "ressources/" + postArgumentsHashTable.get("fname") + postArgumentsHashTable.get("lname") + ".html";
        File personFile = new File(path);

        try {
            if (personFile.createNewFile()) {

                String content = Files.readString(Path.of("ressources/template.html"));
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
            e.printStackTrace();
            return;
        }

        System.out.println("Waiting for connection");
        for (; ; ) {
            try {
                // wait for a connection
                remote = s.accept();
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

                do {
                    str = in.readLine();
                    httpRequest.add(str);
                } while (str != null && !str.equals(""));
                System.out.println(httpRequest);

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
                        returnHeader(200, "text/html");
                        break;
                    case "PUT":
                        break;
                    default:
                        error400();
                }

                out.flush();
                remote.close();
            } catch (Exception e) {
                error500();
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

    protected void showPage(String page, Integer code) {

        System.out.println(page);

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
}
