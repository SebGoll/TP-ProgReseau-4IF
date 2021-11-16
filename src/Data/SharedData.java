package Data;

import stream.ClientThread;

import java.net.Socket;
import java.net.SocketAddress;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SharedData {

    public List<ClientThread> threadList;
    public HashMap<SocketAddress, Long> threadTable;
    public List<AbstractMap.SimpleEntry<String, String>> messagesToSend;
    public Integer counterRead;
    public HashMap<Long, Boolean> messageSent;

    public SharedData(){
        threadList = new LinkedList<>();
        threadTable = new HashMap<>();
        messagesToSend = new LinkedList<>();
        counterRead=0;
        messageSent=new HashMap<>();
    }


}
