package bead.dht.model;

import bead.dht.common.RandomGenerator;
import bead.dht.common.Constants;

public class DHTItem implements Comparable<DHTItem> {
    int port;
    int id;
    int lower;
    int upper;
    
    public DHTItem(int port) {
        this.port = port;
        this.id = this.upper = RandomGenerator.getGenerator().nextInt(65535 + 1) % Constants.Limit;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getLower() {
        return this.lower;
    }
    
    public int getUpper() {
        return this.upper;
    }
    
    public void setLower(int lower) {
        this.lower = lower % Constants.Limit;
    }
    
    @Override
    public String toString() {
        return "Client port: " + this.port + ", id: " + this.id + ", lower: " + this.lower + ", upper: " + this.upper;
    }

    @Override
    public int compareTo(DHTItem t) {
        int otherId = t.getId();
        
        if (otherId == this.id) return 0;
        if (otherId < this.id) return 1;
        
        return -1;
    }
}
