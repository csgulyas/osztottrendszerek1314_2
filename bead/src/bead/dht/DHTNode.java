package bead.dht;

import bead.dht.model.FilePayload;
import bead.dht.common.Constants;
import bead.dht.common.Common;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class DHTNode {
    static int myPort;
    static int myId;
    static int myLower;
    static int myUpper;
    
    static Map<Integer, FilePayload> files;
    
    static {
        files = new HashMap<>();
    }
    
    public static void main(String[] args) throws Exception {
        myPort = Integer.parseInt(args[0]);
        sendPort(myPort);
        FingerTable ft = getFingerTable();
        
        System.out.println(ft);
        System.out.println("My data, lower: " + myLower + ", upper: " + myUpper);
        handleConnections(ft);
    }
    
    private static void sendPort(int myport) throws Exception {
        System.out.println("Sending my port to master server");
        Socket s   = new Socket(Constants.ServerName, Constants.MasterPort);

        PrintWriter pw = new PrintWriter(s.getOutputStream());

        pw.println(myport);
        pw.flush();

        pw.close();
        s.close();
    }
    
    private static FingerTable getFingerTable() throws Exception {
        System.out.println("Waiting for Finger Table from master server");
        ServerSocket ss = new ServerSocket(myPort);
        Socket s = ss.accept();
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        
        FingerTable ft = (FingerTable)ois.readObject();
        if (ft == null) {
            throw new Exception("Could not get Finger Table from master server");
        }
        
        ois.close();
        s.close();
        ss.close();
        
        myId = ft.me.getId();
        myLower = ft.me.getLower();
        myUpper = ft.me.getUpper();
        
        return ft;
    }
    
    private static void handleConnections(FingerTable ft) throws Exception {
        ServerSocket ss = new ServerSocket(myPort);
        Socket s;
        String cmd;
        String fn;
        NodeRequestHandler rh = new NodeRequestHandler(ft);
        
        Scanner sc;
        PrintWriter pw;
        while(true) {
            s = ss.accept();
            sc = new Scanner(s.getInputStream());
            pw = new PrintWriter(s.getOutputStream());
            
            Common.print("Client connected");
            cmd = sc.next();
            fn = sc.next();
            if (sc.hasNextLine()) {sc.nextLine();}
            
            if (Constants.CmdUpload.equals(cmd)) {
                Common.print("Received file upload command, file name: '" + fn+ "'");
                FilePayload fp = rh.uploadFileHandler(fn, sc);
                if (fp != null) {
                    Common.print("Saved file '" + fn + "', hash: " + fp.getHash());
                    files.put(fp.getHash(), fp);
                }
            }
            else if (Constants.CmdLookup.equals(cmd)) {
                Common.print("Received file lookup command, file name: '" + fn+"'");
                FilePayload fp = rh.lookupFileHandler(fn);
                
                if (fp != null)
                {
                    Common.print("Found the file, sending it to client");
                    pw.println(Constants.StatusFound);
                    pw.flush();
                    for (String line : fp.getContent()) {
                        pw.println(line);
                        pw.flush();
                    }
                }
                else {
                    Common.print("File not found");
                    pw.println(Constants.StatusNotFound);
                    pw.flush();
                }
                
            }
            else {
                Common.print("Unknown command received");
            }
            
            pw.close();
            sc.close();
            s.close();
            Common.print("Closing current connection\n==========================\n\n");
        }
    }
}
