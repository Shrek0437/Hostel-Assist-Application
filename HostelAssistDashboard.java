import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class HostelAssistDashboard extends JFrame {

    // Backend status flags
    private boolean socketReady = false;
    private boolean rmiReady = false;
    private boolean restReady = false;
    private boolean peerReady = false;
    // private boolean sharedMemReady = false;

    // Status labels
    private JLabel socketStatus;
    private JLabel rmiStatus;
    private JLabel restStatus;
    private JLabel peerStatus;
    private JLabel sharedMemStatus;

    public HostelAssistDashboard() {
        setTitle("Hostel Assist Integrated Distributed System");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);

        // Initializing Backends
        initializeBackends();
    }

    /* ================= UI ================= */

    private JPanel createHeader() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel title = new JLabel("Hostel Assist", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));

        JLabel subtitle = new JLabel(
                "Integrated Distributed Systems Dashboard",
                JLabel.CENTER
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(title);
        panel.add(subtitle);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JButton module1Btn = createButton("Module 1 Hostel Complaint System (Socket)");
        JButton module2Btn = createButton("Module 2 Room Info (RMI)");
        JButton module3Btn = createButton("Module 3 Notice Board (REST)");
        JButton module4Btn = createButton("Module 4 Peer-to-Peer (P2P)");
        JButton module5Btn = createButton("Module 5 Mess Feedback (Shared Memory)");

        socketStatus = new JLabel("Initializing...");
        rmiStatus = new JLabel("Initializing...");
        restStatus = new JLabel("Initializing...");
        peerStatus = new JLabel("Initializing...");
        sharedMemStatus = new JLabel("🟢 Shared Memory Service Running");

        module1Btn.addActionListener(e -> launchModule1Client());
        module2Btn.addActionListener(e -> launchModule2Client());
        module3Btn.addActionListener(e -> launchModule3Client());
        // module4Btn.addActionListener(e -> launchModule4Client());
        module5Btn.addActionListener(e -> launchModule5Clients());

        panel.add(module1Btn);
        panel.add(socketStatus);
        panel.add(module2Btn);
        panel.add(rmiStatus);
        panel.add(module3Btn);
        panel.add(restStatus);
        panel.add(module4Btn);
        panel.add(peerStatus);
        panel.add(module5Btn);
        panel.add(sharedMemStatus);

        return panel;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    /* ================= BACKEND INITIALIZATION ================= */

    private void initializeBackends() {
        startSocketModule();
        startRMIModule();
        startRESTModule();
        // startPeerModule();
        // startSharedMemoryModule();
    }

    private void startSocketModule() {
        try {
            startProcess(
                new String[]{"java","server"},
                new File("Module-1")
            );
            socketReady = true;
            socketStatus.setText("🟢 Complaint Server Running");
        } catch (Exception e) {
            socketStatus.setText("🔴 Complaint Server Failed");
            logError("Complaint backend failed", e);
        }
    }

    private void startRMIModule() {
        try {
            startProcess(
                    new String[]{"java", "server.RMIserver"},
                    new File("Module-2")
            );
            rmiReady = true;
            rmiStatus.setText("🟢 RMI Server Running");
        } catch (Exception e) {
            rmiStatus.setText("🔴 RMI Server Failed");
            logError("RMI backend failed", e);
        }
    }

    private void startPeerModule() {

    }

    private void startRESTModule() {
        try {
            startProcess(
            new String[]{getVenvPythonPath(), "app.py"},
            new File("Module-3/backend")
            );
            restReady = true;
            restStatus.setText("🟢 REST Service Running");
        } catch (Exception e) {
            restStatus.setText("🔴 REST Service Failed");
            logError("REST backend failed", e);
        }
    }

    private String getVenvPythonPath() {
      String os = System.getProperty("os.name").toLowerCase();
      if (os.contains("win")) {
        return "Module-3/backend/flask-backend/Scripts/python.exe";
      } else {
        return "Module-3/backend/flask-backend/bin/python";
      }
    }

    // private void startSharedMemoryModule() {
    //     try {
    //         startProcess(
    //                 new String[]{"java", "SharedFeedback"},
    //                 new File("Module-5")
    //         );
    //         sharedMemReady = true;
    //         sharedMemStatus.setText("🟢 Shared Memory Ready");
    //     } catch (Exception e) {
    //         sharedMemStatus.setText("🔴 Shared Memory Failed");
    //         logError("Shared memory backend failed", e);
    //     }
    // }

    /* ================= CLIENT LAUNCH ================= */

    private void launchModule1Client() {
        if (!socketReady) {
            showError("Complaint server is not running");
            return;
        }
        runClient("java","ComplaintClientUI","Module-1");
    }

    private void launchModule2Client() {
        if (!rmiReady) {
            showError("RMI backend is not running.");
            return;
        }
        runClient("java", "client.HostelRoomUI", "Module-2");
    }

    private void launchModule3Client() {
        if (!restReady) {
            showError("REST backend is not running.");
            return;
        }
        try {
            startProcess(
                    new String[]{"C:/Program Files/nodejs/npm.cmd", "run","dev"},
                    new File("Module-3/frontend/client-notice-board")
            );
            Thread.sleep(3000);
            Desktop.getDesktop().browse(new URI("http://localhost:5173/"));
        } catch (Exception e) {
            showError("Failed to open browser for Module 3.");
        }
    }


    private void launchModule4Client() {

    }

    private void launchModule5Clients() {
        runClient("java", "FeedbackUI", "Module-5");
        runClient("java", "DisplayUI", "Module-5");
    }

    /* ================= PROCESS UTILITIES ================= */

    private void startProcess(String[] command, File dir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        if (dir != null) pb.directory(dir);
        pb.inheritIO();
        pb.start();
    }

    private void runClient(String cmd, String className, String dir) {
        try {
            startProcess(new String[]{cmd, className}, new File(dir));
        } catch (Exception e) {
            showError("Failed to launch client: " + className);
        }
    }

    /* ================= ERROR HANDLING ================= */

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void logError(String msg, Exception e) {
        System.err.println("[ERROR] " + msg);
        e.printStackTrace();
    }

    /* ================= MAIN ================= */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new HostelAssistDashboard().setVisible(true)
        );
    }
}
