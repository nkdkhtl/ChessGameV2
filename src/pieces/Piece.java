package pieces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.Board;
import main.Type;

public class Piece implements Cloneable {
	public Type type;
	
	public int  col,row;
	public int xPos,yPos;
	
	public boolean isWhite;
	public int value;
	
	public boolean isFirstMove = true;
	
	public BufferedImage image;
	public BufferedImage getImage(String imagePath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	
	Board board;
	public Piece(Board board) {
		this.board = board;
	}
	
	public boolean isValidMovement(int col,int row) {
		return true;
	}
	public boolean isCollide(int col,int row) {
		return false;
	}
	
	public boolean getSquareColor() {
		//true == light
		// black == dark
		return (col+row) % 2 == 0;
	}
	
	public void draw(Graphics2D g2d) {
		g2d.drawImage(image,xPos,yPos,Board.SQUARE_SIZE,Board.SQUARE_SIZE,null);
	}
}
