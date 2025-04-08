package pieces;

import GameLauncher.Board;
import GameLauncher.Type;

public class Rook extends Piece {
	
	private static final int[][] ROOK_EVALUATION = {
	        {0, 0, 0, 0, 0, 0, 0, 0},
	        {5, 10, 10, 10, 10, 10, 10, 5},
	        {-5, 0, 0, 0, 0, 0, 0, -5},
	        {-5, 0, 0, 0, 0, 0, 0, -5},
	        {-5, 0, 0, 0, 0, 0, 0, -5},
	        {-5, 0, 0, 0, 0, 0, 0, -5},
	        {-5, 0, 0, 0, 0, 0, 0, -5},
	        {0, 0, 0, 5, 5, 0, 0, 0}
	};
	
	public Rook(Board board,boolean isWhite, int col, int row) {
		super(board);
		type = Type.ROOK;
		
		this.col = col;
		this.row = row;
		
		this.xPos = col*Board.SQUARE_SIZE;
		this.yPos = row*Board.SQUARE_SIZE;

		this.isWhite = isWhite;
		if (isWhite) {
			image = getImage("wr");
		} else {
			image = getImage("br");
		}
	}
	@Override
	public boolean isValidMovement(int col,int row) {
		return this.col == col || this.row == row;
	}
	
	@Override
	public boolean isCollide(int col,int row) {
		//left 
		if (this.col > col) {
			for (int c = this.col-1;c>col;c--) {
				if (board.getPiece(c, this.row) != null) {
					return true;
				}
			}
		}
		
		//right
		if (this.col < col) {
			for (int c = this.col+1;c<col;c++) {
				if (board.getPiece(c, this.row) != null) {
					return true;
				}
			}
		}
		
		//up
		if (this.row > row) {
			for (int r = this.row-1;r>row;r--) {
				if (board.getPiece(this.col, r) != null) {
					return true;
				}
			}
		}
		
		//down
		if (this.row < row) {
			for (int r = this.row+1;r<row;r++) {
				if (board.getPiece(this.col, r) != null) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
    @Override
    public int getPositionValue() {
        return isWhite ? ROOK_EVALUATION[row][col] : -ROOK_EVALUATION[7 - row][col];
    }
	
}
