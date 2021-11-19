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

    public String name;

    public Long chatId;



    ClientThread(Socket s, SharedData data) {
        this.clientSocket = s;
        this.sd = data;

        this.name = null;

        this.chatId=null;

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
            socOut.println("Renseignez votre id de conv");
            chatId = Long.parseLong(socIn.readLine());
            sd.groupDataTable.get(chatId).groupThreadList.add(this);
            sd.groupDataTable.get(chatId).messageSent.put(this.getId(),false);
            name = socIn.readLine();
            socOut.println("Bienvenue "+ name +" vous pouvez maintenant chatter avec vos amis !\n");
            Persistence.logAndLoad(name,socOut,0);



            while (true) {
                if (socIn.ready()) {
                    String line = socIn.readLine();
                    sd.groupDataTable.get(chatId).messagesToSend.add(new AbstractMap.SimpleEntry<>(line, name));
                    messageSent = true;
                    Persistence.persist(line,this.name,0);

                }
                if (sd.groupDataTable.get(chatId).messageSent.get(this.getId())) {
                    socOut.println(sd.groupDataTable.get(chatId).messagesToSend.get(0).getValue() + " : " + sd.groupDataTable.get(chatId).messagesToSend.get(0).getKey());
                    sd.groupDataTable.get(chatId).messageSent.put(this.getId(), false);
                    sd.groupDataTable.get(chatId).counterRead--;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in ClientThread:" + e);
            e.printStackTrace();
        }
    }

}
