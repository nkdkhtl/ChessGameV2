package pieces;

import GameLauncher.Board;
import GameLauncher.Type;

public class Pawn extends Piece {
    public static final int[][] PAWN_EVALUATION = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };
	
	public Pawn(Board board,boolean isWhite, int col, int row) {
		super(board);
		
		type = Type.PAWN;
		this.col = col;
		this.row = row;
		
		this.xPos = col*Board.SQUARE_SIZE;
		this.yPos = row*Board.SQUARE_SIZE;

		this.isWhite = isWhite;
		
		if (isWhite) {
			image = getImage("wp");
		} else {
			image = getImage("bp");
		}
		

	}
	@Override
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

	@Override
	public boolean isCollide(int col, int row) {
        int colorIdx = isWhite ? 1 : -1;

        // Check collision only for forward moves
        if (this.col == col) {
            // Single square forward
            if (Math.abs(this.row - row) == 1) {
                return board.getPiece(col, row) != null;
            }
            
            // Two squares forward (first move)
            if (Math.abs(this.row - row) == 2) {
                // Check both the destination square and the square being jumped over
                int intermediateRow = this.row - colorIdx;
                return board.getPiece(col, intermediateRow) != null || board.getPiece(col, row) != null;
            }
        }
        
        return false;
    }
	
	@Override 
    public int getPositionValue() {
        return isWhite ? PAWN_EVALUATION[row][col] : -PAWN_EVALUATION[7 - row][col];
    }

}
