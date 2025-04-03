package utils;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ScaleImage {
    public static ImageIcon scaleImage(String imagePath, int width, int height) {
        try {
            Image image = ImageIO.read(ScaleImage.class.getResourceAsStream(imagePath));
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
