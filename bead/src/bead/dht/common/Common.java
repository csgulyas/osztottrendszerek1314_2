package bead.dht.common;

import bead.dht.Crc16;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Common {
    public static void print(String msg) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss").format(Calendar.getInstance().getTime());
        System.out.println("["+timeStamp+"] "+msg);
    }
    
    public static int getFileHash(String fileName) {
        return Crc16.crc(fileName) % Constants.Limit;
    }
}
