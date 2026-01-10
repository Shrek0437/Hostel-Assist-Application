import javax.swing.*;
import java.awt.*;

public class DisplayUI extends JFrame {

    private JLabel goodLabel, avgLabel, poorLabel;

    public DisplayUI() {
        setTitle("Live Mess Feedback");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        goodLabel = new JLabel();
        avgLabel = new JLabel();
        poorLabel = new JLabel();

        setLayout(new GridLayout(3, 1));
        add(goodLabel);
        add(avgLabel);
        add(poorLabel);

        Timer timer = new Timer(1000, e -> refresh());
        timer.start();

        setVisible(true);
    }

    private void refresh() {
        int[] data = SharedFeedback.readFeedback();
        goodLabel.setText("Good: " + data[0]);
        avgLabel.setText("Average: " + data[1]);
        poorLabel.setText("Poor: " + data[2]);
    }

    public static void main(String[] args) {
        new DisplayUI();
    }
}
