import javax.swing.SwingUtilities;

import GameLauncher.GameLauncher;

public class Main {
	public static void main(String[] args) {
		 SwingUtilities.invokeLater(() -> {
	           GameLauncher.initialize();
		 });
	}
}