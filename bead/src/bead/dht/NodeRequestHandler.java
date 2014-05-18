package bead.dht;

import bead.dht.model.NodeInfo;
import bead.dht.model.FilePayload;
import bead.dht.common.Constants;
import bead.dht.common.Common;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class NodeRequestHandler {
    NodeInfo self;
    FingerTable ft;
    
    public NodeRequestHandler(FingerTable ft) {
        this.self = ft.getSelf();
        this.ft = ft;
    }
    
    private boolean amIResponsible(String fn) {
        int hash = Common.getFileHash(fn);
        return amIResponsible(hash);
    }
    
    private boolean amIResponsible(int hash) {
        return this.self.isResponsibleFor(hash);
    }
    
    public FilePayload uploadFileHandler(String fn, Scanner sc) throws Exception {
        return uploadFileHandler(fn, sc, false);
    }
    
    private FilePayload uploadFileHandler(String fn, Scanner sc, boolean tempStore) throws Exception {
        FilePayload p = new FilePayload(fn);
        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            p.addLine(line);
            Common.print("Added line: " + line);
        }
        
        if (tempStore) {
            return p;
        }
        
        boolean responsible = amIResponsible(fn);
        if (responsible) {
            Common.print("I'm responsible for this file");
            return p;
        }
        else {
            Common.print("I'm not responsible for this file");
            fileTransferHandler(p);
            return null;
        }
    }
    
    public FilePayload lookupFileHandler(String fn) throws Exception {
        boolean responsible = amIResponsible(fn);
        FilePayload fp;
        if (responsible) {
            Common.print("I'm responsible for this file");
            int hash = Common.getFileHash(fn);
            fp = DHTNode.files.get(hash);
        }
        else {
            Common.print("I'm not responsible for this file");
            fp = lookupTransferHandler(fn);
        }
        
        return fp;
    }
    
    public void fileTransferHandler(FilePayload p) throws Exception {
        NodeInfo closest = this.ft.getClosest(p.getHash());
        
        Common.print("Transfering data to closest node: " + closest.getPort() + ", hash: " + p.getHash());
        Socket s   = new Socket(Constants.ServerName, closest.getPort());

        PrintWriter pw = new PrintWriter(s.getOutputStream());

        pw.println(Constants.CmdUpload + " " + p.getFileName());
        pw.flush();
        for (String line : p.getContent()) {
            pw.println(line);
            pw.flush();
        }

        pw.close();
        s.close();
    }
    
    public FilePayload lookupTransferHandler(String fn) throws Exception {
        int hash = Common.getFileHash(fn);
        NodeInfo closest = this.ft.getClosest(hash);
        if (closest == null) {
            Common.print("Could not find closest node for hash: " + hash);
            return null;
        }
        
        Common.print("Looking up data at closest node, port: " + closest.getPort() + ", hash: " + hash);
        
        Socket s   = new Socket(Constants.ServerName, closest.getPort());
        PrintWriter pw = new PrintWriter(s.getOutputStream());
        Scanner sc = new Scanner(s.getInputStream());
        
        String response;
        
        pw.println(Constants.CmdLookup + " " + fn);
        pw.flush();
        response = sc.next();
        if (sc.hasNextLine()) { sc.nextLine();}
        if (Constants.StatusNotFound.equals(response)) {
            return null;
        }
        else if (Constants.StatusFound.equals(response)) {
            return uploadFileHandler(fn, sc, true);
        }
        else {
            throw new Exception("Unknown response to lookup request");
        }
    }
}
