package pieces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.Board;
import main.Type;

public abstract class Piece {
    public Type type;
    
    public int col, row;
    public int xPos, yPos;
    
    public boolean isWhite;
    public int value;
    private boolean highlight = false;

    public boolean isFirstMove = true;
    public static String theme = "classic";

    public Piece(Board board, boolean isWhite, int col, int row) {
        this.board = board;
        this.isWhite = isWhite;
        this.col = col;
        this.row = row;
    }
    

	public static void setTheme(String selectedTheme) {
		Piece.theme = selectedTheme;
	}
	public static String getTheme() {
		return Piece.theme;
	}

    public BufferedImage image;
 	public BufferedImage getImage(String imagePath) {
 		BufferedImage image = null;
 		String pathFormat = String.format("/themes/%s/", theme);
 		try {
 			
 			image = ImageIO.read(getClass().getResourceAsStream(pathFormat + imagePath + ".png"));
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
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
    
    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }
    
    public void draw(Graphics2D g2d) {
        g2d.drawImage(image, xPos, yPos, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }


}