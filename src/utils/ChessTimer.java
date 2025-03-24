package utils;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import main.Board;
import panels.TimerPanel;

public class ChessTimer {
	private Board board;
    private Timer timer;
    private int timeLeft;
    private TimerPanel timerPanel;
    private boolean isWhite;

    public ChessTimer(Board board,TimerPanel timerPanel, boolean isWhite, int minutes) {
        this.board = board;
    	this.timerPanel = timerPanel;
        this.isWhite = isWhite;
        this.timeLeft = minutes * 60;

        timer = new Timer(1000, _ -> {
            if (timeLeft > 0) {
                timeLeft--;
                updateDisplay();
            } else {
                timer.stop();
                
            }
        });
    }

    private void updateDisplay() {
    	String timeStr = formatTime();
        SwingUtilities.invokeLater(() -> {
            if (isWhite) {
                timerPanel.updateWhiteTimer(timeStr);
            } else {
                timerPanel.updateBlackTimer(timeStr);
            }
            
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(timerPanel);
            if (topFrame != null) {
                topFrame.repaint();
            }
        });
        
        if (timeLeft <= 0) {
            timer.stop();
            SwingUtilities.invokeLater(() -> {
                board.updateGame();  // Force updateGame() to check for timeout
            });
        }
    }


    private String formatTime() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }


    public void reset(int minutes) {
        timeLeft = minutes * 60;
        updateDisplay();
    }
    
    public boolean isOutOfTime() {
        return timeLeft <= 0;
    }
}
