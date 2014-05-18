package bead.dht.model;

import bead.dht.common.Common;
import java.util.ArrayList;
import java.util.List;

public class FilePayload {
    int hash;
    String fileName;
    List<String> contentLines;
    
    public FilePayload(String fileName) {
        this.fileName = fileName;
        this.hash = Common.getFileHash(fileName);
        this.contentLines = new ArrayList<>();
    }
    
    public void addLine(String line) {
        this.contentLines.add(line);
    }
    
    public int getHash() {
        return this.hash;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public List<String> getContent() {
        return this.contentLines;
    }
}
