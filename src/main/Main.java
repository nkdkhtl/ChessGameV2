package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import panels.GameMenuPanel;
import panels.GameModePanel;
import utils.BackgroundPanel;

public class Main {
    private static JFrame frame;
    private static BackgroundPanel backgroundPanel;
    
    public static void main(String[] args) {
        frame = new JFrame("Chess Game");
        frame.getContentPane().setBackground(new Color(0, 0, 0, 0));
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(960, 720));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setCustomIcon(frame);

        showMenu();
        frame.setVisible(true);
    }
    
    private static void setCustomIcon(JFrame frame) {
        ImageIcon icon = new ImageIcon(Main.class.getResource("/icon/icon.png")); // Ensure correct path
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

   	    int boardSize = 640;  // Adjust according to your chessboard
   	    int margin = 20;      // Space between board and timer

   	    // Background Panel (Lowest Layer)
        backgroundPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
   	    layeredPane.add(backgroundPanel, Integer.valueOf(0)); // Lower z-index
   	    
        // Chess Board
   	    Board board = new Board();
   	    board.setBounds(20, 20, boardSize, boardSize); // Adjust based on your chessboard size
   	    layeredPane.add(board, Integer.valueOf(1)); // Higher z-index
   	    
   	    if (isBotPlaying) {
   	    	board.enableBot(true, isHardMode, false); // Enable EasyBot or HardBot
   	    }
   	    
   	    frame.revalidate();
        frame.repaint();
        
    }
}