package Data;

import stream.ClientThread;

import java.net.SocketAddress;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GroupData {

    public List<AbstractMap.SimpleEntry<String, String>> messagesToSend;
    public Integer counterRead;
    public HashMap<Long, Boolean> messageSent;
    public List<ClientThread> groupThreadList;

    public GroupData(){
        messagesToSend = new LinkedList<>();
        counterRead=0;
        messageSent=new HashMap<>();
        groupThreadList = new LinkedList<>();

    }
}
