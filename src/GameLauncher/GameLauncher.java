package GameLauncher;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import bot.Bot;
import panels.GameMenuPanel;
import panels.GameModePanel;
import panels.SettingsPanel;
import utils.BackgroundPanel;
import utils.SettingsManager;
import utils.StyledButton;

public class GameLauncher {
    private static JFrame frame;
    private static BackgroundPanel backgroundPanel;
    private static Board board;
    public static boolean botColor;

	private static StyledButton homeButton;
	private static StyledButton exitButton;
	private static StyledButton drawButton;
	private static StyledButton resignButton;

    public static void initialize() {
        frame = new JFrame("Chet Gem v1.0");
        frame.getContentPane().setBackground(new Color(0, 0, 0, 0));
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(960, 720));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        setCustomIcon(frame);
        
        SettingsManager.loadSettings();

        showMenu();
        frame.setVisible(true);
    }

    private static void setCustomIcon(JFrame frame) {
        ImageIcon icon = new ImageIcon(GameLauncher.class.getResource("/icon/icon.png"));
        // Ensure correct path
        frame.setIconImage(icon.getImage());
    }

    public static void showMenu() {
        GameMenuPanel menuPanel = new GameMenuPanel();
        frame.getContentPane().removeAll(); // Clear previous components

        backgroundPanel = new BackgroundPanel("/background/pixel_background.jpg");
        frame.setContentPane(backgroundPanel);

        frame.add(menuPanel);
        frame.revalidate();
        frame.repaint();
    }

    public static void showSettings() {
        SettingsPanel settingsPanel = new SettingsPanel();
        frame.getContentPane().removeAll(); // Clear previous components

        backgroundPanel = new BackgroundPanel("/background/pixel_background.jpg");
        frame.setContentPane(backgroundPanel);

        frame.add(settingsPanel);
        frame.revalidate();
        frame.repaint();
    }

    public static void showGameMode() {
        GameModePanel modePanel = new GameModePanel();
        frame.getContentPane().removeAll();
        frame.setContentPane(backgroundPanel);
        frame.add(modePanel);
        frame.revalidate();
        frame.repaint();
    }

    public static void startGame(boolean isBotPlaying, boolean isHardMode) {
        frame.getContentPane().removeAll(); // Clear menu

        JLayeredPane layeredPane = new JLayeredPane();
        frame.setContentPane(layeredPane);
        frame.setLayout(null);

        int boardSize = 640; // Adjust according to your chessboard

        // Background Panel (Lowest Layer)
        backgroundPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(backgroundPanel, Integer.valueOf(0)); // Lower z-index

        // Chess Board
        board = new Board();
        board.setBounds(20, 20, boardSize, boardSize); // Adjust based on your chessboard size
        layeredPane.add(board, Integer.valueOf(1)); // Higher z-index
        
        drawButton = new StyledButton("Draw", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);
        resignButton = new StyledButton("Resign", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);
        homeButton = new StyledButton("Menu", new Color(140, 200, 75), new Color(120, 180, 60), Color.WHITE);
		exitButton = new StyledButton("Exit", new Color(220, 60, 60), new Color(220, 80, 80), Color.WHITE);

		
		drawButton.setBounds(720, 200, 160, 40);
		resignButton.setBounds(720, 250, 160, 40);
		homeButton.setBounds(720, 300, 160, 40);
		exitButton.setBounds(720, 350, 160, 40);

		resignButton.addActionListener(_ -> {
			int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to resign?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
            	board.setIsResign(true);
            	board.updateGame();
            }
		});
		
		drawButton.addActionListener(_ -> {
			int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to draw?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
            	board.setIsDraw(true);
            	board.updateGame();
            }
		});
		
		homeButton.addActionListener(_ -> {
			int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to return to the menu?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                GameLauncher.showMenu();
            }
		});
		
		exitButton.addActionListener(_ -> {
			int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
		});
		layeredPane.add(drawButton,Integer.valueOf(1));
		layeredPane.add(resignButton,Integer.valueOf(1));
		layeredPane.add(homeButton,Integer.valueOf(1));
		layeredPane.add(exitButton,Integer.valueOf(1));
        
        if (isBotPlaying) {
            board.enableBot(true, isHardMode, Bot.getBotColor()); // Enable EasyBot or HardBot
        }

        frame.revalidate();
        frame.repaint();
    }
    

    public static Board getBoard() {
        return board;
    }
}