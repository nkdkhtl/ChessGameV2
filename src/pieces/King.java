package pieces;

import main.Board;
import main.Movements;
import main.Type;

public class King extends Piece {
	public King(Board board,boolean isWhite, int col, int row) {
		super(board);
		type = Type.KING;
		
		this.col = col;
		this.row = row;
		
		this.xPos = col*Board.SQUARE_SIZE;
		this.yPos = row*Board.SQUARE_SIZE;

		this.isWhite = isWhite;
		if (isWhite) {
			image = getImage("wk");
		} else {
			image = getImage("bk");
		}

	}
	@Override
	public boolean isValidMovement(int col,int row) {
        // Calculate absolute differences
        int colDiff = Math.abs(col - this.col);
        int rowDiff = Math.abs(row - this.row);
        
        // King can move one square in any direction (horizontal, vertical, or diagonal)
        // For regular moves: both differences should be at most 1
        boolean isRegularMove = colDiff <= 1 && rowDiff <= 1 && (colDiff != 0 || rowDiff != 0);
        
        return isRegularMove || canCastling(col, row);	}
	@Override
	public boolean isCollide(int col, int row) {
		return false;
	}
	
	private boolean canCastling(int col,int row) {
		if (this.row == row) {
			
			if (col == 6) {
				Piece rook = board.getPiece(7, row);
				if (rook != null && rook.isFirstMove && isFirstMove) {
					return board.getPiece(5,row) == null &&
						   board.getPiece(6, row) == null &&
						   !board.checkFinder.isKingInCheck(new Movements(board,this,5,row));
						
				}
			} else if (col == 2) {
				Piece rook = board.getPiece(0, row);
				if (rook != null && rook.isFirstMove && isFirstMove) {
					return board.getPiece(3,row) == null &&
						   board.getPiece(2, row) == null &&
						   board.getPiece(1, row) == null &&
						   !board.checkFinder.isKingInCheck(new Movements(board,this,3,row));
						
				}
			}
		}
		
		return false;
	}
	
	
	
}
