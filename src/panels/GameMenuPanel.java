package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.StyledButton;

public class GameMenuPanel extends JPanel {
    public GameMenuPanel(Runnable startGameCallback) {
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 30, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        

        JLabel titleLabel = new JLabel("Chét Gêm");
        titleLabel.setForeground(new Color(255,255,255));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 100, 0, 100);
        add(titleLabel, gbc);
        JLabel authorLabel = new JLabel("@nkdkhtl");
        authorLabel.setForeground(new Color(255,255,255));
        authorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 100, 0, 100);
        add(authorLabel, gbc);
        StyledButton playButton = new StyledButton("Play", new Color(140, 200, 75), new Color(120, 180, 60), Color.WHITE);
        playButton.setFont(new Font("Arial", Font.PLAIN, 18));
        playButton.setPreferredSize(new Dimension(145,40));
        playButton.addActionListener(e -> startGameCallback.run());

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 100, 10, 100);
        add(playButton, gbc);

        StyledButton optionButton = new StyledButton("Settings", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);
        optionButton.setFont(new Font("Arial", Font.PLAIN, 18));
        optionButton.addActionListener(e -> System.exit(0));

        gbc.gridy = 4;
        add(optionButton, gbc);
        
        StyledButton exitButton = new StyledButton("Exit", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.PLAIN, 18));
        exitButton.setPreferredSize(new Dimension(145,40));
        exitButton.addActionListener(e -> System.exit(0));

        gbc.gridy = 5;
        add(exitButton, gbc);
    }
}
