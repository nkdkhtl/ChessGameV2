package pieces;

import main.Board;
import main.Type;

public class Pawn extends Piece {
	public Pawn(Board board,boolean isWhite, int col, int row) {
		super(board);
		
		type = Type.PAWN;
		this.col = col;
		this.row = row;
		
		this.xPos = col*Board.SQUARE_SIZE;
		this.yPos = row*Board.SQUARE_SIZE;

		this.isWhite = isWhite;
		
		if (isWhite) {
			image = getImage("/pieces/wp");
		} else {
			image = getImage("/pieces/bp");
		}
	}
	
	public boolean isValidMovement(int col,int row) {
		int colorIdx = isWhite? 1:-1;
		
		//pawn 1
		if (this.col == col && row == this.row - colorIdx && board.getPiece(col, row) == null) {
			return true;
		}
		
		//pawn 2
		if (isFirstMove && this.col == col 
				&& row == this.row - colorIdx * 2 
				&& board.getPiece(col, row) == null
				&& board.getPiece(col, row+colorIdx) == null) {
			return true;
		}
		
		// pawn capture left
		if (col == this.col -1 && row == this.row - colorIdx && board.getPiece(col,row) != null) {
			return true;
		}
		
		if (col == this.col +1 && row == this.row - colorIdx && board.getPiece(col,row) != null) {
			return true;
		}
		
		//enPassant left
		if (board.getSquareNum(col, row) == board.enPassantSquare 
				&& col == this.col - 1
				&& row == this.row - colorIdx 
				&& board.getPiece(col, row+colorIdx) != null) {
			return true;
		}
		
		//enPassant right
		if (board.getSquareNum(col, row) == board.enPassantSquare 
				&& col == this.col + 1
				&& row == this.row - colorIdx 
				&& board.getPiece(col, row+colorIdx) != null) {
			return true;
		}
		
		return false; 
	}
	
}
