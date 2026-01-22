package com.hostelassist.resources;

import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

@Service
public class FileTransferService {

    public void receiveFile(PeerNode sourceNode, String fileName) {
        try (
                Socket socket = new Socket(sourceNode.getHost(), sourceNode.getPort());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                InputStream is = socket.getInputStream();
                FileOutputStream fos = new FileOutputStream("downloads/" + fileName)
        ) {
            dos.writeUTF(fileName);
            dos.flush();

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            fos.flush();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void connectToPeer(PeerNode peerNode) {
//        // Not required for basic TCP implementation
//    }
//
//    public void disconnectFromPeer(PeerNode peerNode) {
//        // Not required for basic TCP implementation
//    }
//
//    public void sendFile(PeerNode targetNode, String filePath) {
//        // Sending is handled by PeerNode when requests arrive
//    }
}
