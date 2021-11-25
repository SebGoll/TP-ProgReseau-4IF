/**
 * ClientThread
 *
 * @author Louis Hasenfratz,Sebastien Goll
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

/**
 * ClientThread is the thread that communicate with the user on the server-side
 */
public class ClientThread extends Thread {

    public boolean messageSent;
    public SharedData sd;
    public String name;
    public Long chatId;

    private final CharSequence TABLE_FLIP_SEQUENCE = "\\tableFlip";
    private final CharSequence TABLE_FLIP = "(╯°□°）╯︵ ┻━┻ ";
    private final String CHANGE_COMMAND = "\\changeto";
    private final String INFO_COMMAND = "\\infos";
    private final String HELP_COMMAND = "\\help";

    private final Socket CLIENT_SOCKET;


    /**
     * Constructor
     * @param s the remote socket used by the user
     * @param data the data of the chat
     */
    ClientThread(Socket s, SharedData data) {
        this.CLIENT_SOCKET = s;
        this.sd = data;

        this.name = null;

        this.chatId = null;

    }

    /**
     * Tell the system that a message from the user arrives and display message to the user
     **/
    public void run() {
        try {
            BufferedReader socIn = new BufferedReader(
                    new InputStreamReader(CLIENT_SOCKET.getInputStream()));
            PrintStream socOut = new PrintStream(CLIENT_SOCKET.getOutputStream());
            // user's login
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

            socOut.println("Bienvenue " + name + " vous pouvez maintenant chatter avec vos amis !\n\\help pour voir les commandes disponibles\n");
            Persistence.logAndLoad(name, socOut, chatId);


            while (true) {
                //reception of a message
                if (socIn.ready()) {
                    String line = socIn.readLine();
                    if (line.startsWith(CHANGE_COMMAND)) {
                        String[] chars = line.split(" ");
                        sd.groupDataTable.get(chatId).groupThreadList.remove(this);
                        try {
                            Long newChatId = Long.parseLong(chars[1]);
                            assert (sd.groupDataTable.containsKey(newChatId));
                            this.chatId = newChatId;

                            sd.groupDataTable.get(chatId).groupThreadList.add(this);
                            sd.groupDataTable.get(chatId).messageSent.put(this.getId(), false);
                            socOut.println("Vous etes maintenant sur la conversation " + chatId);
                        } catch (Exception e) {
                            socOut.println("Mauvais id de conversation");
                            sd.groupDataTable.get(chatId).groupThreadList.add(this);
                            continue;
                        }

                    } else if (line.equals(INFO_COMMAND)) {
                        socOut.println("Conversation n°" + chatId);
                        socOut.println("Utilisateurs actuellements connectés :");
                        for (ClientThread clientThread : sd.groupDataTable.get(chatId).groupThreadList) {
                            if (clientThread.equals(this)) {
                                socOut.println(Persistence.ANSI_GREEN + clientThread.name + Persistence.ANSI_RESET);
                            } else {
                                socOut.println(clientThread.name);
                            }
                        }
                    } else if (line.equals(HELP_COMMAND)) {
                        socOut.println(INFO_COMMAND + " : Donne les informations sur la conversation actuelle\n" +
                                CHANGE_COMMAND + " <idConv> : Change la conversation vers la conversation indiquée");

                    } else {
                        line = line.replace(TABLE_FLIP_SEQUENCE, TABLE_FLIP);
                        sd.groupDataTable.get(chatId).messagesToSend.add(new AbstractMap.SimpleEntry<>(line, name));
                        messageSent = true;
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM|HH:mm");
                        LocalDateTime now = LocalDateTime.now();
                        Persistence.persist(dtf.format(now), line, this.name, chatId);
                    }

                }
                //a message need to be send
                if (sd.groupDataTable.get(chatId).messageSent.get(this.getId())) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM|HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    AbstractMap.SimpleEntry<String, String> message = sd.groupDataTable.get(chatId).messagesToSend.get(0);
                    if (message.getKey().contains("@" + this.name)) {
                        String newMessage = Persistence.ANSI_MENTIONS + message.getKey() + Persistence.ANSI_RESET;
                        message = new AbstractMap.SimpleEntry<>(newMessage, message.getValue());
                    }
                    socOut.println(Persistence.ANSI_DATE + "[" +
                            dtf.format(now) + "] " +
                            Persistence.ANSI_BOLD +
                            message.getValue() +
                            " : " + Persistence.ANSI_RESET +
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
