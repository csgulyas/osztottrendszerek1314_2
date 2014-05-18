package bead.dht.model;

import bead.dht.common.Constants;
import java.util.*;

public class DHT {
    Map<Integer, DHTItem> items;
    List<Integer> sortedKeys;
    
    public DHT(int size) {
        items = new HashMap<>(size);
    }
    
    public void add(int listId, int port) {
        DHTItem item = new DHTItem(port);
        items.put(item.getId(), item);
    }
    
    public void calculateRanges() {
        this.sortedKeys = new ArrayList(this.items.keySet());
        Collections.sort(this.sortedKeys);
        
        for (Map.Entry<Integer, DHTItem> entry : this.items.entrySet()) {
            DHTItem item = entry.getValue();
            DHTItem prevItem = getPrevious(entry.getKey());
            
            item.setLower(prevItem.getId() + 1);
        }
    }
    
    public Map<Integer, DHTItem> getItems() {
        return this.items;
    }
    
    public DHTItem getByListId(int listId) {
        return this.items.get(listId);
    }
    
    public DHTItem getByPort(int port) {
        for (Map.Entry<Integer, DHTItem> entry : this.items.entrySet()) {
            DHTItem item = entry.getValue();
            if (item.getPort() == port) {
                return item;
            }
        }
        
        return null;
    }
    
    public DHTItem getById(int id) {
        for (Map.Entry<Integer, DHTItem> entry : this.items.entrySet()) {
            DHTItem item = entry.getValue();
            if (item.getId()== id) {
                return item;
            }
        }
        
        return null;
    }
    
    public DHTItem getPrevious(int listId) {
        Integer keyIdx = this.sortedKeys.indexOf(listId);
        int prevIdx = keyIdx - 1 < 0 ? this.items.size() - 1 : keyIdx - 1;
        return this.items.get(this.sortedKeys.get(prevIdx));
    }
    
    public NodeInfo getPortForFingerId(int id) {
        int keysSize = this.sortedKeys.size();
        if (keysSize == 1) {
            int currentId = this.sortedKeys.get(0);
            return new NodeInfo(currentId, this.items.get(currentId).getPort(), this.items.get(currentId).getLower(), this.items.get(currentId).getUpper());
        }
        
        for (int i = 0; i < keysSize; i++) {
            int currentId = this.sortedKeys.get(i);
            int nextId = i + 1 >= keysSize ? this.sortedKeys.get(0) : this.sortedKeys.get(i + 1);
            
            if (currentId == id) {
                    return new NodeInfo(currentId, this.items.get(currentId).getPort(), this.items.get(currentId).getLower(), this.items.get(currentId).getUpper());
                }
            else if (currentId < nextId) {
                if (currentId < id && id < nextId) {
                    return new NodeInfo(nextId, this.items.get(nextId).getPort(), this.items.get(nextId).getLower(), this.items.get(nextId).getUpper());
                }
            }
            else if (currentId > nextId) { // körbeért a node körön
                int lower = this.items.get(nextId).getLower();
                if ((lower <= id && id < Constants.Limit) || (0 <= id && id < nextId)) {
                    return new NodeInfo(nextId, this.items.get(nextId).getPort(), lower, this.items.get(nextId).getUpper());
                }
            }
        }
        
        return null;
    }
}
