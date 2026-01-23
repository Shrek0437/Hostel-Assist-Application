package filesharing.peertopeer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class NetworkManager {
    private static final int BROADCAST_PORT = 9876;
    private static final int PEER_TIMEOUT = 30000; // 30 seconds
    
    private String myName;
    private int myPort;
    private ServerSocket serverSocket;
    private DatagramSocket broadcastSocket;
    private Map<String, PeerInfo> discoveredPeers;
    private List<FileMetadata> myFiles;
    private ExecutorService executorService;
    private volatile boolean running;
    private NetworkListener listener;
    
    public interface NetworkListener {
        void onPeerDiscovered(PeerInfo peer);
        void onPeerLeft(PeerInfo peer);
        void onFileReceived(String fileName, byte[] data);
        void onLog(String message);
        void onError(String error);
    }
    
    public NetworkManager(String name, int port, NetworkListener listener) {
        this.myName = name;
        this.myPort = port;
        this.listener = listener;
        this.discoveredPeers = new ConcurrentHashMap<>();
        this.myFiles = new CopyOnWriteArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
    }
    
    public void start() throws IOException {
        running = true;
        
        // Start server for incoming connections
        serverSocket = new ServerSocket(myPort);
        executorService.submit(this::acceptConnections);
        
        // Start UDP broadcast listener
        broadcastSocket = new DatagramSocket(BROADCAST_PORT);
        broadcastSocket.setBroadcast(true);
        executorService.submit(this::listenForBroadcasts);
        
        // Start peer cleanup task
        executorService.submit(this::cleanupPeers);
        
        // Announce presence
        announcePeer();
        
        listener.onLog("Network started on port " + myPort);
    }
    
    private void acceptConnections() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            } catch (IOException e) {
                if (running) {
                    listener.onError("Error accepting connection: " + e.getMessage());
                }
            }
        }
    }
    
    private void handleClient(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            
            Message request = (Message) in.readObject();
            Message response = processMessage(request);
            
            if (response != null) {
                out.writeObject(response);
            }
            
        } catch (Exception e) {
            listener.onError("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
    
    private Message processMessage(Message msg) {
        switch (msg.getType()) {
            case FILE_LIST_REQUEST:
                Message response = new Message(Message.MessageType.FILE_LIST_RESPONSE);
                response.setFileList(new ArrayList<>(myFiles));
                return response;
                
            case FILE_DOWNLOAD_REQUEST:
                return handleFileDownloadRequest(msg);
                
            default:
                return null;
        }
    }
    
    private Message handleFileDownloadRequest(Message msg) {
        String fileName = msg.getRequestedFileName();
        
        for (FileMetadata file : myFiles) {
            if (file.getFileName().equals(fileName)) {
                try {
                    File f = new File("shared_files/" + myName + "/" + fileName);
                    byte[] data = readFileBytes(f);
                    
                    Message response = new Message(Message.MessageType.FILE_DATA);
                    response.setFileData(data);
                    response.setRequestedFileName(fileName);
                    
                    listener.onLog("Sent file: " + fileName + " to peer");
                    return response;
                } catch (IOException e) {
                    listener.onError("Error reading file: " + e.getMessage());
                }
            }
        }
        return null;
    }
    
    private void listenForBroadcasts() {
        byte[] buffer = new byte[8192];
        
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                broadcastSocket.receive(packet);
                
                ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Message msg = (Message) ois.readObject();
                
                if (msg.getType() == Message.MessageType.PEER_ANNOUNCEMENT) {
                    String key = msg.getSenderIP() + ":" + msg.getSenderPort();
                    String myKey = getMyIP() + ":" + myPort;
                    
                    if (!key.equals(myKey)) {
                        PeerInfo peer = new PeerInfo(msg.getSenderName(), msg.getSenderIP(), msg.getSenderPort());
                        
                        if (!discoveredPeers.containsKey(key)) {
                            discoveredPeers.put(key, peer);
                            listener.onPeerDiscovered(peer);
                            listener.onLog("Discovered peer: " + peer);
                        } else {
                            discoveredPeers.get(key).updateLastSeen();
                        }
                    }
                }
            } catch (Exception e) {
                if (running) {
                    listener.onError("Error in broadcast listener: " + e.getMessage());
                }
            }
        }
    }
    
    public void announcePeer() {
        executorService.submit(() -> {
            try {
                Message msg = new Message(Message.MessageType.PEER_ANNOUNCEMENT);
                msg.setSenderName(myName);
                msg.setSenderIP(getMyIP());
                msg.setSenderPort(myPort);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(msg);
                byte[] data = baos.toByteArray();
                
                DatagramPacket packet = new DatagramPacket(
                    data, data.length,
                    InetAddress.getByName("255.255.255.255"),
                    BROADCAST_PORT
                );
                
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.send(packet);
                socket.close();
                
                listener.onLog("Announced presence to network");
            } catch (Exception e) {
                listener.onError("Error announcing peer: " + e.getMessage());
            }
        });
    }
    
    private void cleanupPeers() {
        while (running) {
            try {
                Thread.sleep(10000); // Check every 10 seconds
                
                long currentTime = System.currentTimeMillis();
                List<String> toRemove = new ArrayList<>();
                
                for (Map.Entry<String, PeerInfo> entry : discoveredPeers.entrySet()) {
                    if (currentTime - entry.getValue().getLastSeen() > PEER_TIMEOUT) {
                        toRemove.add(entry.getKey());
                        listener.onPeerLeft(entry.getValue());
                        listener.onLog("Peer timeout: " + entry.getValue());
                    }
                }
                
                for (String key : toRemove) {
                    discoveredPeers.remove(key);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    public List<FileMetadata> requestFileList(PeerInfo peer) {
        try {
            Socket socket = new Socket(peer.getIpAddress(), peer.getPort());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            Message request = new Message(Message.MessageType.FILE_LIST_REQUEST);
            out.writeObject(request);
            
            Message response = (Message) in.readObject();
            
            socket.close();
            
            if (response.getType() == Message.MessageType.FILE_LIST_RESPONSE) {
                return response.getFileList();
            }
        } catch (Exception e) {
            listener.onError("Error requesting file list from " + peer.getName() + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }
    
    public void downloadFile(FileMetadata file, String savePath) {
        executorService.submit(() -> {
            try {
                Socket socket = new Socket(file.getOwnerIP(), file.getOwnerPort());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                
                Message request = new Message(Message.MessageType.FILE_DOWNLOAD_REQUEST);
                request.setRequestedFileName(file.getFileName());
                out.writeObject(request);
                
                Message response = (Message) in.readObject();
                socket.close();
                
                if (response.getType() == Message.MessageType.FILE_DATA) {
                    byte[] data = response.getFileData();
                    File saveFile = new File(savePath + "/" + file.getFileName());
                    saveFile.getParentFile().mkdirs();
                    
                    try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                        fos.write(data);
                    }
                    
                    listener.onFileReceived(file.getFileName(), data);
                    listener.onLog("Downloaded: " + file.getFileName());
                }
            } catch (Exception e) {
                listener.onError("Error downloading file: " + e.getMessage());
            }
        });
    }
    
    public void addFile(File file) throws IOException {
        File sharedDir = new File("shared_files/" + myName);
        sharedDir.mkdirs();
        
        File dest = new File(sharedDir, file.getName());
        copyFile(file, dest);
        
        FileMetadata metadata = new FileMetadata(
            file.getName(),
            file.length(),
            getFileExtension(file.getName()),
            myName,
            getMyIP(),
            myPort
        );
        
        myFiles.add(metadata);
        listener.onLog("Added file: " + file.getName());
    }
    
    private void copyFile(File source, File dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }
    
    private byte[] readFileBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }
    
    private String getMyIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
    
    public List<PeerInfo> getDiscoveredPeers() {
        return new ArrayList<>(discoveredPeers.values());
    }
    
    public List<FileMetadata> getMyFiles() {
        return new ArrayList<>(myFiles);
    }
    
    public void stop() {
        running = false;
        
        try {
            if (serverSocket != null) serverSocket.close();
            if (broadcastSocket != null) broadcastSocket.close();
        } catch (IOException e) {
            // Ignore
        }
        
        executorService.shutdownNow();
        listener.onLog("Network stopped");
    }
}