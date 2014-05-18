
package bead.dht;

import bead.dht.model.DHTItem;
import bead.dht.model.DHT;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class DHTMain {
    final static int Port = 65432;

    public static void main(String[] args) throws Exception {
        
        int clientsNo = Integer.parseInt(args[0]);
        
        DHT table = discoverClients(clientsNo);
        notifyClients(table);
        
        /*table.calculateRanges();
        DHTToFingerTable dft = new DHTToFingerTable(table);
        List<FingerTable> fts = new ArrayList<>(clientsNo);
        for (Map.Entry<Integer, DHTItem> entry : table.getItems().entrySet()) {
            fts.add(dft.getFingerTableFor(entry.getKey()));
        }*/
        
        Map<Integer, DHTItem> sortedDht = DHTHelper.sortDht(table.getItems());
        for (Map.Entry<Integer, DHTItem> entry : sortedDht.entrySet()) {
            DHTItem item = entry.getValue();
            System.out.println(item);
        }
        
        //FingerTable ft3 = fts.get(3);
        //System.out.println(ft3);
    }
    
    private static DHT discoverClients(int clientNo) throws Exception {
        int connected = 0;
        DHT table = new DHT(clientNo);
        ServerSocket ss = new ServerSocket(Port);
        
        System.out.println("Server started");
        while(connected != clientNo) {    
            System.out.println("Waiting for " + (clientNo - connected) + " more clients");
            Socket s = ss.accept();
            Scanner sc = new Scanner(s.getInputStream());
            table.add(connected, sc.nextInt());
            ++connected;
            
            sc.close();
            s.close();
        }
        ss.close();
        System.out.println("Server stopped");
        
        return table;
    }
    
    private static void notifyClients(DHT table) throws Exception {
        final String gep = "localhost";
        
        table.calculateRanges();
        DHTToFingerTable dft = new DHTToFingerTable(table);
        //List<FingerTable> fts = new ArrayList<>(clientsNo);
        for (Map.Entry<Integer, DHTItem> entry : table.getItems().entrySet()) {
            FingerTable ft = dft.getFingerTableFor(entry.getKey());
            //fts.add(ft);
            
            int port = ft.getSelf().getPort();
            Socket s  = new Socket(gep, port);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(ft);
            
            oos.close();
            s.close();
        }
    }
}
