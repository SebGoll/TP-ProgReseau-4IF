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

public class ClientThread
        extends Thread {

    private Socket clientSocket;
    public boolean messageSent;
    public SharedData sd;

    private final CharSequence TABLE_FLIP_SEQUENCE = "\\tableFlip";
    private final CharSequence TABLE_FLIP = "(╯°□°）╯︵ ┻━┻ ";



//    private final String ;

    public String name;

    public Long chatId;


    ClientThread(Socket s, SharedData data) {
        this.clientSocket = s;
        this.sd = data;

        this.name = null;

        this.chatId = null;

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


            while (true) {
                if (socIn.ready()) {
                    String line = socIn.readLine();
                    line = line.replace(TABLE_FLIP_SEQUENCE, TABLE_FLIP);

                    sd.groupDataTable.get(chatId).messagesToSend.add(new AbstractMap.SimpleEntry<>(line, name));
                    messageSent = true;
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM|HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    Persistence.persist(dtf.format(now), line, this.name, chatId);
                }
                if (sd.groupDataTable.get(chatId).messageSent.get(this.getId())) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM|HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    AbstractMap.SimpleEntry<String,String> message = sd.groupDataTable.get(chatId).messagesToSend.get(0);
                    if(message.getKey().contains("@"+this.name)){
                        String newMessage = Persistence.ANSI_MENTIONS+message.getKey()+Persistence.ANSI_RESET;
                        message = new AbstractMap.SimpleEntry<>(newMessage,message.getValue());
                    }
                    socOut.println(Persistence.ANSI_DATE + "[" +
                            dtf.format(now) + "] " +
                            Persistence.ANSI_BOLD +
                            message.getValue() +
                            " : " + Persistence.ANSI_RESET+
                            message.getKey());
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
