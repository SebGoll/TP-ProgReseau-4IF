/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread
        extends Thread {

    private Socket clientSocket;
    public boolean messageSent;


    ClientThread(Socket s) {
        this.clientSocket = s;
    }

    /**
     * receives a request from client then sends an echo to the client
     *
     * @param clientSocket the client socket
     **/
    public void run() {
        try {
            BufferedReader socIn = null;
            socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            while (true) {
                String line = socIn.readLine();
                messageSent = true;
                socOut.println(line);
                System.out.println("MessageState: "+messageSent);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    public void sendMessage(String message) {

        try {

            PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            socOut.println(message);

        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

}
