package stream;

import Data.SharedData;

public class MasterThread extends Thread {
    public SharedData sd;

    public MasterThread(SharedData data) {
        this.sd = data;
    }

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
            for(Long chatId : sd.groupDataTable.keySet()){
                if (sd.groupDataTable.get(chatId).messagesToSend.size() > 0 && sd.groupDataTable.get(chatId).counterRead == 0) {
                    sd.groupDataTable.get(chatId).messagesToSend.remove(0);

                }
            }

        }


    }

}
