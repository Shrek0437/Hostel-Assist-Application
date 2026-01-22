package com.hostelassist.resources;

public class PeerInfo {

    private String peerId;
    private String host;
    private int port;

    public PeerInfo(String peerId, String host, int port) {
        this.peerId = peerId;
        this.host = host;
        this.port = port;
    }

    public String getPeerId() {
        return peerId;
    }

//    public String getHost() {
//        return host;
//    }
//
    public int getPort() {
        return port;
    }
}
