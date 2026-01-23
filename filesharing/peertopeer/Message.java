package filesharing.peertopeer;

import java.io.Serializable;
import java.util.List;


public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        PEER_ANNOUNCEMENT,
        PEER_LIST_REQUEST,
        PEER_LIST_RESPONSE,
        FILE_LIST_REQUEST,
        FILE_LIST_RESPONSE,
        FILE_DOWNLOAD_REQUEST,
        FILE_DATA,
        HEARTBEAT,
        PEER_LEAVING
    }
    
    private MessageType type;
    private String senderName;
    private String senderIP;
    private int senderPort;
    private List<FileMetadata> fileList;
    private List<PeerInfo> peerList;
    private String requestedFileName;
    private byte[] fileData;
    
    public Message(MessageType type) {
        this.type = type;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getSenderIP() {
        return senderIP;
    }
    
    public void setSenderIP(String senderIP) {
        this.senderIP = senderIP;
    }
    
    public int getSenderPort() {
        return senderPort;
    }
    
    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }
    
    public List<FileMetadata> getFileList() {
        return fileList;
    }
    
    public void setFileList(List<FileMetadata> fileList) {
        this.fileList = fileList;
    }
    
    public List<PeerInfo> getPeerList() {
        return peerList;
    }
    
    public void setPeerList(List<PeerInfo> peerList) {
        this.peerList = peerList;
    }
    
    public String getRequestedFileName() {
        return requestedFileName;
    }
    
    public void setRequestedFileName(String requestedFileName) {
        this.requestedFileName = requestedFileName;
    }
    
    public byte[] getFileData() {
        return fileData;
    }
    
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}