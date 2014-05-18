package bead.dht.common;

import java.util.Random;

public class RandomGenerator {
    static Random r;
    static {
        r = new Random(1000);
    }
    
    public static Random getGenerator(){
        return r;
    }
}
