package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class TimerPanel extends JPanel {
    private JLabel whiteTimerLabel;
    private JLabel blackTimerLabel;

    public TimerPanel() {
        setLayout(new GridLayout(2, 1));
        setBackground(new Color(50, 50, 50));
        setPreferredSize(new Dimension(160, 60));

        whiteTimerLabel = new JLabel("White: 10:00", SwingConstants.CENTER);
        blackTimerLabel = new JLabel("Black: 10:00", SwingConstants.CENTER);
        
        whiteTimerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        blackTimerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        whiteTimerLabel.setForeground(Color.WHITE);
        blackTimerLabel.setForeground(Color.WHITE);

        add(blackTimerLabel);
        add(whiteTimerLabel);

        // Auto-refresh every second
        Timer refreshTimer = new Timer(1000, e -> refreshUI());
        refreshTimer.start();
    }
    
    private void refreshUI() {
        SwingUtilities.invokeLater(() -> {
            repaint();
            revalidate();
            getParent().repaint(); // Force parent to repaint
        });
    }

    public void updateWhiteTimer(String time) {
        whiteTimerLabel.setText("White: " + time);
        refreshUI();
    }

    public void updateBlackTimer(String time) {
        blackTimerLabel.setText("Black: " + time);
        refreshUI();
    }
}
