package filesharing.peertopeer;

import java.io.Serializable;

public class FileMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String fileName;
    private long fileSize;
    private String fileType;
    private String owner;
    private String ownerIP;
    private int ownerPort;
    
    public FileMetadata(String fileName, long fileSize, String fileType, String owner, String ownerIP, int ownerPort) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.owner = owner;
        this.ownerIP = ownerIP;
        this.ownerPort = ownerPort;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public String getOwnerIP() {
        return ownerIP;
    }
    
    public int getOwnerPort() {
        return ownerPort;
    }
    
    public String getFileSizeFormatted() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.2f KB", fileSize / 1024.0);
        return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
    }
    
    @Override
    public String toString() {
        return fileName + " (" + getFileSizeFormatted() + ") - " + owner;
    }
}