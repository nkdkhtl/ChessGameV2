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
import bot.Bot;
import utils.SettingsManager;
import utils.SoundManager;
import utils.StyledButton;
import utils.ThemeManager;

public class SettingsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    GridBagConstraints gbc = new GridBagConstraints();

    public SettingsPanel() {
    	
    	SettingsManager.loadSettings();
        
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 30, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE; // Retain component size
        gbc.anchor = GridBagConstraints.CENTER; // Center-align components
        gbc.weightx = 1.0; // Distribute horizontal space
        gbc.weighty = 1.0; // Distribute vertical space
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components

        // Title
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns for centering
        add(titleLabel, gbc);

        // Theme settings

        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setForeground(Color.WHITE);
        themeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset to single column
        gbc.insets = new Insets(10, 100, 10, 100);
        add(themeLabel, gbc);

        JComboBox<String> themeComboBox = new JComboBox<>(new String[] { "classic", "pixels", "twilight", "modern", "8bit" });
        String currentTheme = SettingsManager.getSetting("theme", "classic");
        themeComboBox.setSelectedItem(currentTheme);	
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(themeComboBox, gbc);
        
        // Bot color settings
        JLabel botColorLabel = new JLabel("Bot color:");
        botColorLabel.setForeground(Color.WHITE);
        botColorLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(botColorLabel, gbc);

        JComboBox<String> botColorComboBox = new JComboBox<>(new String[] { "black", "white"});
        String currentBotColor = SettingsManager.getSetting("botColor", "black");
        themeComboBox.setSelectedItem(currentBotColor);	
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(botColorComboBox, gbc);
        
        // Sound effects settings
        JLabel soundLabel = new JLabel("Sound Effects:");
        soundLabel.setForeground(Color.WHITE);
        soundLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(soundLabel, gbc);

        JComboBox<String> soundComboBox = new JComboBox<>(new String[] { "On", "Off" });
        String currentSound = SettingsManager.getSetting("sound", "On");
        soundComboBox.setSelectedItem(currentSound);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(soundComboBox, gbc);

        // Clock settings
        JLabel clockLabel = new JLabel("Clock:");
        clockLabel.setForeground(Color.WHITE);
        clockLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(clockLabel, gbc);

        JComboBox<String> clockComboBox = new JComboBox<>(new String[] { "1 minutes" , "3 minutes", "5 minutes", "10 minutes", "15 minutes" });
        String currentClock = SettingsManager.getSetting("clock", "1 minutes");
        clockComboBox.setSelectedItem(currentClock);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(clockComboBox, gbc);

        
        //To CENTERED
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false); // Make it transparent to match the background

        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.insets = new Insets(0, 10, 0, 10); // Add spacing between buttons

        // Save button
        StyledButton saveButton = new StyledButton("Save", new Color(140, 200, 75), new Color(120, 180, 60), Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.PLAIN, 18));
        saveButton.setPreferredSize(new Dimension(145, 40));
        saveButton.addActionListener(_ -> {
            saveSettings(
                (String) themeComboBox.getSelectedItem(),
                (String) soundComboBox.getSelectedItem(),
                (String) clockComboBox.getSelectedItem(),
                (String) botColorComboBox.getSelectedItem()
            );
            GameLauncher.showMenu();
        });
        buttonGbc.gridx = 0; // First button in the panel
        buttonPanel.add(saveButton, buttonGbc);

        // Back button
        StyledButton backButton = new StyledButton("Back", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.setPreferredSize(new Dimension(145, 40));
        backButton.addActionListener(_ -> GameLauncher.showMenu());
        buttonGbc.gridx = 1; // Second button in the panel
        buttonPanel.add(backButton, buttonGbc);

        // Add the button panel to the main panel
        gbc.gridx = 0;
        gbc.gridy = 6; // Place it below all other components
        gbc.gridwidth = 2; // Span across two columns to center the panel
        add(buttonPanel, gbc);
    }

    private void saveSettings(String theme, String sound, String clock,String botColor) {
        Bot.setBotColor((botColor.equals("white")) ? true : false);
    	ThemeManager.setTheme(theme); // Apply the selected themes
        SoundManager.setSoundSate(sound);
        int minutes = Integer.parseInt(clock.split(" ")[0]); // Extract minutes from the selected option
        GameLauncher.getBoard().setDuration(minutes);
        SettingsManager.saveSettings("theme", theme);
        SettingsManager.saveSettings("sound", sound);
        SettingsManager.saveSettings("clock", clock);
        SettingsManager.saveSettings("botColor", botColor);
    }
}