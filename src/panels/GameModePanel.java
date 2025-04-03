package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import GameLauncher.GameLauncher;
import utils.StyledButton;

public class GameModePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameModePanel() {
		setLayout(new GridBagLayout());
		setBackground(new Color(30,30,30,200));
		GridBagConstraints gbc = new GridBagConstraints();
		
		
		JLabel titleLabel = new JLabel("Select Game Mode");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 100, 0, 100);
        add(titleLabel, gbc);
        
     // Play with Player Button
        StyledButton playerButton = new StyledButton("Play with Player", new Color(140, 200, 75), new Color(120, 180, 60), Color.WHITE);
        playerButton.setFont(new Font("Arial", Font.PLAIN, 14));
        playerButton.setPreferredSize(new Dimension(200, 40));
        playerButton.addActionListener(_ -> GameLauncher.startGame(false, false)); 

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 100, 10, 100);
        add(playerButton, gbc);

        // Play with EasyBot Button
        StyledButton easyBotButton = new StyledButton("Play with EasyBot", new Color(100, 180, 220), new Color(80, 150, 200), Color.WHITE);
        easyBotButton.setFont(new Font("Arial", Font.PLAIN, 14));
        easyBotButton.setPreferredSize(new Dimension(200, 40));
        easyBotButton.addActionListener(_ -> GameLauncher.startGame(true, false));

        gbc.gridy = 2;
        add(easyBotButton, gbc);

        // Play with HardBot Button
        StyledButton hardBotButton = new StyledButton("Play with HardBot", new Color(220, 100, 100), new Color(200, 80, 80), Color.WHITE);
        hardBotButton.setFont(new Font("Arial", Font.PLAIN, 14));
        hardBotButton.setPreferredSize(new Dimension(200, 40));
        hardBotButton.addActionListener(_ -> GameLauncher.startGame(true, true));

        gbc.gridy = 3;
        add(hardBotButton, gbc);
        
        // Back button
        StyledButton backButton = new StyledButton("Back", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.addActionListener(_ -> GameLauncher.showMenu());
        gbc.gridy = 4;
        add(backButton, gbc);

	}
}
