import javax.swing.*;

public class FeedbackUI extends JFrame {

    public FeedbackUI() {
        setTitle("Mess Feedback");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton good = new JButton("Good");
        JButton avg = new JButton("Average");
        JButton poor = new JButton("Poor");

        good.addActionListener(e -> SharedFeedback.updateFeedback(1));
        avg.addActionListener(e -> SharedFeedback.updateFeedback(2));
        poor.addActionListener(e -> SharedFeedback.updateFeedback(3));

        JPanel panel = new JPanel();
        panel.add(good);
        panel.add(avg);
        panel.add(poor);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new FeedbackUI();
    }
}
