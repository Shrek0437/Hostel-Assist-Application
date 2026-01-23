import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ComplaintClientUI extends JFrame {

    private JTextField roomField;
    private JComboBox<String> categoryBox;
    private JTextArea descriptionArea;

    public ComplaintClientUI() {
        setTitle("Hostel Complaint System");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Hostel Complaint System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        // Room Number
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Room Number:"), gbc);

        roomField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(roomField, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Category:"), gbc);

        String[] categories = {"Water", "Electricity", "Cleanliness", "Other"};
        categoryBox = new JComboBox<>(categories);
        gbc.gridx = 1;
        panel.add(categoryBox, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Complaint:"), gbc);

        descriptionArea = new JTextArea(5, 15);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);

        // Submit Button
        JButton submitButton = new JButton("Submit Complaint");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(submitButton, gbc);

        add(panel);

        // Button action
        submitButton.addActionListener(e -> submitComplaint());
    }

    private void submitComplaint() {
        String roomNo = roomField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String description = descriptionArea.getText().trim();

        if (roomNo.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // CHANGE IP if server is on another machine
            Socket socket = new Socket("localhost", 6000);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            server.Complaint complaint = new server.Complaint(roomNo, category, description);
            out.writeObject(complaint);
            out.flush();

            String response = (String) in.readObject();

            JOptionPane.showMessageDialog(this,
                    response,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            socket.close();

            // Clear fields
            roomField.setText("");
            descriptionArea.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Server not reachable",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ComplaintClientUI().setVisible(true);
        });
    }
}

