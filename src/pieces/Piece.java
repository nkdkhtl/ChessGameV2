package pieces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import main.Board;
import main.Type;

public abstract class Piece {
    public Type type;
    
    public int col, row;
    public int xPos, yPos;
    
    public boolean isWhite;
    public int value;
    
    public boolean isFirstMove = true;
    
    protected BufferedImage image;
    private static String theme = "pixels"; // Default theme

    private static Map<String, BufferedImage> pieceImages = new HashMap<>();

    public Piece(Board board, boolean isWhite, int col, int row) {
        this.board = board;
        this.isWhite = isWhite;
        this.col = col;
        this.row = row;
        System.out.println("Piece constructor called for piece at (" + col + ", " + row + ")");
        loadPieceImage();
    }

    public static void setTheme(String theme) {
        Piece.theme = theme;
        System.out.println("Theme set to: " + theme);
        loadAllPieceImages();
    }

    private static void loadAllPieceImages() {
        pieceImages.clear();
        String[] pieceNames = {"p", "r", "n", "b", "q", "k"};
        String[] colors = {"w", "b"};
        
        for (String color : colors) {
            for (String pieceName : pieceNames) {
                String imagePath = String.format("res/themes/%s/%s%s.png", theme, color, pieceName);
                System.out.println("Loading image from path: " + imagePath); // Debug Statement
                try {
                    BufferedImage image = ImageIO.read(Piece.class.getResource(imagePath));
                    if (image == null) {
                        System.err.println("Image is null for path: " + imagePath);
                    } else {
                        pieceImages.put(color + pieceName, image);
                        System.out.println("Loaded image: " + imagePath);
                    }
                } catch (IOException e) {
                    System.err.println("Could not load image: " + imagePath);
                    pieceImages.put(color + pieceName, null);
                }
            }
        }
    }

    private void loadPieceImage() {
        System.out.println("loadPieceImage called for piece at (" + col + ", " + row + ")");
        if (type == null) {
            System.err.println("Piece type is null for piece at (" + col + ", " + row + ")");
            return;
        }
        
        String key = (isWhite ? "w" : "b") + type.name().toLowerCase().charAt(0);
        this.image = pieceImages.get(key);
        System.out.println("Loading image for key: " + key); // Debug Statement
        if (this.image == null) {
            System.err.println("Image not found for key: " + key);
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public boolean isWhite() {
        return isWhite;
    }
    
    Board board;
    public Piece(Board board) {
        this.board = board;
    }
    
    public Piece deepCopy(Board board) {
        Piece copy = null;
        try {
            copy = this.getClass().getConstructor(Board.class, boolean.class, int.class, int.class).newInstance(board, this.isWhite, this.col, this.row);
            copy.type = this.type;
            copy.xPos = this.xPos;
            copy.yPos = this.yPos;
            copy.value = this.value;
            copy.isFirstMove = this.isFirstMove;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return copy;
    }
    
    public abstract boolean isValidMovement(int col, int row);
    public abstract boolean isCollide(int col, int row);
    
    public boolean getSquareColor() {
        // true == light
        // black == dark
        return (col + row) % 2 == 0;
    }
    
    public void draw(Graphics2D g2d) {
        System.out.println("Drawing piece at (" + col + ", " + row + ")");
        if (image == null) {
            System.err.println("Image is null for piece at position (" + col + "," + row + ")");
            return;
        }
        g2d.drawImage(image, xPos, yPos, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}