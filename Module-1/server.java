import java.io.*;
import java.net.*;
import java.util.*;

// MAIN SERVER CLASS
public class server {

    private static final Map<Integer, Complaint> complaints =
            Collections.synchronizedMap(new HashMap<>());

    private static int complaintId = 1000;

    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Complaint Server started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                new Thread(new ClientHandler(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // THREAD HANDLER
    static class ClientHandler implements Runnable {
        private Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                Complaint complaint = (Complaint) in.readObject();

                int id;
                synchronized (server.class) {
                    id = ++complaintId;        // generate ID
                    complaints.put(id, complaint);
                }

                System.out.println("New Complaint Received:");
                System.out.println("Complaint ID: " + id);
                System.out.println(complaint);
                System.out.println("---------------------");

                out.writeObject("Complaint submitted successfully");
                out.flush();


                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // COMPLAINT CLASS
    static class Complaint implements Serializable {
        private static final long serialVersionUID = 1L;

        String roomNo;
        String category;
        String description;

        Complaint(String roomNo, String category, String description) {
            this.roomNo = roomNo;
            this.category = category;
            this.description = description;
        }

        @Override
        public String toString() {
            return "Room: " + roomNo +
                   ", Category: " + category +
                   ", Description: " + description;
        }
    }
}
