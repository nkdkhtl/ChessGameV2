package pieces;

import main.Board;
import main.Type;

public class Knight extends Piece {
	public Knight(Board board,boolean isWhite, int col, int row) {
		super(board);
		
		type = Type.KNIGHT;
		this.col = col;
		this.row = row;
		
		this.xPos = col*Board.SQUARE_SIZE;
		this.yPos = row*Board.SQUARE_SIZE;

		this.isWhite = isWhite;
		
		if (isWhite) {
			image = getImage("/pieces/wn");
		} else {
			image = getImage("/pieces/bn");
		}
	}
	@Override
	public boolean isValidMovement(int col,int row) {
		return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
	}

	@Override
	public boolean isCollide(int col, int row) {
		return false;
	}
	
}
