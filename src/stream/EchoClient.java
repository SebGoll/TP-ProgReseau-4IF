/**
 *EchoClient
 * @author Louis Hasenfratz,Sebastien Goll
 */
package stream;

import java.io.*;
import java.net.*;

/**
 * EchoClient communicate with the chat server and is on the client side
 */
public class EchoClient {

    /**
     * main method
     * accepts a connection, receives and sends messages to the client
     **/
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;

        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
            System.exit(1);
        }

        try {
            // socket creation ==> connexion
            echoSocket = new Socket(args[0], Integer.parseInt(args[1]));
            socIn = new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream()));
            socOut = new PrintStream(echoSocket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to:" + args[0]);
            System.exit(1);
        }

        String line;

        while (true) {
            //receive message from the user and send it to the server
            if (stdIn.ready()) {
                line = stdIn.readLine();
                if (line.equals(".")) break;
                socOut.println(line);
            }
            //receive a message from the server and display it to the user
            if (socIn.ready()) {
                System.out.println(socIn.readLine());
            }

        }
        socOut.close();
        socIn.close();
        stdIn.close();
        echoSocket.close();
    }
}


