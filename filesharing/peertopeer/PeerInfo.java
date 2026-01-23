package filesharing.peertopeer;

import java.io.Serializable;

public class PeerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String ipAddress;
    private int port;
    private long lastSeen;
    
    public PeerInfo(String name, String ipAddress, int port) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.lastSeen = System.currentTimeMillis();
    }
    
    public String getName() {
        return name;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public int getPort() {
        return port;
    }
    
    public long getLastSeen() {
        return lastSeen;
    }
    
    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }
    
    public String getKey() {
        return ipAddress + ":" + port;
    }
    
    @Override
    public String toString() {
        return name + " (" + ipAddress + ":" + port + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PeerInfo peerInfo = (PeerInfo) obj;
        return getKey().equals(peerInfo.getKey());
    }
    
    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}