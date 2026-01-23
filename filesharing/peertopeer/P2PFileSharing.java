package filesharing.peertopeer;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class P2PFileSharing extends JFrame implements NetworkManager.NetworkListener {
    private NetworkManager networkManager;
    private String userName;
    private int port;
    
    // UI Components
    private JTable peersTable;
    private JTable myFilesTable;
    private JTable availableFilesTable;
    private JTextArea logArea;
    private DefaultTableModel peersModel;
    private DefaultTableModel myFilesModel;
    private DefaultTableModel availableFilesModel;
    private JLabel statusLabel;
    private JButton uploadButton;
    private JButton refreshPeersButton;
    private JButton downloadButton;
    private JButton viewFilesButton;
    
    private Map<String, PeerInfo> peerMap;
    private Map<String, FileMetadata> availableFilesMap;
    
    public P2PFileSharing(String userName, int port) {
        this.userName = userName;
        this.port = port;
        this.peerMap = new HashMap<>();
        this.availableFilesMap = new HashMap<>();
        
        initUI();
        initNetwork();
    }
    
    private void initUI() {
        setTitle("P2P Student Resource Sharing - " + userName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel - Status
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Split pane with tabs
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        
        // Left side - Peers and My Files
        JTabbedPane leftTabs = new JTabbedPane();
        leftTabs.addTab("Discovered Peers", createPeersPanel());
        leftTabs.addTab("My Files", createMyFilesPanel());
        splitPane.setLeftComponent(leftTabs);
        
        // Right side - Available Files and Log
        JTabbedPane rightTabs = new JTabbedPane();
        rightTabs.addTab("Available Files", createAvailableFilesPanel());
        rightTabs.addTab("Activity Log", createLogPanel());
        splitPane.setRightComponent(rightTabs);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Bottom panel - Actions
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("System Status"));
        
        statusLabel = new JLabel("Initializing...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(new Color(0, 120, 0));
        panel.add(statusLabel, BorderLayout.WEST);
        
        JLabel infoLabel = new JLabel("User: " + userName + " | Port: " + port);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(infoLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createPeersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        String[] columns = {"Peer Name", "IP Address", "Port"};
        peersModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        peersTable = new JTable(peersModel);
        peersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peersTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(peersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshPeersButton = new JButton("Refresh Peers");
        refreshPeersButton.addActionListener(e -> refreshPeers());
        
        viewFilesButton = new JButton("View Files");
        viewFilesButton.addActionListener(e -> viewPeerFiles());
        
        buttonPanel.add(refreshPeersButton);
        buttonPanel.add(viewFilesButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMyFilesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        String[] columns = {"File Name", "Size", "Type"};
        myFilesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myFilesTable = new JTable(myFilesModel);
        myFilesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myFilesTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(myFilesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        uploadButton = new JButton("Upload File");
        uploadButton.addActionListener(e -> uploadFile());
        
        buttonPanel.add(uploadButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createAvailableFilesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        String[] columns = {"File Name", "Size", "Type", "Owner", "Owner IP"};
        availableFilesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availableFilesTable = new JTable(availableFilesModel);
        availableFilesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableFilesTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(availableFilesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        downloadButton = new JButton("Download Selected");
        downloadButton.addActionListener(e -> downloadFile());
        
        buttonPanel.add(downloadButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton clearButton = new JButton("Clear Log");
        clearButton.addActionListener(e -> logArea.setText(""));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton announceButton = new JButton("Announce Presence");
        announceButton.addActionListener(e -> {
            networkManager.announcePeer();
            log("Broadcasting presence to network...");
        });
        
        panel.add(announceButton);
        
        return panel;
    }
    
    private void initNetwork() {
        try {
            networkManager = new NetworkManager(userName, port, this);
            networkManager.start();
            statusLabel.setText("Online - Listening on port " + port);
            statusLabel.setForeground(new Color(0, 150, 0));
        } catch (IOException e) {
            statusLabel.setText("Error - Failed to start network");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, 
                "Failed to start network: " + e.getMessage(),
                "Network Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void uploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try {
                networkManager.addFile(file);
                refreshMyFiles();
                JOptionPane.showMessageDialog(this, 
                    "File uploaded successfully: " + file.getName(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error uploading file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshPeers() {
        networkManager.announcePeer();
        log("Refreshing peer list...");
    }
    
    private void viewPeerFiles() {
        int selectedRow = peersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a peer first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String peerName = (String) peersModel.getValueAt(selectedRow, 0);
        PeerInfo peer = peerMap.get(peerName);
        
        if (peer != null) {
            new Thread(() -> {
                List<FileMetadata> files = networkManager.requestFileList(peer);
                SwingUtilities.invokeLater(() -> {
                    for (FileMetadata file : files) {
                        String key = file.getOwnerIP() + ":" + file.getFileName();
                        if (!availableFilesMap.containsKey(key)) {
                            availableFilesMap.put(key, file);
                            availableFilesModel.addRow(new Object[]{
                                file.getFileName(),
                                file.getFileSizeFormatted(),
                                file.getFileType(),
                                file.getOwner(),
                                file.getOwnerIP()
                            });
                        }
                    }
                    log("Retrieved " + files.size() + " files from " + peer.getName());
                });
            }).start();
        }
    }
    
    private void downloadFile() {
        int selectedRow = availableFilesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a file to download.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String fileName = (String) availableFilesModel.getValueAt(selectedRow, 0);
        String ownerIP = (String) availableFilesModel.getValueAt(selectedRow, 4);
        String key = ownerIP + ":" + fileName;
        
        FileMetadata file = availableFilesMap.get(key);
        
        if (file != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Select Download Location");
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File downloadDir = fileChooser.getSelectedFile();
                networkManager.downloadFile(file, downloadDir.getAbsolutePath());
                log("Downloading " + fileName + " from " + file.getOwner() + "...");
            }
        }
    }
    
    private void refreshMyFiles() {
        myFilesModel.setRowCount(0);
        List<FileMetadata> files = networkManager.getMyFiles();
        
        for (FileMetadata file : files) {
            myFilesModel.addRow(new Object[]{
                file.getFileName(),
                file.getFileSizeFormatted(),
                file.getFileType()
            });
        }
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    private void shutdown() {
        if (networkManager != null) {
            networkManager.stop();
        }
        System.exit(0);
    }
    
    // NetworkListener implementations
    @Override
    public void onPeerDiscovered(PeerInfo peer) {
        SwingUtilities.invokeLater(() -> {
            if (!peerMap.containsKey(peer.getName())) {
                peerMap.put(peer.getName(), peer);
                peersModel.addRow(new Object[]{
                    peer.getName(),
                    peer.getIpAddress(),
                    peer.getPort()
                });
            }
        });
    }
    
    @Override
    public void onPeerLeft(PeerInfo peer) {
        SwingUtilities.invokeLater(() -> {
            peerMap.remove(peer.getName());
            for (int i = 0; i < peersModel.getRowCount(); i++) {
                if (peersModel.getValueAt(i, 0).equals(peer.getName())) {
                    peersModel.removeRow(i);
                    break;
                }
            }
        });
    }
    
    @Override
    public void onFileReceived(String fileName, byte[] data) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "File downloaded successfully: " + fileName,
                "Download Complete", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    @Override
    public void onLog(String message) {
        log(message);
    }
    
    @Override
    public void onError(String error) {
        log("ERROR: " + error);
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Get user input
        SwingUtilities.invokeLater(() -> {
            JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
            JTextField nameField = new JTextField("Student" + (int)(Math.random() * 1000));
            JTextField portField = new JTextField(String.valueOf(8000 + (int)(Math.random() * 1000)));
            
            panel.add(new JLabel("Your Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Port Number:"));
            panel.add(portField);
            
            int result = JOptionPane.showConfirmDialog(null, panel, 
                "P2P Setup", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText().trim();
                    int port = Integer.parseInt(portField.getText().trim());
                    
                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Name cannot be empty!");
                        System.exit(0);
                    }
                    
                    P2PFileSharing app = new P2PFileSharing(name, port);
                    app.setVisible(true);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid port number!");
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        });
    }
}