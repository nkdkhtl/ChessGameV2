package pieces;

import main.Board;
import main.Type;

public class Bishop extends Piece {
	 	public Bishop(Board board,boolean isWhite, int col, int row) {
	 		super(board);
	 		type = Type.BISHOP;
	 
	 		this.col = col;
	 		this.row = row;
	 
	 		this.xPos = col*Board.SQUARE_SIZE;
	 		this.yPos = row*Board.SQUARE_SIZE;
	 
	 		this.isWhite = isWhite;
	 	}
	@Override
	public boolean isValidMovement(int col,int row) {
		return Math.abs(this.col - col) == Math.abs(this.row - row);
	}
	
	
	@Override
	public boolean isCollide(int col,int row) {
		//up left
		if (this.col > col && this.row > row) {
			for (int i = 1;i< Math.abs(this.col-col);i++) {
				if (board.getPiece(this.col - i, this.row - i) != null) {
					return true;
				}
			}
		}
		//up right
		if (this.col < col && this.row > row) {
			for (int i = 1;i< Math.abs(this.col-col);i++) {
				if (board.getPiece(this.col + i, this.row - i) != null) {
					return true;
				}
			}
		}
		
		//down left
		if (this.col > col && this.row < row) {
			for (int i = 1;i< Math.abs(this.col-col);i++) {
				if (board.getPiece(this.col - i, this.row + i) != null) {
					return true;
				}
			}
		}
		
		//down right
		if (this.col < col && this.row < row) {
			for (int i = 1;i< Math.abs(this.col-col);i++) {
				if (board.getPiece(this.col + i, this.row + i) != null) {
					return true;
				}
			}
		}
		return false;
	}
	
}
