import java.io.*;
import java.net.*;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        String server = "localhost";
        int port = 5000;

        try (Socket socket = new Socket(server, port)) {

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            Scanner sc = new Scanner(System.in);

            // Take input from user
            System.out.print("Enter Room Number: ");
            String roomNo = sc.nextLine();

            System.out.print("Enter Category (Water/Electricity/Cleanliness/Other): ");
            String category = sc.nextLine();

            System.out.print("Enter Complaint Description: ");
            String description = sc.nextLine();

            // Create complaint dynamically
            server.Complaint complaint =
                    new server.Complaint(roomNo, category, description);

            // Send to server
            out.writeObject(complaint);
            out.flush();

            // Receive acknowledgment
            System.out.println("Server Response: " + in.readLine());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
