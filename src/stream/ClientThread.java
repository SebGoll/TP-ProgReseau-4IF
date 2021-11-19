/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import Data.SharedData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.AbstractMap;

public class ClientThread
        extends Thread {

    private Socket clientSocket;
    public boolean messageSent;
    public SharedData sd;
    public String nom;


    ClientThread(Socket s, SharedData data) {
        this.clientSocket = s;
        this.sd = data;
        this.nom = null;
    }

    /**
     * receives a request from client then sends an echo to the client
     **/
    public void run() {
        try {
            BufferedReader socIn = null;
            socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            socOut.println("Renseignez votre pseudonyme");
            nom = socIn.readLine();
            socOut.println("Bienvenue "+nom+" vous pouvez maintenant chatter avec vos amis !");


            while (true) {
                if (socIn.ready()) {
                    String line = socIn.readLine();
                    messageSent = true;
                    sd.messagesToSend.add(new AbstractMap.SimpleEntry<>(line, nom));
                    Persistence.persist(line,0);
                }
                if (sd.messageSent.get(this.getId())) {
                    socOut.println(sd.messagesToSend.get(0).getValue() + " : " + sd.messagesToSend.get(0).getKey());
                    sd.messageSent.put(this.getId(), false);
                    sd.counterRead--;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

}
