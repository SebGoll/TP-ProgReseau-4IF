/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class EchoServerMultiThreaded {

    /**
     * main method
     *
     * @param args [0] : echoServer port
     **/
    public static void main(String args[]) {
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            MasterThread mt = new MasterThread();
            mt.start();

            System.out.println("Server ready...");
            while (true) {
                //Nouvelle connexion
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                System.out.println("ID: " + clientSocket.getRemoteSocketAddress());
                ClientThread ct = new ClientThread(clientSocket);

                mt.threadList.add(ct);
                mt.threadTable.put(clientSocket.getRemoteSocketAddress(),ct.getId());
                ct.start();


            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }
}
