/**
 *Persistence
 * @author Louis Hasenfratz,Sebastien Goll
 */

package stream;

import java.io.*;
import java.util.Scanner;

/**
 * Persist and load the conversations
 */
public class Persistence {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_DATE = "\u001B[35m";

    /**
     * load a conversation when a user log in
     * @param name the name of the user
     * @param out where the conversation must be displayed, here the printstreams of the remote sockets
     * @param idConv the id of the conversation to load
     */
    public static void logAndLoad(String name, PrintStream out, long idConv) {
        try {
            File persistenceFile = new File("PersistenceData/PersistenceData_" + idConv);
            if (persistenceFile.createNewFile()) {
                System.out.println("New persistence file created for group chat " + idConv + ".");
            } else {
                System.out.println("Persistence file for group chat " + idConv + " was located.");
            }
        } catch (IOException e) {
            System.out.println("An error as occurred when opening/creating the file.");
            e.printStackTrace();
        }
        try {
            Scanner reader = new Scanner(new File("PersistenceData/PersistenceData_" + idConv));
            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                String[] data = line.split(" ", 3);
                if (data[1].equals(name)) {
                    out.println(ANSI_GREEN + data[0] + " " + ANSI_BOLD + data[1] + " " + data[2] + ANSI_RESET);
                } else {
                    out.println(ANSI_DATE + data[0] + " " + ANSI_BOLD + data[1] + " " + ANSI_RESET + data[2]);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error as occurred while reading the file.");
            e.printStackTrace();
        }

    }

    /**
     * Persiste a message into the conversation file
     * @param Date The date when the message was sent
     * @param message the content of the message
     * @param name the name of the user that sended the message
     * @param idConv the id of the conversation where the message was sent
     */
    public static void persist(String Date, String message, String name, long idConv) {
        try {
            FileWriter writerToPersistenceFile = new FileWriter("PersistenceData/PersistenceData_" + idConv, true);
            writerToPersistenceFile.write("[" + Date + "] " + name + " : " + message);
            writerToPersistenceFile.write('\n');
            writerToPersistenceFile.close();
            System.out.println("Message successfully persisted.");
        } catch (IOException e) {
            System.out.println("An error as occurred when persisting the message.");
            e.printStackTrace();
        }

    }
}
