/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import Data.GroupData;
import Data.SharedData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;

public class ClientThread extends Thread {

    public boolean messageSent;
    public SharedData sd;
    public String name;
    public Long chatId;

    private final Socket CLIENTSOCKET;



    ClientThread(Socket s, SharedData data) {
        this.CLIENTSOCKET = s;
        this.sd = data;

        this.name = null;

        this.chatId = null;

    }

    /**
     * receives a request from client then sends an echo to the client
     **/
    public void run() {
        try {
            BufferedReader socIn = new BufferedReader(
                    new InputStreamReader(CLIENTSOCKET.getInputStream()));
            PrintStream socOut = new PrintStream(CLIENTSOCKET.getOutputStream());
            //connexion d'un utilisateur
            socOut.println("Renseignez votre pseudonyme");
            name = socIn.readLine();
            socOut.println("Renseignez votre id de conv");
            try {
                chatId = Long.parseLong(socIn.readLine());

            } catch (Exception e) {
                socOut.println("Renseignez votre id de conv");
                chatId = Long.parseLong(socIn.readLine());
            }
            if (!sd.groupDataTable.containsKey(chatId)) {
                sd.groupDataTable.put(chatId, new GroupData());
            }
            sd.groupDataTable.get(chatId).groupThreadList.add(this);
            sd.groupDataTable.get(chatId).messageSent.put(this.getId(), false);

            socOut.println("Bienvenue " + name + " vous pouvez maintenant chatter avec vos amis !\n");
            Persistence.logAndLoad(name, socOut, chatId);

            //reception des messages et de l'utilisateur du thread et envoie des autres messages
            while (true) {
                if (socIn.ready()) {
                    String line = socIn.readLine();
                    sd.groupDataTable.get(chatId).messagesToSend.add(new AbstractMap.SimpleEntry<>(line, name));
                    messageSent = true;
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM|HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    Persistence.persist(dtf.format(now), line, this.name, chatId);
                }
                if (sd.groupDataTable.get(chatId).messageSent.get(this.getId())) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM|HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    socOut.println(Persistence.ANSI_DATE + "[" +
                            dtf.format(now) + "] " +
                            Persistence.ANSI_BOLD +
                            sd.groupDataTable.get(chatId).messagesToSend.get(0).getValue() +
                            Persistence.ANSI_RESET + " : " +
                            sd.groupDataTable.get(chatId).messagesToSend.get(0).getKey());
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
