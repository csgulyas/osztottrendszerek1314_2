package bead.dht;

import bead.dht.model.NodeInfo;
import java.io.Serializable;
import java.util.*;

public class FingerTable implements Serializable {
    NodeInfo me;
    Map<Integer, Integer> items;
    
    public FingerTable(int nodeId, int nodePort) {
        this.me = new NodeInfo(nodeId, nodePort);
        this.items = new HashMap<>(16);
    }
    
    public void addItem(int id, int port) {
        items.put(id, port);
    }
    
    public NodeInfo getSelf() {
        return this.me;
    }
    
    public Map<Integer, Integer> getTable() {
        return this.items;
    }
    
    public NodeInfo getClosest(int hash) {       
        List<Integer> keys = new ArrayList(this.items.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            int currentId = keys.get(i);
            int nextId = i + 1 >= keys.size() ? keys.get(0) : keys.get(i + 1);

            if ((currentId <= hash && hash < nextId) || i + 1 == keys.size()) {
                return new NodeInfo(currentId, this.items.get(currentId));
            }
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        TreeMap<Integer, Integer> tm = new TreeMap<>(this.items);
        StringBuilder sb = new StringBuilder();
        sb.append("Finger table for client id: ").append(this.me.getId()).append(", port: ").append(this.me.getPort());
        for (Map.Entry<Integer, Integer> entry : tm.entrySet()) {
            sb.append("\n");
            sb.append("id: ").append(entry.getKey()).append("\tport: ").append(entry.getValue());
        }
        
        
        return sb.toString();
    }
}
