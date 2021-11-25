/**
 *EchoServerMultiThreaded
 * @author Louis Hasenfratz,Sebastien Goll
 */

package stream;

import Data.GroupData;
import Data.SharedData;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main Class of the server, create all the datastructures used and accept connexions
 */
public class EchoServerMultiThreaded {

    /**
     * main method
     * @param args [0] : echoServer port
     **/
    public static void main(String[] args) {
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            SharedData sd = new SharedData();
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            sd.groupDataTable.put((long) 1, new GroupData());
            sd.groupDataTable.put((long) 2, new GroupData());

            MasterThread mt = new MasterThread(sd);
            mt.start();

            System.out.println("Server ready...");
            while (true) {
                //New connexion
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                ClientThread ct = new ClientThread(clientSocket, sd);
                sd.threadList.add(ct);
                sd.threadTable.put(clientSocket.getRemoteSocketAddress(), ct.getId());
                ct.start();


            }
        } catch (Exception e) {
            System.err.println("Error in EchoServerMultiThreaded:" + e);
        }
    }
}
