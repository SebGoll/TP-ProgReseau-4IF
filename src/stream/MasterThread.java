package stream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MasterThread extends Thread {
    public List<ClientThread> threadList;
    public HashMap<SocketAddress, Long> threadTable;

    public MasterThread() {
        threadList = new LinkedList<>();
        threadTable = new HashMap<>();
    }

    public void run() {
        while(true){

            for(ClientThread clientThread:threadList){
                if(clientThread.messageSent){
                    clientThread.messageSent=false;
                    for(ClientThread destination:threadList){
                        System.out.println("Alllo from"+clientThread.getId());
                        destination.sendMessage("Tmer");
                    }

                }
            }
        }
        try {
            BufferedReader socIn = null;
            socIn = new BufferedReader(
                    new InputStreamReader(new PrintStream()));
            PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            while (true) {
                String line = socIn.readLine();
                messageSent = true;
                socOut.println(line);
                System.out.println("MessageState: "+messageSent);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }


    }

}
