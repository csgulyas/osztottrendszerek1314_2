package bead.dht.model;

import bead.dht.common.Constants;
import java.io.Serializable;

public class NodeInfo implements Serializable {
    int port;
    int id;
    int lower;
    int upper;
    
    public NodeInfo(int id, int port) {
        this.id = this.upper = id;
        this.port = port;
    }
    
    public NodeInfo(int id, int port, int lower, int upper) {
        this.id = id;
        this.port = port;
        this.lower = lower;
        this.upper = upper;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public int getLower() {
        return this.lower;
    }
    
    public int getUpper() {
        return this.upper;
    }
    
    public void setLower(int lower) {
        this.lower = lower;
    }
    
    public boolean isResponsibleFor(int hash) {
        if (this.lower < this.upper) {
            return this.lower <= hash && hash <= this.upper;
        }
        
        return (this.lower <= hash && hash < Constants.Limit) || (0 <= hash && hash <= this.upper);
    }
}
