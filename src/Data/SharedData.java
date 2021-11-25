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
    public HashMap<Long, GroupData> groupDataTable;

    public SharedData() {
        threadList = new LinkedList<>();
        threadTable = new HashMap<>();
        groupDataTable = new HashMap<>();
    }


}
