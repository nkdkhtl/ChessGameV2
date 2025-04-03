package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import GameLauncher.GameLauncher;
import utils.StyledButton;
import utils.ThemeManager;

public class SettingsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    GridBagConstraints gbc = new GridBagConstraints();
    public SettingsPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 30, 200));
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 100, 0, 100);
        add(titleLabel, gbc);

        // Theme settings
        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setForeground(Color.WHITE);
        themeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 100, 10, 100);
        add(themeLabel, gbc);

        JComboBox<String> themeComboBox = new JComboBox<>(new String[] { "classic", "pixels", "twilight", "modern", "8bit" });
        gbc.gridy = 2;
        add(themeComboBox, gbc);

        // Sound effects settings
        JLabel soundLabel = new JLabel("Sound Effects:");
        soundLabel.setForeground(Color.WHITE);
        soundLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridy = 3;
        add(soundLabel, gbc);

        JComboBox<String> soundComboBox = new JComboBox<>(new String[] { "On", "Off" });
        gbc.gridy = 4;
        add(soundComboBox, gbc);

        // Clock settings
        JLabel clockLabel = new JLabel("Clock:");
        clockLabel.setForeground(Color.WHITE);
        clockLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridy = 5;
        add(clockLabel, gbc);

        JComboBox<String> clockComboBox = new JComboBox<>(new String[] { "1 minutes" ,"3 minutes", "5 minutes", "10 minutes", "15 minutes" });
        gbc.gridy = 6;
        add(clockComboBox, gbc);

        // Save button
        StyledButton saveButton = new StyledButton("Save", new Color(140, 200, 75), new Color(120, 180, 60), Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.PLAIN, 18));
        saveButton.setPreferredSize(new Dimension(145, 40));
        saveButton.addActionListener(_ -> {
        	saveSettings((String) themeComboBox.getSelectedItem(), (String) soundComboBox.getSelectedItem(), (String) clockComboBox.getSelectedItem());
        	GameLauncher.showMenu();
        });
        gbc.gridy = 7;
        add(saveButton, gbc);

        // Back button
        StyledButton backButton = new StyledButton("Back", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.setPreferredSize(new Dimension(145, 40));
        backButton.addActionListener(_ -> GameLauncher.showMenu());
        gbc.gridy = 8;
        add(backButton, gbc);
    }

    private void saveSettings(String theme, String sound, String clock) {
        // Implement the logic to save and apply the settings
    	ThemeManager.setTheme(theme); // Apply the selected themes
        int minutes = Integer.parseInt(clock.split(" ")[0]); // Extract minutes from the selected option
        GameLauncher.getBoard().setDuration(minutes);
		
    }
}