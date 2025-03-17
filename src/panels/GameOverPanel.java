package panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.Board;
import utils.StyledButton;

public class GameOverPanel extends JPanel {
	private Board board;
	JPanel rematchPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JLabel winnerLabel = new JLabel();
	JLabel reasonLabel = new JLabel();
    public GameOverPanel(Board board, String winner) {
        this.board = board;
        setLayout(new GridBagLayout());
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        setOpaque(false);
        buttonPanel.setOpaque(false);
        
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 30));
        winnerLabel.setForeground(Color.white);
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        reasonLabel.setFont(new Font("Arial", Font.BOLD, 15));
        reasonLabel.setForeground(new Color(235,236,208));
        reasonLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        StyledButton rematchButton = new StyledButton("Rematch", new Color(140, 200, 75), new Color(120, 180, 60), Color.WHITE);
        StyledButton homeButton = new StyledButton("Return to Menu", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);
        StyledButton exitButton = new StyledButton("Exit", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);

        // Button actions
        rematchButton.addActionListener(e -> board.resetBoard());
        homeButton.addActionListener(e -> board.returnToHome());
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(rematchButton);
        buttonPanel.add(homeButton);
        buttonPanel.add(exitButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 10, 2, 10);
        add(winnerLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(2, 10, 10, 10);
        add(reasonLabel,gbc);
        
        gbc.gridy = 2;
        add(buttonPanel, gbc);
    }
    
    public void showResult(String result,String reason) {
    	winnerLabel.setText(result);
    	reasonLabel.setText(reason);
        setVisible(true);
    }
    
    public void reset() {
        this.setVisible(false);  // Hide the game over panel
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw a semi-transparent dark overlay
        g2d.setColor(new Color(30, 30, 30, 150));  // 150 is the transparency (0-255)
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.dispose();
    }

}
