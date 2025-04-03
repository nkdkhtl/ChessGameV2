package panels;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import utils.ScaleImage;
import utils.ThemeManager;

public class PromotionPanel extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String selectedPiece = "q"; // Default choice

    public PromotionPanel(JFrame parent, boolean isWhite) {
        super(parent, "Promote Pawn", true);
        setLayout(new GridLayout(1, 4));

        String[] pieces = {"q", "r", "b", "n"};
        for (String piece : pieces) {
            JButton button = new JButton();
            button.setIcon(loadPieceIcon(piece, isWhite));
            button.addActionListener(_ -> {
                selectedPiece = piece;
                dispose(); 
            });
            add(button);
        }

        setSize(400, 100);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private ImageIcon loadPieceIcon(String pieceName, boolean isWhite) {
        String color = isWhite ? "w" : "b";
        String imagePath = String.format("/themes/%s/%s%s.png", ThemeManager.getTheme(),color, pieceName);
        return ScaleImage.scaleImage(imagePath, 50, 50);
    }

    public String getSelectedPiece() {
        return selectedPiece;
    }
}
