package panels;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class PromotionPanel extends JDialog {
    private String selectedPiece = "Queen"; // Default choice

    public PromotionPanel(JFrame parent, boolean isWhite) {
        super(parent, "Promote Pawn", true);
        setLayout(new GridLayout(1, 4));

        String[] pieces = {"Queen", "Rook", "Bishop", "Knight"};
        for (String piece : pieces) {
            JButton button = new JButton(piece);
            button.addActionListener(e -> {
                selectedPiece = piece;
                dispose(); // Close dialog after selection
            });
            add(button);
        }

        setSize(400, 100);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public String getSelectedPiece() {
        return selectedPiece;
    }
}
