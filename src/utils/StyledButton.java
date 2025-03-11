package utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class StyledButton extends JButton {

    private Color baseColor;
    private Color hoverColor;
    private Color textColor;
    private int borderRadius = 10;
    
    public StyledButton(String text, Color baseColor, Color hoverColor, Color textColor) {
        super(text);
        this.baseColor = baseColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;	
        

        setFont(new Font("Arial", Font.BOLD, 20));
        setForeground(textColor);
        setBackground(baseColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false); // Important for transparency
        setContentAreaFilled(false); // Prevents default background rendering
        setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40)); // Padding inside button
        
        addHoverEffect();
    }

    private void addHoverEffect() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(baseColor);
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded rectangle background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);

        // Draw button text
        super.paintComponent(g);
        g2.dispose();
    }
}
