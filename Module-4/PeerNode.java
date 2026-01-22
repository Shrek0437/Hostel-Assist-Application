package com.hostelassist.resources;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerNode {

    private String nodeId;
    private String host;
    private int port;
    private boolean running = false;

    public PeerNode(String nodeId, String host, int port) {
        this.nodeId = nodeId;
        this.host = host;
        this.port = port;
    }

    public void start() {
        running = true;

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("PeerNode started on port " + port);
                while (running) {
                    Socket socket = serverSocket.accept();
                    handleIncomingRequest(socket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        running = false;
    }

    private void handleIncomingRequest(Socket socket) {
        try (
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                OutputStream os = socket.getOutputStream()
        ) {
            String fileName = dis.readUTF();
            File file = new File("shared/" + fileName);

            if (!file.exists()) {
                os.write("FILE_NOT_FOUND".getBytes());
                return;
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            os.flush();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public String getNodeId() {
//        return nodeId;
//    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
