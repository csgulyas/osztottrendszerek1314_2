package bead.dht;

import bead.dht.model.NodeInfo;
import bead.dht.model.DHTItem;
import bead.dht.model.DHT;
import bead.dht.common.Constants;

public class DHTToFingerTable {
    DHT dht;
    
    public DHTToFingerTable(DHT dht) {
        this.dht = dht;
    }
    
    public FingerTable getFingerTableFor(int id) {
        DHTItem dhtItem = dht.getById(id);
        FingerTable ft = new FingerTable(id, dhtItem.getPort());
        ft.me.setLower(dhtItem.getLower());
        for (int i = 1; i <= 16; i++) {
            int ftId = (id + (int)Math.pow(2, i-1)) % Constants.Limit;
            NodeInfo node = dht.getPortForFingerId(ftId);
            int ftPort = node == null ? -1 : node.getPort();
            ft.addItem(ftId, ftPort);
        }
        
        return ft;
    }
}
