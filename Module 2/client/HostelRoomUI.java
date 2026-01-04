package client;

import common.RoomInfo;

import javax.swing.*;
import java.awt.*;

public class HostelRoomUI extends JFrame {

    private JTextField roomNumberField;
    private JTextArea resultArea;

    public HostelRoomUI() {

        setTitle("Hostel Room Information Service");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel roomLabel = new JLabel("Enter Room Number:");
        roomNumberField = new JTextField(10);
        JButton searchButton = new JButton("Search");

        inputPanel.add(roomLabel);
        inputPanel.add(roomNumberField);
        inputPanel.add(searchButton);

        // Result Area
        resultArea = new JTextArea(10, 35);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button Action
        searchButton.addActionListener(e -> fetchRoomDetails());
    }

    private void fetchRoomDetails() {
        try {
            int roomNumber = Integer.parseInt(roomNumberField.getText());

            RoomInfo info = RMIclient.fetchRoomDetails(roomNumber);

            if (info != null) {
                resultArea.setText(
                        "Room Number: " + info.getRoomNumber() + "\n" +
                        "Occupants: " + info.getOccupants() + "\n" +
                        "Warden Name: " + info.getWardenName() + "\n" +
                        "Warden Contact: " + info.getWardenContact()
                );
            } else {
                resultArea.setText("No details found for this room.");
            }

        } catch (NumberFormatException ex) {
            resultArea.setText("Please enter a valid room number.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HostelRoomUI().setVisible(true);
        });
    }
}
