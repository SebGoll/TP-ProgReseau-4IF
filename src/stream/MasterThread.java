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
                if (clientThread.messageSent) {
                    clientThread.messageSent = false;
                    sd.counterRead = sd.threadList.size()-1;
                    for (ClientThread destination : sd.threadList) {
                        if(destination.getId()==clientThread.getId())continue;
                        sd.messageSent.put(destination.getId(),true);
                    }
                    System.out.println("Message from " + clientThread.getId());


                }
            }
            if (sd.messagesToSend.size() > 0 && sd.counterRead == 0) {
                sd.messagesToSend.remove(0);

            }
        }


    }

}
