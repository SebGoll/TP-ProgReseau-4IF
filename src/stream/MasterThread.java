/**
 *MasterThread
 * @author Louis Hasenfratz,Sebastien Goll
 */
package stream;

import Data.SharedData;

/**
 * Rule over the client threads, detect when a message is received and tells the threads to display it to the users
 */
public class MasterThread extends Thread {
    public SharedData sd;

    /**
     * Constructor
     * @param data the data of the chat
     */
    public MasterThread(SharedData data) {
        this.sd = data;
    }

    /**
     * detect when a message is received and tells the threads to display it to the users
     */
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (ClientThread clientThread : sd.threadList) {
                Long chatId= clientThread.chatId;
                if(chatId==null)continue;
                if (clientThread.messageSent) {
                    clientThread.messageSent = false;
                    sd.groupDataTable.get(chatId).counterRead = sd.groupDataTable.get(chatId).groupThreadList.size()-1;
                    for (ClientThread destination : sd.groupDataTable.get(chatId).groupThreadList) {
                        if(destination.getId()==clientThread.getId())continue;
                        sd.groupDataTable.get(chatId).messageSent.put(destination.getId(),true);
                    }
                    System.out.println("Message from " + clientThread.getId());

                }
            }
            //remove a message when seen by everyone in the conversation
            for(Long chatId : sd.groupDataTable.keySet()){
                if (sd.groupDataTable.get(chatId).messagesToSend.size() > 0 && sd.groupDataTable.get(chatId).counterRead == 0) {
                    sd.groupDataTable.get(chatId).messagesToSend.remove(0);

                }
            }
        }
    }
}
