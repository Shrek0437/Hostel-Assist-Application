package com.hostelassist.resources;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PeerRegistryService {

    private final List<PeerInfo> peers = new ArrayList<>();

    public synchronized void registerPeer(PeerInfo peerInfo) {
        // Avoid duplicates
        boolean exists = peers.stream()
                .anyMatch(p ->
                        p.getPeerId().equals(peerInfo.getPeerId()) &&
                                p.getPort() == peerInfo.getPort()
                );

        if (!exists) {
            peers.add(peerInfo);
        }
    }

    public List<PeerInfo> getAllPeers() {
        return peers;
    }
}